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
public class RecommendationsApiTest {

    private static MockWebServer mockWebServer;

    private RecommendationsApi recommendationsApi;

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
        recommendationsApi = spy(new RecommendationsApi(Robolectric.application));
        doReturn(mockWebServer.getUrl("/").toString()).when(recommendationsApi).getString(R.string.gf_api_base_url);
    }

    @Test
    public void testAwaitRecommendationsByAll() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);

        Recommendation r1 = new Recommendation();
        r1.setType(Recommendation.Type.SELF);
        r1.setReason("You liked Bowling Alley");
        r1.setPoi(new Poi());
        r1.getPoi().setName("Buzzyland");
        r1.getPoi().setLocation(new Location());
        r1.getPoi().getLocation().setLatitude(19.03482813065687);
        r1.getPoi().getLocation().setLongitude(-98.22346338561037);
        r1.getPoi().setFoursquareId("5005ecb4e4b0cd5210320f38");
        r1.getPoi().setCategories(new Category[1]);
        r1.getPoi().getCategories()[0] = new Category();
        r1.getPoi().getCategories()[0].setName("Bowling Alley");
        r1.getPoi().getCategories()[0].setIconUrl("https://ss3.4sqi.net/img/categories_v2/arts_entertainment/default_88.png");
        r1.getPoi().getCategories()[0].setFoursquareId("4bf58dd8d48988d1e4931735");

        Recommendation r2 = new Recommendation();
        r2.setType(Recommendation.Type.SOCIAL);
        r2.setReason("Your friend Jane Doe liked Seafood Restaurant");
        r2.setPoi(new Poi());
        r2.getPoi().setName("Fisher's Puebla");
        r2.getPoi().setLocation(new Location());
        r2.getPoi().getLocation().setLatitude(19.04873185577618);
        r2.getPoi().getLocation().setLongitude(-98.21319222450256);
        r2.getPoi().setFoursquareId("4ba69285f964a520615f39e3");
        r2.getPoi().setCategories(new Category[1]);
        r2.getPoi().getCategories()[0] = new Category();
        r2.getPoi().getCategories()[0].setName("Seafood Restaurant");
        r2.getPoi().getCategories()[0].setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/default_88.png");
        r2.getPoi().getCategories()[0].setFoursquareId("4bf58dd8d48988d1ce941735");

        Recommendation[] expected = {r1, r2};

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setBody(getJsonFrom("responses/recommendations_byall.json")));
        assertThat(recommendationsApi.awaitRecommendationsByAll("fakeAccessToken", location, 3000)).isNotNull().isNotEmpty().hasSize(expected.length).isEqualTo(expected);
    }

    @Test(expected = ApiException.class)
    public void testAwaitRecommendationsByAllError() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/error.json")));
        recommendationsApi.awaitRecommendationsByAll("fakeAccessToken", location, 3000);
    }

    @Test
    public void testAwaitRecommendationsByLikes() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);

        Recommendation r1 = new Recommendation();
        r1.setType(Recommendation.Type.SELF);
        r1.setReason("You liked Bowling Alley");
        r1.setPoi(new Poi());
        r1.getPoi().setName("Buzzyland");
        r1.getPoi().setLocation(new Location());
        r1.getPoi().getLocation().setLatitude(19.03482813065687);
        r1.getPoi().getLocation().setLongitude(-98.22346338561037);
        r1.getPoi().setFoursquareId("5005ecb4e4b0cd5210320f38");
        r1.getPoi().setCategories(new Category[1]);
        r1.getPoi().getCategories()[0] = new Category();
        r1.getPoi().getCategories()[0].setName("Bowling Alley");
        r1.getPoi().getCategories()[0].setIconUrl("https://ss3.4sqi.net/img/categories_v2/arts_entertainment/default_88.png");
        r1.getPoi().getCategories()[0].setFoursquareId("4bf58dd8d48988d1e4931735");

        Recommendation[] expected = {r1};

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setBody(getJsonFrom("responses/recommendations_bylikes.json")));
        assertThat(recommendationsApi.awaitRecommendationsByLikes("fakeAccessToken", location, 3000)).isNotNull().isNotEmpty().hasSize(expected.length).isEqualTo(expected);
    }

    @Test(expected = ApiException.class)
    public void testAwaitRecommendationsByLikesError() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/error.json")));
        recommendationsApi.awaitRecommendationsByLikes("fakeAccessToken", location, 3000);
    }

    @Test
    public void testAwaitRecommendationsByFriendsLikes() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);

        Recommendation r1 = new Recommendation();
        r1.setType(Recommendation.Type.SOCIAL);
        r1.setReason("Your friend Jane Doe liked Seafood Restaurant");
        r1.setPoi(new Poi());
        r1.getPoi().setName("Fisher's Puebla");
        r1.getPoi().setLocation(new Location());
        r1.getPoi().getLocation().setLatitude(19.04873185577618);
        r1.getPoi().getLocation().setLongitude(-98.21319222450256);
        r1.getPoi().setFoursquareId("4ba69285f964a520615f39e3");
        r1.getPoi().setCategories(new Category[1]);
        r1.getPoi().getCategories()[0] = new Category();
        r1.getPoi().getCategories()[0].setName("Seafood Restaurant");
        r1.getPoi().getCategories()[0].setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/default_88.png");
        r1.getPoi().getCategories()[0].setFoursquareId("4bf58dd8d48988d1ce941735");

        Recommendation[] expected = {r1};

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setBody(getJsonFrom("responses/recommendations_byfriendslikes.json")));
        assertThat(recommendationsApi.awaitRecommendationsByFriendsLikes("fakeAccessToken", location, 3000)).isNotNull().isNotEmpty().hasSize(expected.length).isEqualTo(expected);
    }

    @Test(expected = ApiException.class)
    public void testAwaitRecommendationsByFriendsLikesError() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/error.json")));
        recommendationsApi.awaitRecommendationsByFriendsLikes("fakeAccessToken", location, 3000);
    }

    @Test
    public void testAwaitNextPois() throws Exception {
        Poi seed = new Poi();
        seed.setName("Fisher's Puebla");
        seed.setLocation(new Location());
        seed.getLocation().setLatitude(19.04873185577618);
        seed.getLocation().setLongitude(-98.21319222450256);
        seed.setFoursquareId("4ba69285f964a520615f39e3");
        seed.setCategories(new Category[1]);
        seed.getCategories()[0] = new Category();
        seed.getCategories()[0].setName("Seafood Restaurant");
        seed.getCategories()[0].setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/default_88.png");
        seed.getCategories()[0].setFoursquareId("4bf58dd8d48988d1ce941735");

        Poi poi1 = new Poi();
        poi1.setName("Cinépolis");
        poi1.setLocation(new Location());
        poi1.getLocation().setLatitude(19.032099226143384);
        poi1.getLocation().setLongitude(-98.23300838470459);
        poi1.setFoursquareId("4bad0850f964a52082263be3");
        poi1.setCategories(new Category[1]);
        poi1.getCategories()[0] = new Category();
        poi1.getCategories()[0].setName("Multiplex");
        poi1.getCategories()[0].setIconUrl("https://ss3.4sqi.net/img/categories_v2/arts_entertainment/movietheater_88.png");
        poi1.getCategories()[0].setFoursquareId("4bf58dd8d48988d180941735");

        Poi poi2 = new Poi();
        poi2.setName("Chili's");
        poi2.setLocation(new Location());
        poi2.getLocation().setLatitude(19.032072262618215);
        poi2.getLocation().setLongitude(-98.23318352096007);
        poi2.setFoursquareId("4be47d022457a593414daa15");
        poi2.setCategories(new Category[1]);
        poi2.getCategories()[0] = new Category();
        poi2.getCategories()[0].setName("American Restaurant");
        poi2.getCategories()[0].setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/default_88.png");
        poi2.getCategories()[0].setFoursquareId("4bf58dd8d48988d14e941735");

        Poi[] expected = {poi1, poi2};

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setBody(getJsonFrom("responses/recommendations_nextpois.json")));
        assertThat(recommendationsApi.awaitNextPois("fakeAccessToken", seed)).isNotNull().isNotEmpty().hasSize(expected.length).isEqualTo(expected);
    }

    @Test(expected = ApiException.class)
    public void testAwaitNextPoisError() throws Exception {
        Poi seed = new Poi();
        seed.setName("Fisher's Puebla");
        seed.setLocation(new Location());
        seed.getLocation().setLatitude(19.04873185577618);
        seed.getLocation().setLongitude(-98.21319222450256);
        seed.setFoursquareId("4ba69285f964a520615f39e3");
        seed.setCategories(new Category[1]);
        seed.getCategories()[0] = new Category();
        seed.getCategories()[0].setName("Seafood Restaurant");
        seed.getCategories()[0].setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/default_88.png");
        seed.getCategories()[0].setFoursquareId("4bf58dd8d48988d1ce941735");

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/error.json")));
        recommendationsApi.awaitNextPois("fakeAccessToken", seed);
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