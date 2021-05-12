Clone of Apache Tika project
----------------------------

Apache Tika(TM) is a toolkit for detecting and extracting metadata and structured text content from various documents using existing parser libraries.

Apache Tika, Tika, Apache, the Apache feather logo, and the Apache Tika project logo are trademarks of The Apache Software Foundation.

Why this version ?
-----------------

For a Knowledge mining project, my team were looking to have a consistent representation of embedded images in XHTML output. 
To give you an example, the embedded images links for PowerPoint were missing while the images links for PDF were there.

This version is trying to harmonize the way embedded images are showing up in the XHTML in a nutshell. 

Once stabilized our plan is to propose our changes to the Apache Tika community.

Main contact : [puthurr@gmail.com](mailto:puthurr@gmail.com)

## Tika project

This project doesn't contain all Tika projects modules, only those necessary to run a tika server.

The tika server includes PDFBOX [additional components](https://pdfbox.apache.org/2.0/dependencies.html#optional-components). See tika-server pom.xml for actual dependencies. 

**Current Tika base Version is 1.26 (May 2021)**

## Added Features Set 

### Embedded Resources Naming consistency for Office and PDF

Extracting the embedded images of any document is a great feature. We implement a consistent images numbering format to identify quickly which page or slide a specific was referenced.

Format **image-(source)-(absolute image number).extension**

```java
    // Define how to name an embeddded resource
public final String EMBEDDED_RESOURCE_NAMING_FORMAT = "%05d-%05d";

// Define how to name an embeddded image
public final String EMBEDDED_IMAGE_NAMING_FORMAT = "image-"+EMBEDDED_RESOURCE_NAMING_FORMAT;
```

The final resource name for images would be

- image-00001-00001.png => first image of the document located on page/slide 1
- image-00004-00006.png => sixth image of the document located on page/slide 4 

### XHTML Tags

- PDF Page tags contains the page id.
```<div class="page" id="2">```
- PPTX : added a slide div with slide id and title (when available)
```<div class="slide" id="1">```
```<div class="slide" id="2" title="Oil Production">```
- PPT/PPTX slide-notes div renamed to slide-notes-content for consistency 
```<div class="slide-notes-content">```
  
### Embedded Images XHTML tags

Embedded representation in the XHTML for Office and PDF documents was diverse. 
Images are now represented with an image tag containing extra information like the size or type.
The fact to have images in W3C img tag allow us to work towards a standardized document preview. 

**For PDF** 
- Original 
```html
<img src="embedded:image0.jpg" alt="image0.jpg"/>
```
- Our version
```html
<img src="image-00001-00001.jpg" alt="image-00001-00001.jpg" class="embedded" id="image-00000-00001" contenttype="image/jpeg" size="775380" witdh="1491" height="2109"/>
```
**For PowerPoint**  
- Original 
```html
<div class="embedded" id="slide6_rId3" />
```
- Our version 
```html
<img class="embedded" id="slide6_rId3" contenttype="image/jpeg" src="image-00006-00006.jpeg" alt="image6.jpeg" title="Picture 58" witdh="271" height="280" size="26789"/>
```

Some img attributes aren't HTML compliant we know. This above output is close to provide an HTML preview of any document. 

**Benefits**: we can scan big images, specific type of images, size in bytes or dimensions. 

#### PDF Parser new configuration(s)

The new PDF parser configuration are all related to Image extraction thus they will take effects on calling the unpack endpoint. 
It means they will also requires the **extractInlineImages** option to be set to **true** as well. 

The below options goal is to validate if a PDF page is better off rendered as an image or not. The benefit of rendering a page as image is to: 

1. Workaround fragmented/striped images in PDF.
2. Capture graphical elements. 
3. Reduce the effect of the various scanning techniques.

##### New options

- **allPagesAsImages** : this instructs to convert any PDF page as image.
- **singlePagePDFAsImage** : this instructs to convert a single page PDF to an image.
- **stripedImagesHandling** : this instructs to convert a PDF page into an image based on the number of Contents Streams or images in a page. Some PDF writers tend to stripe an image into multiple contents streams (Array) 
- **stripedImagesThreshold** : minimum number of contents streams to convert the page into an image. Default is 5 content streams or images.
- **graphicsToImage** : a page with graphics objets could be better represented with an image. 
- **graphicsToImageThreshold** : minimum number of graphics objects to convert the page into an image. 

To leverage those features add the corresponding headers prefixed by **X-Tika-PDF**.

```--header "X-Tika-PDFextractInlineImages:true" --header "X-Tika-PDFsinglePagePDFAsImage:true"```

An image originating from the above processing options i.e. singlePagePDFAsImage, the resulting image name will hold a -99999 suffix 

```image-<page/slide number>-99999.png```

##### Changing the image resulting format

The extension of the resulting is taken from OcrImageFormatName which default to png. To change the extension 
```--header "X-Tika-PDFOcrImageFormatName:jpg"```

#### Office Parser new configuration(s)

- **IncludeSlideShowEmbeddedResources** : for PPT Office documents, by default, images are extracted at the slideshow level. Setting this flag to false extracts the images at the slide level. 

#### Azure Blob Storage support for unpacking (tika-server)
The unpack feature produces an archive response which you can expand and process. 
For documents containing a lot of high-res images, the **unpack** will hit some limitations like OOM.
To avoid hitting those potential limitations, support cloud storage and big-size documents, I implemented an azure-unpack resource to write any embedded resource directly into an Azure Storage container and directory. 

**Benefits** : no archive client expansion, network bandwidth reduced, handles documents with a lot of high-res images and more.  

See [tika-server](/tika-server) for more details on how to use that feature. 

TIKA: Getting Started
---------------------

Pre-built binaries of Apache Tika standalone applications are available
from https://tika.apache.org/download.html . Pre-built binaries of all the
Tika jars can be fetched from Maven Central or your favourite Maven mirror.

Tika is based on Java 8 and uses the [Maven 3](https://maven.apache.org) build system. 
To build Tika from source, use the following command in the main directory:

    mvn clean install

The build consists of a number of components, including a standalone runnable jar that you can use to try out Tika features. You can run it like this:

    java -jar tika-app/target/tika-app-*.jar --help
    
TIKA : License (see also LICENSE.txt)
------------------------------------

Collective work: Copyright 2011 The Apache Software Foundation.

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with this work for additional information regarding copyright ownership.  The ASF licenses this file to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at

<https://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.

Apache Tika includes a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the licenses listed in the LICENSE.txt file.

TIKA : Export control
---------------------

This distribution includes cryptographic software.  The country in which you currently reside may have restrictions on the import, possession, use, and/or re-export to another country, of encryption software.  BEFORE using any encryption software, please  check your country's laws, regulations and policies concerning the import, possession, or use, and re-export of encryption software, to  see if this is permitted.  See <http://www.wassenaar.org/> for more information.

The U.S. Government Department of Commerce, Bureau of Industry and Security (BIS), has classified this software as Export Commodity Control Number (ECCN) 5D002.C.1, which includes information security software using or performing cryptographic functions with asymmetric algorithms.  The form and manner of this Apache Software Foundation distribution makes it eligible for export under the License Exception ENC Technology Software Unrestricted (TSU) exception (see the BIS Export Administration Regulations, Section 740.13) for both object code and source code.

The following provides more details on the included cryptographic software:

Apache Tika uses the Bouncy Castle generic encryption libraries for extracting text content and metadata from encrypted PDF files.  See <http://www.bouncycastle.org/> for more details on Bouncy Castle.  

