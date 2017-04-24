package isel.leic.simul.elem;

import isel.leic.simul.bit.InBit;
import isel.leic.simul.bit.OutBit;
import isel.leic.simul.dat.InDat;
import isel.leic.simul.dat.OutDat;

public class Bus implements InBit.BitChangedListener {
    public final InDat in;
    public final OutDat out;

    public Bus(int n) {
        out = new OutDat("out",n);
        in = new InDat("in", n, this );
   }

    @Override
    public void bitChanged(InBit bit) {
        out.write( in.read() );
    }
}
