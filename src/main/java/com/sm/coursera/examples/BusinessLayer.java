package com.sm.coursera.examples;

/** Class under test — its logic depends on {@link DataService}. */
public class BusinessLayer {

    private final DataService dataService;

    public BusinessLayer(DataService dataService) {
        this.dataService = dataService;
    }

    /** Returns the largest value from the data service, or 0 when empty. */
    public int findTheGreatest() {
        int[] data = dataService.retrieveAllData();
        int greatest = 0;
        for (int value : data) {
            if (value > greatest) {
                greatest = value;
            }
        }
        return greatest;
    }
}