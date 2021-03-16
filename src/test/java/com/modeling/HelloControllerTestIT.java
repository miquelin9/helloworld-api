package com.modeling;

import io.smallrye.common.constraint.Assert;
import org.junit.jupiter.api.Test;

public class HelloControllerTestIT {

    @Test
    public void testHelloEndpoint() {
        Assert.assertTrue(new HelloController().hello().equals("Hello World, i'm the change number 2!"));
    }
}