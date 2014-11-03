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
package org.jclouds.googlecomputeengine.binders;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions;
import org.jclouds.googlecomputeengine.options.TargetPoolCreationOptions.SessionAffinityValue;
import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.binders.BindToJsonPayload;


public class TargetPoolCreationBinder extends BindToJsonPayload {

   @Inject TargetPoolCreationBinder(Json jsonBinder) {
      super(jsonBinder);
   }

   @Override public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      TargetPoolCreationOptions options = (TargetPoolCreationOptions) postParams.get("options");
      String name = postParams.get("name").toString();
      TargetPoolBinderHelper targetPoolBinderHelper = new TargetPoolBinderHelper(name, options);
      return super.bindToRequest(request, targetPoolBinderHelper);
   }

   private class TargetPoolBinderHelper{

      /**
       * Values used to bind TargetPoolCreationOptions to json request.
       */
      @SuppressWarnings("unused")
      private String name;
      @SuppressWarnings("unused")
      private List<URI> healthChecks;
      @SuppressWarnings("unused")
      private List<URI> instances;
      @SuppressWarnings("unused")
      private SessionAffinityValue sessionAffinity;
      @SuppressWarnings("unused")
      private Float failoverRatio;
      @SuppressWarnings("unused")
      private URI backupPool;
      @SuppressWarnings("unused")
      private String description; 

      private TargetPoolBinderHelper(String name, TargetPoolCreationOptions targetPoolCreationOptions){
         this.name = name;
         this.healthChecks = targetPoolCreationOptions.getHealthChecks();
         this.instances = targetPoolCreationOptions.getInstances();
         this.sessionAffinity = targetPoolCreationOptions.getSessionAffinity();
         this.failoverRatio = targetPoolCreationOptions.getFailoverRatio();
         this.backupPool = targetPoolCreationOptions.getBackupPool();
         this.description = targetPoolCreationOptions.getDescription();
      }
   }
}
