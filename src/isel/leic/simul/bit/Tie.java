package isel.leic.simul.bit;

import isel.leic.simul.Simul;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

@SuppressWarnings("serial")
public abstract class Tie extends JPanel implements BitListener {
	protected JLabel label = new JLabel("");
	protected JButton but = new JButton("?");
    private String moduleName;

    private BitListener next;
    public BitListener getNext() { return next; }
    public void setNext(BitListener n) { next=n; }

	public Tie(String name) {
		label.setText(name);
        label.setBackground(Color.LIGHT_GRAY);
        label.setOpaque(true);
        but.setOpaque(true);
        setLayout( new BorderLayout() );
		add(label, BorderLayout.CENTER);
	}

	public void update(Value value) {
        if (SwingUtilities.isEventDispatchThread())
            updateButton(value);
        else
            SwingUtilities.invokeLater(() -> updateButton(value) );
	}

    private void updateButton(Value value) {
        but.setBackground(value.color);
        but.setText(Character.toString(value.view));
    }

    public void setTieName(String name) { label.setText(name); }
    public String getTieName() { return  label.getText(); }

	public void addLinkName(String lnk) {
		String old = but.getToolTipText();
		if (old!=null && old.length()>0) lnk = old+';'+lnk;
		but.setToolTipText(lnk);
        label.setBackground(Color.WHITE);
	}
	public String getLinkName() { return but.getToolTipText(); }

    public void setModuleName(String name) { moduleName=name; }
    public String getGlobalName() {
        return (moduleName==null ? "" : moduleName+".")+getTieName();
    }

    @Override
    public String toString() {
        return getGlobalName();
    }

    private static Simul.UI simul;
    public static void setSimul(Simul.UI s) { simul=s; }
    protected void doAction(Runnable action) { simul.doAction(action); }
}
