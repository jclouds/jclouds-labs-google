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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Sets.filter;
import static com.google.common.collect.Sets.newTreeSet;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;
import static org.jclouds.compute.options.RunScriptOptions.Builder.nameTask;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.jclouds.compute.options.TemplateOptions.Builder.runAsRoot;
import static org.jclouds.compute.predicates.NodePredicates.TERMINATED;
import static org.jclouds.compute.predicates.NodePredicates.all;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.compute.predicates.NodePredicates.runningInGroup;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.JettyStatements;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.googlecloud.internal.TestProperties;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;

@Test(groups = "live", singleThreaded = true)
public class GoogleComputeEngineServiceLiveTest extends BaseComputeServiceLiveTest {

   protected static final String DEFAULT_ZONE_NAME = "us-central1-a";

   public GoogleComputeEngineServiceLiveTest() {
      provider = "google-compute-engine";
   }

   @Override protected Properties setupProperties() {
      return TestProperties.apply(provider, super.setupProperties());
   }

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();

      super.loginCredentials = new LoginCredentials.Builder()
            .user("broudy")
            .privateKey(keyPair.get("private"))
            .noPassword()
            .build();
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

   // reboot is not supported by GCE
   @Test(enabled = true, dependsOnMethods = "testGet")
   public void testReboot() throws Exception {
   }

