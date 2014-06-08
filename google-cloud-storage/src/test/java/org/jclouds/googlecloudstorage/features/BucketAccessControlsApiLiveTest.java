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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.jclouds.googlecloudstorage.domain.BucketAccessControls;
import org.jclouds.googlecloudstorage.domain.BucketAccessControls.Role;
import org.jclouds.googlecloudstorage.domain.ListBucketAccessControls;
import org.jclouds.googlecloudstorage.domain.Resource.Kind;
import org.jclouds.googlecloudstorage.internal.BaseGoogleCloudStorageApiLiveTest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * @author Bhathiya Supun
 */
public class BucketAccessControlsApiLiveTest extends BaseGoogleCloudStorageApiLiveTest {

   private BucketAccessControlsApi api() {
      return api.getBucketAccessControlsApi();
   }

   @Test(groups = "live")
   public void testCreateBucketacl() {
      BucketAccessControls bucketacl = BucketAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(Role.READER).build();
      BucketAccessControls response = api().createBucketAccessControls(BUCKET_NAME, bucketacl);

      assertNotNull(response);
      assertEquals(response.getId(), BUCKET_NAME + "/allUsers");
   }

   @Test(groups = "live", dependsOnMethods = "testCreateBucketacl")
   public void testUpdateBucketacl() {
      BucketAccessControls bucketacl = BucketAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(Role.WRITER).build();
      BucketAccessControls response = api().updateBucketAccessControls(BUCKET_NAME, "allUsers", bucketacl);

      assertNotNull(response);
      assertEquals(response.getId(), BUCKET_NAME + "/allUsers");
      assertEquals(response.getRole(), Role.WRITER);
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateBucketacl")
   public void testGetBucketacl() {
      BucketAccessControls response = api().getBucketAccessControls(BUCKET_NAME, "allUsers");

      assertNotNull(response);
      assertEquals(response.getId(), BUCKET_NAME + "/allUsers");
      assertEquals(response.getRole(), Role.WRITER);
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateBucketacl")
   public void testListBucketacl() {
      ListBucketAccessControls response = api().listBucketAccessControls(BUCKET_NAME);

      assertNotNull(response);
      assertEquals(response.getKind(), Kind.bucketAccessControls);
      assertNotNull(response.getItems());
   }

   @Test(groups = "live", dependsOnMethods = "testUpdateBucketacl")
   public void testPatchBucketacl() {
      BucketAccessControls bucketacl = BucketAccessControls.builder().bucket(BUCKET_NAME).entity("allUsers")
               .role(Role.READER).build();
      BucketAccessControls response = api().patchBucketAccessControls(BUCKET_NAME, "allUsers", bucketacl);

      assertNotNull(response);
      assertEquals(response.getId(), BUCKET_NAME + "/allUsers");
      assertEquals(response.getRole(), Role.READER);
   }
   
   @Test(groups = "live", dependsOnMethods = "testPatchBucketacl")
   public void testDeleteBucketacl() {

      HttpResponse response = api().deleteBucketAccessControls(BUCKET_NAME, "allUsers");

      assertNotNull(response);
      assertEquals(response.getStatusCode(), 204);
   }
}
