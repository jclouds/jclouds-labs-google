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
package org.jclouds.googlecomputeengine;

import java.io.Closeable;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.googlecomputeengine.features.AddressApi;
import org.jclouds.googlecomputeengine.features.AggregatedListApi;
import org.jclouds.googlecomputeengine.features.DiskApi;
import org.jclouds.googlecomputeengine.features.DiskTypeApi;
import org.jclouds.googlecomputeengine.features.FirewallApi;
import org.jclouds.googlecomputeengine.features.ForwardingRuleApi;
import org.jclouds.googlecomputeengine.features.HttpHealthCheckApi;
import org.jclouds.googlecomputeengine.features.ImageApi;
import org.jclouds.googlecomputeengine.features.InstanceApi;
import org.jclouds.googlecomputeengine.features.MachineTypeApi;
import org.jclouds.googlecomputeengine.features.NetworkApi;
import org.jclouds.googlecomputeengine.features.OperationApi;
import org.jclouds.googlecomputeengine.features.ProjectApi;
import org.jclouds.googlecomputeengine.features.RegionApi;
import org.jclouds.googlecomputeengine.features.RouteApi;
import org.jclouds.googlecomputeengine.features.SnapshotApi;
import org.jclouds.googlecomputeengine.features.TargetPoolApi;
import org.jclouds.googlecomputeengine.features.ZoneApi;
import org.jclouds.rest.annotations.Delegate;

public interface GoogleComputeEngineApi extends Closeable {

   /**
    * Provides access to Address features
    *
    * @param projectName the name of the project
    * @param region      the name of the region scoping this request.
    */
   @Delegate
   @Path("/projects/{project}/regions/{region}")
   AddressApi getAddressApi(@PathParam("project") String projectName, @PathParam("region") String region);

   /**
    * Provides access to Aggregated list features
    *
    * @param projectName the name of the project
    */
   @Delegate
   @Path("/projects/{project}")
   AggregatedListApi aggregatedList(@PathParam("project") String projectName);

   /**
    * Provides access to Disk features
    *
    * @param projectName the name of the project
    * @param zone        the name of the zone scoping this request.
    */
   @Delegate
   @Path("/projects/{project}/zones/{zone}")
   DiskApi getDiskApi(@PathParam("project") String projectName, @PathParam("zone") String zone);

   /**
    * Provides access to DiskType features
    *
    * @param projectName the name of the project
    * @param zone        the name of the zone scoping this request.
    */
   @Delegate
   @Path("/projects/{project}/zones/{zone}")
   DiskTypeApi getDiskTypeApi(@PathParam("project") String projectName, @PathParam("zone") String zone);

   /**
    * Provides access to Firewall features
    *
    * @param projectName the name of the project
    */
   @Delegate
   @Path("/projects/{project}/global")
   FirewallApi getFirewallApi(@PathParam("project") String projectName);

   /**
    * Provides access to ForwardingRule features
    *
    * @param projectName the name of the project
    * @param region     the name of the region scoping this request.
    */
   @Delegate
   @Path("/projects/{project}/regions/{region}")
   ForwardingRuleApi getForwardingRuleApi(@PathParam("project") String projectName, @PathParam("region") String region);

   /**
    * Provides access to HttpHealthCheck features
    *
    * @param projectName the name of the project
    */
   @Delegate
   @Path("/projects/{project}/global")
   HttpHealthCheckApi getHttpHealthCheckApi(@PathParam("project") String projectName);

   /**
    * Provides access to Image features
    *
    * @param projectName the name of the project
    */
   @Delegate
   @Path("/projects/{project}/global")
   ImageApi getImageApi(@PathParam("project") String projectName);

   /**
    * Provides access to Instance features
    *
    * @param projectName the name of the project
    * @param zone zone the instances are in.
    */
   @Delegate
   @Path("/projects/{project}/zones/{zone}")
   InstanceApi getInstanceApi(@PathParam("project") String projectName, @PathParam("zone") String zone);

   /**
    * Provides access to MachineType features
    *
    * @param projectName the name of the project
    * @param zone        the name of the zone scoping this request.
    */
   @Delegate
   @Path("/projects/{project}/zones/{zone}")
   MachineTypeApi getMachineTypeApi(@PathParam("project") String projectName, @PathParam("zone") String zone);

   /**
    * Provides access to Network features
    *
    * @param projectName the name of the project
    */
   @Delegate
   @Path("/projects/{project}/global")
   NetworkApi getNetworkApi(@PathParam("project") String projectName);

   /**
    * Provides access to Operation features
    *
    * @param projectName the name of the project
    */
   @Delegate
   OperationApi getOperationApi(@PathParam("project") String projectName);

   /**
    * Provides access to Project features
    */
   @Delegate
   ProjectApi getProjectApi();

   /**
    * Provides access to Region features
    *
    * @param projectName the name of the project
    */
   @Delegate
   @Path("/projects/{project}")
   RegionApi getRegionApi(@PathParam("project") String projectName);

   /**
    * Provides access to Route features
    *
    * @param projectName the name of the project
    */
   @Delegate
   @Path("/projects/{project}/global")
   RouteApi getRouteApi(@PathParam("project") String projectName);

   /**
    * Provides access to Snapshot features
    *
    * @param projectName the name of the project
    */
   @Delegate
   @Path("/projects/{project}/global")
   SnapshotApi getSnapshotApi(@PathParam("project") String projectName);

   /**
    * Provides access to TargetPool features
    *
    * @param projectName the name of the project
    * @param region     the name of the region scoping this request.
   */
   @Delegate
   @Path("/projects/{project}/regions/{region}")
   TargetPoolApi getTargetPoolApi(@PathParam("project") String projectName, @PathParam("region") String region);

   /**
    * Provides access to Zone features
    *
    * @param projectName the name of the project
    */
   @Delegate
   @Path("/projects/{project}")
   ZoneApi getZoneApi(@PathParam("project") String projectName);
}
