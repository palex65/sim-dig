package isel.leic.simul.ff;

import isel.leic.simul.bit.*;

public class LatchD extends Latch {
    public final InBit d = new Input("D");
    @Override
    protected Value nextQ() { return d.get(); }

    public LatchD() {}
    public LatchD(IBit to) { q.connect(to); }
    public LatchD(OBit d, OBit en) { d.connect(this.d); en.connect(this.en); }
    public LatchD(IBit to, OBit d, OBit en) { this(d,en); q.connect(to); }
}
