package isel.leic.simul.dat;

import isel.leic.simul.bit.*;

public interface IDat extends Dat {
    IBit bitPos(int idx);
    default void connect(ODat od, int fromPos, int toPos) {
        int idx=0;
        for(int i=fromPos ; i<=toPos ; ++i, ++idx)
            bitPos(idx).connect(od.bitPos(i));
    }
    default void connect(ODat od) { connect(od, 0, Math.min(length(),od.length()) - 1); }
    default void connect(ODat od, int mask) { connect(od, Dat.toLowPos(mask), Dat.toHiPos(mask)); }
}
