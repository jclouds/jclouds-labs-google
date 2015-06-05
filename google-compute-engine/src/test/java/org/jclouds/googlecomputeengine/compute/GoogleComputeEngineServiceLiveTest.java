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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Properties;
import java.util.Set;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.googlecloud.internal.TestProperties;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.MachineType;
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

   // do not run until the auth exception problem is figured out.
   @Test(enabled = false)
   @Override
   public void testCorrectAuthException() throws Exception {
   }

   @Test(enabled = true)
   public void testCompareSizes() throws Exception {
      super.testCompareSizes();
   }

   @Test(enabled = true, dependsOnMethods = {"testCompareSizes"})
   public void testAScriptExecutionAfterBootWithBasicTemplate() throws Exception {
      super.testAScriptExecutionAfterBootWithBasicTemplate();
   }

   @Test(enabled = true, dependsOnMethods = {"testCompareSizes"})
   public void testConcurrentUseOfComputeServiceToCreateNodes() throws Exception {
      super.testConcurrentUseOfComputeServiceToCreateNodes();
   }

   @Test(enabled = true, dependsOnMethods = {"testConcurrentUseOfComputeServiceToCreateNodes"})
   public void testCreateTwoNodesWithRunScript() throws Exception {
      super.testCreateTwoNodesWithRunScript();
   }

   @Test(enabled = true, dependsOnMethods = {"testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired"})
   public void testCredentialsCache() throws Exception {
      super.testCredentialsCache();
   }

   @Test(enabled = true, dependsOnMethods = {"testCreateTwoNodesWithRunScript"})
   public void testCreateTwoNodesWithOneSpecifiedName() throws Exception {
      super.testCreateTwoNodesWithOneSpecifiedName();
   }

   @Test(enabled = true, dependsOnMethods = {"testCreateTwoNodesWithOneSpecifiedName"})
   public void testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired() throws Exception {
      super.testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired();
   }

   @Test(enabled = true, dependsOnMethods = {"testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired"})
   public void testGet() throws Exception {
      super.testGet();
   }

   @Test(enabled = true, dependsOnMethods = {"testGet"})
   public void testReboot() throws Exception {
      super.testReboot();
   }

   @Test(enabled = true, dependsOnMethods = "testReboot")
   public void testSuspendResume() throws Exception {
      super.testSuspendResume();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   public void testListNodesByIds() throws Exception {
      super.testGetNodesWithDetails();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   @Override
   public void testGetNodesWithDetails() throws Exception {
      super.testGetNodesWithDetails();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   @Override
   public void testListNodes() throws Exception {
      super.testListNodes();
   }

   @Test(enabled = true, dependsOnMethods = {"testListNodes", "testGetNodesWithDetails", "testListNodesByIds"})
   @Override
   public void testDestroyNodes() {
      super.testDestroyNodes();
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   protected void checkTagsInNodeEquals(NodeMetadata node, ImmutableSet<String> tags) {
      Set<String> nodeTags = node.getTags();
      for (String tag : tags){
         assert nodeTags.contains(tag) : String.format("node tags did not match %s %s node:", tags, nodeTags, node);
      }
   }
}
