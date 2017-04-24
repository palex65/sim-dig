package isel.leic.simul.elem;


import isel.leic.simul.bit.*;

public class Link {
    public final OutBit out = new OutBit();
    public final InBit in = new InBit((bit -> out.write( bit.get() )));

    public Link() { }
    public Link(IBit... outs) {
        out.connect(outs);
    }
}
