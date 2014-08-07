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

import java.beans.ConstructorProperties;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.googlecomputeengine.domain.BackendService;
import org.jclouds.googlecomputeengine.domain.BackendService.Backend;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Firewall.Rule;
import org.jclouds.googlecomputeengine.domain.Instance;
import org.jclouds.googlecomputeengine.domain.InstanceTemplate;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Metadata;
import org.jclouds.googlecomputeengine.domain.Operation;
import org.jclouds.googlecomputeengine.domain.Project;
import org.jclouds.googlecomputeengine.domain.Quota;
import org.jclouds.googlecomputeengine.domain.Resource.Kind;
import org.jclouds.googlecomputeengine.domain.ResourceView;
import org.jclouds.googlecomputeengine.domain.UrlMap;
import org.jclouds.googlecomputeengine.domain.UrlMap.HostRule;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathMatcher;
import org.jclouds.googlecomputeengine.domain.UrlMap.PathRule;
import org.jclouds.googlecomputeengine.domain.UrlMap.UrlMapTest;
import org.jclouds.googlecomputeengine.domain.UrlMapValidateResult;
import org.jclouds.googlecomputeengine.domain.UrlMapValidateResult.TestFailure;
import org.jclouds.googlecomputeengine.options.BackendServiceOptions;
import org.jclouds.googlecomputeengine.options.FirewallOptions;
import org.jclouds.googlecomputeengine.options.ResourceViewOptions;
import org.jclouds.googlecomputeengine.options.RouteOptions;
import org.jclouds.googlecomputeengine.options.UrlMapOptions;
import org.jclouds.json.config.GsonModule;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.oauth.v2.domain.ClaimSet;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.json.ClaimSetTypeAdapter;
import org.jclouds.oauth.v2.json.HeaderTypeAdapter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public class GoogleComputeEngineParserModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(GsonModule.DateAdapter.class).to(GsonModule.Iso8601DateAdapter.class);
   }

   @Provides
   @Singleton
   public Map<Type, Object> provideCustomAdapterBindings() {
      return new ImmutableMap.Builder<Type, Object>()
              .put(Metadata.class, new MetadataTypeAdapter())
              .put(Operation.class, new OperationTypeAdapter())
              .put(Header.class, new HeaderTypeAdapter())
              .put(ClaimSet.class, new ClaimSetTypeAdapter())
              .put(Project.class, new ProjectTypeAdapter())
              .put(Instance.class, new InstanceTypeAdapter())
              .put(InstanceTemplate.class, new InstanceTemplateTypeAdapter())
              .put(FirewallOptions.class, new FirewallOptionsTypeAdapter())
              .put(RouteOptions.class, new RouteOptionsTypeAdapter())
              .put(Rule.class, new RuleTypeAdapter())
              .put(BackendServiceOptions.class, new BackendServiceOptionsTypeAdapter())
              .put(BackendService.Backend.class, new BackendTypeAdapter())
              .put(UrlMapOptions.class, new UrlMapOptionsTypeAdapter())
              .put(UrlMap.HostRule.class, new HostRuleTypeAdapter())
              .put(UrlMap.PathMatcher.class, new PathMatcherTypeAdapter())
              .put(UrlMap.UrlMapTest.class, new TestTypeAdapter())
              .put(UrlMap.PathRule.class, new PathRuleTypeAdapter())
              .put(UrlMapValidateResult.class, new UrlMapValidateResultTypeAdapter())
              .put(ResourceViewOptions.class, new ResourceViewOptionsTypeAdapter())
              .put(ResourceView.class, new ResourceViewTypeAdapter())
              .put(new TypeLiteral<ListPage<ResourceView>>() {}.getType(),
                   new ListPageResourceViewTypeAdapter())
              .put(new TypeLiteral<ListPage<URI>>() {}.getType(),
                   new ListPageResourceViewMemberTypeAdapter())
              .build();
   }

   /**
    * Parser for operations that unwraps errors avoiding an extra intermediate object.
    *
    * @see <a href="https://developers.google.com/compute/docs/reference/v1/operations"/>
    */
   @Singleton
   private static class OperationTypeAdapter implements JsonDeserializer<Operation> {

      @Override
      public Operation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
              JsonParseException {
         Operation.Builder operationBuilder = ((Operation) context.deserialize(json,
                 OperationInternal.class)).toBuilder();
         JsonObject error = json.getAsJsonObject().getAsJsonObject("error");
         if (error != null) {
            JsonArray array = error.getAsJsonArray("errors");
            if (array != null) {
               for (JsonElement element : array) {
                  operationBuilder.addError((Operation.Error) context.deserialize(element, Operation.Error.class));
               }
            }
         }
         return operationBuilder.build();
      }

      private static class OperationInternal extends Operation {
         @ConstructorProperties({
                 "id", "creationTimestamp", "selfLink", "name", "description", "targetLink", "targetId",
                 "clientOperationId", "status", "statusMessage", "user", "progress", "insertTime", "startTime",
                 "endTime", "httpErrorStatusCode", "httpErrorMessage", "operationType", "region", "zone"
         })
         private OperationInternal(String id, Date creationTimestamp, URI selfLink, String name,
                                   String description, URI targetLink, String targetId, String clientOperationId,
                                   Status status, String statusMessage, String user, int progress, Date insertTime,
                                   Date startTime, Date endTime, int httpErrorStatusCode, String httpErrorMessage,
                                   String operationType, URI region, URI zone) {
            super(id, creationTimestamp, selfLink, name, description, targetLink, targetId, clientOperationId,
                    status, statusMessage, user, progress, insertTime, startTime, endTime, httpErrorStatusCode,
                    httpErrorMessage, operationType, null, region, zone);
         }
      }
   }

   @Singleton
   private static class InstanceTemplateTypeAdapter implements JsonSerializer<InstanceTemplate> {

      @Override
      public JsonElement serialize(InstanceTemplate src, Type typeOfSrc, JsonSerializationContext context) {
         InstanceTemplateInternal template = new InstanceTemplateInternal(src);
         JsonObject instance = (JsonObject) context.serialize(template, InstanceTemplateInternal.class);

         // deal with network
         JsonArray networkInterfaces = new JsonArray();
         for (InstanceTemplate.NetworkInterface networkInterface : template.getNetworkInterfaces()){
            networkInterfaces.add(context.serialize(networkInterface, InstanceTemplate.NetworkInterface.class));
         }
         instance.add("networkInterfaces", networkInterfaces);

         // deal with persistent disks
         if (src.getDisks() != null && !src.getDisks().isEmpty()) {
            JsonArray disks = new JsonArray();
            for (InstanceTemplate.PersistentDisk persistentDisk : src.getDisks()) {
               JsonObject disk = (JsonObject) context.serialize(persistentDisk, InstanceTemplate.PersistentDisk.class);
               disk.addProperty("type", "PERSISTENT");
               disks.add(disk);
            }
            instance.add("disks", disks);
         }

         // deal with metadata
         if (src.getMetadata() != null && !src.getMetadata().isEmpty()) {
            Metadata metadata = Metadata.builder()
                    .items(src.getMetadata())
                    .build();
            JsonObject metadataJson = (JsonObject) context.serialize(metadata);
            instance.add("metadata", metadataJson);
            return instance;
         }

         return instance;
      }

      private static class InstanceTemplateInternal extends InstanceTemplate {
         private InstanceTemplateInternal(InstanceTemplate template) {
            super(template.getMachineType());
            name(template.getName());
            description(template.getDescription());
            image(template.getImage());
            serviceAccounts(template.getServiceAccounts());
            networkInterfaces(template.getNetworkInterfaces());
         }
      }
   }

   @Singleton
   private static class InstanceTypeAdapter implements JsonDeserializer<Instance> {

      @Override
      public Instance deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
              JsonParseException {
         Instance.Builder instanceBuilder = ((Instance) context.deserialize(json,
                 InstanceInternal.class)).toBuilder();
         JsonObject object = (JsonObject) json;
         if (object.get("disks") != null) {
            JsonArray disks = (JsonArray) object.get("disks");
            for (JsonElement element : disks) {
               JsonObject disk = (JsonObject) element;
               if (disk.get("type").getAsString().equals("PERSISTENT")) {
                  instanceBuilder.addDisk((Instance.PersistentAttachedDisk) context.deserialize(disk,
                          Instance.PersistentAttachedDisk.class));
               } else {
                  instanceBuilder.addDisk((Instance.AttachedDisk) context.deserialize(disk,
                          Instance.AttachedDisk.class));
               }
            }

         }

         return Instance.builder().fromInstance(instanceBuilder.build()).build();
      }


      private static class InstanceInternal extends Instance {
         @ConstructorProperties({
                 "id", "creationTimestamp", "selfLink", "name", "description", "tags", "machineType",
                 "status", "statusMessage", "zone", "networkInterfaces", "metadata", "serviceAccounts"
         })
         private InstanceInternal(String id, Date creationTimestamp, URI selfLink, String name, String description,
                                  Tags tags, URI machineType, Status status, String statusMessage,
                                  URI zone, Set<NetworkInterface> networkInterfaces, Metadata metadata,
                                  Set<ServiceAccount> serviceAccounts) {
            super(id, creationTimestamp, selfLink, name, description, tags, machineType,
                    status, statusMessage, zone, networkInterfaces, null, metadata, serviceAccounts);
         }
      }
   }

   /**
    * Parser for Metadata.
    */
   @Singleton
   private static class MetadataTypeAdapter implements JsonDeserializer<Metadata>, JsonSerializer<Metadata> {


      @Override
      public Metadata deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
              JsonParseException {
         ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
         JsonObject metadata = json.getAsJsonObject();
         JsonArray items = metadata.getAsJsonArray("items");
         if (items != null) {
            for (JsonElement element : items) {
               JsonObject object = element.getAsJsonObject();
               builder.put(object.get("key").getAsString(), object.get("value").getAsString());
            }
         }
         String fingerprint = null;
         if (metadata.getAsJsonPrimitive("fingerprint") != null) {
            fingerprint = metadata.getAsJsonPrimitive("fingerprint").getAsString();
         } else {
            fingerprint = "";
         }
         return new Metadata(fingerprint, builder.build());
      }

      @Override
      public JsonElement serialize(Metadata src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject metadataObject = new JsonObject();
         metadataObject.add("kind", new JsonPrimitive("compute#metadata"));
         JsonArray items = new JsonArray();
         for (Map.Entry<String, String> entry : src.getItems().entrySet()) {
            JsonObject object = new JsonObject();
            object.addProperty("key", entry.getKey());
            object.addProperty("value", entry.getValue());
            items.add(object);
         }
         metadataObject.add("items", items);
         if (src.getFingerprint() != null) {
            metadataObject.addProperty("fingerprint", src.getFingerprint());
         }
         return metadataObject;
      }
   }



   @Singleton
   private static class ProjectTypeAdapter implements JsonDeserializer<Project> {

      @Override
      public Project deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
              JsonParseException {
         return Project.builder().fromProject((Project) context.deserialize(json, ProjectInternal.class)).build();
      }

      private static class ProjectInternal extends Project {

         @ConstructorProperties({
                 "id", "creationTimestamp", "selfLink", "name", "description", "commonInstanceMetadata", "quotas",
                 "externalIpAddresses"
         })
         private ProjectInternal(String id, Date creationTimestamp, URI selfLink, String name, String description,
                                 Metadata commonInstanceMetadata, Set<Quota> quotas, Set<String> externalIpAddresses) {
            super(id, creationTimestamp, selfLink, name, description, commonInstanceMetadata, quotas,
                    externalIpAddresses);
         }

      }
   }

   @Singleton
   private static class FirewallOptionsTypeAdapter implements JsonSerializer<FirewallOptions> {

      @Override
      public JsonElement serialize(FirewallOptions src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject firewall = new JsonObject();
         if (src.getName() != null) {
            firewall.addProperty("name", src.getName());
         }
         if (src.getNetwork() != null) {
            firewall.addProperty("network", src.getNetwork().toString());
         }
         if (!src.getSourceRanges().isEmpty()) {
            firewall.add("sourceRanges", buildArrayOfStrings(src.getSourceRanges()));
         }
         if (!src.getSourceTags().isEmpty()) {
            firewall.add("sourceTags", buildArrayOfStrings(src.getSourceTags()));
         }
         if (!src.getTargetTags().isEmpty()) {
            firewall.add("targetTags", buildArrayOfStrings(src.getTargetTags()));
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

   @Singleton
   private static class RouteOptionsTypeAdapter implements JsonSerializer<RouteOptions> {

      @Override
      public JsonElement serialize(RouteOptions src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject route = new JsonObject();
         if (src.getName() != null) {
            route.addProperty("name", src.getName());
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

   private static class RuleTypeAdapter implements JsonDeserializer<Firewall.Rule>, JsonSerializer<Firewall.Rule> {

      @Override
      public Firewall.Rule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
              JsonParseException {
         JsonObject rule = json.getAsJsonObject();
         Rule.Builder builder = Rule.builder();
         builder.IpProtocol(IpProtocol.fromValue(rule.get("IPProtocol").getAsString()));
         if (rule.get("ports") != null) {
            JsonArray ports = (JsonArray) rule.get("ports");
            for (JsonElement port : ports) {
               String portAsString = port.getAsString();
               if (portAsString.contains("-")) {
                  String[] split = portAsString.split("-");
                  builder.addPortRange(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
               } else {
                  builder.addPort(Integer.parseInt(portAsString));
               }
            }
         }
         return builder.build();
      }

      @Override
      public JsonElement serialize(Firewall.Rule src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject ruleObject = new JsonObject();
         ruleObject.addProperty("IPProtocol", src.getIpProtocol().value());
         if (src.getPorts() != null && !src.getPorts().isEmpty()) {
            JsonArray ports = new JsonArray();
            for (Range<Integer> range : src.getPorts().asRanges()) {
               ports.add(new JsonPrimitive(range.lowerEndpoint() == range.upperEndpoint() ? range.lowerEndpoint() + "" :
                       range.lowerEndpoint() + "-" + range.upperEndpoint()));
            }
            ruleObject.add("ports", ports);
         }
         return ruleObject;
      }
   }

   /**
    * Turns a ResourceViewOptions object into json so that it can be sent to
    * Compute Engine in a web request.
    *
    */
   @Singleton
   private static class ResourceViewOptionsTypeAdapter implements JsonSerializer<ResourceViewOptions> {

      @Override
      public JsonElement serialize(ResourceViewOptions resourceViewOptions,
                                   Type typeOfSrc, JsonSerializationContext context) {
         JsonObject resourceView = new JsonObject();
         if (resourceViewOptions.getName() != null) {
            resourceView.addProperty("name", resourceViewOptions.getName());
         }
         if (resourceViewOptions.getDescription() != null) {
            resourceView.addProperty("description", resourceViewOptions.getDescription());
         }
         if (!resourceViewOptions.getMembers().isEmpty()) {
            resourceView.add("members", buildArrayOfStringsFromURI(resourceViewOptions.getMembers()));
         }
         return resourceView;
      }
   }
   
   @Singleton
   private static class ListPageResourceViewTypeAdapter implements
   JsonDeserializer<ListPage<ResourceView>> {

      @Override
      public ListPage<ResourceView> deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
         JsonObject listPageObject = json.getAsJsonObject();
         // ListPage is a subclass of resource so it requires an id and a selfLink.
         // Currently the list function for resource views does not return either
         // of these so set them to "" so no exceptions are thrown.
         ListPage.Builder<ResourceView> builder = ListPage.builder();
         builder.id("")
                .selfLink(URI.create(""))
                .kind(Kind.RESOURCE_VIEW_LIST);
         if (listPageObject.has("nextPageToken")) {
            builder.nextPageToken(listPageObject.get("nextPageToken").getAsString());
         }
         if (listPageObject.has("resourceViews")) {
            for (JsonElement resourceView : listPageObject.getAsJsonArray("resourceViews")) {
               builder.addItem((ResourceView) context.deserialize(resourceView, ResourceView.class));
            }
         }
         return builder.build();
      }
   }
   
   @Singleton
   private static class ListPageResourceViewMemberTypeAdapter implements
   JsonDeserializer<ListPage<URI>> {

      @Override
      public ListPage<URI> deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
         JsonObject listPageObject = json.getAsJsonObject();
         // ListPage is a subclass of resource so it requires an id and a selfLink.
         // Currently the list function for resource views does not return either
         // of these so set them to "" so no exceptions are thrown.
         ListPage.Builder<URI> builder = ListPage.builder();
         builder.id("")
                .selfLink(URI.create(""))
                .kind(Kind.RESOURCE_VIEW_MEMBER_LIST);
         if (listPageObject.has("nextPageToken")) {
            builder.nextPageToken(listPageObject.get("nextPageToken").getAsString());
         }
         if (listPageObject.has("members")) {
            for (JsonElement uri : listPageObject.getAsJsonArray("members")) {
               // remove quotes from member URIs because otherwise it throws an exception
               builder.addItem(getUri(uri.getAsString()));
            }
         }
         return builder.build();
      }
   }
   
   @Singleton
   private static class ResourceViewTypeAdapter implements JsonDeserializer<ResourceView> {

      @Override
      public ResourceView deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
         JsonObject resourceViewObject = json.getAsJsonObject();
         ResourceView.Builder builder = ResourceView.builder();
         // Meant to get the actual json object when resource: object is returned by insert
         if (resourceViewObject.has("resource")) {
            resourceViewObject = resourceViewObject.get("resource").getAsJsonObject();
         }
         if (resourceViewObject.has("id")) {
            builder.id(resourceViewObject.get("id").getAsString());
         }
         if (resourceViewObject.has("description")) {
            builder.description(resourceViewObject.get("description").getAsString());
         }
         if (resourceViewObject.has("name")) {
            builder.name(resourceViewObject.get("name").getAsString());
         }
         if (resourceViewObject.has("numMembers")) {
            builder.numMembers(resourceViewObject.get("numMembers").getAsInt());
         }
         if (resourceViewObject.has("creationTime")) {
            builder.creationTimestamp((Date) context.deserialize(resourceViewObject.get("creationTime"), Date.class));
         }
         if (resourceViewObject.has("members")) {
            for (JsonElement member : resourceViewObject.getAsJsonArray("members")) {
               // remove quotes from member URIs because otherwise it throws an exception
               builder.addMember(getUri(member.getAsString()));
            }
         }
         if (resourceViewObject.has("lastModified")) {
            builder.lastModified((Date) context.deserialize(resourceViewObject.get("lastModified"), Date.class));
         }
         if (resourceViewObject.has("selfLink")) {
            // remove quotes from member URIs because otherwise it throws an exception
            builder.selfLink(getUri(resourceViewObject.get("selfLink").getAsString()));
         }
         if (resourceViewObject.has("labels")) {
            for (JsonElement labelElement : resourceViewObject.getAsJsonArray("labels")) {
               JsonObject label = labelElement.getAsJsonObject();
               builder.addLabel(label.get("key").getAsString(), label.get("value").getAsString());
            }
         }
         return builder.build();
      }
   }

   @Singleton
   private static class BackendServiceOptionsTypeAdapter implements JsonSerializer<BackendServiceOptions> {

      @Override
      public JsonElement serialize(BackendServiceOptions src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject backendService = new JsonObject();
         if (src.getName() != null) {
            backendService.addProperty("name", src.getName());
         }
         if (!src.getBackends().isEmpty()) {
            JsonArray backends = new JsonArray();
            for (Backend backend : src.getBackends()) {
               backends.add(context.serialize(backend, BackendService.Backend.class));
            }
            backendService.add("backends", backends);
         }
         if (!src.getHealthChecks().isEmpty()) {
            backendService.add("healthChecks", buildArrayOfStringsFromURI(src.getHealthChecks()));
         }
         if (src.getTimeoutSec() != null) {
            backendService.addProperty("timeoutSec", src.getTimeoutSec());
         }
         if (src.getPort() != null) {
            backendService.addProperty("port", src.getPort());
         }
         if (src.getProtocol() != null) {
            backendService.addProperty("protocol", src.getProtocol());
         }
         if (src.getFingerprint() != null) {
            backendService.addProperty("fingerprint", src.getFingerprint());
         }
         
         return backendService;
      }
   }
   
   @Singleton
   private static class BackendTypeAdapter implements JsonSerializer<BackendService.Backend> {

      @Override
      public JsonElement serialize(BackendService.Backend src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject backendObject = new JsonObject();
         backendObject.addProperty("group", src.getGroup().toASCIIString());
         if (src.getDescription().isPresent()) {
            backendObject.addProperty("description", src.getDescription().get());
         }
         if (src.getBalancingMode().isPresent()) {
            backendObject.addProperty("balancingMode", src.getBalancingMode().get());
         }
         if (src.getMaxUtilization().isPresent()) {
            backendObject.addProperty("maxUtilization", src.getMaxUtilization().get());
         }
         if (src.getMaxRate().isPresent()) {
            backendObject.addProperty("maxRate", src.getMaxRate().get());
         }
         if (src.getMaxRatePerInstance().isPresent()) {
            backendObject.addProperty("maxRatePerInstance", src.getMaxRatePerInstance().get());
         }
         if (src.getCapacityScaler().isPresent()) {
            backendObject.addProperty("capacityScaler", src.getCapacityScaler().get());
         }
         return backendObject;
      }
   }

   @Singleton
   private static class UrlMapOptionsTypeAdapter implements JsonSerializer<UrlMapOptions> {

      @Override
      public JsonElement serialize(UrlMapOptions src, Type typeOfSrc,
            JsonSerializationContext context) {
         JsonObject urlMap = new JsonObject();
         if (src.getName() != null) {
            urlMap.addProperty("name", src.getName());
         }
         if (src.getDescription() != null) {
            urlMap.addProperty("description", src.getDescription());
         }
         if (!src.getHostRules().isEmpty()) {
            JsonArray hostRules = new JsonArray();
            for (HostRule hostRule : src.getHostRules()) {
               hostRules.add(context.serialize(hostRule, UrlMap.HostRule.class));
            }
            urlMap.add("hostRules", hostRules);
         }
         if (!src.getPathMatchers().isEmpty()) {
            JsonArray pathMatchers = new JsonArray();
            for (PathMatcher pathMatcher : src.getPathMatchers()) {
               pathMatchers.add(context.serialize(pathMatcher, UrlMap.PathMatcher.class));
            }
            urlMap.add("pathMatchers", pathMatchers);
         }
         if (!src.getTests().isEmpty()) {
            JsonArray tests = new JsonArray();
            for (UrlMapTest urlMapTest : src.getTests()) {
               tests.add(context.serialize(urlMapTest, UrlMap.UrlMapTest.class));
            }
            urlMap.add("tests", tests);
         }
         if (src.getDefaultService() != null) {
            urlMap.addProperty("defaultService", src.getDefaultService().toASCIIString());
         }
         if (src.getFingerprint() != null) {
            urlMap.addProperty("fingerprint", src.getFingerprint());
         }
         
         return urlMap;
      }
   }
   
   @Singleton
   private static class HostRuleTypeAdapter implements JsonSerializer<HostRule> {

      @Override
      public JsonElement serialize(HostRule src, Type typeOfSrc,
            JsonSerializationContext context) {
         JsonObject hostRuleObject = new JsonObject();
         if (src.getDescription().isPresent()) {
            hostRuleObject.addProperty("description", src.getDescription().get());
         }
         if (!src.getHosts().isEmpty()) {
            hostRuleObject.add("hosts", buildArrayOfStrings(src.getHosts()));
         }
         if (src.getPathMatcher() != null) {
            hostRuleObject.addProperty("pathMatcher", src.getPathMatcher());
         }
         return hostRuleObject;
      }
   }
   
   @Singleton
   private static class PathMatcherTypeAdapter implements JsonSerializer<PathMatcher> {

      @Override
      public JsonElement serialize(PathMatcher src, Type typeOfSrc,
            JsonSerializationContext context) {
         JsonObject pathMatcherObject = new JsonObject();
         if (src.getName() != null) {
            pathMatcherObject.addProperty("name", src.getName());
         }
         if (src.getDescription().isPresent()) {
            pathMatcherObject.addProperty("description", src.getDescription().get());
         }
         if (src.getDefaultService() != null) {
            pathMatcherObject.addProperty("defaultService", src.getDefaultService().toASCIIString());
         }
         if (!src.getPathRules().isEmpty()) {
            JsonArray pathRules = new JsonArray();
            for (PathRule pathRule : src.getPathRules()) {
               pathRules.add(context.serialize(pathRule, UrlMap.PathRule.class));
            }
            pathMatcherObject.add("pathRules", pathRules);
         }
         return pathMatcherObject;
      }
   }
   
   @Singleton
   private static class TestTypeAdapter implements JsonSerializer<UrlMapTest> {

      @Override
      public JsonElement serialize(UrlMapTest src, Type typeOfSrc,
            JsonSerializationContext context) {
         JsonObject testObject = new JsonObject();
         if (src.getDescription().isPresent()) {
            testObject.addProperty("description", src.getDescription().get());
         }
         if (src.getHost() != null) {
            testObject.addProperty("host", src.getHost());
         }
         if (src.getPath() != null) {
            testObject.addProperty("path", src.getPath());
         }
         if (src.getService() != null) {
            testObject.addProperty("service", src.getService().toASCIIString());
         }
         return testObject;
      }
   }
   
   @Singleton
   private static class PathRuleTypeAdapter implements JsonSerializer<PathRule> {

      @Override
      public JsonElement serialize(PathRule src, Type typeOfSrc,
            JsonSerializationContext context) {
         JsonObject pathRuleObject = new JsonObject();
         if (src.getService() != null) {
            pathRuleObject.addProperty("service", src.getService().toASCIIString());
         }
         if (!src.getPaths().isEmpty()) {
            pathRuleObject.add("paths", buildArrayOfStrings(src.getPaths()));
         }
         return pathRuleObject;
      }
   }
   
   // Needed because the result is returned as a nested object
   @Singleton
   private static class UrlMapValidateResultTypeAdapter implements JsonDeserializer<UrlMapValidateResult> {

      @Override
      public UrlMapValidateResult deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
         JsonObject validateResult = json.getAsJsonObject();
         validateResult = validateResult.get("result").getAsJsonObject();
         UrlMapValidateResult.Builder builder = UrlMapValidateResult.builder();
         builder.loadSucceeded(validateResult.get("loadSucceeded").getAsBoolean());
         if (validateResult.has("loadErrors")) {
            for (JsonElement string : validateResult.getAsJsonArray("loadErrors")) {
               builder.addLoadError(string.getAsString());
            }
         }
         if (validateResult.has("testPassed")) {
            builder.testPassed(validateResult.get("testPassed").getAsBoolean());
         }
         if (validateResult.has("testFailures")) {
            for (JsonElement testFailure : validateResult.getAsJsonArray("testFailures")) {
               builder.addTestFailure((TestFailure) context.deserialize(testFailure, TestFailure.class));
            }
         }
         return builder.build();
      }
   }
   
   private static JsonArray buildArrayOfStrings(Set<String> strings) {
      JsonArray array = new JsonArray();
      for (String string : strings) {
         array.add(new JsonPrimitive(string));
      }
      return array;
   }
   
   private static JsonArray buildArrayOfStringsFromURI(Set<URI> uris) {
      JsonArray array = new JsonArray();
      for (URI uri : uris) {
         array.add(new JsonPrimitive(uri.toString()));
      }
      return array;
   }
   
   private static URI getUri(String uri) {
      return URI.create(uri.replace("\"", ""));
   }
}
