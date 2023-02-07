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
        HAVE_AMP,
        HAVE_BAR,
        HAVE_STAR,
        HAVE_LT,
        HAVE_GT,
        HAVE_LTMINUS,
        IN_STRING_LIT,
        IN_ESCAPE, 
        IN_COMMENT
    }
    int pos; //position of ch
    char ch; //next char, ch == inputChars[pos]
    int line, column;
    String s;
    
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
                    s = "";
                    switch (ch) {
                        case 0 -> {
                            return new Token(IToken.Kind.EOF, tokenStart, pos - tokenStart, inputChars);
                        }
                        case 32, 9, 12, 13 -> nextChar(); // white space
                        case 10 -> nextLineChar(); // FF
                        
                        case 38 -> { // &
                            state = State.HAVE_AMP;
                            nextChar();
                        }
                        case 124 -> { // |
                            state = State.HAVE_BAR;
                            nextChar();
                        }
                        case 42 -> { // *
                            state = State.HAVE_STAR;
                            nextChar();
                        }
                        case 61 -> { // =
                            state = State.HAVE_EQ;
                            nextChar();
                        }
                        case 60 -> { // <
                            state = State.HAVE_LT;
                            nextChar();
                        }
                        case 62 -> { // >
                            state = State.HAVE_GT;
                            nextChar();
                        }
                        case 46 -> { // .
                            nextChar();
                            return new Token(IToken.Kind.DOT, tokenStart, pos - tokenStart, inputChars);
                        }
                        case 44 -> { // ,
                            nextChar();
                            return new Token(IToken.Kind.COMMA, tokenStart, pos - tokenStart, inputChars);
                        }
                        case 63 -> { // ?
                            nextChar();
                            return new Token(IToken.Kind.QUESTION, tokenStart, pos - tokenStart, inputChars);
                        }
                        case 58 -> { // :
                            nextChar();
                            return new Token(IToken.Kind.COLON, tokenStart, pos - tokenStart, inputChars);
                        }
                        case 40 -> { // (
                            nextChar();
                            return new Token(IToken.Kind.LPAREN, tokenStart, pos - tokenStart, inputChars);
                        }
                        case 41 -> { // )
                            nextChar();
                            return new Token(IToken.Kind.RPAREN, tokenStart, pos - tokenStart, inputChars);
                        }
                        case 91 -> { // [
                            nextChar();
                            return new Token(IToken.Kind.LSQUARE,tokenStart, pos - tokenStart, inputChars);
                        }
                        case 93 -> { // ]
                            nextChar();
                            return new Token(IToken.Kind.RSQUARE,tokenStart, pos - tokenStart, inputChars);
                        }
                        case 123 -> { // {
                            nextChar();
                            return new Token(IToken.Kind.LCURLY,tokenStart, pos - tokenStart, inputChars);
                        }
                        case 125 -> { // }
                            nextChar();
                            return new Token(IToken.Kind.RCURLY,tokenStart, pos - tokenStart, inputChars);
                        }
                        case 33 -> { // !
                            nextChar();
                            return new Token(IToken.Kind.BANG,tokenStart, pos - tokenStart, inputChars);
                        }
                        case 43 -> { // +
                            nextChar();
                            return new Token(IToken.Kind.PLUS,tokenStart, pos - tokenStart, inputChars);
                        }
                        case 45 -> { // -
                            nextChar();
                            return new Token(IToken.Kind.MINUS,tokenStart, pos - tokenStart, inputChars);
                        }
                        case 47 -> { // /
                            nextChar();
                            return new Token(IToken.Kind.DIV,tokenStart, pos - tokenStart, inputChars);
                        }
                        case 37 -> { // %
                            nextChar();
                            return new Token(IToken.Kind.MOD,tokenStart, pos - tokenStart, inputChars);
                        }
                        
                        case 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75,
                            76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86,
                            87, 88, 89, 90, // A..Z
                            97, 98, 99, 100, 101, 102, 103, 104, 105, 106,
                            107, 108, 109, 110, 111, 112, 113, 114, 115, 
                            116, 117, 118, 119, 120, 121, 122, // a..z
                            95 -> {// _ 
                            state = State.IN_IDENT;
                            nextChar();
                        }
                        case 48 -> { // 0
                            nextChar();
                            return new Token(IToken.Kind.NUM_LIT,tokenStart, pos - tokenStart, inputChars);
                        }
                        case 49, 50, 51, 52, 53, 54, 55, 56, 57 -> { // 1..9
                            state = State.IN_NUM_LIT;
                            nextChar();
                        }
                        case 34 -> { // "
                            state = State.IN_STRING_LIT;
                            nextChar();
                        }
                        case 126 -> { // ~
                            state = State.IN_COMMENT;
                            nextChar();
                        }
                            
                        
                        default -> {
                            throw new UnsupportedOperationException(
                                "not yet implemented");
                        }
                    }
                    
                }
                case IN_STRING_LIT -> {
                    System.out.print(ch + " ");
                    if ((int)ch == 92) { // escape character \
                        state = State.IN_ESCAPE;
                        nextChar();
                    }
                    if ((int)ch == 10 || (int)ch == 13) {
                        throw new LexicalException("LF and CR are not supported characters in string literals.");
                    }
                    else if ((int)ch == 34) { // closing "
                        nextChar();
                        return new Token(IToken.Kind.STRING_LIT, tokenStart, pos - tokenStart, inputChars);
                    }
                    else {
                        nextChar();
                    }
                }
                case IN_ESCAPE -> {
                    if ((int)ch == 98 || // b
                        (int)ch == 116 || // t
                        (int)ch == 110 || // n
                        (int)ch == 114 || // r
                        (int)ch == 34 || // "
                        (int)ch == 92) { // \
                        state = State.IN_STRING_LIT;
                        nextChar();
                    }
                    else {
                        throw new LexicalException("\\ character not supported in string literals.");
                    }
                }
                case IN_NUM_LIT -> {
                    if ((int)ch >= 48 && (int)ch <= 57) {
                        nextChar();
                    }
                    else
                        return new Token(IToken.Kind.NUM_LIT, tokenStart, pos - tokenStart, inputChars);
                }
                case IN_COMMENT -> {
                    if ((int)ch == 10) { // FF
                        state = State.START;
                        nextLineChar();
                    }
                    else {
                        nextChar();
                    }
                }
                case IN_IDENT -> {
                    if (((int)ch >= 65 && (int)ch <= 90) || 
                        ((int)ch >= 97 && (int)ch <= 122) ||
                        ((int)ch >= 48 && (int)ch <= 57) ||
                        (ch == '_')) {
                        nextChar();
                    }
                    else {
                        state = State.START;
                        s = input.substring(tokenStart, pos);
                        if (s.equals("image"))
                            return new Token(IToken.Kind.RES_image, tokenStart, pos - tokenStart, inputChars);
                        else
                            return new Token(IToken.Kind.IDENT, tokenStart, pos - tokenStart, inputChars);
                    }
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
                case HAVE_BAR -> {
                    if (ch == '|'){
                        state = State.START;
                        nextChar();
                        return new Token(IToken.Kind.OR, tokenStart, pos - tokenStart, inputChars);
                    }
                    else
                        return new Token(IToken.Kind.BITOR, tokenStart, pos - tokenStart, inputChars);
                }
                case HAVE_STAR -> {
                    if (ch == '*'){
                        state = State.START;
                        nextChar();
                        return new Token(IToken.Kind.EXP, tokenStart, pos - tokenStart, inputChars);
                    }
                    else
                        return new Token(IToken.Kind.TIMES, tokenStart, pos - tokenStart, inputChars);
                }
                case HAVE_EQ -> {
                    if (ch == '='){
                        state = State.START;
                        nextChar();
                        return new Token(IToken.Kind.EQ, tokenStart, pos - tokenStart, inputChars);
                    }
                    else
                        return new Token(IToken.Kind.ASSIGN, tokenStart, pos - tokenStart, inputChars);
                }
                case HAVE_LT -> {
                    if (ch == '='){
                        state = State.START;
                        nextChar();
                        return new Token(IToken.Kind.LE, tokenStart, pos - tokenStart, inputChars);
                    }
                    else if (ch == '-') {
                        state = State.HAVE_LTMINUS;
                        nextChar();
                    }
                    else
                        return new Token(IToken.Kind.LT, tokenStart, pos - tokenStart, inputChars);
                }
                case HAVE_LTMINUS -> {
                    if (ch == '>'){
                        state = State.START;
                        nextChar();
                        return new Token(IToken.Kind.EXCHANGE, tokenStart, pos - tokenStart, inputChars);
                    }
                    else {
                        lastChar();
                        return new Token(IToken.Kind.LT, tokenStart, 1, inputChars);
                    } 
                }
                case HAVE_GT -> {
                    if (ch == '='){
                        state = State.START;
                        nextChar();
                        return new Token(IToken.Kind.GE, tokenStart, pos - tokenStart, inputChars);
                    }
                    else
                        return new Token(IToken.Kind.GT, tokenStart, pos - tokenStart, inputChars);
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
    
    void lastChar() {
        ch = inputChars[--pos];
        column--;
    }
    
    void nextLineChar() {
        ch = inputChars[++pos];
        column = 0;
        line++;
    }
}