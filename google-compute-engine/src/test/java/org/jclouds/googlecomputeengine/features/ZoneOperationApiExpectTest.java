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
package org.jclouds.googlecomputeengine.features;

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;
import static org.jclouds.googlecomputeengine.options.ListOptions.Builder.filter;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;

import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseZoneOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ZoneOperationApiExpectTest")
public class ZoneOperationApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   private static final String OPERATIONS_URL_PREFIX = BASE_URL + "/myproject/zones/us-central1-a/operations";

   public static final HttpRequest GET_ZONE_OPERATION_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint(OPERATIONS_URL_PREFIX + "/operation-1354084865060-4cf88735faeb8-bbbb12cb")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse GET_ZONE_OPERATION_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/zone_operation.json")).build();

   public void testGetOperationResponseIs2xx() throws Exception {

      ZoneOperationApi zoneOperationApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
            TOKEN_RESPONSE, GET_ZONE_OPERATION_REQUEST, GET_ZONE_OPERATION_RESPONSE)
            .getZoneOperationApi("myproject", "us-central1-a");

      assertEquals(zoneOperationApi.get("operation-1354084865060-4cf88735faeb8-bbbb12cb"),
            new ParseZoneOperationTest().expected());
   }

   public void testGetOperationResponseIs4xx() throws Exception {

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      ZoneOperationApi zoneOperationApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
            TOKEN_RESPONSE, GET_ZONE_OPERATION_REQUEST, operationResponse)
            .getZoneOperationApi("myproject", "us-central1-a");

      assertNull(zoneOperationApi.get("operation-1354084865060-4cf88735faeb8-bbbb12cb"));
   }

   public void testDeleteOperationResponseIs2xx() throws Exception {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(OPERATIONS_URL_PREFIX + "/operation-1352178598164-4cdcc9d031510-4aa46279")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(204).build();

      ZoneOperationApi zoneOperationApi = requestsSendResponses(requestForScopes(COMPUTE_SCOPE), TOKEN_RESPONSE, delete,
            operationResponse).getZoneOperationApi("myproject", "us-central1-a");

      zoneOperationApi.delete("operation-1352178598164-4cdcc9d031510-4aa46279");
   }

   public void testDeleteOperationResponseIs4xx() throws Exception {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint(OPERATIONS_URL_PREFIX + "/operation-1352178598164-4cdcc9d031510-4aa46279")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      ZoneOperationApi zoneOperationApi = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, operationResponse).getZoneOperationApi("myproject", "us-central1-a");

      zoneOperationApi.delete("operation-1352178598164-4cdcc9d031510-4aa46279");
   }

   private static ListPage<Operation> expectedList() {
      return ListPage.create( //
            ImmutableList.of(new ParseZoneOperationTest().expected()), // items
            null // nextPageToken
      );
   }

   public void testListOperationWithNoOptionsResponseIs2xx() {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(OPERATIONS_URL_PREFIX)
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation_list.json")).build();

      ZoneOperationApi zoneOperationApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getZoneOperationApi("myproject", "us-central1-a");

      assertEquals(zoneOperationApi.list().next().toString(),
              expectedList().toString());
   }

   public void testListOperationWithPaginationOptionsResponseIs2xx() {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(OPERATIONS_URL_PREFIX +
                      "?pageToken=CglPUEVSQVRJT04SOzU5MDQyMTQ4Nzg1Mi5vcG" +
                      "VyYXRpb24tMTM1MjI0NDI1ODAzMC00Y2RkYmU2YTJkNmIwLWVkMzIyMzQz&" +
                      "filter=" +
                      "status%20eq%20done&" +
                      "maxResults=3")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/zone_operation_list.json")).build();

      ZoneOperationApi zoneOperationApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getZoneOperationApi("myproject", "us-central1-a");

      assertEquals(zoneOperationApi.listPage(
              "CglPUEVSQVRJT04SOzU5MDQyMTQ4Nzg1Mi5vcGVyYXRpb24tMTM1Mj" +
                      "I0NDI1ODAzMC00Y2RkYmU2YTJkNmIwLWVkMzIyMzQz",
              filter("status eq done").maxResults(3)).toString(),
              expectedList().toString());
   }

   public void testListOperationWithPaginationOptionsResponseIs4xx() {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint(OPERATIONS_URL_PREFIX)
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      ZoneOperationApi zoneOperationApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
            TOKEN_RESPONSE, get, operationResponse).getZoneOperationApi("myproject", "us-central1-a");

      assertFalse(zoneOperationApi.list().hasNext());
   }
}
