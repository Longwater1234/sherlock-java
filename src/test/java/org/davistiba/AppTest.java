package org.davistiba;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void testDoSearch_Works() throws InterruptedException, ExecutionException {
        int statusCode = App.doSearch("longwater1234", "https://github.com/%").get().statusCode();
        Assertions.assertEquals(200, statusCode);
    }

    @Test
    public void testDoSearch_NotFound() throws InterruptedException, ExecutionException {
        int statusCode = App.doSearch("xlongwater1234", "https://github.com/%").get().statusCode();
        Assertions.assertEquals(404, statusCode);
    }
}
