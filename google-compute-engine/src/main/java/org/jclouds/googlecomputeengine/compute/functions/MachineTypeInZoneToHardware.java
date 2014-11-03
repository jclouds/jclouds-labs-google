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
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.net.URI;
import java.util.Map;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.VolumeBuilder;
import org.jclouds.domain.Location;
import org.jclouds.googlecomputeengine.compute.domain.MachineTypeInZone;
import org.jclouds.googlecomputeengine.compute.domain.SlashEncodedIds;
import org.jclouds.googlecomputeengine.domain.MachineType;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * Transforms a google compute domain specific machine type to a generic Hardware object.
 */
public class MachineTypeInZoneToHardware implements Function<MachineTypeInZone, Hardware> {

   private final Supplier<Map<URI, ? extends Location>> locations;

   @Inject
   public MachineTypeInZoneToHardware(@Memoized Supplier<Map<URI, ? extends Location>> locations) {
      this.locations = locations;
   }

   @Override
   public Hardware apply(final MachineTypeInZone input) {
      Iterable<? extends Location> zonesForMachineType = filter(locations.get().values(), new Predicate<Location>() {
         @Override
         public boolean apply(Location l) {
            return l.getId().equals(input.machineType().zone());
         }
      });

      Location location = checkNotNull(getOnlyElement(zonesForMachineType),
              "location for %s",
              input.machineType().zone());

      return new HardwareBuilder()
              .id(SlashEncodedIds.fromTwoIds(input.machineType().zone(), input.machineType().name()).slashEncode())
              .location(location)
              .name(input.machineType().name())
              .hypervisor("kvm")
              .processor(new Processor(input.machineType().guestCpus(), 1.0))
              .providerId(input.machineType().id())
              .ram(input.machineType().memoryMb())
              .uri(input.machineType().selfLink())
              .volumes(collectVolumes(input.machineType()))
              .supportsImage(Predicates.<Image>alwaysTrue())
              .build();
   }

   private Iterable<Volume> collectVolumes(MachineType input) {
      ImmutableSet.Builder<Volume> volumes = ImmutableSet.builder();
      for (MachineType.ScratchDisk disk : input.scratchDisks()) {
         volumes.add(new VolumeBuilder()
                 .type(Volume.Type.LOCAL)
                 .size(Float.valueOf(disk.diskGb()))
                 .bootDevice(true)
                 .durable(false).build());
      }
      return volumes.build();
   }
}
