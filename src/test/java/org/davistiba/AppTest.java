package org.davistiba;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void testDoSearch() throws InterruptedException, ExecutionException {
        int statusCode = App.doSearch("longwater1234", "https://github.com/%").get().statusCode();
        assertEquals(200, statusCode);
    }
}
