package isel.leic.simul.module;

import isel.leic.simul.Simul;
import isel.leic.simul.dat.InTieDat;
import isel.leic.simul.dat.OutTieDat;
import isel.leic.simul.elem.Counter;
import isel.leic.simul.elem.*;
import isel.leic.simul.state.*;

public class KeyDecode extends KeyModule {
    private class Ctrl extends StateMachine {
        Input   Kpress = new Input(), Kack = new Input();
        Output  Kscan = new Output(), Kval = new Output();
        {
            state("SCAN")
                    .output(Kscan)
                    .transitNextWhen(Kpress::isTrue);
            state("WaitAck")
                    .output(Kval)
                    .transitNextWhen(Kack::isTrue);
            state("WaitAll")
                    .transitNextWhen(() -> Kack.isFalse() && Kpress.isFalse());
            start();
        }
    };
    private final Ctrl ctrl = new Ctrl();

    private final Buffer buffer = new Buffer(4);
    private final Counter counter = new Counter(buffer.in);
    private final Decoder decoder = new Decoder(4);
    private final Mux mux = new Mux(4,ctrl.Kpress);
    private final Clock clock = new Clock(counter.up);

    public final InTieDat lines = new InBlock("L",mux.in);
    public final OutTieDat cols = new OutBlock("C",decoder.out);

    public KeyDecode(String name) {
		super(name);
        Kack = new InPin("ack", ctrl.Kack );
        Koe = new InPin("oe", buffer.oe);
        Kval = new OutPin("val", ctrl.Kval);
        Kdat = new OutBlock("K",buffer.out);
        addToSim(VISIBLE);

        decoder.out.setInvert(true);
        mux.out.setInvert(true);
        decoder.sel.connect(counter.out, 0,1);
        mux.sel.connect(counter.out,2,3);
        counter.ce.connect(ctrl.Kscan);

        clock.start(25);
	}

    public static void main(String[] args) throws InterruptedException {
        Simul.App simul = Simul.start( () -> {
            KeyMatrix k = new KeyMatrix("KeyMatrix","0123456789ABCDEF", 4, 4);
            KeyDecode kd = new KeyDecode("KeyDecode");
            kd.cols.link(k.cols);
            kd.lines.link(k.lines);
        } );
    }
};

