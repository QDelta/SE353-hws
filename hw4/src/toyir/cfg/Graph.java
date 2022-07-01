package toyir.cfg;

import java.util.BitSet;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Vector;

import toyir.ast.BlockDef;
import toyir.ast.Value;

public class Graph {
    private BasicBlock entry;
    private BasicBlock exit;
    private Vector<String> labels;
    private Map<String, BasicBlock> lblToBlk;
    private Map<String, Set<String>> lblToNext;
    private Map<String, Set<String>> lblToPrev;
    private Map<String, BitSet> lblToIn;
    private Map<String, BitSet> lblToOut;
    private Map<String, BitSet> lblToInUndef;
    private Map<String, BitSet> lblToOutUndef;

    // assume that "ENTRY" and "EXIT" are not used
    private static final String ENTRY_LBL = "ENTRY";
    private static final String EXIT_LBL = "EXIT";

    public Graph(Vector<BlockDef> blkDefs) {
        entry = new BasicBlock(ENTRY_LBL);
        exit = new BasicBlock(EXIT_LBL);

        labels = new Vector<>();
        lblToBlk = new HashMap<>();
        lblToNext = new HashMap<>();
        lblToPrev = new HashMap<>();

        // initialize lblToPrev
        for (BlockDef blkDef : blkDefs) {
            lblToPrev.put(blkDef.getBlockLabel(), new HashSet<>());
        }
        lblToPrev.put(ENTRY_LBL, new HashSet<>());
        lblToPrev.put(EXIT_LBL, new HashSet<>());

        // construct basic blocks
        for (int i = 0; i < blkDefs.size(); ++i) {
            BlockDef def = blkDefs.get(i);
            BasicBlock blk = new BasicBlock(def);

            String blkLabel = blk.getLabel();
            labels.add(blkLabel);
            lblToBlk.put(blkLabel, blk);

            Set<String> nextLabels = def.nextBlockLabels();
    
            // the next block of return or last block is EXIT
            if (def.isReturn() || i == blkDefs.size() - 1) {
                nextLabels.add(EXIT_LBL);
            }

            lblToNext.put(blkLabel, nextLabels);
            for (String nextLbl : nextLabels) {
                lblToPrev.get(nextLbl).add(blkLabel);
            }

            // the next block of entry is the first block
            if (i == 0) {
                Set<String> entry_next = new HashSet<>();
                entry_next.add(blkLabel);
                lblToNext.put(ENTRY_LBL, entry_next);
                lblToPrev.get(blkLabel).add(ENTRY_LBL);
            }
        }

        lblToBlk.put(ENTRY_LBL, entry);
        lblToBlk.put(EXIT_LBL, exit);
    }

    // return: is IN changed
    private boolean lvaUpdate(String blkLabel) {
        BitSet out = lblToOut.get(blkLabel);
        BitSet in = lblToIn.get(blkLabel);

        for (String nextLbl : lblToNext.get(blkLabel)) {
            // OUT[B] = sum IN[S] where S : successor of B
            out.or(lblToIn.get(nextLbl));
        }

        // IN[B] = use[B] + (OUT[B] - def[B])
        BasicBlock blk = lblToBlk.get(blkLabel);
        BitSet newIn = new BitSet();
        newIn.or(out);
        newIn.andNot(blk.getDefVars());
        newIn.or(blk.getUseVars());
        if (newIn.equals(in)) {
            return false;
        } else {
            lblToIn.put(blkLabel, newIn);
            return true;
        }
    }

    public void liveVariableAnalyze() {
        // initialize
        lblToIn = new HashMap<>();
        lblToOut = new HashMap<>();
        lblToIn.put(ENTRY_LBL, new BitSet());
        lblToOut.put(ENTRY_LBL, new BitSet());
        lblToIn.put(EXIT_LBL, new BitSet());
        lblToOut.put(EXIT_LBL, new BitSet());
        for (String label : labels) {
            lblToIn.put(label, new BitSet());
            lblToOut.put(label, new BitSet());
        }

        boolean inChanged = true;
        while (inChanged) {
            inChanged = false;
            for (int i = labels.size() - 1; i >= 0; --i) {
                inChanged = inChanged || lvaUpdate(labels.get(i));
            }
            inChanged = inChanged || lvaUpdate(ENTRY_LBL);
        }
    }

    private void showLVABlock(String blkLabel) {
        System.out.print(blkLabel + ": ");
        BasicBlock blk = lblToBlk.get(blkLabel);

        System.out.print("DEF = ");
        Value.showVariableSet(blk.getDefVars());
        System.out.print(", ");

        System.out.print("USE = ");
        Value.showVariableSet(blk.getUseVars());
        System.out.print(", ");

        System.out.print("IN = ");
        Value.showVariableSet(lblToIn.get(blkLabel));
        System.out.print(", ");

        System.out.print("OUT = ");
        Value.showVariableSet(lblToOut.get(blkLabel));
        System.out.println();
    }

