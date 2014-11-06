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

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;

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
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

public final class MachineTypeInZoneToHardware implements Function<MachineTypeInZone, Hardware> {

   private final Supplier<Map<URI, Location>> locationsByUri;

   @Inject MachineTypeInZoneToHardware(@Memoized Supplier<Map<URI, Location>> locationsByUri) {
      this.locationsByUri = locationsByUri;
   }

   @Override
   public Hardware apply(MachineTypeInZone input) {
      URI zoneLink = URI.create(
            input.machineType().selfLink().toString().replace("/machineTypes/" + input.machineType().name(), ""));

      Location zone = locationsByUri.get().get(zoneLink);
      if (zone == null) {
         throw new IllegalStateException(
               String.format("zone %s not present in %s", zoneLink, locationsByUri.get().keySet()));
      }
      return new HardwareBuilder()
              .id(SlashEncodedIds.from(input.machineType().zone(), input.machineType().name()).slashEncode())
              .location(zone)
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
