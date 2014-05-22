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
package org.jclouds.googlecloudstorage.config;

import static org.jclouds.reflect.Reflection2.typeToken;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.date.DateService;
import org.jclouds.date.TimeStamp;
import org.jclouds.googlecloudstorage.GoogleCloudStorageClient;
import org.jclouds.googlecloudstorage.GoogleCloudStorageAsyncClient;
import org.jclouds.googlecloudstorage.filters.RequestAuthorizeSignature;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RequestSigner;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * Configures the GCS connection, including logging and http transport.
 * 
 * @author Bhathiya Supun
 */
@ConfiguresRestClient
public class GoogleCloudStorageRestClientModule<S extends GoogleCloudStorageClient, A extends GoogleCloudStorageAsyncClient>
		extends RestClientModule<S, A> {

	@SuppressWarnings("unchecked")
	public GoogleCloudStorageRestClientModule() {
		this(TypeToken.class.cast(typeToken(GoogleCloudStorageClient.class)), TypeToken.class
				.cast(typeToken(GoogleCloudStorageAsyncClient.class)));
	}

	protected GoogleCloudStorageRestClientModule(TypeToken<S> syncClientType, TypeToken<A> asyncClientType) {
		super(syncClientType, asyncClientType);
	}

	@Override
	protected void configure() {
		// TODO Auto-generated method stub
		super.configure();
		install(new GoogleCloudStorageParserModule());
		bindRequestSigner();
	}

	protected void bindRequestSigner() {
		bind(RequestAuthorizeSignature.class).in(Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	protected RequestSigner provideRequestSigner(RequestAuthorizeSignature in) {
		return in;
	}

	@Provides
	@TimeStamp
	protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
		return cache.get();
	}

	@Provides
	@TimeStamp
	@Singleton
	protected Supplier<String> provideTimeStampCache(@Named(Constants.PROPERTY_SESSION_INTERVAL) long seconds,
			final DateService dateService) {
		return Suppliers.memoizeWithExpiration(new Supplier<String>() {
			public String get() {
				return dateService.rfc822DateFormat();
			}
		}, seconds, TimeUnit.SECONDS);
	}
}
