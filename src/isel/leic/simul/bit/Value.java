package isel.leic.simul.bit;

import java.awt.*;

public enum Value {
    LOW('0', new Color(200,50,50)),//Color.RED),
    HIGH('1', new Color(50,200,50)),//Color.GREEN),
    OPEN('?', Color.LIGHT_GRAY);

    public final char view;
    public final Color color;

    private Value(char face, Color color) {
        view = face;
        this.color = color;
    }

    public boolean isOpen() { return this==OPEN; }
    public boolean isVcc() { return this==HIGH; }
    public boolean isGnd() { return this==LOW; }

    public Value not() { return isOpen() ? OPEN : (isVcc() ? LOW : HIGH); }
    public static Value valueOf(boolean val) { return val ? HIGH : LOW; }
}
