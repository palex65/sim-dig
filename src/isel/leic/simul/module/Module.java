package isel.leic.simul.module;

import isel.leic.simul.Simul;
import isel.leic.simul.bit.*;
import isel.leic.simul.dat.IDat;
import isel.leic.simul.dat.InTieDat;
import isel.leic.simul.dat.ODat;
import isel.leic.simul.dat.OutTieDat;
import isel.leic.simul.elem.Elem;
import isel.leic.simul.panel.TieBlock;
import isel.leic.simul.parser.DinamicModule;
import isel.leic.simul.state.State;
import isel.leic.simul.state.StateMachine;
import isel.leic.simul.state.StateMachineListener;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.lang.reflect.Field;

public class Module {
    public static final boolean LAYOUT_VERTICAL =true, LAYOUT_HORIZONTAL =false;
    public static final boolean VISIBLE =true, HIDE =false;

    public class Frame extends JInternalFrame {
        public Frame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconable) {
            super(title,resizable,closable,maximizable,iconable);
        }
        public Module getModule() {
            return Module.this;
        }
    };
    //private static final boolean BITS_VISIBLE = true;
	private final boolean verticalLayout;
    protected final Frame frame;
	protected TieBlock inputs;
	protected TieBlock outputs;
    protected JLabel status;
	protected JPanel center = new JPanel();
    private JCheckBoxMenuItem showTies,showStatus;
    private final String name;

	protected class InPin extends InTieBit {
		public InPin(String name) { this(name, (InBit.BitChangedListener)null); }
        public InPin(String name, InBit.BitChangedListener action) {
            super(name, verticalLayout,action); inputs.addBit(this);
        }
        public InPin(String name, IBit bit) { super(name, verticalLayout,bit); inputs.addBit(this); }
	}
    protected class OutPin extends OutTieBit {
		public OutPin(String name) { super(name, verticalLayout); outputs.addBit(this); }
        public OutPin(String name, OBit bit) { super(name, verticalLayout,bit); outputs.addBit(this); }
	}
    protected class InBlock extends InTieDat {
		public InBlock(String prfxName, int len) { this(prfxName, len, null); }
        public InBlock(String prfxName, int len, InBit.BitChangedListener action) {
            super(prfxName,len, verticalLayout,action); inputs.addBits(this);
        }
        public InBlock(String prfxName, IDat bits) { super(prfxName, verticalLayout, bits); inputs.addBits(this);}
	}
    protected class OutBlock extends OutTieDat {
		public OutBlock(String prfxName, int len) {	this(prfxName, len, verticalLayout); }
        public OutBlock(String prfxName, int len, boolean vert) { super(prfxName,len,vert); outputs.addBits(this); }
        public OutBlock(String prfxName, ODat bits) { super(prfxName, verticalLayout, bits); outputs.addBits(this);}
        public OutBlock(String prfxName, ODat bits, int from, int to) {
            super(prfxName, verticalLayout, bits, from, to); outputs.addBits(this);
        }
	}

    public InTieBit getInput(String name) {
        for (Component comp : inputs.getComponents()) {
            InTieBit input = (InTieBit) comp;
            if (input.getTieName().equals(name)) return input;
        }
        return null;
    }

    public OutTieBit getOutput(String name) {
        for (Component comp : outputs.getComponents()) {
            OutTieBit output = (OutTieBit) comp;
            if (output.getTieName().equals(name)) return output;
        }
        return null;
    }

    private Tie findTieIn(String name, Component[] comps) {
        for (Component comp : comps)
            if (comp instanceof Tie) {
                Tie tie = (Tie) comp;
                if (tie.getTieName().equalsIgnoreCase(name)) return tie;
            } else if (comp instanceof JPanel) {
                Tie tie = findTieIn(name,((JPanel)comp).getComponents());
                if (tie != null) return tie;
            }
        return null;
    }

    public Tie getTie(String name) {
        Tie tie = findTieIn(name,inputs.getComponents());
        if (tie==null)
            tie = findTieIn(name,outputs.getComponents());
        return tie;
    }

	public Module(String name) { this(name,LAYOUT_HORIZONTAL); }

	protected Module(String name, boolean layout) {
        frame = new Frame(name,true,false,false,true);
		// super(title,true,false,false,true);
        this.name = name;
		this.verticalLayout = layout;
		inputs = new TieBlock(layout,name);
		outputs = new TieBlock(layout,name);
		JPanel pi = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
		pi.add(inputs);
		JPanel po = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
		po.add(outputs);

        frame.add(pi, layout ? BorderLayout.NORTH : BorderLayout.WEST);
		frame.add(po, layout ? BorderLayout.SOUTH : BorderLayout.EAST);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        if (canShowStatus()) {
            JPanel area = new JPanel(new BorderLayout());
            status = new JLabel();
            status.setBackground(Color.WHITE);
            status.setOpaque(true);
            status.setVisible(false);
            JPanel bp = new JPanel(); bp.add(status);
            area.add(bp,BorderLayout.NORTH);
            area.add(center,BorderLayout.CENTER);
            frame.add(area,BorderLayout.CENTER);
        } else {
            frame.add(center,BorderLayout.CENTER);
        }
        frame.setFrameIcon(null);
        BasicInternalFrameUI ui = (BasicInternalFrameUI)frame.getUI();
        ui.getNorthPane().setComponentPopupMenu(createPopupMenu());
	}

    public void updateStatus() {
        status.setText("<html><center><b>"+getName()+"</b></center>"+
                "<table cellpadding=0 cellspacing=0 border=1>"+getStatus()+"</table>"+
                "</html>");
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu mnu = new JPopupMenu("Module");
        showTies = new JCheckBoxMenuItem("Ties");
        showTies.setSelected(true);
        showTies.addActionListener(e -> showTies());
        mnu.add(showTies);
        if (canShowStatus()) {
            showStatus = new JCheckBoxMenuItem("Status");
            showStatus.addActionListener(e -> showStatus());
            mnu.add(showStatus);
        }
        JMenuItem mini = new JMenuItem("Minimize");
        mini.addActionListener( e -> minimize());
        mnu.add(mini);
        return mnu;
    }

    protected boolean canShowStatus() {
        return false;
    }

    public Module(String name, Elem elem) { this(name,elem,LAYOUT_HORIZONTAL); }
    public Module(String name, Elem elem, boolean layout) {
        this(name,layout);
        for(IBit in : elem.inputs)
            new InPin(in instanceof InBit ? ((InBit)in).getName() : "?", in);
        for(OBit out : elem.outputs)
            new OutPin(out instanceof OutBit ? ((OutBit)out).getName() : "?", out);
        if (elem instanceof StateMachine) {
            StateMachine sm = (StateMachine) elem;
            final JLabel curState = new JLabel("-????????-",JLabel.CENTER);
            center.setLayout(new BorderLayout());
            center.add(new JLabel("-state-",JLabel.CENTER),BorderLayout.NORTH);
            center.add(curState);
            StateMachineListener lstnr = (state) -> curState.setText(state.getName());
            sm.setListener(lstnr);
            State state = sm.getCurState();
            if (state!=null)
                lstnr.onEntry(state);
        }
        addToSim(true);
    }

    protected void initOutputs() {
        for (Component comp : outputs.getComponents())
            ((OutTieBit)comp).reset();
    }

    public String getName() { return name; }

    public void setTitle(String title) { frame.setTitle(title); }

    public void minimize() {
        try {
            frame.setIcon(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public void hideTies() {
        inputs.setVisible(false);
        outputs.setVisible(false);
        showTies.setSelected(false);
    }

    private void showTies() {
        boolean show = showTies.isSelected();
        inputs.setVisible(show);
        outputs.setVisible(show);
        Simul.saveTiesVisibile(getName(),show);
        frame.pack();
    }

    public void showStatus() {
        boolean show = showStatus.isSelected();
        if (show) {
            if (!listenerAdded) {
                listenerAdded = true;
                for (Component comp : inputs.getComponents())
                    ((InTieBit) comp).addListener(new StatusCtrl());
            }
            updateStatus();
        }
        statusShowed = show;
        status.setVisible(show);
        frame.pack();
    }

    public String getStatus() {
        String txt = "";
        try {
            Class cls = getClass();
            for(Field f : cls.getDeclaredFields())
                if (Elem.class.isAssignableFrom(f.getType()))
                    txt += "<tr><td colspan=2 style='background-color:#C0C0C0;'><b><center>"+
                            f.getName()+"="+typeName(f.getType())+
                            "</center></b></td></tr>"+
                            ((Elem)f.get(this)).getStatus();

        } catch (IllegalAccessException e) { txt += "<tr><td colspan=2>Error="+e.getMessage()+"</td></tr>"; }
        return txt;
    }

    private class StatusCtrl implements BitListener {
        @Override
        public void update(Value value) {
            if (statusShowed) simul.updateStatus(Module.this);
        }
        private BitListener next;
        @Override
        public BitListener getNext() { return next; }
        @Override
        public void setNext(BitListener next) { this.next = next; }
    }
    private boolean statusShowed;
    private boolean listenerAdded;

    protected String typeName(Class<?> type) {
        if (StateMachine.class.isAssignableFrom(type))
            return "StateMachine";
        return type.getSimpleName();
    }

    public void addToSim() { addToSim(true); }
    protected void addToSim(boolean visible) {
        simul.addModule(this,frame,visible);
    }

    protected void doActionLater(Runnable action) { simul.doAction(action); }

    private static Simul.Mod simul;
    public static void setSimul(Simul.Mod s) { simul=s; }
}
