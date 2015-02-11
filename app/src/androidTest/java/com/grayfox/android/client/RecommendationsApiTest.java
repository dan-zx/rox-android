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

import javax.inject.Inject;

import roboguice.RoboGuice;

import static org.assertj.core.api.Assertions.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RecommendationsApiTest {

    @Inject private RecommendationsApi recommendationsApi;

    @Before
    public void setUp() throws Exception {
        Robolectric.getFakeHttpLayer().interceptHttpRequests(false);
        Injector injector = RoboGuice.overrideApplicationInjector(Robolectric.application, new ConfigModule(Robolectric.application));
        injector.injectMembers(this);
        assertThat(recommendationsApi).isNotNull();
    }

    @Test
    public void testAwaitRecommendationsByLikes() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);
        assertThat(recommendationsApi.awaitRecommendationsByLikes("fakeToken", location, 3000, RecommendationsApi.Transportation.WALKING)).isNotNull();
    }

    @Test
    public void testAwaitRecommendationsByFriendsLikes() throws Exception {
        Location location = new Location();
        location.setLatitude(19.053528);
        location.setLongitude(-98.283187);
        assertThat(recommendationsApi.awaitRecommendationsByFriendsLikes("fakeToken", location, 3000, RecommendationsApi.Transportation.WALKING)).isNotNull();
    }
}