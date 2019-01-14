// SlideShow.java
//
// Slide show display for use with ScoreBoard.java
//
// Written by Bery Rinaldo on December 29, 1999
//
// Version 1 - April 17, 2002
// * Added GPL -- See gpl.txt and LICENSE.txt
// * Added codeBase and numSlides as parameters to make this work
//   under Java 2 v1.3.1
// Version 3.3 - May 6, 2002
// * Added announcements file capability
// Version 3.4 - May 12, 2002
// * Added reset to re-read text file/slides on the fly
//
// Copyright (c) 1999-2009 Bery Rinaldo

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.net.URL;

class SlideShow extends Canvas implements Runnable {
    int w, h;
    int preferredWidth, preferredHeight;
    Dimension size;
    boolean trueSizeKnown;
    boolean suspended = false;

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    int currentSlide;
    int numberOfSlides = 0;
    Container pappy;
    int frameNumber = 0;
    int delay = 15000;
    Thread animatorThread;
    Image clearImage;
    Graphics annGraphics;
    Image[] images;
    int[] delays;
    URL slideFileURL;
    URL announcementFileURL;
    String content = null;
    String saveContent = null;
    String title, message;
    int i;
    int fontSize;
    FontMetrics fontInfo;
    int textWidth;

    public SlideShow(Container highestContainer, URL codeBase, String imageDir, int numSlides, int delaySeconds,
                     int initialWidth, int initialHeight) {
        pappy = highestContainer;
        w = initialWidth;
        h = initialHeight;
        preferredWidth = w;
        preferredHeight = h;
        size = new Dimension(w,h);

        images = new Image[numberOfSlides+50];	// allow for 50 announcements
        delays = new int[numberOfSlides+50];

	if (delaySeconds > 0) {
            delay = delaySeconds * 1000;
        }
        try {
            announcementFileURL =
		new URL(codeBase, imageDir + "announcements.txt");
        } catch (java.net.MalformedURLException e) {;}
	reset(codeBase,imageDir,numSlides,delaySeconds);
    }

