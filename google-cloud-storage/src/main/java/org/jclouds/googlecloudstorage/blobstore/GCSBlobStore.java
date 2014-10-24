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
package org.jclouds.googlecloudstorage.blobstore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.internal.BlobBuilderImpl;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.internal.BaseBlobStore;
import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.blobstore.strategy.internal.FetchBlobMetadata;
import org.jclouds.blobstore.util.BlobUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.domain.Location;
import org.jclouds.googlecloudstorage.GoogleCloudStorageApi;
import org.jclouds.googlecloudstorage.blobstore.functions.BlobMetadataToObjectTemplate;
import org.jclouds.googlecloudstorage.blobstore.functions.BlobStoreListContainerOptionsToListObjectOptions;
import org.jclouds.googlecloudstorage.blobstore.functions.BucketToStorageMetadata;
import org.jclouds.googlecloudstorage.blobstore.functions.ObjectListToStorageMetadata;
import org.jclouds.googlecloudstorage.blobstore.functions.ObjectToBlobMetadata;
import org.jclouds.googlecloudstorage.blobstore.strategy.internal.MultipartUploadStrategy;
import org.jclouds.googlecloudstorage.config.UserProject;
import org.jclouds.googlecloudstorage.domain.Bucket;
import org.jclouds.googlecloudstorage.domain.DomainResourceReferences.ObjectRole;
import org.jclouds.googlecloudstorage.domain.GCSObject;
import org.jclouds.googlecloudstorage.domain.ListPage;
import org.jclouds.googlecloudstorage.domain.templates.BucketTemplate;
import org.jclouds.googlecloudstorage.domain.templates.DefaultObjectAccessControlsTemplate;
import org.jclouds.googlecloudstorage.domain.templates.ObjectTemplate;
import org.jclouds.googlecloudstorage.options.ListObjectOptions;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.internal.PayloadEnclosingImpl;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.hash.HashCode;
import com.google.inject.Inject;
import com.google.inject.Provider;

@Singleton
public class GCSBlobStore extends BaseBlobStore {

   GoogleCloudStorageApi api;
   BucketToStorageMetadata bucketToStorageMetadata;
   ObjectToBlobMetadata objectToBlobMetadata;
   ObjectListToStorageMetadata objectListToStorageMetadata;
   Provider<FetchBlobMetadata> fetchBlobMetadataProvider;
   BlobMetadataToObjectTemplate blobMetadataToObjectTemplate;
   BlobStoreListContainerOptionsToListObjectOptions listContainerOptionsToListObjectOptions;
   MultipartUploadStrategy multipartUploadStrategy;
   Supplier<String> projectId;

   @Inject
   protected GCSBlobStore(BlobStoreContext context, BlobUtils blobUtils, Supplier<Location> defaultLocation,
            @Memoized Supplier<Set<? extends Location>> locations, GoogleCloudStorageApi api,
            BucketToStorageMetadata bucketToStorageMetadata, ObjectToBlobMetadata objectToBlobMetadata,
            ObjectListToStorageMetadata objectListToStorageMetadata,
            Provider<FetchBlobMetadata> fetchBlobMetadataProvider,
            BlobMetadataToObjectTemplate blobMetadataToObjectTemplate,
            BlobStoreListContainerOptionsToListObjectOptions listContainerOptionsToListObjectOptions,
            MultipartUploadStrategy multipartUploadStrategy, @UserProject Supplier<String> projectId) {
      super(context, blobUtils, defaultLocation, locations);
      this.api = api;
      this.bucketToStorageMetadata = bucketToStorageMetadata;
      this.objectToBlobMetadata = objectToBlobMetadata;
      this.objectListToStorageMetadata = objectListToStorageMetadata;
      this.fetchBlobMetadataProvider = fetchBlobMetadataProvider;
      this.blobMetadataToObjectTemplate = blobMetadataToObjectTemplate;
      this.listContainerOptionsToListObjectOptions = listContainerOptionsToListObjectOptions;
      this.projectId = projectId;
      this.multipartUploadStrategy = multipartUploadStrategy;
   }

   @Override
   public PageSet<? extends StorageMetadata> list() {
      return new Function<ListPage<Bucket>, org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata>>() {
         public org.jclouds.blobstore.domain.PageSet<? extends StorageMetadata> apply(ListPage<Bucket> from) {
            return new PageSetImpl<StorageMetadata>(Iterables.transform(from, bucketToStorageMetadata), null);
         }
      }.apply(api.getBucketApi().listBucket(projectId.get()));
   }

   @Override
   public boolean containerExists(String container) {
      return api.getBucketApi().bucketExist(container);
   }

   @Override
   public boolean createContainerInLocation(Location location, String container) {
      BucketTemplate template = new BucketTemplate().name(container);
      if (location != null) {
         org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Location gcsLocation = org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Location
                  .fromValue(location.getId());
         template = template.location(gcsLocation);
      }
      return api.getBucketApi().createBucket(projectId.get(), template) != null;
   }

   @Override
   public boolean createContainerInLocation(Location location, String container, CreateContainerOptions options) {
      BucketTemplate template = new BucketTemplate().name(container);
      if (location != null) {
         org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Location gcsLocation = org.jclouds.googlecloudstorage.domain.DomainResourceReferences.Location
                  .fromValue(location.getId());
         template = template.location(gcsLocation);
      }
      Bucket bucket = api.getBucketApi().createBucket(projectId.get(), template);
      if (options.isPublicRead()) {
         try {
            DefaultObjectAccessControlsTemplate doAclTemplate = new DefaultObjectAccessControlsTemplate().entity(
                     "allUsers").role(ObjectRole.READER);
            api.getDefaultObjectAccessControlsApi().createDefaultObjectAccessControls(container, doAclTemplate);
         } catch (HttpResponseException e) {
            // If DefaultObjectAccessControls operation fail, reverse the create operation.
            api.getBucketApi().deleteBucket(container);
            return false;
         }
      }

      return bucket != null;
   }

