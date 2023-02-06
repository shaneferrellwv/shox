
/**
 * Write a description of class Token here.
 *
 * @author Shane Ferrell
 * @version Feb 4, 2023
 */

package edu.ufl.cise.plcsp23;
public class Token {
    // class variables
    final Kind kind;
    final int pos;
    final int length;
    final char[] source;
    public record SourceLocation(int line, int column) {} // Record to represent the location in the source code
    
    //constructor initializes final fields
    public Token(Kind kind, int pos, int length, char[] source) {
        super();
        this.kind = kind;
        this.pos = pos;
        this.length = length;
        this.source = source;
    }
    
    public static enum Kind {
        IDENT,
        NUM_LIT,
        PLUS,
        TIMES,
        EQ,
        KW_IF,
        KW_ELSE,
        EOF,
        ERROR //may be useful
    }
    
    // public SourceLocation getSourceLocation()
    // {
        // return ;
    // }
    
    // public Kind getKind()
    // {
        // return this.kind;
    // }
    
    // returns the characters from the source belonging to the token
    // public String getTokenString()
    // {
        // return ;
    // }
    
    // prints token, used during development
    // @Override  public String toString()
    // {
        // return ;
    // }

}
