package isel.leic.simul.panel;

import javax.swing.*;
import java.awt.*;

public class SlideSwitch extends JCheckBox {

    private String label;
    private Dimension min = new Dimension(15,40);
    private Dimension pref = new Dimension(20,55);
    private FontMetrics fm;
    private Color ON = new Color(0,150,0);

    public SlideSwitch(String txt) {
        super(txt);
        label = txt;
        fm = getFontMetrics(getFont());
    }
    public Dimension getPreferredSize() {
        return pref;
    }
    public Dimension getMinimumSize() {
        int minW = fm.stringWidth(label);
        System.out.println(minW);
        if (minW>min.width) {
            min.width = minW;
        }
        return min;
    }
    public Dimension getMaximumSize() {
        return super.getMaximumSize();
    }
    public void paintComponent(Graphics g) {
        int w = getWidth(), h = getHeight();
        int b = h-fm.getHeight();
        boolean sel = isSelected();
        g.setColor(getBackground());
        g.fillRect(0,0,w,h);
        g.setColor(sel?ON:Color.GRAY);
        g.fillRect(1,1,w-2,b-2);
        g.setColor(Color.WHITE);
        g.fillRect(2,sel?2:b/2-2,w-4,b/2);
        g.setColor(Color.BLACK);
        g.drawRect(2,sel?2:b/2-2,w-4,b/2);
        g.setColor(Color.BLUE);
        g.drawRect(0,0,w-1,b-1);
        g.setColor(Color.BLACK);
        g.drawString(label,(w-fm.stringWidth(label))/2,h-fm.getDescent());
    }
}
