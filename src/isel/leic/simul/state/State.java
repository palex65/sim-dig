package isel.leic.simul.state;

import isel.leic.simul.bit.IBit;
import isel.leic.simul.bit.OBit;
import isel.leic.simul.bit.Value;
import isel.leic.simul.parser.DinamicStateMachine;

import java.util.function.BooleanSupplier;

public class State {
    protected final String name;
    protected final StateMachine machine;

    public State(String name, StateMachine sm) { this(name,sm,(OBit[])null); }
    public State(String name,  StateMachine sm, OBit... activeBits) {
        this.name = name;
        machine = sm;
        if (activeBits!=null) outputs(activeBits);
    }

    public String getName() {
        return name;
    }

    public State transitToWhen(String stateName, BooleanSupplier condition) {
        return transition(condition,machine.state(stateName));
    }
    public State transitTo(String stateName) { return transitToWhen(stateName,null); }

    public State transitToWhen(int stateIdx, BooleanSupplier condition) {
        return transition(condition,machine.state(stateIdx));
    }
    public State transitTo(int stateIdx) { return transitToWhen(stateIdx,null); }

    public State transitNextWhen(BooleanSupplier condition) {
        return transition(condition,null);
    }
    public State transitNext() { return transitNextWhen(null); }

    public int getIdx() { return machine.getStateIdx(this); }

    private static class Output {
        private OBit bit;
        private BooleanSupplier condition;
        private Output next;

        public Output(OBit bit, BooleanSupplier condition, Output next) {
            this.bit = bit; this.condition = condition; this.next = next;
        }
        public void eval() {
            bit.write(Value.valueOf(condition==null || condition.getAsBoolean()));
            if (next!=null) next.eval();
        }
        public void reset(State nextState) {
            if (!nextState.isOutBit(bit)) bit.reset();
            if (next!=null) next.reset(nextState);
        }
        public boolean contains(OBit bit) {
            return bit==this.bit || next!=null && next.contains(bit);
        }
    };

    private Output outs;

    public State output(BooleanSupplier condition, OBit toActivate) {
        outs = new Output(toActivate,condition, outs);
        return this;
    }
    public State output(OBit toActivate) {
        outs = new Output(toActivate,null, outs);
        return this;
    }
    public State outputs(OBit... outs) {
        for(OBit out : outs) output(out);
        return this;
    }

    public boolean isOutBit(OBit bit) {
        return outs!=null && outs.contains(bit);
    }

    private Runnable actionOnEntry;
    public State onEntry(Runnable action) { actionOnEntry = action; return this; }

    void onEntry() {
        if (actionOnEntry!=null)
            actionOnEntry.run();
        if (outs !=null)
            outs.eval();
    }

    private Runnable actionOnExit;
    public State onExit(Runnable action) { actionOnExit = action; return this; }

    void onExit(State next) {
        if (outs !=null)
            outs.reset(next);
        if (actionOnExit!=null)
            actionOnExit.run();
    }

    void onStay() {
        if (outs !=null)
            outs.eval();
    }

    private class Transition {
        private final BooleanSupplier condition;
        private State to;
        private final Transition next;

        public Transition(BooleanSupplier condition, State to, Transition next) {
            this.condition = condition;
            this.to = to;
            this.next = next;
        }
        public State nextState() {
            if (to==null) to = State.this.machine.state(State.this.getIdx()+1);
            return (condition==null||condition.getAsBoolean()) ? to : (next==null ? null : next.nextState());
        }
        public boolean hasInBit(IBit b) {
            if (condition!=null && condition instanceof DinamicStateMachine.EvalExp) {
                if (((DinamicStateMachine.EvalExp)condition).hasInBit(b)) return true;
            }
            return next!=null && next.hasInBit(b);
        }
    }

    private Transition trans;

    public boolean hasTransitions() { return trans!=null; }

    public boolean isInBit(IBit b) {
        return trans!=null && trans.hasInBit(b);
    }

    protected State transition(BooleanSupplier condition, State next) {
        trans = new Transition(condition, next, trans);
        return this;
    }

    public State nextState() {
        return trans==null ? null : trans.nextState();
    }

    @Override
    public String toString() {
        return "State: "+name;
    }

}
