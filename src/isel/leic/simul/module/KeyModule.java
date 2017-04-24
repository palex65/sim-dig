package isel.leic.simul.module;

import isel.leic.simul.bit.InTieBit;
import isel.leic.simul.bit.OutTieBit;
import isel.leic.simul.dat.OutTieDat;

public class KeyModule extends Module {
    public InTieBit Kack;
    public InTieBit Koe;
    public OutTieBit Kval;
    public OutTieDat Kdat;

    public KeyModule(String name, boolean layout) { super(name, layout); }
    public KeyModule(String name) { super(name); }
}
