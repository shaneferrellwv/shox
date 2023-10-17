package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.IToken.Kind;
import edu.ufl.cise.plcsp23.ast.*;
import java.util.List;

public class TypeChecker extends ASTVisitor { // change back to implements

    SymbolTable symbolTable = new SymbolTable();

    TypeChecker() {

    }

    private void check(boolean condition, AST node, String message) throws TypeCheckException {
        if (!condition) {
            throw new TypeCheckException(message);
        }
    }

    private boolean isNotYetDeclaredInThisScope(String name) throws TypeCheckException {
        if (symbolTable.lookupInThisScope(name) == null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws PLCException {
        stringLitExpr.setType(Type.STRING);
        return Type.STRING;
    }

    @Override
    public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCException {
        numLitExpr.setType(Type.INT);
        return Type.INT;
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws PLCException {
        List<NameDef> params = program.getParamList();
        for (AST node : params) {
            node.visit(this, arg);
        }
        program.getBlock().visit(this, program);
        return program;
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCException {
        if (nameDef.getDimension() != null) {
            nameDef.getDimension().visit(this, arg);
            check(nameDef.getType() == Type.IMAGE, nameDef, "incompatible types for dimension");
        }
        check(isNotYetDeclaredInThisScope(nameDef.getIdent().getName()), nameDef,
                "identifier previously delcared in this scope");
        check(nameDef.type != Type.VOID, nameDef, "type cannot be void");
        return symbolTable.insert(nameDef.getIdent().getName(), nameDef);
    }

    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws PLCException {
        check((Type) dimension.getWidth().visit(this, arg) == Type.INT, dimension,
                "dimension expression0 type must be int"); // Expr0 is properly typed
        check((Type) dimension.getHeight().visit(this, arg) == Type.INT, dimension,
                "dimension expression1 type must be int"); // Expr1 is properly typed
        return dimension;
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws PLCException {
        List<Declaration> decList = block.getDecList();
        for (AST declaration : decList) {
            declaration.visit(this, arg);
        }
        List<Statement> statementList = block.getStatementList();
        for (AST statement : statementList) {
            statement.visit(this, arg);
        }
        return block;
    }

    private boolean declarationAssignmentCompatible(Type targetType, Type rhsType) {
        if (targetType == rhsType) {
            return true;
        } else if (targetType == Type.IMAGE) {
            return (rhsType == Type.PIXEL || rhsType == Type.STRING);
        } else if (targetType == Type.PIXEL) {
            return (rhsType == Type.INT || rhsType == Type.PIXEL);
        } else if (targetType == Type.INT) {
            return (rhsType == Type.PIXEL);
        } else if (targetType == Type.STRING) {
            return (rhsType == Type.INT || rhsType == Type.PIXEL || rhsType == Type.STRING || rhsType == Type.IMAGE);
        } else
            return false;
    }

    @Override
    public Object visitDeclaration(Declaration declaration, Object arg) throws PLCException {
        declaration.getNameDef().visit(this, arg); // check if nameDef is properly typed by visiting
        String definedName = declaration.getNameDef().getIdent().getName();
        Type nameDefType = declaration.getNameDef().getType();
        if (declaration.getInitializer() != null) {
            declaration.getInitializer().visit(this, definedName);
            Type expressionType = declaration.getInitializer().getType(); // expr is properly typed
            check(declarationAssignmentCompatible(nameDefType, expressionType), // Expr.type is assignment compatible with NameDef.type
                    declaration, "incompatible types in assignment");
        }
        if (nameDefType == Type.IMAGE) {
            check(declaration.getInitializer() != null || declaration.getNameDef().dimension != null, declaration,
                    "type image must have initializer or dimension"); // If NameDef.Type == image then either it has an initializer (Expr != null) or NameDef.dimension != null, or both.
        }
        return nameDefType;
    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws PLCException {
        Type expressionType = (Type) returnStatement.getE().visit(this, arg); // expr is properly typed
        check(declarationAssignmentCompatible(((Program) arg).getType(), expressionType), // Expr.type is assignment
                // compatible with Program.type
                returnStatement, "incompatible types in assignment");
        return expressionType;
    }

    @Override
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws PLCException {
        check((Type) whileStatement.getGuard().visit(this, arg) == Type.INT, whileStatement,
                "while statement expression must be of type int");
        symbolTable.enterScope();
        whileStatement.getBlock().visit(this, arg);
        symbolTable.exitScope();
        return whileStatement;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCException {
        Kind op = binaryExpr.getOp();
        Type leftType = (Type) binaryExpr.getLeft().visit(this, arg);
        Type rightType = (Type) binaryExpr.getRight().visit(this, arg);
        Type resultType = null;
        switch (op) {// AND, OR, PLUS, MINUS, TIMES, DIV, MOD, EQUALS, NOT_EQUALS, LT, LE, GT,GE
            case BITAND, BITOR -> {
                check(leftType == Type.PIXEL && rightType == Type.PIXEL, binaryExpr,
                        "incompatible types for comparison");
                resultType = Type.PIXEL;
            }
            case OR, AND -> {
                check(leftType == Type.INT && rightType == Type.INT, binaryExpr, "incompatible types for comparison");
                resultType = Type.INT;
            }
            case LE, GE, LT, GT -> {
                check(leftType == Type.INT && rightType == Type.INT, binaryExpr, "incompatible types for comparison");
                resultType = Type.INT;
            }
            case EQ -> {
                check((leftType == Type.INT && rightType == Type.INT)
                        || (leftType == Type.PIXEL && rightType == Type.PIXEL)
                        || (leftType == Type.IMAGE && rightType == Type.IMAGE)
                        || (leftType == Type.STRING && rightType == Type.STRING), binaryExpr,
                        "incompatible types for comparison");
                resultType = Type.INT;
            }
            case EXP -> {
                check((leftType == Type.INT && rightType == Type.INT)
                        || (leftType == Type.PIXEL && rightType == Type.INT), binaryExpr,
                        "incompatible types for comparison");
                resultType = leftType;
            }
            case PLUS -> {
                check((leftType == Type.INT && rightType == Type.INT)
                        || (leftType == Type.PIXEL && rightType == Type.PIXEL)
                        || (leftType == Type.IMAGE && rightType == Type.IMAGE)
                        || (leftType == Type.STRING && rightType == Type.STRING), binaryExpr,
                        "incompatible types for comparison");
                resultType = leftType;
            }
            case MINUS -> {
                check((leftType == Type.INT && rightType == Type.INT)
                        || (leftType == Type.PIXEL && rightType == Type.PIXEL)
                        || (leftType == Type.IMAGE && rightType == Type.IMAGE), binaryExpr,
                        "incompatible types for comparison");
                resultType = leftType;
            }
            case TIMES, DIV, MOD -> {
                check((leftType == Type.INT && rightType == Type.INT)
                        || (leftType == Type.PIXEL && rightType == Type.PIXEL)
                        || (leftType == Type.IMAGE && rightType == Type.IMAGE)
                        || (leftType == Type.PIXEL && rightType == Type.INT)
                        || (leftType == Type.IMAGE && rightType == Type.INT), binaryExpr,
                        "incompatible types for comparison");
                resultType = leftType;
            }
            default -> {
                throw new PLCException("compiler error");
            }
        }
        binaryExpr.setType(resultType);
        return resultType;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws PLCException {
        Kind op = unaryExpr.getOp();
        Type exprType = (Type) unaryExpr.getE().visit(this, arg);
        Type resultType = null;
        switch (op) {
            case BANG -> {
                check(exprType == Type.INT || exprType == Type.PIXEL, unaryExpr,
                        "incompatible types for uanry operation");
                resultType = exprType;
            }
            case MINUS, RES_sin, RES_cos, RES_atan -> {
                check(exprType == Type.INT, unaryExpr, "incompatible types for uanry operation");
                resultType = exprType;
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + op);
        }
        unaryExpr.setType(resultType);
        return unaryExpr.getType();
    }

    @Override
    public Object visitZExpr(ZExpr zExpr, Object arg) throws PLCException {
        zExpr.setType(Type.INT);
        return zExpr.getType();
    }

    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCException {
        check((Type) conditionalExpr.getGuard().visit(this, arg) == Type.INT, conditionalExpr,
                "conditional expression guard type must be int"); // Expr0 is properly typed
        check((Type) conditionalExpr.getTrueCase().visit(this, arg) == (Type) conditionalExpr.getFalseCase().visit(this,
                arg), conditionalExpr, "conditional expression true and false case types must match"); // Expr1.type ==
                                                                                                       // Expr2.type
        conditionalExpr.setType(conditionalExpr.getTrueCase().getType());
        return conditionalExpr.getType();
    }

    @Override
    public Object visitWriteStatement(WriteStatement statementWrite, Object arg) throws PLCException {
        statementWrite.getE().visit(this, arg); // Expr is properly typed
        return statementWrite;
    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws PLCException {
        Type lValType = (Type) assignmentStatement.getLv().visit(this, arg); // LValue is properly typed
        Type exprType = (Type) assignmentStatement.getE().visit(this, arg); // rhs expression is properly typed
        check(declarationAssignmentCompatible(lValType, exprType), assignmentStatement, "types incompatible for assignment");
        return assignmentStatement;
    }

    @Override
    public Object visitLValue(LValue lValue, Object arg) throws PLCException {
        check(symbolTable.lookup(lValue.getIdent().getName()) != null, lValue, "lValue must be declared and visible in this scope");
        lValue.getIdent().setType(symbolTable.lookup(lValue.getIdent().getName()).getType());
        if (lValue.getPixelSelector() != null) {
            if (symbolTable.lookup(lValue.getIdent().getName()).getType() == Type.PIXEL) {
                throw new TypeCheckException("Incompatible types in LValue");
            }
            lValue.getPixelSelector().visit(this, arg); // PixelSelector is properly typed, the grammar doesn't say to do this though??
            if (lValue.getColor() != null) {
                return Type.INT;
            }
        }
        if (lValue.getColor() != null) {
            // lValue.getColor().visit(this, arg); ColorChannel is properly typed (this is not in the grammar either???)
        }
        if (symbolTable.lookup(lValue.getIdent().getName()).getType() == Type.IMAGE) {
            lValue.getIdent().setType(Type.IMAGE);
            lValue.setType(Type.IMAGE);
            if (lValue.getPixelSelector() != null && lValue.getColor() == null) {
                return Type.PIXEL;
            } else if (lValue.getPixelSelector() != null && lValue.getColor() != null) {
                return Type.INT;
            }
        }
        else if (symbolTable.lookup(lValue.getIdent().getName()).getType() == Type.PIXEL)
            lValue.setType(Type.PIXEL);
        return symbolTable.lookup(lValue.getIdent().getName()).getType();
    }

    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCException {
        String name = identExpr.getName();
        NameDef nameDef = symbolTable.lookup(name);
        if (arg.equals(name))
            throw new TypeCheckException("Expression cannot refer to name being defined.");
        check(nameDef != null, identExpr, "undefined identifier " + name); // check if identExpr.name has been defined
        Type type = nameDef.getType();
        identExpr.setType(type);
        return type;
    }

    @Override
    public Object visitUnaryExprPostFix(UnaryExprPostfix unaryExprPostfix, Object arg) throws PLCException {
        if (unaryExprPostfix.getPixel() != null) {
            unaryExprPostfix.getPixel().visit(this, arg); // PixelSelector is properly typed
        }
        if (unaryExprPostfix.getColor() != null) {
            // unaryExprPostfix.getPixel().visit(this, arg); // ColorChannel is properly
            // typed (this is not in the grammar???)
        }
        unaryExprPostfix.getPrimary().visit(this, arg);
        if (unaryExprPostfix.getPrimary().getType() == Type.PIXEL) {
            check(unaryExprPostfix.getPixel() == null && unaryExprPostfix.getColor() != null, unaryExprPostfix,
                    "PixelSelector and ChannelSelector imcompatible with primary expresssion of type pixel");
            unaryExprPostfix.setType(Type.INT);
        } else if (unaryExprPostfix.getPrimary().getType() == Type.IMAGE) {
            if (unaryExprPostfix.getPixel() == null && unaryExprPostfix.getColor() != null) {
                unaryExprPostfix.setType(Type.IMAGE);
            } else if (unaryExprPostfix.getPixel() != null && unaryExprPostfix.getColor() == null) {
                unaryExprPostfix.setType(Type.PIXEL);
            } else if (unaryExprPostfix.getPixel() != null && unaryExprPostfix.getColor() != null) {
                unaryExprPostfix.setType(Type.INT);
            }
        }
        return unaryExprPostfix.getType();
    }

    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws PLCException {
        Type expXType = (Type) pixelSelector.getX().visit(this, arg); // Expr0 is properly typed
        Type expYType = (Type) pixelSelector.getY().visit(this, arg); // Expr1 is properly typed
        check(expXType == Type.INT && expYType == Type.INT, pixelSelector, "PixelSelector must be type int"); // Expr0
                                                                                                              // and
                                                                                                              // Expr1
                                                                                                              // types
                                                                                                              // == int
        return pixelSelector;
    }

    @Override
    public Object visitPixelFuncExpr(PixelFuncExpr pixelFuncExpr, Object arg) throws PLCException {
        pixelFuncExpr.getSelector().visit(this, arg);
        pixelFuncExpr.setType(Type.INT);
        return pixelFuncExpr.getType();
    }

    @Override
    public Object visitExpandedPixelExpr(ExpandedPixelExpr expandedPixelExpr, Object arg) throws PLCException {
        check((Type) expandedPixelExpr.getRedExpr().visit(this, arg) == Type.INT, expandedPixelExpr,
                "PixelSelector expression0 type must be int"); // Expr0 is properly typed
        check((Type) expandedPixelExpr.getGrnExpr().visit(this, arg) == Type.INT, expandedPixelExpr,
                "PixelSelector expression1 type must be int"); // Expr1 is properly typed
        check((Type) expandedPixelExpr.getBluExpr().visit(this, arg) == Type.INT, expandedPixelExpr,
                "PixelSelector expression2 type must be int"); // Expr2 is properly typed
        expandedPixelExpr.setType(Type.PIXEL);
        return expandedPixelExpr.getType();
    }

    @Override
    public Object visitPredeclaredVarExpr(PredeclaredVarExpr predeclaredVarExpr, Object arg) throws PLCException {
        predeclaredVarExpr.setType(Type.INT);
        return predeclaredVarExpr.getType();
    }

    @Override
    public Object visitRandomExpr(RandomExpr randomExpr, Object arg) throws PLCException {
        randomExpr.setType(Type.INT);
        return randomExpr.getType();
    }
}
