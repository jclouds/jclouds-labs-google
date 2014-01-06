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
import org.jclouds.googlecomputeengine.domain.HttpHealthCheck;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Andrea Turli
 */
public class HttpHealthCheckApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private static final String HTTP_HEALTH_CHECK_NAME = "http-health-check-api-live-test";
   private static final int TIME_WAIT = 60;

   private HttpHealthCheckApi api() {
      return api.getHttpHealthCheckApiForProject(userProject.get());
   }

   @Test(groups = "live")
   public void testInsertHttpHealthCheck() {
      assertGlobalOperationDoneSucessfully(api().create(HTTP_HEALTH_CHECK_NAME), TIME_WAIT);
   }

   @Test(groups = "live", dependsOnMethods = "testInsertHttpHealthCheck")
   public void testGetHttpHealthCheck() {
      HttpHealthCheck httpHealthCheck = api().get(HTTP_HEALTH_CHECK_NAME);
      assertNotNull(httpHealthCheck);
      assertEquals(httpHealthCheck.getName(), HTTP_HEALTH_CHECK_NAME);
   }

   @Test(groups = "live", dependsOnMethods = "testGetHttpHealthCheck")
   public void testListHttpHealthCheck() {
      PagedIterable<HttpHealthCheck> httpHealthCheck = api().list(new ListOptions.Builder()
              .filter("name eq " + HTTP_HEALTH_CHECK_NAME));
      List<HttpHealthCheck> httpHealthChecksAsList = Lists.newArrayList(httpHealthCheck.concat());
      assertEquals(httpHealthChecksAsList.size(), 1);
   }

   @Test(groups = "live", dependsOnMethods = "testListHttpHealthCheck")
   public void testDeleteHttpHealthCheck() {
      assertGlobalOperationDoneSucessfully(api().delete(HTTP_HEALTH_CHECK_NAME), TIME_WAIT);
   }
}
