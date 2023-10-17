package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class LValue extends AST {

	final Ident ident;
	final PixelSelector pixelSelector;
	final ColorChannel color;
	Type type;

	public LValue(IToken firstToken, Ident ident, PixelSelector pixelSelector, ColorChannel color) {
		super(firstToken);
		this.ident = ident;
		this.pixelSelector = pixelSelector;
		this.color = color;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitLValue(this, arg);
	}

	public Ident getIdent() {
		return ident;
	}

	public PixelSelector getPixelSelector() {
		return pixelSelector;
	}

	public ColorChannel getColor() {
		return color;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return "LValue [ident=" + ident + ", pixelSelector=" + pixelSelector + ", color=" + color + "]";
	}

}
