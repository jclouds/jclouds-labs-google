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

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.googlecomputeengine.domain.Instance.Status.RUNNING;
import static org.jclouds.googlecomputeengine.domain.Instance.Status.TERMINATED;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.squareup.okhttp.mockwebserver.MockResponse;

@Test(groups = "unit", testName = "GoogleComputeEngineServiceMockTest", singleThreaded = true)
public class GoogleComputeEngineServiceMockTest extends BaseGoogleComputeEngineApiMockTest {

   public void templateMatch() throws Exception {
      server.enqueue(singleRegionSingleZoneResponse());
      server.enqueue(jsonResponse("/image_list.json"));
      server.enqueue(jsonResponse("/image_list_debian.json")); // per IMAGE_PROJECTS = "debian-cloud"
      server.enqueue(jsonResponse("/aggregated_machinetype_list.json"));

      ComputeService computeService = computeService();

      Template template = computeService.templateBuilder().build();
      Hardware defaultSize = computeService.templateBuilder().build().getHardware();

      Hardware smallest = computeService.templateBuilder().smallest().build().getHardware();
      assertEquals(defaultSize, smallest);

      Hardware fastest = computeService.templateBuilder().fastest().build().getHardware();
      assertNotNull(fastest);

      assertEquals(computeService.listHardwareProfiles().size(), 3);

      Template toMatch = computeService.templateBuilder().imageId(template.getImage().getId()).build();
      assertEquals(toMatch.getImage(), template.getImage());

      assertSent(server, "GET", "/projects/party/regions");
      assertSent(server, "GET", "/projects/party/global/images");
      assertSent(server, "GET", "/projects/debian-cloud/global/images");
      assertSent(server, "GET", "/projects/party/aggregated/machineTypes");
   }

   public void networksAndFirewallDeletedWhenAllGroupNodesAreTerminated() throws IOException, InterruptedException {
      server.enqueue(instanceWithNetworkAndStatus("test-delete-networks", "test-network", RUNNING));
      server.enqueue(singleRegionSingleZoneResponse());
      server.enqueue(jsonResponse("/aggregated_machinetype_list.json"));
      server.enqueue(jsonResponse("/operation.json"));
      server.enqueue(jsonResponse("/zone_operation.json"));
      server.enqueue(instanceWithNetworkAndStatus("test-delete-networks", "test-network", TERMINATED));
      server.enqueue(aggregatedListWithInstanceNetworkAndStatus("test-delete-networks", "test-network", TERMINATED));
      server.enqueue(jsonResponse("/GoogleComputeEngineServiceExpectTest/network_get.json"));
      server.enqueue(jsonResponse("/GoogleComputeEngineServiceExpectTest/firewall_list.json"));
      server.enqueue(jsonResponse("/operation.json"));
      server.enqueue(jsonResponse("/zone_operation.json"));
      server.enqueue(jsonResponse("/operation.json"));
      server.enqueue(jsonResponse("/zone_operation.json"));

      ComputeService computeService = computeService();
      computeService.destroyNode(url("/jclouds/zones/us-central1-a/instances/test-delete-networks"));

      assertSent(server, "GET", "/jclouds/zones/us-central1-a/instances/test-delete-networks");
      assertSent(server, "GET", "/projects/party/regions");
      assertSent(server, "GET", "/projects/party/aggregated/machineTypes");
      assertSent(server, "DELETE", "/jclouds/zones/us-central1-a/instances/test-delete-networks");
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/operations/operation-1354084865060");
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/instances/test-delete-networks");
      assertSent(server, "GET", "/projects/party/aggregated/instances");
      assertSent(server, "GET", "/projects/party/global/networks/jclouds-test-delete");
      assertSent(server, "GET", "/projects/party/global/firewalls");
      assertSent(server, "DELETE", "/projects/party/global/firewalls/jclouds-test-delete");
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/operations/operation-1354084865060");
      assertSent(server, "DELETE", "/projects/party/global/networks/jclouds-test-delete");
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/operations/operation-1354084865060");
   }

   public void listAssignableLocations() throws Exception {
      server.enqueue(singleRegionSingleZoneResponse());

      ComputeService computeService = computeService();

      Set<? extends Location> locations = computeService.listAssignableLocations();

      assertNotNull(locations);
      assertEquals(locations.size(), 1);
      Location firstZone = locations.iterator().next();
      assertEquals(firstZone.getId(), "us-central1-a");
      assertEquals(firstZone.getDescription(), url("/projects/party/zones/us-central1-a"));
      assertEquals(firstZone.getScope(), LocationScope.ZONE);

      assertEquals(firstZone.getParent().getId(), "us-central1");
      assertEquals(firstZone.getParent().getDescription(), url("/projects/party/regions/us-central1"));
      assertEquals(firstZone.getParent().getScope(), LocationScope.REGION);

     // Google intentionally does not document locations!
      assertTrue(firstZone.getIso3166Codes().isEmpty());
      assertTrue(firstZone.getParent().getIso3166Codes().isEmpty());

      assertSent(server, "GET", "/projects/party/regions");
   }

