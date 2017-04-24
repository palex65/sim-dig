package isel.leic.simul;

class ActionQueue {
    private volatile ActionNode first;
    private volatile ActionNode last;
    private volatile int len = 0;

    void addNode(ActionNode node) {
        synchronized (Simul.logicActions) {
            if (first == null)
                first = last = node;
            else {
                last.next = node;
                last = node;
            }
            ++len;
            Simul.logicActions.notify();
        }
    }

    ActionNode removeFirstNode() {
        synchronized (Simul.logicActions) {
            ActionNode node = first;
            if (node != null) {
                if (first == last)
                    first = last = null;
                else {
                    first = node.next;
                    node.next = null;
                }
                --len;
            }
            return node;
        }
    }

    int size() {
        return len;
    }

    boolean isEmpty() {
        return first==null;
    }

    public boolean contains(Class type, Object obj) {
        return first != null && first.contains(type, obj);
    }
}
