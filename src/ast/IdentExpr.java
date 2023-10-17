package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class IdentExpr extends Expr {
		
	public IdentExpr(IToken firstToken) {
		super(firstToken);
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitIdentExpr(this,arg);
	}
	
	public String getName() {
		return firstToken.getTokenString();
	}

	@Override
	public String toString() {
		return "IdentExpr [firstToken=" + firstToken + "]";
	}
	

}
