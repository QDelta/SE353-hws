package toyir.ast;

import java.util.BitSet;

public class BrInst extends InstDef {
    private Value condVal;
    private String trueLabel;
    private String falseLabel;

    public BrInst(Value condVal, String trueLabel, String falseLabel) {
        this.condVal = condVal;
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
    }

    public Value getCondVal() { return condVal; }
    public String getTrueLabel() { return trueLabel; }
    public String getFalseLabel() { return falseLabel; }

    public BitSet getUseVars() {
        BitSet useSet = new BitSet();
        if (condVal.getType() == Value.Type.Local) {
            useSet.set(condVal.getVal());
        }
        return useSet;
    }

    public Integer getDefVar() {
        return null;
    }
}
