package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class WriteStatement extends Statement {

	final Expr e;

	public WriteStatement(IToken firstToken, Expr e) {
		super(firstToken);
		this.e = e;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitWriteStatement(this, arg);
	}

	public Expr getE() {
		return e;
	}

	@Override
	public String toString() {
		return "StatementWrite [e=" + e + "]";
	}

}
