package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.ast.ConditionalExpr;
import edu.ufl.cise.plcsp23.ast.Declaration;
import edu.ufl.cise.plcsp23.ast.Dimension;
import edu.ufl.cise.plcsp23.ast.ExpandedPixelExpr;
import edu.ufl.cise.plcsp23.ast.Ident;
import edu.ufl.cise.plcsp23.ast.AssignmentStatement;
import edu.ufl.cise.plcsp23.ast.BinaryExpr;
import edu.ufl.cise.plcsp23.ast.Block;
import edu.ufl.cise.plcsp23.ast.UnaryExpr;
import edu.ufl.cise.plcsp23.ast.UnaryExprPostfix;
import edu.ufl.cise.plcsp23.ast.WhileStatement;
import edu.ufl.cise.plcsp23.ast.WriteStatement;
import edu.ufl.cise.plcsp23.ast.StringLitExpr;
import edu.ufl.cise.plcsp23.ast.NumLitExpr;
import edu.ufl.cise.plcsp23.ast.PixelFuncExpr;
import edu.ufl.cise.plcsp23.ast.PixelSelector;
import edu.ufl.cise.plcsp23.ast.PredeclaredVarExpr;
import edu.ufl.cise.plcsp23.ast.Program;
import edu.ufl.cise.plcsp23.ast.IdentExpr;
import edu.ufl.cise.plcsp23.ast.LValue;
import edu.ufl.cise.plcsp23.ast.NameDef;
import edu.ufl.cise.plcsp23.ast.ZExpr;
import edu.ufl.cise.plcsp23.ast.RandomExpr;
import edu.ufl.cise.plcsp23.ast.ReturnStatement;

public class ASTVisitor { //change back to interface later
	public Object visitAssignmentStatement(AssignmentStatement statementAssign, Object arg) throws PLCException{return arg;};

	public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCException{return arg;};

	public Object visitBlock(Block block, Object arg) throws PLCException{return arg;};

	public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCException{return arg;};

	public Object visitDeclaration(Declaration declaration, Object arg) throws PLCException{return arg;};

	public Object visitDimension(Dimension dimension, Object arg) throws PLCException{return arg;};

	public Object visitExpandedPixelExpr(ExpandedPixelExpr expandedPixelExpr, Object arg) throws PLCException{return arg;};

	public Object visitIdent(Ident ident, Object arg) throws PLCException{return arg;};

	public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCException{return arg;};

	public Object visitLValue(LValue lValue, Object arg) throws PLCException{return arg;};

	public Object visitNameDef(NameDef nameDef, Object arg) throws PLCException{return arg;};

	public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCException{return arg;};

	public Object visitPixelFuncExpr(PixelFuncExpr pixelFuncExpr, Object arg) throws PLCException{return arg;};

	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws PLCException{return arg;};

	public Object visitPredeclaredVarExpr(PredeclaredVarExpr predeclaredVarExpr, Object arg) throws PLCException{return arg;};

	public Object visitProgram(Program program, Object arg) throws PLCException{return arg;};

	public Object visitRandomExpr(RandomExpr randomExpr, Object arg) throws PLCException{return arg;};

	public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws PLCException{return arg;};

	public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws PLCException{return arg;};

	public Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws PLCException{return arg;};

	public Object visitUnaryExprPostFix(UnaryExprPostfix unaryExprPostfix, Object arg) throws PLCException{return arg;};

	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws PLCException{return arg;};

	public Object visitWriteStatement(WriteStatement statementWrite, Object arg) throws PLCException{return arg;};

	public Object visitZExpr(ZExpr zExpr, Object arg) throws PLCException{return arg;};
}