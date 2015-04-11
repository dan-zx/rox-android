package com.grayfox.android.client;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.grayfox.android.client.model.Category;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.client.model.Poi;
import com.grayfox.android.client.model.Recommendation;

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
public class PoisApiTest {

    private static MockWebServer mockWebServer;

    private PoisApi poisApi;

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
        poisApi = spy(new PoisApi(Robolectric.application));
        doReturn(mockWebServer.getUrl("/").toString()).when(poisApi).getString(R.string.gf_api_base_url);
    }

    @Test
    public void testSearchByCategory() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);

        Category category = new Category();
        category.setName("Coffee Shop");
        category.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/default_88.png");
        category.setFoursquareId("4bf58dd8d48988d1e0931735");

        Poi poi1 = new Poi();
        poi1.setName("Starbucks");
        poi1.setLocation(new Location());
        poi1.getLocation().setLatitude(19.051916205742696);
        poi1.getLocation().setLongitude(-98.21938276290894);
        poi1.setFoursquareId("4c2ec541ac0ab713fa9b1b1e");
        poi1.setFoursquareRating(7.4);
        poi1.setCategories(new Category[]{category});

        Poi poi2 = new Poi();
        poi2.setName("Profética - Casa de la Lectura");
        poi2.setLocation(new Location());
        poi2.getLocation().setLatitude(19.04288527071933);
        poi2.getLocation().setLongitude(-98.20153534412384);
        poi2.setFoursquareId("4baace32f964a520a7873ae3");
        poi2.setFoursquareRating(8.9);
        poi2.setCategories(new Category[]{category});

        Poi[] expectedResponse = {poi1, poi2};

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/pois.json")));
        Poi[] actualResponse = poisApi.awaitSearchByCategory(location, 20_000, category.getFoursquareId());

        assertThat(actualResponse).isNotNull().isNotEmpty().hasSize(expectedResponse.length).isEqualTo(expectedResponse);
    }

    @Test(expected = ApiException.class)
    public void testSearchByCategoryError() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/error.json")));
        poisApi.awaitSearchByCategory(location, 20_000, "fakeId");
    }

    @Test
    public void testAwaitNextPois() throws Exception {
        Poi seed = new Poi();
        seed.setName("Fisher's Puebla");
        seed.setLocation(new Location());
        seed.getLocation().setLatitude(19.04873185577618);
        seed.getLocation().setLongitude(-98.21319222450256);
        seed.setFoursquareId("4ba69285f964a520615f39e3");
        seed.setFoursquareRating(8.3);
        seed.setCategories(new Category[1]);
        seed.getCategories()[0] = new Category();
        seed.getCategories()[0].setName("Seafood Restaurant");
        seed.getCategories()[0].setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/default_88.png");
        seed.getCategories()[0].setFoursquareId("4bf58dd8d48988d1ce941735");

        Category category = new Category();
        category.setName("Coffee Shop");
        category.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/default_88.png");
        category.setFoursquareId("4bf58dd8d48988d1e0931735");

        Poi poi1 = new Poi();
        poi1.setName("Starbucks");
        poi1.setLocation(new Location());
        poi1.getLocation().setLatitude(19.051916205742696);
        poi1.getLocation().setLongitude(-98.21938276290894);
        poi1.setFoursquareId("4c2ec541ac0ab713fa9b1b1e");
        poi1.setFoursquareRating(7.4);
        poi1.setCategories(new Category[]{category});

        Poi poi2 = new Poi();
        poi2.setName("Profética - Casa de la Lectura");
        poi2.setLocation(new Location());
        poi2.getLocation().setLatitude(19.04288527071933);
        poi2.getLocation().setLongitude(-98.20153534412384);
        poi2.setFoursquareId("4baace32f964a520a7873ae3");
        poi2.setFoursquareRating(8.9);
        poi2.setCategories(new Category[]{category});

        Poi[] expected = {poi1, poi2};

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setBody(getJsonFrom("responses/pois.json")));
        assertThat(poisApi.awaitNextPois(seed)).isNotNull().isNotEmpty().hasSize(expected.length).isEqualTo(expected);
    }

    @Test(expected = ApiException.class)
    public void testAwaitNextPoisError() throws Exception {
        Poi seed = new Poi();
        seed.setName("Fisher's Puebla");
        seed.setLocation(new Location());
        seed.getLocation().setLatitude(19.04873185577618);
        seed.getLocation().setLongitude(-98.21319222450256);
        seed.setFoursquareId("4ba69285f964a520615f39e3");
        seed.setFoursquareRating(8.3);
        seed.setCategories(new Category[1]);
        seed.getCategories()[0] = new Category();
        seed.getCategories()[0].setName("Seafood Restaurant");
        seed.getCategories()[0].setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/default_88.png");
        seed.getCategories()[0].setFoursquareId("4bf58dd8d48988d1ce941735");

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/error.json")));
        poisApi.awaitNextPois(seed);
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
        Category[] actualResponse = poisApi.awaitCategoriesLikeName("rest");

        assertThat(actualResponse).isNotNull().isNotEmpty().hasSize(expectedResponse.length).isEqualTo(expectedResponse);
    }

    @Test(expected = ApiException.class)
    public void testAwaitCategoriesLikeNameError() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/error.json")));
        poisApi.awaitCategoriesLikeName("rest");
    }

    @Test
    public void testAwaitRecommendations() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);

        Recommendation r1 = new Recommendation();
        r1.setType(Recommendation.Type.SELF);
        r1.setReason("You liked Italian Restaurant");
        r1.setPoi(new Poi());
        r1.getPoi().setName("Vittorio's");
        r1.getPoi().setLocation(new Location());
        r1.getPoi().getLocation().setLatitude(19.043296008919373);
        r1.getPoi().getLocation().setLongitude(-98.19728136062622);
        r1.getPoi().setFoursquareId("4c22837f9a67a593219edc87");
        r1.getPoi().setFoursquareRating(8.6);
        r1.getPoi().setCategories(new Category[1]);
        r1.getPoi().getCategories()[0] = new Category();
        r1.getPoi().getCategories()[0].setName("Italian Restaurant");
        r1.getPoi().getCategories()[0].setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/default_88.png");
        r1.getPoi().getCategories()[0].setFoursquareId("4bf58dd8d48988d110941735");

        Recommendation r2 = new Recommendation();
        r2.setType(Recommendation.Type.SOCIAL);
        r2.setReason("Your friend Jane Doe liked Brewery");
        r2.setPoi(new Poi());
        r2.getPoi().setName("El Corona");
        r2.getPoi().setLocation(new Location());
        r2.getPoi().getLocation().setLatitude(19.046120299277323);
        r2.getPoi().getLocation().setLongitude(-98.20206759449505);
        r2.getPoi().setFoursquareId("4ce636f3708460fc485e86c3");
        r2.getPoi().setFoursquareRating(8.2);
        r2.getPoi().setCategories(new Category[1]);
        r2.getPoi().getCategories()[0] = new Category();
        r2.getPoi().getCategories()[0].setName("Brewery");
        r2.getPoi().getCategories()[0].setIconUrl("https://ss3.4sqi.net/img/categories_v2/nightlife/default_88.png");
        r2.getPoi().getCategories()[0].setFoursquareId("50327c8591d4c4b30a586d5d");

        Recommendation r3 = new Recommendation();
        r3.setType(Recommendation.Type.GLOBAL);
        r3.setReason("This place is popular");
        r3.setPoi(new Poi());
        r3.getPoi().setName("La Pasita");
        r3.getPoi().setLocation(new Location());
        r3.getPoi().getLocation().setLatitude(19.04104454259915);
        r3.getPoi().getLocation().setLongitude(-98.19588661193848);
        r3.getPoi().setFoursquareId("4c095fd97e3fc928ddddf182");
        r3.getPoi().setFoursquareRating(9.0);
        r3.getPoi().setCategories(new Category[1]);
        r3.getPoi().getCategories()[0] = new Category();
        r3.getPoi().getCategories()[0].setName("Cocktail Bar");
        r3.getPoi().getCategories()[0].setIconUrl("https://ss3.4sqi.net/img/categories_v2/nightlife/default_88.png");
        r3.getPoi().getCategories()[0].setFoursquareId("4bf58dd8d48988d11e941735");

        Recommendation[] expected = {r1, r2, r3};

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setBody(getJsonFrom("responses/recommendations.json")));
        assertThat(poisApi.awaitRecommendations("fakeAccessToken", location, 3000)).isNotNull().isNotEmpty().hasSize(expected.length).isEqualTo(expected);
    }

    @Test(expected = ApiException.class)
    public void testAwaitRecommendationsError() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/error.json")));
        poisApi.awaitRecommendations("fakeAccessToken", location, 3000);
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