    public void showLVAnalysis() {
        showLVABlock(ENTRY_LBL);
        for (String label : labels) {
            showLVABlock(label);
        }
        showLVABlock(EXIT_LBL);
    }

    // return: is OUTUNDEF changed
    private boolean undefSetUpdate(String blkLabel) {
        BitSet inUndef = lblToInUndef.get(blkLabel);
        BitSet outUndef = lblToOutUndef.get(blkLabel);

        for (String prevLbl : lblToPrev.get(blkLabel)) {
            // INUNDEF[B] = sum OUTUNDEF[S] where S : predecessor of B
            inUndef.or(lblToOutUndef.get(prevLbl));
        }

        // OUTUNDEF[B] = INUNDEF[B] - sum def[inst] where inst in B
        BasicBlock blk = lblToBlk.get(blkLabel);
        BitSet newOutU = new BitSet();
        newOutU.or(inUndef);
        for (Integer defVar : blk.getInstDefs()) {
            if (defVar != null) {
                newOutU.clear(defVar);
            }
        }

        if (newOutU.equals(outUndef)) {
            return false;
        } else {
            lblToOutUndef.put(blkLabel, newOutU);
            return true;
        }
    }

    private void undefSetAnalyze() {
        int varCount = Value.getVarCount();
        BitSet allUndef = new BitSet(varCount);
        allUndef.set(0, varCount);

        // initialize
        lblToInUndef = new HashMap<>();
        lblToOutUndef = new HashMap<>();
        lblToInUndef.put(ENTRY_LBL, allUndef);
        lblToOutUndef.put(ENTRY_LBL, allUndef);
        lblToInUndef.put(EXIT_LBL, new BitSet());
        lblToOutUndef.put(EXIT_LBL, new BitSet());
        for (String label : labels) {
            lblToInUndef.put(label, new BitSet());
            lblToOutUndef.put(label, new BitSet());
        }

        boolean outChanged = true;
        while (outChanged) {
            outChanged = false;
            for (String label: labels) {
                outChanged = outChanged || undefSetUpdate(label);
            }
            outChanged = outChanged || undefSetUpdate(EXIT_LBL);
        }
    }

    private void showUndefSetBlock(String blkLabel) {
        System.out.print(blkLabel + ": ");
        BasicBlock blk = lblToBlk.get(blkLabel);

        BitSet instDefSet = new BitSet();
        for (Integer defVar : blk.getInstDefs()) {
            if (defVar != null) {
                instDefSet.set(defVar);
            }
        }
        System.out.print("INSTDEF = ");
        Value.showVariableSet(instDefSet);
        System.out.print(", ");

        System.out.print("USE = ");
        Value.showVariableSet(blk.getUseVars());
        System.out.print(", ");

        System.out.print("INUNDEF = ");
        Value.showVariableSet(lblToInUndef.get(blkLabel));
        System.out.print(", ");

        System.out.print("OUTUNDEF = ");
        Value.showVariableSet(lblToOutUndef.get(blkLabel));
        System.out.println();
    }

    private void showUndefSetAnalysis() {
        showUndefSetBlock(ENTRY_LBL);
        for (String label : labels) {
            showUndefSetBlock(label);
        }
        showUndefSetBlock(EXIT_LBL);
    }

    public void undefAnalyze() {
        undefSetAnalyze();
        showUndefSetAnalysis();
        System.out.println();

        for (String blkLabel : labels) {
            BasicBlock blk = lblToBlk.get(blkLabel);
            BitSet blkInUndef = (BitSet)lblToInUndef.get(blkLabel).clone();
            BitSet blkUse = blk.getUseVars();

            if (blkUse.intersects(blkInUndef)) {
                // possible use of undef detected (block)
                Vector<BitSet> instUses = blk.getInstUses();
                Vector<Integer> instDefs = blk.getInstDefs();

                for (int i = 0; i < instUses.size(); ++i) {
                    Integer defVar = instDefs.get(i);
                    BitSet instUse = instUses.get(i);

                    if (instUse.intersects(blkInUndef)) {
                        // possible use of undef detected (inst)
                        BitSet undefs = (BitSet)instUse.clone();
                        undefs.and(blkInUndef);
                        System.out.print("Instruction " + (i + 1) + " in block " + blkLabel + " uses variables ");
                        Value.showVariableSet(undefs);
                        System.out.println(" which are possibly undefined before.");

                        // update blkInUndef;
                        if (defVar != null) {
                            blkInUndef.clear(defVar);
                        }
                    }
                }
            }
        }
    }
}