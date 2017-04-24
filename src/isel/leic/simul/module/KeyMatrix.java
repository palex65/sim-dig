package isel.leic.simul.module;

import isel.leic.simul.Simul;
import isel.leic.simul.bit.InBit;
import isel.leic.simul.bit.InTieBit;
import isel.leic.simul.bit.OutTieBit;
import isel.leic.simul.bit.Value;
import isel.leic.simul.dat.InTieDat;
import isel.leic.simul.dat.OutTieDat;
import isel.leic.simul.panel.KeyPanel;
import isel.leic.simul.panel.TieBlock;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class KeyMatrix extends Module implements KeyPanel.KeyListener {
    public InTieDat cols;
	public OutTieDat lines;

    private TieBlock blkLines;
    private TieBlock blkCols;
    private final KeyPanel matrix;

    private GridBagConstraints gbc = new GridBagConstraints();

    public KeyMatrix(String name, String keys, int l, int c) {
        super(name);
        frame.getContentPane().removeAll();
        matrix = new KeyPanel(l,c,keys,this);

        blkCols = inputs = new TieBlock(LAYOUT_VERTICAL, getName());
        blkLines = outputs = new TieBlock(LAYOUT_HORIZONTAL, getName());

        frame.setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.BOTH;

        add(blkCols,   0,0,  c,1, 0,0);
        add(matrix, 0,1,  c,l, 0.5,0.5);
        add(blkLines,  c,1,  1,l, 0,0);

        cols = new InTieDat("Col",c,LAYOUT_VERTICAL, this::evaluate );
        for(int i=0 ; i<c ; i++) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
            InTieBit tb = cols.bitPos(i);
            p.add(tb);
            blkCols.addBit(tb, p);
        }
        lines = new OutTieDat("Line",l,LAYOUT_HORIZONTAL);
        for(int i=0 ; i<l ; i++) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING,0,0));
            OutTieBit tb = lines.bitPos(i);
            //tb.pullUp();
            p.add(tb);
            blkLines.addBit(tb, p);
        }
        addToSim(true);
        evaluate(null);
    }

    private void add(JComponent comp,
                     int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty) {
        gbc.gridx=gridx; gbc.gridy=gridy;
        gbc.gridwidth=gridwidth; gbc.gridheight=gridheight;
        gbc.weightx = weightx; gbc.weighty = weighty;
        frame.add(comp,gbc);
    }

    @Override
    public void keyChanged(int line, int col, KeyPanel.KeyButton key) {
        doActionLater(() -> evaluate(null));
    }

    private void evaluate(InBit bit) {
        Value val; int l,c;
        for (l=0 ; l< lines.getTies().length ; ++l) {
            val = Value.OPEN;
            for (c = 0; c < cols.getTies().length; ++c)
                if (matrix.isPressed(l,c) && !cols.bitPos(c).isOpen()) {
                    val = cols.bitPos(c).get();
                    break;
                }
            lines.bitPos(l).write(val);
        }
    }
    public static void main(String[] args) {
        Simul.start(() -> new KeyMatrix("KeyMatrix","123456789",3,3));
    }

};

