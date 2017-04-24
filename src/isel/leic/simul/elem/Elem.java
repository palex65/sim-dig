package isel.leic.simul.elem;

import isel.leic.simul.Simul;
import isel.leic.simul.bit.*;
import isel.leic.simul.dat.AbstractDat;
import isel.leic.simul.dat.InDat;
import isel.leic.simul.dat.OutDat;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public abstract class Elem {
    public List<IBit> inputs = new LinkedList<>();
    public List<OBit> outputs = new LinkedList<>();
    public List<InDat> datInputs = new LinkedList<>();
    public List<OutDat> datOutputs = new LinkedList<>();

    public Object getField(String name) {
        try {
            Field f = this.getClass().getDeclaredField(name);
            return f.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            for (IBit bit : inputs)
                if (((AbstractBit) bit).getName().equals(name)) return bit;
            for (OBit bit : outputs)
                if (((AbstractBit) bit).getName().equals(name)) return bit;
            return null;
        }
    }

    public String getStatus() {
        List<String> in = new LinkedList<>();
        List<String> out = new LinkedList<>();
        for (IBit bit : inputs)
            if (!(bit instanceof InDat.IBit)) {
                AbstractBit b = (AbstractBit) bit;
                in.add(b.getName() + " = " + bit.get().view);
            }
        for (InDat dat : datInputs)
            in.add(dat.getName() + "["+dat.length()+"]=" + dat.read());
        for (OBit bit : outputs)
            if (!(bit instanceof OutDat.OBit)) {
                AbstractBit b = (AbstractBit) bit;
                out.add(b.getName() + " = " + bit.get().view);
            }
        for (OutDat dat : datOutputs)
           out.add(dat.getName() + "["+dat.length()+"]=" + dat.read());
        int n = Math.max(in.size(),out.size());
        String txt = "";
        for (int i = 0; i < n; i++) {
            txt += "<tr><td>";
            if (i<in.size()) txt += in.get(i);
            txt += "</td><td>";
            if (i<out.size()) txt += out.get(i);
            txt += "</td></tr>";
        }
        return txt;
    }

    protected class Input extends InBit {
        public Input() { this(""); }
        public Input(String name) {
            this(name,Elem.this::onInputChanged);
        }
        public Input(String name, InBit.BitChangedListener action) {
            super(name,action);
            inputs.add(this);
        }
    }
    protected class InputDat extends InDat {
        public InputDat(String name, int length) {
            this(name,length,Elem.this::onInputChanged);
        }
        public InputDat(String name, int length, InBit.BitChangedListener action) {
            super(name, length, action);
            datInputs.add(this);
            for(Bit bit : bits) inputs.add((IBit)bit);
        }
    }
    protected class Output extends OutBit {
        public Output() { this(""); }
        public Output(String name) {
            this(name,false);
        }
        public Output(String name, boolean value) {
            super(name,value);
            outputs.add(this);
        }
    }
    protected class OutputDat extends OutDat {
        public OutputDat(String name, int length) {
            super(name, length);
            datOutputs.add(this);
            for(Bit bit : bits) outputs.add((OBit)bit);
        }
    }
    protected final void initOutputs() {
        for (OBit out : outputs)
            if (out.isOpen()) out.reset();
    }

    protected abstract void onInputChanged(InBit bit);

    private static Simul.UI simul;
    public static void setSimul(Simul.UI s) { simul=s; }
    protected final void doAction(Runnable action) { simul.doAction(action); }
}