   // suspend/Resume is not supported by GCE
   @Test(enabled = true, dependsOnMethods = "testReboot")
   public void testSuspendResume() throws Exception {
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
   @Test(enabled = true, dependsOnMethods = { "testCompareSizes" })
   public void testAScriptExecutionAfterBootWithBasicTemplate() throws Exception {
      String group = this.group + "r";
      try {
         client.destroyNodesMatching(inGroup(group));
      } catch (Exception e) {}

      template = buildTemplate(client.templateBuilder());
      template.getOptions().blockOnPort(22, 120);
      template.getOptions().overrideLoginCredentials(loginCredentials);
      template.getOptions().authorizePublicKey(this.keyPair.get("public"));

      try {
         Set<? extends NodeMetadata> nodes = client.createNodesInGroup(group, 1, template);
         NodeMetadata node = get(nodes, 0);
         LoginCredentials good = node.getCredentials();
         assert good.identity != null : nodes;

         for (Entry<? extends NodeMetadata, ExecResponse> response : client.runScriptOnNodesMatching(
               runningInGroup(group), "hostname",
               wrapInInitScript(false).runAsRoot(false).overrideLoginCredentials(good)).entrySet()) {
            checkResponseEqualsHostname(response.getValue(), response.getKey());
         }

         // test single-node execution
         ExecResponse response = client.runScriptOnNode(node.getId(), "hostname",
                     wrapInInitScript(false).runAsRoot(false));
         checkResponseEqualsHostname(response, node);
         OperatingSystem os = node.getOperatingSystem();

         // test bad password
         tryBadPassword(group, good);

         runScriptWithCreds(group, os, good);

         checkNodes(nodes, group, "runScriptWithCreds");

         // test adding AdminAccess later changes the default boot user, in this
         // case to foo, with home dir /over/ridden/foo
         ListenableFuture<ExecResponse> future = client.submitScriptOnNode(node.getId(), AdminAccess.builder()
               .adminUsername("foo").adminHome("/over/ridden/foo").build(), nameTask("adminUpdate"));

         response = future.get(3, TimeUnit.MINUTES);

         assert response.getExitStatus() == 0 : node.getId() + ": " + response;

         node = client.getNodeMetadata(node.getId());
         // test that the node updated to the correct admin user!
         assertEquals(node.getCredentials().identity, "foo");
         assert node.getCredentials().credential != null : nodes;

         weCanCancelTasks(node);

         assert response.getExitStatus() == 0 : node.getId() + ": " + response;

         response = client.runScriptOnNode(node.getId(), "echo $USER", wrapInInitScript(false).runAsRoot(false));

         assert response.getOutput().trim().equals("foo") : node.getId() + ": " + response;

      } finally {
         client.destroyNodesMatching(inGroup(group));
      }

   }

   @Override
   @Test(enabled = true, dependsOnMethods = "testCompareSizes")
   public void testConcurrentUseOfComputeServiceToCreateNodes() throws Exception{

      final long timeoutMs = 20 * 60 * 1000;
      List<String> groups = Lists.newArrayList();
      List<ListenableFuture<NodeMetadata>> futures = Lists.newArrayList();
      ListeningExecutorService userExecutor = context.utils().injector()
            .getInstance(Key.get(ListeningExecutorService.class, Names.named(PROPERTY_USER_THREADS)));

      try {
         for (int i = 0; i < 2; i++) {
            final int groupNum = i;
            final String group = "twin" + groupNum;
            groups.add(group);
            template = buildTemplate(client.templateBuilder());
            template.getOptions().inboundPorts(22, 8080).blockOnPort(22, 300 + groupNum);
            template.getOptions().overrideLoginCredentials(loginCredentials);
            template.getOptions().authorizePublicKey(this.keyPair.get("public"));


            ListenableFuture<NodeMetadata> future = userExecutor.submit(new Callable<NodeMetadata>() {
               public NodeMetadata call() throws Exception {
                  NodeMetadata node = getOnlyElement(client.createNodesInGroup(group, 1, template));
                  getAnonymousLogger().info("Started node " + node.getId());
                  return node;
               }
            });
            futures.add(future);
         }

         ListenableFuture<List<NodeMetadata>> compoundFuture = Futures.allAsList(futures);
         compoundFuture.get(timeoutMs, TimeUnit.MILLISECONDS);

      } finally {
         for (String group : groups) {
            client.destroyNodesMatching(inGroup(group));
         }
      }

   }

   @Override
   protected void createAndRunAServiceInGroup(String group) throws RunNodesException {

      // note that some cloud providers do not support mixed case tag names
      ImmutableMap<String, String> userMetadata = ImmutableMap.<String, String> of("test", group);

      ImmutableSet<String> tags = ImmutableSet.of(group);
      Stopwatch watch = Stopwatch.createStarted();

      template = buildTemplate(client.templateBuilder());
      template.getOptions().inboundPorts(22, 8080).blockOnPort(22, 300).userMetadata(userMetadata).tags(tags);

      template.getOptions().overrideLoginCredentials(loginCredentials);
      template.getOptions().authorizePublicKey(this.keyPair.get("public"));

      NodeMetadata node = getOnlyElement(client.createNodesInGroup(group, 1, template));
      long createSeconds = watch.elapsed(TimeUnit.SECONDS);

      final String nodeId = node.getId();

      checkUserMetadataContains(node, userMetadata);
      checkTagsInNodeEquals(node, tags);

      getAnonymousLogger().info(
            format("<< available node(%s) os(%s) in %ss", node.getId(), node.getOperatingSystem(), createSeconds));

      watch.reset().start();

      client.runScriptOnNode(nodeId, JettyStatements.install(), nameTask("configure-jetty"));

      long configureSeconds = watch.elapsed(TimeUnit.SECONDS);

      getAnonymousLogger().info(
            format(
                  "<< configured node(%s) with %s and jetty %s in %ss",
                  nodeId,
                  exec(nodeId, "java -fullversion"),
                  exec(nodeId, JettyStatements.version()), configureSeconds));

      trackAvailabilityOfProcessOnNode(JettyStatements.start(), "start jetty", node);

      client.runScriptOnNode(nodeId, JettyStatements.stop(), runAsRoot(false).wrapInInitScript(false));

      trackAvailabilityOfProcessOnNode(JettyStatements.start(), "start jetty", node);
   }


   @Override
   @Test(enabled = true, dependsOnMethods = "testCreateTwoNodesWithRunScript")
   public void testCreateTwoNodesWithOneSpecifiedName() throws Exception {
      template = buildTemplate(client.templateBuilder());
      template.getOptions().nodeNames(ImmutableSet.of("first-node"));

      template.getOptions().overrideLoginCredentials(loginCredentials);
      template.getOptions().authorizePublicKey(this.keyPair.get("public"));
      System.out.println("STARTING " + template.getOptions());
      Set<? extends NodeMetadata> nodes;
      try {
         nodes = newTreeSet(client.createNodesInGroup(group, 2, template));
      } catch (RunNodesException e) {
         nodes = newTreeSet(concat(e.getSuccessfulNodes(), e.getNodeErrors().keySet()));
         throw e;
      }
      System.out.println("MADE IT THIS FAR");
      assertEquals(nodes.size(), 2, "expected two nodes but was " + nodes);
      System.out.println("MADE IT THIS FAR1");
      NodeMetadata node1 = Iterables.getFirst(nodes, null);
      NodeMetadata node2 = Iterables.getLast(nodes, null);
      // credentials aren't always the same
      // assertEquals(node1.getCredentials(), node2.getCredentials());
      System.out.println("MADE IT THIS FAR2");

      assertTrue(node1.getName().equals("first-node") || node2.getName().equals("first-node"),
              "one node should be named 'first-node'");
      assertFalse(node1.getName().equals("first-node") && node2.getName().equals("first-node"),
              "one node should be named something other than 'first-node");

      System.out.println("MADE IT THIS FAR3");
      this.nodes.addAll(nodes);
   }

   @Override
   @Test(enabled = true, dependsOnMethods = "testConcurrentUseOfComputeServiceToCreateNodes")
   public void testCreateTwoNodesWithRunScript() throws Exception {
      try {
         client.destroyNodesMatching(inGroup(group));
      } catch (NoSuchElementException e) {

      }
      refreshTemplate();

      template.getOptions().overrideLoginCredentials(loginCredentials);
      template.getOptions().authorizePublicKey(this.keyPair.get("public"));

      try {
         nodes = newTreeSet(client.createNodesInGroup(group, 2, template));
      } catch (RunNodesException e) {
         nodes = newTreeSet(concat(e.getSuccessfulNodes(), e.getNodeErrors().keySet()));
         throw e;
      }

      assertEquals(nodes.size(), 2, "expected two nodes but was " + nodes);
      checkNodes(nodes, group, "bootstrap");
      NodeMetadata node1 = nodes.first();
      NodeMetadata node2 = nodes.last();
      // credentials aren't always the same
      // assertEquals(node1.getCredentials(), node2.getCredentials());

      assertLocationSameOrChild(checkNotNull(node1.getLocation(), "location of %s", node1), template.getLocation());
      assertLocationSameOrChild(checkNotNull(node2.getLocation(), "location of %s", node2), template.getLocation());
      checkImageIdMatchesTemplate(node1);
      checkImageIdMatchesTemplate(node2);
      checkOsMatchesTemplate(node1);
      checkOsMatchesTemplate(node2);
   }

   void assertLocationSameOrChild(final Location test, final Location expected) {
      if (!test.equals(expected)) {
         assertEquals(test.getParent().getId(), expected.getId());
      } else {
         assertEquals(test, expected);
      }
   }

   private Template refreshTemplate() {
      return template = addRunScriptToTemplate(buildTemplate(client.templateBuilder()));
   }

   @Test(enabled = true, dependsOnMethods = "testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired")
   public void testCredentialsCache() throws Exception {
      initializeContext();
      for (NodeMetadata node : nodes)
         assert view.utils().credentialStore().get("node#" + node.getId()) != null : "credentials for " + node.getId();
   }

   @Test(enabled = true, dependsOnMethods = "testCreateTwoNodesWithOneSpecifiedName")
   public void testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired() throws Exception {
      initializeContext();

      Location existingLocation = Iterables.get(this.nodes, 0).getLocation();
      boolean existingLocationIsAssignable = Iterables.any(client.listAssignableLocations(),
            Predicates.equalTo(existingLocation));

      if (existingLocationIsAssignable) {
         getAnonymousLogger().info("creating another node based on existing nodes' location: " + existingLocation);
         template = buildTemplate(client.templateBuilder());
         template = addRunScriptToTemplate(client.templateBuilder().fromTemplate(template)
               .locationId(existingLocation.getId()).build());
      } else {
         refreshTemplate();

         getAnonymousLogger().info(
               format("%s is not assignable; using template's location %s as  ", existingLocation,
                     template.getLocation()));
      }
      template.getOptions().overrideLoginCredentials(loginCredentials);
      template.getOptions().authorizePublicKey(this.keyPair.get("public"));


      Set<? extends NodeMetadata> nodes = client.createNodesInGroup(group, 1, template);
      assertEquals(nodes.size(), 1);
      checkNodes(nodes, group, "bootstrap");
      NodeMetadata node = Iterables.getOnlyElement(nodes);
      if (existingLocationIsAssignable)
         assertEquals(node.getLocation(), existingLocation);
      else
         this.assertLocationSameOrChild(checkNotNull(node.getLocation(), "location of %s", node), template.getLocation());
      checkOsMatchesTemplate(node);
      this.nodes.add(node);
   }

   @Test(enabled = true, dependsOnMethods = "testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired")
   public void testGet() throws Exception {
      Map<String, ? extends NodeMetadata> metadataMap = newLinkedHashMap(uniqueIndex(
            filter(client.listNodesDetailsMatching(all()), and(inGroup(group), not(TERMINATED))),
            new Function<NodeMetadata, String>() {

               @Override
               public String apply(NodeMetadata from) {
                  return from.getId();
               }

            }));
      for (NodeMetadata node : nodes) {
         metadataMap.remove(node.getId());
         NodeMetadata metadata = client.getNodeMetadata(node.getId());
         assertEquals(metadata.getProviderId(), node.getProviderId());
         assertEquals(metadata.getGroup(), node.getGroup());
         assertLocationSameOrChild(checkNotNull(metadata.getLocation(), "location of %s", metadata), template.getLocation());
         checkImageIdMatchesTemplate(metadata);
         checkOsMatchesTemplate(metadata);
         assert metadata.getStatus() == Status.RUNNING : metadata;
         // due to DHCP the addresses can actually change in-between runs.
         assertEquals(metadata.getPrivateAddresses().size(), node.getPrivateAddresses().size(), format(
               "[%s] didn't match: [%s]", metadata.getPrivateAddresses(), node.getPrivateAddresses().size()));
         assertEquals(metadata.getPublicAddresses().size(), node.getPublicAddresses().size(), format(
               "[%s] didn't match: [%s]", metadata.getPublicAddresses(), node.getPublicAddresses().size()));
      }
      assertNodeZero(metadataMap.values());
   }


   //TODO: figure out why this takes so long. Is it blocking?
   protected int nonBlockDurationSeconds = 60;
   @Override
   public void testOptionToNotBlock() throws Exception {

      String group = this.group + "block";
      try {
         client.destroyNodesMatching(inGroup(group));
      } catch (Exception e) {

      }
      // no inbound ports
      template = buildTemplate(client.templateBuilder());
      template.getOptions().blockUntilRunning(false).inboundPorts();
      template.getOptions().overrideLoginCredentials(loginCredentials);
      template.getOptions().authorizePublicKey(this.keyPair.get("public"));

      try {
         long time = currentTimeMillis();
         Set<? extends NodeMetadata> nodes = client.createNodesInGroup(group, 1, template);
         NodeMetadata node = getOnlyElement(nodes);
         assert node.getStatus() != Status.RUNNING : node;
         long duration = (currentTimeMillis() - time) / 1000;
         assert duration < nonBlockDurationSeconds : format("duration(%d) longer than expected(%d) seconds! ",
               duration, nonBlockDurationSeconds);
      } finally {
         client.destroyNodesMatching(inGroup(group));
      }

   }

}
