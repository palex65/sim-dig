package isel.leic.simul.module;

import isel.leic.simul.Simul;
import isel.leic.simul.bit.InBit;
import isel.leic.simul.panel.KeyPanel;

@SuppressWarnings("serial")
public class Keyboard extends KeyModule implements KeyPanel.KeyListener {
    private final KeyPanel matrix;
    private final int COLS;

    public Keyboard(String name, String keys, int l, int c) {
		super(name);
        Kack = new InPin("ack", this::ackChanged);
        Koe = new InPin("oe", this::oeChanged);
        Kval = new OutPin("val");
        Kdat = new OutBlock("K",4);

        matrix = new KeyPanel(l,c,keys,this);
        frame.add( matrix );
        addToSim(VISIBLE);
        COLS =c;
        Kval.reset();
	}

	public void ackChanged(InBit bit) {
		if (Kack.isTrue() && Kval.isTrue())
			Kval.reset();
	}
    private void oeChanged(InBit bit) {
        if (Koe.isFalse())
            Kdat.write(-1);
        else if(Kval.isTrue())
            Kdat.write(lastKey);
    }

    int lastKey;

    @Override
    public void keyChanged(int line, int col, KeyPanel.KeyButton key) {
        if (!key.isPressed()) return;
        if (Kval.isTrue() || Kack.isTrue())
            System.out.println("Keyboard hardware: "+key.getKey()+" ignored.");
        else {
            lastKey = line * COLS + col;
            doActionLater(this::action);
        }
    }

    private void action() {
        if (Koe.isTrue())
            Kdat.write(lastKey);
        Kval.set();
        ackChanged(null); // work as a (load/txE)
    }

    public static void main(String[] args) {
        Simul.start(() -> new Keyboard("Keyboard","0123456789ABCDEF",4,4));
    }
};

