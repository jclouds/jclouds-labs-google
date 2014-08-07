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

import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_READONLY_SCOPE;
import static org.jclouds.googlecomputeengine.GoogleComputeEngineConstants.COMPUTE_SCOPE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNull;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.googlecomputeengine.internal.BaseGoogleComputeEngineApiExpectTest;
import org.jclouds.googlecomputeengine.options.ForwardingRuleOptions;
import org.jclouds.googlecomputeengine.parse.ParseGlobalForwardingRuleListTest;
import org.jclouds.googlecomputeengine.parse.ParseGlobalForwardingRuleTest;
import org.jclouds.googlecomputeengine.parse.ParseOperationTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

@Test(groups = "unit")
public class ForwardingRuleApiExpectTest extends BaseGoogleComputeEngineApiExpectTest {
   
   private static final String ENDPOINT_BASE = "https://www.googleapis.com/"
            + "compute/v1/projects/myproject/global/forwardingRules";
   
   private org.jclouds.http.HttpRequest.Builder<? extends HttpRequest.Builder<?>> getBasicRequest() {
      return HttpRequest.builder().addHeader("Accept", "application/json")
                                  .addHeader("Authorization", "Bearer " + TOKEN);
   }
   
   private HttpResponse createResponse(String payloadFile) {
      return HttpResponse.builder().statusCode(200)
                                   .payload(payloadFromResource(payloadFile))
                                   .build();
   }

   public void testGetGlobalForwardingRuleResponseIs2xx() throws Exception {
      HttpRequest request = getBasicRequest().method("GET")
                                             .endpoint(ENDPOINT_BASE + "/jclouds-test")
                                             .build();
      
      HttpResponse response = createResponse("/global_forwarding_rule_get.json");

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getForwardingRuleApiForProject("myproject");

      assertEquals(api.get("jclouds-test"), new ParseGlobalForwardingRuleTest().expected());
   }

   public void testGetGlobalForwardingRuleResponseIs4xx() throws Exception {
      HttpRequest request = getBasicRequest().method("GET")
                                             .endpoint(ENDPOINT_BASE + "/jclouds-test")
                                             .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getForwardingRuleApiForProject("myproject");

      assertNull(api.get("jclouds-test"));
   }

   public void testInsertGlobalForwardingRuleResponseIs2xx() throws IOException {
      HttpRequest request = getBasicRequest().method("POST")
               .endpoint(ENDPOINT_BASE)
               .payload(payloadFromResourceWithContentType("/global_forwarding_rule_insert.json",
                                                           MediaType.APPLICATION_JSON))
               .build();

      HttpResponse response = createResponse("/operation.json");

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getForwardingRuleApiForProject("myproject");
      
      
      URI target = URI.create("https://www.googleapis.com/compute/v1/projects/myproject"
                              + "/global/targetHttpProxies/jclouds-test");
      assertEquals(api.create("jclouds-test", new ForwardingRuleOptions().target(target)
                                                                         .portRange("80")),
                   new ParseOperationTest().expected());

   }

   public void testDeleteGlobalForwardingRuleResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("DELETE")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .build();

      HttpResponse response = createResponse("/operation.json");

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response)
              .getForwardingRuleApiForProject("myproject");

      assertEquals(api.delete("jclouds-test"),
              new ParseOperationTest().expected());
   }

   public void testDeleteGlobalForwardingRuleResponseIs4xx() {
      HttpRequest request = getBasicRequest().method("DELETE")
               .endpoint(ENDPOINT_BASE + "/jclouds-test")
               .build();

      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).getForwardingRuleApiForProject("myproject");

      assertNull(api.delete("jclouds-test"));
   }

   public void testListGlobalForwardingRulesResponseIs2xx() {
      HttpRequest request = getBasicRequest().method("GET")
               .endpoint(ENDPOINT_BASE)
               .build();
      
            HttpResponse response = createResponse("/global_forwarding_rule_list.json");

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response).getForwardingRuleApiForProject("myproject");

      assertEquals(api.listFirstPage().toString(),
              new ParseGlobalForwardingRuleListTest().expected().toString());
   }

   public void testListGlobalForwardingRulesResponseIs4xx() {
      HttpRequest request = getBasicRequest().method("GET")
               .endpoint(ENDPOINT_BASE)
               .build();
      
      HttpResponse response = HttpResponse.builder().statusCode(404).build();

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_READONLY_SCOPE),
              TOKEN_RESPONSE, request, response).getForwardingRuleApiForProject("myproject");

      assertTrue(api.list().concat().isEmpty());
   }
   
   public void testSetTargetResponseIs2xx() throws Exception {
      HttpRequest request = getBasicRequest().method("POST")
               .endpoint(ENDPOINT_BASE + "/jclouds-test/setTarget")
               .payload(payloadFromResourceWithContentType("/global_forwarding_rule_setTarget.json",
                                                           MediaType.APPLICATION_JSON))
               .build();

      HttpResponse response = createResponse("/operation.json");

      ForwardingRuleApi api = requestsSendResponses(requestForScopes(COMPUTE_SCOPE),
              TOKEN_RESPONSE, request, response).getForwardingRuleApiForProject("myproject");

      URI target = URI.create("https://www.googleapis.com/compute/v1/projects/myproject/global"
                              + "/targetHttpProxies/jclouds-test-2");
      assertEquals(api.setTarget("jclouds-test", target),
              new ParseOperationTest().expected());
   }
}
