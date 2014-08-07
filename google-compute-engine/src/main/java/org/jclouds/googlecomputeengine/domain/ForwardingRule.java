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

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;


public class ForwardingRule extends Resource {
   
   private final Optional<String> region;
   private final Optional<String> ipAddress;
   private final Optional<String> ipProtocol;
   private final String portRange;
   private final URI target;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "region",
           "IPAddress", "IPProtocol", "portRange", "target"
   })
   protected ForwardingRule(String id, Date creationTimestamp, URI selfLink,
                            String name, @Nullable String description,
                            @Nullable String region, @Nullable String ipAddress,
                            @Nullable String ipProtocol,
                            String portRange, URI target) {
      super(Kind.FORWARDING_RULE, id, creationTimestamp, selfLink, name,
            description);
      this.region = fromNullable(region);
      this.ipAddress = fromNullable(ipAddress);
      this.ipProtocol = fromNullable(ipProtocol);
      this.portRange = checkNotNull(portRange);
      this.target = checkNotNull(target);
   }
   
   /**
    * @return the region this forwarding rule resides in or absent
    * if it is a global forwarding rule.
    */
   public Optional<String> getRegion() {
      return region;
   }

   /**
    * @return External IP address of this forwarding rule.
    */
   public Optional<String> getIpAddress() {
      return ipAddress;
   }

   /**
    * @return IP protocol to which this forwarding rule applies.
    */
   public Optional<String> getIpProtocol() {
      return ipProtocol;
   }

   /**
    * @return port range to which this forwarding rule applies.
    */
   public String getPortRange() {
      return portRange;
   }

   /**
    * @return URL of the TargetHttpProxie that this forwarding rule points to.
    */
   public URI getTarget() {
      return target;
   }
   
   /**
    *  {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, name, region, ipAddress, ipProtocol,
                              portRange, target);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ForwardingRule that = ForwardingRule.class.cast(obj);
      return equal(this.kind, that.kind)
              && equal(this.name, that.name)
              && equal(this.region, that.region)
              && equal(this.ipAddress, that.ipAddress)
              && equal(this.ipProtocol, that.ipProtocol)
              && equal(this.portRange, that.portRange)
              && equal(this.target, that.target);
   }
   
   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .omitNullValues()
              .add("region", region.orNull())
              .add("ipAddress", ipAddress.orNull())
              .add("ipProtocol", ipProtocol.orNull())
              .add("portRange", portRange)
              .add("target", target);
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
      return new Builder().fromForwardingRule(this);
   }

   public static final class Builder extends Resource.Builder<Builder> {
      
      private String region;
      private String ipAddress;
      private String ipProtocol;
      private String portRange;
      private URI target;
      
      public Builder region(String region) {
         this.region = region;
         return this;
      }
      
      public Builder ipAddress(String ipAddress) {
         this.ipAddress = ipAddress;
         return this;
      }
      
      public Builder ipProtocol(String ipProtocol) {
         this.ipProtocol = ipProtocol;
         return this;
      }
      
      public Builder portRanges(String portRange) {
         this.portRange = portRange;
         return this;
      }
      
      public Builder target(URI target) {
         this.target = target;
         return this;
      }
      
      @Override
      protected Builder self() {
         return this;
      }
      
      public ForwardingRule build() {
         return new ForwardingRule(super.id, super.creationTimestamp,
                                   super.selfLink, super.name,
                                   super.description, region, ipAddress,
                                   ipProtocol, portRange, target);
      }
      
      public Builder fromForwardingRule(ForwardingRule in) {
         return super.fromResource(in)
                 .region(in.getRegion().orNull())
                 .ipAddress(in.getIpAddress().orNull())
                 .ipProtocol(in.getIpProtocol().orNull())
                 .portRanges(in.getPortRange())
                 .target(in.getTarget());
      }
   }
      
}
