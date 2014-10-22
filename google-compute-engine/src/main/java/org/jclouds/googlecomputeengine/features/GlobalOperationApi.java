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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.GoogleComputeEngineFallbacks.EmptyListPageOnNotFoundOr404;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.functions.internal.ParseGlobalOperations;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticationFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;

/**
 * Provides access to Global Operations via their REST API.
 *
 * @see <a href="https://developers.google.com/compute/docs/reference/v1/globalOperations"/>
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticationFilter.class)
public interface GlobalOperationApi {

   /**
    * Retrieves the specified operation resource.
    *
    * @param operationName name of the operation resource to return.
    * @return If successful, this method returns an Operation resource
    */
   @Named("GlobalOperations:get")
   @GET
   @Path("/global/operations/{operation}")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   Operation get(@PathParam("operation") String operationName);

   /**
    * Deletes the specified operation resource.
    *
    * @param operationName name of the operation resource to delete.
    */
   @Named("GlobalOperations:delete")
   @DELETE
   @Path("/global/operations/{operation}")
   @OAuthScopes(COMPUTE_SCOPE)
   @Fallback(NullOnNotFoundOr404.class)
   void delete(@PathParam("operation") String operationName);

   /**
    * List all global operations.
    */
   @Named("GlobalOperations:list")
   @GET
   @Path("/global/operations")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseGlobalOperations.ToPage.class)
   @Transform(ParseGlobalOperations.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Operation> list();

   @Named("GlobalOperations:list")
   @GET
   @Path("/global/operations")
   @OAuthScopes(COMPUTE_READONLY_SCOPE)
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseGlobalOperations.class)
   @Fallback(EmptyListPageOnNotFoundOr404.class)
   ListPage<Operation> list(ListOptions listOptions);

}
