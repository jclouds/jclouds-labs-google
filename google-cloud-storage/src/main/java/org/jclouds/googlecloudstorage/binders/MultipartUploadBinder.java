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
package org.jclouds.googlecloudstorage.binders;

import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.googlecloudstorage.domain.templates.ObjectTemplate;
import org.jclouds.http.HttpRequest;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.MultipartForm;
import org.jclouds.io.payloads.Part;
import org.jclouds.io.payloads.StringPayload;
import org.jclouds.rest.MapBinder;

import com.google.gson.Gson;

import static com.google.common.base.Preconditions.checkNotNull;

public class MultipartUploadBinder implements MapBinder {

   private final String BOUNDARY_HEADER = "multipart_boundary";

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams)
            throws IllegalArgumentException {

      ObjectTemplate template = (ObjectTemplate) postParams.get("template");
      Payload payload = (Payload) postParams.get("payload");

      String contentType = checkNotNull(template.getContentType(), "contentType");
      Long length = checkNotNull(template.getSize(), "contetLength");

      StringPayload jsonPayload = Payloads.newStringPayload(new Gson().toJson(template));

      payload.getContentMetadata().setContentLength(length);

      Part jsonPart = Part.create("Metadata", jsonPayload,
               new Part.PartOptions().contentType(MediaType.APPLICATION_JSON));
      Part mediaPart = Part.create(template.getName(), payload, new Part.PartOptions().contentType(contentType));

      MultipartForm compPayload = new MultipartForm(BOUNDARY_HEADER, jsonPart, mediaPart);
      request.setPayload(compPayload);
      // HeaderPart
      request.toBuilder().replaceHeader(HttpHeaders.CONTENT_TYPE, "Multipart/related; boundary= " + BOUNDARY_HEADER)
               .build();
      return request;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return request;
   }
}
