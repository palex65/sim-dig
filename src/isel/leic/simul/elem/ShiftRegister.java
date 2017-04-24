package isel.leic.simul.elem;

import isel.leic.simul.bit.InBit;
import isel.leic.simul.dat.InDat;
import isel.leic.simul.dat.OutDat;
import isel.leic.simul.elem.Elem;

public class ShiftRegister extends Elem {
	public final InBit clk = new Input("clk");
    public final InBit in = new Input("in",null);
    public final InBit en = new Input("en",null);
    public final OutDat out;

    public ShiftRegister(int n) {
		out = new OutputDat("out",n);
        initOutputs();
	}

    protected void onInputChanged(InBit bit) {
       if (clk.isTrue() && en.isTrue()) {
           int val = out.read() >>> 1;
           if (in.isTrue())
               val |= 1<<(out.length()-1);
           out.write(val);
       }
    }

    @Override
    public String toString() {
        return "in("+in+") en="+en+" clk="+clk+" out("+out+")";
    }
}
