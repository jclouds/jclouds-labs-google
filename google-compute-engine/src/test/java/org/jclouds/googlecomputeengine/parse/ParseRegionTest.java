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
package org.jclouds.googlecomputeengine.parse;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecomputeengine.domain.Region;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Andrew Bayer
 */
@Test(groups = "unit")
public class ParseRegionTest extends BaseGoogleComputeEngineParseTest<Region> {

   @Override
   public String resource() {
      return "/region_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Region expected() {
      return Region.builder()
              .id("12912210600542709766")
              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2013-07-08T14:40:37.939-07:00"))
              .selfLink(URI.create("https://www.googleapis.com/compute/v1beta15/projects/myproject/regions/us-central1"))
              .name("us-central1")
              .description("us-central1")
              .status(Region.Status.UP)
              .zones(ImmutableSet.of(URI.create("https://www.googleapis.com/compute/v1beta15/zones/us-central1-a"),
                      URI.create("https://www.googleapis.com/compute/v1beta15/zones/us-central1-b")))
              .build();
   }
}
