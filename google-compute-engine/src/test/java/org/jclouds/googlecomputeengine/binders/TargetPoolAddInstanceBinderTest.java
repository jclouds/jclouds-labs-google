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
package org.jclouds.googlecomputeengine.binders;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Set;
import java.util.Map;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.json.internal.GsonWrapper;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;


/**
 * Tests behavior of {@code BindToJsonPayload}
 */
@Test(groups = "unit", testName = "TargetPoolAddInstanceBinderTest")
public class TargetPoolAddInstanceBinderTest extends BaseGoogleComputeEngineExpectTest<Object>{

   private static final Set<URI> FAKE_INSTANCES = ImmutableSet.of(
                                       URI.create("https://www.googleapis.com/compute/v1/" +
                                                  "projects/project/zones/us-central1-a/instances/instance-1"),
                                       URI.create("https://www.googleapis.com/compute/v1/" +
                                                  "projects/project/zones/us-central1-a/instances/instance-2"));
   
   Json json = new GsonWrapper(new Gson());
 
   @Test
   public void testMap() throws SecurityException, NoSuchMethodException {
      TargetPoolChangeInstancesBinder binder = new TargetPoolChangeInstancesBinder(json);
      HttpRequest request = HttpRequest.builder().method("GET").endpoint("http://momma").build();
      Map<String, Object> postParams = ImmutableMap.of("instances", (Object) FAKE_INSTANCES);

      binder.bindToRequest(request, postParams);

      assertEquals(request.getPayload().getRawContent(),
               "{"
            + "\"instances\":["
            + "{\"instance\":\"https://www.googleapis.com/compute/v1/projects/project/zones/us-central1-a/instances/instance-2\"},"
            + "{\"instance\":\"https://www.googleapis.com/compute/v1/projects/project/zones/us-central1-a/instances/instance-1\"}"
            + "]"
            + "}");
      assertEquals(request.getPayload().getContentMetadata().getContentType(), "application/json");

   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() {
      DiskCreationBinder binder = new DiskCreationBinder(json);
      binder.bindToRequest(HttpRequest.builder().method("GET").endpoint("http://momma").build(), null);
   }

}
