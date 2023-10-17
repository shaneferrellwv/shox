package edu.ufl.cise.plcsp23;


/**
 * Write a description of class StringLitToken here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class StringLitToken implements IStringLitToken
{
    // class variables
    final Kind kind;
    final int pos;
    final int length;
    final char[] source;
    final int line;
    final int column;
    
    public StringLitToken(Kind kind, int pos, int length, int line, int column, char[] source) {
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
        String s = "";
        for (int i = pos; i < pos + length; i++)
            s += source[i];
        return s;
    }
    
    @Override
    public String getValue() {
        String s = "";
        for (int i = pos + 1; i < pos + length - 1; i++)
            s += source[i];
        String q = "";
        for (int i = 0; i < length - 2; i++) {
            if ((int)s.charAt(i) == 92) {
                if ((int)s.charAt(i+1) == 116) {
                    q += "\t"; 
                    i++;
                }
                else if ((int)s.charAt(i+1) == 110) {
                    q += "\n"; 
                    i++;
                }
                else if ((int)s.charAt(i+1) == 98) {
                    q += "\b"; 
                    i++;
                }
                else if ((int)s.charAt(i+1) == 114) {
                    q += "\r"; 
                    i++;
                }
            }
            else
                q += s.charAt(i);
        }
        return q;
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
