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

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.PageWithMarker;
import org.jclouds.googlecomputeengine.options.ListOptions;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.Json;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

public class ParseFirewalls extends BasePageParser<Firewall, ParseFirewalls> {

   @Inject
   ParseFirewalls(ToPage toPage, ToPagedIterable toPagedIterable) {
      super(toPage, toPagedIterable);
   }

   public static class ToPage extends ParseJson<PageWithMarker<Firewall>> {
      @Inject
      ToPage(Json json, TypeLiteral<PageWithMarker<Firewall>> type) {
         super(json, new TypeLiteral<PageWithMarker<Firewall>>() {
         });
      }
   }

   public static class ToPagedIterable extends BaseToPagedIterable<Firewall, ToPagedIterable> {

      private final GoogleComputeEngineApi api;

      @Inject
      ToPagedIterable(GoogleComputeEngineApi api) {
         this.api = checkNotNull(api, "api");
      }

      @Override
      protected Function<Object, IterableWithMarker<Firewall>> fetchNextPage(final String projectName,
            final ListOptions options) {
         return new Function<Object, IterableWithMarker<Firewall>>() {

            @Override
            public IterableWithMarker<Firewall> apply(Object input) {
               options.pageToken(input.toString());
               return api.getFirewallApiForProject(projectName).list(options);
            }
         };
      }
   }
}
