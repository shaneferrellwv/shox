package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class NameDef extends AST {
	
	public final Type type;
	public final Dimension dimension;
	final Ident ident;
	
	public NameDef(IToken firstToken, Type type, Dimension dimension, Ident ident) {
		super(firstToken);
		this.type = type;
		this.dimension = dimension;
		this.ident = ident;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitNameDef(this, arg);
	}

	public Type getType() {
		return type;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public Ident getIdent() {
		return ident;
	}

	@Override
	public String toString() {
		return "NameDef [type=" + type + ", dimension=" + dimension + ", ident=" + ident + "]";
	}
	
}
