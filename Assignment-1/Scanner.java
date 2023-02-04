
/**
 * Write a description of class Scanner here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
 
import edu.ufl.cise.plcsp23;

public abstract class Scanner implements IScanner
{
    // Record to represent the location in the source code
    public record SourceLocation(int line, int column) {}
    
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
    final Kind kind;
    final int pos;
    final int length;
    final char[] source;
}
