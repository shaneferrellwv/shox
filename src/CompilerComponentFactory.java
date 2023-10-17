package edu.ufl.cise.plcsp23;

import java.util.List;
import java.util.ArrayList;

public class CompilerComponentFactory {
    public static IScanner makeScanner(String input) {
        // Add statement to return an instance of your scanner
        return new Scanner(input);
    }

    public static IParser makeAssignment2Parser(String input) throws LexicalException {
        // add code to create a scanner and parser and return the parser.
        IScanner scanner = new Scanner(input);
        List<IToken> tokens = new ArrayList<>();
        IToken t;
        do {
            t = scanner.next();
            tokens.add(t);
        } while (t.getKind() != IToken.Kind.EOF);
        return new Parser(tokens);
    }

    public static IParser makeParser(String input) throws LexicalException {
        // add code to create a scanner and parser and return the parser.
        IScanner scanner = new Scanner(input);
        List<IToken> tokens = new ArrayList<>();
        IToken t;
        do {
            t = scanner.next();
            tokens.add(t);
        } while (t.getKind() != IToken.Kind.EOF);
        return new Parser(tokens);
    }

    public static ASTVisitor makeTypeChecker() {
        return new TypeChecker();
    }

    public static ASTVisitor makeCodeGenerator(String packageName) {
        return new CodeGenerator();
    }
}
