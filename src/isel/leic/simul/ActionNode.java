package isel.leic.simul;

abstract class ActionNode {
    ActionNode next;
    ActionNode() { next = null; }
    abstract void doNow();

    @Override
    public String toString() {
        return "#Nodes="+nextNodes(1);
    }
    int nextNodes(int n) {
        return next==null ? n : next.nextNodes(n+1);
    }

    boolean contains(Class type, Object obj) {
        if (type.isInstance(this) && isEqual(obj)) return true;
        if (next==null) return false;
        return next.contains(type,obj);
    }

    protected boolean isEqual(Object obj) {
        return false;
    }
}
