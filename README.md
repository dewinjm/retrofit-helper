# retrofit-helper
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![jitpack](https://jitpack.io/v/dewinjm/retrofit-helper.svg)](https://jitpack.io/v/dewinjm/retrofit-helper.svg)
[![Build Status](https://travis-ci.org/dewinjm/retrofit-helper.svg?branch=master)](https://travis-ci.org/dewinjm/retrofit-helper)

## Helper for [Retrofit](https://github.com/square/retrofit) HTTP client.

### How to include
Add the repository to your project **build.gradle**:
``` groovy
repositories {
    jcenter()
    maven {
        url "https://jitpack.io"
    }
}
```
And add the library to your module **build.gradle**:
``` groovy
dependencies {
    compile 'com.github.dewinjm:retrofit-helper:1.0.3'
}
```

### Example
Create a service interface:
``` java
public interface TestService {
  @Headers("Content-Type:application/json")
  @GET("/")
  Call<YouResponseClass> getData(); //YouResponseClass is your class to parse values from response
}
```

Create an asynchronous call
``` java
 ServiceFactory.builder("http://urlbase")
    .createService(TestService.class)
    .getData()
    ..enqueue(new Callback<YouResponseClass>() {....});
```
To learn how to use retrofit, see the [doc](http://square.github.io/retrofit/)

## Create a Service Helper
To save us typing the same API URL in each Provider, we can create an API Services administrator.

For the example, Imagine that we should request an API of animal classes.

Create a interface AnimalService:
``` java
public interface AnimalService {
    @GET("api/animals")
    Call<List<Animal>> get(@Query("class") String classes);
}
```

Create ApiHelper for service administrations:
``` java
public class ApiHelper implements ProviderBase {
    private String YOUR_API_URL = "http://api.yourdomain";

    @Override
    public String getDefaultBaseUrl() {
        return YOUR_API_URL;
    }

    private ServiceFactory builder() {
        return ServiceFactory.builder(this);
    }

    public AnimalService animalService() {
        return builder().createService(AnimalService.class);
    }

    public OtherService otherService() {
        return builder().createService(OtherService.class);
    }
}
```

AnimalProvider:
``` java
public class AnimalProvider {

    public void getByClasses(String classes) {
        new ApiHelper().animalService
            .get(classes)
            .enqueue(new Callback<List<Animal>>() {
                 @Override
                 public void onResponse(@NonNull Call<List<Animal>> call,
                                        @NonNull Response<List<Animal>> response) {
                     if (response.isSuccessful())
                        response.body(); //the animal List response
                 }

                 @Override
                 public void onFailure(@NonNull Call<List<Animal>> call,
                                       @NonNull Throwable t) {
                     // failure trying to get response
                 }
             });
    }
}
```

## Example to get response body by a zip xml request:

``` java
public interface YourService {
    @Headers({
        "Content-Type: application/xml;charset=utf-8",
        "Accept: application/xml"
    })
    @GET
    Call<YourModel> get();
}
```

In your service provider, specify the factory type to FACTORY_XML and the gzipEnable parameter true:
``` java
public class YourProvider implements ProviderBase {
    @Override
    public String getDefaultBaseUrl() {
        //The zip xml URL
        return "http://your.domain/data.xml.gz";
    }

    public void yourMethod() {
        ServiceFactory
                .builder(this, ServiceFactory.FACTORY_XML, true) //just set true when need request gzip file
                .createService(YourService.class)
                .get()
                .enqueue(new Callback<YourModel>() { ... });
    }
}
```

### License
	Copyright 2018 Dewin J. Martínez
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
