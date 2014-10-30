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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * A persistent disk resource
 */
@Beta
public final class Disk extends AbstractDisk {

   private final URI zone;
   private final Optional<URI> type;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "sizeGb", "zone",
           "status", "type"
   })
   private Disk(String id, Date creationTimestamp, URI selfLink, String name, String description,
                Integer sizeGb, URI zone, String status, @Nullable URI type) {
      super(Kind.DISK, id, creationTimestamp, selfLink, name, description, sizeGb, status);
      this.zone = checkNotNull(zone, "zone of %s", name);
      this.type = fromNullable(type);
   }

   /**
    * @return URL for the zone where the persistent disk resides.
    */
   public URI getZone() {
      return zone;
   }

   /**
    * @return URL of the disk type resource describing which disk type
    */
   public Optional<URI> getType(){
      return type;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Disk that = Disk.class.cast(obj);
      return equal(this.kind, that.kind)
              && equal(this.name, that.name)
              && equal(this.zone, that.zone);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("zone", zone);
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
      return new Builder().fromDisk(this);
   }

   public static final class Builder extends AbstractDisk.Builder<Builder> {

      private URI zone;
      private URI type;

      /**
       * @see Disk#getZone()
       */
      public Builder zone(URI zone) {
         this.zone = zone;
         return this;
      }

      /**
       * @see Disk#getType()
       */
      public Builder type(URI type){
         this.type = type;
         return this;
      }

      @Override
      protected Builder self() {
         return this;
      }

      public Disk build() {
         return new Disk(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, super.sizeGb, zone, super.status, type);
      }

      public Builder fromDisk(Disk in) {
         return super.fromAbstractDisk(in)
                 .zone(in.getZone())
                 .type(in.getType().orNull());
      }

   }

}
