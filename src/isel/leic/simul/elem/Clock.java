package isel.leic.simul.elem;

import isel.leic.simul.bit.InBit;
import isel.leic.simul.bit.OutBit;

public class Clock extends Elem {
    public final OutBit out = new Output("out");
    private volatile Thread thr = null;

    public Clock(InBit output) {
        out.connect(output);
    }
    public Clock() { }

    private class TicTac extends Thread {
        private long millis;
        private int nanos;

        private TicTac(long millis, int nanos) {
            super("Clock "+millis+"ms");
            this.millis = millis;
            this.nanos = nanos;
        }
        @Override
        public void run() {
            while(thr!=null) {
                try {
                    Thread.sleep(millis / 2, nanos/2);
                    doAction(()-> out.write(out.get().not()));
                }
                catch (InterruptedException ignored) { }
            }
        }
    }
    public void start(long periodInMillis) { start(periodInMillis,0); }
    public void start(long periodInMillis, int periodInNanos) {
        if (thr!=null)
            throw new java.lang.IllegalStateException("Clock should be stopped");
        out.reset();
        thr = new TicTac(periodInMillis, periodInNanos);
        thr.start();
    }
    public void stop() {
        Thread t = thr;
        thr = null;
        t.interrupt();
    }
    protected void onInputChanged(InBit bit) {}
}
