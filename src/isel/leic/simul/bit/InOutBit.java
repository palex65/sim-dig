package isel.leic.simul.bit;

public class InOutBit extends InBit implements OBit {
    @Override
    public void connect(IBit ib) { addListener(ib); }

    public void connect(InTieBit ib) { addListener(ib); addListener(ib.getBit());}

    //public void connect(Bit b) {}

    public InOutBit() {}
    public InOutBit(IBit... ibs) { for(IBit ib : ibs) connect(ib); }
//    public InOutBit(Out ob) { connect(ob); }
//    public InOutBit(Runnable action) { super(action); }
}
