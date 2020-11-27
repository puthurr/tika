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

package org.apache.tika.server.resource;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import org.apache.tika.server.HTMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.util.Map;

@Path("/azure-status")
public class AzureStatusResource {

    private static final Logger LOG = LoggerFactory.getLogger(AzureStatusResource.class);

    // AZURE
    private static final String AZURE_CONTAINER = "X-TIKA-AZURE-CONTAINER";
    private static final String AZURE_CONTAINER_DIRECTORY = "X-TIKA-AZURE-CONTAINER-DIRECTORY";

    // Retrieve the connection string for use with the application. The storage
    // connection string is stored in an environment variable on the machine
    // running the application called AZURE_STORAGE_CONNECTION_STRING. If the environment variable
    // is created after the application is launched in a console or with
    // Visual Studio, the shell or application needs to be closed and reloaded
    // to take the environment variable into account.
    private static String connectStr = System.getenv("AZURE_STORAGE_CONNECTION_STRING");

    /* Create a new BlobServiceClient with a connection string */
    private static BlobServiceClient blobServiceClient;

    private HTMLHelper html = new HTMLHelper();;

    @GET
    @Produces({"text/html"})
    public String status(
            InputStream is,
            @Context HttpHeaders httpHeaders,
            @Context UriInfo info
    ) throws Exception {

        StringBuffer h = new StringBuffer();
        html.generateHeader(h, "Apache Azure Status");

        h.append("<h2>Environment Variables</h2>");
        h.append("<ul>");
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            h.append("<li>"+envName+" : " +env.get(envName)+"</li>");
        }
        h.append("</ul>");

        h.append("<div>");
        try {

            h.append("<h2>Headers</h2>");
            h.append("<ul>");
            // Get the headers
            MultivaluedMap<String, String> headers = httpHeaders.getRequestHeaders();

            String containerName = headers.getFirst(AZURE_CONTAINER);
            h.append("<li>Container : " +containerName+"</li>");

            String containerDirectory = "";

            if ( headers.containsKey(AZURE_CONTAINER_DIRECTORY) )
            {
                containerDirectory = headers.getFirst((AZURE_CONTAINER_DIRECTORY));
                h.append("<li>Container Directory : " +containerDirectory+"</li>");
            }
            h.append("</ul>");

            if (connectStr != null) {
                blobServiceClient = new BlobServiceClientBuilder()
                        .connectionString(connectStr)
                        .buildClient();
            }

            if ( blobServiceClient == null )
            {
                h.append("<p>");
                h.append("<strong>Blob Service Client is null...</strong>");
                h.append(connectStr);
                h.append("</p>");
            }
            else
            {
                h.append("<p>Account Url "+blobServiceClient.getAccountUrl()+"</p>");
                h.append("<p>Account Name "+blobServiceClient.getAccountName()+"</p>");
            }

            /* Create a new container client */
            BlobContainerClient containerClient = null;

            try {

                containerClient = blobServiceClient.createBlobContainer(containerName);

            } catch (BlobStorageException ex) {
                // The container may already exist, so don't throw an error
                if (!ex.getErrorCode().equals(BlobErrorCode.CONTAINER_ALREADY_EXISTS)) {
                    h.append("<p>");
                    h.append(ex.getMessage());
                    h.append("</p>");
                }
                else
                {
                    containerClient=blobServiceClient.getBlobContainerClient(containerName);
                }
            }
        }
        catch (Exception e)
        {
            h.append("<p>");
            h.append(e.getMessage());
            h.append("</p>");
        }

        h.append("</div>");

        html.generateFooter(h);
        return h.toString();
    }
}
