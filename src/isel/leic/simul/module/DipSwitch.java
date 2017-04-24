package isel.leic.simul.module;

import isel.leic.simul.Simul;
import isel.leic.simul.bit.OutTieBit;
import isel.leic.simul.bit.Value;
import isel.leic.simul.dat.Dat;
import isel.leic.simul.dat.OutTieDat;
import isel.leic.simul.panel.SlideSwitch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class DipSwitch extends Module implements ActionListener {
	public OutTieDat bits;
	private JCheckBox[] switches;

	public DipSwitch(String title, int n) {
		super(title,Module.LAYOUT_HORIZONTAL);
		bits = new OutBlock("D",n);
        JPanel dip = new JPanel(new GridLayout(1,n,3,0));
        center.add(dip);
		switches = new JCheckBox[n];
		for (int i = 0; i < n; i++) {
			JCheckBox sw = new SlideSwitch(""+(i+1));
			switches[i] = sw;
			dip.add(sw);
			sw.addActionListener(this);
		}
        addToSim(true);
        bits.write(0);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
        JCheckBox sw = (JCheckBox) event.getSource();
        for (int i = 0; i < switches.length; i++) {
            if (switches[i]==sw) {
                int mask = 1 << i;
                int value = bits.read();
                if (sw.isSelected()) value |= mask;
                else value &= ~mask;
                final int val = value;
                doActionLater(()->bits.write(val));
            }
        }
	}

    public static void main(String[] args) {
        Simul.start(() -> new DipSwitch("Switches",4));
    }
}