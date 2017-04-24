package isel.leic.simul.bit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class InTieBit extends Tie implements IBit {
    private IBit bit;
    private boolean linked = false;

    public InTieBit(String name, boolean vertical) { this(name,vertical,new InBit()); }
    public InTieBit(String name, boolean vertical, InBit.BitChangedListener action) {
        this(name,vertical,new InBit(action));
    }
    public InTieBit(String name, boolean vertical, IBit bit) {
        super(name);
        label.setHorizontalAlignment(vertical ? SwingConstants.CENTER : SwingConstants.LEFT);
        label.setVerticalAlignment(vertical ? SwingConstants.TOP : SwingConstants.CENTER);
        add(but, vertical ? BorderLayout.NORTH : BorderLayout.WEST);
        but.addActionListener((ActionEvent e) -> clickAction());
        setBit(bit);
    }
    public IBit getBit() { return bit; }
    public void setBit(IBit bit) { this.bit = bit; bit.addListener(this); }

    protected void clickAction() {
        Value value = bit.get();
        if (bit.isInverted()) value = value.not();
        if (value.isOpen()) value = Value.HIGH;
        final Value val = value.not();
        doAction(() -> bit.write(val));
    }
    public void link(OutTieBit ob) {
        if (linked)
            throw new IllegalArgumentException("InTieBit double linked:"+getLinkName()+" and "+ob.getGlobalName());
        addLinkName(ob.getGlobalName());
        ob.addLinkName(getGlobalName());
        bit.connect(ob.getBit());
        linked = true;
    }
    public void link(OutBit ob) {
        addLinkName("[OutBit]");
        bit.connect(ob);
    }

    protected void bitChanged() { }

    // Delegation in bitMask
    @Override
    public Value get() { return bit.get(); }
    @Override
    public void write(Value val) { bit.write(val); }
    @Override
    public void addListener(BitListener bl) { bit.addListener(bl); }
    @Override
    public void connect(OBit ob) { bit.connect(ob); }
    @Override
    public void pull(boolean up) { bit.pull(up); addLinkName(up?"UP":"DOWN"); }
    @Override
    public void setInvert(boolean invert) { bit.setInvert(invert); }
    @Override
    public boolean isInverted() { return bit.isInverted(); }
    @Override
    public String toString() {
        return super.toString()+"->"+ bit.toString();
    }
}
