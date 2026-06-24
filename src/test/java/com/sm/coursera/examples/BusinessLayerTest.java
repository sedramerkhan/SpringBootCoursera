package com.sm.coursera.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

/**
 * Mockito example.
 *
 * BusinessLayer depends on DataService. Instead of a real database (or a hand-written
 * stub class implementing every method), we mock the interface and stub only the one
 * method the test needs with when(...).thenReturn(...).
 */
class BusinessLayerTest {

    @Test
    void findTheGreatest_basic() {
        DataService dataServiceMock = mock(DataService.class);
        when(dataServiceMock.retrieveAllData()).thenReturn(new int[] {25, 15, 5});

        BusinessLayer businessLayer = new BusinessLayer(dataServiceMock);

        assertEquals(25, businessLayer.findTheGreatest());
    }

    @Test
    void findTheGreatest_oneItem() {
        DataService dataServiceMock = mock(DataService.class);
        when(dataServiceMock.retrieveAllData()).thenReturn(new int[] {10});

        assertEquals(10, new BusinessLayer(dataServiceMock).findTheGreatest());
    }

    @Test
    void findTheGreatest_empty() {
        DataService dataServiceMock = mock(DataService.class);
        when(dataServiceMock.retrieveAllData()).thenReturn(new int[] {});

        assertEquals(0, new BusinessLayer(dataServiceMock).findTheGreatest());
    }
}