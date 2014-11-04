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
package org.jclouds.googlecomputeengine.functions.internal;

import javax.inject.Inject;

import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.MachineType;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

public final class ParseMachineTypes extends ParseJson<ListPage<MachineType>> {

   @Inject ParseMachineTypes(Json json) {
      super(json, new TypeLiteral<ListPage<MachineType>>() {
      });
   }

   public static class ToIteratorOfListPage
         extends BaseWithZoneToIteratorOfListPage<MachineType, ToIteratorOfListPage> {

      private final GoogleComputeEngineApi api;

      @Inject ToIteratorOfListPage(GoogleComputeEngineApi api) {
         this.api = api;
      }

      @Override protected Function<String, ListPage<MachineType>> fetchNextPage(final String projectName,
            final String zoneName, final ListOptions options) {
         return new Function<String, ListPage<MachineType>>() {

            @Override public ListPage<MachineType> apply(String input) {
               return api.getMachineTypeApi(projectName).listAtMarkerInZone(zoneName, input, options);
            }
         };
      }
   }
}
