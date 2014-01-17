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
package org.jclouds.googlecomputeengine.compute.extensions;

import org.jclouds.compute.extensions.internal.BaseSecurityGroupExtensionLiveTest;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
import static org.jclouds.oauth.v2.OAuthTestUtils.setCredentialFromPemFile;

/**
 * @author Andrew Bayer
 */
@Test(groups = "live", singleThreaded = true, testName = "GoogleComputeEngineSecurityGroupExtensionLiveTest")
public class GoogleComputeEngineSecurityGroupExtensionLiveTest extends BaseSecurityGroupExtensionLiveTest {

   public GoogleComputeEngineSecurityGroupExtensionLiveTest() {
      provider = "google-compute-engine";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      setCredentialFromPemFile(props, provider + ".credential");
      props.setProperty(TEMPLATE, "osFamily=CENTOS,locationId=us-central1-a,loginUser=jclouds");
      return props;
   }

}
