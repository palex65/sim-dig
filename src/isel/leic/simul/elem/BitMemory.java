package isel.leic.simul.elem;

import isel.leic.simul.Simul;
import isel.leic.simul.bit.InBit;
import isel.leic.simul.dat.Dat;
import isel.leic.simul.dat.InDat;
import isel.leic.simul.dat.OutDat;
import isel.leic.simul.module.Module;

public class BitMemory extends Elem {
	public final InBit clk = new Input("clk");
    public final InBit in = new Input("in",null);
    public final InDat addr;
    public final OutDat out;

    public BitMemory(int n) {
		out = new OutputDat("out",n);
        addr = new InputDat("addr", Dat.selOf(n),null);
	}

    protected void onInputChanged(InBit bit) {
       if (clk.isTrue()) {
           int address = addr.read();
           if (address<out.length())
                out.bitPos(address).write(in.get());
           else
               System.out.println("BitMemory: inv�lid address ("+address+").");
       }
    }

    @Override
    public String toString() {
        return "BitMem: in="+in+" clk="+clk+" addr("+addr+") out("+out+")";
    }

    public static void main(String[] args) {
        Simul.start( ()-> new Module("BitMem",new BitMemory(4)) );
    }
}
