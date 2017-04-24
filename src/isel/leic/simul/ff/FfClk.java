package isel.leic.simul.ff;

import isel.leic.simul.Simul;
import isel.leic.simul.bit.InBit;
import isel.leic.simul.bit.Value;
import isel.leic.simul.elem.Elem;
import isel.leic.simul.module.Module;

public abstract class FfClk extends FfBase {
    public final InBit clk = new Elem.Input("clk",this::onClkChanged);

    protected void onClkChanged(InBit bit) {
        if (bit==clk && clk.isTrue() && ar.isFalse() && ap.isFalse())
            q.write(nextQ());
    }
    protected abstract Value nextQ();

    public static void main(String[] args) {
        Simul.start( () -> {
            new Module("SR", new FfSR());
            new Module("JK", new FfJK());
            new Module("D", new FfD());
        });
    }
}
