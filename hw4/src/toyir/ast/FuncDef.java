package toyir.ast;

import java.util.Vector;

public class FuncDef {
    public enum Type {
        I1, I32
    }

    private Type type;
    private String name;
    private Vector<BlockDef> blocks;

    public Type getType() { return type; }
    public String getName() { return name; }
    public Vector<BlockDef> getBlocks() { return blocks; }

    public FuncDef(Type type, String name, Vector<BlockDef> blocks) {
        this.type = type;
        this.name = name;
        this.blocks = blocks;
    }
}
