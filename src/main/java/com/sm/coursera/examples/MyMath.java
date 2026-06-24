package com.sm.coursera.examples;

/** Plain class under test — used by the JUnit example. */
public class MyMath {

    public int calculateSum(int[] numbers) {
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return sum;
    }
}