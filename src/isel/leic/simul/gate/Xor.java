package isel.leic.simul.gate;

import isel.leic.simul.bit.IBit;
import isel.leic.simul.bit.InBit;
import isel.leic.simul.bit.OBit;
import isel.leic.simul.bit.Value;

public class Xor extends Gate {
	public Xor(int n) { super(n); }
    public Xor(IBit output, OBit... inputs) {
        super(output, inputs);
    }
    public Xor(IBit output, int n) {
        super(output, n);
    }

    protected Value inChanged() {
        boolean res = false;
		for(InBit i : in)
			if (i.isTrue()) res = !res;
		return Value.valueOf(res);
	}
}
