package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IStringLitToken;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class StringLitExpr extends Expr {
	
	
	public StringLitExpr(IToken firstToken) {
		super(firstToken);
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitStringLitExpr(this,arg);
	}
	
	public String getValue() {
		return ((IStringLitToken)firstToken).getValue();
	}

	@Override
	public String toString() {
		return "StringLitExpr [firstToken=" + firstToken + "]";
	}
	
	

}
