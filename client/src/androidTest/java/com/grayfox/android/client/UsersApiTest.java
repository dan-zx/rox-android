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
package com.grayfox.android.client;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.grayfox.android.client.model.Category;
import com.grayfox.android.client.model.UpdateResponse;
import com.grayfox.android.client.model.User;

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
public class UsersApiTest {

    private static MockWebServer mockWebServer;

    private UsersApi usersApi;

    @BeforeClass
    public static void setUpClass() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        mockWebServer.shutdown();
    }

    @Before
    public void setUp() throws Exception {
        usersApi = spy(new UsersApi(Robolectric.application));
        doReturn(mockWebServer.getUrl("/").toString()).when(usersApi).getString(R.string.gf_api_base_url);
    }

    @Test
    public void testRegister() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setBody(getJsonFrom("responses/access-token.json")));
        assertThat(usersApi.awaitAccessToken("fakeCode")).isNotNull().isNotEmpty().isEqualTo("fakeAccessToken");
    }

    @Test(expected = ApiException.class)
    public void testRegisterError() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/error.json")));
        usersApi.awaitAccessToken("fakeCode");
    }

    @Test
    public void testGetSelfUser() throws Exception {
        User user = new User();
        user.setName("John");
        user.setLastName("Doe");
        user.setPhotoUrl("https://irs0.4sqi.net/img/user/923847.jpg");
        user.setFoursquareId("923847");

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setBody(getJsonFrom("responses/user.json")));
        assertThat(usersApi.awaitSelfUser("fakeAccessToken")).isNotNull().isEqualTo(user);
    }

    @Test(expected = ApiException.class)
    public void testGetSelfUserError() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/error.json")));
        usersApi.awaitSelfUser("fakeAccessToken");
    }

    @Test
    public void testGetSelfUserFriends() throws Exception {
        User friend1 = new User();
        friend1.setName("Jane");
        friend1.setLastName("Doe");
        friend1.setPhotoUrl("https://irs0.4sqi.net/img/user/234237.jpg");
        friend1.setFoursquareId("234237");

        User friend2 = new User();
        friend2.setName("John");
        friend2.setLastName("Doe");
        friend2.setPhotoUrl("https://irs0.4sqi.net/img/user/543863.jpg");
        friend2.setFoursquareId("543863");

        User[] friends = {friend1, friend2};

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setBody(getJsonFrom("responses/users.json")));

        assertThat(usersApi.awaitSelfUserFriends("fakeAccessToken")).isNotNull().isNotEmpty().hasSize(friends.length).isEqualTo(friends);
    }

    @Test(expected = ApiException.class)
    public void testGetSelfUserFriendsError() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/error.json")));
        usersApi.awaitSelfUserFriends("fakeAccessToken");
    }

    @Test
    public void testGetUserLikes() throws Exception {
        Category like1 = new Category();
        like1.setName("Argentinian Restaurant");
        like1.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/default_88.png");
        like1.setFoursquareId("4bf58dd8d48988d107941735");

        Category like2 = new Category();
        like2.setName("Mexican Restaurant");
        like2.setIconUrl("https://ss3.4sqi.net/img/categories_v2/food/default_88.png");
        like2.setFoursquareId("4bf58dd8d48988d1c1941735");

        Category[] likes = {like1, like2};

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setBody(getJsonFrom("responses/categories.json")));

        assertThat(usersApi.awaitUserLikes("fakeAccessToken", "fakeFoursquareId")).isNotNull().isNotEmpty().hasSize(likes.length).isEqualTo(likes);
    }

    @Test(expected = ApiException.class)
    public void testGetUserLikesError() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/error.json")));
        usersApi.awaitUserLikes("fakeAccessToken", "fakeFoursquareId");
    }

    @Test
    public void testAddLike() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setBody(getJsonFrom("responses/update_ok.json")));

        assertThat(usersApi.awaitAddLike("fakeAccessToken", "fakeFoursquareId")).isNotNull().isEqualTo(updateOk());
    }

    @Test(expected = ApiException.class)
    public void testAddLikeError() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/error.json")));

        usersApi.awaitAddLike("fakeAccessToken", "fakeFoursquareId");
    }

    @Test
    public void testRemoveLike() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setBody(getJsonFrom("responses/update_ok.json")));

        assertThat(usersApi.awaitRemoveLike("fakeAccessToken", "fakeFoursquareId")).isNotNull().isEqualTo(updateOk());
    }

    @Test(expected = ApiException.class)
    public void testRemoveLikeError() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 500 Internal Server Error")
                .setBody(getJsonFrom("responses/error.json")));

        usersApi.awaitRemoveLike("fakeAccessToken", "fakeFoursquareId");
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

    private UpdateResponse updateOk() {
        UpdateResponse updateResponse = new UpdateResponse();
        updateResponse.setSuccess(true);
        return updateResponse;
    }
}