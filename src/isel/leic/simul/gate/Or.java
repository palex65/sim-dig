package isel.leic.simul.gate;

import isel.leic.simul.bit.*;
import java.util.stream.Stream;

public class Or extends Gate {
	public Or(int n) { super(n); }
    public Or(IBit to, int n) { super(to,n); }
    public Or(IBit to, OBit... from) { super(to, from); }

    protected Value inChanged() {
        return Value.valueOf(Stream.of(in).anyMatch(InBit::isTrue));
	}
}
