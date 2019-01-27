// ScoreBoard.java
//
// A scoreboard program designed to run as an applet for use with
// the video system in the new Community Life Center to keep score
// for basketball, volleyball and wrestling.
//
// Written by Bery Rinaldo on December 26, 1999
// Updated and modified by Ryan Won on January 26, 2019
// Version 1 - April 17, 2002
// * Added GPL -- See gpl.txt and LICENSE.txt
// * Updated SlideShow to work properly with Java 2 v1.3.1
// Version 2 - April 20, 2002
// * Changed screen layout for bigger fonts, separate possession arrow area
//   includes change to size of slides to 640x190
// Version 2.1 - April 23, 2002
// * Changed possession indicator from "<" and ">" to circles like the bonus
//   indicator for readability.
// Version 2.2 - April 26, 2002
// * Add Home+2 and Guest+2 buttons
// * Add Clear Team Fouls button for end of half
// * Add StartTO (timeout) ClearTO buttons with input field for length
//   (secondary internal timer with buzz sound at end)
// Version 3.0 - April 27, 2002
// * Change screen layout from 640x480 to 800x600 to match configuration
//   of PC in the CLC (SongBase wants it like this, so we'll match).
// * Added volleyball mode.
// Version 3.1 - May 2, 2002
// * Fix screen updates for volleyball mode.
// Version 3.2 - May 3, 2002
// * Yet another fix screen updates for volleyball mode (this hack works ;-)
// * Added buttonFontSize parameter, use Helvetica Bold font.  Increased
//   default size of control window in ScoreBoard.txt
// Version 3.3 - May 6, 2002
// * Added announcements.txt capability to slide show
// Version 3.4 - May 12, 2002
// * Added undo last foul and re-init slide show (re-read announcements file)
// Version 3.5 - January 6, 2009
// * Added maxPeriods option in the ScoreBoard.txt and ScoreBoard.java to
//   allow for 6 period games (Thanks for the suggestion, Roger).
//
// Version 4 - January 26, 2019
// * Changed stuff
//
// Copyright (c) 1999-2009 Bery Rinaldo
// Copyright (c) 2019 Ryan Won

import java.util.*;
import java.text.*;
import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.net.URL;

public class ScoreBoard extends Applet implements Runnable, ActionListener {
    private Thread appletThread;

    String tagString = "ScoreBoard Version 3.5 - January 6, 2009";
    String copyString = "Copyright (C) 1999-2009 Bery Rinaldo";

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    URL hornSoundFile, beepSoundFile;
    AudioClip hornSound, beepSound;
    Timer scoreboardTimer;
    Timer timeoutTimer;
    String currentTime;
    Image scoreboardImage;
    ImageCanvas scoreboardImageCanvas;
    Frame windowFrame;
    Graphics scoreboardGraphics;
    Button startButton, stopButton, modeButton,
           homeuptwoButton, homedntwoButton, homesetButton,
           guestuptwoButton, guestdntwoButton, guestsetButton,
           sethomeButton, setguestButton, settimeButton,
           possesionButton, homeBonusButton, guestBonusButton,
           periodUpButton, periodOneButton, periodDnButton,
       homeupthreeButton, guestupthreeButton,
       homednthreeButton, guestdnthreeButton,
       homeTOButton, guestTOButton, resetTOButton,
       homeResetTOButton, guestResetTOButton,
	   startTOButton, clearTOButton,
           resetButton, undoLastButton, switchButton;
    boolean homeInBonus;
    boolean guestInBonus;
    int scoreHome, scoreGuest;
    int setsHome, setsGuest;
    int periodNumber;
    int maxPeriods;
    String nameHome, nameGuest;
    String tempName;
    TextField homeText, guestText, timerText, foulPlayerText,
              timeoutText, scoreText;
    int homeTimeouts;
    int guestTimeouts;
    // for undo capability
    int lastIndex = -1;
    String prevPlayer;
    int prevTeam;	
    int scoreboardMode;// Home = 0, Guest = 1

