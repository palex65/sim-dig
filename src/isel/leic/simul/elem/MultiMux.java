package isel.leic.simul.elem;

import isel.leic.simul.bit.InBit;
import isel.leic.simul.dat.Dat;
import isel.leic.simul.dat.InDat;
import isel.leic.simul.dat.OutDat;

public class MultiMux extends Elem {
    public final InDat[] in;
    public final InDat sel;
    public final OutDat out;

    public MultiMux(int channels, int bits) {
        in = new InDat[channels];
        for (int i = 0; i < channels; i++)
            in[i] = new InputDat("in"+i+'.',bits);
        out = new OutputDat("out",bits);
        sel = new InputDat("sel",Dat.selOf(channels));
    }
    public MultiMux(int channels, int bits, InDat output) {
        this(channels,bits);
        out.connect(output);
    }

    protected void onInputChanged(InBit bit) {
        int select = sel.read();
        if (select==Dat.OPEN || select>=in.length) return;
        out.write( in[ sel.read() ].read() );
    }

    @Override
    public String toString() {
        String res = "";
        for(int i=0 ; i<in.length ; ++i)
            res +="Mux["+i+"]("+in[i]+") ";
        return res+"sel("+sel+") out("+out+")";
    }
}
