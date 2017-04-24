package isel.leic.simul.ff;

import isel.leic.simul.Simul;
import isel.leic.simul.bit.IBit;
import isel.leic.simul.bit.InBit;
import isel.leic.simul.bit.OBit;
import isel.leic.simul.module.Module;

public class FfSR extends FfBase {
    public final InBit s = new Input("S");
    public final InBit r = new Input("R");

    public FfSR() {}
    public FfSR(IBit to) { q.connect(to); }
    public FfSR(OBit s, OBit r) { s.connect(this.s); r.connect(this.r); }
    public FfSR(IBit to, OBit s, OBit r) { this(s,r); q.connect(to); }

    protected void onInputChanged(InBit bit) {
        super.onInputChanged(bit);
        if (ar.isTrue() || ap.isTrue() || s==null || r==null ) return;
        if (s.isTrue() && r.isFalse())
          q.set();
        else if (s.isFalse() && r.isTrue())
          q.reset();
        else if (s.isTrue() && r.isTrue()) {
          q.reset();
          nq.reset();
        }
    }

    @Override
    public String toString() {
        return " S="+s+" R="+r+" Q="+q+" /Q="+nq;
    }

    public static void main(String[] args) {
        Simul.start(() -> {
            new Module("SR",new FfSR());
        });
    }
}
