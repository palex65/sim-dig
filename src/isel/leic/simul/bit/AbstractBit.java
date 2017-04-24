package isel.leic.simul.bit;

import isel.leic.simul.Simul;

public abstract class AbstractBit implements Bit {
    private static final int FIRST_WRITE = 0x001;
    private static final int PULL_UP     = 0x002;
    private static final int PULL_DOWN   = 0x004;
    private static final int INVERT      = 0x008;
    private int modifiers = FIRST_WRITE;

    private int delay = 0;
    private volatile Value value;
    private String name;

    protected AbstractBit(String name, Value init) { this.name=name; value = init; }
    protected AbstractBit(String name) { this(name,Value.OPEN); }
    protected AbstractBit(Value init) { this("",init); }
    protected AbstractBit() { this("",Value.OPEN); }

    public String getName() { return name; }
    public void setName(String name) { this.name=name; }
    @Override
    public Value get() { return value; }

    @Override
    public void write(Value v) {
        if (v==Value.OPEN && (modifiers&(PULL_DOWN|PULL_UP))!=0)
            v = (modifiers&PULL_UP)!=0 ? Value.HIGH : Value.LOW;
        if ((modifiers&INVERT) != 0) v = v.not();
        if ((modifiers & FIRST_WRITE)!=0 || value!=v) {
            value = v;
            modifiers &= ~FIRST_WRITE;
            if (delay<=0) propagate();
            else simul.doActionDelayed(this::propagate,delay);
        }
    }

    private void propagate() {
        bitChanged();
        notifyListeners();
    }

    private BitListener listeners = null;

    @Override
    public void addListener(BitListener bl) {
        bl.setNext(listeners);
        listeners = bl;
        if (!isOpen()) {
            bl.update(get());
            simul.doAll();
        }
    }

    @Override
    public void pull(boolean up) {
        modifiers |= up ? PULL_UP : PULL_DOWN;
        write(value);
    }

    protected void notifyListeners() {
        for( BitListener bl = listeners ; bl!= null ; bl=bl.getNext() )
            bl.update(value);
    }

    protected abstract void bitChanged();

    @Override
    public String toString() {
        return name+"="+value.view;
    }

    public void setInvert(boolean invert) {
        if (invert) modifiers |= INVERT;
        else modifiers &= ~INVERT;
    }
    public boolean isInverted() { return (modifiers & INVERT) != 0; }

    public void setDelayed(int times) {
        delay = times;
    }

    private static Simul.Bit simul;
    public static void setSimul(Simul.Bit s) { simul=s; }
    protected void writeLater(IBit bit, Value val) { simul.doWrite(bit,val); }
}
