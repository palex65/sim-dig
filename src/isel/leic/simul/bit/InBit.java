package isel.leic.simul.bit;

public class InBit extends AbstractBit implements IBit {
    public interface BitChangedListener {
        void bitChanged(InBit bit);
    }
    private final BitChangedListener action;

    private BitListener next;
    public BitListener getNext() { return next; }
    public void setNext(BitListener n) { next=n; }

    public BitChangedListener getAction() { return action; }

    public InBit(String name, BitChangedListener action) { super(name); this.action=action; }
    public InBit(BitChangedListener action) { this.action=action; }
    public InBit(String name) { this(name,null); }
    public InBit() { action=null; }

    @Override
    public void bitChanged() { if (action!=null) action.bitChanged(this); }
    @Override
    public void connect(OBit ob) { ob.connect(this); }
    @Override
    public void update(Value value) { writeLater(this, value); }

    public void connect(OutTieBit ob) { ob.link(this); }
}
