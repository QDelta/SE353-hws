package toyir.ast;

import java.util.Map;
import java.util.HashMap;
import java.util.BitSet;

public class Value {
    // types of value
    public enum Type {
        Local, Const
    }

    private int nameVal;
    private int constVal;
    private Type type;

    // Recording and converting string to index
    private static Map<String, Integer> nameMap = new HashMap<>();
    private static Map<Integer, String> indexMap = new HashMap<>();
    private static int counter = 0;

    public static Map<String, Integer> getNameMap() { return nameMap; }
    public static Map<Integer, String> getIndexMap() { return indexMap; }
    public static int getVarCount() { return counter; }

    public Value(String val) {
        type = Type.Local;
        if (nameMap.containsKey(val)) {
            nameVal = nameMap.get(val);
        } else {
            nameVal = counter;
            nameMap.put(val, counter);
            indexMap.put(counter, val);
            counter += 1;
        }
    }

    public Value(int val) {
        type = Type.Const;
        constVal = val;
    }

    public Type getType() { return type; }
    public int getVal() {
        if (type == Type.Local) {
            return nameVal;
        } else {
            return constVal;
        }
    }

    public static void showVariableSet(BitSet vset) {
        System.out.print('{');
        int[] varis = vset.stream().toArray();
        if (varis.length > 0) {
            System.out.print(indexMap.get(varis[0]));
        }
        for (int i = 1; i < varis.length; ++i) {
            System.out.print(", " + indexMap.get(varis[i]));
        }
        System.out.print('}');
    }
}
