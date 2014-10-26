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
package org.jclouds.oauth.v2.features;

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.oauth.v2.OAuthTestUtils.getMandatoryProperty;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.SCOPES;
import static org.jclouds.oauth.v2.config.OAuthProperties.SIGNATURE_OR_MAC_ALGORITHM;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Properties;

import org.jclouds.oauth.v2.OAuthConstants;
import org.jclouds.oauth.v2.domain.ClaimSet;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.oauth.v2.internal.BaseOAuthApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * A live test for authentication. Requires the following properties to be set:
 * - test.oauth.endpoint
 * - test.oauth.identity
 * - test.oauth.credential
 * - test.jclouds.oauth.audience
 * - test.jclouds.oauth.scopes
 * - test.jclouds.oauth.signature-or-mac-algorithm
 */
@Test(groups = "live", singleThreaded = true)
public class OAuthApiLiveTest extends BaseOAuthApiLiveTest {

   private Properties properties;

   @Override
   protected Properties setupProperties() {
      properties = super.setupProperties();
      return properties;

   }

   @Test(groups = "live", singleThreaded = true)
   public void testAuthenticateJWTToken() throws Exception {
      assertTrue(properties != null, "properties were not set");
      String signatureAlgorithm = getMandatoryProperty(properties, SIGNATURE_OR_MAC_ALGORITHM);
      checkState(OAuthConstants.OAUTH_ALGORITHM_NAMES_TO_SIGNATURE_ALGORITHM_NAMES.containsKey(signatureAlgorithm)
              , String.format("Algorithm not supported: " + signatureAlgorithm));

      Header header = Header.create(signatureAlgorithm, "JWT");

      String scopes = getMandatoryProperty(properties, SCOPES);
      String audience = getMandatoryProperty(properties, AUDIENCE);

      long now = nowInSeconds();

      ClaimSet claimSet = ClaimSet.create(now, now + 3600,
            ImmutableMap.of("aud", audience, "scope", scopes, "iss", identity));

      TokenRequest tokenRequest = TokenRequest.create(header, claimSet);
      Token token = api.authenticate(tokenRequest);

      assertNotNull(token, "no token when authenticating " + tokenRequest);
   }
}
