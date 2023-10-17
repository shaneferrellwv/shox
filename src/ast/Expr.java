package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ast.AST;
import edu.ufl.cise.plcsp23.IToken;

public abstract class Expr extends AST {
	
	Type type;

	public Expr(IToken firstToken) {
		super(firstToken);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	

}
