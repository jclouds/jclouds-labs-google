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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.DiskType;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.functions.internal.ParseDiskTypes;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticationFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Transform;

/**
 * Provides access to DiskTypes via their REST API.
 *
 * @see <a href="https://cloud.google.com/compute/docs/reference/v1/diskTypes"/>
 */
@SkipEncoding({'/', '='})
@RequestFilters(OAuthAuthenticationFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface DiskTypeApi {

      /**
       * Returns the specified disk type resource.
       *
       * @param zone            the name of the zone the disk type is in
       * @param diskType       name of the disk type resource to return.
       * @return If successful, this method returns a DiskType resource
       */
      @Named("DiskTypes:get")
      @GET
      @Path("/zones/{zone}/diskTypes/{diskType}")
      @OAuthScopes(COMPUTE_READONLY_SCOPE)
      @Fallback(NullOnNotFoundOr404.class)
      DiskType getInZone(@PathParam("zone") String zone, @PathParam("diskType") String diskTypeName);

      /**
       * List all disk types int he given zone.
       */
      @Named("DiskTypes:list")
      @GET
      @Path("/zones/{zone}/diskTypes")
      @OAuthScopes(COMPUTE_READONLY_SCOPE)
      @ResponseParser(ParseDiskTypes.ToPage.class)
      @Transform(ParseDiskTypes.ToPagedIterable.class)
      @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
      PagedIterable<DiskType> listInZone(@PathParam("zone") String zone);

      @Named("DiskTypes:list")
      @GET
      @Path("/zones/{zone}/diskTypes")
      @OAuthScopes(COMPUTE_READONLY_SCOPE)
      @ResponseParser(ParseDiskTypes.class)
      @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
      ListPage<DiskType> listInZone(@PathParam("zone") String zone, ListOptions listOptions);

}
