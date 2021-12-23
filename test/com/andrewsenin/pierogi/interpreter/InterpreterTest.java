package com.andrewsenin.pierogi.interpreter;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {
    @Test
    void zero_division_causes_runtime_error() {

    }

    @Test
    void equality_checks_dont_coerce_types() {

    }

    @Test
    void what() {
        List<Integer> l = new ArrayList<>(Arrays.asList(1, 2, 3));
        List<Integer> m = l;
        m.add(4);
        assertEquals(Arrays.asList(1, 2, 3), l);
        assertEquals(Arrays.asList(1, 2, 3, 4), m);
    }

    // TODO: and and or short circuit
}
