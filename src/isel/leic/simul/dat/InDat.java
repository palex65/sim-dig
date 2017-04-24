package isel.leic.simul.dat;

import isel.leic.simul.bit.InBit;

public class InDat extends AbstractDat implements IDat {

    public InDat(String name, int n, InBit.BitChangedListener action) {
        super(name);
        bits = new InBit[n];
        for (int i = 0; i < n; i++)
            bits[i] = new IBit(name+i,1<<i,action);
    }

    public class IBit extends InBit {
        final int mask;
        public IBit(String name, int m, InBit.BitChangedListener action) {
            super(name,action);
            mask=m;
        }
    }

    public InBit bitPos(int idx) { return (InBit)bits[idx]; }
    public InBit bitMask(int mask) { return bitPos(Dat.toPos(mask)); }
}
