package com.grayfox.android.client;

import com.google.inject.Injector;
import com.grayfox.android.client.model.Location;
import com.grayfox.android.config.ConfigModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;

import roboguice.RoboGuice;

import static org.assertj.core.api.Assertions.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RecommendationsApiTest {

    @Inject private RecommendationsApi recommendationsApi;

    @Before
    public void setUp() throws Exception {
        Injector injector = RoboGuice.overrideApplicationInjector(Robolectric.application, new ConfigModule(Robolectric.application));
        injector.injectMembers(this);
        assertThat(recommendationsApi).isNotNull();
        Robolectric.clearPendingHttpResponses();
    }

    @Test
    public void testAwaitRecommendationsByLikes() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);

        Robolectric.addPendingHttpResponse(200, getJsonFrom("responses/recommendations_bylikes.json"));
        assertThat(recommendationsApi.awaitRecommendationsByLikes("fakeAccessToken", location, 3000, RecommendationsApi.Transportation.WALKING)).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test(expected = ApiException.class)
    public void testAwaitRecommendationsByLikesError() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);

        Robolectric.addPendingHttpResponse(401, getJsonFrom("responses/error.json"));
        recommendationsApi.awaitRecommendationsByLikes("fakeAccessToken", location, 3000, RecommendationsApi.Transportation.WALKING);
    }

    @Test
    public void testAwaitRecommendationsByFriendsLikes() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);

        Robolectric.addPendingHttpResponse(200, getJsonFrom("responses/recommendations_byfriendslikes.json"));
        assertThat(recommendationsApi.awaitRecommendationsByFriendsLikes("fakeAccessToken", location, 3000, RecommendationsApi.Transportation.WALKING)).isNotNull().isNotEmpty().hasSize(2);
    }

    @Test(expected = ApiException.class)
    public void testAwaitRecommendationsByFriendsLikesError() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);

        Robolectric.addPendingHttpResponse(401, getJsonFrom("responses/error.json"));
        recommendationsApi.awaitRecommendationsByFriendsLikes("fakeAccessToken", location, 3000, RecommendationsApi.Transportation.WALKING);
    }

    private String getJsonFrom(String file) throws Exception {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line).append('\n');
        br.close();
        return sb.toString();
    }
}