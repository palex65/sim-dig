package isel.leic.simul.ff;

import isel.leic.simul.bit.*;
import isel.leic.simul.ff.FfBase;

public abstract class Latch extends FfBase {
    public InBit en = new Input("en");

    protected void onInputChanged(InBit bit) {
        super.onInputChanged(bit);
        if (en!=null && en.isTrue() && ar.isFalse() && ap.isFalse())
            q.write(nextQ());
    }
    protected abstract Value nextQ();
}
