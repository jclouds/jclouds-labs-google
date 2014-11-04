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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Iterator;

import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiLiveTest;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "GlobalOperationApiLiveTest")
public class GlobalOperationApiLiveTest extends BaseGoogleComputeEngineApiLiveTest {

   private Operation operation;

   private GlobalOperationApi api() {
      return api.getGlobalOperationApi(userProject.get());
   }

   public void testListOperationsWithFiltersAndPagination() {
      Iterator<ListPage<Operation>> operations = api().list(new ListOptions.Builder()
            //              .filter("operationType eq insert")
            .maxResults(1));

      // make sure that in spite of having only one result per page we get at least two results
      int count = 0;
      for (; count < 2 && operations.hasNext(); ) {
         ListPage<Operation> result = operations.next();
         if (result.isEmpty()) {
            operation = result.get(0);
            count++;
         }
      }
      if (count < 2) {
         throw new SkipException("Not enough global operations");
      }
      assertEquals(count, 2);
   }

   @Test(groups = "live", dependsOnMethods = "testListOperationsWithFiltersAndPagination")
   public void testGetOperation() {
      Operation result = api().get(operation.name());
      assertNotNull(result);
      assertEquals(result.name(), operation.name()); // Checking state besides name can lead to flaky test.
   }
}
