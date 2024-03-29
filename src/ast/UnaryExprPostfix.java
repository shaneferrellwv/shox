package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class UnaryExprPostfix extends Expr {

	final Expr primary;
	final PixelSelector pixel;
	final ColorChannel color;

	public UnaryExprPostfix(IToken firstToken, Expr primary, PixelSelector pixel, ColorChannel color) {
		super(firstToken);
		this.primary = primary;
		this.pixel = pixel;
		this.color = color;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitUnaryExprPostFix(this, arg);
	}

	public Expr getPrimary() {
		return primary;
	}

	public PixelSelector getPixel() {
		return pixel;
	}

	public ColorChannel getColor() {
		return color;
	}

	@Override
	public String toString() {
		return "UnaryExprPostfix [primary=" + primary + ", pixel=" + pixel + ", color=" + color + "]";
	}

}
