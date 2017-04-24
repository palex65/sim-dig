package isel.leic.simul.elem;

import isel.leic.simul.bit.*;
import isel.leic.simul.dat.*;

public class DeMux extends Elem {
	public InDat sel;
    public InBit in = new Input("in");
    public OutDat out;

    public DeMux(int n) {
		out = new OutputDat("out",n);
        sel = new InputDat("sel",Dat.selOf(n));
    }
    public DeMux(int n, OBit input) {
        this(n);
        in.connect(input);
    }

    protected void onInputChanged(InBit bit) {
        out.write(in.isFalse() ? 0 : (1<<sel.read()) );
    }
}
