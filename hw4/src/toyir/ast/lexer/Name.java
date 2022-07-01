package toyir.ast.lexer;

import java.util.Map;

public class Name extends Token {
    // types of name, including keywords
    public enum Type {
        LABEL, GLOBAL, LOCAL,
        ADD, SUB, MUL, DIV, REM,
        EQ, NE, LT, LE, GT, GE,
        I1, I32,
        DEFINE,
        JMP, BR, RET
    }

    private String name;
    private Type type;

    private static final Map<String, Type> kwMap = Map.ofEntries(
        Map.entry("add", Type.ADD),
        Map.entry("sub", Type.SUB),
        Map.entry("mul", Type.MUL),
        Map.entry("div", Type.DIV),
        Map.entry("rem", Type.REM),
        Map.entry("eq",  Type.EQ ),
        Map.entry("ne",  Type.NE ),
        Map.entry("lt",  Type.LT ),
        Map.entry("le",  Type.LE ),
        Map.entry("gt",  Type.GT ),
        Map.entry("ge",  Type.GE ),
        Map.entry("i1",  Type.I1 ),
        Map.entry("i32", Type.I32),
        Map.entry("jmp", Type.JMP),
        Map.entry("br",  Type.BR ),
        Map.entry("ret", Type.RET),
        Map.entry("define", Type.DEFINE)
    );

    public String getName() { return name; }
    public Type getType() { return type; }

    public String toString() {
        switch (type) {
            case GLOBAL:
                return "GLOBAL @" + name;
            case LOCAL:
                return "LOCAL %" + name;
            case LABEL:
                return "LABEL " + name;
            default:
                return "KEYWORD " + name;
        }
    }

    public Name(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public Name(String s) {
        Type kwType = kwMap.get(s);

        if (kwType != null) {
            name = s;
            type = kwType;
        } else if (s.charAt(0) == '@') {
            name = s.substring(1);
            type = Type.GLOBAL;
        } else if (s.charAt(0) == '%') {
            name = s.substring(1);
            type = Type.LOCAL;
        } else {
            name = s;
            type = Type.LABEL;
        }
    }
}
