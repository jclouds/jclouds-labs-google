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
package org.jclouds.googlecomputeengine.compute;

import static com.google.common.collect.Iterables.contains;
import static org.jclouds.util.Strings2.toStringAndClose;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.googlecloud.internal.TestProperties;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@Test(groups = "live", singleThreaded = true)
public class GoogleComputeEngineServiceLiveTest extends BaseComputeServiceLiveTest {

   protected static final String DEFAULT_ZONE_NAME = "us-central1-a";

   public GoogleComputeEngineServiceLiveTest() {
      provider = "google-compute-engine";
   }

   @Override protected Properties setupProperties() {
      TestProperties.setGoogleCredentialsFromJson(provider);
      return TestProperties.apply(provider, super.setupProperties());
   }

   public void testListHardwareProfiles() throws Exception {
      GoogleComputeEngineApi api = client.getContext().unwrapApi(GoogleComputeEngineApi.class);
      ImmutableSet.Builder<String> deprecatedMachineTypes = ImmutableSet.builder();
      for (MachineType machine : api.machineTypesInZone(DEFAULT_ZONE_NAME).list().next()) {
         if (machine.deprecated() != null) {
            deprecatedMachineTypes.add(machine.id());
         }
      }
      ImmutableSet<String> deprecatedMachineTypeIds = deprecatedMachineTypes.build();
      for (Hardware hardwareProfile : client.listHardwareProfiles()) {
         assertFalse(contains(deprecatedMachineTypeIds, hardwareProfile.getId()));
      }
   }

   /**
    * Nodes may have additional metadata entries (particularly they may have an "sshKeys" entry)
    */
   protected void checkUserMetadataInNodeEquals(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      assertTrue(node.getUserMetadata().keySet().containsAll(userMetadata.keySet()));
   }

   @Test(expectedExceptions = AuthorizationException.class)
   @Override
   public void testCorrectAuthException() throws Exception {
      ComputeServiceContext context = null;
      try {
         String credential = toStringAndClose(getClass().getResourceAsStream("/test"));
         Properties overrides = setupProperties();
         overrides.setProperty(provider + ".identity", "000000000000@developer.gserviceaccount.com");
         overrides.setProperty(provider + ".credential", credential);
         context = newBuilder()
               .modules(ImmutableSet.of(getLoggingModule(), credentialStoreModule))
               .overrides(overrides).build(ComputeServiceContext.class);
         context.getComputeService().listNodes();
      } catch (AuthorizationException e) {
         throw e;
      } catch (RuntimeException e) {
         e.printStackTrace();
         throw e;
      } finally {
         if (context != null)
            context.close();
      }
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }
}
