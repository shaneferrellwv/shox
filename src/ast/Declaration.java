package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.ASTVisitor;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class Declaration extends AST {

	final NameDef nameDef;
	final Expr initializer;

	public Declaration(IToken firstToken, NameDef nameDef, Expr initializer) {
		super(firstToken);
		this.nameDef = nameDef;
		this.initializer = initializer;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitDeclaration(this, arg);
	}

	public NameDef getNameDef() {
		return nameDef;
	}

	public Expr getInitializer() {
		return initializer;
	}

	@Override
	public String toString() {
		return "Declaration [nameDef=" + nameDef + ", initializer=" + initializer + "]";
	}

}
