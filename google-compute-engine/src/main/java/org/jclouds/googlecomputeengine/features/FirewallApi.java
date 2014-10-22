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
package org.jclouds.googlecomputeengine.features;

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;

import java.net.URI;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.GoogleComputeEngineFallbacks.EmptyListPageOnNotFoundOr404;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.functions.internal.ParseFirewalls;
import org.jclouds.googlecomputeengine.handlers.FirewallBinder;
import org.jclouds.googlecomputeengine.options.FirewallOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticationFilter;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * Provides access to Firewalls via their REST API.
 * <p/>
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/v1/firewalls"/>
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticationFilter.class)
public interface FirewallApi {
   /**
    * Returns the specified image resource.
    *
    * @param firewallName name of the firewall resource to return.
    * @return an Firewall resource
    */
   @Named("Firewalls:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/firewalls/{firewall}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Firewall get(@PathParam("firewall") String firewallName);

   /**
    * Creates a firewall resource in the specified project using the data included in the request.
    *
    * @param name            the name of the firewall to be inserted.
    * @param network         the network to which to add the firewall
    * @param firewallOptions the options of the firewall to add
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Firewalls:insert")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/firewalls")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(FirewallBinder.class)
   Operation createInNetwork(@PayloadParam("name") String name,
                             @PayloadParam("network") URI network,
                             @PayloadParam("options") FirewallOptions firewallOptions);

   /**
    * Updates the specified firewall resource with the data included in the request.
    *
    * @param firewallName    the name firewall to be updated.
    * @param firewallOptions the new firewall.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Firewalls:update")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/firewalls/{firewall}")
   @OAuthScopes({COMPUTE_SCOPE})
   Operation update(@PathParam("firewall") String firewallName,
                    @BinderParam(BindToJsonPayload.class) FirewallOptions firewallOptions);

   /**
    * Updates the specified firewall resource, with patch semantics, with the data included in the request.
    *
    * @param firewallName    the name firewall to be updated.
    * @param firewallOptions the new firewall.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("Firewalls:patch")
   @PATCH
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/firewalls/{firewall}")
   @OAuthScopes({COMPUTE_SCOPE})
   Operation patch(@PathParam("firewall") String firewallName,
                   @BinderParam(BindToJsonPayload.class) FirewallOptions firewallOptions);

   /**
    * Deletes the specified image resource.
    *
    * @param firewallName name of the firewall resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the image did not exist the result is null.
    */
   @Named("Firewalls:delete")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/firewalls/{firewall}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   Operation delete(@PathParam("firewall") String firewallName);

   /**
    * List all firewalls.
    */
   @Named("Firewalls:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/firewalls")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseFirewalls.ToPage.class)
   @Transform(ParseFirewalls.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Firewall> list();

   @Named("Firewalls:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/global/firewalls")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseFirewalls.class)
   @Fallback(EmptyListPageOnNotFoundOr404.class)
   ListPage<Firewall> list(ListOptions options);
}
