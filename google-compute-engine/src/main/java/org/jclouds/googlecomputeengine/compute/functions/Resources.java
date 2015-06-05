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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.Fallbacks.NullOnNotFoundOr404;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.net.URI;

import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.Network;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;

@SkipEncoding({'/', '='})
@RequestFilters(OAuthFilter.class)
@Consumes(APPLICATION_JSON)
public interface Resources {

   /** Returns an instance by self-link or null if not found. */
   @Named("Instances:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class) @Nullable Instance instance(@EndpointParam URI selfLink);

   /** Returns an network by self-link or null if not found. */
   @Named("Networks:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class) @Nullable Network network(@EndpointParam URI selfLink);

   /** Returns an operation by self-link or null if not found. */
   @Named("Operations:get")
   @GET
   @Fallback(NullOnNotFoundOr404.class) @Nullable Operation operation(@EndpointParam URI selfLink);

   /** Deletes any resource by self-link and returns the operation in progress, or null if not found. */
   @Named("Resources:delete")
   @DELETE
   @Fallback(NullOnNotFoundOr404.class) @Nullable Operation delete(@EndpointParam URI selfLink);

   /** Hard-resets the instance by self-link and returns the operation in progres */
   @Named("Instances:reset")
   @POST
   @Path("/reset")
   Operation resetInstance(@EndpointParam URI selfLink);

   /**
    * This method starts an instance that was stopped using the using the {@link #stop(String)} method.
    * @param instance - name of the instance to be started
    */
   @Named("Instances:start")
   @POST
   @Path("/start")
   @Produces(APPLICATION_JSON)
   Operation startInstance(@EndpointParam URI selfLink);

   /**
    * This method stops a running instance, shutting it down cleanly, and allows you to restart
    *  the instance at a later time. Stopped instances do not incur per-minute, virtual machine
    *  usage charges while they are stopped, but any resources that the virtual machine is using,
    *  such as persistent disks and static IP addresses,will continue to be charged until they are deleted.
    * @param instance
    * @return
    */
   @Named("Instances:stop")
   @POST
   @Path("/stop")
   @Produces(APPLICATION_JSON)
   Operation stopInstance(@EndpointParam URI selfLink);
}
