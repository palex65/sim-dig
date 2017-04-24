package isel.leic.simul.state;

import isel.leic.simul.Simul;
import isel.leic.simul.bit.InBit;
import isel.leic.simul.elem.Elem;

public abstract class StateMachine extends Elem {
    private State cur = null;
    private State start = null;

    private void init() {
        cur = start;
        cur.onEntry();
        if (listener!=null) listener.onEntry(cur);
        onInputChanged(null);
    }

    protected void start(State s) {
        start = s;
        initOutputs();
        init();
    }
    protected void start(String name) { start( state(name) ); }
    protected void start(int numState) { start( states[0] ); }
    protected void start() { start(states[0]); }

    public void reset() {
        if (start==null) start = states[0];
        if (cur!=null) cur.onExit(start);
        else initOutputs();
        init();
    }

    public void step() {
        if (cur==null) {
            if (states==null || states[0]==null)
                System.out.println("Inválid State Machine");
            if (start==null) start = states[0];
            init();
        }
        State next = cur.nextState();
        if (next!=null && next!=cur) {
            cur.onExit(next);
            cur = next;
            cur.onEntry();
            if (listener!=null) listener.onEntry(cur);
            onInputChanged(null);
        } else {
            cur.onStay();
        }
    }

    public void onInputChanged(InBit bit) {
        simul.step( this );
    }

    @Override
    public String toString() {
        return "cur="+cur;
    }

    @Override
    public String getStatus() {
        return "<tr><td colspan=2><center>"+cur+"</center></td></tr>"+super.getStatus();
    }

    private StateMachineListener listener;
    public void setListener( StateMachineListener listener ) {
        this.listener = listener;
    }

    protected State[] states;
    protected void states(String ... names) {
        states = new State[names.length];
        for (int i = 0; i < names.length; i++)
            states[i] = new State(names[i],this);
    }
    public State state(String name) {
        if (states == null) {
            states(name);
            return states[0];
        }
        for (State s : states)
            if (s.getName().equalsIgnoreCase(name)) return s;
        return addState(name);
    }

    protected State state(int numState) {
        return states[numState<states.length ? numState : 0];
    }

    private State addState(String name) {
        State s = new State(name,this);
        State[] ss = new State[states.length+1];
        ss[ss.length-1] = s;
        System.arraycopy(states,0,ss,0,states.length);
        states = ss;
        return s;
    }

    public State getCurState() {
        return cur;
    }

    public int getStateIdx(State state) {
        for(int idx=0 ; idx<states.length ; ++idx)
            if (states[idx]==state) return idx;
        return -1;
    }

    private static Simul.State simul;
    public static void setSimul(Simul.State s) { simul=s; }
}
