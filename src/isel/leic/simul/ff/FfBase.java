package isel.leic.simul.ff;

import isel.leic.simul.bit.IBit;
import isel.leic.simul.bit.InBit;
import isel.leic.simul.bit.OutBit;
import isel.leic.simul.elem.Elem;

public abstract class FfBase extends Elem {
    public final OutBit q = new Output("Q") {
        protected void bitChanged() { nq.write(q.get().not()); }
    };
    public final OutBit nq = new Output("nQ");
    public final InBit ar = new Input("ar");
    public final InBit ap = new Input("ap");

    protected FfBase() {
        q.reset();
        ar.reset();
        ap.reset();
    }

    @Override
    protected void onInputChanged(InBit bit) {
        if (ar.isTrue()) q.reset();
        else if (ap.isTrue()) q.set();
    }
}
