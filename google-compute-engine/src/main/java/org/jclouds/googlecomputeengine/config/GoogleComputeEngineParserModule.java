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
package org.jclouds.googlecomputeengine.config;

import static org.jclouds.googlecomputeengine.domain.Firewall.Rule;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.options.FirewallOptions;
import org.jclouds.googlecomputeengine.options.RouteOptions;
import org.jclouds.json.config.GsonModule;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public final class GoogleComputeEngineParserModule extends AbstractModule {

   @Override protected void configure() {
      bind(GsonModule.DateAdapter.class).to(GsonModule.Iso8601DateAdapter.class);
   }

   @Provides @Singleton Map<Type, Object> typeAdapters() {
      return new ImmutableMap.Builder<Type, Object>()
            .put(FirewallOptions.class, new FirewallOptionsTypeAdapter())
            .put(RouteOptions.class, new RouteOptionsTypeAdapter()).build();
   }

   // TODO: change jclouds core to use collaborative set bindings
   @Provides @Singleton Set<TypeAdapterFactory> typeAdapterFactories() {
      return ImmutableSet.<TypeAdapterFactory>of(new ListPageAdapterFactory());
   }

   private static final class FirewallOptionsTypeAdapter implements JsonSerializer<FirewallOptions> {

      @Override public JsonElement serialize(FirewallOptions src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject firewall = new JsonObject();
         if (src.name() != null) {
            firewall.addProperty("name", src.name());
         }
         if (src.network() != null) {
            firewall.addProperty("network", src.network().toString());
         }
         if (!src.sourceRanges().isEmpty()) {
            firewall.add("sourceRanges", buildArrayOfStrings(src.sourceRanges()));
         }
         if (!src.sourceTags().isEmpty()) {
            firewall.add("sourceTags", buildArrayOfStrings(src.sourceTags()));
         }
         if (!src.targetTags().isEmpty()) {
            firewall.add("targetTags", buildArrayOfStrings(src.targetTags()));
         }
         if (!src.getAllowed().isEmpty()) {
            JsonArray rules = new JsonArray();
            for (Rule rule : src.getAllowed()) {
               rules.add(context.serialize(rule, Firewall.Rule.class));
            }
            firewall.add("allowed", rules);
         }
         return firewall;
      }
   }

   private static final class RouteOptionsTypeAdapter implements JsonSerializer<RouteOptions> {

      @Override public JsonElement serialize(RouteOptions src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject route = new JsonObject();
         if (src.name() != null) {
            route.addProperty("name", src.name());
         }
         if (src.getNetwork() != null) {
            route.addProperty("network", src.getNetwork().toString());
         }
         if (src.getNextHopGateway() != null) {
            route.addProperty("nextHopGateway", src.getNextHopGateway().toString());
         }
         if (src.getNextHopInstance() != null) {
            route.addProperty("nextHopInstance", src.getNextHopInstance().toString());
         }
         if (src.getNextHopNetwork() != null) {
            route.addProperty("nextHopNetwork", src.getNextHopNetwork().toString());
         }
         if (src.getDestRange() != null) {
            route.addProperty("destRange", src.getDestRange());
         }
         if (src.getDescription() != null) {
            route.addProperty("description", src.getDescription());
         }
         if (src.getPriority() != null) {
            route.addProperty("priority", src.getPriority());
         }
         if (src.getNextHopIp() != null) {
            route.addProperty("nextHopIp", src.getNextHopIp());
         }
         if (!src.getTags().isEmpty()) {
            route.add("tags", buildArrayOfStrings(src.getTags()));
         }
         return route;
      }
   }

   private static JsonArray buildArrayOfStrings(Iterable<String> strings) {
      JsonArray array = new JsonArray();
      for (String string : strings) {
         array.add(new JsonPrimitive(string));
      }
      return array;
   }

   static final class ListPageAdapterFactory implements TypeAdapterFactory {
      static final class ListPageAdapter extends TypeAdapter<ListPage<?>> {
         private final TypeAdapter<?> itemAdapter;

         ListPageAdapter(TypeAdapter<?> itemAdapter) {
            this.itemAdapter = itemAdapter;
            nullSafe();
         }

         public void write(JsonWriter out, ListPage<?> value) throws IOException {
            throw new UnsupportedOperationException("We only read ListPages!");
         }

         public ListPage<?> read(JsonReader in) throws IOException {
            ImmutableList.Builder<Object> items = ImmutableList.builder();
            String nextPageToken = null;
            in.beginObject();
            while (in.hasNext()) {
               String name = in.nextName();
               if (name.equals("items")) {
                  if (in.peek() == JsonToken.BEGIN_ARRAY) {
                     readItems(in, items);
                  } else { // aggregated
                     readAggregate(in, items);
                  }
               } else if (name.equals("nextPageToken")) {
                  nextPageToken = in.nextString();
               } else {
                  in.skipValue();
               }
            }
            in.endObject();
            return ListPage.create(items.build(), nextPageToken);
         }

         private void readItems(JsonReader in, ImmutableList.Builder<Object> items) throws IOException {
            in.beginArray();
            while (in.hasNext()) {
               Object item = itemAdapter.read(in);
               if (item != null) {
                  items.add(item);
               }
            }
            in.endArray();
         }

         private void readAggregate(JsonReader in, ImmutableList.Builder<Object> items) throws IOException {
            in.beginObject(); // enter zone name -> type -> items map
            while (in.hasNext()) {
               String scope = in.nextName(); // skip zone name
               in.beginObject(); // enter zone map
               while (in.hasNext()) {
                  String resourceTypeOrWarning = in.nextName();
                  if (!resourceTypeOrWarning.equals("warning")) {
                     readItems(in, items);
                  } else {
                     in.skipValue();
                  }
               }
               in.endObject(); // end zone map
            }
            in.endObject(); // end item wrapper
         }
      }

      @SuppressWarnings("unchecked") public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> ownerType) {
         Type type = ownerType.getType();
         if (ownerType.getRawType() != ListPage.class || !(type instanceof ParameterizedType))
            return null;
         Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
         TypeAdapter<?> itemAdapter = gson.getAdapter(TypeToken.get(elementType));
         return (TypeAdapter<T>) new ListPageAdapter(itemAdapter);
      }
   }
}
