package edu.ufl.cise.plcsp23;


/**
 * Write a description of class Scanner here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */

import java.util.Arrays;

public class Scanner implements IScanner {
    // class variables
    final String input;
    final char[] inputChars;    // array containing input chars, terminated with extra char 0
    private enum State {        // internal states
        START,
        HAVE_EQ,
        IN_IDENT,
        IN_NUM_LIT,
        HAVE_AMP
    }
    int pos; //position of ch
    char ch; //next char, ch == inputChars[pos]
    int line, column;
    
    //constructor
    public Scanner(String input) {
        this.input = input;
        inputChars = Arrays.copyOf(input.toCharArray(),input.length()+1);
        pos = 0;
        ch = inputChars[pos];
        line = 0;
        column = 0;
    }
    
    @Override
    public Token next() throws LexicalException {
        return scanToken();
    }
    
    private Token scanToken() throws LexicalException {
        State state = State.START;
        int tokenStart = -1;
        while(true) { //read chars, loop terminates when a Token is returned 
            switch(state) {
                case  START ->  {
                    tokenStart = pos;
                    switch (ch) {
                        case 0 -> {
                            return new Token(IToken.Kind.EOF, tokenStart, pos - tokenStart, inputChars);
                        }
                        case 32, 9, 12, 13 -> nextChar(); // white space
                        case 10 -> nextLineChar(); // new line
                        
                        case 38 -> { // &
                            state = State.HAVE_AMP;
                            nextChar();
                        }
                        
                        
                        case 46 -> { // .
                            return new Token(IToken.Kind.DOT, tokenStart, pos - tokenStart, inputChars);
                        }
                        case 44 -> { // ,
                            return new Token(IToken.Kind.COMMA, tokenStart, pos - tokenStart, inputChars);
                        }
                        case 63 -> { // ?
                            return new Token(IToken.Kind.QUESTION, tokenStart, pos - tokenStart, inputChars);
                        }
                        case 58 -> { // :
                            return new Token(IToken.Kind.COLON, tokenStart, pos - tokenStart, inputChars);
                        }
                        case 40 -> { // (
                            return new Token(IToken.Kind.LPAREN, tokenStart, pos - tokenStart, inputChars);
                        }
                        case 41 -> { // )
                            return new Token(IToken.Kind.RPAREN, tokenStart, pos - tokenStart, inputChars);
                        }
                        
                        default -> {
                            throw new UnsupportedOperationException(
                                "not yet implemented");
                        }
                    }
                    
                }
                case HAVE_EQ -> {
                
                }
                case IN_NUM_LIT -> {
                    
                }
                case IN_IDENT -> {
                    
                }
                case HAVE_AMP -> {
                    if (ch == '&'){
                        state = State.START;
                        nextChar();
                        return new Token(IToken.Kind.AND, tokenStart, pos - tokenStart, inputChars);
                    }
                    else
                        return new Token(IToken.Kind.BITAND, tokenStart, pos - tokenStart, inputChars);
                }
                default ->  {
                    throw new UnsupportedOperationException("Bug in Scanner");  
                }
            }
            tokenStart++;
        }
    }
    
    void nextChar() {
        ch = inputChars[++pos];
        column++;
    }
    
    void nextLineChar() {
        ch = inputChars[++pos];
        column = 0;
        line++;
    }
}