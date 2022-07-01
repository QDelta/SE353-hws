package toyir.ast;

import java.util.BitSet;

public class JmpInst extends InstDef {
    private String jmpLabel;

    public JmpInst(String jmpLabel) {
        this.jmpLabel = jmpLabel;
    }

    public String getJmpLabel() { return jmpLabel; }

    public BitSet getUseVars() {
        return new BitSet();
    }

    public Integer getDefVar() {
        return null;
    }
}
