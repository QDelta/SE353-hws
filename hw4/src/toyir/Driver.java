package toyir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import toyir.ast.lexer.Lexer;
import toyir.ast.parser.Parser;
import toyir.ast.parser.Parser.ParseException;
import toyir.cfg.Graph;

public class Driver {
    public static void main(String[] args) throws IOException {
        Path srcPath = Path.of(args[0]);
        Lexer lexer = Lexer.fromFile(srcPath, StandardCharsets.UTF_8);
        Parser parser = new Parser(lexer);
        try {
            var ast = parser.parse();
            // just analyze the first function
            var blkDefs = ast.getFuncs().get(0).getBlocks();

            Graph cfg = new Graph(blkDefs);
            cfg.liveVariableAnalyze();

            System.out.println("Live Variable Analysis:");
            cfg.showLVAnalysis();
            System.out.println();

            System.out.println("Undefined Variable Analysis:");
            cfg.undefAnalyze();
        } catch (ParseException e) {
            System.out.println("ParseError: " + e.getMessage());
        }
    }
}
