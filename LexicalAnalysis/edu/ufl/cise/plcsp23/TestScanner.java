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
    public void test0()
    {
    }
}




