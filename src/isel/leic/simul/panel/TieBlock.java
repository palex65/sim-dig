package isel.leic.simul.panel;


import isel.leic.simul.bit.Tie;
import isel.leic.simul.dat.TieDat;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class TieBlock extends JPanel {
	GridLayout gl;
	boolean vertical;
	private String moduleName;

	public void addBit(Tie bitView) { addBit(bitView,bitView); }

    public void addBit(Tie bit, JComponent view) {
        if (vertical) gl.setColumns( gl.getColumns()+1 );
        else gl.setRows( gl.getRows()+1 );
        super.add(view);
        bit.setModuleName(moduleName);
    }

    public void addBits(TieDat dat) {
    	Tie[] v = dat.getTies();
    	if (vertical) {
        	for(int i=v.length-1 ; i>=0 ; --i)
        		addBit(v[i]);
    	} else {
    		for(Tie bv : v)
    			addBit(bv);
    	}
    }

	public TieBlock(boolean vertical, String moduleName) {
		super(new GridLayout(vertical?1:0,vertical?0:1));
		this.moduleName = moduleName;
		gl = (GridLayout) getLayout();
		this.vertical = vertical;
	}
}
