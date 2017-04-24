package isel.leic.simul.elem;

import isel.leic.simul.bit.*;
import isel.leic.simul.dat.*;

import java.util.Arrays;


public class Mux extends Elem {
	public final InDat sel;
    public final InDat in;
    public final OutBit out = new Output("out");

    public Mux(int n) {
		in = new InputDat("in",n,this::onInputChanged);
        int nbits=1;
        for (int i = 2; i < n ; i*=2)
            nbits++;
        sel = new InputDat("sel",nbits);
    }
    public Mux(int n, IBit output) {
        this(n);
        out.connect(output);
    }

    protected void onInputChanged(InBit bit) {
        int idx = sel.read();
        if (idx==Dat.OPEN) return;
        out.write(in.bitPos(idx).get());
    }
}
