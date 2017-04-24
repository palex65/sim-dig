package isel.leic.simul.elem;

import isel.leic.simul.bit.*;
import isel.leic.simul.dat.*;

public class Comparator extends Elem {
    public final InDat a, b;
    public final OutBit equal = new Output("equal");
    public final OutBit great = new Output("great");
    public final OutBit less = new Output("less");

    public Comparator(int n) {
        a = new InputDat("a",n);
        b = new InputDat("b",n);
    }
    public Comparator(ODat a, ODat b) {
        this(a.length());
        a.connect(this.a);
        b.connect(this.b);
    }

    private int value; // Fixed value (if b==null)

    public Comparator(ODat a, int b) {
        int n = a.length();
        if (Dat.nBits(b)>n)
            throw new IllegalArgumentException("Comparator: "+b+" > "+Dat.maskOf(n));
        this.a = new InputDat("a",n);
        this.b =null;
        a.connect(this.a);
        this.value = b;
        onInputChanged(null);
    }

    protected void onInputChanged(InBit bit) {
        int va = a.read();
        int vb = (b==null) ? value : b.read();
        equal.write(Value.valueOf(va==vb));
        great.write(Value.valueOf(va>vb));
        less.write(Value.valueOf(va<vb));
    }

    public String toString() {
        return "a("+a+") b"+(b==null?("="+value):("("+b+")"))+" eq="+equal+" gr="+great+" ls="+less;
    }
}
