package isel.leic.simul.module;

import isel.leic.simul.Simul;
import isel.leic.simul.bit.InBit;
import isel.leic.simul.bit.InTieBit;
import isel.leic.simul.dat.InTieDat;
import isel.leic.simul.panel.CharLCD;

import javax.swing.*;
import java.awt.*;

public class LCD extends Module {
	public InTieBit e = new InPin("e",this::enableChanged);
	public InTieBit rs = new InPin("rs");
	public InTieDat dat = new InBlock("D",8);
	
	private static final int LINES = 2;
	private static final int COLS = 16;
	private static final int LINE_MEM = 0x40;
	private static final int CG_CHARS = 8, PATTERN_LINES = 8;

	private int adrDD, adrCG=-1;
	private boolean cursorVisible;
	private boolean onOff;

	private char[] ddRAM = new char[LINES * LINE_MEM];
	private byte[] cgRAM = new byte[CG_CHARS * PATTERN_LINES];
    private CharLCD[][] cells = new CharLCD[LINES][COLS];
	private Thread blink = null;

    public LCD(String name) {
		super(name,LAYOUT_VERTICAL);
        JPanel p = new JPanel( new GridLayout(LINES,COLS,2,5));
        center.add(p);
        CharLCD e;
        for(int l=0 ; l<LINES ; ++l)
            for( int c=0 ; c<COLS ; ++c) {
                e = cells[l][c] = new CharLCD();
                p.add(e);
            }
        addToSim(true);
		init();
	}
	
	private boolean interface8bit = false;
	private boolean interface4bit = false;

    private boolean firstNibble = true;
	private int data8;
	private int counterInitSeq = 0;

	public void enableChanged(InBit bit) {
		if ( e.isTrue() ) return; // down transition
		if (rs.isFalse() && (dat.read()&0xE0)==0x20) {
			if (++counterInitSeq==4) {
				functionSetCMD(dat.read());
				return;
			}
		}
		else counterInitSeq=0;
		if (interface8bit) {
			data8 = dat.read();
			process8Bits();
		}
		if (interface4bit) {
            if (firstNibble)
                data8 = dat.read() & 0xF0;
            else {
                data8 |= (dat.read() & 0xF0) >>> 4;
                process8Bits();
            }
            firstNibble = !firstNibble;
        }
    }
    
    public void process8Bits() {
        //System.out.println("LCD: RS="+rs.isTrue()+" data="+Integer.toHexString(data8));
    	if (rs.isTrue())
            if (adrCG>=0) writeCGRamDATA();
			else writeDDRamDATA();
    	else {
			     if ((data8 & 0x80)!=0) setDDRamCMD();
			else if ((data8 & 0x40)!=0) setCGRamCMD();
		 	else if ((data8 & 0x20)!=0) functionSetCMD(data8);
			else if ((data8 & 0x10)!=0) cursorDisplayShiftCMD();
			else if ((data8 & 0x08)!=0) displayOnOffCMD();
			else if ((data8 & 0x04)!=0) entryModeSetCMD();
			else if ((data8 & 0x02)!=0) returnHomeCMD();
			else if ((data8 & 0x01)!=0) clearDisplayCMD();
    	}
    }

	private void hideCursor() { hideCursor(false); }
    private void hideCursor(boolean forced) {
    	if (!forced && (!cursorVisible || !onOff)) return;
		int l = adrDD / LINE_MEM;
		int c = adrDD % LINE_MEM;
		if (c<COLS && l<LINES) 
			cells[l][c].resetCursor();
    }

	private void showCursor() { showCursor(false); }
    private void showCursor(boolean forced) {
        if (!forced && (!cursorVisible || !onOff)) return;
		int l = adrDD / LINE_MEM;
		int c = adrDD % LINE_MEM;
		if (c<COLS && l<LINES)
			cells[l][c].setCursor();    	
    }

	private void writeCGRamDATA() {
		cgRAM[adrCG] = (byte)data8;
		CharLCD.setPattern(adrCG,(byte)data8);
		++adrCG;
		if(adrCG>=cgRAM.length)
			adrCG = 0;
	}

