package edu.ufl.cise.plcsp23;


/**
 * Write a description of class Parser here.
 *
 * @author Shane Ferrell
 * @version Feb 21, 2023
 */

import java.util.List;
import java.util.Arrays;

public class Parser implements IParser
{
    private final List<Token> tokens;
    private int current = 0;
    private Token firstToken;
    
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
    
    @Override
    public AST parse() throws PLCException {
        return expression();
    }
    
    private Expr expression() { 
        Expr expr;
        
        if (match(Arrays.asList(IToken.Kind.RES_if))) {
            return conditional();
        }
        
        expr = or();
        
        return expr;
    }
    
    private Expr or() {
        Expr expr = and();
        
        while (match(Arrays.asList(IToken.Kind.BITOR, IToken.Kind.OR))) {
            Token operator = previous();
            Expr right = and();
            expr = new BinaryExpr(this.tokens.get(current), expr, operator.kind, right);
        }
        
        return expr;
    }
    
    private Expr and() {
        Expr expr = comparison();
        
        while (match(Arrays.asList(IToken.Kind.BITAND, IToken.Kind.AND))) {
            Token operator = previous();
            Expr right = comparison();
            expr = new BinaryExpr(this.tokens.get(current), expr, operator.kind, right);
        }
        
        return expr;
    }
    
    private Expr comparison() {
        Expr expr = power();
        
        while (match(Arrays.asList(IToken.Kind.LT, IToken.Kind.GT, IToken.Kind.EQ, IToken.Kind.LE, IToken.Kind.GE))) {
            Token operator = previous();
            Expr right = power();
            expr = new BinaryExpr(this.tokens.get(current), expr, operator.kind, right);
        }
        
        return expr;
    }
    
    private Expr power() {
        Expr expr = additive();
        
        while (match(Arrays.asList(IToken.Kind.EXP))) {
            Token operator = previous();
            Expr right = additive();
            expr = new BinaryExpr(this.tokens.get(current), expr, operator.kind, right);
        }
        
        return expr;
    }
    
    private Expr additive() {
        Expr expr = multiplicative();
        
        while (match(Arrays.asList(IToken.Kind.PLUS, IToken.Kind.MINUS))) {
            Token operator = previous();
            Expr right = multiplicative();
            expr = new BinaryExpr(this.tokens.get(current), expr, operator.kind, right);
        }
        
        return expr;
    }
    
    private Expr multiplicative() {
        Expr expr = unary();
        
        while (match(Arrays.asList(IToken.Kind.TIMES, IToken.Kind.DIV, IToken.Kind.MOD))) {
            Token operator = previous();
            Expr right = unary();
            expr = new BinaryExpr(this.tokens.get(current), expr, operator.kind, right);
        }
        
        return expr;
    }
    
    private Expr unary() {
        if (match(Arrays.asList(IToken.Kind.BANG, IToken.Kind.MINUS, IToken.Kind.RES_sin, IToken.Kind.RES_cosin, IToken.Kind.RES_atan))) {
          Token operator = previous();
          Expr right = unary();
          return new UnaryExpr(this.tokens.get(current), operator.kind, right);
        }
    
        return primary();
    }
    
    private Expr primary() {
        if (match(Arrays.asList(IToken.Kind.STRING_LIT)))
            return new StringLitExpr(this.tokens.get(current));
            
        if (match(Arrays.asList(IToken.Kind.NUM_LIT)))
            return new NumLitExpr(this.tokens.get(current));
            
        if (match(Arrays.asList(IToken.Kind.IDENT)))
            return new IdentExpr(this.tokens.get(current));
        
        if (match((Arrays.asList(IToken.Kind.LPAREN)))) {
            Expr expr = expression();
            consume(IToken.Kind.RPAREN, "Expect ')' after expression.");
            return expression();
        }
        
        if (match(Arrays.asList(IToken.Kind.RES_Z)))
            return new ZExpr(this.tokens.get(current));
            
        if (match(Arrays.asList(IToken.Kind.RES_rand)))
            return new RandomExpr(this.tokens.get(current));
    }
    
    private Expr conditional() {
        
    }
    
    
    
    private boolean match(List<IToken.Kind> kinds) {
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
    
    private Token consume(IToken.Kind kind, String message) throws SyntaxException {
        if (check(kind))
            return advance();
        
        throw SyntaxException(message);
    }
}
