package edu.ufl.cise.plcsp23;


/**
 * Write a description of class Parser here.
 *
 * @author Shane Ferrell
 * @version Feb 21, 2023
 */

import java.util.List;
import java.util.Arrays;
import edu.ufl.cise.plcsp23.SyntaxException;

public class Parser implements IParser
{
    private final List<IToken> tokens;
    private int current = 0;
    private IToken firstToken;
    
    Parser(List<IToken> tokens) {
        this.tokens = tokens;
        firstToken = tokens.get(0);
        //System.out.println(tokens);
    }
    
    @Override
    public AST parse() throws PLCException {
        if (firstToken.getKind() == IToken.Kind.EOF)
            throw new SyntaxException("No empty expressions allowed");
        
        return expression();
    }
    
    private Expr expression() throws SyntaxException {
        if (match(Arrays.asList(IToken.Kind.RES_if))) {
            return conditional();
        }
        
        return or();
    }
    
    private Expr conditional() throws SyntaxException {
        Expr guard = expression();
        //System.out.println(guard);
        consume(IToken.Kind.QUESTION, "Expect '?' after expression.");
        Expr trueCase = expression();
        //System.out.println(trueCase);
        consume(IToken.Kind.QUESTION, "Expect '?' after expression.");
        Expr falseCase = expression();
        //System.out.println(falseCase);
        return new ConditionalExpr(firstToken, guard, trueCase, falseCase);
    }
    
    private Expr or() throws SyntaxException {
        Expr expr = and();
        
        while (match(Arrays.asList(IToken.Kind.OR, IToken.Kind.BITOR))) {
            IToken operator = previous();
            Expr right = and();
            expr = new BinaryExpr(firstToken, expr, operator, right);
        }

        return expr;
    }
    
    private Expr and() throws SyntaxException {
        Expr expr = comparison();
        
        while (match(Arrays.asList(IToken.Kind.AND, IToken.Kind.BITAND))) {
            IToken operator = previous();
            Expr right = comparison();
            expr = new BinaryExpr(firstToken, expr, operator, right);
        }

        return expr;
    }
    
    private Expr comparison() throws SyntaxException {
        Expr expr = power();
        
        while (match(Arrays.asList(IToken.Kind.LT, IToken.Kind.GT, IToken.Kind.EQ, IToken.Kind.LE, IToken.Kind.GE))) {
            IToken operator = previous();
            Expr right = power();
            expr = new BinaryExpr(firstToken, expr, operator, right);
        }

        return expr;
    }
    
    private Expr power() throws SyntaxException {
        Expr expr = additive();
        
        if (match(Arrays.asList(IToken.Kind.EXP))) {
            IToken operator = previous();
            Expr right = power();
            expr = new BinaryExpr(firstToken, expr, operator, right);
        }

        return expr;
    }
    
    private Expr additive() throws SyntaxException {
        Expr expr = multiplicative();
        
        while (match(Arrays.asList(IToken.Kind.PLUS, IToken.Kind.MINUS))) {
            IToken operator = previous();
            Expr right = multiplicative();
            expr = new BinaryExpr(firstToken, expr, operator, right);
        }

        return expr;
    }
    
    private Expr multiplicative() throws SyntaxException {
        Expr expr = unary();
        
        while (match(Arrays.asList(IToken.Kind.TIMES, IToken.Kind.DIV, IToken.Kind.MOD))) {
            IToken operator = previous();
            Expr right = unary();
            expr = new BinaryExpr(firstToken, expr, operator, right);
        }

        return expr;
    }
    
    private Expr unary() throws SyntaxException {
        if (match(Arrays.asList(IToken.Kind.BANG, IToken.Kind.MINUS, IToken.Kind.RES_sin, IToken.Kind.RES_cos, IToken.Kind.RES_atan))) {
            IToken operator = previous();
            Expr right = unary();
            return new UnaryExpr(firstToken, operator, right);
        }
        
        return primary();
    }
    
    private Expr primary() throws SyntaxException {
        if (match(Arrays.asList(IToken.Kind.STRING_LIT))) {
            return new StringLitExpr(previous());
        }
        if (match(Arrays.asList(IToken.Kind.NUM_LIT))) {
            return new NumLitExpr(previous());
        }
        if (match(Arrays.asList(IToken.Kind.IDENT))) {
            return new IdentExpr(previous());
        }
        if (match(Arrays.asList(IToken.Kind.RES_Z))) {
            return new ZExpr(previous());
        }
        if (match(Arrays.asList(IToken.Kind.RES_rand))) {
            return new RandomExpr(previous());
        }
        // parentheses/grouping
        if (match(Arrays.asList(IToken.Kind.LPAREN))) {
            Expr expr = expression();
            consume(IToken.Kind.RPAREN, "Expect ')' after expression.");
            return expr;
        }
        
        throw new SyntaxException("Invalid expression");
    }
    
    private IToken consume(IToken.Kind kind, String message) throws SyntaxException {
        if (check(kind))
            return advance();
            
        throw new SyntaxException(message);
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
    
    private IToken advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }
    
    private boolean isAtEnd() {
        return peek().getKind() == IToken.Kind.EOF;
    }
    
    private IToken peek() {
        return tokens.get(current);
    }
    
    private IToken previous() {
        return tokens.get(current - 1);
    }
}
