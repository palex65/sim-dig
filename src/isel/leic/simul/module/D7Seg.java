package isel.leic.simul.module;

import isel.leic.simul.Simul;
import isel.leic.simul.bit.InBit;
import isel.leic.simul.bit.InTieBit;
import isel.leic.simul.dat.InTieDat;
import isel.leic.simul.elem.HexTo7Seg;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class D7Seg extends Module {
	private Segments segs = new Segments();
    public InTieDat dat = new InBlock("D",7,segs::onChange);

	static class Segment extends Polygon {
		private static int[] px = { 1, 2, 2, 1, 0, 0 };
		private static int[] py = { 0, 1, 7, 8, 7, 1 };
		static {
			for(int i=0 ; i<px.length ; ++i) { px[i]*=8; py[i]*=8; }
		}
		public Segment(boolean vertical, int dx, int dy) {
			super(vertical ? px : py, vertical ? py : px, px.length);
			translate(dx, dy);
		}
		public void paint(Graphics g) { 
			g.fillPolygon(this); 
		}
	}
	
	class Segments extends JComponent {
		
		private Segment[] seg = { 
			new Segment(false,12,  2), //a		
			new Segment(true, 70, 12), //b
			new Segment(true, 70, 80), //c
			new Segment(false,12,138), //d		
			new Segment(true,  2, 80), //e
			new Segment(true,  2, 12), //f  
			new Segment(false,12, 70)  //g		
		};
		
		public void paint(Graphics g) {
			for(int i=0 ; i<seg.length ; ++i) {
				g.setColor(dat.bitPos(i).isTrue() ? Color.ORANGE : Color.BLACK);
				seg[i].paint(g);
			}
		}
		
		public Dimension getPreferredSize() {
			return new Dimension(88, 156);
		}
		public void onChange(InBit bit) {
			repaint();
		}
	}

    public D7Seg(String name) {
		super(name);
		center.setLayout(new FlowLayout());
		center.add(segs);
        center.setBackground(Color.DARK_GRAY);
		for(int i=0 ; i<7 ; ++i)
			((InTieBit)(dat.bitPos(i))).setTieName(""+(char)('a'+i));
        addToSim(VISIBLE);
	}

    public static void main(String[] args) {
        Simul.start(()->{
			D7Seg d = new D7Seg("D7Seg");
		});
    }

};
