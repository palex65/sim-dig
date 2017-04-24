package isel.leic.simul.dat;

import isel.leic.simul.bit.OutBit;

public class OutDat extends AbstractDat implements ODat {
    public OutDat(String name, int n) {
        super(name);
        bits = new OBit[n];
        for (int i = 0; i < n; i++)
            bits[i] = new OBit(name+i);
    }

    public class OBit extends OutBit {
        public OBit(String s) {
            super(s);
        }
    }

    public void link(InTieDat itd) {
        connect(itd.getDat());
    }
    public OutBit bitPos(int idx) { return (OutBit)bits[idx]; }
    public OutBit bitMask(int mask) { return (OutBit)super.bitMask(mask); }
}
