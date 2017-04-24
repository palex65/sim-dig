package isel.leic.simul.parser;

import isel.leic.simul.bit.IBit;
import isel.leic.simul.bit.InBit;
import isel.leic.simul.bit.OBit;
import isel.leic.simul.bit.OutBit;
import isel.leic.simul.module.Module;
import isel.leic.simul.state.State;
import isel.leic.simul.state.StateMachine;

import java.util.function.BooleanSupplier;

public class DinamicStateMachine extends StateMachine {

    void addInput(String name) { new StateMachine.Input(name); }
    void addOutput(String name) { new StateMachine.Output(name); }

    public void finish() {
        boolean[] usedInputs = new boolean[inputs.size()];
        boolean[] usedOutputs = new boolean[outputs.size()];
        for(State s : states) {
            if (!s.hasTransitions())
                throw new SyntaxError("State "+s.getName()+" without transitions");
            for(int i=0 ; i<usedOutputs.length ; ++i)
                if (s.isOutBit(outputs.get(i))) usedOutputs[i] = true;
            for(int i=0 ; i<usedInputs.length ; ++i)
                if (s.isInBit(inputs.get(i))) usedInputs[i] = true;
        }
        for(int i=0 ; i<usedOutputs.length ; ++i)
            if (!usedOutputs[i])
                throw new SyntaxError("Unused output '"+((OutBit)outputs.get(i)).getName()+"' in State machine");
        for(int i=0 ; i<usedInputs.length ; ++i)
            if (!usedInputs[i])
                throw new SyntaxError("Unused input '"+((InBit)inputs.get(i)).getName()+"' in State machine");
        start();
    }



    public static class EvalExp implements BooleanSupplier {
        private final Exp condition;
        public EvalExp(Exp exp) { condition = exp; }
        @Override
        public boolean getAsBoolean() { return condition.eval(); }
        public boolean hasInBit(IBit b) { return condition.hasInBit(b); }
    }
    void transition(State from, String to, Exp exp) {
        from.transitToWhen(to,new EvalExp(exp));
    }
    public void transition(State from, String to) {
        from.transitTo(to);
    }
    public void output(State state, Exp cond, OBit out) {
        if (cond==null) state.output(out);
        else state.output(new EvalExp(cond),out);
    }
}

