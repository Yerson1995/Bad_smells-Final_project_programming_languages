import org.antlr.v4.runtime.Token;

public class VField {
    String name;
    int calls;
    Token t;
    String clase;

    public VField(String name, int calls, Token t,String clase) {
        this.name = name;
        this.calls = calls;
        this.t = t;
        this.clase=clase;
    }
    public VField(String name) {
        this.name = name;
        this.calls = 1;
        this.t = null;
        this.clase=null;
    }

    public int getCalls() {
        return calls;
    }
    public void setCalls(int calls) {
        this.calls = calls;
    }
    public void AddCalls() {
        this.calls = calls+1;
    }
    public Token getT() {
        return t;
    }
    public void setT(Token t) {
        this.t = t;
    }
}