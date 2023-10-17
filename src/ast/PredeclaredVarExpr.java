package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.IToken.Kind;
import edu.ufl.cise.plcsp23.PLCException;

public class PredeclaredVarExpr extends Expr {

	public PredeclaredVarExpr(IToken firstToken) {
		super(firstToken);
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitPredeclaredVarExpr(this,arg);
	}
	
	public Kind getKind() {
		return getFirstToken().getKind();
	}

	@Override
	public String toString() {
		return "PredeclaredVarExpr [getKind()=" + getKind() + ", getType()=" + getType() + "]";
	}

}
