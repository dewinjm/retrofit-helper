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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;

public interface Generator {
    /**
     * save an implementation of the ServiceFactory endpoints defined by the {@code service} interface.
     * <p>
     * By default, methods return a {@link Call} which represents the HTTP request. The generic
     * parameter of the call is the response body type and will be converted by one of the
     * {@link Converter.Factory} instances. {@link ResponseBody} can also be used for a raw
     * representation. {@link Void} can be used if you do not care about the body contents.
     * <p>
     */
    <T> T createService(@NonNull final Class<T> serviceClass);

    /**
     * save the {@link Retrofit} instance using the configured values.
     */
    Retrofit createRetrofitService();
}
