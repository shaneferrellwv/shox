package edu.ufl.cise.plcsp23;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import edu.ufl.cise.plcsp23.IToken.Kind;
import edu.ufl.cise.plcsp23.IToken.SourceLocation;

class TestScanner_starter {

    // makes it easy to turn output on and off (and less typing than
    // System.out.println)
    static final boolean VERBOSE = true;

    void show(Object obj) {
        if (VERBOSE) {
            System.out.println(obj);
        }
    }

    // check that this token has the expected kind
    void checkToken(Kind expectedKind, IToken t) {
        assertEquals(expectedKind, t.getKind());
    }
    
    void checkToken(Kind expectedKind, String expectedChars, SourceLocation expectedLocation, IToken t) {
        assertEquals(expectedKind, t.getKind());
        assertEquals(expectedChars, t.getTokenString());
        assertEquals(expectedLocation, t.getSourceLocation());
        ;
    }

    void checkIdent(String expectedChars, IToken t) {
        checkToken(Kind.IDENT, t);
        assertEquals(expectedChars.intern(), t.getTokenString().intern());
        ;
    }

    void checkString(String expectedValue, IToken t) {
        assertTrue(t instanceof IStringLitToken);
        assertEquals(expectedValue, ((IStringLitToken) t).getValue());
    }

    void checkString(String expectedChars, String expectedValue, SourceLocation expectedLocation, IToken t) {
        assertTrue(t instanceof IStringLitToken);
        assertEquals(expectedValue, ((IStringLitToken) t).getValue());
        assertEquals(expectedChars, t.getTokenString());
        assertEquals(expectedLocation, t.getSourceLocation());
    }

    void checkNUM_LIT(int expectedValue, IToken t) {
        checkToken(Kind.NUM_LIT, t);
        int value = ((INumLitToken) t).getValue();
        assertEquals(expectedValue, value);
    }
    
    void checkNUM_LIT(int expectedValue, SourceLocation expectedLocation, IToken t) {
        checkToken(Kind.NUM_LIT, t);
        int value = ((INumLitToken) t).getValue();
        assertEquals(expectedValue, value);
        assertEquals(expectedLocation, t.getSourceLocation());
    }

    void checkTokens(IScanner s, IToken.Kind... kinds) throws LexicalException {
        for (IToken.Kind kind : kinds) {
            checkToken(kind, s.next());
        }
    }

    void checkTokens(String input, IToken.Kind... kinds) throws LexicalException {
        IScanner s = CompilerComponentFactory.makeScanner(input);
        for (IToken.Kind kind : kinds) {
            checkToken(kind, s.next());
        }
    }

    // check that this token is the EOF token
    void checkEOF(IToken t) {
        checkToken(Kind.EOF, t);
    }
    
    @Test
    public void test1()
    {
    }

    
    @Test
    void emptyProg() throws LexicalException {
        String input = "";
        IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkEOF(scanner.next());
    }

    @Test
    void onlyWhiteSpace() throws LexicalException {
        String input = " \t \r\n \f \n";
        IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkEOF(scanner.next());
        checkEOF(scanner.next());  //repeated invocations of next after end reached should return EOF token
    }

    @Test
    public void numLits1() throws LexicalException {
        String input = """
                123
                05 240
                """;
        IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkNUM_LIT(123, scanner.next());
        checkNUM_LIT(0, scanner.next());
        checkNUM_LIT(5, scanner.next());
        checkNUM_LIT(240, scanner.next());
        checkEOF(scanner.next());
    }
    
    @Test
    //Too large should still throw LexicalException
    public void numLitTooBig() throws LexicalException {
        String input = "999999999999999999999";
        IScanner scanner = CompilerComponentFactory.makeScanner(input);
        assertThrows(LexicalException.class, () -> {
            scanner.next();
        });
    }


