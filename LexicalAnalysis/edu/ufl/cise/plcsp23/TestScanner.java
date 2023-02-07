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
    
    @Test
    void onlyRowIdent() throws LexicalException{
        String input = "row";
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.IDENT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
     @Test
    void onlyTwoIdentsWithSpaces() throws LexicalException{
        String input = "row column";
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.IDENT,scanner.next());
        checkToken(Kind.IDENT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
     @Test
    void onlyTwoIdentsWithPlusSeparator() throws LexicalException{
        String input = "row+column";
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.IDENT,scanner.next());
        checkToken(Kind.PLUS,scanner.next());
        checkToken(Kind.IDENT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
     @Test
    void onlyTwoIdentsWithPlusSeparatorCarriageReturn() throws LexicalException{
        String input = """
        row0
        _column
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.IDENT,scanner.next());
        checkToken(Kind.IDENT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyTwoIdentsWithDigitCarriageReturn() throws LexicalException{
        String input = """
        row9
        _column
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.IDENT,scanner.next());
        checkToken(Kind.IDENT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyZero() throws LexicalException{
        String input = """
        0
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.NUM_LIT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyFive() throws LexicalException{
        String input = """
        5
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.NUM_LIT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyTen() throws LexicalException{
        String input = """
        10
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.NUM_LIT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlySixNines() throws LexicalException{
        String input = """
        69 69 69
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.NUM_LIT,scanner.next());
        checkToken(Kind.NUM_LIT,scanner.next());
        checkToken(Kind.NUM_LIT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyZeroOne() throws LexicalException{
        String input = """
        01
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.NUM_LIT,scanner.next());
        checkToken(Kind.NUM_LIT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyComment() throws LexicalException{
        String input = """
        ~this is a comment
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlynumLitSpaceComment() throws LexicalException{
        String input = """
        420 ~this is a comment
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.NUM_LIT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlynumLitSpaceCommentnumLit() throws LexicalException{
        String input = """
        420 ~this is a comment
        69
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.NUM_LIT,scanner.next());
        checkToken(Kind.NUM_LIT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlynumLitSpaceCommentIdent() throws LexicalException{
        String input = """
        420 ~this is a comment
        shane
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.NUM_LIT,scanner.next());
        checkToken(Kind.IDENT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyReservedWordImage() throws LexicalException{
        String input = "image";
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.RES_image,scanner.next());
        checkEOF(scanner.next()); 
    }
    /*
    @Test
    void onlyStringLiteral() throws LexicalException{
        String input = """
        "Shane"
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.STRING_LIT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyStringLiteralWithSpace() throws LexicalException{
        String input = """
        "Shane Ferrell"
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.STRING_LIT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyStringLiteralWithNewLine() throws LexicalException{
        String input = """
        "Shane\nFerrell"
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        assertThrows(LexicalException.class, () -> {scanner.next(); });
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyStringLiteralWithLegalNewLine() throws LexicalException{
        String input = """
        "Shane\\nFerrell"
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.STRING_LIT,scanner.next());
        checkEOF(scanner.next()); 
    }
    
    @Test
    void onlyStringLiteralWithNewCarriageReturn() throws LexicalException{
        String input = """
        "Shane
        Ferrell"
        """;
        IScanner scanner =
        CompilerComponentFactory.makeScanner(input);
        //assertThrows(LexicalException.class, () -> {scanner.next(); });
        checkEOF(scanner.next()); 
    }*/
}




