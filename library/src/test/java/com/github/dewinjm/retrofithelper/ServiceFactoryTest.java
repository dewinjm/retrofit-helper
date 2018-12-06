/*
 * Copyright (c) 2017 Dewin J. Martínez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dewinjm.retrofithelper;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static okhttp3.mockwebserver.SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class ServiceFactoryTest {
    private static final String FAKE_NAME = "PETER";
    private static final String FAKE_ID = "123";

    @Rule
    public final MockWebServer server = new MockWebServer();
    private String urlBase;

    class MockClass {
        private String name;
        private String id;

        public String getId() {
            return id;
        }

        String getName() {
            return name;
        }

        MockClass(String name, String id) {
            this.name = name;
            this.id = id;
        }
    }

    interface MockService {
        @Headers("Content-Type:application/json")
        @GET("/")
        Call<MockClass> getBody();
    }

    public MockResponse mockResponse(int statusCode, String jsonBody) {
        return new MockResponse()
                .setResponseCode(statusCode)
                .addHeader("Content-Type", "application/json")
                .setBody(jsonBody);
    }

    private void createMockResponse() {
        MockClass entity = new MockClass(FAKE_NAME, FAKE_ID);
        String json = new Gson().toJson(entity);
        server.enqueue(mockResponse(200, json));
    }

    @Before
    public void setup() {
        urlBase = server.url("/").toString();
    }

    @Test
    public void httpGetAsync() throws InterruptedException {
        createMockResponse();
        final AtomicReference<Response<MockClass>> responseRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        ServiceFactory.builder(urlBase)
                .createService(MockService.class)
                .getBody()
                .enqueue(new Callback<MockClass>() {
                    @Override
                    public void onResponse(@NonNull Call<MockClass> call, @NonNull Response<MockClass> response) {
                        responseRef.set(response);
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(@NonNull Call<MockClass> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        t.getMessage();
                    }
                });

        assertTrue(latch.await(10, SECONDS));
        checkAssert(responseRef.get());
    }

    @Test
    public void httpGetSync() throws IOException {
        createMockResponse();
        Response<MockClass> response = ServiceFactory.builder(urlBase)
                .createService(MockService.class)
                .getBody()
                .execute();

        checkAssert(response);
    }

    @Test
    public void httpGetAsyncIntercepto() throws InterruptedException {
        createMockResponse();
        final AtomicReference<Response<MockClass>> responseRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        ServiceFactory.builder(urlBase)
                .setInterceptor(new OkHttpClient())
                .createService(MockService.class)
                .getBody()
                .enqueue(new Callback<MockClass>() {
                    @Override
                    public void onResponse(@NonNull Call<MockClass> call, @NonNull Response<MockClass> response) {
                        responseRef.set(response);
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(@NonNull Call<MockClass> call, @NonNull Throwable t) {
                        t.printStackTrace();
                        t.getMessage();
                    }
                });

        assertTrue(latch.await(10, SECONDS));
        checkAssert(responseRef.get());
    }

    @Test
    public void transportProblemSync() {
        MockService example = ServiceFactory.builder(urlBase)
                .createRetrofitService()
                .create(MockService.class);

        server.enqueue(new MockResponse()
                .setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        Call<MockClass> call = example.getBody();

        try {
            call.execute();
            fail();
        } catch (IOException ignored) {
            ignored.getMessage();
        }
    }

    @Test
    public void responseBodyBuffers() {
        Retrofit retrofit = ServiceFactory.builder(urlBase)
                .createRetrofitService();

        MockService example = retrofit.create(MockService.class);

        MockClass entity = new MockClass("Mark", "0001");
        String json = new Gson().toJson(entity);

        server.enqueue(new MockResponse()
                .setBody(json)
                .setSocketPolicy(DISCONNECT_DURING_RESPONSE_BODY));

        Call<MockClass> buffered = example.getBody();
        try {
            buffered.execute();
            fail();
        } catch (IOException e) {
            assertEquals(e.getMessage(), "unexpected end of stream");
        }
    }

    private void checkAssert(Response<MockClass> response) {
        assertTrue(response.isSuccessful());

        MockClass mockClass = response.body();
        assert mockClass != null;
        assertEquals(mockClass.getId(), FAKE_ID);
        assertEquals(mockClass.getName(), FAKE_NAME);
    }
}
