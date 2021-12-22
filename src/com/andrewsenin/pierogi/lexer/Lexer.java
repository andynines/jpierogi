package com.andrewsenin.pierogi.lexer;

import com.andrewsenin.pierogi.io.ErrorType;
import com.andrewsenin.pierogi.io.IoManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Lexer {

    private static final char NULL_CHARACTER = '\0';
    private static final Map<String, TokenType> KEYWORDS = Map.of(
            "and", TokenType.AND,
            "or", TokenType.OR,
            "not", TokenType.NOT,
            "true", TokenType.TRUE,
            "false", TokenType.FALSE,
            "nil", TokenType.NIL
    );

    private final String source;
    private final IoManager ioManager;
    private final List<Token> tokens = new ArrayList<>();
    private int lexemeStartIndex = 0;
    private int currentCharacterIndex = 0;
    private int lineNumber = 1;

    public Lexer(String source, IoManager ioManager) {
        this.source = source;
        this.ioManager = ioManager;
    }

    public List<Token> lexSource() {
        while (!isAtEnd()) lexNextToken();
        tokens.add(new Token(TokenType.EOF, "", null, lineNumber));
        return tokens;
    }

    private boolean isAtEnd() {
        return currentCharacterIndex >= source.length();
    }

    private void lexNextToken() {
        lexemeStartIndex = currentCharacterIndex;
        char currentCharacter = consumeCurrentCharacter();
        switch (currentCharacter) {
            case '\\':
                addToken(TokenType.BACKSLASH);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '*':
                addToken(TokenType.ASTERISK);
                break;
            case '^':
                addToken(TokenType.CARET);
                break;
            case '(':
                addToken(TokenType.LEFT_PARENTHESIS);
                break;
            case ')':
                addToken(TokenType.RIGHT_PARENTHESIS);
                break;
            case '[':
                addToken(TokenType.LEFT_SQUARE_BRACKET);
                break;
            case ']':
                addToken(TokenType.RIGHT_SQUARE_BRACKET);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case ':':
                addToken(TokenType.COLON);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '=':
                addToken(consumeCurrentCharacterIfMatches('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(consumeCurrentCharacterIfMatches('=') ? TokenType.LESS_EQUAL : TokenType.LESS_THAN);
                break;
            case '>':
                addToken(consumeCurrentCharacterIfMatches('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER_THAN);
                break;
            case '.':
                addToken(consumeCurrentCharacterIfMatches('.') ? TokenType.DOT_DOT : TokenType.DOT);
                break;
            case '/':
                addToken(consumeCurrentCharacterIfMatches('=') ? TokenType.SLASH_EQUAL : TokenType.SLASH);
                break;
            case '\n':
                lineNumber++;
            case ' ':
            case '\t':
            case '\r':
                break;
            case '#':
                consumeComment();
                break;
            case '"':
                consumeString();
                break;
            default:
                if (Character.isDigit(currentCharacter)) consumeNumber();
                else if (Character.isLetter(currentCharacter)) consumeWord();
                else
                    ioManager.reportError(ErrorType.UNRECOGNIZED_CHARACTER, getCurrentLexeme(), lineNumber);
        }
    }

    private char consumeCurrentCharacter() {
        if (isAtEnd()) return NULL_CHARACTER;
        return source.charAt(currentCharacterIndex++);
    }

    private boolean consumeCurrentCharacterIfMatches(char expectedCharacter) {
        if (isAtEnd() || peekCurrentCharacter() != expectedCharacter) return false;
        consumeCurrentCharacter();
        return true;
    }

    private char peekCurrentCharacter() {
        if (isAtEnd()) return NULL_CHARACTER;
        return source.charAt(currentCharacterIndex);
    }

    private char peekNextCharacter() {
        if (currentCharacterIndex + 1 >= source.length()) return NULL_CHARACTER;
        return source.charAt(currentCharacterIndex + 1);
    }

    private String getCurrentLexeme() {
        return source.substring(lexemeStartIndex, currentCharacterIndex);
    }

    private void addToken(TokenType tokenType) {
        tokens.add(new Token(tokenType, "", null, lineNumber));
    }

    private void addToken(TokenType tokenType, String lexeme, Object literalValue) {
        tokens.add(new Token(tokenType, lexeme, literalValue, lineNumber));
    }

    private void consumeComment() {
        while (peekCurrentCharacter() != '\n' && !isAtEnd()) consumeCurrentCharacter();
    }

    private void consumeString() {
        // TODO: accept escape sequences for at least \n and \"
        while (peekCurrentCharacter() != '"') {
            if (isAtEnd()) throw ioManager.reportError(ErrorType.UNTERMINATED_STRING, getCurrentLexeme(), lineNumber);
            if (peekCurrentCharacter() == '\n') lineNumber++;
            consumeCurrentCharacter();
        }
        consumeCurrentCharacter(); // Consume closing '"'
        String lexeme = getCurrentLexeme();
        String stringContents = lexeme.substring(1, lexeme.length() - 1);
        addToken(TokenType.STRING, lexeme, stringContents);
    }

    private void consumeNumber() {
        while (Character.isDigit(peekCurrentCharacter())) consumeCurrentCharacter();
        if (peekCurrentCharacter() == '.' && Character.isDigit(peekNextCharacter())) {
            consumeCurrentCharacter(); // Consume '.'
            while (Character.isDigit(peekCurrentCharacter())) consumeCurrentCharacter();
        }
        String lexeme = getCurrentLexeme();
        double value = Double.parseDouble(lexeme);
        addToken(TokenType.NUMBER, lexeme, value);
    }

    private void consumeWord() {
        while (isIdentifierCharacter(peekCurrentCharacter())) consumeCurrentCharacter();
        String lexeme = getCurrentLexeme();
        if (KEYWORDS.containsKey(lexeme)) addToken(KEYWORDS.get(lexeme));
        else
            addToken(TokenType.IDENTIFIER, lexeme, null);
    }

    private static boolean isIdentifierCharacter(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }
}
