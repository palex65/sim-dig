package isel.leic.simul.bit;

public interface BitListener {
    void update(Value value);
    BitListener getNext();
    void setNext(BitListener next);
}
