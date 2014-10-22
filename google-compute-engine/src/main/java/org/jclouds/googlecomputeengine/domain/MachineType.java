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
package org.jclouds.googlecomputeengine.domain;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * Represents a machine type used to host an instance.
 */
@Beta
public final class MachineType extends Resource {

   private final Integer guestCpus;
   private final Integer memoryMb;
   private final List<ScratchDisk> scratchDisks;
   private final Integer maximumPersistentDisks;
   private final Long maximumPersistentDisksSizeGb;
   private final String zone;
   private final Optional<Deprecated> deprecated;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "guestCpus", "memoryMb",
           "scratchDisks", "maximumPersistentDisks", "maximumPersistentDisksSizeGb", "zone", "deprecated"
   })
   private MachineType(String id, Date creationTimestamp, URI selfLink, String name, String description,
                       int guestCpus, int memoryMb, List<ScratchDisk> scratchDisks,
                       int maximumPersistentDisks, long maximumPersistentDisksSizeGb, String zone,
                       @Nullable Deprecated deprecated) {
      super(Kind.MACHINE_TYPE, id, creationTimestamp, selfLink, name, description);
      this.guestCpus = checkNotNull(guestCpus, "guestCpus of %s", name);
      this.memoryMb = checkNotNull(memoryMb, "memoryMb of %s", name);
      this.scratchDisks = scratchDisks == null ? ImmutableList.<ScratchDisk>of() : scratchDisks;
      this.maximumPersistentDisks = checkNotNull(maximumPersistentDisks, "maximumPersistentDisks of %s", name);
      this.maximumPersistentDisksSizeGb = maximumPersistentDisksSizeGb;
      this.zone = checkNotNull(zone, "zone of %s", name);
      this.deprecated = fromNullable(deprecated);
   }

   /**
    * @return count of CPUs exposed to the instance.
    */
   public int getGuestCpus() {
      return guestCpus;
   }

   /**
    * @return physical memory assigned to the instance, defined in MB.
    */
   public int getMemoryMb() {
      return memoryMb;
   }

   /**
    * @return extended scratch disks assigned to the instance.
    */
   public List<ScratchDisk> getScratchDisks() {
      return scratchDisks;
   }

   /**
    * @return maximum persistent disks allowed.
    */
   public int getMaximumPersistentDisks() {
      return maximumPersistentDisks;
   }

   /**
    * @return maximum total persistent disks size (GB) allowed.
    */
   public long getMaximumPersistentDisksSizeGb() {
      return maximumPersistentDisksSizeGb;
   }

   /**
    * @return the zones that this machine type can run in.
    */
   public String getZone() {
      return zone;
   }

   /**
    * @return the deprecation information for this machine type
    */
   public Optional<Deprecated> getDeprecated() {
      return deprecated;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      MachineType that = MachineType.class.cast(obj);
      return equal(this.kind, that.kind)
              && equal(this.name, that.name)
              && equal(this.zone, that.zone);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected MoreObjects.ToStringHelper string() {
      return super.string()
              .add("guestCpus", guestCpus)
              .add("memoryMb", memoryMb)
              .add("scratchDisks", scratchDisks)
              .add("maximumPersistentDisks", maximumPersistentDisks)
              .add("maximumPersistentDisksSizeGb", maximumPersistentDisksSizeGb)
              .add("zone", zone)
              .add("deprecated", deprecated.orNull());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromMachineType(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {

      private Integer guestCpus;
      private Integer memoryMb;
      private Integer imageSpaceGb;
      private ImmutableList.Builder<ScratchDisk> scratchDisks = ImmutableList.builder();
      private Integer maximumPersistentDisks;
      private Long maximumPersistentDisksSizeGb;
      private String zone;
      private Deprecated deprecated;

      /**
       * @see MachineType#getGuestCpus()
       */
      public Builder guestCpus(int guesCpus) {
         this.guestCpus = guesCpus;
         return this;
      }

      /**
       * @see MachineType#getMemoryMb()
       */
      public Builder memoryMb(int memoryMb) {
         this.memoryMb = memoryMb;
         return this;
      }

      /**
       * @see MachineType#getImageSpaceGb()
       */
      public Builder imageSpaceGb(int imageSpaceGb) {
         this.imageSpaceGb = imageSpaceGb;
         return this;
      }

      /**
       * @see MachineType#getScratchDisks()
       */
      public Builder addScratchDisk(int diskGb) {
         this.scratchDisks.add(ScratchDisk.builder().diskGb(diskGb).build());
         return this;
      }

      /**
       * @see MachineType#getScratchDisks()
       */
      public Builder scratchDisks(List<ScratchDisk> scratchDisks) {
         this.scratchDisks.addAll(scratchDisks);
         return this;
      }

      /**
       * @see MachineType#getMaximumPersistentDisks()
       */
      public Builder maximumPersistentDisks(int maximumPersistentDisks) {
         this.maximumPersistentDisks = maximumPersistentDisks;
         return this;
      }

      /**
       * @see MachineType#getMaximumPersistentDisksSizeGb()
       */
      public Builder maximumPersistentDisksSizeGb(long maximumPersistentDisksSizeGb) {
         this.maximumPersistentDisksSizeGb = maximumPersistentDisksSizeGb;
         return this;
      }

      /**
       * @see MachineType#getZone()
       */
      public Builder zone(String zone) {
         this.zone = zone;
         return this;
      }

      /**
       * @see MachineType#getDeprecated()
       */
      public Builder deprecated(Deprecated deprecated) {
         this.deprecated = deprecated;
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public MachineType build() {
         return new MachineType(id, creationTimestamp, selfLink, name, description, guestCpus, memoryMb,
                 scratchDisks.build(), maximumPersistentDisks, maximumPersistentDisksSizeGb, zone, deprecated);
      }


      public Builder fromMachineType(MachineType in) {
         return super.fromResource(in).memoryMb(in.getMemoryMb()).scratchDisks(in
                 .getScratchDisks()).maximumPersistentDisks(in.getMaximumPersistentDisks())
                 .maximumPersistentDisksSizeGb(in.getMaximumPersistentDisksSizeGb()).zone(in.getZone())
                 .deprecated(in.getDeprecated().orNull());
      }
   }

   /**
    * An scratch disk of a MachineType
    */
   public static final class ScratchDisk {

      private final int diskGb;

      @ConstructorProperties({
              "diskGb"
      })
      private ScratchDisk(int diskGb) {
         this.diskGb = diskGb;
      }

      /**
       * @return size of the scratch disk, defined in GB.
       */
      public int getDiskGb() {
         return diskGb;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode() {
         return Objects.hashCode(diskGb);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (obj == null || getClass() != obj.getClass()) return false;
         ScratchDisk that = ScratchDisk.class.cast(obj);
         return equal(this.diskGb, that.diskGb);
      }

      protected MoreObjects.ToStringHelper string() {
         return toStringHelper(this)
                 .add("diskGb", diskGb);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String toString() {
         return string().toString();
      }

      public static Builder builder() {
         return new Builder();
      }

      public Builder toBuilder() {
         return builder().fromScratchDisk(this);
      }

      public static class Builder {

         private int diskGb;

         /**
          * @see org.jclouds.googlecomputeengine.domain.MachineType.ScratchDisk#getDiskGb()
          */
         public Builder diskGb(int diskGb) {
            this.diskGb = diskGb;
            return this;
         }

         public ScratchDisk build() {
            return new ScratchDisk(diskGb);
         }

         public Builder fromScratchDisk(ScratchDisk in) {
            return new Builder().diskGb(in.getDiskGb());
         }
      }
   }
}
