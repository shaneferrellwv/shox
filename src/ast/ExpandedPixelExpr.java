package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class ExpandedPixelExpr extends Expr {
	
	final Expr redExpr;
	final Expr grnExpr;
	final Expr bluExpr;		

	public ExpandedPixelExpr(IToken firstToken, Expr redExpr, Expr grnExpr, Expr bluExpr) {
		super(firstToken);
		this.redExpr = redExpr;
		this.grnExpr = grnExpr;
		this.bluExpr = bluExpr;
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitExpandedPixelExpr(this, arg);
	}


	public Expr getRedExpr() {
		return redExpr;
	}


	public Expr getGrnExpr() {
		return grnExpr;
	}


	public Expr getBluExpr() {
		return bluExpr;
	}


	@Override
	public String toString() {
		return "ExpandedPixelExpr [redExpr=" + redExpr + ", grnExpr=" + grnExpr + ", bluExpr=" + bluExpr + "]";
	}

	
}