    Color bgColor;
    Color timeColor;
    int timeFontSize;
    Color lastMinuteTimeColor;
    Color scoreColor;
    Color homeNameColor;
    Color guestNameColor;
    int scoreFontSize;
    int buttonFontSize;
    Color textColor;
    Color fillColor;
    String preferredFont;
    int framePositionX;
    int framePositionY;
    int tempScore;
    int tempTimeouts;
    Color guestColor;
    String tempText;

    Image logoJBA;
    Image logoJesuwon;
    
    public void init() {

	System.out.println(tagString);
	System.out.println(copyString);
	System.out.println("");
	System.out.println("ScoreBoard comes with ABSOLUTELY NO WARRANTY. ");
	System.out.println("This is free software, and you are welcome");
	System.out.println("to redistribute it under certain conditions.");
    System.out.println("See the file gpl.txt for details.");
    System.out.println("Modified and updated by Ryan Won, January 2019");
    System.out.println("Used for noncommerical purposes for the Annual Jesuwon Basketball Tournament");

        getParamTags();
        logoJBA = toolkit.getImage("JBA CALEBv2.png");
        logoJesuwon = toolkit.getImage("jesuwon1.png");
        setupControlPanel();
        scoreboardImage = createImage(1920,1080);
        scoreboardGraphics = scoreboardImage.getGraphics();
        scoreboardImageCanvas = new ImageCanvas(scoreboardImage,this,1920,660);
        windowFrame = new Frame("Scoreboard");
        windowFrame.setLocation(framePositionX,framePositionY);
        windowFrame.setBackground(bgColor);
        windowFrame.setLayout(new BorderLayout());
        windowFrame.add("North", scoreboardImageCanvas);
        windowFrame.pack();
        homeNameColor = Color.RED;
        guestNameColor = Color.YELLOW;
        scoreboardTimer = new Timer(0);
        scoreboardTimer.start();
        timeoutTimer = new Timer(0);
        timeoutTimer.start();
        resetScoreboard();
        windowFrame.show();
        startSounds();
        transferFocus();
    }

    public void getParamTags() {
        String paramString;

        paramString = getParameter("bgColor");
        if (paramString != null) {
            bgColor = new Color(hexValue(paramString));
        } else {
            bgColor = Color.black;
        }
        paramString = getParameter("timeColor");
        if (paramString != null) {
            timeColor = new Color(hexValue(paramString));
        } else {
            timeColor = Color.yellow;
        }
        paramString = getParameter("lastMinuteTimeColor");
        if (paramString != null) {
            lastMinuteTimeColor = new Color(hexValue(paramString));
        } else {
            lastMinuteTimeColor = Color.red;
        }
        paramString = getParameter("scoreColor");
        if (paramString != null) {
            scoreColor = new Color(hexValue(paramString));
        } else {
            scoreColor = Color.green;
        }
        paramString = getParameter("textColor");
        if (paramString != null) {
            textColor = new Color(hexValue(paramString));
        } else {
            textColor = Color.yellow;
        }
        paramString = getParameter("fillColor");
        if (paramString != null) {
            fillColor = new Color(hexValue(paramString));
        } else {
            fillColor = Color.lightGray;
        }
        paramString = getParameter("preferredFont");
        if (paramString != null) {
            preferredFont = paramString;
        } else {
            preferredFont = "Helvetica";
        }
        paramString = getParameter("framePositionX");
        if (paramString != null) {
            framePositionX = intValue(paramString);
        } else {
            framePositionX = 340;
        }
        paramString = getParameter("framePositionY");
        if (paramString != null) {
            framePositionY = intValue(paramString);
        } else {
            framePositionY = 0;
        }
        paramString = getParameter("timeFontSize");
        if (paramString != null) {
            timeFontSize = intValue(paramString);
        } else {
            timeFontSize = 100;
        }
        paramString = getParameter("scoreFontSize");
        if (paramString != null) {
            scoreFontSize = intValue(paramString);
        } else {
            scoreFontSize = 90;
        }
        paramString = getParameter("buttonFontSize");
        if (paramString != null) {
            buttonFontSize = intValue(paramString);
        } else {
            buttonFontSize = 14;
        }
        paramString = getParameter("maxPeriods");
        if (paramString != null) {
            maxPeriods = intValue(paramString);
        } else {
            maxPeriods = 4;
        }
    }
     
