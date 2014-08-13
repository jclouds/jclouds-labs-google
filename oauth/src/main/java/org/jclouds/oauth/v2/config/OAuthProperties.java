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
package org.jclouds.oauth.v2.config;

/**
 * Configurable properties for jclouds OAuth
 */
public class OAuthProperties {

   /**
    * The selected signature algorithm to use to sign the requests.
    * <p/>
    * This refers to the name the oauth provider expects, i.e., "RSA
    */
   public static final String SIGNATURE_OR_MAC_ALGORITHM = "jclouds.oauth.signature-or-mac-algorithm";

   /**
    * The oauth audience, who this token is intended for. For instance in JWT and for
    * google API's this property maps to: {"aud","https://accounts.google.com/o/oauth2/token"}
    *
    * @see <a href="http://tools.ietf.org/html/draft-jones-json-web-token-04">doc</a>
    */
   public static final String AUDIENCE = "jclouds.oauth.audience";

   /**
    * Optional list of comma-separated scopes to use when no OAuthScopes annotation is present.
    */
   public static final String SCOPES = "jclouds.oauth.scopes";

   /**
    * Specify if credentials are id + private key or if you are reusing an oauth2 token.
    *
    * @see org.jclouds.oauth.v2.config.CredentialType
    */
   public static final String CREDENTIAL_TYPE = "jclouds.oauth.credential-type";

}
