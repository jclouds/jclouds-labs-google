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
package org.jclouds.googlecloudstorage.blobstore.strategy.internal;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

public class MultipartUploadSlicingAlgorithm {

   @Resource
   @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
   protected Logger logger = Logger.NULL;

   @VisibleForTesting
   static final long DEFAULT_PART_SIZE = 32 * 1024 * 1024;

   @VisibleForTesting
   static final int DEFAULT_MAGNITUDE_BASE = 100;

   @Inject(optional = true)
   @Named("jclouds.mpu.parts.size")
   @VisibleForTesting
   long defaultPartSize = DEFAULT_PART_SIZE;

   @Inject(optional = true)
   @Named("jclouds.mpu.parts.magnitude")
   @VisibleForTesting
   int magnitudeBase = DEFAULT_MAGNITUDE_BASE;

   //TODO: Needs testing!
   protected long calculateChunkSize(long length) {
      long unitPartSize = defaultPartSize; // first try with default part size
      long partSize = unitPartSize;
      int parts = (int) (length / partSize);
      int magnitude = parts / magnitudeBase;
      if (magnitude > 0) {
         partSize = magnitude * unitPartSize;
         if (partSize > MultipartUpload.MAX_PART_SIZE) {
            partSize = MultipartUpload.MAX_PART_SIZE;
            unitPartSize = MultipartUpload.MAX_PART_SIZE;
         }
         parts = (int) (length / partSize);
         if (parts * partSize < length) {
            partSize = (magnitude + 1) * unitPartSize;
            if (partSize > MultipartUpload.MAX_PART_SIZE) {
               partSize = MultipartUpload.MAX_PART_SIZE;
               unitPartSize = MultipartUpload.MAX_PART_SIZE;
            }
            parts = (int) (length / partSize);
         }
      }
      if (parts > MultipartUpload.MAX_NUMBER_OF_PARTS) { // if splits in too many parts or
         // cannot be split
         unitPartSize = MultipartUpload.MIN_PART_SIZE; // take the minimum part size
         parts = (int) (length / unitPartSize);
      }
      if (parts > MultipartUpload.MAX_NUMBER_OF_PARTS) { // if still splits in too many parts
         parts = MultipartUpload.MAX_NUMBER_OF_PARTS - 1; // limit them. do not care about not
         // covering
      }
      long remainder = length % unitPartSize;
      if (remainder == 0 && parts > 0) {
         parts -= 1;
      }
      long remaining = length - partSize * parts;
      logger.debug(" %d bytes partitioned in %d parts of part size: %d, remaining: %d%s", length, parts, partSize,
               remaining, remaining > MultipartUpload.MAX_PART_SIZE ? " overflow!" : "");
      return partSize;
   }

   // Returns the correct number of parts.
   public static int calculateParts(long length, long chunkSize){
      if (length < chunkSize){
         return 1;
      }
      long remainder = length % chunkSize;
      int val = (int) (length / chunkSize);
      if (remainder > 0){
         return val + 1;
      }
      return val;
   }

   // Calculates the size of the last chunk given a length of bytes and a chunkSize
   public static long calculateRemaining(long length, long chunkSize) {
      if (length < chunkSize) {
         return length;
      }
      return length % chunkSize;
   }
}
