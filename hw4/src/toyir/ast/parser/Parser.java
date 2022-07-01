package toyir.ast.parser;

import java.util.Vector;

import toyir.ast.*;
import toyir.ast.lexer.*;

public class Parser {
    public class ParseException extends Exception {
        public ParseException(String msg) {
            super(msg);
        }
    }

    private Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Value parseValue() throws ParseException {
        String expectMsg = "LOCAL or CONST";
        Token tok = parseToken(expectMsg);
        if (tok instanceof Name) {
            Name nTok = (Name)tok;
            if (nTok.getType() == Name.Type.LOCAL) {
                return new Value(nTok.getName());
            } else throw error(expectMsg, nTok);
        } else if (tok instanceof Const) {
            Const cTok = (Const)tok;
            return new Value(cTok.getValue());
        } else throw error(expectMsg, tok);
    }

    public BinInst.Inst nTypeToBinInst(Name.Type t) {
        switch (t) {
            case ADD: return BinInst.Inst.ADD;
            case SUB: return BinInst.Inst.SUB;
            case MUL: return BinInst.Inst.MUL;
            case DIV: return BinInst.Inst.DIV;
            case REM: return BinInst.Inst.REM;
            case EQ : return BinInst.Inst.EQ ;
            case NE : return BinInst.Inst.NE ;
            case LT : return BinInst.Inst.LT ;
            case LE : return BinInst.Inst.LE ;
            case GT : return BinInst.Inst.GT ;
            case GE : return BinInst.Inst.GE ;
            default: return null;
        }
    }

    public InstDef parseInst() throws ParseException {
        Name first = parseNameOrError("LOCAL or JMP or BR or RET");
        switch (first.getType()) {
            case JMP:
                String label = parseNameOrError("LABEL", Name.Type.LABEL);
                return new JmpInst(label);
            case BR:
                Value condVal = parseValue();
                String trueLabel = parseNameOrError("LABEL", Name.Type.LABEL);
                String falseLabel = parseNameOrError("LABEL", Name.Type.LABEL);
                return new BrInst(condVal, trueLabel, falseLabel);
            case RET:
                Value retVal = parseValue();
                return new RetInst(retVal);
            case LOCAL:
                Value localVal = new Value(first.getName());
                String expectMsg = "LOCAL or CONST or BIN or COND";
                expectSymbol('=');
                Token tok = parseToken(expectMsg);
                if (tok instanceof Name) {
                    Name nTok = (Name)tok;
                    BinInst.Inst binInst = nTypeToBinInst(nTok.getType());
                    if (binInst != null) {
                        // BinInst
                        Value leftVal = parseValue();
                        Value rightVal = parseValue();
                        return new BinInst(binInst, localVal.getVal(), leftVal, rightVal);
                    } else {
                        // AssignInst
                        if (nTok.getType() == Name.Type.LOCAL) {
                            return new AssignInst(localVal.getVal(), new Value(nTok.getName()));
                        } else throw error(expectMsg, nTok);
                    }
                } else if (tok instanceof Const) {
                    // AssignInst
                    Const cTok = (Const)tok;
                    return new AssignInst(localVal.getVal(), new Value(cTok.getValue()));
                } else throw error(expectMsg, tok);
            default:
                throw error("LOCAL or JMP or BR or RET", first);
        }
    }

    public BlockDef parseBlock() throws ParseException {
        String label = parseNameOrError("LABEL", Name.Type.LABEL);

        expectSymbol(':');

        Vector<InstDef> insts = new Vector<>();
        insts.add(parseInst());
        while (true) {
            Token tok = lookAheadToken("LABEL or SYMBOL } or LOCAL or JMP or BR or RET");
            if (tok instanceof Symbol && ((Symbol)tok).getSymbol() == '}') {
                break;
            } else if (tok instanceof Name && ((Name)tok).getType() == Name.Type.LABEL) {
                break;
            }
            insts.add(parseInst());
        }
        return new BlockDef(label, insts);
    }

    public FuncDef parseFunc() throws ParseException {
        parseNameOrError("DEFINE", Name.Type.DEFINE);

        FuncDef.Type fType;
        Name tTok = parseNameOrError("TYPE");
        switch (tTok.getType()) {
            case I1: fType = FuncDef.Type.I1; break;
            case I32: fType = FuncDef.Type.I32; break;
            default: throw error("TYPE", tTok);
        }
        
        String name = parseNameOrError("GLOBAL", Name.Type.GLOBAL);

        expectSymbol('(');
        expectSymbol(')');

        expectSymbol('{');
        
        Vector<BlockDef> blocks = new Vector<>();
        blocks.add(parseBlock());
        while (true) {
            Token tok = lookAheadToken("LABEL or SYMBOL }");
            if (tok instanceof Symbol && ((Symbol)tok).getSymbol() == '}') {
                break;
            }
            blocks.add(parseBlock());
        }

        expectSymbol('}');

        return new FuncDef(fType, name, blocks);
    }

    public ModuleDef parse() throws ParseException {
        Vector<FuncDef> funcs = new Vector<>();
        funcs.add(parseFunc());
        while (true) {
            if (! lexer.hasNext()) {
                break;
            }
            funcs.add(parseFunc());
        }

        return new ModuleDef(funcs);
    }

    private Token lookAheadToken(String expectMsg) throws ParseException {
        if (lexer.hasNext()) {
            return lexer.lookAhead();
        } else throw error(expectMsg, null);
    }

    private Token parseToken(String expectMsg) throws ParseException {
        if (lexer.hasNext()) {
            return lexer.nextToken();
        } else throw error(expectMsg, null);
    }

    private Name parseNameOrError(String expectMsg) throws ParseException {
        Token tok = parseToken(expectMsg);
        if (tok instanceof Name) {
            return (Name)tok;
        } else throw error(expectMsg, tok);
    }

    private String parseNameOrError(String expectMsg, Name.Type expect) throws ParseException {
        Name nTok = parseNameOrError(expectMsg);
        if (nTok.getType() == expect) {
            return nTok.getName();
        } else throw error(expectMsg, nTok);
    }

    private void expectSymbol(char sym) throws ParseException {
        String expectMsg = String.valueOf(sym);
        Token tok = parseToken(expectMsg);
        if (tok instanceof Symbol) {
            Symbol sTok = (Symbol)tok;
            if (sTok.getSymbol() != sym) {
                throw error(expectMsg, sTok);
            }
        } else throw error(expectMsg, tok);
    }

    private ParseException error(String expectMsg, Token found) {
        if (found != null) {
            return new ParseException("Expect " + expectMsg + ", found " + found.toString());
        } else {
            return new ParseException("Expect " + expectMsg + ", found nothing");
        }
    }
}
