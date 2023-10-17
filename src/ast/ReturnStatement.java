package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class ReturnStatement extends Statement {

	final Expr e;

	public ReturnStatement(IToken firstToken, Expr e) {
		super(firstToken);
		this.e = e;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitReturnStatement(this, arg);
	}

	public Expr getE() {
		return e;
	}

	@Override
	public String toString() {
		return "ReturnStatement [e=" + e + "]";
	}

}
