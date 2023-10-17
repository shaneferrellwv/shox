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
    int line, column, myLine, myColumn;
    String s;
    
    //constructor
    public Scanner(String input) {
        this.input = input;
        inputChars = Arrays.copyOf(input.toCharArray(),input.length()+1);
        pos = 0;
        ch = inputChars[pos];
        line = 1;
        column = 1;
    }
    
    @Override
    public IToken next() throws LexicalException {
        return scanToken();
    }
    
    private IToken scanToken() throws LexicalException {
        State state = State.START;
        int tokenStart = -1;
        while(true) { //read chars, loop terminates when a Token is returned 
            switch(state) {
                case  START ->  {
                    tokenStart = pos;
                    s = "";
                    myLine = line;
                    myColumn = column;
                    switch (ch) {
                        case 0 -> {
                            return new Token(IToken.Kind.EOF, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
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
                            return new Token(IToken.Kind.DOT, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        }
                        case 44 -> { // ,
                            nextChar();
                            return new Token(IToken.Kind.COMMA, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        }
                        case 63 -> { // ?
                            nextChar();
                            return new Token(IToken.Kind.QUESTION, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        }
                        case 58 -> { // :
                            nextChar();
                            return new Token(IToken.Kind.COLON, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        }
                        case 40 -> { // (
                            nextChar();
                            return new Token(IToken.Kind.LPAREN, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        }
                        case 41 -> { // )
                            nextChar();
                            return new Token(IToken.Kind.RPAREN, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        }
                        case 91 -> { // [
                            nextChar();
                            return new Token(IToken.Kind.LSQUARE,tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        }
                        case 93 -> { // ]
                            nextChar();
                            return new Token(IToken.Kind.RSQUARE,tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        }
                        case 123 -> { // {
                            nextChar();
                            return new Token(IToken.Kind.LCURLY,tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        }
                        case 125 -> { // }
                            nextChar();
                            return new Token(IToken.Kind.RCURLY,tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        }
                        case 33 -> { // !
                            nextChar();
                            return new Token(IToken.Kind.BANG,tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        }
                        case 43 -> { // +
                            nextChar();
                            return new Token(IToken.Kind.PLUS,tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        }
                        case 45 -> { // -
                            nextChar();
                            return new Token(IToken.Kind.MINUS,tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        }
                        case 47 -> { // /
                            nextChar();
                            return new Token(IToken.Kind.DIV,tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        }
                        case 37 -> { // %
                            nextChar();
                            return new Token(IToken.Kind.MOD,tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
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
                            return new NumLitToken(INumLitToken.Kind.NUM_LIT,tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
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
                            throw new LexicalException("Character not supported");
                        }
                    }
                    
                }
                case IN_STRING_LIT -> {
                    if ((int)ch == 92) { // escape character \
                        state = State.IN_ESCAPE;
                        nextChar();
                    }
                    else if ((int)ch == 10 || (int)ch == 13) {
                        pos = input.length()-1;
                        throw new LexicalException("LF and CR are not supported characters in string literals.");
                    }
                    else if ((int)ch == 34) { // closing "
                        nextChar();
                        return new StringLitToken(IStringLitToken.Kind.STRING_LIT, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
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
                        if (pos - tokenStart > 10)
                            throw new LexicalException("Value too large for integer.");
                        else if (pos - tokenStart == 10) {
                            String z = "";
                            for (int i = tokenStart; i < pos; i++)
                                z = z + inputChars[i];
                            long val = Long.parseLong(z);
                            if (val > 2147483647)
                                throw new LexicalException("Value too large for integer.");
                        }
                        else
                            return new NumLitToken(INumLitToken.Kind.NUM_LIT, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
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
                            return new Token(IToken.Kind.RES_image, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("pixel"))
                            return new Token(IToken.Kind.RES_pixel, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("int"))
                            return new Token(IToken.Kind.RES_int, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("string"))
                            return new Token(IToken.Kind.RES_string, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("void"))
                            return new Token(IToken.Kind.RES_void, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("nil"))
                            return new Token(IToken.Kind.RES_nil, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("load"))
                            return new Token(IToken.Kind.RES_load, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("display"))
                            return new Token(IToken.Kind.RES_display, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("write"))
                            return new Token(IToken.Kind.RES_write, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("x"))
                            return new Token(IToken.Kind.RES_x, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("y"))
                            return new Token(IToken.Kind.RES_y, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("a"))
                            return new Token(IToken.Kind.RES_a, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("r"))
                            return new Token(IToken.Kind.RES_r, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("X"))
                            return new Token(IToken.Kind.RES_X, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("Y"))
                            return new Token(IToken.Kind.RES_Y, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("Z"))
                            return new Token(IToken.Kind.RES_Z, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("x_cart"))
                            return new Token(IToken.Kind.RES_x_cart, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("y_cart"))
                            return new Token(IToken.Kind.RES_y_cart, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("a_polar"))
                            return new Token(IToken.Kind.RES_a_polar, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("r_polar"))
                            return new Token(IToken.Kind.RES_r_polar, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("rand"))
                            return new Token(IToken.Kind.RES_rand, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("sin"))
                            return new Token(IToken.Kind.RES_sin, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("cos"))
                            return new Token(IToken.Kind.RES_cos, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("atan"))
                            return new Token(IToken.Kind.RES_atan, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("if"))
                            return new Token(IToken.Kind.RES_if, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("while"))
                            return new Token(IToken.Kind.RES_while, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("red"))
                            return new Token(IToken.Kind.RES_red, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("grn"))
                            return new Token(IToken.Kind.RES_grn, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        if (s.equals("blu"))
                            return new Token(IToken.Kind.RES_blu, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                        else
                            return new Token(IToken.Kind.IDENT, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                    }
                }
                case HAVE_AMP -> {
                    if (ch == '&'){
                        state = State.START;
                        nextChar();
                        return new Token(IToken.Kind.AND, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                    }
                    else
                        return new Token(IToken.Kind.BITAND, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                }
                case HAVE_BAR -> {
                    if (ch == '|'){
                        state = State.START;
                        nextChar();
                        return new Token(IToken.Kind.OR, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                    }
                    else
                        return new Token(IToken.Kind.BITOR, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                }
                case HAVE_STAR -> {
                    if (ch == '*'){
                        state = State.START;
                        nextChar();
                        return new Token(IToken.Kind.EXP, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                    }
                    else
                        return new Token(IToken.Kind.TIMES, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                }
                case HAVE_EQ -> {
                    if (ch == '='){
                        state = State.START;
                        nextChar();
                        return new Token(IToken.Kind.EQ, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                    }
                    else
                        return new Token(IToken.Kind.ASSIGN, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                }
                case HAVE_LT -> {
                    if (ch == '='){
                        state = State.START;
                        nextChar();
                        return new Token(IToken.Kind.LE, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                    }
                    else if (ch == '-') {
                        state = State.HAVE_LTMINUS;
                        nextChar();
                    }
                    else
                        return new Token(IToken.Kind.LT, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                }
                case HAVE_LTMINUS -> {
                    if (ch == '>'){
                        state = State.START;
                        nextChar();
                        return new Token(IToken.Kind.EXCHANGE, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                    }
                    else {
                        throw new LexicalException("Exchange operator incomplete");
                    } 
                }
                case HAVE_GT -> {
                    if (ch == '='){
                        state = State.START;
                        nextChar();
                        return new Token(IToken.Kind.GE, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                    }
                    else
                        return new Token(IToken.Kind.GT, tokenStart, pos - tokenStart, myLine, myColumn, inputChars);
                }
                default ->  {
                    throw new UnsupportedOperationException("Bug in Scanner");  
                }
            }
        }
    }
    
    void nextChar() {
        ch = inputChars[++pos];
        column++;
    }
    
    void nextLineChar() {
        ch = inputChars[++pos];
        column = 1;
        line++;
    }
}