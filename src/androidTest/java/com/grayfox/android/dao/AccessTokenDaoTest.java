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
public class AccessTokenDaoTest {

    @Inject private AccessTokenDao accessTokenDao;

    @Before
    public void setUp() throws Exception {
        Injector injector = RoboGuice.overrideApplicationInjector(Robolectric.application, new ConfigModule(Robolectric.application));
        injector.injectMembers(this);
        assertThat(accessTokenDao).isNotNull();
    }

    @Test
    public void testTransaction() throws Exception {
        assertThat(accessTokenDao.fetchAccessToken()).isNull();

        String fakeToken = "fakeToken";
        accessTokenDao.saveOrUpdateAccessToken(fakeToken);
        assertThat(accessTokenDao.fetchAccessToken()).isNotNull().isNotEmpty().isEqualTo(fakeToken);

        accessTokenDao.deleteAccessToken();
        assertThat(accessTokenDao.fetchAccessToken()).isNull();
    }
}