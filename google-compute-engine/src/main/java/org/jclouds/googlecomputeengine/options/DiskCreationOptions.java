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
package org.jclouds.googlecomputeengine.options;

import java.net.URI;

public final class DiskCreationOptions {

   private URI type;
   private URI sourceImage;
   private URI sourceSnapshot;

   /**
    * The disk type, fully qualified URL for the disk type.
    *
    * @return the disk type
    */
   public URI type(){
      return type;
   }

   /**
    * The source image
    *
    * @return sourceImage, fully qualified URL for the image to be copied.
    */
   public URI sourceImage(){
      return sourceImage;
   }

   /**
    * The source snapshot
    *
    * @return sourceSnapshot, fully qualified URL for the snapshot to be copied.
    */
   public URI sourceSnapshot(){
      return sourceSnapshot;
   }

   /**
    * @see DiskCreationOptions#type()
    */
   public DiskCreationOptions type(URI type){
      this.type = type;
      return this;
   }

   /**
    * @see DiskCreationOptions#sourceImage()
    */
   public DiskCreationOptions sourceImage(URI sourceImage){
      this.sourceImage = sourceImage;
      return this;
   }

   /**
    * @see DiskCreationOptions#sourceSnapshot()
    */
   public DiskCreationOptions sourceSnapshot(URI sourceSnapshot){
      this.sourceSnapshot = sourceSnapshot;
      return this;
   }
}
