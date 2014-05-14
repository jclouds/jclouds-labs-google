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

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.googlecloudstorage.reference.GoogleCloudStorageConstants.GCS_PROVIDER_NAME;
import static org.jclouds.googlecloudstorage.reference.GoogleCloudStorageConstants.OPERATION_COMPLETE_INTERVAL;
import static org.jclouds.googlecloudstorage.reference.GoogleCloudStorageConstants.OPERATION_COMPLETE_TIMEOUT;
import static org.jclouds.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.oauth.v2.config.OAuthProperties.SIGNATURE_OR_MAC_ALGORITHM;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.googlecloudstorage.GoogleCloudStorageApiMetadata;
import org.jclouds.googlecloudstorage.config.GoogleCloudStorageParserModule;
import org.jclouds.googlecloudstorage.config.OAuthModuleWithoutTypeAdapters;
import org.jclouds.oauth.v2.config.OAuthAuthenticationModule;

import org.jclouds.rest.internal.BaseHttpApiMetadata;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for api.
 */

public class GoogleCloudStorageApiMetadata extends BaseRestApiMetadata {

	/**
	 * @deprecated please use
	 *             {@code org.jclouds.ContextBuilder#buildClient(S3Client.class)}
	 *             as {@link GoogleCloudStorageAsyncClient} interface will be
	 *             removed in jclouds 1.7.
	 */
	@Deprecated
	public static final TypeToken<org.jclouds.rest.RestContext<? extends GoogleCloudStorageClient, ? extends GoogleCloudStorageAsyncClient>> CONTEXT_TOKEN = new TypeToken<org.jclouds.rest.RestContext<? extends GoogleCloudStorageClient, ? extends GoogleCloudStorageAsyncClient>>() {
		private static final long serialVersionUID = 1L;
	};

	@Override
	public Builder<?> toBuilder() {
		return new ConcreteBuilder().fromApiMetadata(this);
	}

	public GoogleCloudStorageApiMetadata() {
		this(new ConcreteBuilder());
	}

	protected GoogleCloudStorageApiMetadata(Builder<?> builder) {
		super(builder);
	}

	public static Properties defaultProperties() {
		Properties properties = BaseHttpApiMetadata.defaultProperties();

		properties.put("oauth.endpoint", "https://accounts.google.com/o/oauth2/token");
		properties.put(AUDIENCE, "https://accounts.google.com/o/oauth2/token");
		properties.put(SIGNATURE_OR_MAC_ALGORITHM, "RS256");
		properties.put(PROPERTY_SESSION_INTERVAL, 3600);
		properties.put(OPERATION_COMPLETE_INTERVAL, 500);
		properties.put(OPERATION_COMPLETE_TIMEOUT, 600000);
		return properties;
	}

   public abstract static class Builder<T extends Builder<T>> extends BaseRestApiMetadata.Builder<T> {

		@SuppressWarnings("deprecation")
		protected Builder() {
			this(GoogleCloudStorageClient.class, GoogleCloudStorageAsyncClient.class);
		}

   	protected Builder(Class<?> syncClient, Class<?> asyncClient) {
			super(syncClient, asyncClient);
			id(GCS_PROVIDER_NAME)
					.name("Google Cloud Storage Api ")
					.identityName("Email associated with the Google API client_id")
					.credentialName("Private key literal associated with the Google API client_id")
					.documentation(URI.create("https://developers.google.com/storage/docs/json_api"))
					.version("v1")
					.defaultEndpoint("https://www.googleapis.com/storage/v1")
					.defaultProperties(GoogleCloudStorageApiMetadata.defaultProperties())
					.view(typeToken(BlobStoreContext.class))
					.defaultModules(
							ImmutableSet.<Class<? extends Module>> builder().add(GoogleCloudStorageParserModule.class)
									.add(OAuthAuthenticationModule.class).add(OAuthModuleWithoutTypeAdapters.class)
									.build());

		}

		@Override
		public ApiMetadata build() {
			return new GoogleCloudStorageApiMetadata(this);
		}
	}

	private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
		@Override
		protected ConcreteBuilder self() {
			return this;
		}
	}

}
