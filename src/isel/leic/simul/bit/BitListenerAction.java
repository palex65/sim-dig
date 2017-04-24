package isel.leic.simul.bit;

class BitListenerAction implements BitListener {
    private BitListener next;
    private Runnable action;
    public BitListenerAction(Runnable action) { this.action = action; }
    public void update(Value value) { action.run(); }
    public BitListener getNext() { return next; }
    public void setNext(BitListener next) { this.next = next; }
}