   public void listNodes() throws Exception {
      server.enqueue(aggregatedListWithInstanceNetworkAndStatus("test-0", "test-network", RUNNING));
      server.enqueue(singleRegionSingleZoneResponse());
      server.enqueue(jsonResponse("/aggregated_machinetype_list.json"));

      Set<? extends ComputeMetadata> nodes = computeService().listNodes();
      assertEquals(nodes.size(), 1);
      NodeMetadata node = (NodeMetadata) nodes.iterator().next();
      assertNull(node.getImageId()); // not pre-cached by createNodes

      assertSent(server, "GET", "/projects/party/aggregated/instances");
      assertSent(server, "GET", "/projects/party/regions");
      assertSent(server, "GET", "/projects/party/aggregated/machineTypes");
   }

   public void createNodeWhenNetworkNorFirewallExistDoesNotExist() throws Exception {
      server.enqueue(singleRegionSingleZoneResponse());
      server.enqueue(jsonResponse("/image_list.json"));
      server.enqueue(jsonResponse("/image_list_debian.json")); // per IMAGE_PROJECTS = "debian-cloud"
      server.enqueue(jsonResponse("/aggregated_machinetype_list.json"));
      server.enqueue(new MockResponse().setResponseCode(404)); // Network
      server.enqueue(new MockResponse().setResponseCode(404)); // Network again?
      server.enqueue(jsonResponse("/operation.json")); // Create Network
      server.enqueue(jsonResponse("/zone_operation.json"));
      server.enqueue(jsonResponse("/network_get.json"));
      server.enqueue(new MockResponse().setResponseCode(404)); // Firewall
      server.enqueue(jsonResponse("/operation.json")); // Create Firewall
      server.enqueue(jsonResponse("/zone_operation.json"));
      server.enqueue(aggregatedListWithInstanceNetworkAndStatus("test-0", "test-network", RUNNING));
      server.enqueue(jsonResponse("/operation.json")); // Create Instance
      server.enqueue(instanceWithNetworkAndStatus("test-1", "test-network", RUNNING));

      ComputeService computeService = computeService();

      GoogleComputeEngineTemplateOptions options = computeService.templateOptions()
            .as(GoogleComputeEngineTemplateOptions.class).autoCreateKeyPair(false)
            .tags(ImmutableSet.of("aTag")).blockUntilRunning(false);

      Template template = computeService.templateBuilder().options(options).build();
      NodeMetadata node = getOnlyElement(computeService.createNodesInGroup("test", 1, template));

      // prove our caching works.
      assertEquals(node.getImageId(), template.getImage().getId());

      assertSent(server, "GET", "/projects/party/regions");
      assertSent(server, "GET", "/projects/party/global/images");
      assertSent(server, "GET", "/projects/debian-cloud/global/images");
      assertSent(server, "GET", "/projects/party/aggregated/machineTypes");
      assertSent(server, "GET", "/projects/party/global/networks/jclouds-test");
      assertSent(server, "GET", "/projects/party/global/networks/jclouds-test");
      assertSent(server, "POST", "/projects/party/global/networks",
            "{\"name\":\"jclouds-test\",\"IPv4Range\":\"10.0.0.0/8\"}");
      assertSent(server, "GET", "/projects/party/zones/us-central1-a/operations/operation-1354084865060");
      assertSent(server, "GET", "/projects/party/global/networks/jclouds-test");
      assertSent(server, "GET", "/projects/party/global/firewalls/jclouds-test-port-22");
      assertSent(server, "POST", "/projects/party/global/firewalls",
            stringFromResource("/firewall_insert_2.json"));

      assertSent(server, "GET", "/projects/party/zones/us-central1-a/operations/operation-1354084865060");
      assertSent(server, "GET", "/projects/party/aggregated/instances");
      assertSent(server, "POST", "/projects/party/zones/us-central1-a/instances",
            String.format(stringFromResource("/instance_insert_2.json"), template.getHardware().getId(), template.getImage().getId()));

      assertSent(server, "GET", "/projects/party/zones/us-central1-a/instances/test-1");
   }

   private MockResponse instanceWithNetworkAndStatus(String instanceName, String networkName, Instance.Status status) {
      return new MockResponse().setBody(
            stringFromResource("/instance_get.json").replace("test-0", instanceName).replace("default", networkName)
                  .replace("RUNNING", status.toString()));
   }

   private MockResponse aggregatedListWithInstanceNetworkAndStatus(String instanceName, String networkName,
         Instance.Status status) {
      return new MockResponse().setBody(
            stringFromResource("/aggregated_instance_list.json").replace("test-0", instanceName)
                  .replace("default", networkName).replace("RUNNING", status.toString()));
   }
}

