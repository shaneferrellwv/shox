package edu.ufl.cise.plcsp23;


/**
 * Write a description of class NumLitToken here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class NumLitToken implements INumLitToken
{
    // class variables
    final Kind kind;
    final int pos;
    final int length;
    final char[] source;
    final int line;
    final int column;
    
    public NumLitToken(Kind kind, int pos, int length, int line, int column, char[] source) {
        super();
        this.kind = kind;
        this.pos = pos;
        this.length = length;
        this.line = line;
        this.column = column;
        this.source = source;
    }
    
    @Override
    public String getTokenString() {
        String digits = "";
        for (int i = pos; i < pos + length; i++)
            digits = digits + source[i];
        return digits;
    }
    
    @Override
    public int getValue() {
        try {
            return Integer.parseInt(getTokenString());
        }
        catch(NumberFormatException e) {
            throw new NumberFormatException("Number format error");
        }
    }
    
    @Override
    public Kind getKind() {
        return this.kind;
    }
    
    @Override
    public SourceLocation getSourceLocation() {
        return new SourceLocation(line, column);
    }
}
