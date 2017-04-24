package isel.leic.simul.ff;

import isel.leic.simul.bit.*;

public class FfT extends FfClk {
    public final InBit t = new Input("T",null);
    protected Value nextQ() { return t.isTrue() ? q.get().not(): q.get(); }

    public FfT() {}
    public FfT(OBit clk) { clk.connect(this.clk); }
    public FfT(OBit clk, IBit to) { this(clk); q.connect(to); }
    public FfT(OBit clk, OBit t) { this(clk); this.t.connect(t); }
    public FfT(OBit clk, IBit to, OBit t) { this(clk,t); q.connect(to); }
}
