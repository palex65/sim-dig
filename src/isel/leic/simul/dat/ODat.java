package isel.leic.simul.dat;

import isel.leic.simul.bit.*;

public interface ODat extends Dat {
    OBit bitPos(int idx);
    default void connect(IDat id, int fromPos, int toPos) {
        int idx=0;
        for(int i=fromPos ; i<=toPos ; ++i, ++idx)
            bitPos(idx).connect(id.bitPos(i));
    }
    default void connect(IDat id) { connect(id, 0, Math.min(id.length(),length()) - 1); }
    default void connect(IDat id, int mask) { connect(id, Dat.toLowPos(mask), Dat.toHiPos(mask)); }
}
