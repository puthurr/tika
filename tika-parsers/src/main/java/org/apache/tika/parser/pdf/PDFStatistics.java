package org.apache.tika.parser.pdf;

public class PDFStatistics {
    private int numberOfImages ;
    private int numberOfGraphics;

    // Specific treatment of jb2 images
    private int numberOfJB2Images;

    protected int getNumberOfImages() {
        return numberOfImages;
    }

    protected int getNumberOfGraphics() {
        return numberOfGraphics;
    }
    protected int getNumberOfJB2Images() {
        return numberOfJB2Images;
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

    protected void incrementJB2Counter()
    {
        this.numberOfJB2Images++;
    }
    protected void incrementJB2Counter(int increment)
    {
        this.numberOfJB2Images=this.numberOfJB2Images+increment;
    }
}
