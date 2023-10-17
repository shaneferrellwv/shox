package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.INumLitToken;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class NumLitExpr extends Expr {
	
	public NumLitExpr(IToken firstToken) {
		super(firstToken);
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitNumLitExpr(this,arg);
	}
	
	public int getValue() {
		return ((INumLitToken)firstToken).getValue();
	}

	@Override
	public String toString() {
		return "NumLitExpr [firstToken=" + firstToken + "]";
	}

	
}
