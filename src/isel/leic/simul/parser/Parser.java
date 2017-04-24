package isel.leic.simul.parser;

import isel.leic.simul.Simul;
import isel.leic.simul.bit.*;
import isel.leic.simul.dat.InDat;
import isel.leic.simul.dat.OutDat;
import isel.leic.simul.elem.Elem;
import isel.leic.simul.elem.Link;
import isel.leic.simul.module.Module;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Stream;

public abstract class Parser implements AutoCloseable {

    private final Stream<String> lines;
    protected static LinkedList<String> packages = new LinkedList<>( Arrays.asList(
            "isel.leic.simul.module",
            "isel.leic.simul.elem",
            "isel.leic.simul",
            "isel.leic.simul.gate",
            "isel.leic.simul.ff"
    ));
    protected static Simul.App simul;

    protected Parser(String initFileName, Simul.App app) throws IOException {
        this(initFileName);
        simul = app;
    }
    protected Parser(String initFileName) throws IOException {
        lines = Files.lines(Paths.get(initFileName));
    }

    public void parse() {
        lines.filter((s)->!isComment(s)).forEach((line) -> {
            try { parseLine(trimLine(line)); }
            catch (SyntaxError e) {
                System.out.println("ERROR : "+e.getMessage()+"\n in line : "+line);
                if (e.isFatal())
                    throw new RuntimeException(e);
            }
            catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    protected abstract void parseLine(String line) throws SyntaxError;

    protected void parseModule(String line) {
        int eq = line.indexOf('=');
        String name = line.substring(0,eq).trim();
        String[] exp = line.substring(eq+1).split(";");
        ArgsInfo args = new ArgsInfo(exp[0],name);
        Class<?> cls = findModule(args.name);
        Object obj = newInstance(cls,args,name);
        Module mod = getModule(name, obj);
        parseArgs(name, exp, cls, obj, mod);
    }

    protected void parseArgs(String name, String[] exp, Class<?> cls, Object obj, Module mod) {
        ArgsInfo args;
        String txt;
        for (int i = 1; i < exp.length; i++) {
            txt = exp[i].trim();
            char first = txt.charAt(0);
            if (first=='"' && mod!=null)
                mod.setTitle(txt.substring(1,txt.length()-1));
            else if (first=='0' || first=='1' || first=='~')
                modifier(first,txt,name);
            else {
                args = new ArgsInfo(txt);
                try {
                    cls.getMethod(args.name, args.argsType).invoke(obj, args.argsValue);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new SyntaxError("in calling method "+args.name);
                }
            }
        }
    }
    protected abstract Module getModule(String name, Object obj);

    private void modifier(char first, String txt, String comp) {
        int pinIdx = 1;
        if (first=='0' || first=='1') {
            pinIdx = txt.indexOf('?');
            if (pinIdx==-1) throw new SyntaxError("missing '?' in pull up/down");
            pinIdx++;
        }
        PinInfo pinInfo = new PinInfo(comp,txt.substring(pinIdx));
        Bit[] bits = pinInfo.getBits();
        for(Bit bit: bits)
            switch (first) {
            case '~': bit.setInvert(true); break;
            case '0': bit.pull(false); break;
            case '1': bit.pull(true); break;
            }
    }

    protected Module createModule(String name, Object obj) {
        if (obj instanceof Elem)
            return new Module(name,(Elem)obj);
        if (obj instanceof Module) {
            Module m = (Module) obj;
            if (obj.getClass()==DinamicModule.class && simul.findModule(name)==null)
                m.addToSim();
            return m;
        }
        throw new SyntaxError(obj.getClass().getSimpleName()+" is not a Module or Elem");
    }

    protected Object newInstance(Class<?> cls, ArgsInfo args, String name) {
        try {
            if (isSubclassOf(cls,Elem.class)) {
                args.removeFirstArg();
                return getConstructor(cls,args.argsType).newInstance(args.argsValue);
                //return cls.getConstructor(args.argsType).newInstance(args.argsValue);
            }
            try {
                Object obj = getConstructor(cls,args.argsType).newInstance(args.argsValue);
                if (cls==DinamicModule.class && name!="Module") {
                    DinamicModule mod = (DinamicModule) getModule(name, obj);
                    ModuleParser.parseFile(mod, args.name+".simul");
                }
                return obj;
            } catch (NoSuchMethodException e) {
                return getConstructor(cls,String.class).newInstance(name);
            }
        } catch (NoSuchMethodException e) {
            //System.out.println("ERROR in newInstance: cls="+cls+" args="+args+" name="+name);
            throw new SyntaxError("Constructor not found for: "+args);
        } catch (InstantiationException e) {
            //System.out.println("ERROR in newInstance: cls="+cls+" args="+args+" name="+name);
            throw new SyntaxError("InstantiationException. ("+ e.getMessage()+")");
        } catch (IllegalAccessException e) {
            //System.out.println("ERROR in newInstance: cls="+cls+" args="+args+" name="+name);
            throw new SyntaxError("IllegalAccessException. ("+ e.getMessage()+")");
        } catch (InvocationTargetException e) {
            //System.out.println("ERROR in newInstance: cls="+cls+" args="+args+" name="+name);
            throw new SyntaxError("InvocationTargetException. ("+ e.getCause().getMessage()+")");
        }
    }

    private Constructor getConstructor(Class<?> cls, Class... types) throws NoSuchMethodException {
        Constructor[] ctrs = cls.getDeclaredConstructors();
        for (Constructor ctr : ctrs)
            if (ctr.getParameterCount()==types.length) {
                Class[] pTypes = ctr.getParameterTypes();
                boolean ok = true;
                for (int i = 0; ok && i < types.length; i++)
                    if (!pTypes[i].isAssignableFrom(types[i])) ok=false;
                if (ok) return ctr;
            }
        throw new NoSuchMethodException(cls.getName());
    }

    private static boolean isSubclassOf(Class<?> cls, Class<?> base) {
        if (base==Object.class || cls==base) return true;
        while(cls!=Object.class) {
            cls = cls.getSuperclass();
            if (cls==base) return true;
        }
        return false;
    }

    protected Class findModule(String exp) {
        String module = exp.trim();
        int arg = module.indexOf('(');
        if (arg>0) module = module.substring(0,arg).trim();
        if (module.equals("Module"))
            return DinamicModule.class;
        for(String pck : packages) {
            try {
                return Class.forName(pck+"."+module);
            } catch (ClassNotFoundException ignored) {}
        }
        if (Files.exists(Paths.get(module+".simul")))
            return DinamicModule.class;
        throw new SyntaxError("Unknown module "+module);
    }

    protected class PinInfo {
        String pinExp;
        String compName;
        String[] pinName;
        Tie[] pin;
        Object[] field;
        Value value;

        PinInfo(DinamicModule mod, String exp) {
            pinExp = exp.trim();
            parseSyntaxPin(pinExp);
        }
        PinInfo(String comp, String exp) {
            pinExp = exp.trim();
            compName = comp;
            parsePin(pinExp);
        }
        PinInfo(String exp) {
            pinExp = exp.trim();
            int idx = pinExp.indexOf('.');
            if (idx < 0) {
                for (Value val : Value.values())
                    if (pinExp.equals(val.toString()) || val.view == pinExp.charAt(0)) {
                        value = val;
                        return;
                    }
                String moduleName = getModule();
                if (moduleName==null)
                    throw new SyntaxError(pinExp+" unknown or missing dot in " + exp);
                pinExp = moduleName+"."+pinExp;
                idx = moduleName.length();
            }
            compName = pinExp.substring(0, idx).trim();
            if (idx + 1 >= pinExp.length()) throw new SyntaxError("Missing pin name in " + exp);
            parsePin(pinExp.substring(idx + 1, pinExp.length()).trim());
        }
        private void parsePin(String pName) {
            parseSyntaxPin(pName);
            String moduleName = getModule();
            if (moduleName!=null && !moduleName.equals(compName)) {
                Elem elem = findElem(compName);
                if (elem == null) throw new SyntaxError("Unknown element " + compName + " in " + pinExp);
                field = new Object[pinName.length];
                for (int i = 0; i < field.length; i++) {
                    field[i] = elem.getField(pinName[i]);
                    if (field[i] == null) throw new SyntaxError("Unknown field " + pinName[i] + " in " + pinExp);
                }
            } else {
                Module mod = simul.findModule(compName);
                if (mod == null) throw new SyntaxError("Unknown module " + compName + " in " + pinExp);
                pin = new Tie[pinName.length];
                for (int i = 0; i < pin.length; i++) {
                    pin[i] = mod.getTie(pinName[i]);
                    if (pin[i] == null) throw new SyntaxError("Unknown pin " + pinName[i] + " in " + pinExp);
                }
            }
        }

        private void parseSyntaxPin(String exp) {
            int idx = exp.indexOf('[');
            if (idx<0) pinName = new String[]{exp};
            else {
                pinExp = exp.substring(0,idx);
                int cIdx = exp.indexOf(']',idx);
                int sIdx = exp.indexOf('-',idx);
                if (sIdx==-1) sIdx = exp.indexOf(':',idx);
                if (cIdx<0 || sIdx<0) throw new SyntaxError("Invalid range in "+pinExp);
                String first = exp.substring(idx + 1, sIdx).trim();
                String second = exp.substring(sIdx + 1, cIdx).trim();
                exp = exp.substring(0, idx);
                try {
                    if (first.length() > 1 || second.length() > 1) {
                        int from = Integer.parseInt(first);
                        int to = Integer.parseInt(second);
                        pinName = new String[to - from + 1];
                        for (int i = from; i <= to; i++)
                            pinName[i - from] = exp + i;
                    } else {
                        char from = first.charAt(0);
                        char to = second.charAt(0);
                        pinName = new String[to - from + 1];
                        for (int i = 0; i < pinName.length; i++)
                            pinName[i] = exp + ((char) (from + i));
                    }
                } catch( NumberFormatException | IndexOutOfBoundsException e) {
                    throw new SyntaxError("Invalid range in "+pinExp);
                }
            }
        }

        private InBit last = null;
        private Link lnk = null;

        private void setBitOf(InTieBit tie, IBit bit) {
            IBit actual = tie.getBit();
            if (actual instanceof InBit) {
                InBit old = (InBit) actual;
                if (old==last) {
                    lnk.out.connect(bit);
                    return;
                }
                if (old.getAction() != null){
                    lnk = new Link(old,bit);
                    tie.setBit(lnk.in);
                    last = lnk.in;
                    return;
                }
            }
            tie.setBit(bit);
        }

        public void link(PinInfo to) {
            if (value!=null) {
                for(int i=0 ; i< to.pin.length ; i++)
                    ((InTieBit)to.pin[i]).write(value);
                return;
            }
            int flen = pin!=null ? pin.length : field.length;
            int tlen = to.pin!=null ? to.pin.length : to.field.length;
            if (flen!=tlen) throw new SyntaxError("Pin range incompatible in "+pinExp+" and "+to.pinExp);
            if (pin!=null && to.pin==null) {            // InTie to Input
                for (int i = 0; i < pin.length; i++)
                    setBitOf((InTieBit) pin[i],(IBit)to.field[i]);
            } else if(pin==null && to.pin!=null) {      // output to OutTie
                for (int i = 0; i < field.length; i++)
                    ((OutTieBit) to.pin[i]).setBit((OBit) field[i]);
            } else if (pin==null) {  // output to input
                if (field.length==1 && field[0] instanceof OutDat)
                    ((OutDat)field[0]).connect((InDat)to.field[0]);
                else
                    for (int i = 0; i < field.length; i++)
                        ((OBit) field[i]).connect((IBit) to.field[i]);
            } else {                                    // Tie to Tie
                if (pin[0] instanceof OutTieBit) {
                    for (int i = 0; i < pin.length; i++)
                        ((OutTieBit) pin[i]).link((InTieBit) to.pin[i]);
                } else if (pin[0] instanceof InTieBit) {
                    for (int i = 0; i < pin.length; i++)
                        ((InTieBit) pin[i]).link((OutTieBit) to.pin[i]);
                } else
                    throw new SyntaxError("pin " + pinExp + " is a " + pin.getClass().getSimpleName());
            }
        }

        public Bit[] getBits() {
            Bit[] bits = new Bit[pinName.length];
            for (int i = 0; i <bits.length ; i++) {
                if (pin!=null) {
                    Tie tie = pin[i];
                    if (tie instanceof InTieBit) bits[i] = ((InTieBit) tie).getBit();
                    else bits[i] = ((OutTieBit) tie).getBit();
                } else
                    bits[i] = (Bit) field[i];
            }
            return bits;
        }
    }

    protected Elem findElem(String name) { return null; }
    protected String getModule() { return null; }

    protected void parseLink(String line) {
        //System.out.println("Link : "+line);
        int idx = line.indexOf("->");
        PinInfo from = new PinInfo(line.substring(0,idx));
        String[] toArgs = line.substring(idx+2).split(",");
        for(String toArg : toArgs) {
            from.link(new PinInfo(toArg));
        }
    }

    protected String trimLine(String line) {
        int idx = line.indexOf('#');
        if (idx>0 && (line.indexOf('"',idx)<0 || line.lastIndexOf('"',idx)<0))
                line = line.substring(0,idx);
        return line.trim();
    }

    private boolean isComment(String line) {
        return line.length()==0 || line.startsWith("#");
    }

    @Override
    public void close() throws Exception {
        //lines.close();
    }

    class ArgsInfo {
        String name;
        String args;
        String[] argsTxt;
        Class[] argsType;
        Object[] argsValue;

        ArgsInfo(String exp) {
            this(exp, null);
        }

        ArgsInfo(String exp, String firstArg) {
            name = exp.trim();
            int idx = name.indexOf('(');
            if (idx < 0) args = firstArg == null ? "" : '"' + firstArg + '"';
            else {
                int fIdx = name.indexOf(')', idx);
                if (fIdx < 0) throw new SyntaxError("Missing ')'");
                args = name.substring(idx + 1, fIdx).trim();
                if (firstArg != null)
                    args = '"' + firstArg + "\"," + args;
                name = name.substring(0, idx).trim();
            }
            if (args.length() == 0) {
                argsType = new Class[0];
                argsValue = new Object[0];
                return;
            }
            argsTxt = args.split(",");
            argsTypes();
            argsValues();
        }

        private void argsValues() {
            argsValue = new Object[argsTxt.length];
            for (int i = 0; i < argsTxt.length; i++) {
                String arg = argsTxt[i];
                Class type = argsType[i];
                Object obj;
                if (type == String.class)
                    obj = arg.substring(1, arg.length() - 1);
                else if (type == char.class)
                    obj = arg.charAt(1);
                else if (type == boolean.class)
                    obj = Boolean.parseBoolean(arg);
                else if (type == long.class)
                    obj = Long.parseLong(arg.substring(0, arg.length() - 1));
                else if (type == int.class)
                    obj = Integer.parseInt(arg);
                else if (type == float.class)
                    obj = Float.parseFloat(arg);
                else {
                    int dot = arg.indexOf('.');
                    Elem elem = findElem(arg.substring(0,dot).trim());
                    obj = elem.getField(arg.substring(dot+1).trim());
                }
                argsValue[i] = obj;
            }
        }

        private void argsTypes() {
            argsType = new Class[argsTxt.length];
            for (int i = 0; i < argsTxt.length; i++) {
                String val = argsTxt[i] = argsTxt[i].trim();
                int dot = val.indexOf('.');
                char c = val.charAt(0);
                Class type = null;
                if (c == '"') type = String.class;
                else if (c == '\'') type = char.class;
                else if (val.equals("true") || val.equals("false")) type = boolean.class;
                else if (Character.isDigit(c) || c == '-') {
                    if (val.charAt(val.length() - 1) == 'L') type = long.class;
                    else if (dot > 0) type = float.class;
                    else type = int.class;
                } else if (getModule() != null && dot>0) {
                    String name = val.substring(0,dot).trim();
                    Elem elem = findElem(name);
                    if (elem==null) throw new SyntaxError("Element not found: "+name);
                    name = val.substring(dot+1).trim();
                    Object bit = elem.getField(name);
                    if (bit==null) throw new SyntaxError("field not found: "+name);
                    type = bit.getClass();
                }
                argsType[i] = type;
            }
        }

        @Override
        public String toString() {
            StringBuilder res = new StringBuilder("(");
            for (int i = 0; i < argsTxt.length - 1; i++) {
                res.append(argsTxt[i]);
                res.append(',');
            }
            if (args.length() > 0) res.append(argsTxt[argsTxt.length - 1]);
            res.append(')');
            return res.toString();
        }

        public void removeFirstArg() {
            Class[] types = new Class[argsType.length - 1];
            System.arraycopy(argsType, 1, types, 0, types.length);
            argsType = types;
            Object[] values = new Object[argsValue.length - 1];
            System.arraycopy(argsValue, 1, values, 0, values.length);
            argsValue = values;
        }
    }
}

