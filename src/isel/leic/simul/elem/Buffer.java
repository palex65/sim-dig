package isel.leic.simul.elem;

import isel.leic.simul.bit.InBit;
import isel.leic.simul.dat.InDat;
import isel.leic.simul.dat.OutDat;

public class Buffer extends Elem {
	public final InBit oe = new Input("oe");
    public final InDat in;
    public final OutDat out;

    public Buffer(int n) {
		out = new OutDat("out",n);
        in = new InDat("in",n, this::onInputChanged);
	}

    protected void onInputChanged(InBit bit) {
        out.write( oe.isTrue() ? in.read() : -1 );
    }
}
