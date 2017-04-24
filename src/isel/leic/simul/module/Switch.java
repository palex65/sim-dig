package isel.leic.simul.module;

import isel.leic.simul.Simul;
import isel.leic.simul.bit.OutTieBit;
import isel.leic.simul.bit.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class Switch extends Module implements ActionListener {
	public OutTieBit bit = new OutPin("out");
	protected JButton but;
    private static Color onColor = new Color(0,150,0);
    private static Color offColor = Color.BLACK;
    private Font offFont, onFont;

	public Switch(String title) {
		super(title);
		but = new JButton(title);
		but.addActionListener(this);
		frame.add(but);
        offFont = but.getFont();
        onFont = offFont.deriveFont(Font.BOLD);
        update(false);
        addToSim(true);
        bit.reset();
	}

    private void update(boolean on) {
        but.setForeground( on ? onColor : offColor);
        but.setFont( on ? onFont : offFont );
    }

	@Override
	public void actionPerformed(ActionEvent ignore) {
        Value value = bit.get().not();
		doActionLater(() -> bit.write( value ));
		update( value==Value.HIGH );
	}

    public void setLabel(String label) {
        but.setText(label);
    }

    public static void main(String[] args) {
        Simul.start(() -> new Switch("Switch"));
    }
}