package isel.leic.simul.parser;

import isel.leic.simul.bit.IBit;
import isel.leic.simul.bit.InBit;
import isel.leic.simul.state.StateMachine;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

class Exp {
    public boolean eval() { return true; }
    public boolean hasInBit(IBit b) { return false; }

    private static class In extends Exp {
        private final InBit var;
        In(InBit input) { var=input; }
        @Override
        public boolean eval() { return var.isTrue(); }
        public boolean hasInBit(IBit b) { return var==b; }
    }

    private static class Not extends Exp {
        private final Exp arg;
        Not(Exp exp) { arg=exp; }
        @Override
        public boolean eval() { return !arg.eval(); }
        public boolean hasInBit(IBit b) { return arg.hasInBit(b); }
    }

    private static class And extends Exp {
        private final Exp left, right;
        And(Exp l, Exp r) { left=l; right=r; }
        @Override
        public boolean eval() { return left.eval() && right.eval(); }
        public boolean hasInBit(IBit b) { return left.hasInBit(b) || right.hasInBit(b); }
    }

    private static class Or extends Exp {
        private final Exp left, right;
        Or(Exp l, Exp r) { left=l; right=r; }
        @Override
        public boolean eval() { return left.eval() || right.eval(); }
        public boolean hasInBit(IBit b) { return left.hasInBit(b) || right.hasInBit(b); }
    }

    private static StreamTokenizer lex;
    private static StateMachine machine;
    private static String txt;

    static Exp parse(String exp, StateMachine sm) {
        try {
            lex = new StreamTokenizer(new StringReader(exp));
            lex.ordinaryChar('|');
            lex.ordinaryChar('&');
            lex.ordinaryChar('!');
            lex.ordinaryChar('(');
            lex.ordinaryChar(')');
            lex.nextToken();
            machine = sm;
            txt = exp;
            Exp res = exp();
            if (lex.ttype!=StreamTokenizer.TT_EOF)
                throw new SyntaxError("Syntax error in expression (" + exp + ") token="+token());
            return res;
        } catch (IOException e) {
            throw new SyntaxError("Inválid expression : "+txt);
        }
    }
    private static String token() {
        String msg = lex.toString();
        int idxEq = msg.indexOf('[');
        int idxCm = msg.lastIndexOf(']');
        if (idxCm<0) idxCm = msg.length();
        return msg.substring(idxEq+1,idxCm);
    }
    private static Exp exp() throws IOException {
        Exp left = term();
        while ( lex.ttype == '|') {
            lex.nextToken();
            if (lex.ttype == '|') lex.nextToken();
            Exp right = term();
            left = new Or(left,right);
        }
        return left;
    }

    private static Exp term() throws IOException {
        Exp left = factor();
        while ( lex.ttype == '&') {
            lex.nextToken();
            if (lex.ttype == '&') lex.nextToken();
            Exp right = factor();
            left = new And(left,right);
        }
        return left;
    }

    private static Exp factor() throws IOException {
        switch (lex.ttype) {
            case '!':
                lex.nextToken();
                return new Not( factor() );
            case '(':
                lex.nextToken();
                Exp e = exp();
                if (lex.ttype!=')')
                    throw new SyntaxError("Missing ')' ");
                lex.nextToken();
                return e;
            case StreamTokenizer.TT_WORD:
                String var = lex.sval;
                lex.nextToken();
                InBit in = (InBit)machine.getField(var);
                if (in==null) throw new SyntaxError("Input of state machine not found : "+var);
                return new In(in);
            default:
                throw new SyntaxError("Unexpected token '"+((char)lex.ttype)+"' in expression : "+txt);
        }
    }
}
