package com.andrewsenin.pierogi.parser;

import com.andrewsenin.pierogi.ast.*;
import com.andrewsenin.pierogi.io.DummyIoManager;
import com.andrewsenin.pierogi.lexer.Lexer;
import com.andrewsenin.pierogi.lexer.Token;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {
    @Test
    void parse_valueless_literals() {
        assertEquals(new NilExpression(), parseSingleLine("nil"));
        assertEquals(new TrueExpression(), parseSingleLine("true"));
        assertEquals(new FalseExpression(), parseSingleLine("false"));
    }

    @Test
    void parse_number() {
        Expression ast = parseSingleLine("5");
        assertEquals(numeralOf(5), ast);
    }

    @Test
    void parse_simple_group() {
        Expression ast = parseSingleLine("(5)");
        assertEquals(new GroupExpression(numeralOf(5)), ast);
    }

    @Test
    void parse_arithmetic_negation() {
        Expression ast = parseSingleLine("-5");
        assertEquals(new NegationExpression(numeralOf(5)), ast);
    }

    @Test
    void parse_logical_negation() {
        Expression ast = parseSingleLine("not true");
        assertEquals(new NotExpression(new TrueExpression()), ast);
    }

    @Test
    void parse_simple_addition() {
        Expression ast = parseSingleLine("2 + 3");
        assertEquals(new AdditionExpression(numeralOf(2), numeralOf(3)), ast);
    }

    @Test
    void parse_simple_subtraction() {
        Expression ast = parseSingleLine("2 - 3");
        assertEquals(new SubtractionExpression(numeralOf(2), numeralOf(3)), ast);
    }

    @Test
    void parse_simple_multiplication() {
        Expression ast = parseSingleLine("2 * 3");
        assertEquals(new MultiplicationExpression(numeralOf(2), numeralOf(3)), ast);
    }

    @Test
    void parse_simple_division() {
        Expression ast = parseSingleLine("2 / 3");
        assertEquals(new DivisionExpression(numeralOf(2), numeralOf(3)), ast);
    }

    @Test
    void parse_simple_exponentiation() {
        Expression ast = parseSingleLine("2 ^ 3");
        assertEquals(new ExponentExpression(numeralOf(2), numeralOf(3)), ast);
    }

    @Test
    void parse_complex_arithmetic_expressions() {
        // TODO: add arithmetic negations and exponentiation
        Expression ast;
        ast = parseSingleLine("2 + 3 * 4");
        assertEquals(new AdditionExpression(numeralOf(2), new MultiplicationExpression(numeralOf(3), numeralOf(4))), ast);
        ast = parseSingleLine("2 * 3 / 4 - 7");
        assertEquals(new SubtractionExpression(new DivisionExpression(new MultiplicationExpression(numeralOf(2), numeralOf(3)), numeralOf(4)), numeralOf(7)), ast);
        ast = parseSingleLine("2 * (3 + (78 - 2)) + 1");
        assertEquals(new AdditionExpression(new MultiplicationExpression(numeralOf(2), new GroupExpression(new AdditionExpression(numeralOf(3), new GroupExpression(new SubtractionExpression(numeralOf(78), numeralOf(2)))))), numeralOf(1)), ast);
    }

    @Test
    void parse_string() {
        Expression ast = parseSingleLine("\"string\"");
        assertEquals(new StringExpression("string"), ast);
    }

    @Test
    void parse_list_literals() {
        Expression ast;
        ast = parseSingleLine("[]");
        assertEquals(new ListExpression(Collections.emptyList()), ast);
        ast = parseSingleLine("[5]");
        assertEquals(new ListExpression(Collections.singletonList(numeralOf(5))), ast);
        ast = parseSingleLine("[5, 6]");
        assertEquals(new ListExpression(Arrays.asList(numeralOf(5), numeralOf(6))), ast);
        ast = parseSingleLine("[5, 6, 7, 8, 9, 10]");
        assertEquals(new ListExpression(Arrays.asList(numeralOf(5), numeralOf(6), numeralOf(7), numeralOf(8), numeralOf(9), numeralOf(10))), ast);
        ast = parseSingleLine("[\"hello\", 4+5, [[3, 9]], ---006.7]");
        assertEquals(new ListExpression(Arrays.asList(
                new StringExpression("hello"),
                new AdditionExpression(numeralOf(4), numeralOf(5)),
                new ListExpression(Collections.singletonList(new ListExpression(Arrays.asList(numeralOf(3), numeralOf(9))))),
                new NegationExpression(new NegationExpression(new NegationExpression(numeralOf(6.7))))
        )), ast);
    }

    @Test
    void parse_simple_comparison() {
        Expression ast;
        ast = parseSingleLine("2 < 3");
        assertEquals(new LessThanExpression(numeralOf(2), numeralOf(3)), ast);
        ast = parseSingleLine("2 > 3");
        assertEquals(new GreaterThanExpression(numeralOf(2), numeralOf(3)), ast);
        ast = parseSingleLine("2 <= 3");
        assertEquals(new LessEqualExpression(numeralOf(2), numeralOf(3)), ast);
        ast = parseSingleLine("2 >= 3");
        assertEquals(new GreaterEqualExpression(numeralOf(2), numeralOf(3)), ast);
        ast = parseSingleLine("2 == 3");
        assertEquals(new EqualsExpression(numeralOf(2), numeralOf(3)), ast);
        ast = parseSingleLine("2 /= 3");
        assertEquals(new NotEqualExpression(numeralOf(2), numeralOf(3)), ast);
    }

    @Test
    void parse_simple_logic() {
        Expression ast;
        ast = parseSingleLine("true and false");
        assertEquals(new AndExpression(new TrueExpression(), new FalseExpression()), ast);
        ast = parseSingleLine("false or true");
        assertEquals(new OrExpression(new FalseExpression(), new TrueExpression()), ast);
    }

    @Test
    void parse_simple_string_concatenation() {
        Expression ast = parseSingleLine("\"hello\" .. \"world\"");
        assertEquals(new ConcatenationExpression(new StringExpression("hello"), new StringExpression("world")), ast);
    }

    @Test
    void parse_simple_list_construction() {
        Expression ast;
        ast = parseSingleLine("1 : [2, 3]");
        assertEquals(new ConsExpression(numeralOf(1), new ListExpression(Arrays.asList(numeralOf(2), numeralOf(3)))), ast);
        ast = parseSingleLine("1 : 2 : [3]");
        assertEquals(new ConsExpression(numeralOf(1), new ConsExpression(numeralOf(2), new ListExpression(Collections.singletonList(numeralOf(3))))), ast);
    }

    @Test
    void parse_simple_definition() {
        Expression ast = parseSingleLine("x = 5");
        assertEquals(new DefinitionExpression("x", numeralOf(5)), ast);
    }

    @Test
    void parse_simple_if() {
        Expression ast = parseSingleLine("if true { 1 } else { 0 }");
        assertEquals(new IfExpression(new TrueExpression(), Collections.singletonList(numeralOf(1)), Collections.singletonList(numeralOf(0))), ast);
    }

    @Test
    void parse_simple_call() {
        Expression ast;
        ast = parseSingleLine("function()");
        assertEquals(new CallExpression(new IdentifierExpression("function"), Collections.emptyList()), ast);
        ast = parseSingleLine("function(1)");
        assertEquals(new CallExpression(new IdentifierExpression("function"), Collections.singletonList(numeralOf(1))), ast);
        ast = parseSingleLine("function(1, 2, 3)");
        assertEquals(new CallExpression(new IdentifierExpression("function"), Arrays.asList(numeralOf(1), numeralOf(2), numeralOf(3))), ast);
    }

//    @Test
//    void reject_leading_decimal_point() {
//        Expression ast = parseSource(".123");
//    }

    private static NumberExpression numeralOf(double d) {
        return new NumberExpression(d);
    }

    private Expression parseSingleLine(String source) {
        Lexer lexer = new Lexer(source, new DummyIoManager());
        List<Token> tokens = lexer.lexSource();
        Parser parser = new Parser(tokens, new DummyIoManager());
        return parser.parseTokens().get(0);
    }
}

// TODO: (hello = 5) = 6 should be an error
// Reject leading decimal points like .123 and trailing ones like 456.
// unmatched paren error
// should reject [1,2,3,]
// nest existing operators, verify precedences
// nest list literal values
// identifier 123abc should cause error
// test all operator precedences
// more complex logical expressions
// all nodes that can cause errors should contain line numbers