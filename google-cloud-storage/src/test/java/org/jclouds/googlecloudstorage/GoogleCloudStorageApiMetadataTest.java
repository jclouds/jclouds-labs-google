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
package org.jclouds.googlecloudstorage;

import org.jclouds.View;
import org.jclouds.apis.internal.BaseApiMetadataTest;
import org.jclouds.googlecloudstorage.GoogleCloudStorageApiMetadata;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;

/**
 * 
 * <p/>
 * 
 * <pre>
 * META - INF / services / org.jclouds.apis.ApiMetadata
 * </pre>
 * 
 * @author Bhathiya Supun
 */
@Test(groups = "unit", testName = "GoogleStorageApiMetadataTest")
public class GoogleCloudStorageApiMetadataTest extends BaseApiMetadataTest {
	public GoogleCloudStorageApiMetadataTest() {
		super(new GoogleCloudStorageApiMetadata(), ImmutableSet.<TypeToken<? extends View>> of());
	}
}
