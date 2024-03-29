package edu.ufl.cise.plcsp23.ast;

import java.util.Objects;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.IToken.Kind;
import edu.ufl.cise.plcsp23.PLCException;

public class BinaryExpr extends Expr {
	
	Expr left;
	Kind op;
	Expr right;



	public BinaryExpr(IToken firstToken, Expr left, Kind op, Expr right) {
		super(firstToken);
		this.left = left;
		this.op = op;
		this.right = right;		
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitBinaryExpr(this, arg);
	}


	public Expr getLeft() {
		return left;
	}


	public Kind getOp() {
		return op;
	}


	public Expr getRight() {
		return right;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(left, op, right);
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
		BinaryExpr other = (BinaryExpr) obj;
		return Objects.equals(left, other.left) && op == other.op && Objects.equals(right, other.right);
	}


	@Override
	public String toString() {
		return "BinaryExpr [left=" + left + ", op=" + op + ", right=" + right + "]";
	}
	
	

}
