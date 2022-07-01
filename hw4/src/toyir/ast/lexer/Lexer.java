package toyir.ast.lexer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class Lexer {
    private String src;
    private Token buffer;

    private boolean isIdentFirst(char ch) {
        return ch == '_' ||
            ('a' <= ch && ch <= 'z') || 
            ('A' <= ch && ch <= 'Z');
    }

    private boolean isIdentChar(char ch) {
        return ch == '_' ||
            ('a' <= ch && ch <= 'z') || 
            ('A' <= ch && ch <= 'Z') ||
            ('0' <= ch && ch <= '9');
    }

    private boolean isDigit(char ch) {
        return '0' <= ch && ch <= '9';
    }

    public static Lexer fromFile(Path path, Charset charset) throws IOException {
        byte[] data = Files.readAllBytes(path);
        Lexer lexer = new Lexer();

        String srcText = new String(data, charset);
        // skip whitespace
        int start = 0;
        while (start < srcText.length() &&
            Character.isWhitespace(srcText.charAt(start))) {
            ++start;
        }
        lexer.src = srcText.substring(start);
        lexer.buffer = null;
        return lexer;
    }

    public boolean hasNext() {
        return src.length() != 0 || buffer != null;
    }

    public Token lookAhead() {
        if (buffer != null) {
            return buffer;
        } else {
            buffer = nextToken();
            return buffer;
        }
    }

    public Token nextToken() {
        if (buffer != null) {
            Token tmp = buffer;
            buffer = null;
            return tmp;
        }

        char first = src.charAt(0);

        int end = 1;
        Token tok;
        if (first == '@' ||
            first == '%' ||
            isIdentFirst(first)) {
            // Name token
            while (end < src.length() && 
                isIdentChar(src.charAt(end))) {
                ++end;
            }
            tok = new Name(src.substring(0, end));
        } else if (first == '+' ||
            first == '-' ||
            isDigit(first)) {
            // Const token
            while (end < src.length()) {
                if (isDigit(src.charAt(end)))
                    ++end;
                else
                    break;
            }
            tok = new Const(Integer.parseInt(src.substring(0, end)));
        } else {
            // Symbol token
            tok = new Symbol(first);
        }

        // skip whitespace
        while (end < src.length() &&
            Character.isWhitespace(src.charAt(end))) {
            ++end;
        }
        src = src.substring(end);
        return tok;
    }
}
