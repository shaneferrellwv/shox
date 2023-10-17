package edu.ufl.cise.plcsp23;
import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public abstract class ASTNode {
    public final IToken firstToken;
    
    public ASTNode(IToken firstToken) {
        this.firstToken = firstToken;
    }
    
    public abstract Object visit(ASTVisitor v, Object arg) throws PLCException;
    
    public IToken getFirstToken() {
        return firstToken;
    }
    
    public int getLine() {
        return firstToken.getSourceLocation().line();
    }
    
    public int getColumn() {
        return firstToken.getSourceLocation().column();
    }
    
    // @Override
    // public int hashCode() {
        // return Objects.hash(firstToken);
    // }
    
    // @Override
    // public boolean equals(Object obj) {
        // if (this == obj)
            // return true;
        // if (obj == null)
            // return false;
        // if (getClass() != obj.getClass())
            // return false;
        // ASTNode other = (ASTNode) obj;
        // return Objects.equals(firstToken, other.firstToken);
    // }
    
    @Override
    public String toString() {
        return "ASTNode [" + (firstToken != null ? "firstToken=" + firstToken : "") + "]";
    }
}
