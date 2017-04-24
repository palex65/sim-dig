package isel.leic.simul.bit;

public class OutBit extends AbstractBit implements OBit {
    public OutBit() {}
    public OutBit(String name) { super(name); }
    public OutBit(boolean init) { super(Value.valueOf(init)); }
    public OutBit(String name, boolean init) { super(name,Value.valueOf(init)); }

	@Override
	public void connect(IBit ib) { addListener(ib); }
    public void connect(IBit... ibs) { for(IBit ib : ibs) addListener(ib); }
	@Override
	protected void bitChanged() { }

    public void connect(InTieBit ib) { addListener(ib); addListener(ib.getBit());}
}
