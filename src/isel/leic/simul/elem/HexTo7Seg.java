package isel.leic.simul.elem;

import isel.leic.simul.bit.*;
import isel.leic.simul.dat.*;

public class HexTo7Seg extends Elem {
    public InDat in = new InputDat("D",4);
    public OutDat seg = new OutputDat("S",7);
    byte[] table = {
            0b1111110, // 0
            0b0110000, // 1
            0b1101101, // 2
            0b1111001, // 3
            0b0110011, // 4
            0b1011011, // 5
            0b1011111, // 6
            0b1110000, // 7
            0b1111111, // 8
            0b1111011, // 9
            0b1110111, // A
            0b0011111, // b
            0b1001110, // C
            0b0111101, // d
            0b1001111, // E
            0b1000111, // F
    };

    public HexTo7Seg() {
        initOutputs();
    }

    @Override
    protected void onInputChanged(InBit bit) {
        byte segs = table[in.read()];
        int i=0;
        for(byte m = 0b1000000 ; m!=0 ; m>>=1)
            seg.bitPos(i++).write(Value.valueOf((m&segs)!=0));
    }
}
