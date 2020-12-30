package org.apache.tika.parser.pdf;

public class PDFStatistics {
    private int numberOfImages ;
    private int numberOfGraphics;

    protected int getNumberOfImages() {
        return numberOfImages;
    }

    protected int getNumberOfGraphics() {
        return numberOfGraphics;
    }

    protected void incrementImageCounter()
    {
        this.numberOfImages++;
    }
    protected void incrementImageCounter(int increment)
    {
        this.numberOfImages=this.numberOfImages+increment;
    }

    protected void incrementGraphicCounter()
    {
        this.numberOfGraphics++;
    }
    protected void incrementGraphicCounter(int increment)
    {
        this.numberOfGraphics=this.numberOfGraphics+increment;
    }

}
