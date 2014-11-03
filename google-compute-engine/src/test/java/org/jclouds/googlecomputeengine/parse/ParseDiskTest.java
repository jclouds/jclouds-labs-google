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

import org.jclouds.googlecomputeengine.domain.Disk;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ParseDiskTest")
public class ParseDiskTest extends BaseGoogleComputeEngineParseTest<Disk> {

   @Override
   public String resource() {
      return "/disk_get.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public Disk expected() {
      return Disk.create( //
            "13050421646334304115", // id
            URI.create(BASE_URL + "/myproject/zones/us-central1-a"), // zone
            "READY", // status
            "testimage1", // name
            null, // description
            1, // sizeGb
            URI.create(BASE_URL + "/myproject/zones/us-central1-a/disks/testimage1"), // selfLink
            URI.create(BASE_URL + "/studied-point-720/zones/us-central1-a/diskTypes/pd-standard") // type
      );
   }
}
