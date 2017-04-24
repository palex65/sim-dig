package isel.leic.simul.elem;

import isel.leic.simul.bit.*;
import isel.leic.simul.dat.*;

public class Counter extends Elem {
	public final InBit ce = new Input("ce", null);
    public final InBit ar = new Input("ar");
    public final InBit up = new Input("up", this::upChanged);
    public final InBit down = new Input("down", this::downChanged );

    public final OutDat out;

    private final int MAX;
    private int value;

    public Counter(int n) {
		out = new OutputDat("out",n);
        MAX = ~(-1 << n);
        ar.reset();
        ce.set();
        out.write( value = 0 );
	}

    public Counter(IDat output) {
        this(output.length());
        out.connect(output);
    }

    protected void onInputChanged(InBit bit) {
        if (ar.isTrue())
            out.write( value = 0 );
    }

    private void upChanged(InBit bit) {
        if (up.isTrue() && ce.isTrue() && ar.isFalse()) {
            ++value;
            if (value>MAX) value=0;
            out.write(value);
        }
    }

    private void downChanged(InBit in) {
        if (down.isTrue() && ce.isTrue() && ar.isFalse()) {
            --value;
            if (value<0) value = MAX;
            out.write(value);
        }
    }

    @Override
    public String toString() {
        return "Counter to "+MAX+" : "+out+" ce="+ce+" ar="+ar+" up="+up+" down="+down;
    }
}
