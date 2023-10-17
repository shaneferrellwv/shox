package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;
import edu.ufl.cise.plcsp23.IToken.Kind;

public class PixelFuncExpr extends Expr {
	
	final Kind function;
	final PixelSelector selector;
	
	public PixelFuncExpr(IToken firstToken, Kind function, PixelSelector selector) {
		super(firstToken);
		this.function = function;
		this.selector = selector;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitPixelFuncExpr(this,arg);
	}

	public Kind getFunction() {
		return function;
	}

	public PixelSelector getSelector() {
		return selector;
	}

	@Override
	public String toString() {
		return "PixelFuncExpr [function=" + function + ", selector=" + selector + "]";
	}
	
	

}
