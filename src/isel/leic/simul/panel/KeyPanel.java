package isel.leic.simul.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class KeyPanel extends JPanel {
    static final String FONT_NAME = "Arial Unicode";
    static final int FONT_SIZE = 20;
    static final int KEY_DIM = 50;
    static private Font font;
    static Dimension keyDim = new Dimension(KEY_DIM, KEY_DIM);

    private final KeyListener listener;
    private KeyButton[][] grid;
    static HashMap<Character, String> map = new HashMap<>(4);
    static {
        map.put('^', "\u25B2");
        map.put('v', "\u25BC");
        map.put('.', "OK");
    }

    private static class CharParser {
        String chars;
        int i = 0;
        CharParser(String keys) { chars = keys; }
        char get() { return i<chars.length() ? chars.charAt(i++) : 0; }
        char next() {
            char c = get();
            if (c=='\\') {
                c = get();
                if (c=='u') {
                    c = 0;
                    for (int j = 0; j < 4; j++) {
                        char digit = get();
                        if (digit==0) return 0;
                        if (digit>='a' && digit<='z') digit += 'A'-'a';
                        c = (char) (c*16 + digit -((digit>='A')?('A'-10):'0'));
                    }
                }
            }
            return c;
        }
    }

    public KeyPanel(int lines, int cols, String keys, KeyListener listener) {
        this.listener = listener;
        grid = new KeyButton[lines][cols];
        setLayout(new GridLayout(lines, cols));
        setPreferredSize(new Dimension(cols * KeyPanel.KEY_DIM, lines * KeyPanel.KEY_DIM));
        setMinimumSize( getPreferredSize() );
        font =new Font(KeyPanel.FONT_NAME,Font.PLAIN|Font.BOLD, KeyPanel.FONT_SIZE);
        StringBuilder txt = new StringBuilder();
        CharParser cp = new CharParser(keys);
        for (int idx = 0; idx < lines * cols; idx++) {
            txt.setLength(0);
            char c = cp.next();
            if (c=='[') {
                c = cp.next();
                do txt.append(c); while( (c = cp.next())!=']' );
            } else {
                String m = map.get(c);
                if (m!=null) txt.append(m);
                else if (c!=0) txt.append(c);
            }
            add(new KeyPanel.KeyButton(txt.toString(),idx / cols, idx % cols));
        }
    }

    public boolean isPressed(int l, int c) {
        return grid[l][c].isPressed();
    }

    public class KeyButton extends JButton {
        private char key;
        private int line, col;
        private boolean pressed;

        public Dimension getPreferredSize() {
            return keyDim;
        }

        public KeyButton(String txt, int l, int c) {
            super(txt);
            line = l;
            col = c;
            grid[l][c] = this;
            setFont(font);
            setMargin(new Insets(2, 2, 2, 2));
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) { fire(true); }
                public void mouseReleased(MouseEvent e) { fire(false); }
            });
            addKeyListener(new java.awt.event.KeyListener() {
                public void keyTyped(KeyEvent e) {}
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode()==KeyEvent.VK_SPACE && !pressed) { fire(true); }
                }
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode()==KeyEvent.VK_SPACE) { fire(false); }
                }
            });
        }
        private void fire(boolean state) {
            pressed = state;
            listener.keyChanged(line, col, KeyButton.this);
        }
        public boolean isPressed() { return pressed; }
        public char getKey() { return key; }
    }

    public interface KeyListener {
        void keyChanged(int line, int col, KeyButton key);
    }
}