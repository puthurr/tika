Clone of Apache Tika project
----------------------------

Apache Tika(TM) is a toolkit for detecting and extracting metadata and structured text content from various documents using existing parser libraries.

Apache Tika, Tika, Apache, the Apache feather logo, and the Apache Tika project logo are trademarks of The Apache Software Foundation.

Why this version ?
-----------------

For a Knowledge mining project, my team were looking to have a consistent representation of embedded images in XHTML output. 
To give you an example, the embedded images links for PowerPoint were missing but the images links for PDF are there.

This version is trying to harmonize the way embedded images are showing up in the XHTML in a nutshell.Once stabilized our plan is to propose our changes to the Apache Tika community.

Contact : puthurr@gmail.com

This version' features
----------------------
#### XHTML Tags

- PDF Page tags contains the page id.
```<div class="page" id="2">```
- PPTX : added a slide div with slide id and title (when available)
```<div class="slide" id="1">```
```<div class="slide" id="2" title="Oil Production">```
- PPT/PPTX slide-notes div renamed to slide-notes-content for consistency 
```<div class="slide-notes-content">```

#### Embedded Images XHTML tags
- Numbering 5 digits with padding left 0. 
- Representation in the XHTML for Office and PDF documents. 

**For PDF** 
- Original 
```
<img src="embedded:image0.jpg" alt="image0.jpg"/>
```
- Our version
```
<img src="image00000.jpg" alt="image00000.jpg" class="embedded" id="00000" 
    contenttype="image/jpeg" witdh="1491" height="2109" size="775380"/>
```
**For PowerPoint**
- Original 
```
<div class="embedded" id="slide6_rId3" />
```
- Our version 
```
<img class="embedded" id="slide5_rId4" contenttype="image/x-wmf" src="image00005.wmf" alt="image5.wmf" 
title="Picture 4" witdh="120" height="172" size="7092"/>
```

Some img attributes aren't HTML compliant we know. This above output is close to provide an HTML preview of any document. 

**Benefits**: we can scan big images, specific type of images, size in bytes or dimensions. 

#### PDF Parser new configurations

The new PDF parser configuration are all related to Image extraction thus they will take effects on calling the unpack endpoint. 
It means they will also requires the **extractInlineImages** option to be set to true as well. 

- **SinglePagePDFAsImage** : this instructs to convert a PDF with a single page to an image.
- **StripedImagesHandling** : this instructs to convert a PDF page into an image. Some PDF writers tend to stripe an image into multiple contents streams (Array) 
- **StripedImagesThreshold** : minimum number of contents streams to convert the page into an image
- **GraphicsToImage** : a page with graphics objets could be better represented with an image. 
- **GraphicsToImageThreshold** : minimum number of graphics objects to convert the page into an image. 

To leverage those features add the corresponding headers prefixed by X-Tika-PDF.

```--header "X-Tika-PDFextractInlineImages:true" --header "X-Tika-PDFSinglePagePDFAsImage:true"``` 

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

