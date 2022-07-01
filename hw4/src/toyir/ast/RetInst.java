package toyir.ast;

import java.util.BitSet;

public class RetInst extends InstDef {
    private Value retVal;

    public RetInst(Value retVal) {
        this.retVal = retVal;
    }

    public Value getRetVal() { return retVal; }

    public BitSet getUseVars() {
        BitSet useSet = new BitSet();
        if (retVal.getType() == Value.Type.Local) {
            useSet.set(retVal.getVal());
        }
        return useSet;
    }

    public Integer getDefVar() {
        return null;
    }
}
