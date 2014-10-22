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
package org.jclouds.googlecomputeengine.functions.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecomputeengine.domain.ListPage;
import org.jclouds.googlecomputeengine.domain.PageWithMarker;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rest.InvocationContext;

import com.google.common.base.Function;

/**
 * Base class for the classes that parse a single page resulting of a call to a
 * paginated api.
 */
public abstract class BasePageParser<T, I extends BasePageParser<T, I>> implements Function<HttpResponse, ListPage<T>>, InvocationContext<I> {

   protected final ParseJson<PageWithMarker<T>> parser;
   protected final Function<PageWithMarker<T>, PagedIterable<T>> advancingFunction;

   protected BasePageParser(ParseJson<PageWithMarker<T>> parser,
         Function<PageWithMarker<T>, PagedIterable<T>> advancingFunction) {
      this.parser = checkNotNull(parser, "parser");
      this.advancingFunction = checkNotNull(advancingFunction, "advancingFunction");

   }

   @Override
   public ListPage<T> apply(HttpResponse from) {
      PageWithMarker<T> page = parser.apply(from);
      return new ListPage<T>(page, advancingFunction);
   }

   @SuppressWarnings("unchecked")
   @Override
   public I setContext(HttpRequest request) {
      if (advancingFunction instanceof InvocationContext) {
         InvocationContext.class.cast(advancingFunction).setContext(request);
      }
      return (I) this;
   }
}
