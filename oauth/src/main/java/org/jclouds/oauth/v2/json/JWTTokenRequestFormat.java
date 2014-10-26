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
package org.jclouds.oauth.v2.json;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Joiner.on;
import static com.google.common.io.BaseEncoding.base64Url;
import static org.jclouds.io.Payloads.newUrlEncodedFormPayload;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.json.Json;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.oauth.v2.domain.TokenRequestFormat;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Formats a token request into JWT format namely:
 * - transforms the token request to json
 * - creates the base64 header.claimset portions of the payload.
 * - uses the provided signer function to create a signature
 * - creates the full url encoded payload as described in:
 * https://developers.google.com/accounts/docs/OAuth2ServiceAccount
 * <p/>
 */
public class JWTTokenRequestFormat implements TokenRequestFormat {

   private static final String ASSERTION_FORM_PARAM = "assertion";
   private static final String GRANT_TYPE_FORM_PARAM = "grant_type";
   private static final String GRANT_TYPE_JWT_BEARER = "urn:ietf:params:oauth:grant-type:jwt-bearer";

   private final Function<byte[], byte[]> signer;
   private final Json json;

   @Inject JWTTokenRequestFormat(Function<byte[], byte[]> signer, Json json) {
      this.signer = signer;
      this.json = json;
   }

   @Override public <R extends HttpRequest> R formatRequest(R request, TokenRequest tokenRequest) {

      String encodedHeader = json.toJson(tokenRequest.header());
      String encodedClaimSet = json.toJson(tokenRequest.claimSet());

      encodedHeader = base64Url().omitPadding().encode(encodedHeader.getBytes(UTF_8));
      encodedClaimSet = base64Url().omitPadding().encode(encodedClaimSet.getBytes(UTF_8));

      byte[] signature = signer.apply(on(".").join(encodedHeader, encodedClaimSet).getBytes(UTF_8));
      String encodedSignature = signature != null ?  base64Url().omitPadding().encode(signature) : "";

      // the final assertion in base 64 encoded {header}.{claimSet}.{signature} format
      String assertion = on(".").join(encodedHeader, encodedClaimSet, encodedSignature);
      Payload payload = newUrlEncodedFormPayload(ImmutableMultimap.<String, String> builder()
                           .put(GRANT_TYPE_FORM_PARAM, GRANT_TYPE_JWT_BEARER)
                           .put(ASSERTION_FORM_PARAM, assertion).build());

      return (R) request.toBuilder().payload(payload).build();
   }

   @Override public String type() {
      return "JWT";
   }

   @Override public Set<String> requiredClaims() {
      // exp and ist (expiration and emission times) are assumed mandatory already
      return ImmutableSet.of("iss", "scope", "aud");
   }
}
