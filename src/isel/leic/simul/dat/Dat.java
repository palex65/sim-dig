package isel.leic.simul.dat;

import isel.leic.simul.bit.Bit;

public interface Dat {
    int OPEN = 0x3F000000;
    int read();
    void write(int val);
    void setInvert(boolean invert);
    Bit bitPos(int idx);
    Bit bitMask(int mask);
    int length();

    static int toLowPos(int mask) { return toPos(mask); }

    static int toHiPos(int mask) {
        int pos;
        for(pos=0 ; (mask&1)==0 ; ++pos) mask>>=1;
        for( ; (mask&1)==1 ; ++pos) mask>>=1;
        return pos-1;
    }

    static int nBits(int val) {
        if (val==0) return 1;
        int bits=1;
        for( val>>=1 ; val!=0 ; val>>>=1 ) ++bits;
        return bits;
    }

    static int toPos(int mask) {
        int pos;
        for(pos=0 ; (mask&1)==0 ; ++pos) mask>>=1;
        return pos;
    }

    static int selOf(int n) {
        int sel=1;
        for(int r=2 ; r<n ; r<<=1 )  ++sel;
        return sel;
    }

    static int maskOf(int n) {
        return ~ (-1 << n);
    }
}
