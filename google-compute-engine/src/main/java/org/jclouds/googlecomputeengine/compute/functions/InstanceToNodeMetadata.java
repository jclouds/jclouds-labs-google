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
package org.jclouds.googlecomputeengine.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.groupFromMapOrName;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Location;
import org.jclouds.googlecomputeengine.domain.Instance;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

public final class InstanceToNodeMetadata implements Function<Instance, NodeMetadata> {

   private final Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus;
   private final GroupNamingConvention nodeNamingConvention;
   private final Map<URI, URI> diskToSourceImage;
   private final Supplier<Map<URI, Hardware>> hardwares;
   private final Supplier<Map<URI, Location>> locationsByUri;
   private final FirewallTagNamingConvention.Factory firewallTagNamingConvention;

   @Inject InstanceToNodeMetadata(Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus,
                                  GroupNamingConvention.Factory namingConvention,
                                  Map<URI, URI> diskToSourceImage,
                                  @Memoized Supplier<Map<URI, Hardware>> hardwares,
                                  @Memoized Supplier<Map<URI, Location>> locationsByUri,
                                  FirewallTagNamingConvention.Factory firewallTagNamingConvention) {
      this.toPortableNodeStatus = toPortableNodeStatus;
      this.nodeNamingConvention = namingConvention.createWithoutPrefix();
      this.diskToSourceImage = diskToSourceImage;
      this.hardwares = hardwares;
      this.locationsByUri = locationsByUri;
      this.firewallTagNamingConvention = checkNotNull(firewallTagNamingConvention, "firewallTagNamingConvention");
   }

   @Override public NodeMetadata apply(Instance input) {
      String group = groupFromMapOrName(input.metadata().asMap(), input.name(), nodeNamingConvention);

      // Attempt to filter out firewallTags. Ignore failures for names that don't match jclouds expected format.
      try {
         Predicate<String> isFirewallTag = firewallTagNamingConvention.get(group).isFirewallTag();
         if (group != null) {
            for (Iterator<String> tag = input.tags().items().iterator(); tag.hasNext(); ) {
               if (isFirewallTag.apply(tag.next())) {
                  tag.remove();
               }
            }
         }
      } catch (IllegalArgumentException e){
         // pass
      }

      NodeMetadataBuilder builder = new NodeMetadataBuilder();

      Location zone = locationsByUri.get().get(input.zone());
      if (zone == null) {
         throw new IllegalStateException(
               String.format("zone %s not present in %s", input.zone(), locationsByUri.get().keySet()));
      }

      // The boot disk is the first disk. It may have been created from an image, so look it up.
      //
      // Note: This will be present if we created the node. In the future we could choose to make diskToSourceImage
      // a loading cache. That would be more expensive, but could ensure this isn't null.
      URI bootImage = diskToSourceImage.get(input.disks().get(0).source());

      builder.id(input.selfLink().toString())
             .providerId(input.id())
             .name(input.name())
             .providerId(input.id())
             .hostname(input.name())
             .location(zone)
             .imageId(bootImage != null ? bootImage.toString() : null)
             .hardware(hardwares.get().get(input.machineType()))
             .status(input.status() != null ? toPortableNodeStatus.get(input.status()) : Status.UNRECOGNIZED)
             .tags(input.tags().items())
             .uri(input.selfLink())
             .userMetadata(input.metadata().asMap())
             .group(group)
             .privateAddresses(collectPrivateAddresses(input))
             .publicAddresses(collectPublicAddresses(input));
      return builder.build();
   }

   private List<String> collectPrivateAddresses(Instance input) {
      ImmutableList.Builder<String> privateAddressesBuilder = ImmutableList.builder();
      for (Instance.NetworkInterface networkInterface : input.networkInterfaces()) {
         if (networkInterface.networkIP() != null) {
            privateAddressesBuilder.add(networkInterface.networkIP());
         }
      }
      return privateAddressesBuilder.build();
   }

   private List<String> collectPublicAddresses(Instance input) {
      ImmutableList.Builder<String> publicAddressesBuilder = ImmutableList.builder();
      for (Instance.NetworkInterface networkInterface : input.networkInterfaces()) {
         for (Instance.NetworkInterface.AccessConfig accessConfig : networkInterface.accessConfigs()) {
            if (accessConfig.natIP() != null) {
               publicAddressesBuilder.add(accessConfig.natIP());
            }
         }
      }
      return publicAddressesBuilder.build();
   }
}
