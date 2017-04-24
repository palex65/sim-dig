package isel.leic.simul.elem;

import isel.leic.simul.bit.InBit;
import isel.leic.simul.dat.Dat;
import isel.leic.simul.dat.InDat;
import isel.leic.simul.dat.InOutDat;

public class RAM extends Elem {
	public final InBit oe = new Input("oe", this::oeChanged );
    public final InBit wr = new Input("wr", this::wrChanged );

    public final InDat addr;
    public final InOutDat dat;

    private int [] memory;

    public RAM(int n, int dim) {
        dat = new InOutDat("D",n,this::write);
        memory = new int[dim];
        addr = new InputDat("addr",Dat.selOf(n),this::addrChanged);
        oe.reset();
        wr.reset();
	}

    private void addrChanged(InBit bit) {
        read();
        write();
    }

    private void oeChanged(InBit bit) {
        read();
        if (oe.isFalse())
            dat.write(Dat.OPEN);
    }

    private void read() {
        if (oe.isTrue()) {
            int address = addr.read();
            if (address==Dat.OPEN || address>=memory.length) return;
            dat.write(memory[address]);
        }
    }

    private void write() {
        if (wr.isTrue())
            memory[ addr.read() ] = dat.read();
    }

    private void wrChanged(InBit bit) {
        write();
    }

    @Override
    public String toString() {
        String res = "[ ";
        for(int v : memory) res += v+" ";
        return res + "] oe="+oe+" wr="+wr+" add("+addr+") dat("+dat+")";
    }

    public int size() { return memory.length; }

    public int get(int i) { return memory[i]; }

    @Override
    protected void onInputChanged(InBit bit) { }
}
