import org.antlr.v4.runtime.Token;
public class function {
    String name;
    int calls;
    Token t;

    public function(String name, int calls, Token t) {
        this.name = name;
        this.calls = calls;
        this.t = t;
    }

    public int getCalls() {
        return calls;
    }
    public void setCalls(int calls) {
        this.calls = calls;
    }
    public Token getT() {
        return t;
    }
    public void setT(Token t) {
        this.t = t;
    }
}
