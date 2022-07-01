package toyir.ast;

import java.util.BitSet;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;

import toyir.util.Pair;

public class BlockDef {
    private String blockLabel;
    private Vector<InstDef> insts;

    public String getBlockLabel() { return blockLabel; }
    public Vector<InstDef> getInsts() { return insts; }

    public BlockDef(String blockLabel, Vector<InstDef> insts) {
        this.blockLabel = blockLabel;
        this.insts = insts;
    }

    public Pair<BitSet, BitSet> getDefUseSet() {
        BitSet defSet = new BitSet();
        BitSet useSet = new BitSet();

        for (int i = insts.size() - 1; i >= 0; --i) {
            Integer defVar = insts.get(i).getDefVar();
            BitSet instUse = insts.get(i).getUseVars();
            // defSet = defSet + instDef - instUse
            // useSet = useSet - instDef + instUse
            if (defVar != null) {
                defSet.set(defVar);
                useSet.clear(defVar);
            }
            defSet.andNot(instUse);
            useSet.or(instUse);
        }

        return new Pair<>(defSet, useSet);
    }

    public Pair<Vector<Integer>, Vector<BitSet>> getInstDefUse() {
        Vector<Integer> instDefs = new Vector<>();
        Vector<BitSet> instUses = new Vector<>();

        for (InstDef inst : insts) {
            instDefs.add(inst.getDefVar());
            instUses.add(inst.getUseVars());
        }

        return new Pair<>(instDefs, instUses);
    }

    public Set<String> nextBlockLabels() {
        InstDef lastInst = insts.lastElement();
        HashSet<String> nextLabels = new HashSet<>();

        if (lastInst instanceof BrInst) {
            BrInst lastBr = (BrInst)lastInst;
            nextLabels.add(lastBr.getTrueLabel());
            nextLabels.add(lastBr.getFalseLabel());
        } else if (lastInst instanceof JmpInst) {
            JmpInst lastJmp = (JmpInst)lastInst;
            nextLabels.add(lastJmp.getJmpLabel());
        }

        return nextLabels;
    }

    public boolean isReturn() {
        return insts.lastElement() instanceof RetInst;
    }
}
