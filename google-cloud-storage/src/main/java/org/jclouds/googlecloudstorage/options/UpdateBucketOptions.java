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
package org.jclouds.googlecloudstorage.options;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.PredefinedAcl;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Projection;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Allows to optionally specify ifMetagenerationMatch,ifMetagenerationNotMatch and projection which used in Bucket
 */
public class UpdateBucketOptions extends BaseHttpRequestOptions {

   public UpdateBucketOptions ifMetagenerationMatch(Long ifMetagenerationMatch) {
      this.queryParameters.put("ifMetagenerationMatch", checkNotNull(ifMetagenerationMatch, "ifMetagenerationMatch")
               + "");
      return this;
   }

   public UpdateBucketOptions ifMetagenerationNotMatch(Long ifMetagenerationNotMatch) {
      this.queryParameters.put("ifMetagenerationNotMatch",
               checkNotNull(ifMetagenerationNotMatch, "ifMetagenerationNotMatch") + "");
      return this;
   }

   public UpdateBucketOptions projection(Projection projection) {
      this.queryParameters.put("projection", checkNotNull(projection, "projection").toString());
      return this;
   }

   public UpdateBucketOptions predefinedAcl(PredefinedAcl predefinedAcl) {
      this.queryParameters.put("predefinedAcl", checkNotNull(predefinedAcl, "predefinedAcl").toString());
      return this;
   }

   public static class Builder {

      public UpdateBucketOptions ifMetagenerationMatch(Long ifMetagenerationMatch) {
         return new UpdateBucketOptions().ifMetagenerationMatch(ifMetagenerationMatch);
      }

      public UpdateBucketOptions ifMetagenerationNotMatch(Long ifMetagenerationNotMatch) {
         return new UpdateBucketOptions().ifMetagenerationNotMatch(ifMetagenerationNotMatch);
      }

      public UpdateBucketOptions projection(Projection projection) {
         return new UpdateBucketOptions().projection(projection);
      }

      public UpdateBucketOptions predefinedAcl(PredefinedAcl predefinedAcl) {
         return new UpdateBucketOptions().predefinedAcl(predefinedAcl);
      }

   }
}
