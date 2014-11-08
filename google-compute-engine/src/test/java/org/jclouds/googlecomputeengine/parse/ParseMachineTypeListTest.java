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

import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineParseTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

@Test(groups = "unit", testName = "ParseMachineTypeListTest")
public class ParseMachineTypeListTest extends BaseGoogleComputeEngineParseTest<ListPage<MachineType>> {

   @Override
   public String resource() {
      return "/machinetype_list.json";
   }

   @Override @Consumes(APPLICATION_JSON)
   public ListPage<MachineType> expected() {
      MachineType machineType1 = MachineType.create( //
            "4618642685664990776", // id
            URI.create(BASE_URL + "/party/zones/us-central1-a/machineTypes/f1-micro"), // selfLink
            "f1-micro", // name
            "1 vCPU (shared physical core) and 0.6 GB RAM", // description
            1, // guestCpus
            614, // memoryMb
            null, // scratchDisks
            4, // maximumPersistentDisks
            3072, // maximumPersistentDisksSizeGb
            "us-central1-a", // zone
            null // deprecated
      );
      MachineType machineType2 = MachineType.create( //
            "12907738072351752276", // id
            URI.create(BASE_URL + "/party/zones/us-central1-a/machineTypes/n1-standard-1"), // selfLink
            "n1-standard-1", // name
            "1 vCPU, 3.75 GB RAM, and a 10 GB ephemeral root disk", // description
            1, // guestCpus
            3840, // memoryMb
            null, // scratchDisks
            16, // maximumPersistentDisks
            128, // maximumPersistentDisksSizeGb
            "us-central1-a", // zone
            null // deprecated
      );
      MachineType machineType3 = MachineType.create( //
            "12908560709887590691", // id
            URI.create(BASE_URL + "/party/zones/us-central1-a/machineTypes/n1-standard-8-d"), // selfLink
            "n1-standard-8-d", // name
            "8 vCPUs, 30 GB RAM, a 10 GB ephemeral root disk, and 2 extra 1770 GB ephemeral disks", // description
            8, // guestCpus
            30720, // memoryMb
            ImmutableList.of(MachineType.ScratchDisk.create(1770), MachineType.ScratchDisk.create(1770)), // scratchDisks
            16, // maximumPersistentDisks
            1024, // maximumPersistentDisksSizeGb
            "us-central1-a", // zone
            null // deprecated
      );
      return ListPage.create( //
            ImmutableList.of(machineType1, machineType2, machineType3), // items
            null // nextPageToken
      );
   }
}
