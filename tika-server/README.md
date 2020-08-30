# Apache Tika Server

https://cwiki.apache.org/confluence/display/TIKA/TikaJAXRS

Running
-------
```
$ java -jar tika-server/target/tika-server.jar --help
   usage: tikaserver
    -?,--help           this help message
    -h,--host <arg>     host name (default = localhost)
    -l,--log <arg>      request URI log level ('debug' or 'info')
    -p,--port <arg>     listen port (default = 9998)
    -s,--includeStack   whether or not to return a stack trace
                        if there is an exception during 'parse'
```
Running via Docker
------------------
Assuming you have Docker installed, you can use a prebuilt image:

`docker run -d -p 9998:9998 apache/tika`

This will load Apache Tika Server and expose its interface on:

`http://localhost:9998`

You may also be interested in the https://github.com/apache/tika-docker project
which provides prebuilt Docker images.

Installing as a Service on Linux
-----------------------
To run as a service on Linux you need to run the `install_tika_service.sh` script.

Assuming you have the binary distribution like `tika-server-1.24-bin.tgz`,
then you can extract the install script via:

`tar xzf tika-server-1.24-bin.tgz tika-server-1.24-bin/bin/install_tika_service.sh --strip-components=2`

and then run the installation process via:

`./install_tika_service.sh  ./tika-server-1.24-bin.tgz`


Usage
-----
Usage examples from command line with `curl` utility:

* Extract plain text:  
`curl -T price.xls http://localhost:9998/tika`

* Extract text with mime-type hint:  
`curl -v -H "Content-type: application/vnd.openxmlformats-officedocument.wordprocessingml.document" -T document.docx http://localhost:9998/tika`

* Get all document attachments as ZIP-file:  
`curl -v -T Doc1_ole.doc http://localhost:9998/unpack > /var/tmp/x.zip`

* Extract metadata to CSV format:  
`curl -T price.xls http://localhost:9998/meta`

* Detect media type from CSV format using file extension hint:  
`curl -X PUT -H "Content-Disposition: attachment; filename=foo.csv" --upload-file foo.csv http://localhost:9998/detect/stream`


HTTP Return Codes
-----------------
`200` - Ok  
`204` - No content (for example when we are unpacking file without attachments)  
`415` - Unknown file type  
`422` - Unparsable document of known type (password protected documents and unsupported versions like Biff5 Excel)  
`500` - Internal error  


Azure Blob Storage Support
----------------------------
Our version of Tika Server includes support for Azure Blob Storage for unpacking resources from documents. 
Tika's unpack implementation loads all resources im memory to serve a zip/tar archive in response. Documents with a lot of embedded images like scanned-pdf you will hit OOM.

To workaround this and support writing embedded resources directly in an Azure Blob storage, we added an azure-unpack endpoint i.e. **http://localhost:9998/azure-unpack**   

####Azure Blob Storage Connection 
Azure Blob connection string is taken from the environment variable **AZURE_STORAGE_CONNECTION_STRING**. 
[https://docs.microsoft.com/en-us/azure/storage/blobs/storage-quickstart-blobs-java](Azure Blob Storage - Java) 

####Azure Blob Target Container 
To specify which container is used to write all embedded resources to, send the header **X-TIKA-AZURE-CONTAINER** along with your azure-unpack query. 

####Azure Blob Target Container Directory
To specify which container directory to write all embedded resources to, send the header **X-TIKA-AZURE-CONTAINER-DIRECTORY** along with your azure-unpack query. 

####Azure Blob Metadata
Our implementation supports adding blob metadata to each embedded resource. To your azure-unpack request, any header with the prefix  **X-TIKA-AZURE-META-** will end up in the user-defined blob properties. 
Refer to the official documentation for limitations 
[https://docs.microsoft.com/en-us/azure/storage/blobs/storage-blob-properties-metadata?tabs=dotnet](Azure Blob Metadata)

####Usage

- Example 1

`curl -T PDF-01NCMKWBOVUAAXWBGMCVHLD72RR376GDAX.pdf http://localhost:9998/azure-unpack --header "X-Tika-PDFextractInlineImages:true" --header "X-TIKA-AZURE-CONTAINER:tika2" --header "X-TIKA-AZURE-CONTAINER-DIRECTORY:test2" --header "X-TIKA-AZURE-META-property1:test12" --header "X-TIKA-AZURE-META-property3:test34"`

The above command will extract embedded resources to the Azure Blob container **tika2** , in the **test2** directory. 
Each extracted resource will have 2 user-defined metadata property1:test12 & property3:test34

- Example 2
 
`curl -T 01NCMKWBOY74FZ5GAOOJBLJZ2MAHDINEK7.pptx http://localhost:9998/azure-unpack --header "X-TIKA-AZURE-CONTAINER:tika" --header "X-TIKA-AZURE-META-property1:test12" --header "X-TIKA-AZURE-META-property3:test34"`

The above command will extract embedded resources to the Azure Blob container **tika** in the root directory. 
Each extracted resource will have 2 user-defined metadata property1:test12 & property3:test34

### Implementation

Refer to the resource class **AzureUnpackerResource.class** 

Added POM Dependency
`    <!-- https://mvnrepository.com/artifact/com.azure/azure-storage-blob -->
    <dependency>
      <groupId>com.azure</groupId>
      <artifactId>azure-storage-blob</artifactId>
      <version>12.8.0</version>
    </dependency>
`