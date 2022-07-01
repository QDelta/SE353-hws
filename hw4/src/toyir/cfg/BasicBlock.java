package toyir.cfg;

import java.util.BitSet;
import java.util.Vector;

import toyir.util.Pair;
import toyir.ast.BlockDef;

public class BasicBlock {
    private BlockDef blkSyn;
    private BitSet defVars;
    private BitSet useVars;
    private Vector<Integer> instDefs;
    private Vector<BitSet> instUses;

    public String getLabel() { return blkSyn.getBlockLabel(); }
    public BitSet getDefVars() { return defVars; }
    public BitSet getUseVars() { return useVars; }
    public Vector<Integer> getInstDefs() { return instDefs; }
    public Vector<BitSet> getInstUses() { return instUses; }

    public BasicBlock(String label) {
        blkSyn = new BlockDef(label, new Vector<>());
        defVars = new BitSet();
        useVars = new BitSet();
        instDefs = new Vector<>();
        instUses = new Vector<>();
    }

    public BasicBlock(BlockDef blkDef) {
        blkSyn = blkDef;
        Pair<BitSet, BitSet> defUse = blkDef.getDefUseSet();
        Pair<Vector<Integer>, Vector<BitSet>> instDefUse = blkDef.getInstDefUse();
        defVars = defUse.fst;
        useVars = defUse.snd;
        instDefs = instDefUse.fst;
        instUses = instDefUse.snd;
    }
}
