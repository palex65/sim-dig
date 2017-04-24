package isel.leic.simul.parser;

public class SyntaxError extends RuntimeException {
    private boolean fatal;
    public SyntaxError() {
        super("Syntax error");
    }
    public SyntaxError(String msg) {
        super(msg);
    }
    public SyntaxError(String msg, boolean fatal) {
        super(msg);
        this.fatal = true;
    }
    public boolean isFatal() { return fatal; }
}
