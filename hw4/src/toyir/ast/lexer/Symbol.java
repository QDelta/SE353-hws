package toyir.ast.lexer;

public class Symbol extends Token {
    private char symbol;

    public char getSymbol() { return symbol; }

    public Symbol(char symbol) {
        this.symbol = symbol;
    }

    public String toString() {
        return "SYMBOL " + String.valueOf(symbol);
    }
}
