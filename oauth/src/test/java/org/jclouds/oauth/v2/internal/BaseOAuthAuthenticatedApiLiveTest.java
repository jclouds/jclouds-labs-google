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
package org.jclouds.oauth.v2.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.JWS_ALG;
import static org.jclouds.oauth.v2.domain.Claims.EXPIRATION_TIME;
import static org.jclouds.oauth.v2.domain.Claims.ISSUED_AT;
import static org.testng.Assert.assertNotNull;

import java.io.Closeable;
import java.util.Map;
import java.util.Properties;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.oauth.v2.JWSAlgorithms;
import org.jclouds.oauth.v2.OAuthApi;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

/**
 * A base test of oauth authenticated rest providers. Providers must set the following properties:
 * <p/>
 * - oauth.endpoint
 * - oauth.audience
 * - oauth.jws-alg
 * <p/>
 * - oauth.scopes is provided by the subclass
 * <p/>
 * This test asserts that a provider can authenticate with oauth for a given scope, or more simply
 * that authentication/authorization is working.
 */

@Test(groups = "live")
public abstract class BaseOAuthAuthenticatedApiLiveTest<A extends Closeable> extends BaseApiLiveTest<A> {

   protected abstract String getScopes();

   private OAuthApi oauthApi;

   public void testAuthenticate() {
      // obtain the necessary properties from the context
      String jwsAlg = checkNotNull(propFunction.apply(JWS_ALG), JWS_ALG);

      checkState(JWSAlgorithms.supportedAlgs().contains(jwsAlg), "Algorithm not supported: %s", jwsAlg);

      String audience = checkNotNull(propFunction.apply(AUDIENCE), AUDIENCE);

      // obtain the scopes from the subclass
      String scopes = getScopes();

      Header header = Header.create(jwsAlg, "JWT");

      long now = SECONDS.convert(System.currentTimeMillis(), MILLISECONDS);

      Map<String, Object> claims = ImmutableMap.<String, Object>builder()
            .put("iss", identity)
            .put("scope", scopes)
            .put("aud", audience)
            .put(EXPIRATION_TIME, now + 3600)
            .put(ISSUED_AT, now).build();

      TokenRequest tokenRequest = TokenRequest.create(header, claims);

      Token token = oauthApi.authenticate(tokenRequest);

      assertNotNull(token, "no token when authenticating " + tokenRequest);
   }

   @SuppressWarnings({ "unchecked", "serial" })
   protected A create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      propFunction = injector.getInstance(ValueOfConfigurationKeyOrNull.class);
      try {
         oauthApi = injector.getInstance(OAuthApi.class);
      } catch (Exception e) {
         throw new IllegalStateException("Provider has no OAuthApi bound. Was the OAuthAuthenticationModule added?");
      }
      return (A) injector.getInstance(Key.get(new TypeToken<A>(getClass()) {
      }.getType()));
   }

   private Function<String, String> propFunction;
}
