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

import com.github.dewinjm.retrofithelper.utils.UnzippingInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Extended Retrofit Adapter a Java interface to HTTP calls by using annotations on the declared
 * methods to define how requests are made.
 */
public class ServiceFactory implements Generator {
    public static final int FACTORY_JSON = 1;
    public static final int FACTORY_XML = 2;

    private OkHttpClient interceptor;
    private String baseUrl;
    private boolean gzipEnableInterceptor = false;

    private static final int FACTORY_TYPE_DEFAULT = FACTORY_JSON;
    private static Converter.Factory factoryConvert;

    private static ServiceFactory ServiceInit(String baseUrl, boolean gzipEnable) {
        ServiceFactory serviceFactory = new ServiceFactory();
        serviceFactory.baseUrl = baseUrl;
        serviceFactory.gzipEnableInterceptor = gzipEnable;
        return serviceFactory;
    }

    private static ServiceFactory ServiceInit(String baseUrl) {
        return ServiceInit(baseUrl, false);
    }

    private static void setFactoryConvert(int factory) {
        switch (factory) {
            case FACTORY_JSON:
                factoryConvert = GsonConverterFactory.create();
                break;
            case FACTORY_XML:
                factoryConvert = SimpleXmlConverterFactory.create();
                break;
            default:
                factoryConvert = GsonConverterFactory.create();
                break;
        }
    }

    /**
     * Create ServiceFactory instance
     */
    public static ServiceFactory builder(@NonNull String baseUrl) {
        setFactoryConvert(FACTORY_TYPE_DEFAULT);
        return ServiceInit(baseUrl);
    }

    public static ServiceFactory builder(ProviderBase providerBase) {
        setFactoryConvert(FACTORY_TYPE_DEFAULT);
        return ServiceInit(providerBase.getDefaultBaseUrl());
    }

    public static ServiceFactory builder(ProviderBase providerBase, int factoryType) {
        setFactoryConvert(factoryType);
        return ServiceInit(providerBase.getDefaultBaseUrl());
    }

    public static ServiceFactory builder(ProviderBase providerBase, int factoryType, boolean gzipEnable) {
        setFactoryConvert(factoryType);
        return ServiceInit(providerBase.getDefaultBaseUrl(), gzipEnable);
    }

    /**
     * Set the OkHttpClient inteceptor, for Header, Auth, etc.
     */
    public ServiceFactory setInterceptor(@NonNull OkHttpClient interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    @Override
    public <T> T createService(@NonNull Class<T> serviceClass) {
        return callService().create(serviceClass);
    }

    @Override
    public Retrofit createRetrofitService() {
        return callService();
    }

    @NonNull
    private Retrofit callService() {
        urlFormat();
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(factoryConvert)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        if (interceptor != null)
            return retrofitBuilder.client(interceptor).build();
        else {
            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

            if (gzipEnableInterceptor)
                okHttpBuilder.addInterceptor(new UnzippingInterceptor());

            return retrofitBuilder.client(okHttpBuilder.build()).build();
        }
    }

    private void urlFormat() {
        if (!baseUrl.contains("http"))
            baseUrl = "http://" + baseUrl;

        if (!baseUrl.substring(baseUrl.length() - 1, baseUrl.length()).equals("/"))
            baseUrl += "/";
    }
}
