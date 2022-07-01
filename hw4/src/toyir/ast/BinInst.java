package toyir.ast;

import java.util.BitSet;

public class BinInst extends InstDef {
    public enum Inst {
        ADD, SUB, MUL, DIV, REM,
        EQ, NE, LT, LE, GT, GE
    }

    private Inst inst;
    private int assignLocal;
    private Value leftVal;
    private Value rightVal;

    public BinInst(Inst inst, int assignLocal, Value leftVal, Value rightVal) {
        this.inst = inst;
        this.assignLocal = assignLocal;
        this.leftVal = leftVal;
        this.rightVal = rightVal;
    }

    public int getAssignLocal() { return assignLocal; }
    public Value getLeftVal() { return leftVal; }
    public Value getRightVal() { return rightVal; }

    public BitSet getUseVars() {
        BitSet useSet = new BitSet();
        if (leftVal.getType() == Value.Type.Local) {
            useSet.set(leftVal.getVal());
        }
        if (rightVal.getType() == Value.Type.Local) {
            useSet.set(rightVal.getVal());
        }
        return useSet;
    }

    public Integer getDefVar() {
        return assignLocal;
    }
}