    public void setupControlPanel() {
        Font buttonFont = new Font("Helvetica", Font.BOLD, buttonFontSize); 
        Font textFieldFont = new Font("Helvetica", Font.BOLD, 50);
        homeText = new TextField(10);
        homeText.setFont(textFieldFont);
        guestText = new TextField(10);
        guestText.setFont(textFieldFont);
        timerText = new TextField(10);
        timerText.setFont(textFieldFont);


        periodUpButton = new Button("Period+");
        periodUpButton.addActionListener(this);
	    periodUpButton.setFont(buttonFont);
        periodOneButton = new Button("Period=1");
        periodOneButton.addActionListener(this);
	    periodOneButton.setFont(buttonFont);
        periodDnButton = new Button("Period-");
        periodDnButton.addActionListener(this);
        periodDnButton.setFont(buttonFont);
        
        sethomeButton = new Button("Red");
        sethomeButton.addActionListener(this);
	    sethomeButton.setFont(buttonFont);

        setguestButton = new Button("Yellow");
        setguestButton.addActionListener(this);
	    setguestButton.setFont(buttonFont);

        settimeButton = new Button("Set Timer");
        settimeButton.addActionListener(this);
	    settimeButton.setFont(buttonFont);

        homeupthreeButton = new Button("Red Pt+3");
        homeupthreeButton.addActionListener(this);
	    homeupthreeButton.setFont(buttonFont);

        homeuptwoButton = new Button("Red Pt+2");
        homeuptwoButton.addActionListener(this);
	    homeuptwoButton.setFont(buttonFont);

        homednthreeButton = new Button("Red Pt-3");
        homednthreeButton.addActionListener(this);
        homednthreeButton.setFont(buttonFont);
        
        homedntwoButton = new Button("Red Pt-2");
        homedntwoButton.addActionListener(this);
	    homedntwoButton.setFont(buttonFont);

        homesetButton = new Button("Red Pt Reset");
        homesetButton.addActionListener(this);
	    homesetButton.setFont(buttonFont);

        startButton = new Button("Start");
        startButton.addActionListener(this);
        startButton.setEnabled(false);
        startButton.setFont(buttonFont);

        stopButton = new Button("Stop");
        stopButton.addActionListener(this);
        stopButton.setEnabled(false);
	    stopButton.setFont(buttonFont);

        guestupthreeButton = new Button("Yellow Pt+3");
        guestupthreeButton.addActionListener(this);
	    guestupthreeButton.setFont(buttonFont);

        guestuptwoButton = new Button("Yellow Pt+2");
        guestuptwoButton.addActionListener(this);
	    guestuptwoButton.setFont(buttonFont);

        guestdntwoButton = new Button("Yellow Pt-2");
        guestdntwoButton.addActionListener(this);
        guestdntwoButton.setFont(buttonFont);
        
        guestdnthreeButton = new Button("Yellow Pt-3");
        guestdnthreeButton.addActionListener(this);
	    guestdnthreeButton.setFont(buttonFont);

        guestsetButton = new Button("Yellow Pt Reset");
        guestsetButton.addActionListener(this);
        guestsetButton.setFont(buttonFont);
        
        homeTOButton = new Button("Red Timeout");
        homeTOButton.addActionListener(this);
        homeTOButton.setFont(buttonFont);

        guestTOButton = new Button("Yellow Timeout");
        guestTOButton.addActionListener(this);
        guestTOButton.setFont(buttonFont);

        homeResetTOButton = new Button("Red Timeout Reset");
        homeResetTOButton.addActionListener(this);
        homeResetTOButton.setFont(buttonFont);

        guestResetTOButton = new Button("Yellow Timeout Reset");
        guestResetTOButton.addActionListener(this);
        guestResetTOButton.setFont(buttonFont);

        startTOButton = new Button("Start Timeout");
        startTOButton.addActionListener(this);
        startTOButton.setFont(buttonFont);

        clearTOButton = new Button("Clear Timeout");
        clearTOButton.addActionListener(this);
        clearTOButton.setFont(buttonFont);

        resetButton = new Button("Reset");
        resetButton.addActionListener(this);
        resetButton.setFont(buttonFont);
        
        switchButton = new Button("Switch Sides");
        switchButton.addActionListener(this);
        switchButton.setFont(buttonFont);

        setLayout(new GridLayout(14,2,3,3));
        
        add(timerText);
        add(settimeButton);
        
        add(startButton);
        add(stopButton);

        add(homeText);
        add(guestText);

        add(sethomeButton);
        add(setguestButton);

        add(homeupthreeButton);
        add(guestupthreeButton);

        add(homeuptwoButton);
        add(guestuptwoButton);

        add(homedntwoButton);
        add(guestdntwoButton);

        add(homednthreeButton);
        add(guestdnthreeButton);

        add(homesetButton);
        add(guestsetButton);

        add(homeTOButton);
        add(guestTOButton);

        add(homeResetTOButton);
        add(guestResetTOButton);

        add(startTOButton);
        add(clearTOButton);

        add(periodUpButton);
        add(periodDnButton);

        add(resetButton);
        add(switchButton);

    }

