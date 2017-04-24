package isel.leic.simul.dat;


import isel.leic.simul.bit.InTieBit;
import isel.leic.simul.bit.OBit;
import isel.leic.simul.bit.OutTieBit;
import isel.leic.simul.bit.Tie;

public class OutTieDat extends TieDat {
	private final OutTieBit[] ties;
    private final ODat dat;

    public OutTieDat(String prfxName, boolean vertical, ODat od) {
        this(prfxName,vertical,od,0,od.length()-1);
    }
	public OutTieDat(String prfxName, int nBits, boolean vertical) {
        this(prfxName,vertical,new OutDat(prfxName,nBits));
    }

    public OutTieDat(String prfxName, boolean vertical, ODat od, int from, int to) {
        int len = to-from+1;
        ties = new OutTieBit[ len ];
        dat = od;
        for(int i=0; i<ties.length ; ++i )
            ties[i]= new OutTieBit(prfxName+i, vertical, (OBit)dat.bitPos(from++));
    }

    public void write(int val) { dat.write(val); }
    public int read() {
        return dat.read();
    }
    //public void setOpen(boolean open) { dat.setOpen(open); }

    private void link(int bit, InTieBit in) { ties[bit].link(in); }
    public void link(InTieDat id) { link(id, 0, ties.length - 1); }
    public void link(InTieDat bits, int fromPos, int toPos) {
        int idx=0;
        for(int i=fromPos ; i<=toPos ; ++i, ++idx) link(idx, bits.bitPos(i));
    }
    public void link(InTieDat bits, int mask) {
        link(bits, Dat.toLowPos(mask), Dat.toHiPos(mask));
    }
	public OutTieBit bitPos(int idx) { return ties[idx]; }
	public OutTieBit bit(int mask) { return bitPos(Dat.toPos(mask)); }
	
	@Override
	public Tie[] getTies() {
		return ties;
	}

    @Override
    public String toString() {
        return " dat("+dat+")";
    }
}
