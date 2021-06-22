package org.apache.tika.server.resource.azure;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import org.apache.commons.codec.binary.Base64;

import javax.ws.rs.core.MultivaluedMap;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class AbstractAzureResource {
    // AZURE
    protected static final String AZURE_STORAGE_CONNECTION_STRING = "AZURE_STORAGE_CONNECTION_STRING";

    protected static final String AZURE_CONTAINER = "X-TIKA-AZURE-CONTAINER";
    protected static final String AZURE_CONTAINER_DIRECTORY = "X-TIKA-AZURE-CONTAINER-DIRECTORY";
    protected static final String AZURE_CONTAINER_DIRECTORY_BASE64ENCODED = "X-TIKA-AZURE-CONTAINER-DIRECTORY-BASE64ENCODED";
    protected static final String AZURE_METADATA_PREFIX = "X-TIKA-AZURE-META-";

    // Retrieve the connection string for use with the application. The storage
    // connection string is stored in an environment variable on the machine
    // running the application called AZURE_STORAGE_CONNECTION_STRING. If the environment variable
    // is created after the application is launched in a console or with
    // Visual Studio, the shell or application needs to be closed and reloaded
    // to take the environment variable into account.
    protected static String connectStr = System.getenv(AZURE_STORAGE_CONNECTION_STRING);

    /* Create a new BlobServiceClient with a connection string */
    protected static BlobServiceClient blobServiceClient;

    protected void AcquireBlobServiceClient()
    {
        if (connectStr != null) {
            blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectStr)
                    .buildClient();
        }

    }

    protected BlobContainerClient AcquireBlobContainerClient(String containerName) throws BlobStorageException{

        BlobContainerClient containerClient = null;

        try {
            if ( blobServiceClient == null )
            {
                this.AcquireBlobServiceClient();
            }

            if ( blobServiceClient != null )
            {
                containerClient = blobServiceClient.createBlobContainer(containerName);
            }

        } catch (BlobStorageException ex) {
            // The container may already exist, so don't throw an error
            if (!ex.getErrorCode().equals(BlobErrorCode.CONTAINER_ALREADY_EXISTS)) {
                throw ex;
            }
            else
            {
                containerClient=blobServiceClient.getBlobContainerClient(containerName);
            }
        }
        return containerClient;
    }

    protected String GetContainer(MultivaluedMap<String, String> headers){
        String container = null;
        if ( headers.containsKey(AZURE_CONTAINER) )
        {
            container = headers.getFirst(AZURE_CONTAINER);
        }
        return container;
    }

    protected String GetContainerDirectory(MultivaluedMap<String, String> headers){
        String containerDirectory = null ;

        if ( headers.containsKey(AZURE_CONTAINER_DIRECTORY) )
        {
            containerDirectory = headers.getFirst(AZURE_CONTAINER_DIRECTORY);

            if ( headers.containsKey(AZURE_CONTAINER_DIRECTORY_BASE64ENCODED) ) {
                containerDirectory = new String(Base64.decodeBase64(containerDirectory.getBytes(StandardCharsets.UTF_8)),StandardCharsets.UTF_8);
            }
        }
        return containerDirectory;
    }

}
