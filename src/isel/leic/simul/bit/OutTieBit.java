package isel.leic.simul.bit;


import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class OutTieBit extends Tie implements OBit {
	private OBit bit;

    public OutTieBit(String name, boolean vertical) {
        this(name,vertical,new OutBit());
    }
	public OutTieBit(String name, boolean vertical, OBit bit) {
		super(name);
		label.setHorizontalAlignment(vertical ? SwingConstants.CENTER : SwingConstants.RIGHT);
		label.setVerticalAlignment(vertical ? SwingConstants.BOTTOM : SwingConstants.CENTER);
		add(but, vertical ? BorderLayout.SOUTH : BorderLayout.EAST);
		update(Value.OPEN);
        setBit(bit);
	}
	public void link(InTieBit ib) {
        addLinkName(ib.getGlobalName());
        ib.addLinkName(getGlobalName());
		bit.connect(ib.getBit());
	}
    public OBit getBit() { return bit; }
    public void setBit(OBit bit) { this.bit = bit; bit.addListener(this); }

    // Delegation in bitMask
    @Override
    public Value get() { return bit.get(); }
    @Override
    public void write(Value value) { bit.write(value);  }
	@Override
	public void addListener(BitListener bl) { bit.addListener(bl); }
	@Override
	public void connect(IBit ib) { bit.connect(ib); }
    @Override
    public void pull(boolean up) { bit.pull(up); addLinkName("pull "+(up?"UP":"DOWN")); }
    @Override
    public void setInvert(boolean invert) { bit.setInvert(invert); }
    @Override
    public boolean isInverted() { return bit.isInverted(); }

    @Override
    public String toString() {
        return super.toString()+"->"+ bit.toString();
    }

    public void link(InBit ib) {
        addLinkName("[InBit]");
        bit.connect(ib);
    }
}
