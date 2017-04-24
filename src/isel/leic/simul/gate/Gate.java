package isel.leic.simul.gate;

import isel.leic.simul.bit.*;
import isel.leic.simul.elem.Elem;
import java.util.Arrays;

public abstract class Gate extends Elem {
	public final InBit in[];
	public final OutBit out = new Output("out");;

	protected Gate(int n) {
        if (n<2)
            throw new IllegalArgumentException("under two inputs");
		in = new Input[n];
        Arrays.setAll(in, i -> new Input("in"+i));
	}
    protected Gate(IBit to, OBit... from) {
        this(to, from.length);
        for (int i = 0; i < from.length; i++)
            in[i].connect(from[i]);
    }
    protected Gate(IBit to, int n) {
        this(n);
        out.connect(to);
    }

    @Override
    protected void onInputChanged(InBit bit) { out.write( inChanged() ); }
	protected abstract Value inChanged();
}
