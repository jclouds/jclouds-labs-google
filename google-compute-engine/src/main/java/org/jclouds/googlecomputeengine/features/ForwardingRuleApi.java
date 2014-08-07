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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.ForwardingRule;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.functions.internal.ParseForwardingRules;
import org.jclouds.googlecomputeengine.handlers.PayloadBinder;
import org.jclouds.googlecomputeengine.options.ForwardingRuleOptions;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.binders.BindToJsonPayload;

// TODO: merge with regional forwarding rules when they are added to the project
/**
 * Provides access to forwarding rules via their REST API.
 * <p/>
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/latest/globalForwardingRules"/>
 * @see <a href="https://developers.google.com/compute/docs/reference/latest/forwardingRules"/>
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticator.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface ForwardingRuleApi {
   
   /**
    * Returns the specified forwarding rule resource.
    *
    * @param forwardingRuleName name of the global forwarding rule resource to return.
    * @return a ForwardingRule resource.
    */
   @Named("ForwardingRules:get")
   @GET
   @Path("/global/forwardingRules/{forwardingRule}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ForwardingRule get(@PathParam("forwardingRule") String forwardingRuleName);
   
   /**
    * Creates a forwarding rule resource using the given options.
    *
    * @param name            the name of the global forwarding rule to be inserted.
    * @param options         the options this forwarding rule will have.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("ForwardingRules:insert")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/forwardingRules")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(PayloadBinder.class)
   Operation create(@PayloadParam("name") String name, @PayloadParam("options") ForwardingRuleOptions options);
   
   /**
    * Updates a global forwarding rule resource in the specified project with the given target.
    *
    * @param forwardingRuleName  the name of the forwarding rule to be updated.
    * @param target              the url of the TargetHttpProxy this global forwarding rule points to.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   @Named("ForwardingRules:setTarget")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/global/forwardingRules/{forwardingRule}/setTarget")
   @OAuthScopes({COMPUTE_SCOPE})
   @MapBinder(BindToJsonPayload.class)
   Operation setTarget(@PathParam("forwardingRule") String forwardingRuleName,
                       @PayloadParam("target") URI target);
   
   /**
    * Deletes the specified global forwarding rule resource.
    *
    * @param forwardingRuleName   name of the forwarding rule resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the image did not exist the result is null.
    */
   @Named("ForwardingRules:delete")
   @DELETE
   @Path("/global/forwardingRules/{forwardingRule}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   Operation delete(@PathParam("forwardingRule") String forwardingRuleName);
   
   /**
    * @see ForwardingRuleApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ForwardingRules:list")
   @GET
   @Path("/global/forwardingRules")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseForwardingRules.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<ForwardingRule> listFirstPage();

   /**
    * @see ForwardingRuleApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ForwardingRules:list")
   @GET
   @Path("/global/forwardingRules")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseForwardingRules.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<ForwardingRule> listAtMarker(@QueryParam("pageToken") @Nullable String marker);

   /**
    * Retrieves the list of persistent forwarding rule resources contained within the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param marker      marks the beginning of the next list page.
    * @param listOptions listing options.
    * @return a page of the list.
    * @see ListOptions
    * @see org.jclouds.googlecomputeengine.domain.ListPage
    */
   @Named("ForwardingRules:list")
   @GET
   @Path("/global/forwardingRules")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseForwardingRules.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   ListPage<ForwardingRule> listAtMarker(@QueryParam("pageToken") @Nullable String marker,
                                         ListOptions options);

   /**
    * @see ForwardingRuleApi#list(org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ForwardingRules:list")
   @GET
   @Path("/global/forwardingRules")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseForwardingRules.class)
   @Transform(ParseForwardingRules.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<ForwardingRule> list();

   /**
    * A paged version of ForwardingRuleApi#list().
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required.
    * @see PagedIterable
    * @see ForwardingRuleApi#listAtMarker(String, org.jclouds.googlecomputeengine.options.ListOptions)
    */
   @Named("ForwardingRules:list")
   @GET
   @Path("/global/forwardingRules")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @ResponseParser(ParseForwardingRules.class)
   @Transform(ParseForwardingRules.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<ForwardingRule> list(ListOptions options);
}