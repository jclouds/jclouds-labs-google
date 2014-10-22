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
package org.jclouds.googlecomputeengine;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.Fallbacks.valOnNotFoundOr404;

import org.jclouds.Fallback;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.Resource.Kind;

public interface GoogleComputeEngineFallbacks {

   public static final class EmptyListPageOnNotFoundOr404 implements Fallback<ListPage<Object>> {
      public ListPage<Object> createOrPropagate(Throwable t) throws Exception {
         // The Kind attribute is mandatory but dummy, as it will be never be used
         return valOnNotFoundOr404(new ListPage.Empty<Object>(Kind.ADDRESS_LIST), checkNotNull(t, "throwable"));
      }
   }

}
