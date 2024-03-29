package edu.ufl.cise.plcsp23.ast;

import java.util.Objects;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;
import edu.ufl.cise.plcsp23.IToken.Kind;

public class UnaryExpr extends Expr {
	
	final Kind op;
	final Expr e;	
	
	public UnaryExpr(IToken firstToken, Kind op, Expr e) {
		super(firstToken);
		this.op = op;
		this.e = e;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitUnaryExpr(this,arg);
	}

	
	public Kind getOp() {
		return op;
	}

	public Expr getE() {
		return e;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(e, op);
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
		UnaryExpr other = (UnaryExpr) obj;
		return Objects.equals(e, other.e) && op == other.op;
	}

	@Override
	public String toString() {
		return "UnaryExpr [op=" + op + ", e=" + e + "]";
	}

}
