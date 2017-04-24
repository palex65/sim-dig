package isel.leic.simul.parser;

import isel.leic.simul.bit.OBit;
import isel.leic.simul.elem.Elem;
import isel.leic.simul.module.Module;
import isel.leic.simul.state.State;

import java.io.IOException;

public class ModuleParser extends Parser {
    private DinamicModule module;
    private DinamicStateMachine machine;

    public ModuleParser(String initFileName) throws IOException {
        super(initFileName);
    }

    static void parseFile(DinamicModule mod, String fName) {
        try {
            ModuleParser parser = new ModuleParser(fName);
            parser.module = mod;
            parser.parse();
        } catch (IOException e) {
            throw new SyntaxError("Unknown simul file \""+fName+'"',true);
        } catch (Exception e) {
            throw new SyntaxError("Inválid simul file \""+fName+'"',true);
        }
    }

    private void parseState(String line) {
        int sep = line.indexOf(':');
        if (sep<0) throw new SyntaxError("Missing ':' in state line : "+line);
        State state = machine.state(line.substring(0,sep));
        String[] args = line.substring(sep+1).split(";");
        for(String arg : args) {
            String a = arg.trim();
            int idx = a.indexOf("->");
            if (idx>=0) { // transition
                String to = a.substring(idx+2).trim();
                if (idx==0) machine.transition(state,to);
                else {
                    Exp cond = Exp.parse(a.substring(0,idx).trim(),machine);
                    machine.transition(state, to, cond);
                }
            } else {   // output var
                idx = a.indexOf('?');
                String var = a;
                Exp cond = null;
                if (idx>0) {
                    var = a.substring(idx+1).trim();
                    cond = Exp.parse(a.substring(0,idx).trim(),machine);
                }
                OBit out = (OBit) machine.getField(var);
                if (out==null) throw new SyntaxError("Output of state machine not found : "+a);
                machine.output(state,cond,out);
            }
        }
    }

    private void parseInputOutput(String line) {
        int sep = line.indexOf(">>");
        String[] inputs = line.substring(0,sep).split(";");
        for(String input : inputs) {
            PinInfo pin = new PinInfo(module,input);
            if (pin.pinName.length==1) {
                if (machine!=null) machine.addInput(pin.pinExp);
                else module.addInput(pin.pinExp);
            }
            else module.addInBlock(pin.pinExp,pin.pinName.length);
        }
        String[] outputs = line.substring(sep+2).split(";");
        for(String output : outputs) {
            PinInfo pin = new PinInfo(module,output);
            if (pin.pinName.length==1) {
                if (machine!=null) machine.addOutput(pin.pinExp);
                else module.addOutput(pin.pinExp);
            }
            else module.addOutBlock(pin.pinExp,pin.pinName.length);
        }
    }

    private void parseElem(String line) {
        parseModule(line);
    }

    @Override
    protected void parseLine(String line) throws SyntaxError {
        if (line.contains(">>"))
            parseInputOutput(line);
        else if (line.contains("->")) {
            if (machine!=null) parseState(line);
            else parseLink(line);
        }
        else if (line.contains("{"))
            parseMachine(line);
        else if (line.contains("="))
            parseElem(line);
        else if (line.trim().equals("}")) {
            if (machine == null)
                throw new SyntaxError("'}' without StateMachine");
            machine.finish();
            machine = null;
        }
        else
            throw new SyntaxError();
    }

    private void parseMachine(String line) {
        int eq = line.indexOf('=');
        int cb = line.indexOf('{');
        if (eq<0) throw new SyntaxError("Missing = before {");
        if (!"StateMachine".equals(line.substring(eq+1,cb).trim()))
            throw new SyntaxError("Expected 'StateMachine'");
        String name = line.substring(0,eq).trim();
        machine = new DinamicStateMachine();
        module.elems.put(name,machine);
    }

    @Override
    protected Module getModule(String name, Object obj) {
        Module mod = null;
        Elem elem = (Elem) obj;
        module.elems.put(name,elem);
        return mod;
    }

    @Override
    protected String getModule() {
        return module.getName();
    }

    @Override
    protected Elem findElem(String name) {
        return module.elems.get(name);
    }
}
