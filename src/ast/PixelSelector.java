package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class PixelSelector extends AST {

	final Expr x;
	final Expr y;

	public PixelSelector(IToken firstToken, Expr x, Expr y) {
		super(firstToken);
		this.x = x;
		this.y = y;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitPixelSelector(this, arg);
	}

	public Expr getX() {
		return x;
	}

	public Expr getY() {
		return y;
	}

	@Override
	public String toString() {
		return "PixelSelector [x=" + x + ", y=" + y + "]";
	}




	
}
