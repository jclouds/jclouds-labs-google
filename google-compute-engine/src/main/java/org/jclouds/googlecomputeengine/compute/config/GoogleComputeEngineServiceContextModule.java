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
package org.jclouds.googlecomputeengine.compute.config;

import static com.google.common.base.Suppliers.memoizeWithExpiration;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.googlecomputeengine.config.GoogleComputeEngineProperties.OPERATION_COMPLETE_INTERVAL;
import static org.jclouds.googlecomputeengine.config.GoogleComputeEngineProperties.OPERATION_COMPLETE_TIMEOUT;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;
import static org.jclouds.util.Predicates2.retry;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.SecurityGroup;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.googlecomputeengine.compute.GoogleComputeEngineService;
import org.jclouds.googlecomputeengine.compute.GoogleComputeEngineServiceAdapter;
import org.jclouds.googlecomputeengine.compute.domain.NetworkAndAddressRange;
import org.jclouds.googlecomputeengine.compute.extensions.GoogleComputeEngineSecurityGroupExtension;
import org.jclouds.googlecomputeengine.compute.functions.CreateNetworkIfNeeded;
import org.jclouds.googlecomputeengine.compute.functions.FindNetworkOrCreate;
import org.jclouds.googlecomputeengine.compute.functions.FirewallTagNamingConvention;
import org.jclouds.googlecomputeengine.compute.functions.FirewallToIpPermission;
import org.jclouds.googlecomputeengine.compute.functions.GoogleComputeEngineImageToImage;
import org.jclouds.googlecomputeengine.compute.functions.InstanceToNodeMetadata;
import org.jclouds.googlecomputeengine.compute.functions.MachineTypeToHardware;
import org.jclouds.googlecomputeengine.compute.functions.NetworkToSecurityGroup;
import org.jclouds.googlecomputeengine.compute.functions.OrphanedGroupsFromDeadNodes;
import org.jclouds.googlecomputeengine.compute.functions.Resources;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.compute.predicates.AllNodesInGroupTerminated;
import org.jclouds.googlecomputeengine.compute.predicates.AtomicInstanceVisible;
import org.jclouds.googlecomputeengine.compute.predicates.AtomicOperationDone;
import org.jclouds.googlecomputeengine.compute.strategy.CreateNodesWithGroupEncodedIntoNameThenAddToSet;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Image;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.implicit.FirstZone;
import org.jclouds.net.domain.IpPermission;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