    public void resetScoreboard() {
        scoreboardGraphics.setColor(fillColor);
        scoreboardGraphics.fillRect(0,0,1920,1080);
        scoreboardTimer.pause();
        scoreboardTimer.setTimer(0);
        paintTimer();
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        nameHome = "RED";
        homeNameColor = Color.RED;
        paintHomeName();
        nameGuest = "YELLOW";
        guestNameColor = Color.YELLOW;
        paintGuestName();
        scoreHome = 0;
        paintHomeScore();
        scoreGuest = 0;
        paintGuestScore();
        periodNumber = 1;
        paintPeriod();
        homeTimeouts = 3;
        guestTimeouts = 3;
        paintTimeouts();
        paintLogos();
        scoreboardImageCanvas.repaint();
    }

    private void startSounds() {
        try {
            hornSoundFile = new URL(getCodeBase(), "beep_long.wav");
        } catch (java.net.MalformedURLException e) {
            System.err.println("can't form beep_long.wav URL");
        }
        if (hornSoundFile != null) {
            hornSound = getAudioClip(hornSoundFile);
        }
        try {
            beepSoundFile = new URL(getCodeBase(), "beep.au");
        } catch (java.net.MalformedURLException e) {
            System.err.println("can't form beep.au URL");
        }
        if (beepSoundFile != null) {
            beepSound = getAudioClip(beepSoundFile);
        }
    }

