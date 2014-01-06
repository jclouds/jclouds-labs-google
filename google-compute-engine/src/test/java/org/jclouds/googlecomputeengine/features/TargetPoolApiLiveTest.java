/*
 * Licensed to jclouds, Inc. (jclouds) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership.
 * jclouds licenses this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may obtain a copy of the Licens at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.jclouds.googlecomputeengine.features;

import com.google.common.collect.Lists;
import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.TargetPool;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Andrea Turli
 */
public class TargetPoolApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String TARGETPOOL_NAME = "targetpool-api-live-test";
   private static final int TIME_WAIT = 30;

   private TargetPoolApi api() {
      return api.getTargetPoolApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testInsertTargetPool() {
      assertRegionOperationDoneSucessfully(api().createInRegion(DEFAULT_REGION_NAME, TARGETPOOL_NAME), TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertTargetPool")
   public void testGetTargetPool() {
      TargetPool targetPool = api().getInRegion(DEFAULT_REGION_NAME, TARGETPOOL_NAME);
      assertNotNull(targetPool);
      assertEquals(targetPool.getName(), TARGETPOOL_NAME);
   }

   @Test(groups = "live", dependsOnMethods = "testGetTargetPool")
   public void testListTargetPool() {

      PagedIterable<TargetPool> targetPool = api().listInRegion(DEFAULT_REGION_NAME, new ListOptions.Builder()
              .filter("name eq " + TARGETPOOL_NAME));
      List<TargetPool> targetPoolsAsList = Lists.newArrayList(targetPool.concat());
      assertEquals(targetPoolsAsList.size(), 1);
   }

   @Test(groups = "live", dependsOnMethods = "testListTargetPool")
   public void testDeleteTargetPool() {
      assertRegionOperationDoneSucessfully(api().deleteInRegion(DEFAULT_REGION_NAME, TARGETPOOL_NAME), TIME_WAIT);
   }
}
