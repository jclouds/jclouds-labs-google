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
package org.jclouds.googlecomputeengine.compute;

import static com.google.common.base.Objects.firstNonNull;
import static org.jclouds.compute.domain.OsFamily.COREOS;
import static org.jclouds.compute.domain.OsFamily.DEBIAN;
import static org.jclouds.compute.domain.OsFamily.WINDOWS;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseTemplateBuilderLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@Test(groups = "live", testName = "GoogleComputeEngineTemplateBuilderLiveTest")
public class GoogleComputeEngineTemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {

   public GoogleComputeEngineTemplateBuilderLiveTest() {
      provider = "google-compute-engine";
   }
   
   @Test
   @Override
   public void testDefaultTemplateBuilder() throws IOException {
      Template defaultTemplate = view.getComputeService().templateBuilder().build();
      assertTrue(defaultTemplate.getImage().getOperatingSystem().getVersion().equals("7.wheezy"));
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), DEBIAN);
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }
   
   @Test
   public void testDefaultCredentials() {
      Map<OsFamily, String> defaultUsernames = ImmutableMap.of(COREOS, "core", WINDOWS, "Administrator");
      Set<? extends Image> images = view.getComputeService().listImages();
      for (Image image : images) {
         assertEquals(image.getDefaultCredentials().getUser(),
               firstNonNull(defaultUsernames.get(image.getOperatingSystem().getFamily()), "jclouds"));
      }
   }
   
   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.<String> of();
   }

}
