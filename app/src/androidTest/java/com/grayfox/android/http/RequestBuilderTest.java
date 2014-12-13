package com.grayfox.android.http;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RequestBuilderTest {

    @Test
    public void testMakeRequest() throws Exception {
        Integer result = new RequestBuilder("http://www.google.com").setMethod(Method.GET).make();
        assertThat(result).isNotNull().isBetween(200, 201);
    }

    @Test
    public void testMakeRequestForResult() throws Exception {
        String result = new RequestBuilder("http://www.google.com").setMethod(Method.GET).makeForResult();
        assertThat(result).isNotNull().isNotEmpty();
    }
}