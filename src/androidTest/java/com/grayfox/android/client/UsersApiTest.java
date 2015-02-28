package com.grayfox.android.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.google.inject.Injector;

import com.grayfox.android.R;
import com.grayfox.android.client.model.User;
import com.grayfox.android.config.ConfigModule;

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

import roboguice.RoboGuice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class UsersApiTest {

    private static MockWebServer mockWebServer;

    @Inject private UsersApi usersApi;

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
        Injector injector = RoboGuice.overrideApplicationInjector(Robolectric.application, new ConfigModule(Robolectric.application));
        injector.injectMembers(this);
        assertThat(usersApi).isNotNull();
        usersApi = spy(usersApi);
        String mockHost = mockWebServer.getUrl("/").toString().replaceAll("http://", "");
        doReturn(mockHost).when(usersApi).getString(R.string.gf_api_host);
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
                .setStatus("HTTP/1.1 401 Unauthorized")
                .setBody(getJsonFrom("responses/error.json")));
        usersApi.awaitAccessToken("fakeCode");
    }

    @Test
    public void testGetSelfUser() throws Exception {
        User user = new User();
        user.setName("Daniel");
        user.setLastName("Pedraza");
        user.setPhotoUrl("https://irs3.4sqi.net/img/user/300x300/88260846-Q1M41BHXTDOTJJA3.jpg");
        user.setFoursquareId("88260846");

        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 OK")
                .setBody(getJsonFrom("responses/user.json")));
        assertThat(usersApi.awaitSelfUser("fakeAccessToken")).isNotNull().isEqualTo(user);
    }

    @Test(expected = ApiException.class)
    public void testGetSelfUserError() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 401 Unauthorized")
                .setBody(getJsonFrom("responses/error.json")));
        usersApi.awaitSelfUser("fakeAccessToken");
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