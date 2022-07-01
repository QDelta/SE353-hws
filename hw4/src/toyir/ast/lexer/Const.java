package toyir.ast.lexer;

public class Const extends Token {
    private int value;

    public int getValue() { return value; }

    public Const(int value) {
        this.value = value;
    }

    public String toString() {
        return "CONST " + String.valueOf(value);
    }
}
