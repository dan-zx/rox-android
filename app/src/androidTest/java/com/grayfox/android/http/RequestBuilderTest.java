package com.grayfox.android.http;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RequestBuilderTest {

    @Before
    public void setUp() throws Exception {
        Robolectric.getFakeHttpLayer().interceptHttpRequests(false);
    }

    @Test
    public void testMakeRequest() throws Exception {
        Integer result = RequestBuilder.newInstance("http://www.google.com").setMethod(Method.GET).make();
        assertThat(result).isNotNull().isBetween(200, 201);
    }

    @Test
    public void testMakeRequestForResult() throws Exception {
        String result = RequestBuilder.newInstance("http://www.google.com").setMethod(Method.GET).makeForResult();
        assertThat(result).isNotNull().isNotEmpty();
    }
}