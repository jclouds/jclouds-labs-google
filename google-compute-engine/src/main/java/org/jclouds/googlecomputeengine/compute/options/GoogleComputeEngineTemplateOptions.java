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
package org.jclouds.googlecomputeengine.compute.options;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.googlecomputeengine.domain.AttachDisk;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.collect.Lists;

/** Instance options specific to Google Compute Engine. */
public final class GoogleComputeEngineTemplateOptions extends TemplateOptions {

   private URI network = null;
   private boolean autoCreateKeyPair = true;
   private List<AttachDisk> disks = Lists.newArrayList();
   private AutoCreateDiskOptions autoCreateDiskOptions = null;
   private boolean canIpForward = false;

   @Override
   public GoogleComputeEngineTemplateOptions clone() {
      GoogleComputeEngineTemplateOptions options = new GoogleComputeEngineTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof GoogleComputeEngineTemplateOptions) {
         GoogleComputeEngineTemplateOptions eTo = GoogleComputeEngineTemplateOptions.class.cast(to);
         eTo.network(network());
         eTo.disks(getDisks());
         eTo.autoCreateDisk(getAutoCreateDiskOptions());
         eTo.canIpForward(canIpForward());
         eTo.autoCreateKeyPair(autoCreateKeyPair());
      }
   }

   /** @see #network() */
   public GoogleComputeEngineTemplateOptions network(URI network) {
      this.network = network;
      return this;
   }

   /**
    * The network instances will attach to. When absent, a new network will be
    * created for the project.
    */
   @Nullable
   public URI network() {
      return network;
   }

   /**
    * Sets whether an SSH key pair should be created automatically.
    */
   public GoogleComputeEngineTemplateOptions autoCreateKeyPair(boolean autoCreateKeyPair) {
      this.autoCreateKeyPair = autoCreateKeyPair;
      return this;
   }

   /**
    * Sets whether an SSH key pair should be created automatically.
    */
   public boolean autoCreateKeyPair() {
      return autoCreateKeyPair;
   }

   public boolean canIpForward() {
      return canIpForward;
   }

   public void canIpForward(boolean enable) {
      this.canIpForward = enable;
   }

   /**
    * @see #getDisks()
    * @see org.jclouds.googlecomputeengine.domain.templates.InstanceTemplate.AttachDisk
    */
   public GoogleComputeEngineTemplateOptions addDisk(AttachDisk disk) {
      this.disks.add(disk);
      return this;
   }

   public void autoCreateDisk(final AutoCreateDiskOptions diskOptions) {
      this.autoCreateDiskOptions = diskOptions;
   }

   public AutoCreateDiskOptions getAutoCreateDiskOptions() {
      return autoCreateDiskOptions;
   }

   /**
    * @return the AttachDisks for this instance.
    */
   public List<AttachDisk> getDisks() {
      return disks;
   }

   // /**
   // * @see #shouldKeepBootDisk()
   // */
   // public GoogleComputeEngineTemplateOptions keepBootDisk(boolean
   // keepBootDisk) {
   // this.keepBootDisk = keepBootDisk;
   // return this;
   // }

   /**
    * @see #getDisks()
    * @see org.jclouds.googlecomputeengine.domain.templates.InstanceTemplate.AttachDisk
    */
   public GoogleComputeEngineTemplateOptions disks(List<AttachDisk> disks) {
      this.disks = Lists.newArrayList(disks);
      return this;
   }

   // /**
   // * @see #getBootDiskSize()
   // */
   // public GoogleComputeEngineTemplateOptions bootDiskSize(Long bootDiskSize)
   // {
   // this.bootDiskSize = fromNullable(bootDiskSize);
   // return this;
   // }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions blockOnPort(int port, int seconds) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions inboundPorts(int... ports) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions authorizePublicKey(String publicKey) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions installPrivateKey(String privateKey) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.blockUntilRunning(blockUntilRunning));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions dontAuthorizePublicKey() {
      return GoogleComputeEngineTemplateOptions.class.cast(super.dontAuthorizePublicKey());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions nameTask(String name) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.nameTask(name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions runAsRoot(boolean runAsRoot) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.runAsRoot(runAsRoot));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions runScript(Statement script) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.overrideLoginCredentials(overridingCredentials));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions overrideLoginPassword(String password) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.overrideLoginPassword(password));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions overrideLoginPrivateKey(String privateKey) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.overrideLoginPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions overrideLoginUser(String loginUser) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.overrideLoginUser(loginUser));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.overrideAuthenticateSudo(authenticateSudo));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions userMetadata(String key, String value) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.userMetadata(key, value));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions nodeNames(Iterable<String> nodeNames) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.nodeNames(nodeNames));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions networks(Iterable<String> networks) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.networks(networks));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions networks(String... networks) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.networks(networks));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions tags(Iterable<String> tags) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.tags(tags));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions wrapInInitScript(boolean wrapInInitScript) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.wrapInInitScript(wrapInInitScript));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions runScript(String script) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public GoogleComputeEngineTemplateOptions blockOnComplete(boolean blockOnComplete) {
      return GoogleComputeEngineTemplateOptions.class.cast(super.blockOnComplete(blockOnComplete));
   }

   public static class AutoCreateDiskOptions {
      public final AttachDisk.Type diskType;
      public final AttachDisk.Mode diskMode;
      public final boolean isBootDisk;
      public final int diskSizeGb;
      private String diskName = null;

      public AutoCreateDiskOptions(final AttachDisk.Type diskType, final AttachDisk.Mode diskMode, final boolean isBootDisk, final int diskSizeGb) {
         this.diskType = diskType;
         this.diskMode = diskMode;
         this.isBootDisk = isBootDisk;
         this.diskSizeGb = diskSizeGb;
      }

      public AutoCreateDiskOptions(final AttachDisk.Type diskType, final AttachDisk.Mode diskMode, final boolean isBootDisk, final int diskSizeGb, final String diskName) {
         this(diskType, diskMode, isBootDisk, diskSizeGb);
         // TODO: Validate against regex according to GCE spec.
         this.diskName = diskName;
      }

      public String getDiskName(final String nodeName) {
         if (null != diskName)
            return diskName;
         else
            return "jclouds-" + UUID.randomUUID().toString();
      }
   }
}
