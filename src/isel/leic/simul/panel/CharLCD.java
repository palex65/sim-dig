package isel.leic.simul.panel;

import java.awt.*;

public class CharLCD extends Canvas {
    private static final long X = 0x15_0A_15_0A_15_0A_15_0AL;
    private static final long S = 0x00_00_00_00_00_00_00_00L;

    private static long[] cgRAM = {
            S,S,S,S,S,S,S,S,S,S,S,S,S,S,S,S, // CGRAM  [0x00 - 0x0F]
            S,S,S,S,S,S,S,S,S,S,S,S,S,S,S,S,

            0x00_00_00_00_00_00_00_00L, // Space   [0x20 - 32]
            0x04_04_04_04_00_00_04_00L, // !
            0x0A_0A_0A_00_00_00_00_00L, // "
            0x0A_0A_1F_0A_1F_0A_0A_00L, // #
            0x04_0F_14_0E_05_1E_04_00L, // $
            0x18_19_02_04_08_13_03_00L, // %
            0x0C_12_14_08_15_12_0D_00L, // &
            0x0C_04_08_00_00_00_00_00L, // ´
            0x02_04_08_08_08_04_02_00L, // (
            0x08_04_02_02_02_04_08_00L, // )
            0x00_04_15_0E_15_04_00_00L, // *
            0x00_04_04_1F_04_04_00_00L, // +
            0x00_00_00_00_0C_04_08_00L, // ,
            0x00_00_00_1F_00_00_00_00L, // -
            0x00_00_00_00_00_0C_0C_00L, // .
            0x00_01_02_04_08_10_00_00L, // /

            0x0E_11_13_15_19_11_0E_00L, // 0  [0x30 - 48]
            0x04_0C_04_04_04_04_0E_00L, // 1
            0x0E_11_01_02_04_08_1F_00L, // 2
            0x1F_02_04_02_01_11_0E_00L, // 3
            0x02_06_0A_12_1F_02_02_00L, // 4
            0x1F_10_1E_01_01_11_0E_00L, // 5
            0x06_08_10_1E_11_11_0E_00L, // 6
            0x1F_01_02_04_08_08_08_00L, // 7
            0x0E_11_11_0E_11_11_0E_00L, // 8
            0x0E_11_11_0F_01_02_0C_00L, // 9
            0x00_0C_0C_00_0C_0C_00_00L, // :
            0x00_0C_0C_00_0C_04_08_00L, // ;
            0x02_04_08_10_08_04_02_00L, // <
            0x00_00_1F_00_1F_00_00_00L, // =
            0x08_04_02_01_02_04_08_00L, // >
            0x0E_11_01_02_04_00_04_00L, // ?

            0x0E_11_01_0D_15_15_0E_00L, // @  [0x40 - 64]
            0x0E_11_11_11_1F_11_11_00L, // A
            0x1E_11_11_1E_11_11_1E_00L, // B
            0x0E_11_10_10_10_11_0E_00L, // C
            0x1C_12_11_11_11_12_1C_00L, // D
            0x1F_10_10_1E_10_10_1F_00L, // E
            0x1F_10_10_1E_10_10_10_00L, // F
            0x0E_11_10_17_11_11_0F_00L, // G
            0x11_11_11_1F_11_11_11_00L, // H
            0x0E_04_04_04_04_04_0E_00L, // I
            0x07_02_02_02_02_12_0C_00L, // J
            0x11_12_14_18_14_12_11_00L, // K
            0x10_10_10_10_10_10_1F_00L, // L
            0x11_1B_15_15_11_11_11_00L, // M
            0x11_11_19_15_13_11_11_00L, // N
            0x0E_11_11_11_11_11_0E_00L, // O

            0x1E_11_11_1E_10_10_10_00L, // P  [0x50 - 80]
            0x0E_11_11_11_15_12_0D_00L, // Q
            0x1E_11_11_1E_14_12_11_00L, // R
            0x0F_10_10_0E_01_01_1E_00L, // S
            0x1F_04_04_04_04_04_04_00L, // T
            0x11_11_11_11_11_11_0E_00L, // U
            0x11_11_11_11_11_0A_04_00L, // V
            0x11_11_11_15_15_15_0A_00L, // W
            0x11_11_0A_04_0A_11_11_00L, // X
            0x11_11_11_0A_04_04_04_00L, // Y
            0x1F_01_02_04_08_10_1F_00L, // Z
            0x1C_10_10_10_10_10_1C_00L, // [
            0x00_10_08_04_02_01_00_00L, // \
            0x0E_02_02_02_02_02_0E_00L, // ]
            0x04_0A_11_00_00_00_00_00L, // ^
            0x00_00_00_00_00_00_1F_00L, // _

            0x08_04_02_00_00_00_00_00L, // `  [0x60 - 96]
            0x00_00_0E_01_0F_11_0F_00L, // a
            0x10_10_16_19_11_11_1E_00L, // b
            0x00_00_0E_10_10_10_0E_00L, // c
            0x01_01_0D_13_11_11_0F_00L, // d
            0x00_00_0E_11_1F_10_0E_00L, // e
            0x06_09_08_1C_08_08_08_00L, // f
            0x00_00_0F_11_11_0F_01_0EL, // g
            0x10_10_16_19_11_11_11_00L, // h
            0x04_00_0C_04_04_04_0E_00L, // i
            0x02_00_06_02_02_02_12_0CL, // j
            0x10_10_12_14_18_14_12_00L, // k
            0x0C_04_04_04_04_04_0E_00L, // l
            0x00_00_1A_15_15_11_11_00L, // m
            0x00_00_16_19_11_11_11_00L, // n
            0x00_00_0E_11_11_11_0E_00L, // o

            0x00_00_1E_11_11_1E_10_10L, // p  [0x70 - 112]
            0x00_00_0D_13_11_0F_01_01L, // q
            0x00_00_16_19_10_10_10_00L, // r
            0x00_00_0E_10_0E_01_1E_00L, // s
            0x08_08_1C_08_08_09_06_00L, // t
            0x00_00_11_11_11_13_0D_00L, // u
            0x00_00_11_11_11_0A_04_00L, // v
            0x00_00_11_11_15_15_0A_00L, // w
            0x00_00_11_0A_04_0A_11_00L, // x
            0x00_00_11_11_09_07_01_0EL, // y
            0x00_00_1F_02_04_08_1F_00L, // z
            0x02_04_04_08_04_04_02_00L, // {
            0x04_04_04_04_04_04_04_00L, // |
            0x08_04_04_02_04_04_08_00L, // }
            0x00_04_02_1F_02_04_00_00L, // ->
            0x00_04_08_1F_08_04_00_00L, // <-

            X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X, // [0x80 - 128]

            X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X, // [0x90 - 144]

            X,X,X,X,X,X,X,X,X,X,X,X,         // [0xA0 - 160]
            0x06_09_08_1E_08_09_06_00L,      // € [0xAC - 172]
            X,X,X,

            X,X,X,X,X,X,X,X,X,X,X,X,X,X,X,X, // [0xB0 - 176]

            0x08_04_0E_11_1F_11_11_00L, // À    [0xC0 - ]
            0x02_04_0E_11_1F_11_11_00L, // Á
            0x04_0A_0E_11_1F_11_11_00L, // Â
            0x0D_12_0E_11_1F_11_11_00L, // Ã
            X,X,X,
            0x0E_11_10_10_11_0E_04_0CL, // Ç
            0x08_04_1F_10_1E_10_1F_00L, // È
            0x02_04_1F_10_1E_10_1F_00L, // É
            0x04_0A_1F_10_1E_10_1F_00L, // Ê
            X,
            0x08_04_0E_04_04_04_0E_00L, // Ì
            0x02_04_0E_04_04_04_0E_00L, // Í
            X,X,

            X,X,                        //      [0xD0 - ]
            0x08_04_0E_11_11_11_0E_00L, // Ò
            0x02_04_0E_11_11_11_0E_00L, // Ó
            0x04_0A_0E_11_11_11_0E_00L, // Ô
            0x0D_12_0E_11_11_11_0E_00L, // Õ
            X,X,X,
            0x08_04_11_11_11_11_0E_00L, // Ù
            0x02_04_11_11_11_11_0E_00L, // Ú
            X,X,X,X,X,

            0x08_04_0E_01_0F_11_0F_00L, // à    [0xE0 - ]
            0x02_04_0E_01_0F_11_0F_00L, // á
            0x04_0A_0E_01_0F_11_0F_00L, // â
            0x0D_12_0E_01_0F_11_0F_00L, // ã
            X,X,X,
            0x00_00_0E_10_10_0E_04_0CL, // ç
            0x08_04_0E_11_1F_10_0E_00L, // è
            0x02_04_0E_11_1F_10_0E_00L, // é
            0x04_0A_0E_11_1F_10_0E_00L, // ê
            X,
            0x08_04_00_0C_04_04_0E_00L, // i
            0x02_04_00_0C_04_04_0E_00L, // i
            X,X,

            X,X,                        //      [0xF0 - ]
            0x08_04_0E_11_11_11_0E_00L, // Ò
            0x02_04_0E_11_11_11_0E_00L, // Ó
            0x04_0A_0E_11_11_11_0E_00L, // Ô
            0x0D_12_0E_11_11_11_0E_00L, // Õ
            X,X,X,
            0x08_04_11_11_11_13_0D_00L, // ù
            0x02_04_11_11_11_13_0D_00L, // ú
            X,X,X,X,
            0xFF_FF_FF_FF_FF_FF_FF_FFL  // Block
    };

