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

import java.net.URI;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BackendServiceGroupHealth {

   public abstract List<HealthStatus> healthStatus();

   @SerializedNames({"healthStatus"})
   public static BackendServiceGroupHealth create(List<HealthStatus> healthStatus){
      return new AutoValue_BackendServiceGroupHealth(healthStatus);
   }

   BackendServiceGroupHealth(){
   }

   @AutoValue
   public abstract static class HealthStatus {

      @Nullable public abstract String ipAddress();
      public abstract Integer port();
      public abstract URI instance();
      public abstract String healthState();

      @SerializedNames({"ipAddress", "port", "instance", "healthState"})
      public static HealthStatus create(String ipAddress, int port, URI instance, String healthState) {
         return new AutoValue_BackendServiceGroupHealth_HealthStatus(ipAddress, port, instance, healthState);
      }

      HealthStatus(){
      }
   }
}