    @Test
    public void identsAndReserved() throws LexicalException {
        String input = """
                i0
                  i1  x ~~~2 spaces at beginning and after il
                y Y
                """;

        IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.IDENT,"i0", new SourceLocation(1,1), scanner.next());
        checkToken(Kind.IDENT, "i1",new SourceLocation(2,3), scanner.next());
        checkToken(Kind.RES_x, "x", new SourceLocation(2,7), scanner.next());        
        checkToken(Kind.RES_y, "y", new SourceLocation(3,1), scanner.next());
        checkToken(Kind.RES_Y, "Y", new SourceLocation(3,3), scanner.next());
        checkEOF(scanner.next());
    }
    

    @Test
    public void operators0() throws LexicalException {
        String input = """
                ==
                +
                /
                ====
                =
                ===
                """;
        IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.EQ, scanner.next());
        checkToken(Kind.PLUS, scanner.next());
        checkToken(Kind.DIV, scanner.next());
        checkToken(Kind.EQ, scanner.next());
        checkToken(Kind.EQ, scanner.next());
        checkToken(Kind.ASSIGN, scanner.next());
        checkToken(Kind.EQ, scanner.next());
        checkToken(Kind.ASSIGN, scanner.next());
        checkEOF(scanner.next());
    }


    @Test
    public void stringLiterals1() throws LexicalException {
        String input = """
                "hello"
                "\t"
                "\\""
                """;
        IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkString(input.substring(0, 7),"hello", new SourceLocation(1,1), scanner.next());
        checkString(input.substring(8, 11), "\t", new SourceLocation(2,1), scanner.next());
        checkString(input.substring(12, 16), "\"",  new SourceLocation(3,1), scanner.next());
        checkEOF(scanner.next());
    }


    @Test
    public void illegalEscape() throws LexicalException {
        String input = """
                "\\t"
                "\\k"
                """;
        IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkString("\"\\t\"","\t", new SourceLocation(1,1), scanner.next());
        assertThrows(LexicalException.class, () -> {
            scanner.next();
        });
    }
    
    @Test
    public void illegalLineTermInStringLiteral() throws LexicalException {
        String input = """
                "\\n"  ~ this one passes the escape sequence--it is OK
                "\n"   ~ this on passes the LF, it is illegal.
                """;
        IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkString("\"\\n\"","\n", new SourceLocation(1,1), scanner.next());
        assertThrows(LexicalException.class, () -> {
            scanner.next();
        });
    }

    @Test
    public void lessThanGreaterThanExchange() throws LexicalException {
        String input = """
                <->>>>=
                <<=<
                """;
        checkTokens(input, Kind.EXCHANGE, Kind.GT, Kind.GT, Kind.GE, Kind.LT, Kind.LE, Kind.LT, Kind.EOF);
    }
    
    /* The Scanner should not backtrack so this input should throw an exception  */
    @Test
    public void incompleteExchangeThrowsException() throws LexicalException {
        String input = " <- ";
        IScanner scanner = CompilerComponentFactory.makeScanner(input);
        assertThrows(LexicalException.class, () -> {
            scanner.next();
        });    
    }

    @Test
    public void illegalChar() throws LexicalException {
        String input = """
                abc
                @
                """;
        IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkIdent("abc", scanner.next());
        assertThrows(LexicalException.class, () -> {
            @SuppressWarnings("unused")
            IToken t = scanner.next();
        });
    }
    
@Test
void andNothingButComments() throws LexicalException {
    String input = """
            ~jerry
            ~can
            ~move
            ~if
            ~he's
            ~not
            ~@#$%&#^%&@
            ~tired
            """;
    IScanner scanner = CompilerComponentFactory.makeScanner(input);
    checkEOF(scanner.next());
}

@Test
void andNumLitsZeroes() throws LexicalException {
    String input = """
            000
            00
            001
            10 0
            """;
    IScanner scanner = CompilerComponentFactory.makeScanner(input);
    checkNUM_LIT(0, scanner.next());
    checkNUM_LIT(0, scanner.next());
    checkNUM_LIT(0, scanner.next());
    checkNUM_LIT(0, scanner.next());
    checkNUM_LIT(0, scanner.next());
    checkNUM_LIT(0, scanner.next());
    checkNUM_LIT(0, scanner.next());
    checkNUM_LIT(1, scanner.next());
    checkNUM_LIT(10, scanner.next());
    checkNUM_LIT(0, scanner.next());
    checkEOF(scanner.next());
}

@Test
void andIdentsWithNumLits() throws LexicalException {
    String input = """
            0f0f0
            12if21
            12if 21
            00 if 12
            """;
    IScanner scanner = CompilerComponentFactory.makeScanner(input);
    checkNUM_LIT(0, scanner.next());
    checkToken(Kind.IDENT, "f0f0", new SourceLocation(1, 2), scanner.next());
    checkNUM_LIT(12, scanner.next());
    checkToken(Kind.IDENT, "if21", new SourceLocation(2, 3), scanner.next());
    checkNUM_LIT(12, scanner.next());
    checkToken(Kind.RES_if, "if", new SourceLocation(3, 3), scanner.next());
    checkNUM_LIT(21, scanner.next());
    checkNUM_LIT(0, scanner.next());
    checkNUM_LIT(0, scanner.next());
    checkToken(Kind.RES_if, "if", new SourceLocation(4, 4), scanner.next());
    checkNUM_LIT(12, scanner.next());
    checkEOF(scanner.next());
}

@Test
void andOperators() throws LexicalException {
    String input = """
            =&&
            *****
            ~====
            ||?:,|
            """;
    IScanner scanner = CompilerComponentFactory.makeScanner(input);
    checkToken(Kind.ASSIGN, scanner.next());
    checkToken(Kind.AND, scanner.next());
    checkToken(Kind.EXP, scanner.next());
    checkToken(Kind.EXP, scanner.next());
    checkToken(Kind.TIMES, scanner.next());
    checkToken(Kind.OR, scanner.next());
    checkToken(Kind.QUESTION, scanner.next());
    checkToken(Kind.COLON, scanner.next());
    checkToken(Kind.COMMA, scanner.next());
    checkToken(Kind.BITOR, scanner.next());
    checkEOF(scanner.next());
}

@Test
void andEmptyStrings() throws LexicalException {
    String input = """
            \"\"\"\"\"\"\"
            """;
    IScanner scanner = CompilerComponentFactory.makeScanner(input);
    checkString("", scanner.next());
    checkString("", scanner.next());
    checkString("", scanner.next());
    assertThrows(LexicalException.class, () -> {
        scanner.next();
    });
}

}
