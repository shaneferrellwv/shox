package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.IToken;

public abstract class Statement extends AST {

	public Statement(IToken firstToken) {
		super(firstToken);
	}

}
