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
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.primitives.Ints.asList;
import static org.jclouds.http.HttpUtils.returnValueOnCodeOrNull;

import org.jclouds.Fallback;

public final class GoogleComputeEngineFallbacks {
   public static class NullOn400or404 implements Fallback<Object> {
      @Override public Object createOrPropagate(Throwable t) throws Exception {
         Boolean returnVal = returnValueOnCodeOrNull(checkNotNull(t, "throwable"), false, in(asList(400, 404)));
         if (returnVal != null)
            return null;
         throw propagate(t);
      }
   }
}
