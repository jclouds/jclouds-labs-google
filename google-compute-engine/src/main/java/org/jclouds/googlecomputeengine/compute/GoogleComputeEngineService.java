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

import static autovalue.shaded.com.google.common.common.collect.Sets.newHashSet;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.compute.predicates.NodePredicates.all;
import static org.jclouds.googlecloud.internal.ListPages.concat;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.Constants;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.extensions.SecurityGroupExtension;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.internal.PersistNodeCredentials;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetImageStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.InitializeRunScriptOnNodeOrPlaceInBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.features.FirewallApi;
import org.jclouds.scriptbuilder.functions.InitAdminAccess;

import autovalue.shaded.com.google.common.common.collect.ImmutableSet;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Atomics;
import com.google.common.util.concurrent.ListeningExecutorService;

public final class GoogleComputeEngineService extends BaseComputeService {

   private final Function<Set<? extends NodeMetadata>, Set<String>> findOrphanedGroups;
   private final GroupNamingConvention.Factory namingConvention;
   private final GoogleComputeEngineApi api;
   private final Predicate<AtomicReference<Operation>> operationDone;

   @Inject GoogleComputeEngineService(ComputeServiceContext context,
                                        Map<String, Credentials> credentialStore,
                                        @Memoized Supplier<Set<? extends Image>> images,
                                        @Memoized Supplier<Set<? extends Hardware>> hardwareProfiles,
                                        @Memoized Supplier<Set<? extends Location>> locations,
                                        ListNodesStrategy listNodesStrategy,
                                        GetImageStrategy getImageStrategy,
                                        GetNodeMetadataStrategy getNodeMetadataStrategy,
                                        CreateNodesInGroupThenAddToSet runNodesAndAddToSetStrategy,
                                        RebootNodeStrategy rebootNodeStrategy,
                                        DestroyNodeStrategy destroyNodeStrategy,
                                        ResumeNodeStrategy resumeNodeStrategy,
                                        SuspendNodeStrategy suspendNodeStrategy,
                                        Provider<TemplateBuilder> templateBuilderProvider,
                                        @Named("DEFAULT") Provider<TemplateOptions> templateOptionsProvider,
                                        @Named(TIMEOUT_NODE_RUNNING) Predicate<AtomicReference<NodeMetadata>>
                                                nodeRunning,
                                        @Named(TIMEOUT_NODE_TERMINATED) Predicate<AtomicReference<NodeMetadata>>
                                                nodeTerminated,
                                        @Named(TIMEOUT_NODE_SUSPENDED)
                                        Predicate<AtomicReference<NodeMetadata>> nodeSuspended,
                                        InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory,
                                        InitAdminAccess initAdminAccess,
                                        RunScriptOnNode.Factory runScriptOnNodeFactory,
                                        PersistNodeCredentials persistNodeCredentials,
                                        ComputeServiceConstants.Timeouts timeouts,
                                        @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
                                        Optional<ImageExtension> imageExtension,
                                        Optional<SecurityGroupExtension> securityGroupExtension,
                                        Function<Set<? extends NodeMetadata>, Set<String>> findOrphanedGroups,
                                        GroupNamingConvention.Factory namingConvention,
                                        GoogleComputeEngineApi api,
                                        Predicate<AtomicReference<Operation>> operationDone) {
      super(context, credentialStore, images, hardwareProfiles, locations, listNodesStrategy, getImageStrategy,
              getNodeMetadataStrategy, runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy,
              resumeNodeStrategy, suspendNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning,
              nodeTerminated, nodeSuspended, initScriptRunnerFactory, initAdminAccess, runScriptOnNodeFactory,
              persistNodeCredentials, timeouts, userExecutor, imageExtension, securityGroupExtension);
      this.findOrphanedGroups = findOrphanedGroups;
      this.namingConvention = namingConvention;
      this.api = api;
      this.operationDone = operationDone;
   }
   
   @Override
   public void destroyNode(String id) {
      // GCE does not return TERMINATED nodes, so in practice no node will never reach the TERMINATED
      // state, and the deleted nodes will never be returned.
      // In order to be able to clean up the resources associated to the deleted nodes, we have to retrieve
      // the details of the nodes before deleting them.
      NodeMetadata node = getNodeMetadata(id);
      super.destroyNode(id);
      cleanUpIncidentalResourcesOfDeadNodes(ImmutableSet.of(node));
   }

   @Override
   public Set<? extends NodeMetadata> destroyNodesMatching(Predicate<NodeMetadata> filter) {
      // GCE does not return TERMINATED nodes, so in practice no node will never reach the TERMINATED
      // state, and the deleted nodes will never be returned.
      // In order to be able to clean up the resources associated to the deleted nodes, we have to retrieve
      // the details of the nodes before deleting them.
      Set<? extends NodeMetadata> nodes = newHashSet(filter(listNodesDetailsMatching(all()), filter));
      super.destroyNodesMatching(filter); // This returns an empty list (a list of null elements) in GCE, as the api does not return deleted nodes
      cleanUpIncidentalResourcesOfDeadNodes(nodes);
      return nodes;
   }



   @Override
   protected synchronized void cleanUpIncidentalResourcesOfDeadNodes(Set<? extends NodeMetadata> deadNodes) {
      Set<String> orphanedGroups = findOrphanedGroups.apply(deadNodes);
      for (String orphanedGroup : orphanedGroups) {
         cleanUpNetworksAndFirewallsForGroup(orphanedGroup);
      }
   }

   private void cleanUpNetworksAndFirewallsForGroup(final String groupName) {
      String resourceName = namingConvention.create().sharedNameForGroup(groupName);
      Network network = api.networks().get(resourceName);
      FirewallApi firewallApi = api.firewalls();

      if (network != null) {
         for (Firewall firewall : concat(firewallApi.list())) {
            if (firewall == null || firewall.network() == null || !firewall.network().equals(network.selfLink())) {
               continue;
            }
            AtomicReference<Operation> operation = Atomics.newReference(firewallApi.delete(firewall.name()));
            operationDone.apply(operation);
   
            if (operation.get().httpErrorStatusCode() != null) {
               logger.warn("delete orphaned firewall %s failed. Http Error Code: %d HttpError: %s",
                     operation.get().targetId(), operation.get().httpErrorStatusCode(),
                     operation.get().httpErrorMessage());
            }
         }
    
         AtomicReference<Operation> operation = Atomics.newReference(api.networks().delete(resourceName));

         operationDone.apply(operation);

         if (operation.get().httpErrorStatusCode() != null) {
            logger.warn("delete orphaned network failed. Http Error Code: " + operation.get().httpErrorStatusCode() +
                  " HttpError: " + operation.get().httpErrorMessage());
         }
      }
   }

   @Override
   public GoogleComputeEngineTemplateOptions templateOptions() {
      return GoogleComputeEngineTemplateOptions.class.cast(super.templateOptions());
   }
}
