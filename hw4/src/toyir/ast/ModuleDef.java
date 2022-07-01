package toyir.ast;

import java.util.Vector;

public class ModuleDef {
    private Vector<FuncDef> funcs;

    public Vector<FuncDef> getFuncs() { return funcs; }

    public ModuleDef(Vector<FuncDef> funcs) {
        this.funcs = funcs;
    }
}