public final class GoogleComputeEngineServiceContextModule
      extends ComputeServiceAdapterContextModule<Instance, MachineType, Image, Location> {

   @Override
   protected void configure() {
      super.configure();

      bind(ComputeService.class).to(GoogleComputeEngineService.class);

      bind(new TypeLiteral<ComputeServiceAdapter<Instance, MachineType, Image, Location>>() {
      }).to(GoogleComputeEngineServiceAdapter.class);

      // Use compute service to supply locations, which are always zones.
      install(new LocationsFromComputeServiceAdapterModule<Instance, MachineType, Image, Location>() {
      });
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).toInstance(Functions.<Location>identity());
      bind(ImplicitLocationSupplier.class).to(FirstZone.class);

      bind(new TypeLiteral<Function<Instance, NodeMetadata>>() {
      }).to(InstanceToNodeMetadata.class);

      bind(new TypeLiteral<Function<MachineType, Hardware>>() {
      }).to(MachineTypeToHardware.class);

      bind(new TypeLiteral<Function<Image, org.jclouds.compute.domain.Image>>() {
      }).to(GoogleComputeEngineImageToImage.class);

      bind(new TypeLiteral<Function<Firewall, Iterable<IpPermission>>>() {
      }).to(FirewallToIpPermission.class);

      bind(new TypeLiteral<Function<Network, SecurityGroup>>() {
      }).to(NetworkToSecurityGroup.class);

      bind(org.jclouds.compute.strategy.impl.CreateNodesWithGroupEncodedIntoNameThenAddToSet.class)
            .to(CreateNodesWithGroupEncodedIntoNameThenAddToSet.class);

      bind(TemplateOptions.class).to(GoogleComputeEngineTemplateOptions.class);

      bind(new TypeLiteral<Function<Set<? extends NodeMetadata>, Set<String>>>() {
      }).to(OrphanedGroupsFromDeadNodes.class);

      bind(new TypeLiteral<Predicate<String>>() {
      }).to(AllNodesInGroupTerminated.class);

      bind(new TypeLiteral<Function<NetworkAndAddressRange, Network>>() {
      }).to(CreateNetworkIfNeeded.class);

      bind(new TypeLiteral<CacheLoader<NetworkAndAddressRange, Network>>() {
      }).to(FindNetworkOrCreate.class);

      bind(SecurityGroupExtension.class).to(GoogleComputeEngineSecurityGroupExtension.class);
      bind(FirewallTagNamingConvention.Factory.class).in(Scopes.SINGLETON);
      bindHttpApi(binder(), Resources.class);
   }

   // TODO: these timeouts need thinking through.
   @Provides Predicate<AtomicReference<Operation>> operationDone(AtomicOperationDone input,
         @Named(OPERATION_COMPLETE_TIMEOUT) long timeout, @Named(OPERATION_COMPLETE_INTERVAL) long interval) {
      return retry(input, timeout, interval, MILLISECONDS);
   }

   @Provides Predicate<AtomicReference<Instance>> instanceVisible(AtomicInstanceVisible input,
         @Named(OPERATION_COMPLETE_TIMEOUT) long timeout, @Named(OPERATION_COMPLETE_INTERVAL) long interval) {
      return retry(input, timeout, interval, MILLISECONDS);
   }

   @Provides @Singleton Map<URI, URI> diskToSourceImage() {
      return Maps.newConcurrentMap();
   }

   @Provides @Singleton @Memoized Supplier<Map<URI, Hardware>> hardwareByUri(
         @Memoized final Supplier<Set<? extends Hardware>> hardwareSupplier,
         @Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      return memoizeWithExpiration(new Supplier<Map<URI, Hardware>>() {
         @Override public Map<URI, Hardware> get() {
            ImmutableMap.Builder<URI, Hardware> result = ImmutableMap.builder();
            for (Hardware hardware : hardwareSupplier.get()) {
               result.put(hardware.getUri(), hardware);
            }
            return result.build();
         }
      }, seconds, SECONDS);
   }

   @Provides @Singleton @Memoized Supplier<Map<URI, Location>> locationsByUri(
         @Memoized final Supplier<Set<? extends Location>> locations, @Named(PROPERTY_SESSION_INTERVAL) long seconds) {
      return memoizeWithExpiration(new Supplier<Map<URI, Location>>() {
         @Override public Map<URI, Location> get() {
            ImmutableMap.Builder<URI, Location> result = ImmutableMap.builder();
            for (Location location : locations.get()) {
               result.put(URI.create(location.getDescription()), location);
            }
            return result.build();
         }
      }, seconds, SECONDS);
   }

   @Provides @Singleton
   LoadingCache<NetworkAndAddressRange, Network> networkMap(CacheLoader<NetworkAndAddressRange, Network> in) {
      return CacheBuilder.newBuilder().build(in);
   }

   @Override protected Optional<ImageExtension> provideImageExtension(Injector i) {
      return Optional.absent();
   }

   @Override protected Optional<SecurityGroupExtension> provideSecurityGroupExtension(Injector i) {
      return Optional.of(i.getInstance(SecurityGroupExtension.class));
   }

   private static final Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus =
         ImmutableMap.<Instance.Status, NodeMetadata.Status>builder()
                     .put(Instance.Status.PROVISIONING, NodeMetadata.Status.PENDING)
                     .put(Instance.Status.STAGING, NodeMetadata.Status.PENDING)
                     .put(Instance.Status.RUNNING, NodeMetadata.Status.RUNNING)
                     .put(Instance.Status.STOPPING, NodeMetadata.Status.PENDING)
                     .put(Instance.Status.STOPPED, NodeMetadata.Status.SUSPENDED)
                     .put(Instance.Status.TERMINATED, NodeMetadata.Status.TERMINATED).build();

   @Provides Map<Instance.Status, NodeMetadata.Status> toPortableNodeStatus() {
      return toPortableNodeStatus;
   }
}
