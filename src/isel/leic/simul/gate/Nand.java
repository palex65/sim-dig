package isel.leic.simul.gate;
import isel.leic.simul.bit.*;

public class Nand extends And {
    { out.setInvert(true); }
	public Nand(int n) { super(n); }
    public Nand(IBit to, int n) { super(to,n); }
    public Nand(IBit to, OBit... from){ super(to, from); }
}
