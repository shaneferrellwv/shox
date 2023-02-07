package edu.ufl.cise.plcsp23;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import edu.ufl.cise.plcsp23.IToken.Kind;
class TestScanner {    

    //check that this token is the EOF token
    void checkEOF(IToken t) {
        assertEquals(Kind.EOF, t.getKind());
    }
    
    void checkToken(Kind expectedKind, IToken t) {
            assertEquals(expectedKind, t.getKind());
        }     
        
    @Test
    public void test0()
    {
    }

    @Test
    void emptyProg() throws LexicalException{
        String input = "";
        IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkEOF(scanner.next());
    }
    
    @Test
    void onlyWhiteSpace() throws LexicalException{
        String input = " \t \r\n \f \n";
        IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkEOF(scanner.next()); 
    }
        
    @Test
    void onlyBitAnd() throws LexicalException{
        String input = "&";
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.BITAND,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyAnd() throws LexicalException{
        String input = "&&";
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.AND,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void andWithSpaces() throws LexicalException{
        String input = "  && ";
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.AND,scanner.next());
        checkEOF(scanner.next()); 
    }

    @Test
    void onlyTwoDots() throws LexicalException{
        String input = "..";
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.DOT,scanner.next());
        checkToken(Kind.DOT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyDotComma() throws LexicalException{
        String input = ".,";
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.DOT,scanner.next());
        checkToken(Kind.COMMA,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyDotCommaAnd() throws LexicalException{
        String input = ".,&&";
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.DOT,scanner.next());
        checkToken(Kind.COMMA,scanner.next());
        checkToken(Kind.AND,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyDotBitandComma() throws LexicalException{
        String input = ".&,";
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.DOT,scanner.next());
        checkToken(Kind.BITAND,scanner.next());
        checkToken(Kind.COMMA,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyExchange() throws LexicalException{
        String input = "<->";
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.EXCHANGE,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyLessThanMinus() throws LexicalException{
        String input = "<-";
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.LT,scanner.next());
        checkToken(Kind.MINUS,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyLessThanMinusSpaceGreaterThan() throws LexicalException{
        String input = "<- >";
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.LT,scanner.next());
        checkToken(Kind.MINUS,scanner.next());
        checkToken(Kind.GT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyTimesNewLineExponent() throws LexicalException{
        String input = "*\n**";
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.TIMES,scanner.next());
        checkToken(Kind.EXP,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyTimesCarriageReturnTimes() throws LexicalException{
        String input = """
        *
        *
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.TIMES,scanner.next());
        checkToken(Kind.TIMES,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyTimesCarriageReturnExponent() throws LexicalException{
        String input = """
        *
        **
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.TIMES,scanner.next());
        checkToken(Kind.EXP,scanner.next());
        checkEOF(scanner.next()); 
    }
}




