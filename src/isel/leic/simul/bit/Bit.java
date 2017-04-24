package isel.leic.simul.bit;

public interface Bit {
    Value get();
    default boolean isTrue()  { return get()==Value.HIGH; }
    default boolean isFalse() { return get()==Value.LOW; }
    default boolean isOpen()  { return get()==Value.OPEN; }

    void write(Value value);
    default void set()      { write(Value.HIGH); }
    default void reset()    { write(Value.LOW); }
    default void open()     { write(Value.OPEN); }

    void addListener(BitListener bl);
    default void addActionOnChange(Runnable action) { addListener(new BitListenerAction(action)); }

    void pull(boolean up);
    default void pullUp() { pull(true); }
    default void pullDown() { pull(false); }

    void setInvert(boolean invert);
    boolean isInverted();
}
