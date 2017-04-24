package isel.leic.simul;

import isel.leic.simul.bit.*;
import isel.leic.simul.elem.Elem;
import isel.leic.simul.module.Module;
import isel.leic.simul.parser.*;
import isel.leic.simul.state.StateMachine;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.util.*;

public class Simul {
    private static final String PROPS_FILE = "Simul.properties";
    private static final String SIMULATOR_TITLE = "SimDig";
    private static JFrame frame;
    private JDesktopPane desktop;
    private Properties props = new Properties();

    private static Simul instance;
    private static Thread simulThr;

    private Simul() {
        instance = this;
        frame = new JFrame(SIMULATOR_TITLE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        loadProps();
        Point orig = getLastLocation(SIMULATOR_TITLE);
        Point size = getLastSize(SIMULATOR_TITLE);
        if (size==null) {
            int inset = 150;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
        } else {
            frame.setLocation(orig);
            frame.setSize(size.x,size.y);
        }

        desktop = new JDesktopPane();
        //desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        frame.setContentPane(desktop);
        frame.setVisible(true);
        desktop.setBackground(Color.LIGHT_GRAY);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveLocation(SIMULATOR_TITLE,frame.getX(),frame.getY());
                saveSize(SIMULATOR_TITLE,frame.getWidth(),frame.getHeight());
                super.windowClosing(e);
                saveProps();
            }
        });
    }

    private void saveProps() {
        try (OutputStream out = new FileOutputStream(PROPS_FILE)) {
            props.store(out,null);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadProps() {
        try (InputStream in  = new FileInputStream(PROPS_FILE)){
            props.load(in);
        } catch (IOException _e) {
            System.out.println("A gerar valores por omissão para "+PROPS_FILE);
            try {
                InputStream source = Simul.class.getResourceAsStream(PROPS_FILE);
                Files.copy(source,Paths.get(PROPS_FILE), StandardCopyOption.REPLACE_EXISTING);
                loadProps();
            } catch (Exception e) {
                System.out.println("Erro ao obter "+PROPS_FILE+" da classe Simul");
            }
        }
    }

    private Point getLastLocation(String moduleName) {
        String[] args = props.getProperty(moduleName, "0,0").split(",");
        return new Point(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
    }

    private Point getLastSize(String moduleName) {
        String value =  props.getProperty(moduleName+"_SIZE", null);
        if (value==null) return null;
        String[] args = value.split(",");
        return new Point(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
    }

    private boolean isIconified(String moduleName) {
        String value = props.getProperty(moduleName + "_ICON", null);
        return value != null && value.equalsIgnoreCase("true");
    }

    private boolean isTiesVisible(String moduleName) {
        String value = props.getProperty(moduleName + "_TIES", null);
        return value==null || value.equalsIgnoreCase("true");
    }

    public static App start(Runnable init) {
        if (simulThr!=null)
            throw new IllegalStateException("ERROR: Simul already Started.");
        try {
            simulThr = new Thread(Simul::loop,"SIMUL");
            simulThr.setUncaughtExceptionHandler(
                    (t, e) -> System.out.println("Thread="+t.getName()+" Exception="+e.getMessage())
            );
            SwingUtilities.invokeAndWait( () -> {
                JFrame.setDefaultLookAndFeelDecorated(true);
                try {
                    //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                    //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                    //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new Simul();
            });
            if (init!=null)
                Simul.actionLater(init);
            simulThr.start();
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }
        app.waitAction( app.doAction(null));
        return app;
    }

    private static Map<String,Module> modules = new HashMap<>();

    public static App start(String initFileName) {
        try (Parser simulParser = new MainParser(initFileName,app)) {
            return start(simulParser::parse);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static App start(Runnable init, String initFileName) {
        try (Parser simulParser = new MainParser(initFileName,app)) {
            return start(()-> { init.run(); simulParser.parse(); } );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void saveLocation(String moduleName, int x, int y) {
        //System.out.println(moduleName+": x="+x+" y="+y);
        instance.props.setProperty(moduleName,x+","+y);
    }
    private static void saveSize(String moduleName, int width, int height) {
        //System.out.println(moduleName+" SIZE : width="+width+" height="+height);
        instance.props.setProperty(moduleName+"_SIZE",width+","+height);
    }
    private static void saveIconified(String moduleName, boolean is) {
        //System.out.println(moduleName+" ICON : "+is);
        instance.props.setProperty(moduleName+"_ICON",""+is);
    }
    public static void saveTiesVisibile(String moduleName, boolean is) {
        //System.out.println(moduleName+" ICON : "+is);
        instance.props.setProperty(moduleName+"_TIES",""+is);
    }


    //region App Interface
    public interface App {
        /**
         * Schedule an external action
         * @param action to shedule
         * @return the number of the scheduled action
         */
        long doAction(Runnable action);

        /**
         * Wait for finishing the identified external action
         * @param actionNumber identifies the action to wait for
         */
        void waitAction(long actionNumber);

        /**
         * Ends execution of the simulator thread
         * and dispose the simulator window
         */
        void dispose();

        Module findModule(String name);

        void addModule(String name, Module module);
    };

    private static final App app = new App() {
        public long doAction(Runnable action) { return externalAction(action); }
        public void waitAction(long actionNumber) { waitExternalAction(actionNumber); }
        public void dispose() { Simul.exit(); }
        public Module findModule(String name) { return modules.get(name); }
        public void addModule(String name, Module module) { modules.put(name,module); }
    };

    private static long actionNumber = 0;
    private static volatile long lastActionTerminated = -1;

    private static class ActionLater extends ActionNode {
        Runnable action;
        ActionLater(Runnable action) {
            this.action = action;
        }
        @Override
        void doNow() {
            action.run();
        }
    }

    private static long externalAction(final Runnable exec) {
        externalActions.addNode(new ActionLater(new Runnable() {
            long number = actionNumber;
            @Override
            public void run() {
                if (exec!=null)
                    exec.run();
                doAll();
                synchronized (app) {
                    lastActionTerminated = number;
                    app.notifyAll();
                }
            }
        }));
        if (externalActions.size()>1000) {
            stateTitle("(TOO MANY ACTIONS TO PROCESS)", true);
            throw new RuntimeException("TOO MANY ACTIONS TO PROCESS");
        }
        return actionNumber++;
    }

    private static void waitExternalAction(long number) {
        synchronized (app) {
            while (lastActionTerminated<number)
                try {
                    app.wait(1000);
                } catch (InterruptedException ignored) { }
        }
    }

    private static void exit() {
        simulThr = null;
        synchronized (logicActions) {
            logicActions.notify();
        }
        frame.dispose();
    }

    //endregion

    //region State Interface
    public interface State {
        void step(StateMachine sm);
    }
    private static final State state = Simul::stepMachineAction;

    private static void stepMachineAction(StateMachine sm) {
        synchronized (logicActions) {
            if (!stateMachineActions.contains(ActionStep.class, sm)) {
                stateMachineActions.addNode(new ActionStep(sm));
                logicActions.notify();
            }
        }
    }

    private static class ActionStep extends ActionNode {
        StateMachine sm;
        ActionStep(StateMachine sm) {
            this.sm = sm;
        }
        public void setMachine(StateMachine sm) {
            this.sm = sm;
        }
        void doNow() {
            sm.step();
        }
        protected boolean isEqual(Object obj) {
            return obj == sm;
        }
    }
    //endregion

    //region Bit Interface
    public interface Bit {
        void doAll();
        void doActionDelayed(Runnable action, int delay);
        void doWrite(IBit bit, Value val);
    }
    private static final Bit bit = new Bit() {
        public void doAll() { Simul.doAll(); }
        public void doActionDelayed(Runnable action, int factor) { logicActions.addNode(new ActionDelayed(action,factor)); }
        public void doWrite(IBit bit, Value val) { logicActions.addNode(new WriteLater(bit,val)); }
    };
    private static boolean inDoAll = false;
    private static void doAll() {
        if (inDoAll) return;
        inDoAll = true;
        while (!logicActions.isEmpty() || !stateMachineActions.isEmpty()) {
            ActionNode node = logicActions.removeFirstNode();
            if (node==null)
                node = stateMachineActions.removeFirstNode();
            processNode(node);
        }
        nodesProcessed = 0;
        inDoAll = false;
    }
    private static class ActionDelayed extends ActionNode {
        Runnable action;
        int delay;
        ActionDelayed(Runnable action, int delay) {
            this.action = action;
            this.delay = delay;
        }
        @Override
        void doNow() {
            if (delay<=0) action.run();
            else {
                --delay;
                logicActions.addNode(this);
                --nodesProcessed;
            }
        }
    }
    private static class WriteLater extends ActionNode {
        IBit bit;
        Value value;
        WriteLater(IBit bit, Value value) {
            this.bit = bit;
            this.value = value;
        }
        @Override
        void doNow() {
            bit.write(value);
        }
    }
    //endregion

    // region Module Interface
    public interface Mod {
        void addModule(Module module, Module.Frame frame, boolean visible);
        void doAction(Runnable action);
        void updateStatus(Module mod);
    }
    private static Mod mod = new Mod() {
        public void addModule(Module module, Module.Frame frame, boolean visible) { Simul.addModule(module,frame,visible); }
        public void doAction(Runnable action) { externalAction(action); }
        public void updateStatus(Module mod) { addActionUpdate(mod); }
    };

    private static InternalFrameListener frameListener = new InternalFrameAdapter() {
        public void internalFrameIconified(InternalFrameEvent e) {
            Module m = ((Module.Frame) e.getInternalFrame()).getModule();
            saveIconified(m.getName(),true);
        }
        public void internalFrameDeiconified(InternalFrameEvent e) {
            Module m = ((Module.Frame) e.getInternalFrame()).getModule();
            saveIconified(m.getName(),false);
        }
    };
    private static ComponentListener componentListener = new ComponentAdapter() {
        public void componentResized(ComponentEvent e) {
            Module.Frame f = (Module.Frame) e.getComponent();
            Module m = f.getModule();
            Simul.saveSize(m.getName(),f.getWidth(),f.getHeight());
        }
        public void componentMoved(ComponentEvent e) {
            Module.Frame f = (Module.Frame) e.getComponent();
            Module m = f.getModule();
            Simul.saveLocation(m.getName(),f.getX(), f.getY());
        }
    };

    private static void addModule(Module module, Module.Frame frame, boolean visible) {
        instance.desktop.add(frame);
        String name = module.getName();
        frame.setLocation( instance.getLastLocation(name));
        Point size = instance.getLastSize(name);
        if (size==null)
            frame.pack();
        else
            frame.setSize(size.x,size.y);
        if (instance.isIconified(name))
            module.minimize();
        if (!instance.isTiesVisible(name)) {
            module.hideTies();
        }
        frame.setVisible(visible);
        //instance.frames.addLast(mod);
        frame.addInternalFrameListener(frameListener);
        frame.addComponentListener(componentListener);
        modules.put(name,module);
    }

    private static class ActionStatus extends ActionNode {
        Module m;
        ActionStatus(Module m) {
            this.m = m;
        }
        void doNow() {
            m.updateStatus();
        }
        protected boolean isEqual(Object obj) {
            return obj == m;
        }
    }

    private static void addActionUpdate(Module mod) {
        synchronized (logicActions) {
            if (!externalActions.contains(ActionStatus.class, mod)) {
                externalActions.addNode(new ActionStatus(mod));
                logicActions.notify();
            }
        }
    }

    //endregion

    //region UI Interface
    public interface UI {
        void doAction(Runnable action);
    }
    private static final UI ui = app::doAction;

    //endregion

    private static void actionLater(Runnable action) {
        logicActions.addNode(new ActionLater(action));
    }

    static final ActionQueue logicActions = new ActionQueue();
    private static final ActionQueue stateMachineActions = new ActionQueue();
    private static final ActionQueue externalActions = new ActionQueue();

    private static long limitNodes = 1;
    private static final long MAX_NODES_PROCESSED = 1000;
    private static long nodesProcessed = 0;

    private static void loop() {
        Module.setSimul(mod);
        AbstractBit.setSimul(bit);
        Tie.setSimul(ui);
        Elem.setSimul(ui);
        StateMachine.setSimul(state);

        ActionNode node;
        while(simulThr!=null) {
            node = getNode();
            if (node!=null)
                processNode(node);
        }
        //System.exit(0);
    }

    private static void processNode(ActionNode node) {
        if (nodesProcessed >= MAX_NODES_PROCESSED) {
            if (nodesProcessed == MAX_NODES_PROCESSED) {
                stateTitle("(UNSTABLE)", true);
                ++nodesProcessed;
            }
        } else {
            ++nodesProcessed;
            node.doNow();
            if (nodesProcessed > limitNodes) {
                limitNodes = nodesProcessed;
                stateTitle("(" + limitNodes + ")", false);
            }
        }
    }

    private static ActionNode getNode() {
        ActionNode node;
        synchronized (logicActions) {
            while(simulThr!=null &&
                    logicActions.isEmpty() && stateMachineActions.isEmpty() && externalActions.isEmpty()) {
                try {
                    logicActions.wait(1000);
                } catch (InterruptedException ignored) { }
                nodesProcessed = 0;
            }
            node = logicActions.removeFirstNode();
            if (node==null)
                node = stateMachineActions.removeFirstNode();
            if (node==null)
                node = externalActions.removeFirstNode();
        }
        return node;
    }

    private static void stateTitle(String state, boolean alert) {
        SwingUtilities.invokeLater(() -> {
            frame.setTitle(SIMULATOR_TITLE + " "+state);
            if (alert)
                instance.desktop.setBackground(Color.RED);
        });
    }

    public static void main(String[] args) {
        try {
            Parser parser = new MainParser(args[0], app);
            start(parser::parse);
        } catch (Exception e) {
            System.out.println("Use: java -jar SimDig.jar <hardware_file>");
        }
    }
}
