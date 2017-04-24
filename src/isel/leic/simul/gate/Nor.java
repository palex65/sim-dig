package isel.leic.simul.gate;

import isel.leic.simul.bit.IBit;
import isel.leic.simul.bit.OBit;

public class Nor extends Or {
    public Nor(int n)                      { super(n); }
    public Nor(IBit output, OBit... inputs){ super(output, inputs); }
    public Nor(IBit output, int n)         { super(output,n); }
    {
        out.setInvert(true);
    }
}
