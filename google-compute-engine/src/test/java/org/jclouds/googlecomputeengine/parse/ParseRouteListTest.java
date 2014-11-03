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

import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Route;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ParseRouteListTest")
public class ParseRouteListTest extends BaseGoogleComputeEngineParseTest<ListPage<Route>> {

   @Override
   public String resource() {
      return "/route_list.json";
   }

   @Override @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<Route> expected() {
      Route route1 = new ParseRouteTest().expected();
      Route route2 = Route.create( //
            "507025480040058551", // id
            URI.create(BASE_URL + "/myproject/global/routes/default-route-fc92a41ecb5a8d17"), // selfLink
            "default-route-fc92a41ecb5a8d17", // name
            "Default route to the Internet.", // description
            URI.create(BASE_URL + "/myproject/global/networks/default"), // network
            null, // tags
            "0.0.0.0/0", // destRange
            1000, // priority
            null, // nextHopInstance
            null, // nextHopIp
            null, // nextHopNetwork
            URI.create(BASE_URL + "/myproject/global/gateways/default-internet-gateway"), // nextHopGateway
            null // warnings
      );
      return ListPage.create( //
            ImmutableList.of(route1, route2), // items
            null, // nextPageToken
            null // prefixes
      );
   }
}
