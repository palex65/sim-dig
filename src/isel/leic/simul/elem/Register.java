package isel.leic.simul.elem;

import isel.leic.simul.bit.InBit;
import isel.leic.simul.dat.InDat;
import isel.leic.simul.dat.OutDat;

public class Register extends Elem {
	public final InBit clk = new Input("clk");
    public final InDat in;
    public final OutDat out;

    public Register(int n) {
		out = new OutputDat("out",n);
        in = new InputDat("in",n,null);
        initOutputs();
	}

    protected void onInputChanged(InBit bit) {
       if (clk.isTrue())
           out.write( in.read() );
    }

    @Override
    public String toString() {
        return "in("+in+") clk="+clk+" out("+out+")";
    }
}
