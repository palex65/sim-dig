package isel.leic.simul;

import isel.leic.simul.dat.*;
import isel.leic.simul.module.Module;
import isel.leic.simul.parser.SyntaxError;
import isel.leic.utils.Time;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * @author    palex
 */
@SuppressWarnings("serial")
public class UsbPort extends Module {
    private static final boolean LAYOUT = LAYOUT_HORIZONTAL;
    private static final int INPUT_BITS_LENGTH = 8, OUTPUT_BITS_LENGTH = 8;
    private static final int INPUT_MASK = Dat.maskOf(INPUT_BITS_LENGTH);
    private static final int OUTPUT_MASK = Dat.maskOf(OUTPUT_BITS_LENGTH);

	public final OutTieDat outPort = new OutTieDat("O",OUTPUT_BITS_LENGTH,LAYOUT);
	public final InTieDat inPort = new InTieDat("I",INPUT_BITS_LENGTH,LAYOUT);

    private static UsbPort instance;
	
	public UsbPort(String name) {
		super(name,LAYOUT);
        instance = this;
        inputs.addBits(inPort);
        outputs.addBits(outPort);
        addToSim(true);
		outPort.write(0);
	}

    private static boolean started = false;

    private static Simul.App simul;

	public static void start(Runnable initHardwareSimul) {
		if (started) return;
		started =true;
        simul = Simul.start(() -> {
            new UsbPort("kit").setTitle("USB_PORT");
            if (initHardwareSimul!=null)
                initHardwareSimul.run();
        });
 	}

    private static long lastAction;

    public static int in() {
        if (!started) init();
        if (simul!=null) {
            simul.waitAction(lastAction);
            int val = instance.inPort.read();
            if (val==Dat.OPEN) val=INPUT_MASK;
            return ~val;
        }
        return isel.leic.usbio.UsbPort.in();
	}

	//private static final int MIN_TIME_OUTPUT = 3; // 7 miliseconds.
	public static void out(int val) {
        if (!started) init();
        if (simul!=null) {
            //long ts = System.currentTimeMillis();
            lastAction = simul.doAction(() -> instance.outPort.write(~val & OUTPUT_MASK));
            //long te = System.currentTimeMillis();
            //if (te-ts < MIN_TIME_OUTPUT)
            //    Time.sleep(MIN_TIME_OUTPUT-(te-ts));
        }
        else
            isel.leic.usbio.UsbPort.out(val);
    }

    public static void start(Properties props) {
        // starts java simulation
        String simulFile = props.getProperty("file", "Hardware.simul");
        simul = Simul.start(simulFile);
        if (instance == null) {
            String msg = "UsbPort module not used in simul file (" + simulFile + ")";
            System.out.println("ERROR: " + msg);
            throw new SyntaxError(msg);
        }
    }

    private static void init() {
        if (loadProps() && props.getProperty("simul","false").equals("true")) {
            String simulFile = props.getProperty("file", "Hardware.simul");
            simul = Simul.start(simulFile);
            if (instance==null) {
                String msg = "UsbPort module not used in simul file ("+simulFile+")";
                System.out.println("ERROR: "+msg);
                throw new SyntaxError(msg);
            }
        }
        started = true;
    }

    private static final String PROPS_FILE = "USB_PORT.properties";
    private static Properties props = new Properties();

    private static boolean loadProps() {
        try (InputStream in = new FileInputStream(PROPS_FILE)){
            props.load(in);
        } catch (IOException _e) {
            System.out.println("A gerar valores por omissão para "+PROPS_FILE);
            try {
                InputStream source = UsbPort.class.getResourceAsStream(PROPS_FILE);
                Files.copy(source, Paths.get(PROPS_FILE), StandardCopyOption.REPLACE_EXISTING);
                loadProps();
            } catch (Exception e) {
                System.out.println("Erro ao obter "+PROPS_FILE+".");
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        int mask = 0x01;
        while( (UsbPort.in()&0x01) == 0 ) {
            UsbPort.out(mask);
            Time.sleep(500);
            mask <<= 1;
            if (mask > 0xFF) mask = 0x01;
        }
        System.exit(0);
    }
}