    private char c = ' ';
    private boolean cursor;

    private static final int LINES =8, COLS =5, FACTOR_SEP=4;

    public void paint(Graphics g) {
        super.paint(g);
        float fW = getWidth() / (float)(COLS * (FACTOR_SEP + 1));
        int blW = (int) (fW * FACTOR_SEP);
        int sepW = (int) fW;
        float fH = getHeight() / (float)(LINES * (FACTOR_SEP + 1));
        int blH = (int) (fH * FACTOR_SEP);
        int sepH = (int) fH;
        long bits = cgRAM[c] | (cursor? 0x1F : 0);
        long mask = 0x10_00_00_00_00_00_00_00L;
        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLS; c++) {
                if ((bits & mask) != 0L)
                    g.setColor(Color.BLACK);
                else
                    g.setColor(background);
                g.fillRect(sepW+c*(sepW+blW),sepH+l*(sepH+blH),blW,blH);
                mask >>= 1;
            }
            mask >>= 3;
        }
    }
    public void setChar(char c) {
        if (c<0 || c>=0x80 && c!=0xFF) {
            System.out.println("invalid char = "+c+" ("+(int)c+")");
            c='?';
        }
        if(cgRAM[c]==X)
            System.out.println("undefined char = "+c+" ("+(int)c+")");
        if (this.c==c) return;
        this.c = c;
        repaint(10);
    }

    private static Color background = new Color(230,230,230);
    public void setCursor() { cursor=true; repaint(); }
    public void resetCursor() { cursor=false; repaint(); }

    private Dimension dim;
    @Override
    public Dimension getPreferredSize() {
        if (dim==null) {
            dim = new Dimension(COLS*(FACTOR_SEP+1)+1,LINES*(FACTOR_SEP+1)+1);
        }
        return dim;
    }

    public static void setPattern(int adrCG, byte line) {
        char c = (char) (adrCG / 8);
        int shift = 64 - (adrCG % 8 +1) * 8;
        long pattern = cgRAM[c] & ~(0xFFL<<shift) | (((long)line)<<shift);
        cgRAM[c+8] = cgRAM[c] = pattern;
    }
}
