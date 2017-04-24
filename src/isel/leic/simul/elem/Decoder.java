package isel.leic.simul.elem;

import isel.leic.simul.Simul;
import isel.leic.simul.bit.*;
import isel.leic.simul.dat.*;
import isel.leic.simul.module.Module;

public class Decoder extends Elem {
	public final InDat sel;
    public final OutDat out;

    public Decoder(int n) {
		out = new OutputDat("out",n);
        sel = new InputDat("sel", Dat.selOf(n));
    }
    public Decoder(int n, ODat sel) {
        this(n);
        sel.connect(this.sel);
    }

    protected void onInputChanged(InBit bit) { out.write( 1 << sel.read() ); }

    @Override
    public String toString() {
        return "sel("+sel+") out("+out+")";
    }
    public static void main(String[] args) throws InterruptedException {
        Simul.App simul = Simul.start( () -> {
            Counter ctr = new Counter(3);
            Decoder dec = new Decoder(10,ctr.out);
            Clock clk = new Clock();
            clk.out.connect(ctr.up);
            new Module("counter",ctr);
            new Module("Decoder",dec);
            clk.start(100);
        } );
    }

}
