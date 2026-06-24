package com.sm.coursera.examples;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit 5 example.
 *
 * Shows the @Test annotation, common assertions, and the lifecycle hooks.
 * Lifecycle order: @BeforeAll -> (@BeforeEach -> @Test -> @AfterEach) per test -> @AfterAll.
 */
class MyMathTest {

    private MyMath math;

    @BeforeAll
    static void beforeAll() {
        // runs once, before any test — must be static
    }

    @BeforeEach
    void setUp() {
        // runs before every test — fresh instance keeps tests independent
        math = new MyMath();
    }

    @AfterEach
    void tearDown() {
        // runs after every test (cleanup)
    }

    @AfterAll
    static void afterAll() {
        // runs once, after all tests — must be static
    }

    @Test
    void calculateSum_threeNumbers() {
        assertEquals(15, math.calculateSum(new int[] {2, 3, 10}));
    }

    @Test
    void calculateSum_emptyArray() {
        assertEquals(0, math.calculateSum(new int[] {}));
    }

    @Test
    void otherAssertions() {
        assertTrue(math.calculateSum(new int[] {1, 1}) == 2);
        assertArrayEquals(new int[] {1, 2}, new int[] {1, 2});
    }
}