   @Override
   public PageSet<? extends StorageMetadata> list(String container) {
      ListPage<GCSObject> gcsList = api.getObjectApi().listObjects(container);
      PageSet<? extends StorageMetadata> list = objectListToStorageMetadata.apply(gcsList);
      return list;
   }

   @Override
   public PageSet<? extends StorageMetadata> list(String container, ListContainerOptions options) {

      if (options != null && options != ListContainerOptions.NONE) {
         ListObjectOptions listOptions = listContainerOptionsToListObjectOptions.apply(options);
         ListPage<GCSObject> gcsList = api.getObjectApi().listObjects(container, listOptions);
         PageSet<? extends StorageMetadata> list = objectListToStorageMetadata.apply(gcsList);
         return options.isDetailed() ? fetchBlobMetadataProvider.get().setContainerName(container).apply(list) : list;
      } else {
         return list(container);
      }
   }

   /**
    * Checks whether an accessible object is available. Google cloud storage does not directly support
    * BucketExist or ObjectExist operations
    */
   @Override
   public boolean blobExists(String container, String name) {
      try {
         String urlName = name.contains("/") ? URLEncoder.encode(name, Charsets.UTF_8.toString()) : name;
         return api.getObjectApi().objectExist(container, urlName);
      } catch (UnsupportedEncodingException e) {
         throw Throwables.propagate(e);
      }
   }

   /**
    * This supports multipart/related upload which has exactly 2 parts, media-part and metadata-part
    */
   @Override
   public String putBlob(String container, Blob blob) {
      checkNotNull(blob.getPayload().getContentMetadata().getContentLength(), "content length");
      HashCode md5 = blob.getMetadata().getContentMetadata().getContentMD5AsHashCode();

      ObjectTemplate template = blobMetadataToObjectTemplate.apply(blob.getMetadata());

      if (md5 != null) {
         template.md5Hash(md5);
      }
      return api.getObjectApi().multipartUpload(container, template, blob.getPayload()).getEtag();
   }

   /**Support multipart uploads if the PutOptions.multipart = true */
   @Override
   public String putBlob(String container, Blob blob, PutOptions options) {
      if (options.multipart().isMultipart()) {
         return multipartUploadStrategy.execute(container, blob);
      } else {
         return putBlob(container, blob);
      }
   }

   @Override
   public BlobMetadata blobMetadata(String container, String name) {
      return objectToBlobMetadata.apply(api.getObjectApi().getObject(container, name));
   }

   @Override
   public Blob getBlob(final String container, String name, org.jclouds.blobstore.options.GetOptions options) {
      final PayloadEnclosingImpl object = api.getObjectApi().download(container, name);

      GCSObject gcsObject = api.getObjectApi().getObject(container, name);
      if (gcsObject == null) {
         return null;
      }
     return new Function<GCSObject, Blob>() {

         @Override
         public Blob apply(GCSObject input) {
            Blob blob = new BlobBuilderImpl().payload(object.getPayload()).payload(object.getPayload())
                     .contentType(input.getContentType()).contentDisposition(input.getContentDisposition())
                     .contentEncoding(input.getContentEncoding()).contentLanguage(input.getContentLanguage())
                     .contentLength(input.getSize()).contentMD5(input.getMd5HashCode()).name(input.getName())
                     .userMetadata(input.getAllMetadata()).build();
            blob.getMetadata().setContainer(container);
            blob.getMetadata().setLastModified(input.getUpdated());
            blob.getMetadata().setETag(input.getEtag());
            blob.getMetadata().setPublicUri(input.getMediaLink());
            blob.getMetadata().setUserMetadata(input.getAllMetadata());
            blob.getMetadata().setUri(input.getSelfLink());
            blob.getMetadata().setId(input.getId());
            return blob;
         }
      }.apply(gcsObject); 
   }

   @Override
   public void removeBlob(String container, String name) {
      String urlName;
      try {
         urlName = name.contains("/") ? URLEncoder.encode(name, Charsets.UTF_8.toString()) : name;
      } catch (UnsupportedEncodingException uee) {
         throw Throwables.propagate(uee);
      }
      api.getObjectApi().deleteObject(container, urlName);
   }

   @Override
   protected boolean deleteAndVerifyContainerGone(String container) {
      ListPage<GCSObject> list = api.getObjectApi().listObjects(container);
      
      //if the list contain either objects or prefixes container can not be deleted 
      if ((list != null) && (list.iterator().hasNext() || !list.getPrefixes().isEmpty())){
         return false;
      }
      return api.getBucketApi().deleteBucket(container);
   }

   public Set<String> listPrefixes(String container, ListContainerOptions options) {
      ListObjectOptions gcsOptions = listContainerOptionsToListObjectOptions.apply(options);
      Set<String> prefixes = api.getObjectApi().listObjects(container, gcsOptions).getPrefixes();
      return prefixes;
   }

   public Set<String> listPrefixes(String container) {
      return listPrefixes(container, ListContainerOptions.NONE);
   }
}
