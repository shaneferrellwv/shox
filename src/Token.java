
/**
 * Write a description of class Token here.
 *
 * @author Shane Ferrell
 * @version Feb 4, 2023
 */

package edu.ufl.cise.plcsp23;
public class Token implements IToken {
    // class variables
    final Kind kind;
    final int pos;
    final int length;
    final int line;
    final int column;
    final char[] source;
    
    //constructor initializes final fields
    public Token(Kind kind, int pos, int length, int line, int column, char[] source) {
        super();
        this.kind = kind;
        this.pos = pos;
        this.length = length;
        this.line = line;
        this.column = column;
        this.source = source;
    }
    
    public SourceLocation getSourceLocation() {
        return new SourceLocation(line, column);
    }
    
    public Kind getKind() {
        return this.kind;
    }
    
    //returns the characters from the source belonging to the token
    public String getTokenString() {
        String s = "";
        for (int i = pos; i < pos + length; i++)
            s += source[i];
        return s;
    }
    
    //prints token, used during development
    @Override public String toString() {
        System.out.print(getTokenString());
        return getTokenString();
    }

}
