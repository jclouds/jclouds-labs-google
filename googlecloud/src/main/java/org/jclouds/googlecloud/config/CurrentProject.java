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
package org.jclouds.googlecloud.config;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Qualifier;

/** Associated bindings with the current <a href="https://cloud.google.com/compute/docs/projects">project</a>. */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Qualifier
public @interface CurrentProject {

   /** Utilities related to the email associated with the service account of a project. */
   public static final class ClientEmail {
      public static final String DESCRIPTION = "" //
            + "client_email which usually looks like project_id@developer.gserviceaccount.com or " //
            + "project_id-extended_uid@developer.gserviceaccount.com";
      private static final Pattern PROJECT_NUMBER_PATTERN = Pattern.compile("^([0-9]+)[@-].*");

      /** Parses the project number from the client email or throws an {@linkplain IllegalArgumentException}. */
      public static String toProjectNumber(String email) {
         Matcher matcher = PROJECT_NUMBER_PATTERN.matcher(email);
         checkArgument(matcher.find(), "Client email %s is malformed. Should be %s", email, DESCRIPTION);
         return matcher.group(1);
      }
   }
}
