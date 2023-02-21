package edu.ufl.cise.plcsp23;


/**
 * Write a description of class Parser here.
 *
 * @author Shane Ferrell
 * @version Feb 21, 2023
 */

import java.util.ArrayList;

public class Parser implements IParser
{
    private final ArrayList<Token> tokens;
    private int current = 0;
    
    Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }
    
    @Override
    public AST parse() throws PLCException {
        return expression();
    }
    
    private Expr expression() {
        Expr expr = comparison();

        while (match(IToken.Kind.RES_if)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        
        return expr;
    }
    
    private Expr comparison() {
        
    }
    
    private boolean match(ArrayList<IToken.Kind> kinds) {
        for (IToken.Kind kind : kinds) {
            if (check(kind)) {
                advance();
                return true;
            }
        }
        return false;
    }
    
    private boolean check(IToken.Kind kind) {
        if (isAtEnd()) 
            return false;
        return peek().getKind() == kind;
    }
    
    private Token advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }  
    
    private boolean isAtEnd() {
        return peek().getKind() == IToken.Kind.EOF;
    }
    
    private Token peek() {
        return tokens.get(current);
    }
    
    private Token previous() {
        return tokens.get(current - 1);
    }
}
