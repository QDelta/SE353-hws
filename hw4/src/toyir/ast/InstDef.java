package toyir.ast;

import java.util.BitSet;

public abstract class InstDef {
    public abstract BitSet getUseVars();
    public abstract Integer getDefVar(); // null means no def
}
