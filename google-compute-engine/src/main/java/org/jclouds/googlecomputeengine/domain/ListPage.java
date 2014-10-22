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
package org.jclouds.googlecomputeengine.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.IterableWithMarkers;
import org.jclouds.collect.PagedIterable;
import org.jclouds.collect.PagedIterables;
import org.jclouds.googlecomputeengine.domain.Resource.Kind;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * A single page returned from a paginated collection that knows how to advance
 * to the next page.
 */
public class ListPage<T> extends IterableWithMarker<T> {

   private final PageWithMarker<T> delegate;
   private final Function<PageWithMarker<T>, PagedIterable<T>> advancingFunction;

   public ListPage(PageWithMarker<T> delegate, Function<PageWithMarker<T>, PagedIterable<T>> advancingFunction) {
      this.delegate = checkNotNull(delegate, "delegate");
      this.advancingFunction = checkNotNull(advancingFunction, "advancingFunction");
   }

   public PagedIterable<T> toPagedIterable() {
      return advancingFunction.apply(delegate);
   }

   @Override
   public Iterator<T> iterator() {
      return delegate.iterator();
   }

   @Override
   public Optional<Object> nextMarker() {
      return delegate.nextMarker();
   }

   public static class Empty<T> extends ListPage<T> {

      public Empty(Kind kind) {
         super(PageWithMarker.<T>builder().kind(kind).build(), new Function<PageWithMarker<T>, PagedIterable<T>>() {
            @Override
            public PagedIterable<T> apply(PageWithMarker<T> input) {
               return PagedIterables.onlyPage(IterableWithMarkers.from(ImmutableSet.<T>of()));
            }
         });
      }

   }
   
}
