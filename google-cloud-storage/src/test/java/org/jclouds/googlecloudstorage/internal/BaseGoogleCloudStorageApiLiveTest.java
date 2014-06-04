/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.googlecloudstorage.internal;

import java.util.Properties;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.googlecloudstorage.GoogleCloudStorageApi;

import com.google.inject.Injector;

import com.google.inject.Module;

/**
 * @TODO BUCKET_NAME should be replaced by the name of the bucket created in "Bucket insert" operation when it is implemented
 * @author Bhathiya Supun
 */
public class BaseGoogleCloudStorageApiLiveTest extends BaseApiLiveTest<GoogleCloudStorageApi> {

   protected static final String API_URL_PREFIX = "https://www.googleapis.com/storage/v1/b/";
   
   protected static final String BUCKET_NAME = System.getProperty("test.google-cloud-storage.bucket");
   protected static final String BUCKETACL_API_URL_SUFFIX = BUCKET_NAME + "/acl/";

   protected static final String PROJECT_NAME = "JcloudTest";

   public BaseGoogleCloudStorageApiLiveTest() {
      provider = "google-cloud-storage";
   }

   protected GoogleCloudStorageApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      return injector.getInstance(GoogleCloudStorageApi.class);
   }

}
