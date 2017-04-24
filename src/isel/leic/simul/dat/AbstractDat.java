package isel.leic.simul.dat;

import isel.leic.simul.bit.Bit;
import isel.leic.simul.bit.Value;

public abstract class AbstractDat implements Dat {
    protected Bit[] bits;
    private final String name;

    protected AbstractDat(String name) {
        this.name = name;
    }

    public int read() {
        boolean allOpen = true;
        int value = 0;
        int m=1;
        for (Bit bit : bits) {
            if (!bit.isOpen()) allOpen=false;
            if (!bit.isFalse()) value |= m;
            m<<=1;
        }
        return allOpen ? OPEN : value;
    }

    public void write(int val) {
        int value = val;
        if (val==OPEN || val==-1) {
            openBits();
            return;
        }
        value &= ~(~1<<bits.length);
        int m = 1;
        for (int i = 0; i < bits.length; ++i, m <<= 1)
            bits[i].write(Value.valueOf((value & m) != 0));
    }

    protected void openBits() {
        for(Bit bit : bits)
            bit.open();
    }

    public void setInvert(boolean invert) {
        for(Bit bit : bits)
            bit.setInvert(invert);
    }

    public int length() { return bits.length; }

    public Bit bitPos(int idx) { return bits[idx]; }
    public Bit bitMask(int mask) { return bitPos(Dat.toPos(mask)); }

    @Override
    public String toString() {
        int val = read();
        String res = "value="+(val==OPEN?"OPEN":(""+val))+" [";
        for(int i=bits.length-1 ; i>=0 ; --i)
            res += " "+bits[i];
        res += " ]";
        return res;
    }

    public String getName() {
        return name;
    }
}