    public void reset(URL codeBase, String imageDir,
		      int numSlides, int delaySeconds) {
        Color saveBg = pappy.getBackground();

  	    numberOfSlides = 1;
        if (announcementFileURL != null) {
	    InputStream urlStream = null;
	    try {
	        urlStream = announcementFileURL.openStream();
	    } catch (java.io.IOException ie) {
		System.err.println("can't openStream");
	    }
	    if (urlStream != null) {
	        // first, read in the entire file (into String content)
	        byte b[] = new byte[1000];
	        int numRead = 0;
	        try {
	            numRead = urlStream.read(b);
	        } catch (java.io.IOException ie) {
		    System.err.println("can't read 1");
	        }
	        content = new String(b, 0, numRead);
	        while (numRead != -1) {
	            try {
		        numRead = urlStream.read(b);
	            } catch (java.io.IOException ie) {
		        System.err.println("can't read");
	            }
		    if (numRead != -1) {
		        String newContent = new String(b, 0, numRead);
		        content += newContent;
		    }
	        }
	        try {
	            urlStream.close();
	        } catch (java.io.IOException ie) {
		    System.err.println("can't close Stream");
	        }
	        // parse file line by line....
	        StringTokenizer st = new StringTokenizer(content, "\r\n");
	        String strLink;
	        while (st.hasMoreTokens() && numberOfSlides < 50) {
	            strLink = st.nextToken();
		    if (strLink.substring(0,4).compareTo("TITL") == 0) {
			pappy.setBackground(Color.black);
		        images[numberOfSlides] = pappy.createImage(w,h);
		        annGraphics = images[numberOfSlides].getGraphics();
		        title = strLink.substring(5);
		        annGraphics.setColor(Color.green);
		        fontSize = 45;
		        do {
		            annGraphics.setFont
				(new Font("Helvetica", Font.PLAIN, fontSize));
		            fontInfo = annGraphics.getFontMetrics();
		            textWidth = fontInfo.stringWidth(title);
		            fontSize--;
		        } while (textWidth > (w*0.9));
		        annGraphics.drawString(title, ((w/2)-(textWidth/2)), 50);
			delays[numberOfSlides] = delay;
		    } else if (strLink.substring(0,4).compareTo("MSG1") == 0) {
		        message = strLink.substring(5);
		        annGraphics.setColor(Color.yellow);
            
		        annGraphics.setFont
			    (new Font("Helvetica", Font.PLAIN, 35));
		        fontInfo = annGraphics.getFontMetrics();
		        textWidth = fontInfo.stringWidth(message);
		        annGraphics.drawString(message, ((w/2)-(textWidth/2)), 100);
		    } else if (strLink.substring(0,4).compareTo("MSG2") == 0) {
		        message = strLink.substring(5);
		        annGraphics.setColor(Color.yellow);
            
		        annGraphics.setFont
			    (new Font("Helvetica", Font.PLAIN, 35));
		        fontInfo = annGraphics.getFontMetrics();
		        textWidth = fontInfo.stringWidth(message);
		        annGraphics.drawString(message, ((w/2)-(textWidth/2)), 140);
		    } else if (strLink.substring(0,4).compareTo("MSG3") == 0) {
		        message = strLink.substring(5);
		        annGraphics.setColor(Color.yellow);
            
		        annGraphics.setFont
			    (new Font("Helvetica", Font.PLAIN, 35));
		        fontInfo = annGraphics.getFontMetrics();
		        textWidth = fontInfo.stringWidth(message);
		        annGraphics.drawString(message, ((w/2)-(textWidth/2)), 180);
		    } else if (strLink.substring(0,4).compareTo("MSG4") == 0) {
		        message = strLink.substring(5);
		        annGraphics.setColor(Color.yellow);
            
		        annGraphics.setFont
			    (new Font("Helvetica", Font.PLAIN, 35));
		        fontInfo = annGraphics.getFontMetrics();
		        textWidth = fontInfo.stringWidth(message);
		        annGraphics.drawString(message, ((w/2)-(textWidth/2)), 220);
		    } else if (strLink.substring(0,4).compareTo("TIME") == 0) {
			try {
		            delays[numberOfSlides] = (int)Integer.valueOf(strLink.substring(5)).intValue() * 1000;
		        } catch (java.lang.NumberFormatException e) {
			    delays[numberOfSlides] = delay;
		        }
			numberOfSlides++;
		    }
	        }
	    }
	}
	frameNumber = numberOfSlides - 1;
        for (i = 1; i <= numSlides; i++) {
            try {
                slideFileURL = new URL(codeBase, imageDir+i+".jpg");
            } catch (java.net.MalformedURLException e) {
                System.err.println("can't form slide URL " + i);
            }
            if (slideFileURL != null) {
                images[numberOfSlides] = toolkit.getImage(slideFileURL);
		delays[numberOfSlides] = delay;
                numberOfSlides++;
            }
	}
	numberOfSlides--;

        // create a blank slide
        pappy.setBackground(Color.black);
        clearImage = pappy.createImage(w,h);
        images[0] = clearImage;

        pappy.setBackground(saveBg);
	repaint();
    }

    public void pause() {
        if (animatorThread != null) {
            suspended = true;
        }
    }
    
    public void cont() {
        if (animatorThread != null) {
            frameNumber = (frameNumber + 1) % numberOfSlides;
            repaint();
            suspended = false;
        }
    }
    
    public void start() {
        //Start animating!
        if (animatorThread == null) {
            animatorThread = new Thread(this);
        }
        animatorThread.start();
    }

    public void stop() {
        //Stop the animating thread.
        animatorThread = null;
    }

    public void run() {
        //Just to be nice, lower this thread's priority
        //so it can't interfere with other processing going on.
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
 
        //This is the animation loop.
        while (Thread.currentThread() == animatorThread) {
            try {
                Thread.sleep(delays[frameNumber+1]);
            } catch (InterruptedException e) {
                break;
            }

            //Advance the animation frame.
            frameNumber = (frameNumber + 1) % numberOfSlides;

	    if (!suspended) {
                //Display it.
                repaint();
	    }
        }
    }

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    public synchronized Dimension getMinimumSize() {
        return size;
    }

    public synchronized void clear() {
        pause();
        frameNumber = -1;
        repaint();
    }
    

    public void update (Graphics g) {
        if (g != null) {
	    paint(g);
	}
    }

    public void paint (Graphics g) {
    	if (numberOfSlides > 0) {
	    // these cause flicker...don't do it!
	    // g.setColor(Color.black);
	    // g.fillRect(0, 0, preferredWidth, preferredHeight);
	    g.drawImage(images[frameNumber+1], 0, 0, this);
        }
    }

}
