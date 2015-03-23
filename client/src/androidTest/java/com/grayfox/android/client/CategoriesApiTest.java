package com.grayfox.android.client;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.grayfox.android.client.model.Category;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class CategoriesApiTest {

    private static MockWebServer mockWebServer;

    private CategoriesApi categoriesApi;

    @BeforeClass
    public static void setUpClass() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.play();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        mockWebServer.shutdown();
    }

    @Before
    public void setUp() throws Exception {
        categoriesApi = spy(new CategoriesApi(Robolectric.application));
        String mockHost = mockWebServer.getUrl("/").toString().replaceAll("http://", "");
        doReturn(mockHost).when(categoriesApi).getString(R.string.gf_api_host);
    }

    @Test
    public void testAwaitCategoriesLikeName() throws Exception {
        Category category1 = new Category();
        category1.setName("Argentinian Restaurant");
        category1.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/default_88.png");
        category1.setFoursquareId("4bf58dd8d48988d107941735");

        Category category2 = new Category();
        category2.setName("Mexican Restaurant");
        category2.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/default_88.png");
        category2.setFoursquareId("4bf58dd8d48988d1c1941735");

        Category[] expectedResponse = {category1, category2};

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setBody(getJsonFrom("responses/categories.json")));
        Category[] actualResponse = categoriesApi.awaitCategoriesLikeName("rest");

        assertThat(actualResponse).isNotNull().isNotEmpty().hasSize(expectedResponse.length).isEqualTo(expectedResponse);
    }

    @Test(expected = ApiException.class)
    public void testAwaitCategoriesLikeNameError() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/error.json")));
        categoriesApi.awaitCategoriesLikeName("rest");
    }

    private String getJsonFrom(String file) throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line).append('\n');
        br.close();
        return sb.toString();
    }
}