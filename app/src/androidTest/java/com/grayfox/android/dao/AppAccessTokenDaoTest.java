package com.grayfox.android.dao;

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
public class AppAccessTokenDaoTest {

    @Inject private AppAccessTokenDao appAccessTokenDao;

    @Before
    public void setUp() throws Exception {
        Injector injector = RoboGuice.overrideApplicationInjector(Robolectric.application, new ConfigModule(Robolectric.application));
        injector.injectMembers(this);
        assertThat(appAccessTokenDao).isNotNull();
    }

    @Test
    public void testTransaction() throws Exception {
        assertThat(appAccessTokenDao.fetchAccessToken()).isNull();

        String fakeToken = "fakeToken";
        appAccessTokenDao.saveOrUpdateAccessToken(fakeToken);
        assertThat(appAccessTokenDao.fetchAccessToken()).isNotNull().isNotEmpty().isEqualTo(fakeToken);

        appAccessTokenDao.deleteAccessToken();
        assertThat(appAccessTokenDao.fetchAccessToken()).isNull();
    }
}