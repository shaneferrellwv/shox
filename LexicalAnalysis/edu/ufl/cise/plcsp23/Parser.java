package edu.ufl.cise.plcsp23;


/**
 * Write a description of class Parser here.
 *
 * @author Shane Ferrell
 * @version Feb 21, 2023
 */

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import edu.ufl.cise.plcsp23.SyntaxException;
import edu.ufl.cise.plcsp23.ast.AST;
import edu.ufl.cise.plcsp23.ast.BinaryExpr;
import edu.ufl.cise.plcsp23.ast.ConditionalExpr;
import edu.ufl.cise.plcsp23.ast.Expr;
import edu.ufl.cise.plcsp23.ast.IdentExpr;
import edu.ufl.cise.plcsp23.ast.NumLitExpr;
import edu.ufl.cise.plcsp23.ast.RandomExpr;
import edu.ufl.cise.plcsp23.ast.StringLitExpr;
import edu.ufl.cise.plcsp23.ast.UnaryExpr;
import edu.ufl.cise.plcsp23.ast.ZExpr;

import edu.ufl.cise.plcsp23.ast.Type;
import edu.ufl.cise.plcsp23.ast.UnaryExprPostfix;
import edu.ufl.cise.plcsp23.ast.PixelSelector;
import edu.ufl.cise.plcsp23.ast.ColorChannel;
import edu.ufl.cise.plcsp23.ast.PredeclaredVarExpr;
import edu.ufl.cise.plcsp23.ast.ExpandedPixelExpr;
import edu.ufl.cise.plcsp23.ast.PixelFuncExpr;
import edu.ufl.cise.plcsp23.ast.Ident;
import edu.ufl.cise.plcsp23.ast.NameDef;
import edu.ufl.cise.plcsp23.ast.Block;
import edu.ufl.cise.plcsp23.ast.Program;
import edu.ufl.cise.plcsp23.ast.Dimension;
import edu.ufl.cise.plcsp23.ast.Declaration;
import edu.ufl.cise.plcsp23.ast.Statement;
import edu.ufl.cise.plcsp23.ast.WriteStatement;
import edu.ufl.cise.plcsp23.ast.WhileStatement;
import edu.ufl.cise.plcsp23.ast.AssignmentStatement;
import edu.ufl.cise.plcsp23.ast.LValue;

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
            throw new SyntaxException("No empty programs allowed");
        
        return program();
    }
    
    private Program program() throws SyntaxException {
        Type type = type();
        advance();
        Ident ident = ident();
        consume(IToken.Kind.LPAREN, "Left parenthesis must follow identifier.");
        List<NameDef> paramList = paramList();
        consume(IToken.Kind.RPAREN, "Right parenthesis must follow list of parameters.");
        Block block = block();
        return new Program(firstToken, type, ident, paramList, block);
    }

    private Type type() throws SyntaxException {
        try {
            return Type.getType(tokens.get(current));
        }
        catch (RuntimeException e) {
            throw new SyntaxException("Unexpected token kind in Type");
        }
    }
    
    private Ident ident() throws SyntaxException {
        if (match(Arrays.asList(IToken.Kind.IDENT))) {
            return new Ident(previous());
        }
        else
            throw new SyntaxException("Unexpected token kind in Ident.");
    }
    
    private List<NameDef> paramList() throws SyntaxException {
        List<NameDef> params = new ArrayList<>();
        try {
            while (!check(IToken.Kind.RPAREN)) {
                Type type = type();
                advance();
                Dimension dimension = null;
                if (match(Arrays.asList(IToken.Kind.LSQUARE))) {
                    dimension = dimension();
                }
                Ident ident = new Ident(consume(IToken.Kind.IDENT, "Expected ident in name def."));
                params.add(new NameDef(firstToken, type, dimension, ident));
                consume(IToken.Kind.COMMA, "Expected comma in parameter list.");
            }
        }
        catch(SyntaxException e) {
            
        }
        finally {
            return params;
        }
    }
    
    private Dimension dimension() throws SyntaxException {
        Expr expr1 = expression();
        consume(IToken.Kind.COMMA, "Expect ',' in dimension.");
        Expr expr2 = expression();
        consume(IToken.Kind.RSQUARE, "Expect ']' in dimension.");
        return new Dimension(firstToken, expr1, expr2);
    }
    
    private Block block() throws SyntaxException {
        if (match(Arrays.asList(IToken.Kind.LCURLY))) {
            List<Declaration> decList = new ArrayList<>();
            List<Statement> statementList = new ArrayList<>();
            while (!check(IToken.Kind.RCURLY)) {
                if (check(IToken.Kind.IDENT)) {
                    IToken f = peek();
                    Ident ident = ident();
                    PixelSelector pixel = null;
                    ColorChannel color = null;
                    if (match(Arrays.asList(IToken.Kind.LSQUARE))) {
                        pixel = pixelSelector();
                    }
                    if (match(Arrays.asList(IToken.Kind.COLON))) {
                        color = colorChannel();
                        advance();
                    }
                    consume(IToken.Kind.ASSIGN, "Expected assignment operator in statement with LValue.");
                    LValue lval = new LValue(f, ident, pixel, color);
                    statementList.add(new AssignmentStatement(f, lval, expression()));
                    if (match(Arrays.asList(IToken.Kind.RSQUARE))) {
                    
                    }
                    consume(IToken.Kind.DOT, "Expected dot after statement.");
                }
                else if (match(Arrays.asList(IToken.Kind.RES_write))) {
                    statementList.add(new WriteStatement(previous(), expression()));
                    consume(IToken.Kind.DOT, "Expected dot after statement.");
                }
                else if (match(Arrays.asList(IToken.Kind.RES_while))) {
                    statementList.add(new WhileStatement(previous(), expression(), block()));
                }
                else if (match(Arrays.asList(IToken.Kind.RES_image, IToken.Kind.RES_pixel,
                         IToken.Kind.RES_int, IToken.Kind.RES_string, IToken.Kind.RES_void))) {
                    IToken first = previous();
                    
                    Dimension dimension = null;
                    if (match(Arrays.asList(IToken.Kind.LSQUARE))) {
                        dimension = dimension();
                    }
                    
                    NameDef nameDef = new NameDef(first, Type.getType(first), dimension, ident());
                    
                    if (match(Arrays.asList(IToken.Kind.DOT))) {
                        decList.add(new Declaration(first, nameDef, null));
                    }
                    else if (match(Arrays.asList(IToken.Kind.ASSIGN))) {
                        decList.add(new Declaration(first, nameDef, expression()));
                        advance();
                    }
                }
                else
                    break;
            }
            return new Block (firstToken, decList, statementList);
        }
        else
            throw new SyntaxException("Expected '{' in block.");
    }
    
    
    
    private Expr expression() throws SyntaxException {
        if (tokens.get(current).getKind() == IToken.Kind.EOF)
            throw new SyntaxException("No empty expressions allowed");
            
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
            IToken.Kind operator = previous().getKind();
            Expr right = and();
            expr = new BinaryExpr(firstToken, expr, operator, right);
        }

        return expr;
    }
    
    private Expr and() throws SyntaxException {
        Expr expr = comparison();
        
        while (match(Arrays.asList(IToken.Kind.AND, IToken.Kind.BITAND))) {
            IToken.Kind operator = previous().getKind();
            Expr right = comparison();
            expr = new BinaryExpr(firstToken, expr, operator, right);
        }

        return expr;
    }
    
    private Expr comparison() throws SyntaxException {
        Expr expr = power();
        
        while (match(Arrays.asList(IToken.Kind.LT, IToken.Kind.GT, IToken.Kind.EQ, IToken.Kind.LE, IToken.Kind.GE))) {
            IToken.Kind operator = previous().getKind();
            Expr right = power();
            expr = new BinaryExpr(firstToken, expr, operator, right);
        }

        return expr;
    }
    
    private Expr power() throws SyntaxException {
        Expr expr = additive();
        
        if (match(Arrays.asList(IToken.Kind.EXP))) {
            IToken.Kind operator = previous().getKind();
            Expr right = power();
            expr = new BinaryExpr(firstToken, expr, operator, right);
        }

        return expr;
    }
    
    private Expr additive() throws SyntaxException {
        Expr expr = multiplicative();
        
        while (match(Arrays.asList(IToken.Kind.PLUS, IToken.Kind.MINUS))) {
            IToken.Kind operator = previous().getKind();
            Expr right = multiplicative();
            expr = new BinaryExpr(firstToken, expr, operator, right);
        }

        return expr;
    }
    
    private Expr multiplicative() throws SyntaxException {
        Expr expr = unary();
        
        while (match(Arrays.asList(IToken.Kind.TIMES, IToken.Kind.DIV, IToken.Kind.MOD))) {
            IToken.Kind operator = previous().getKind();
            Expr right = unary();
            expr = new BinaryExpr(firstToken, expr, operator, right);
        }

        return expr;
    }
    
    private Expr unary() throws SyntaxException {
        Expr expr = unaryExprPostfix();
        
        if (match(Arrays.asList(IToken.Kind.BANG, IToken.Kind.MINUS, IToken.Kind.RES_sin, IToken.Kind.RES_cos, IToken.Kind.RES_atan))) {
            IToken.Kind operator = previous().getKind();
            Expr right = unary();
            return new UnaryExpr(firstToken, operator, right);
        }
        
        return expr;
    }
    
    private Expr unaryExprPostfix() throws SyntaxException {
        try {
            Expr expr = primary();
                
            PixelSelector pixel = null;
            ColorChannel color = null;
            
            if (match(Arrays.asList(IToken.Kind.LSQUARE))) {
                pixel = pixelSelector();
                if (match(Arrays.asList(IToken.Kind.COLON))) {
                    color = colorChannel();
                    advance();
                }
                
                return new UnaryExprPostfix(firstToken, expr, pixel, color);
            }
            
            return expr;
        }
        catch (SyntaxException e) {
            return null;
        }
    }
    
    private PixelSelector pixelSelector() throws SyntaxException {
        Expr expr1 = expression();
        consume(IToken.Kind.COMMA, "Expect ',' in PixelSelector.");
        Expr expr2 = expression();
        consume(IToken.Kind.RSQUARE, "Expect ']' in PixelSelector.");
        return new PixelSelector(firstToken, expr1, expr2);
    }
    
    private ColorChannel colorChannel() throws SyntaxException {
        try {
            return ColorChannel.getColor(tokens.get(current));
        }
        catch (RuntimeException e) {
            throw new SyntaxException("Unexpected token kind in ColorChannel.getColor");
        }
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
        // parentheses/grouping
        if (match(Arrays.asList(IToken.Kind.LPAREN))) {
            Expr expr = expression();
            consume(IToken.Kind.RPAREN, "Expect ')' after expression.");
            return expr;
        }
        if (match(Arrays.asList(IToken.Kind.RES_Z))) {
            return new ZExpr(previous());
        }
        if (match(Arrays.asList(IToken.Kind.RES_rand))) {
            return new RandomExpr(previous());
        }
        if (match(Arrays.asList(IToken.Kind.RES_x, IToken.Kind.RES_y,
                                IToken.Kind.RES_a, IToken.Kind.RES_r))) {
            return new PredeclaredVarExpr(previous());
        }
        if (match(Arrays.asList(IToken.Kind.LSQUARE))) {
            Expr expr1 = expression();
            consume(IToken.Kind.COMMA, "Expect ',' in PixelSelector.");
            Expr expr2 = expression();
            consume(IToken.Kind.COMMA, "Expect ',' in PixelSelector.");
            Expr expr3 = expression();
            consume(IToken.Kind.RSQUARE, "Expect ']' in PixelSelector.");
            return new ExpandedPixelExpr(firstToken, expr1, expr2, expr3);
        }
        if (match(Arrays.asList(IToken.Kind.RES_x_cart, IToken.Kind.RES_y_cart,
                                IToken.Kind.RES_a_polar, IToken.Kind.RES_r_polar))) {
            IToken.Kind function = previous().getKind();
            consume(IToken.Kind.LSQUARE, "Expect '[' in pixel function expression.");
            PixelSelector selector = pixelSelector();
            return new PixelFuncExpr(firstToken, function, selector);
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
