package org.apache.tika.parser;

import org.apache.tika.mime.MimeTypes;

import java.io.Serializable;
import java.util.Locale;

public class AbstractParserConfig implements Serializable {

    // Define how to name an embeddded resource
    public final String EMBEDDED_RESOURCE_NAMING_FORMAT = "%05d-%05d";

    // Define how to name an embeddded image
    public final String EMBEDDED_IMAGE_NAMING_FORMAT = "image-"+EMBEDDED_RESOURCE_NAMING_FORMAT;

    /**
     * @param sourceNumber
     * @param imageNumber
     * @return
     */
    public String getImageName(int sourceNumber,int imageNumber)
    {
        return String.format(Locale.ROOT, EMBEDDED_IMAGE_NAMING_FORMAT, sourceNumber, imageNumber) ;
    }

    /**
     * @param sourceNumber
     * @param imageNumber
     * @param extension
     * @return
     */
    public String getImageFilename(int sourceNumber,int imageNumber,String extension)
    {
        if (extension.startsWith("."))
        {
            return String.format(Locale.ROOT, EMBEDDED_IMAGE_NAMING_FORMAT, sourceNumber, imageNumber) + extension;
        }
        else
        {
            return String.format(Locale.ROOT, EMBEDDED_IMAGE_NAMING_FORMAT, sourceNumber, imageNumber) + "." + extension;
        }
    }

    /**
     * @param prefix
     * @param sourceNumber
     * @param resourceNumber
     * @param extension
     * @return
     */
    public String getResourceFilename(String prefix, int sourceNumber,int resourceNumber,String extension)
    {
        if (extension.startsWith("."))
        {
            return prefix + "-"+ String.format(Locale.ROOT, EMBEDDED_RESOURCE_NAMING_FORMAT, sourceNumber, resourceNumber) + extension;
        }
        else
        {
            return prefix + "-"+ String.format(Locale.ROOT, EMBEDDED_RESOURCE_NAMING_FORMAT, sourceNumber, resourceNumber) + "." + extension;
        }
    }
}
