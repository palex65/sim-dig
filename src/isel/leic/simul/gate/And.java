package isel.leic.simul.gate;

import isel.leic.simul.bit.*;

public class And extends Gate {
	public And(int n) { super(n); }
    public And(IBit to, int n) { super(to,n); }
	public And(IBit to, OBit... from) { super(to, from); }

    protected Value inChanged() {
		for(InBit i : in)
			if (!i.isTrue()) return i.get();
		return Value.HIGH;
	}
}
