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
package org.jclouds.googlecomputeengine.compute.internal;

import com.google.common.base.Supplier;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.TemplateBuilderImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.strategy.GetImageStrategy;
import org.jclouds.compute.suppliers.ImageCacheSupplier;
import org.jclouds.domain.Location;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.Set;

public class GCETemplateBuilderImpl extends TemplateBuilderImpl {
    private final String projectSelfLink;

    @Inject
    protected GCETemplateBuilderImpl(@Memoized Supplier<Set<? extends Location>> locations, ImageCacheSupplier images,
                                     @Memoized Supplier<Set<? extends Hardware>> hardwares, Supplier<Location> defaultLocation,
                                     @Named("DEFAULT") Provider<TemplateOptions> optionsProvider, @Named("DEFAULT") Provider<TemplateBuilder> defaultTemplateProvider,
                                     GetImageStrategy getImageStrategy, GoogleComputeEngineApi api) {
        super(locations, images, hardwares, defaultLocation, optionsProvider, defaultTemplateProvider, getImageStrategy);
        projectSelfLink = api.project().get().selfLink().toString();
    }

    @Override
    public TemplateBuilder hardwareId(String hardwareId) {
        if (!hardwareId.startsWith("https://") ) {
            if (location == null) {
                throw new IllegalStateException("GCE: first set locationId and then you can use shortened hardwareId");
            }
            hardwareId = projectSelfLink + "/zones/" + location.getId() + "/machineTypes/" + hardwareId;
        }
        super.hardwareId(hardwareId);
        return this;
    }
}
