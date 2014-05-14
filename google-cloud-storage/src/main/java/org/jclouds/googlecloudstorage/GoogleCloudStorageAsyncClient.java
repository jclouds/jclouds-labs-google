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

import static org.jclouds.blobstore.attr.BlobScopes.CONTAINER;
import static org.jclouds.googlecloudstorage.reference.GoogleCloudStorageConstants.STORAGE_FULLCONTROL_SCOPE;

import java.io.Closeable;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.attr.BlobScope;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.filters.OAuthAuthenticator;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.name.Named;

/**
 * Provides asynchronous access to GCS via their REST API.
 * <p/> * 
 * @author Bhathiya Supun
 */
@Deprecated
@RequestFilters(OAuthAuthenticator.class)
@BlobScope(CONTAINER)
public interface GoogleCloudStorageAsyncClient extends Closeable {

	/**
	 * https://developers.google.com/storage/docs/json_api/v1/buckets/insert
	 */
	@Named("BucketInsert")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	@OAuthScopes(STORAGE_FULLCONTROL_SCOPE)
	@MapBinder(BindToJsonPayload.class)
	ListenableFuture<Boolean> BucketInsert(@PayloadParam("name") String BucketName,
			@QueryParam("Project") String ProjectID);

}
