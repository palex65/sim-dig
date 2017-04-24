package isel.leic.simul.bit;


public interface IBit extends Bit, BitListener {
    void connect(OBit ob);
}
