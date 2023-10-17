package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class Ident extends AST {
	
	NameDef def;
	Type type;

	public Ident(IToken firstToken) {
		super(firstToken);
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitIdent(this, arg);
	}
	
	public String getName() {
		return firstToken.getTokenString();
	}

	public NameDef getDef() {
		return def;
	}

	public void setDef(NameDef def) {
		this.def = def;
	}

	@Override
	public String toString() {
		return "Ident [getName()=" + getName() + ", getDef()=" + getDef() + "]";
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return this.type;
	}

}
