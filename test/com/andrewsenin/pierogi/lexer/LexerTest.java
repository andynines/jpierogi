package com.andrewsenin.pierogi.lexer;

import com.andrewsenin.pierogi.io.ErrorType;
import com.andrewsenin.pierogi.io.TestIoManager;
import com.andrewsenin.pierogi.io.DummyIoManager;
import com.andrewsenin.pierogi.io.UnwindingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LexerTest {

    @Test
    void read_eof_from_empty_source() {
        expectEof("");
    }

    @Test
    void recognize_each_token_type_individually() {
        expectSingleToken("\\", TokenType.BACKSLASH);
        expectSingleToken("+", TokenType.PLUS);
        expectSingleToken("-", TokenType.MINUS);
        expectSingleToken("*", TokenType.ASTERISK);
        expectSingleToken("/", TokenType.SLASH);
        expectSingleToken("^", TokenType.CARET);
        expectSingleToken("<", TokenType.LESS_THAN);
        expectSingleToken(">", TokenType.GREATER_THAN);
        expectSingleToken("=", TokenType.EQUAL);
        expectSingleToken("(", TokenType.LEFT_PARENTHESIS);
        expectSingleToken(")", TokenType.RIGHT_PARENTHESIS);
        expectSingleToken("[", TokenType.LEFT_SQUARE_BRACKET);
        expectSingleToken("]", TokenType.RIGHT_SQUARE_BRACKET);
        expectSingleToken("{", TokenType.LEFT_BRACE);
        expectSingleToken("}", TokenType.RIGHT_BRACE);
        expectSingleToken(":", TokenType.COLON);
        expectSingleToken(".", TokenType.DOT);
        expectSingleToken(",", TokenType.COMMA);

        expectSingleToken("==", TokenType.EQUAL_EQUAL);
        expectSingleToken("/=", TokenType.SLASH_EQUAL);
        expectSingleToken("<=", TokenType.LESS_EQUAL);
        expectSingleToken(">=", TokenType.GREATER_EQUAL);
        expectSingleToken("..", TokenType.DOT_DOT);

        expectSingleToken("and", TokenType.AND);
        expectSingleToken("or", TokenType.OR);
        expectSingleToken("not", TokenType.NOT);
        expectSingleToken("true", TokenType.TRUE);
        expectSingleToken("false", TokenType.FALSE);
        expectSingleToken("nil", TokenType.NIL);

        expectSingleToken("\"string\"", TokenType.STRING);
        expectSingleToken("123", TokenType.NUMBER);
        expectSingleToken("123.45", TokenType.NUMBER);
        expectSingleToken("identifier", TokenType.IDENTIFIER);
        expectSingleToken("_another1", TokenType.IDENTIFIER);
    }

    @Test
    void ignore_whitespace_on_one_line() {
        expectEof(" \t   \r");
    }

    @Test
    void ignore_comment_on_its_own_line() {
        expectEof("# Test comment");
    }

    @Test
    void ignore_comment_on_line_with_tokens() {
        expectTokenSequence("2 + 2 # Another test comment",
                Arrays.asList(TokenType.NUMBER, TokenType.PLUS, TokenType.NUMBER));
    }

    @Test
    void read_long_sequences_of_tokens() {
        expectTokenSequence("3.0 / 5 + (6 - 7) * 9^4", Arrays.asList(
                TokenType.NUMBER, TokenType.SLASH, TokenType.NUMBER,
                TokenType.PLUS, TokenType.LEFT_PARENTHESIS, TokenType.NUMBER,
                TokenType.MINUS, TokenType.NUMBER, TokenType.RIGHT_PARENTHESIS,
                TokenType.ASTERISK, TokenType.NUMBER, TokenType.CARET,
                TokenType.NUMBER
        ));
        expectTokenSequence("f(x) : [3, true, false]", Arrays.asList(
                TokenType.IDENTIFIER, TokenType.LEFT_PARENTHESIS, TokenType.IDENTIFIER,
                TokenType.RIGHT_PARENTHESIS, TokenType.COLON, TokenType.LEFT_SQUARE_BRACKET,
                TokenType.NUMBER, TokenType.COMMA, TokenType.TRUE,
                TokenType.COMMA, TokenType.FALSE, TokenType.RIGHT_SQUARE_BRACKET
        ));
        expectTokenSequence("object.method(not enabled, count == 5)", Arrays.asList(
                TokenType.IDENTIFIER, TokenType.DOT, TokenType.IDENTIFIER,
                TokenType.LEFT_PARENTHESIS, TokenType.NOT, TokenType.IDENTIFIER,
                TokenType.COMMA, TokenType.IDENTIFIER, TokenType.EQUAL_EQUAL,
                TokenType.NUMBER, TokenType.RIGHT_PARENTHESIS
        ));
        expectTokenSequence("valid = \\x { x <= 100 or x >= 0 and x /= nil }", Arrays.asList(
                TokenType.IDENTIFIER, TokenType.EQUAL, TokenType.BACKSLASH,
                TokenType.IDENTIFIER, TokenType.LEFT_BRACE, TokenType.IDENTIFIER,
                TokenType.LESS_EQUAL, TokenType.NUMBER, TokenType.OR,
                TokenType.IDENTIFIER, TokenType.GREATER_EQUAL, TokenType.NUMBER,
                TokenType.AND, TokenType.IDENTIFIER, TokenType.SLASH_EQUAL,
                TokenType.NIL, TokenType.RIGHT_BRACE
        ));
        expectTokenSequence("\"Hello \" .. username", Arrays.asList(
                TokenType.STRING, TokenType.DOT_DOT, TokenType.IDENTIFIER
        ));
        expectTokenSequence(
                "make_adder = \\n { \\x { x + n } }\nadd_five = make_adder(5)\nprint(add_five(7)) # 12\n",
                Arrays.asList(
                        TokenType.IDENTIFIER, TokenType.EQUAL, TokenType.BACKSLASH,
                        TokenType.IDENTIFIER, TokenType.LEFT_BRACE, TokenType.BACKSLASH,
                        TokenType.IDENTIFIER, TokenType.LEFT_BRACE, TokenType.IDENTIFIER,
                        TokenType.PLUS, TokenType.IDENTIFIER, TokenType.RIGHT_BRACE,
                        TokenType.RIGHT_BRACE, TokenType.IDENTIFIER, TokenType.EQUAL,
                        TokenType.IDENTIFIER, TokenType.LEFT_PARENTHESIS, TokenType.NUMBER,
                        TokenType.RIGHT_PARENTHESIS, TokenType.IDENTIFIER, TokenType.LEFT_PARENTHESIS,
                        TokenType.IDENTIFIER, TokenType.LEFT_PARENTHESIS, TokenType.NUMBER,
                        TokenType.RIGHT_PARENTHESIS, TokenType.RIGHT_PARENTHESIS
                )
        );
    }

    @Test
    void use_maximal_munch_to_differentiate_identifiers_from_keywords() {
        expectSingleToken("andrew", TokenType.IDENTIFIER);
        expectSingleToken("orchid", TokenType.IDENTIFIER);
        expectSingleToken("nothing", TokenType.IDENTIFIER);
        expectSingleToken("nile_river", TokenType.IDENTIFIER);
        expectSingleToken("trueness", TokenType.IDENTIFIER);
        expectSingleToken("falsey", TokenType.IDENTIFIER);
    }

    @Test
    void record_token_line_numbers() {
        expectFinalLineNumber("", 1);
        expectFinalLineNumber("one_line_source = 1", 1);
        expectFinalLineNumber("multiple = 1\nline = \"s\"\nsource = nil", 3);
        expectFinalLineNumber("source_with_multiline_string = \"multi\nline\nstring\"\nx = nil", 4);
    }

    @ParameterizedTest
    @ValueSource(chars = {'@', '$', '~', '!', '?', '`', '%', '|', '&', '\''})
    void report_error_for_unrecognized_characters(char unrecognizedCharacter) {
        StringBuilder source = new StringBuilder("area = pi * r^2");
        source.insert(unrecognizedCharacter % source.length(), unrecognizedCharacter);
        expectErrorType(source.toString(), ErrorType.UNRECOGNIZED_CHARACTER);
    }

    @Test
    void report_error_for_unterminated_string() {
        expectErrorType("\"string", ErrorType.UNTERMINATED_STRING);
    }

    @Test
    void record_lexeme_where_error_occurred() {
        expectErrorNearLexeme("| = 5", "|");
        expectErrorNearLexeme("\"unterminated string", "\"unterminated string");
    }

    @Test
    void record_number_of_line_where_error_occurred() {
        expectErrorOnLine("x = nil\ny = false\n$ = true", 3);
        expectErrorOnLine("a = 1\n\"unfinished", 2);
    }

    @Test
    void extract_contents_of_string_tokens() {
        expectStringTokenValue("\"string\"", "string");
        expectStringTokenValue("\"a string with spaces\"", "a string with spaces");
        expectStringTokenValue("\"string with 1 2 34 numbers\"", "string with 1 2 34 numbers");
        expectStringTokenValue("\"characters like Ʃ and £\"", "characters like Ʃ and £");
        expectStringTokenValue("\"a string\nwith multiple\nlines\"", "a string\nwith multiple\nlines");
    }

    @Test
    void replace_escape_sequences() {
        expectStringTokenValue("\"incoming line break:\\nfinished\"", "incoming line break:\nfinished");
        expectStringTokenValue("\"incoming backslash: \\\\\"", "incoming backslash: \\");
        expectStringTokenValue("\"incoming double quote: \\\"\"", "incoming double quote: \"");
        expectStringTokenValue("\"Multiple \\\\ \\\"escape sequences\\\"\\nabove\"", "Multiple \\ \"escape sequences\"\nabove");
    }

    @Test
    void report_error_for_unknown_escape_sequence() {
        expectErrorType("\"What is a \\x?\"", ErrorType.UNKNOWN_ESCAPE_SEQUENCE);
    }

    @Test
    void extract_contents_of_number_tokens() {
        expectNumberTokenValue("123", 123);
        expectNumberTokenValue("000123", 123);
        expectNumberTokenValue("123.456", 123.456);
        expectNumberTokenValue("0.012500", 0.0125);
        expectNumberTokenValue("00001.23", 1.23);
    }

    private void expectEof(String source) {
        Lexer lexer = new Lexer(source, new DummyIoManager());
        List<Token> tokens = lexer.lexSource();
        assertEquals(TokenType.EOF, tokens.get(0).getType());
    }

    private List<TokenType> getTokenTypes(List<Token> tokens) {
        List<TokenType> tokenTypes = new ArrayList<>();
        for (Token token : tokens) tokenTypes.add(token.getType());
        return tokenTypes;
    }

    private void expectSingleToken(String source, TokenType tokenType) {
        expectTokenSequence(source, Collections.singletonList(tokenType));
    }

    private void expectTokenSequence(String source, List<TokenType> tokenTypes) {
        Lexer lexer = new Lexer(source, new DummyIoManager());
        List<Token> tokens = lexer.lexSource();
        List<TokenType> actualTokenTypes = getTokenTypes(tokens);
        List<TokenType> expectedTokenTypesWithEof = new ArrayList<>(tokenTypes);
        expectedTokenTypesWithEof.add(TokenType.EOF);
        assertEquals(expectedTokenTypesWithEof, actualTokenTypes);
    }

    private void expectFinalLineNumber(String source, int finalLineNumber) {
        Lexer lexer = new Lexer(source, new DummyIoManager());
        List<Token> tokens = lexer.lexSource();
        assertEquals(finalLineNumber, tokens.get(tokens.size() - 1).getLineNumber());
    }

    private void expectErrorType(String source, ErrorType errorType) {
        TestIoManager ioManager = new TestIoManager();
        Lexer lexer = new Lexer(source, ioManager);
        assertThrows(UnwindingException.class, lexer::lexSource);
        assertEquals(errorType, ioManager.getMostRecentErrorType());
    }

    private void expectErrorNearLexeme(String source, String lexeme) {
        TestIoManager ioManager = new TestIoManager();
        Lexer lexer = new Lexer(source, ioManager);
        assertThrows(UnwindingException.class, lexer::lexSource);
        assertEquals(lexeme, ioManager.getMostRecentErrorLexeme());
    }

    private void expectErrorOnLine(String source, int lineNumber) {
        TestIoManager ioManager = new TestIoManager();
        Lexer lexer = new Lexer(source, ioManager);
        assertThrows(UnwindingException.class, lexer::lexSource);
        assertEquals(lineNumber, ioManager.getMostRecentErrorLineNumber());
    }

    private void expectStringTokenValue(String source, String value) {
        Lexer lexer = new Lexer(source, new DummyIoManager());
        List<Token> tokens = lexer.lexSource();
        assertEquals(2, tokens.size());
        assertEquals(value, tokens.get(0).getLiteralValue());
    }

    private void expectNumberTokenValue(String source, double value) {
        Lexer lexer = new Lexer(source, new DummyIoManager());
        List<Token> tokens = lexer.lexSource();
        assertEquals(2, tokens.size());
        assertEquals(value, tokens.get(0).getLiteralValue());
    }
}