	private void writeDDRamDATA() {
		ddRAM[adrDD] = (char)data8;
		int l = adrDD / LINE_MEM;
		int c = adrDD % LINE_MEM;
		if (c<COLS && l<LINES && onOff)
			cells[l][c].setChar(ddRAM[adrDD]);
		hideCursor();
		++adrDD;
        if (adrDD >= ddRAM.length)
            adrDD =0;
		showCursor();
	}	

	private void setDDRamCMD() {
		hideCursor();
		adrDD = data8 & 0x7F;
		adrCG =-1;
		showCursor();
	}

	private void init() {
		CharLCD cell;
		for( int c=0 ; c<COLS ; ++c) {
			cell = cells[0][c];
			cell.resetCursor();
			cell.setChar('\u00FF');
		}
	}

	private void clearDisplayCMD() {
		hideCursor();
		adrDD =0;
		for(int i=0 ; i<LINES*LINE_MEM ; ++i) ddRAM[i]=' ';
		clearLCD();
		showCursor();
	}

	private void refreshLCD() {
		CharLCD cell;
		for(int l=0 ; l<LINES ; ++l)
			for( int c=0 ; c<COLS ; ++c) {
				cell = cells[l][c];
				cell.resetCursor();
				cell.setChar(ddRAM[l*LINE_MEM+c]);
			}
	}

	private void clearLCD() {
		CharLCD cell;
		for(int l=0 ; l<LINES ; ++l)
			for( int c=0 ; c<COLS ; ++c) {
				cell = cells[l][c];
				cell.resetCursor();
				cell.setChar(' ');
			}
	}

	private void setCGRamCMD() {
		adrCG = data8 & 0x3F;
	}

	private void returnHomeCMD() {
		hideCursor();
		adrDD = 0;
		adrCG = -1;
		showCursor();
	}

	private void cursorDisplayShiftCMD() {}

	// COMMAND: Function set
	private static final int
		DL_MASK = 0x10,	N_MASK = 0x08, F_MASK = 0x04;
	private void functionSetCMD(int data) {
		interface8bit = (data & DL_MASK)!=0;
		if (!interface8bit) {
			interface4bit = true;
			if (counterInitSeq==4) return;
		}
		if ((data & N_MASK)==0 || (data & F_MASK)!=0)
			System.out.println("LCD: Only N=1 and F=0 supported in Function set");
		clearLCD();
	}

	// COMMAND: Entry mode set
	private static final int
		ID_MASK = 0x02,	S_MASK = 0x01;
	private void entryModeSetCMD() {
		if ((data8 & ID_MASK)==0 || (data8 & S_MASK)!=0)
			System.out.println("LCD: Only I/D=1 and S=0 supported in Entry mode set");
	}

	// COMMAND: Display on/off control
	private static final int
			D_MASK = 0x04,	C_MASK = 0x02, B_MASK = 0x01;
	private void displayOnOffCMD() {
		boolean on = ((data8 & D_MASK)!=0) ; // Display ON/OFF
		boolean curVisible = ((data8 & C_MASK)!=0) ; // Cursor ON/OFF
		boolean curBlink = ((data8 & B_MASK)!=0) ; // Blink ON/OFF

		if (on && !onOff) refreshLCD();
		if (!on && onOff) clearLCD();
		onOff = on;

		if (curVisible && !cursorVisible) showCursor(true);
		if (!curVisible && cursorVisible) hideCursor(true);
	    cursorVisible = curVisible;

	    if (curBlink && cursorVisible && blink==null && onOff) {
			blink = new Thread() {
				public void run() {
					try {
						for(;;) {
							SwingUtilities.invokeLater(LCD.this::showCursor);
                            Thread.sleep(750);
                            SwingUtilities.invokeLater(LCD.this::hideCursor);
                            Thread.sleep(750);
						}
					} catch (InterruptedException e) { }
				}
			};
			blink.start();
		}
	    if ((!curBlink || !cursorVisible || !onOff) && blink!=null) {
	    	blink.interrupt();
	    	blink=null;
	    }
	}

    public static void main(String[] args) {
        Simul.start(()->new LCD("lcd"));
    }

};
