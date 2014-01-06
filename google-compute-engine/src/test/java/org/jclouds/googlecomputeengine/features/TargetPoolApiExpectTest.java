/*
 * Licensed to jclouds, Inc. (jclouds) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership.
 * jclouds licenses this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may obtain a copy of the Licens at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.jclouds.googlecomputeengine.features;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseRegionOperationTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetPoolListTest;
import org.jclouds.googlecomputeengine.parse.ParseTargetPoolTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNull;

/**
 * @author Andrea Turli
 */
@Test(groups = "unit")
public class TargetPoolApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   public void testGetTargetPoolResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/test")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/targetpool_get.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getTargetPoolApiForProject("myproject");

      assertEquals(api.getInRegion("us-central1", "test"),
              new ParseTargetPoolTest().expected());
   }

   public void testGetTargetPoolResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/test")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getTargetPoolApiForProject("myproject");

      assertNull(api.getInRegion("us-central1", "test"));
   }

   public void testInsertTargetPoolResponseIs2xx() {
      HttpRequest insert = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/targetpool_insert.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse insertTargetPoolResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, insert,
              insertTargetPoolResponse).getTargetPoolApiForProject("myproject");
      assertEquals(api.createInRegion("us-central1", "test"), new ParseRegionOperationTest().expected());
   }

   public void testDeleteTargetPoolResponseIs2xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/test-targetPool")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getTargetPoolApiForProject("myproject");

      assertEquals(api.deleteInRegion("us-central1", "test-targetPool"),
              new ParseRegionOperationTest().expected());
   }

   public void testDeleteTargetPoolResponseIs4xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/test-targetPool")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getTargetPoolApiForProject("myproject");

      assertNull(api.deleteInRegion("us-central1", "test-targetPool"));
   }

   public void testListTargetPoolsResponseIs2xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/targetpool_list.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getTargetPoolApiForProject("myproject");

      assertEquals(api.listFirstPageInRegion("us-central1").toString(),
              new ParseTargetPoolListTest().expected().toString());
   }

   public void testListTargetPoolsResponseIs4xx() {
      HttpRequest list = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, list, operationResponse).getTargetPoolApiForProject("myproject");

      assertTrue(api.listInRegion("us-central1").concat().isEmpty());
   }

   public void testAddInstanceResponseIs2xx() throws Exception {
      HttpRequest addInstance = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/test/addInstance")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/targetpool_addinstance.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/region_operation.json")).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, addInstance, operationResponse).getTargetPoolApiForProject("myproject");

      assertEquals(api.addInstance("us-central1", "test",
              "https://www.googleapis.com/compute/v1/projects/myproject/zones/europe-west1-a/instances/test"),
              new ParseRegionOperationTest().expected());
   }

   public void testAddInstanceResponseIs4xx() throws Exception {
      HttpRequest addInstance = HttpRequest
              .builder()
              .method("POST")
              .endpoint("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/test/addInstance")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN)
              .payload(payloadFromResourceWithContentType("/targetpool_addinstance.json", MediaType.APPLICATION_JSON))
              .build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      TargetPoolApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, addInstance, operationResponse).getTargetPoolApiForProject("myproject");

      assertNull(api.addInstance("us-central1", "test", "https://www.googleapis" +
              ".com/compute/v1/projects/myproject/zones/europe-west1-a/instances/test"));
   }
}
