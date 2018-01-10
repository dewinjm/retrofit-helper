# retrofit-helper
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![jitpack](https://jitpack.io/v/dewinjm/retrofit-helper.svg)](https://jitpack.io/v/dewinjm/retrofit-helper.svg)
[![Build Status](https://travis-ci.org/dewinjm/retrofit-helper.svg?branch=master)](https://travis-ci.org/dewinjm/retrofit-helper)

## Helper for [Retrofit](https://github.com/square/retrofit) HTTP client.

### How to include
Add the repository to your project **build.gradle**:
``` gradle
repositories {
	jcenter()
	maven {
	   url "https://jitpack.io"
	}
}
```
And add the library to your module **build.gradle**:
``` gradle
dependencies {
  compile 'com.github.dewinjm:retrofit-helper:1.0.1'
}
```

### Example
Create a service interface:
```java
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
