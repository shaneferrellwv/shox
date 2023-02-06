package edu.ufl.cise.plcsp23;


/**
 * Write a description of class Scanner here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */

import java.util.Arrays;

public class Scanner implements IScanner
{
    // class variables
    final String input;
    final char[] inputChars;    // array containing input chars, terminated with extra char 0
    private enum State          // internal states
    {
        START,
        HAVE_EQ,
        IN_IDENT,
        IN_NUM_LIT
    }
    int pos; //position of ch
    char ch; //next char, ch == inputChars[pos]
    
    //constructor
    public Scanner(String input)
    {
        this.input = input;
        inputChars = Arrays.copyOf(input.toCharArray(),input.length()+1);
        pos = 0;
        ch = inputChars[pos];
    }
    
    @Override
    public Token next() throws LexicalException 
    {
        this.State = START;
        scanToken();
        
        return ;
    }
    
    private Token scanToken() throws LexicalException 
    {
        State state = State.START;
        int tokenStart = -1;
        while(true) 
        { //read chars, loop terminates when a Token is returned 
            switch(state)
            {
                case  START -> 
                {
                    if (((int)ch >= 65 && (int)ch <= 90) || 
                        ((int)ch >= 97 && (int)ch <= 122) ||
                        (ch == '_'))
                    {
                        state = IN_IDENT;
                    }
                    
                }
                case HAVE_EQ ->
                {
                
                }
                case IN_NUM_LIT ->
                {
                    
                }
                case IN_IDENT ->
                {
                    
                }
                default -> 
                {
                    throw new UnsupportedOperationException("Bug in Scanner");  
                }
            }
        }
    }
}
