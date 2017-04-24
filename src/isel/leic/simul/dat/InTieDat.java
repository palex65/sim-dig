package isel.leic.simul.dat;


import isel.leic.simul.bit.*;

public class InTieDat extends TieDat implements IDat {
    private final InTieBit[] ties;
    private final IDat dat;

    public InTieDat(String prfxName, boolean vertical, IDat id) {
        dat = id;
        ties = new InTieBit[id.length()];
        initTies(prfxName, vertical);
    }

    public InTieDat(String prfxName, int nBits, boolean vertical) {
        dat = new InDat(prfxName,nBits,null); //, this::readValue );
        ties = new InTieBit[nBits];
        initTies(prfxName, vertical);
    }

    public InTieDat(String prfxName, int nBits) { this(prfxName, nBits, false); }
    public InTieDat(String prfxName, int nBits, InBit.BitChangedListener action) {
        this(prfxName, false, new InDat(prfxName,nBits,action));
    }
	public InTieDat(String prfxName, int nBits, boolean vertical, InBit.BitChangedListener action) {
        this(prfxName,vertical,new InDat(prfxName,nBits,action));
    }

    //private void readValue() { value = dat.read(); }
    private void initTies(String prfxName, boolean vertical) {
        for(int i=0; i<ties.length ; ++i)
            ties[i]= new InTieBit(prfxName+i,vertical,(IBit)dat.bitPos(i));
    }


    private void link(int bit, OutTieBit od) { ties[bit].link(od); }
	public void link(OutTieDat od) { link(od, 0, ties.length - 1); }
	public void link(OutTieDat od, int from, int to) {
		int idx=0;
		for(int i=from ; i<=to ; ++i, ++idx) link(idx, od.bitPos(i));
	}
	public void link(OutTieDat outPort, int mask) {
		link(outPort, Dat.toLowPos(mask), Dat.toHiPos(mask));
	}

    @Override
    public int read() { return dat.read(); }
    @Override
    public int length() { return dat.length(); }
    @Override
    public void write(int val) { throw new RuntimeException("InTieDat"); }
    @Override
    public void setInvert(boolean invert) { throw new RuntimeException("InTieDat"); }

    public InTieBit bitPos(int idx) { return ties[idx]; }
	public InTieBit bitMask(int mask) { return bitPos(Dat.toPos(mask)); }

    @Override
	public Tie[] getTies() {
		return ties;
	}

    @Override
    public void connect(ODat od, int fromPos, int toPos) { throw new RuntimeException("InTieDat"); }

    @Override
    public String toString() {
        return " dat("+dat+")";
    }

    public InDat getDat() { return (InDat)dat; } // Can trows InvalidCast
}
