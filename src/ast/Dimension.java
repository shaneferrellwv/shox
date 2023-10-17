package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class Dimension extends AST {

	final Expr width;
	final Expr height;

	public Dimension(IToken firstToken, Expr width, Expr height) {
		super(firstToken);
		this.width = width;
		this.height = height;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitDimension(this, arg);
	}


	public Expr getWidth() {
		return width;
	}

	public Expr getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return "Dimension [width=" + width + ", height=" + height + "]";
	}

}
