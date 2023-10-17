package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class WhileStatement extends Statement {
	
	final Expr guard;
	final Block block;
	
	

	public WhileStatement(IToken firstToken, Expr guard, Block block) {
		super(firstToken);
		this.guard = guard;
		this.block = block;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitWhileStatement(this, arg);
	}

	public Expr getGuard() {
		return guard;
	}
	public Block getBlock() {
		return block;
	}

	@Override
	public String toString() {
		return "WhileStatement [guard=" + guard + ", block=" + block + "]";
	}

}
