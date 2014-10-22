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

import com.google.common.collect.ImmutableSet;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Resource;
import org.jclouds.googlecomputeengine.domain.TargetPool;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.net.URI;

@Test(groups = "unit")
public class ParseTargetPoolListTest extends BaseGoogleComputeEngineParseTest<ListPage<TargetPool>> {

   @Override
   public String resource() {
      return "/targetpool_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<TargetPool> expected() {
      return ListPage.<TargetPool>builder()
              .kind(Resource.Kind.TARGET_POOL_LIST)
              .items(ImmutableSet.of(TargetPool.builder()
                      .id("5199309593612841404")
                      .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2014-01-07T05:25:27.783-08:00"))
                      .selfLink(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1/targetPools/test-targetpool"))
                      .name("test-targetpool")
                      .region(URI.create("https://www.googleapis.com/compute/v1/projects/myproject/regions/us-central1"))
                      .sessionAffinity("NONE")
                      .build())
              ).build();
   }
}
