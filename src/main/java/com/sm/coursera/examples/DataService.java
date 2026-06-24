package com.sm.coursera.examples;

/** Dependency of {@link BusinessLayer}. In the Mockito example we mock this
 *  instead of talking to a real database. */
public interface DataService {
    int[] retrieveAllData();
}