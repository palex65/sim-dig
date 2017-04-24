package isel.leic.simul.ff;

import isel.leic.simul.bit.*;

public class FfJK extends FfClk {
   public final InBit j = new Input("J",null);
   public final InBit k = new Input("K",null);
   protected Value nextQ() {
	 if (j.isTrue() && k.isFalse()) return Value.HIGH;
	 if (j.isFalse() && k.isTrue()) return Value.LOW;
	 return j.isTrue() ? q.get() : nq.get();
   }

    public FfJK() {}
    public FfJK(OBit clk) { clk.connect(this.clk); }
    public FfJK(OBit clk, IBit to) { this(clk); q.connect(to); }
    public FfJK(OBit clk, OBit j, OBit k) { this(clk); this.j.connect(j); this.k.connect(k);}
    public FfJK(OBit clk, IBit to, OBit j, OBit k) { this(clk,j,k); q.connect(to); }

}
