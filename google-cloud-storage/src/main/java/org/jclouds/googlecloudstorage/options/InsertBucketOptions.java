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

import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.PredefinedAcl;
import org.jclouds.googlecloudstorage.domain.DomainResourceRefferences.Projection;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Allows to optionally specify predefinedAcl and projection which used in Bucket
 * 
 * @see <a href="https://developers.google.com/storage/docs/json_api/v1/buckets/insert"/>
 */
public class InsertBucketOptions extends BaseHttpRequestOptions {

   public InsertBucketOptions predefinedAcl(PredefinedAcl predefinedAcl) {
      this.queryParameters.put("predefinedAcl", checkNotNull(predefinedAcl, "predefinedAcl").toString());
      return this;
   }

   public InsertBucketOptions projection(Projection projection) {
      this.queryParameters.put("projection", checkNotNull(projection, "projection").toString());
      return this;
   }

   public static class Builder {

      public InsertBucketOptions predefinedAcl(PredefinedAcl predefinedAcl) {
         return new InsertBucketOptions().predefinedAcl(predefinedAcl);
      }

      public InsertBucketOptions projection(Projection projection) {
         return new InsertBucketOptions().projection(projection);
      }

   }
}
