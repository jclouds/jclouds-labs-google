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

package org.jclouds.googlecloudstorage.features;

import static org.jclouds.googlecloudstorage.reference.GoogleCloudStorageConstants.STORAGE_FULLCONTROL_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.ObjectRole;
import org.jclouds.googlecloudstorage.domain.templates.ObjectAccessControlsTemplate;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageApiExpectTest;
import org.jclouds.googlecloudstorage.parse.ObjectaclGetTest;
import org.jclouds.googlecloudstorage.parse.ObjectaclInsertTest;
import org.jclouds.googlecloudstorage.parse.ObjectaclListTest;
import org.jclouds.googlecloudstorage.parse.ObjectaclUpdateTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class ObjectAccessControlsApiExpectTest extends BaseGoogleCloudStorageApiExpectTest {

   private static final String EXPECTED_TEST_BUCKET = "jcloudtestbucket";
   private static final String EXPECTED_TEST_OBJECT = "foo.txt";
   private static final String EXPECTED_TEST_GROUP_ENTITY = "group-00b4903a971ec6cff233284d6d24f5bf5cba904c4ade4d43ebd6a5d33800e68b";
   private static final String EXPECTED_TEST_USER_ENTITY = "user-00b4903a97adfde729f0650133a7379693099d8d85d6b1b18255ca70bf89e31d";

   public static final HttpRequest GET_OBJECTACL_REQUEST = HttpRequest
            .builder()
            .method("GET")
            .endpoint(
                     "https://www.googleapis.com/storage/v1/b/jcloudtestbucket/o/foo.txt/acl/group-00b4903a971ec6cff233284d6d24f5bf5cba904c4ade4d43ebd6a5d33800e68b")
            .addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + TOKEN).build();

   public static final HttpRequest GET_OBJECTACL_REQUEST_WITHOPTIONS = HttpRequest
            .builder()
            .method("GET")
            .endpoint(
                     "https://www.googleapis.com/storage/v1/b/jcloudtestbucket/o/foo.txt/acl/group-00b4903a971ec6cff233284d6d24f5bf5cba904c4ade4d43ebd6a5d33800e68b")
            .addQueryParam("generation", "100").addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN).build();

   public final HttpResponse GET_OBJECTACL_RESPONSE = HttpResponse.builder().statusCode(200)
            .payload(staticPayloadFromResource("/objectacl_get.json")).build();

   public final HttpResponse CREATE_OBJECTACL_RESPONSE = HttpResponse.builder().statusCode(200)
            .payload(staticPayloadFromResource("/objectacl_insert_response.json")).build();

   public final HttpRequest LIST_OBJECTACL_REQUEST = HttpRequest.builder().method("GET")
            .endpoint("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/o/foo.txt/acl")
            .addHeader("Accept", "application/json").addHeader("Authorization", "Bearer " + TOKEN).build();

   public final HttpRequest LIST_OBJECTACL_REQUEST_WITHOPTIONS = HttpRequest.builder().method("GET")
            .endpoint("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/o/foo.txt/acl")
            .addQueryParam("generation", "100").addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer " + TOKEN).build();

   public final HttpResponse LIST_OBJECTACL_RESPONSE = HttpResponse.builder().statusCode(200)
            .payload(staticPayloadFromResource("/objectacl_list.json")).build();

   // Test getObjectAccessControls
   public void testGetObjectaclWithNoOptionsResponseIs2xx() throws Exception {

      ObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               GET_OBJECTACL_REQUEST, GET_OBJECTACL_RESPONSE).getObjectAccessControlsApi();

      assertEquals(api.getObjectAccessControls(EXPECTED_TEST_BUCKET, EXPECTED_TEST_OBJECT, EXPECTED_TEST_GROUP_ENTITY),
               new ObjectaclGetTest().expected());
   }

   public void testGetObjectaclWithOptionsResponseIs2xx() throws Exception {

      ObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               GET_OBJECTACL_REQUEST_WITHOPTIONS, GET_OBJECTACL_RESPONSE).getObjectAccessControlsApi();

      assertEquals(
               api.getObjectAccessControls(EXPECTED_TEST_BUCKET, EXPECTED_TEST_OBJECT, EXPECTED_TEST_GROUP_ENTITY,
                        Long.valueOf(100)), new ObjectaclGetTest().expected());
   }

   public void testGetObjectaclResponseIs4xx() throws Exception {

      HttpResponse getResponse = HttpResponse.builder().statusCode(404).build();

      ObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               GET_OBJECTACL_REQUEST, getResponse).getObjectAccessControlsApi();

      assertNull(api.getObjectAccessControls(EXPECTED_TEST_BUCKET, EXPECTED_TEST_OBJECT, EXPECTED_TEST_GROUP_ENTITY));
   }

   // Test listObjectAccessControls
   public void testListObjectaclWithNoOptionsResponseIs2xx() throws Exception {

      ObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               LIST_OBJECTACL_REQUEST, LIST_OBJECTACL_RESPONSE).getObjectAccessControlsApi();

      assertEquals(api.listObjectAccessControls(EXPECTED_TEST_BUCKET, EXPECTED_TEST_OBJECT),
               new ObjectaclListTest().expected());

   }

   // Test listObjectAccessControls
   public void testListObjectaclWithOptionsResponseIs2xx() throws Exception {

      ObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               LIST_OBJECTACL_REQUEST_WITHOPTIONS, LIST_OBJECTACL_RESPONSE).getObjectAccessControlsApi();

      assertEquals(api.listObjectAccessControls(EXPECTED_TEST_BUCKET, EXPECTED_TEST_OBJECT, Long.valueOf(100)),
               new ObjectaclListTest().expected());

   }

   public void testListObjectaclResponseIs4xx() throws Exception {

      HttpResponse listResponse = HttpResponse.builder().statusCode(404).build();

      ObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               LIST_OBJECTACL_REQUEST, listResponse).getObjectAccessControlsApi();

      assertNull(api.listObjectAccessControls(EXPECTED_TEST_BUCKET, EXPECTED_TEST_OBJECT));
   }

   // Test insertObjectAccessControls
   public void testInsertObjectaclWithNoOptionsResponseIs2xx() throws Exception {
      HttpRequest insertRequest = HttpRequest
               .builder()
               .method("POST")
               .endpoint("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/o/foo.txt/acl")
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Bearer " + TOKEN)
               .payload(payloadFromResourceWithContentType("/objectacl_insert_requestpayload.json",
                        MediaType.APPLICATION_JSON)).build();

      ObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               insertRequest, CREATE_OBJECTACL_RESPONSE).getObjectAccessControlsApi();

      ObjectAccessControlsTemplate template = new ObjectAccessControlsTemplate().entity(EXPECTED_TEST_USER_ENTITY)
               .role(ObjectRole.OWNER);

      assertEquals(api.createObjectAccessControls(EXPECTED_TEST_BUCKET, EXPECTED_TEST_OBJECT, template),
               new ObjectaclInsertTest().expected());

   }

   public void testInsertObjectaclWithOptionsResponseIs2xx() throws Exception {
      HttpRequest insertRequest = HttpRequest
               .builder()
               .method("POST")
               .endpoint("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/o/foo.txt/acl")
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Bearer " + TOKEN)
               .addQueryParam("generation", "100")
               .payload(payloadFromResourceWithContentType("/objectacl_insert_requestpayload.json",
                        MediaType.APPLICATION_JSON)).build();

      ObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               insertRequest, CREATE_OBJECTACL_RESPONSE).getObjectAccessControlsApi();

      ObjectAccessControlsTemplate template = new ObjectAccessControlsTemplate().entity(
               "user-00b4903a97adfde729f0650133a7379693099d8d85d6b1b18255ca70bf89e31d").role(ObjectRole.OWNER);

      assertEquals(
               api.createObjectAccessControls(EXPECTED_TEST_BUCKET, EXPECTED_TEST_OBJECT, template, Long.valueOf(100)),
               new ObjectaclInsertTest().expected());

   }

   // Test updateObjectAccessControls
   public void testUpdateObjectaclWithNoOptionsResponseIs2xx() throws Exception {
      HttpRequest update = HttpRequest
               .builder()
               .method("PUT")
               .endpoint("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/o/foo.txt/acl/allUsers")
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Bearer " + TOKEN)
               .payload(payloadFromResourceWithContentType("/objectacl_request_payload.json",
                        MediaType.APPLICATION_JSON)).build();

      HttpResponse updateResponse = HttpResponse.builder().statusCode(200)
               .payload(staticPayloadFromResource("/objectacl_update_initial.json")).build();

      ObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               update, updateResponse).getObjectAccessControlsApi();

      ObjectAccessControlsTemplate template = new ObjectAccessControlsTemplate().entity("allUsers").role(
               ObjectRole.OWNER);

      assertEquals(api.updateObjectAccessControls(EXPECTED_TEST_BUCKET, EXPECTED_TEST_OBJECT, "allUsers", template),
               new ObjectaclUpdateTest().expected());
   }

   public void testUpdateObjectaclWithOptionsResponseIs2xx() throws Exception {
      HttpRequest update = HttpRequest
               .builder()
               .method("PUT")
               .endpoint("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/o/foo.txt/acl/allUsers")
               .addQueryParam("generation", "100")
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Bearer " + TOKEN)
               .payload(payloadFromResourceWithContentType("/objectacl_request_payload.json",
                        MediaType.APPLICATION_JSON)).build();

      HttpResponse updateResponse = HttpResponse.builder().statusCode(200)
               .payload(staticPayloadFromResource("/objectacl_update_initial.json")).build();

      ObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               update, updateResponse).getObjectAccessControlsApi();

      ObjectAccessControlsTemplate template = new ObjectAccessControlsTemplate().entity("allUsers").role(
               ObjectRole.OWNER);

      assertEquals(
               api.updateObjectAccessControls(EXPECTED_TEST_BUCKET, EXPECTED_TEST_OBJECT, "allUsers", template,
                        Long.valueOf(100)), new ObjectaclUpdateTest().expected());
   }

   // Test updateObjectAccessControls
   public void testPatchObjectaclWithNoOptionsResponseIs2xx() throws Exception {
      HttpRequest patchRequest = HttpRequest
               .builder()
               .method("PATCH")
               .endpoint("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/o/foo.txt/acl/allUsers")
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Bearer " + TOKEN)
               .payload(payloadFromResourceWithContentType("/objectacl_request_payload.json",
                        MediaType.APPLICATION_JSON)).build();

      HttpResponse patchResponse = HttpResponse.builder().statusCode(200)
               .payload(staticPayloadFromResource("/objectacl_update_initial.json")).build();

      ObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               patchRequest, patchResponse).getObjectAccessControlsApi();

      ObjectAccessControlsTemplate template = new ObjectAccessControlsTemplate().entity("allUsers").role(
               ObjectRole.OWNER);

      assertEquals(api.patchObjectAccessControls(EXPECTED_TEST_BUCKET, EXPECTED_TEST_OBJECT, "allUsers", template),
               new ObjectaclUpdateTest().expected());
   }

   public void testPatchObjectaclWithOptionsResponseIs2xx() throws Exception {
      HttpRequest patchRequest = HttpRequest
               .builder()
               .method("PATCH")
               .endpoint("https://www.googleapis.com/storage/v1/b/jcloudtestbucket/o/foo.txt/acl/allUsers")
               .addQueryParam("generation", "100")
               .addHeader("Accept", "application/json")
               .addHeader("Authorization", "Bearer " + TOKEN)
               .payload(payloadFromResourceWithContentType("/objectacl_request_payload.json",
                        MediaType.APPLICATION_JSON)).build();

      HttpResponse patchResponse = HttpResponse.builder().statusCode(200)
               .payload(staticPayloadFromResource("/objectacl_update_initial.json")).build();

      ObjectAccessControlsApi api = requestsSendResponses(requestForScopes(STORAGE_FULLCONTROL_SCOPE), TOKEN_RESPONSE,
               patchRequest, patchResponse).getObjectAccessControlsApi();

      ObjectAccessControlsTemplate template = new ObjectAccessControlsTemplate().entity("allUsers").role(
               ObjectRole.OWNER);
      assertEquals(
               api.patchObjectAccessControls(EXPECTED_TEST_BUCKET, EXPECTED_TEST_OBJECT, "allUsers", template,
                        Long.valueOf(100)), new ObjectaclUpdateTest().expected());
   }

}
