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
package org.jclouds.oauth.v2.functions;

import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.SIGNATURE_OR_MAC_ALGORITHM;
import static org.jclouds.oauth.v2.domain.Claims.EXPIRATION_TIME;
import static org.jclouds.oauth.v2.domain.Claims.ISSUED_AT;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.domain.Header;
import org.jclouds.oauth.v2.domain.OAuthCredentials;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.reflect.Invokable;


/** Builds the default token request with the following claims: iss,scope,aud,iat,exp.  */
@Beta // This class at certain refactor risk.
public class BuildTokenRequest implements Function<GeneratedHttpRequest, TokenRequest> {
   private final String assertionTargetDescription;
   private final String signatureAlgorithm;
   private final Supplier<OAuthCredentials> credentialsSupplier;
   private final long tokenDuration;

   /** For testing, time is always 0. */
   public static final class WithConstantIssuedAt extends BuildTokenRequest {
      @Inject WithConstantIssuedAt(@Named(AUDIENCE) String assertionTargetDescription,
            @Named(SIGNATURE_OR_MAC_ALGORITHM) String signatureAlgorithm,
            Supplier<OAuthCredentials> credentialsSupplier,
            @Named(Constants.PROPERTY_SESSION_INTERVAL) long tokenDuration) {
         super(assertionTargetDescription, signatureAlgorithm, credentialsSupplier, tokenDuration);
      }

      @Override long timeInSeconds() {
         return 0l;
      }
   }

   @Inject BuildTokenRequest(@Named(AUDIENCE) String assertionTargetDescription,
         @Named(SIGNATURE_OR_MAC_ALGORITHM) String signatureAlgorithm, Supplier<OAuthCredentials> credentialsSupplier,
         @Named(Constants.PROPERTY_SESSION_INTERVAL) long tokenDuration) {
      this.assertionTargetDescription = assertionTargetDescription;
      this.signatureAlgorithm = signatureAlgorithm;
      this.credentialsSupplier = credentialsSupplier;
      this.tokenDuration = tokenDuration;
   }

   @Override public TokenRequest apply(GeneratedHttpRequest request) {
      long now = timeInSeconds();

      Header header = Header.create(signatureAlgorithm, "JWT");

      Map<String, Object> claims = new LinkedHashMap<String, Object>();
      claims.put("iss", credentialsSupplier.get().identity);
      claims.put("scope", getOAuthScopes(request));
      claims.put("aud", assertionTargetDescription);
      claims.put(EXPIRATION_TIME, now + tokenDuration);
      claims.put(ISSUED_AT, now);

      return TokenRequest.create(header, claims);
   }

   long timeInSeconds() {
      return System.currentTimeMillis() / 1000;
   }

   private String getOAuthScopes(GeneratedHttpRequest request) {
      Invokable<?, ?> invokable = request.getInvocation().getInvokable();
      
      OAuthScopes classScopes = invokable.getOwnerType().getRawType().getAnnotation(OAuthScopes.class);
      OAuthScopes methodScopes = invokable.getAnnotation(OAuthScopes.class);

      OAuthScopes scopes = methodScopes != null ? methodScopes : classScopes;

      if (scopes == null) {
         throw new IllegalStateException(
               String.format("Missing OAuthScopes on %s or %s", invokable.getOwnerType(), invokable.getName()));
      }

      return Joiner.on(",").join(scopes.value());
   }
}
