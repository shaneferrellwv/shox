package edu.ufl.cise.plcsp23.ast;

import java.util.Objects;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class AssignmentStatement extends Statement {

	final LValue lv;
	final Expr e;

	public AssignmentStatement(IToken firstToken, LValue lv, Expr e) {
		super(firstToken);
		this.lv = lv;
		this.e = e;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitAssignmentStatement(this, arg);
	}

	public LValue getLv() {
		return lv;
	}

	public Expr getE() {
		return e;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(e, lv);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssignmentStatement other = (AssignmentStatement) obj;
		return Objects.equals(e, other.e) && Objects.equals(lv, other.lv);
	}

	@Override
	public String toString() {
		return "AssignmentStatement [lv=" + lv + ", e=" + e + "]";
	}

}
