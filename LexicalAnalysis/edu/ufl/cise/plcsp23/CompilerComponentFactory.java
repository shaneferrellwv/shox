/*Copyright 2023 by Beverly A Sanders
 * 
 * This code is provided for solely for use of students in COP4020 Programming Language Concepts at the 
 * University of Florida during the spring semester 2023 as part of the course project.  
 * 
 * No other use is authorized. 
 * 
 * This code may not be posted on a public web site either during or after the course.  
 */

package edu.ufl.cise.plcsp23;
import java.util.List;
import java.util.ArrayList;

public class CompilerComponentFactory {
    public static IScanner makeScanner(String input) {
        //Add statement to return an instance of your scanner
        return new Scanner(input);
    }
    
    public static IParser makeAssignment2Parser(String input) throws LexicalException {
        //add code to create a scanner and parser and return the parser.
        IScanner scanner = new Scanner(input);
        List<IToken> tokens = new ArrayList();
        IToken t;
        do {
            t = scanner.next();
            tokens.add(t);
        } while (t.getKind() != IToken.Kind.EOF);
        return new Parser(tokens);
    }
    
    public static IParser makeParser(String input) throws LexicalException {
        //add code to create a scanner and parser and return the parser.
        IScanner scanner = new Scanner(input);
        List<IToken> tokens = new ArrayList();
        IToken t;
        do {
            t = scanner.next();
            tokens.add(t);
        } while (t.getKind() != IToken.Kind.EOF);
        return new Parser(tokens);
    }
}
