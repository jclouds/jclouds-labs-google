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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNull;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.parse.ParseImageListTest;
import org.jclouds.googlecomputeengine.parse.ParseImageTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class ImageApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {

   public static final HttpRequest LIST_PROJECT_IMAGES_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint("https://www.googleapis" +
                   ".com/compute/v1/projects/myproject/global/images")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_PROJECT_IMAGES_RESPONSE = HttpResponse.builder().statusCode(200)
           .payload(staticPayloadFromResource("/image_list.json")).build();

   public static final HttpRequest LIST_CENTOS_IMAGES_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint("https://www.googleapis.com/compute/v1/projects/centos-cloud/global/images")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_CENTOS_IMAGES_RESPONSE = HttpResponse.builder().statusCode(200)
         .payload(staticPayloadFromResource("/image_list_centos.json")).build();

   public static final HttpRequest LIST_DEBIAN_IMAGES_REQUEST = HttpRequest
           .builder()
           .method("GET")
           .endpoint("https://www.googleapis.com/compute/v1/projects/debian-cloud/global/images")
           .addHeader("Accept", "application/json")
           .addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpResponse LIST_DEBIAN_IMAGES_RESPONSE =
      HttpResponse.builder().statusCode(200)
            .payload(staticPayloadFromResource("/image_list_debian.json")).build();

   public void testGetImageResponseIs2xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/centos-cloud/global/images/centos-6-2-v20120326")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/image_get.json")).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getImageApiForProject("centos-cloud");

      assertEquals(imageApi.get("centos-6-2-v20120326"),
              new ParseImageTest().expected());
   }

   public void testGetImageResponseIs4xx() throws Exception {
      HttpRequest get = HttpRequest
              .builder()
              .method("GET")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/centos-cloud/global/images/centos-6-2-v20120326")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, get, operationResponse).getImageApiForProject("centos-cloud");

      assertNull(imageApi.get("centos-6-2-v20120326"));
   }

   public void testDeleteImageResponseIs2xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/global/images/centos-6-2-v20120326")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(200)
              .payload(payloadFromResource("/operation.json")).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getImageApiForProject("myproject");

      assertEquals(imageApi.delete("centos-6-2-v20120326"),
              new ParseOperationTest().expected());
   }

   public void testDeleteImageResponseIs4xx() {
      HttpRequest delete = HttpRequest
              .builder()
              .method("DELETE")
              .endpoint("https://www.googleapis" +
                      ".com/compute/v1/projects/myproject/global/images/centos-6-2-v20120326")
              .addHeader("Accept", "application/json")
              .addHeader("Authorization", "Bearer " + TOKEN).build();

      HttpResponse deleteResponse = HttpResponse.builder().statusCode(404).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, delete, deleteResponse).getImageApiForProject("myproject");

      assertNull(imageApi.delete("centos-6-2-v20120326"));
   }

   public void testListImagesResponseIs2xx() {

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_PROJECT_IMAGES_REQUEST, LIST_PROJECT_IMAGES_RESPONSE).getImageApiForProject
              ("myproject");

      assertEquals(imageApi.list().first().get().toString(),
              new ParseImageListTest().expected().toString());
   }

   public void testListImagesResponseIs4xx() {

      HttpResponse operationResponse = HttpResponse.builder().statusCode(404).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, LIST_PROJECT_IMAGES_REQUEST, operationResponse).getImageApiForProject("myproject");

      assertTrue(imageApi.list().concat().isEmpty());
   }

   public void testCreateImageFromPdResponseIs2xx(){
      HttpRequest createImage = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://www.googleapis" +
                    ".com/compute/v1/projects/myproject/global/images")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResource("/image_insert_from_pd.json"))
            .build();

      HttpResponse createImageResponse = HttpResponse.builder().statusCode(200)
                                  .payload(payloadFromResource("/operation.json")).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
            TOKEN_RESPONSE, createImage, createImageResponse).getImageApiForProject("myproject");

      assertEquals(imageApi.createImageFromPD("my-image", "https://www.googleapis.com/" +
            "compute/v1/projects/myproject/zones/us-central1-a/disks/mydisk"),
            new ParseOperationTest().expected());
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testCreateImageFromPdResponseIs4xx() {
      HttpRequest createImage = HttpRequest
            .builder()
            .method("POST")
            .endpoint("https://www.googleapis" +
                     ".com/compute/v1/projects/myproject/global/images")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN)
            .payload(payloadFromResource("/image_insert_from_pd.json"))
            .build();

      HttpResponse createImageResponse = HttpResponse.builder().statusCode(404).build();

      ImageApi imageApi = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, createImage, createImageResponse).getImageApiForProject("myproject");

      imageApi.createImageFromPD("my-image", "https://www.googleapis.com/" +
                  "compute/v1/projects/myproject/zones/us-central1-a/disks/mydisk");
   }
}
