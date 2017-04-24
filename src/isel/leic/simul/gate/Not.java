package isel.leic.simul.gate;


import isel.leic.simul.bit.IBit;
import isel.leic.simul.bit.InBit;
import isel.leic.simul.bit.OBit;
import isel.leic.simul.bit.OutBit;
import isel.leic.simul.elem.Elem;

public class Not extends Elem {
    public final InBit in = new Input("in");
    public final OutBit out = new Output("out");

    public Not() { super(); }
    public Not(IBit to) { this(); out.connect(to); }
    public Not(IBit to, OBit from) { this(to); from.connect(in); }

    protected void onInputChanged(InBit bit) {
        out.write( in.get().not() );
    }
}