    public synchronized void paintLogos() {
        scoreboardGraphics.drawImage(logoJBA, 20, 30, this);
        scoreboardImageCanvas.repaint(20, 30, 480, 279);
        scoreboardGraphics.drawImage(logoJesuwon, 1420, 74, this);
        scoreboardImageCanvas.repaint(1402, 74, 480, 192);
    }
    public synchronized void paintTimeouts() {
        String homeTOString;
        String guestTOString;

        homeTOString = homeTimeouts + "";
        guestTOString = guestTimeouts + "";

        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, 160));
        scoreboardGraphics.fillRoundRect(790, 850, 140, 180, 40, 40);
        scoreboardGraphics.setColor(Color.WHITE);
        scoreboardGraphics.drawString(homeTOString, 820, 1000);
        scoreboardImageCanvas.repaint(790, 850, 140, 180);

        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, 160));
        scoreboardGraphics.fillRoundRect(990, 850, 140, 180, 40, 40);
        scoreboardGraphics.setColor(Color.WHITE);
        scoreboardGraphics.drawString(guestTOString, 1020, 1000);
        scoreboardImageCanvas.repaint(990, 850, 140, 180);
    }
    public synchronized void paintTimeoutTime() {
        String sSec, sTim, sMil;
        int dMin, dMil, dSec, dTime = 0;

        dTime = timeoutTimer.timerValue;

        dMin = dTime / 600;
        dSec = (dTime / 10) % 60;
        dMil = dTime % 10;

        sSec = dSec < 10 ? "0" + dSec : "" + dSec;
        sMil = "" + dMil;
        sTim = ":" + sSec + "." + sMil;

        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, timeFontSize));

        scoreboardGraphics.fillRoundRect(520,20,880,300,40,40);
        scoreboardGraphics.setColor(lastMinuteTimeColor);
        scoreboardGraphics.drawString(sTim, 590, 280);
        scoreboardImageCanvas.repaint(520,20,880,300);
    }
    public synchronized void paintTimer() {
        String sMin, sSec, sMil, sTim;
        int dMin, dSec, dMil, dTime = 0;

        if (scoreboardTimer != null) dTime = scoreboardTimer.timerValue;

        dMin = dTime / 600;
        dSec = (dTime / 10) % 60;
        dMil = dTime % 10;

        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, timeFontSize));

        if (dMin > 0) {
          if (dMil == 0) {
            sMin = dMin < 10 ? "0" + dMin : "" + dMin;
            sSec = dSec < 10 ? "0" + dSec : "" + dSec;
            sTim = sMin + ":" + sSec;
            scoreboardGraphics.fillRoundRect(520,20,880,300,40,40);
            scoreboardGraphics.setColor(timeColor);
            scoreboardGraphics.drawString(sTim, 550, 280);
            scoreboardImageCanvas.repaint(520,20,880,300);
          }
        }
        else {
          sSec = dSec < 10 ? "0" + dSec : "" + dSec;
          sMil = "" + dMil;
          sTim = ":" + sSec + "." + sMil;
          scoreboardGraphics.fillRoundRect(520,20,880,300,40,40);
          scoreboardGraphics.setColor(lastMinuteTimeColor);
          scoreboardGraphics.drawString(sTim, 590, 280);
          scoreboardImageCanvas.repaint(520,20,880,300);
        }
    }

    public synchronized void paintPeriod() { 
        String periodString;

        if (periodNumber <= maxPeriods) {
            periodString = periodNumber + "";
            scoreboardGraphics.setColor(bgColor);
            scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, 300));
            scoreboardGraphics.fillRoundRect(835,530,250,300,40,40);
            scoreboardGraphics.setColor(Color.WHITE);
            scoreboardGraphics.drawString(periodString, 880, 780);
            scoreboardImageCanvas.repaint(835,530,250,300);
        } else {
            periodString = "OT";
            scoreboardGraphics.setColor(bgColor);
            scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, 175));
            scoreboardGraphics.fillRoundRect(835,530,250,300,40,40);
            scoreboardGraphics.setColor(Color.WHITE);
            scoreboardGraphics.drawString(periodString, 835, 750);
            scoreboardImageCanvas.repaint(835,530,250,300);
	    }
    }

    public synchronized void paintHomeName() {
        int fontSize = 150;
        FontMetrics fontInfo;
        int nameWidth;
        
        scoreboardGraphics.setColor(homeNameColor);
        scoreboardGraphics.fillRoundRect(20,340,920,170,40,40);
        scoreboardGraphics.setColor(Color.BLACK);
        do {
            scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, fontSize));
            fontInfo = scoreboardGraphics.getFontMetrics();
            nameWidth = fontInfo.stringWidth(nameHome);
            fontSize--;
        } while (nameWidth > 900);
        scoreboardGraphics.drawString(nameHome,(480-(nameWidth/2)), (400+(fontSize/2)));
        scoreboardImageCanvas.repaint(20,340,920,150);
    }

    public synchronized void paintGuestName() { 
        int fontSize = 150;
        FontMetrics fontInfo;
        int nameWidth;

        scoreboardGraphics.setColor(guestNameColor);
        scoreboardGraphics.fillRoundRect(980,340,920,170,40,40);
        scoreboardGraphics.setColor(Color.BLACK);
        do {
            scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, fontSize));
            fontInfo = scoreboardGraphics.getFontMetrics();
            nameWidth = fontInfo.stringWidth(nameGuest);
            fontSize--;
        } while (nameWidth > 900);
        scoreboardGraphics.drawString(nameGuest,(1440-(nameWidth/2)), (400+(fontSize/2)));
        scoreboardImageCanvas.repaint(980,340,920,150);
    }

    public synchronized void paintHomeScore() { 
        String score = scoreHome < 10 ? "0" + scoreHome : "" + scoreHome;
	int scoreWidth;
	FontMetrics fontInfo;

        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.fillRoundRect(20,530,750,500,40,40);
        scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, scoreFontSize));
        scoreboardGraphics.setColor(scoreColor);
	    fontInfo = scoreboardGraphics.getFontMetrics();
	    scoreWidth = fontInfo.stringWidth(score);
        scoreboardGraphics.drawString(score, (400 - (scoreWidth/2)), 990);
        scoreboardImageCanvas.repaint(20,530,750,500);
    }

    public synchronized void paintGuestScore() { 
        String score = scoreGuest < 10 ? "0" + scoreGuest : "" + scoreGuest;
	int scoreWidth;
	FontMetrics fontInfo;
        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.fillRoundRect(1150,530,750,500,40,40);
        scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, scoreFontSize));
        scoreboardGraphics.setColor(scoreColor);
	fontInfo = scoreboardGraphics.getFontMetrics();
	scoreWidth = fontInfo.stringWidth(score);
        scoreboardGraphics.drawString(score, (1530 - (scoreWidth/2)), 990);
        scoreboardImageCanvas.repaint(1150,530,750,500);
    }

    public synchronized void switchButton() {
        tempScore = scoreGuest;
        scoreGuest = scoreHome;
        scoreHome = tempScore;
        tempName = nameGuest;
        nameGuest = nameHome;
        nameHome = tempName;
        tempTimeouts = guestTimeouts;
        guestTimeouts = homeTimeouts;
        homeTimeouts = tempTimeouts;
        tempText = guestText.getText();
        guestText.setText(homeText.getText());
        homeText.setText(tempText);
        if (homeNameColor == Color.RED) {
            homeNameColor = Color.YELLOW;
            guestNameColor = Color.RED;
            sethomeButton.setLabel("Yellow");
            setguestButton.setLabel("Red");
            homeupthreeButton.setLabel("Yellow Pt +3");
            homeuptwoButton.setLabel("Yellow Pt +2");
            homedntwoButton.setLabel("Yellow Pt -2");
            homednthreeButton.setLabel("Yellow Pt -3");
            homesetButton.setLabel("Yellow Pt Reset");
            homeTOButton.setLabel("Yellow Timeout");
            homeResetTOButton.setLabel("Yellow Timeout Reset");
            guestupthreeButton.setLabel("Red Pt +3");
            guestuptwoButton.setLabel("Red Pt +2");
            guestdntwoButton.setLabel("Red Pt -2");
            guestdnthreeButton.setLabel("Red Pt -3");
            guestsetButton.setLabel("Red Pt Reset");
            guestTOButton.setLabel("Red Timeout");
            guestResetTOButton.setLabel("Red Timeout Reset");
        } else {
            homeNameColor = Color.RED;
            guestNameColor = Color.YELLOW;
            sethomeButton.setLabel("Red");
            setguestButton.setLabel("Yellow");
            homeupthreeButton.setLabel("Red Pt +3");
            homeuptwoButton.setLabel("Red Pt +2");
            homedntwoButton.setLabel("Red Pt -2");
            homednthreeButton.setLabel("Red Pt -3");
            homesetButton.setLabel("Red Pt Reset");
            homeTOButton.setLabel("Red Timeout");
            homeResetTOButton.setLabel("Red Timeout Reset");
            guestupthreeButton.setLabel("Yellow Pt +3");
            guestuptwoButton.setLabel("Yellow Pt +2");
            guestdntwoButton.setLabel("Yellow Pt -2");
            guestdnthreeButton.setLabel("Yellow Pt -3");
            guestsetButton.setLabel("Yellow Pt Reset");
            guestTOButton.setLabel("Yellow Timeout");
            guestResetTOButton.setLabel("Yellow Timeout Reset");
        }
        paintHomeName();
        paintGuestName();
        paintHomeScore();
        paintGuestScore();
        paintTimeouts();
}


    private int intValue(String str) {
        int returnValue;

        try {
            returnValue = (int)Integer.valueOf(str).intValue();
        } catch (java.lang.NumberFormatException e) {
            returnValue = 0;
        }
        return returnValue;
    }

    private int hexValue(String str) {
        int returnValue;

        try {
            returnValue = (int)Integer.valueOf(str,16).intValue();
        } catch (java.lang.NumberFormatException e) {
            returnValue = 0;
        }
        return returnValue;
    }

    private int convertTimeStringToInt(String timeString) { 
        int colon, dot, start;
        int returnValue = 0;

        start = 0;
        colon = timeString.indexOf(":");
        dot = timeString.indexOf(".");
        if (colon > 0) {
            returnValue += intValue(timeString.substring(0,colon)) * 600;
            start = colon + 1;
        }
        if (dot != -1) {
            returnValue += intValue(timeString.substring(start,dot)) * 10;
            returnValue += intValue(timeString.substring(dot+1));
        }
        else {
            returnValue += intValue(timeString.substring(start)) * 10;
        }
        return(returnValue);
    }



    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == settimeButton) {
	    scoreboardTimer.pause();
	    scoreboardTimer.setTimer(convertTimeStringToInt(timerText.getText()));
	    startButton.setEnabled(true);
	    stopButton.setEnabled(false);
	    paintTimer();
        } else if (source == sethomeButton) {
            nameHome = homeText.getText();
            paintHomeName();
        } else if (source == setguestButton) {
            nameGuest = guestText.getText();
            paintGuestName();
        } else if (source == startButton) {
            scoreboardTimer.cont();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            settimeButton.setEnabled(false);
        } else if (source == homeTOButton) {
            if (homeTimeouts > 0) {
                homeTimeouts--;
            }
            paintTimeouts();
        } else if (source == guestTOButton) {
                if (guestTimeouts > 0) {
                    guestTimeouts--;
                }
            paintTimeouts();
        } else if (source == homeResetTOButton) {
            homeTimeouts = 3;
            paintTimeouts();
        } else if (source == guestResetTOButton) {
            guestTimeouts = 3;
            paintTimeouts();
        } else if (source == startTOButton) {
            scoreboardTimer.pause();
            startButton.setEnabled(false);
            stopButton.setEnabled(false);
            startButton.setEnabled(false);
            timeoutTimer.pause();
            timeoutTimer.setTimer(convertTimeStringToInt("0:30"));
            paintTimeoutTime();
            timeoutTimer.cont();
            startTOButton.setEnabled(false);
            clearTOButton.setEnabled(true);
        } else if (source == clearTOButton) {
            timeoutTimer.pause();
            timeoutTimer.setTimer(0);
            startTOButton.setEnabled(true);
            clearTOButton.setEnabled(false);
            startButton.setEnabled(true);
            paintTimer();
            startButton.setEnabled(true);
        } else if (source == stopButton) {
            scoreboardTimer.pause();
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            settimeButton.setEnabled(true);
        } else if (source == resetButton) {
            resetScoreboard();
        } else if (source == switchButton) {
            switchButton();
        } else if (source == homeupthreeButton) {
            scoreHome += 3;
            paintHomeScore();
        } else if (source == homeuptwoButton) {
            scoreHome+=2;
            paintHomeScore();
        } else if (source == homedntwoButton) {
            if (scoreHome > 1) scoreHome -= 2;
            paintHomeScore();
        } else if (source == homednthreeButton) {
            if (scoreHome > 2) scoreHome -= 3;
            paintHomeScore();
        } else if (source == homesetButton) {
            scoreHome = intValue(scoreText.getText());
            paintHomeScore();
        } else if (source == guestuptwoButton) {
            scoreGuest += 2;
            paintGuestScore();
        } else if (source == guestupthreeButton) {
            scoreGuest += 3;
            paintGuestScore();
        } else if (source == guestdntwoButton) {
            if (scoreGuest > 1) scoreGuest -= 2;
            paintGuestScore();
        } else if (source == guestdnthreeButton) {
            if (scoreGuest > 2) scoreGuest -= 3;
            paintGuestScore();
        } else if (source == guestsetButton) {
            scoreGuest = intValue(scoreText.getText());
            paintGuestScore();
        } else if (source == periodOneButton) {
            periodNumber = 1;
            paintPeriod();
        } else if (source == periodUpButton) {
            if (periodNumber <= maxPeriods){
                periodNumber++;
                paintPeriod();
            }
        } else if (source == periodDnButton) {
            if (periodNumber > 1) periodNumber--;
            paintPeriod();
        }
    }

    public void paint() {
        scoreboardImageCanvas.repaint();
    }

    public void start() {
        if ((appletThread == null) || (!appletThread.isAlive())) {
            appletThread = new Thread(this);
        }
        appletThread.start();
    }

    public void run() {
        int lastScoreboardTimerValue = 0;
        int lastTimeoutTimerValue = 0;
	String lastTime = "";

        while (Thread.currentThread() == appletThread) {
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) { e.printStackTrace(); }

            if (lastTimeoutTimerValue != timeoutTimer.timerValue) {
                paintTimeoutTime();
                if (timeoutTimer.timerValue == 0) {
                    clearTOButton.setEnabled(false);
                    startTOButton.setEnabled(true);
                    startButton.setEnabled(true);
                    settimeButton.setEnabled(true);
                    if (beepSound != null) {
                        beepSound.play();
                    }
                }
            }
            if (lastScoreboardTimerValue != scoreboardTimer.timerValue) {
                paintTimer();
                if (scoreboardTimer.timerValue == 0) {
                    settimeButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    if (hornSound != null) {
                        hornSound.play();
                    }
                }
            }
	    paint(scoreboardGraphics);
            lastScoreboardTimerValue = scoreboardTimer.timerValue;
            lastTimeoutTimerValue = timeoutTimer.timerValue;
        }
    }

    public void stop() {
        if ((appletThread != null) && (appletThread.isAlive())) {
            // appletThread.stop();
            appletThread = null;
        }
    }

    public void destroy() {
        appletThread = null;
    }


    public void paint(Graphics g) {
        scoreboardGraphics.drawImage(scoreboardImage, 0, 0, this);
    }

    public String getAppletInfo() {
        return tagString + "\n" +
               copyString + "\n" +
               "A basketball scoreboard program";
    }

    public String[][] getParameterInfo() {
        String[][] info = {
            { "bgColor", "hexadecimal int", "the color under the text areas (default=black)."},
            { "timeColor", "hexadecimal int", "the color of the clock text (default=yellow)."},
            { "timeFontSize", "int", "the size of the time font (default=100)."},
            { "lastMinuteTimeColor", "hexadecimal int", "the color of the clock text under 1 minute (default=red)."},
            { "scoreColor", "hexadecimal int", "the color of the scores and team names (default=green)."},
            { "scoreFontSize", "int", "the size of the score font (default=90)."},
            { "textColor", "hexadecimal int", "the color of the other text (default=yellow)."},
            { "fillColor", "hexadecimal int", "the color between the text areas (default=lightGray)."},
            { "bonusLightColor", "hexadecimal int", "the color of the bonus lights (default=red)."},
            { "preferredFont", "String", "the font for all fields (default=Helvetica)."},
            { "framePositionX", "int", "the left-to-right position of the scoreboard (default 340)."},
            { "framePositionY", "int", "the top-to-bottom position of the scoreboard (default 0)."},
            { "buttonFontSize", "int", "the size of the button font (default=14)."},

        };
        return info;
    }
}

