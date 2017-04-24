package isel.leic.simul.ff;

import isel.leic.simul.bit.*;

public class FfD extends FfClk {
   public final InBit d = new Input("D",null);
   protected Value nextQ() { return d.get(); }

    public FfD() {}
    public FfD(OBit clk) { clk.connect(this.clk); }
    public FfD(OBit clk, IBit to) { this(clk); q.connect(to); }
    public FfD(OBit clk, OBit d) { this(clk); this.d.connect(d); }
    public FfD(OBit clk, IBit to, OBit d) { this(clk,d); q.connect(to); }
}
