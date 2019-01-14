// ImageCanvas.java
//
// Class to display an Image within a Frame.
//
// Adapted from ftp://ftp.javasoft.com/docs/tut-OLDui.zip
// 
// by Bery Rinaldo on December 26, 1999
//
// Version 1 - April 17, 2002
// * Added GPL -- See gpl.txt and LICENSE.txt
//
// Copyright (c) 1999-2009 Bery Rinaldo

import java.awt.*;

class ImageCanvas extends Canvas {
    Container pappy;
    Image image;
    Dimension size;
    int w, h;
    boolean trueSizeKnown;
    MediaTracker tracker;

    public ImageCanvas(Image image, Container highestContainer, 
                       int initialWidth, int initialHeight) {
        if (image == null) {
            System.err.println("Canvas got invalid image object!");
            return;
        }

        this.image = image;
        this.pappy = highestContainer;

        w = initialWidth;
        h = initialHeight;

        tracker = new MediaTracker(this);
        tracker.addImage(image, 0);

        size = new Dimension(w,h);
    }

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    public synchronized Dimension getMinimumSize() {
        return size;
    }

    public void update (Graphics g) {
        paint(g);
    }

    public void paint (Graphics g) {
        if (image != null) {
            if (!trueSizeKnown) {
                int imageWidth = image.getWidth(this);
                int imageHeight = image.getHeight(this);

                if (tracker.checkAll(true)) {
                    trueSizeKnown = true;
                    if (tracker.isErrorAny()) {
                        System.err.println("Error loading image: "
                                           + image);
                    }
                }

                //Component-initiated resizing.
                if (((imageWidth > 0) && (w != imageWidth)) ||
                    ((imageHeight > 0) && (h != imageHeight))) {
                    w = imageWidth;
                    h = imageHeight;
                    size = new Dimension(w,h);
                    setSize(w, h);
                    pappy.validate();
                }
            }
        }

        g.drawImage(image, 0, 0, this);
        g.drawRect(0, 0, w - 1, h - 1);
    }
}
