/*
 * Copyright 2014-2015 Daniel Pedraza-Arcega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.grayfox.android.app.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Injector;

import com.grayfox.android.app.config.ConfigModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import roboguice.RoboGuice;

import javax.inject.Inject;

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