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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;

import javax.ws.rs.Consumes;

import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ParseFirewallListTest")
public class ParseFirewallListTest extends BaseGoogleComputeEngineParseTest<ListPage<Firewall>> {

   @Override
   public String resource() {
      return "/firewall_list.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public ListPage<Firewall> expected() {
      Firewall firewall1 = new ParseFirewallTest().expected();
      Firewall firewall2 = Firewall.create( //
            "12862241067393040785", // id
            URI.create(BASE_URL + "/google/global/firewalls/default-ssh"), // selfLink
            "default-ssh", // name
            "SSH allowed from anywhere", // description
            URI.create(BASE_URL + "/google/global/networks/default"), // network
            ImmutableList.of("0.0.0.0/0"), // sourceRanges
            null, // sourceTags
            null, // targetTags
            ImmutableList.of(Firewall.Rule.create("tcp", ImmutableList.of("22"))) // allowed
      );
      return ListPage.create( //
            ImmutableList.of(firewall1, firewall2), // items
            null, // nextPageToken
            null // prefixes
      );
   }
}
