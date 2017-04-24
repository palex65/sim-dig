package isel.leic.simul.dat;

import isel.leic.simul.bit.InOutBit;

public class InOutDat extends AbstractDat implements IDat, ODat {
    protected Runnable action;

    public InOutDat(String name, int n, Runnable action) {
        super(name);
        bits = new InOutBit[n];
        for (int i = 0; i < n; i++)
            bits[i] = new IOBit(1<<i);
        this.action = action;
    }

    private class IOBit extends InOutBit {
        final int mask;
        public IOBit(int m) { mask=m; }
        public void bitChanged() {
            if (action !=null)
                action.run();
        }
    }

    public InOutBit bitPos(int idx) { return (InOutBit)bits[idx]; }
    public InOutBit bitMask(int mask) { return (InOutBit)super.bitMask(mask); }
}
