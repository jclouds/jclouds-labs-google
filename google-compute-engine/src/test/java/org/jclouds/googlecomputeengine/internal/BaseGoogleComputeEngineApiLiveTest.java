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
package org.jclouds.googlecomputeengine.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.oauth.v2.OAuthTestUtils.setCredential;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.config.UserProject;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Atomics;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;


public class BaseGoogleComputeEngineApiLiveTest extends BaseApiLiveTest<GoogleComputeEngineApi> {

   protected static final String API_URL_PREFIX = "https://www.googleapis.com/compute/v1/projects/";
   protected static final String ZONE_API_URL_SUFFIX = "/zones/";
   protected static final String DEFAULT_ZONE_NAME = "us-central1-a";
   protected static final String DEFAULT_REGION_NAME = "us-central1";
   protected static final String NETWORK_API_URL_SUFFIX = "/global/networks/";
   protected static final String MACHINE_TYPE_API_URL_SUFFIX = "/machineTypes/";
   protected static final String DEFAULT_MACHINE_TYPE_NAME = "n1-standard-1";
   protected static final String GATEWAY_API_URL_SUFFIX = "/global/gateways/";
   protected static final String DEFAULT_GATEWAY_NAME = "default-internet-gateway";
   protected static final String IMAGE_API_URL_SUFFIX = "/global/images/";
   protected static final String DISK_TYPE_API_URL_SUFFIX = "/diskTypes/";

   protected Supplier<String> userProject;
   protected Predicate<AtomicReference<Operation>> operationDone;

   public BaseGoogleComputeEngineApiLiveTest() {
      provider = "google-compute-engine";
   }

    @Override
    protected Properties setupProperties() {
       Properties props = super.setupProperties();
       setCredential(props, provider + ".credential");
       return props;
    }

   protected GoogleComputeEngineApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      userProject = injector.getInstance(Key.get(new TypeLiteral<Supplier<String>>() {
      }, UserProject.class));
      operationDone = injector.getInstance(Key.get(new TypeLiteral<Predicate<AtomicReference<Operation>>>() {
      }));
      return injector.getInstance(GoogleComputeEngineApi.class);
   }

   protected void assertOperationDoneSuccessfully(Operation operation) {
      AtomicReference<Operation> ref = Atomics.newReference(checkNotNull(operation, "operation"));
      checkState(operationDone.apply(ref), "Timeout waiting for operation: %s", operation);
      assertEquals(ref.get().status(), Operation.Status.DONE);
      assertTrue(ref.get().errors().isEmpty());
   }

   protected void waitOperationDone(@Nullable Operation operation) {
      if (operation == null) {
         return;
      }
      if (!operationDone.apply(Atomics.newReference(operation))) {
         Logger.getAnonymousLogger().warning("Timeout waiting for operation: " + operation);
      }
   }

   protected URI getDiskTypeUrl(String project, String zone, String diskType){
      return URI.create(API_URL_PREFIX + project + ZONE_API_URL_SUFFIX + zone + DISK_TYPE_API_URL_SUFFIX + diskType);
   }

   protected URI getDefaultZoneUrl(String project) {
      return getZoneUrl(project, DEFAULT_ZONE_NAME);
   }

   protected URI getZoneUrl(String project, String zone) {
      return URI.create(API_URL_PREFIX + project + ZONE_API_URL_SUFFIX + zone);
   }

   protected URI getNetworkUrl(String project, String network) {
      return URI.create(API_URL_PREFIX + project + NETWORK_API_URL_SUFFIX + network);
   }

   protected URI getGatewayUrl(String project, String gateway) {
      return URI.create(API_URL_PREFIX + project + GATEWAY_API_URL_SUFFIX + gateway);
   }

   protected URI getImageUrl(String project, String image){
      return URI.create(API_URL_PREFIX + project + IMAGE_API_URL_SUFFIX + image);
   }

   protected URI getDefaultMachineTypeUrl(String project) {
      return getMachineTypeUrl(project, DEFAULT_MACHINE_TYPE_NAME);
   }

   protected URI getMachineTypeUrl(String project, String machineType) {
      return URI.create(API_URL_PREFIX + project + ZONE_API_URL_SUFFIX
              + DEFAULT_ZONE_NAME + MACHINE_TYPE_API_URL_SUFFIX + machineType);
   }

   protected URI getDiskUrl(String project, String diskName) {
      return URI.create(API_URL_PREFIX + project + ZONE_API_URL_SUFFIX + DEFAULT_ZONE_NAME + "/disks/" + diskName);
   }
}

