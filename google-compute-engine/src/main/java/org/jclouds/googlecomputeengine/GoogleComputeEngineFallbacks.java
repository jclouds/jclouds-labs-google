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

import static org.jclouds.Fallbacks.valOnNotFoundOr404;

import org.jclouds.Fallback;
import org.jclouds.googlecomputeengine.domain.ListPage;

public final class GoogleComputeEngineFallbacks {
   public static final class EmptyListPageOnNotFoundOr404 implements Fallback<Object> {
      public ListPage<Object> createOrPropagate(Throwable t) throws Exception {
         return valOnNotFoundOr404(ListPage.create(null, null, null), t);
      }
   }
}
