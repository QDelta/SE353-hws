package toyir.ast;

import java.util.BitSet;

public class AssignInst extends InstDef {
    private int assignLocal;
    private Value assignVal;

    public AssignInst(int assignLocal, Value assignVal) {
        this.assignLocal = assignLocal;
        this.assignVal = assignVal;
    }

    public int getAssignLocal() { return assignLocal; }
    public Value getAssignVal() { return assignVal; }

    public BitSet getUseVars() {
        BitSet useSet = new BitSet();
        if (assignVal.getType() == Value.Type.Local) {
            useSet.set(assignVal.getVal());
        }
        return useSet;
    }

    public Integer getDefVar() {
        return assignLocal;
    }
}
