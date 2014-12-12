package com.grayfox.android.client;

import com.google.inject.Injector;

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
public class AppUsersApiTest {

    @Inject private AppUsersApi appUsersApi;

    @Before
    public void setUp() throws Exception {
        Robolectric.getFakeHttpLayer().interceptHttpRequests(false);
        Injector injector = RoboGuice.overrideApplicationInjector(Robolectric.application, new ConfigModule(Robolectric.application));
        injector.injectMembers(this);
        assertThat(appUsersApi).isNotNull();
    }

    @Test
    public void testRegister() throws Exception {
        assertThat(appUsersApi.awaitAccessToken("fakeCode")).isNotNull().isNotEmpty();
    }
}