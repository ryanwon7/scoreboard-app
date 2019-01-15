// ScoreBoard.java
//
// A scoreboard program designed to run as an applet for use with
// the video system in the new Community Life Center to keep score
// for basketball, volleyball and wrestling.
//
// Written by Bery Rinaldo on December 26, 1999
//
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
// Copyright (c) 1999-2009 Bery Rinaldo

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
           homeupButton, homednButton, homesetButton,
           guestupButton, guestdnButton, guestsetButton,
           sethomeButton, setguestButton, settimeButton,
           possesionButton, homeBonusButton, guestBonusButton,
           periodUpButton, periodOneButton, periodDnButton,
           homeFoulsButton, hornButton,
           guestFoulsButton, beepButton,
           clearAllFoulsButton, clearTeamFoulsButton,
	   homeuptwoButton, guestuptwoButton,
	   starttoButton, cleartoButton,
           resetButton, undoLastButton, resetSlidesButton,
           suspendSlidesButton, clearSlidesButton, resumeSlidesButton;
    boolean possesionArrow;
    boolean homeInBonus;
    boolean guestInBonus;
    int scoreHome, scoreGuest;
    int setsHome, setsGuest;
    int periodNumber;
    int maxPeriods;
    String nameHome, nameGuest;
    TextField homeText, guestText, timerText, foulPlayerText,
              timeoutText, scoreText;
    String homePlayers[] = new String[50];
    int homeFouls[] = new int[50];
    int totalHomeFouls;
    String guestPlayers[] = new String[50];
    int guestFouls[] = new int[50];
    int totalGuestFouls;
    String lastHomePlayer, lastGuestPlayer;
    int lastHomeFouls, lastGuestFouls;
    // for undo capability
    int lastIndex = -1;
    String prevPlayer;
    int prevFouls;
    int prevTeam;	// Home = 0, Guest = 1

    Color bgColor;
    Color timeColor;
    int timeFontSize;
    Color lastMinuteTimeColor;
    Color scoreColor;
    int scoreFontSize;
    int buttonFontSize;
    Color textColor;
    Color fillColor;
    Color bonusLightColor;
    Color possesionArrowColor;
    String preferredFont;
    String slideShowDir;
    int slideDelay;    
    int numSlides;    
    SlideShow scoreboardSlides;
    int framePositionX;
    int framePositionY;
    int scoreboardMode;	// 0 = Basketball, 1 = Volleyball
    
    public void init() {

	System.out.println(tagString);
	System.out.println(copyString);
	System.out.println("");
	System.out.println("ScoreBoard comes with ABSOLUTELY NO WARRANTY. ");
	System.out.println("This is free software, and you are welcome");
	System.out.println("to redistribute it under certain conditions.");
	System.out.println("See the file gpl.txt for details.");

        getParamTags();
        setupControlPanel();
        scoreboardImage = createImage(1920,660);
        scoreboardGraphics = scoreboardImage.getGraphics();
        scoreboardImageCanvas = new ImageCanvas(scoreboardImage,this,1920,660);
        windowFrame = new Frame("Scoreboard");
        windowFrame.setLocation(framePositionX,framePositionY);
        windowFrame.setBackground(bgColor);
        windowFrame.setLayout(new BorderLayout());
        windowFrame.add("North", scoreboardImageCanvas);
        if (slideShowDir != null) {
            scoreboardSlides = new SlideShow(this,getCodeBase(),slideShowDir,numSlides,slideDelay,1920,400);
            windowFrame.add("South", scoreboardSlides);
            scoreboardSlides.start();
        }
        windowFrame.pack();
        scoreboardTimer = new Timer(0);
        scoreboardTimer.start();
        timeoutTimer = new Timer(0);
        timeoutTimer.start();
	    scoreboardMode = 0;
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
        paramString = getParameter("bonusLightColor");
        if (paramString != null) {
            bonusLightColor = new Color(hexValue(paramString));
        } else {
            bonusLightColor = Color.red;
        }
        paramString = getParameter("possesionArrowColor");
        if (paramString != null) {
            possesionArrowColor = new Color(hexValue(paramString));
        } else {
            possesionArrowColor = Color.red;
        }
        paramString = getParameter("preferredFont");
        if (paramString != null) {
            preferredFont = paramString;
        } else {
            preferredFont = "Helvetica";
        }
        paramString = getParameter("slideDelay");
        if (paramString != null) {
            slideDelay = intValue(paramString);
        } else {
            slideDelay = 15;
        }
        slideShowDir = getParameter("slideShowDir");
        paramString = getParameter("numSlides");
        if (paramString != null) {
            numSlides = intValue(paramString);
        } else {
            numSlides = 3;
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
        homeText = new TextField(10);
        guestText = new TextField(10);
        timerText = new TextField(10);
        foulPlayerText = new TextField(10);
        timeoutText = new TextField(10);
        scoreText = new TextField(10);

        homeFoulsButton = new Button("Home Foul");
        homeFoulsButton.addActionListener(this);
	    homeFoulsButton.setFont(buttonFont);
        hornButton = new Button("Horn");
        hornButton.addActionListener(this);
	    hornButton.setFont(buttonFont);
        guestFoulsButton = new Button("Guest Foul");
        guestFoulsButton.addActionListener(this);
	    guestFoulsButton.setFont(buttonFont);
        beepButton = new Button("Beep");
        beepButton.addActionListener(this);
	    beepButton.setFont(buttonFont);
        clearAllFoulsButton = new Button("Clear All Fouls");
        clearAllFoulsButton.addActionListener(this);
	    clearAllFoulsButton.setFont(buttonFont);
        clearTeamFoulsButton = new Button("Clear Team Fouls");
        clearTeamFoulsButton.addActionListener(this);
	    clearTeamFoulsButton.setFont(buttonFont);

        homeBonusButton = new Button("Home Bonus");
        homeBonusButton.addActionListener(this);
	    homeBonusButton.setFont(buttonFont);
        guestBonusButton = new Button("Guest Bonus");
        guestBonusButton.addActionListener(this);
	    guestBonusButton.setFont(buttonFont);

        periodUpButton = new Button("Period+");
        periodUpButton.addActionListener(this);
	    periodUpButton.setFont(buttonFont);
        periodOneButton = new Button("Period=1");
        periodOneButton.addActionListener(this);
	    periodOneButton.setFont(buttonFont);
        periodDnButton = new Button("Period-");
        periodDnButton.addActionListener(this);
	    periodDnButton.setFont(buttonFont);

        possesionButton = new Button("Poss Arrow");
        possesionButton.addActionListener(this);
	    possesionButton.setFont(buttonFont);

        sethomeButton = new Button("Hoame");
        sethomeButton.addActionListener(this);
	    sethomeButton.setFont(buttonFont);

        setguestButton = new Button("Guest");
        setguestButton.addActionListener(this);
	    setguestButton.setFont(buttonFont);

        settimeButton = new Button("Set Timer");
        settimeButton.addActionListener(this);
	    settimeButton.setFont(buttonFont);

        homeuptwoButton = new Button("Home Pt+2");
        homeuptwoButton.addActionListener(this);
	    homeuptwoButton.setFont(buttonFont);

        homeupButton = new Button("Home Pt+");
        homeupButton.addActionListener(this);
	    homeupButton.setFont(buttonFont);

        homednButton = new Button("Home Pt-");
        homednButton.addActionListener(this);
	    homednButton.setFont(buttonFont);

        homesetButton = new Button("Home Pt=");
        homesetButton.addActionListener(this);
	    homesetButton.setFont(buttonFont);

        starttoButton = new Button("Start TO");
        starttoButton.addActionListener(this);
        starttoButton.setEnabled(true);
	    starttoButton.setFont(buttonFont);

        cleartoButton = new Button("Clear TO");
        cleartoButton.addActionListener(this);
        cleartoButton.setEnabled(false);
	    cleartoButton.setFont(buttonFont);

        startButton = new Button("Start");
        startButton.addActionListener(this);
        startButton.setEnabled(false);
	    startButton.setFont(buttonFont);

        stopButton = new Button("Stop");
        stopButton.addActionListener(this);
        stopButton.setEnabled(false);
	    stopButton.setFont(buttonFont);

        modeButton = new Button("Volleyball");
        modeButton.addActionListener(this);
	    modeButton.setFont(buttonFont);

        guestuptwoButton = new Button("Guest Pt+2");
        guestuptwoButton.addActionListener(this);
	    guestuptwoButton.setFont(buttonFont);

        guestupButton = new Button("Guest Pt+");
        guestupButton.addActionListener(this);
	    guestupButton.setFont(buttonFont);

        guestdnButton = new Button("Guest Pt-");
        guestdnButton.addActionListener(this);
	    guestdnButton.setFont(buttonFont);

        guestsetButton = new Button("Guest Pt=");
        guestsetButton.addActionListener(this);
	    guestsetButton.setFont(buttonFont);

        suspendSlidesButton = new Button("Pause Slides");
        suspendSlidesButton.addActionListener(this);
	    suspendSlidesButton.setFont(buttonFont);
        clearSlidesButton = new Button("Clear Slide");
        clearSlidesButton.addActionListener(this);
	    clearSlidesButton.setFont(buttonFont);
        resumeSlidesButton = new Button("Start Slides");
        resumeSlidesButton.addActionListener(this);
	    resumeSlidesButton.setFont(buttonFont);
        resetSlidesButton = new Button("Reset Slides");
        resetSlidesButton.addActionListener(this);
	    resetSlidesButton.setFont(buttonFont);

        resetButton = new Button("Reset");
        resetButton.addActionListener(this);
	    resetButton.setFont(buttonFont);

        undoLastButton = new Button("Undo Last Foul");
        undoLastButton.addActionListener(this);
	    undoLastButton.setFont(buttonFont);

        setLayout(new GridLayout(13,3,3,3)); // 13 rows, 3 cols, 3 pixel gaps

        add(homeText);
        add(timerText);
        add(guestText);

        add(sethomeButton);
        add(settimeButton);
        add(setguestButton);

        add(homeuptwoButton);
        add(startButton);
        add(guestuptwoButton);

        add(homeupButton);
        add(stopButton);
        add(guestupButton);

        add(homednButton);
        add(possesionButton);
        add(guestdnButton);

        add(homesetButton);
        add(scoreText);
        add(guestsetButton);

        add(homeBonusButton);
        add(clearTeamFoulsButton);
        add(guestBonusButton);

        add(homeFoulsButton);
        add(foulPlayerText);
        add(guestFoulsButton);

        add(periodUpButton);
        add(clearAllFoulsButton);
        add(periodDnButton);

        add(starttoButton);
        add(timeoutText);
        add(cleartoButton);

        add(hornButton);
        add(modeButton);
        add(beepButton);

	add(resetButton);
	add(undoLastButton);
        
        if (slideShowDir != null) {
	    add(resetSlidesButton);

            add(suspendSlidesButton);
            add(clearSlidesButton);
            add(resumeSlidesButton);
        }

    }

    public void resetScoreboard() {
        scoreboardGraphics.setColor(fillColor);
        scoreboardGraphics.fillRect(0,0,1920,660);
        scoreboardTimer.pause();
        scoreboardTimer.setTimer(0);
        timeoutTimer.pause();
        timeoutTimer.setTimer(0);
        paintTimer();
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        nameHome = "HOME";
        paintHomeName();
        nameGuest = "GUEST";
        paintGuestName();
        scoreHome = 0;
        paintHomeScore();
        scoreGuest = 0;
        paintGuestScore();
        possesionArrow = false;
        //paintPossesionArrow();
        periodNumber = 1;
        //paintPeriod();
        homeInBonus = false;
        guestInBonus = false;
        //paintBonus();
        setsHome = 0;
        homePlayers[0] = null;
        totalHomeFouls = 0;
        lastHomePlayer = null;
        setsGuest = 0;
        guestPlayers[0] = null;
        totalGuestFouls = 0;
        lastGuestPlayer = null;
        prevPlayer = null;
        prevTeam = -1;
        //paintFoulsSets();
        scoreboardImageCanvas.repaint();
    }

    private void startSounds() {
        try {
            hornSoundFile = new URL(getCodeBase(), "horn.au");
        } catch (java.net.MalformedURLException e) {
            System.err.println("can't form horn.au URL");
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

    public synchronized void paintTimer() {
        String sMin, sSec, sMil, sTim;
        int dMin, dSec, dMil, dTime = 0;

      if (scoreboardMode == 1) {
        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.fillRoundRect(520,190,880,300,40,40);
        scoreboardImageCanvas.repaint(520,190,880,300);
      } else {
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
            scoreboardGraphics.fillRoundRect(530,190,880,300,40,40);
            scoreboardGraphics.setColor(timeColor);
            scoreboardGraphics.drawString(sTim, 550, 450);
            scoreboardImageCanvas.repaint(520,190,880,300);
          }
        }
        else {
          sSec = dSec < 10 ? "0" + dSec : "" + dSec;
          sMil = "" + dMil;
          sTim = ":" + sSec + "." + sMil;
          scoreboardGraphics.fillRoundRect(520,190,880,300,40,40);
          scoreboardGraphics.setColor(lastMinuteTimeColor);
          scoreboardGraphics.drawString(sTim, 590, 450);
          scoreboardImageCanvas.repaint(520,190,880,300);
        }
      }
    }

    public synchronized void paintPeriod() { 
        String periodString;
	int lrPos = 737;

	if (scoreboardMode == 1) {
            periodString = "GAME " + periodNumber;
	    lrPos = 780;
	} else {
            if (periodNumber <= maxPeriods) {
                periodString = "PERIOD " + periodNumber;
            } else {
                periodString = "PERIOD OT";
	        lrPos = 699;
                if (periodNumber > (maxPeriods+1)) {
		    lrPos = 672;
                    periodString += (periodNumber - maxPeriods);
                }
	    }
        }
        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, 40));

        scoreboardGraphics.fillRoundRect(540,315,850,90,20,20);
        scoreboardGraphics.setColor(textColor);
        scoreboardGraphics.drawString(periodString, lrPos, 516);
        scoreboardImageCanvas.repaint(540,315,850,90);
    }

    public synchronized void paintBonus() { 
        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, 90));

        scoreboardGraphics.fillRoundRect(520,510,880,130,40,40);
        if (timeoutTimer != null && timeoutTimer.timerValue > 0) {
	    String sSec, sTim;
	    int dMin, dSec, dTime = 0;

            dTime = timeoutTimer.timerValue;

	    dMin = dTime / 600;
	    dSec = (dTime / 10) % 60;

	    sSec = dSec < 10 ? "0" + dSec : "" + dSec;
	    sTim = dMin + ":" + sSec;
	    scoreboardGraphics.setColor(textColor);
	    scoreboardGraphics.drawString("TIMEOUT", 600, 598);
	    scoreboardGraphics.drawString(sTim, 1130, 598);
	} else if (scoreboardMode == 0) {
            if (timeoutTimer != null && timeoutTimer.timerValue > 0) {
		String sSec, sTim;
		int dMin, dSec, dTime = 0;

        	dTime = timeoutTimer.timerValue;

		dMin = dTime / 600;
		dSec = (dTime / 10) % 60;

		sSec = dSec < 10 ? "0" + dSec : "" + dSec;
		sTim = dMin + ":" + sSec;
		scoreboardGraphics.setColor(textColor);
		scoreboardGraphics.drawString("TIMEOUT", 600, 598);
		scoreboardGraphics.drawString(sTim, 1130, 598);
	    ///} else {
                //scoreboardGraphics.setColor(textColor);
                //scoreboardGraphics.drawString("BONUS", 811, 598);
                //scoreboardGraphics.setColor(bonusLightColor);
                //if (homeInBonus == true) {
                //    scoreboardGraphics.fillOval(564,549,60,60);
                //}
                //if (guestInBonus == true) {
                    //scoreboardGraphics.fillOval(1284,549,60,60);
                //}
	    }
	}
        scoreboardImageCanvas.repaint(520,510,880,130);
    }

    public synchronized void paintHomeName() {
        int fontSize = 150;
        FontMetrics fontInfo;
        int nameWidth;
        
        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.fillRoundRect(20,20,920,150,20,20);
        scoreboardGraphics.setColor(scoreColor);
        do {
            scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, fontSize));
            fontInfo = scoreboardGraphics.getFontMetrics();
            nameWidth = fontInfo.stringWidth(nameHome);
            fontSize--;
        } while (nameWidth > 900);
        if (nameHome.contains("y") || nameHome.contains("g") || nameHome.contains("p") || nameHome.contains("q")) {
            scoreboardGraphics.drawString(nameHome, (480-(nameWidth/2)), (60+(fontSize/2)));
        } else{
            scoreboardGraphics.drawString(nameHome,(480-(nameWidth/2)), (70+(fontSize/2)));
        }
        scoreboardImageCanvas.repaint(20,20,920,150);
    }

    public synchronized void paintGuestName() { 
        int fontSize = 150;
        FontMetrics fontInfo;
        int nameWidth;
        
        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.fillRoundRect(980,20,920,150,20,20);
        scoreboardGraphics.setColor(scoreColor);
        do {
            scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, fontSize));
            fontInfo = scoreboardGraphics.getFontMetrics();
            nameWidth = fontInfo.stringWidth(nameGuest);
            fontSize--;
        } while (nameWidth > 900);
        if (nameGuest.contains("y") || nameGuest.contains("g") || nameGuest.contains("p") || nameGuest.contains("q")) {
            scoreboardGraphics.drawString(nameGuest, (1440-(nameWidth/2)), (60+(fontSize/2)));
        } else{
            scoreboardGraphics.drawString(nameGuest,(1440-(nameWidth/2)), (70+(fontSize/2)));
        }
        scoreboardImageCanvas.repaint(980,20,920,150);
    }

    public synchronized void paintHomeScore() { 
        String score = scoreHome < 10 ? "0" + scoreHome : "" + scoreHome;
	int scoreWidth;
	FontMetrics fontInfo;

        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.fillRoundRect(20,190,480,450,20,20);
        scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, scoreFontSize));
        scoreboardGraphics.setColor(scoreColor);
	    fontInfo = scoreboardGraphics.getFontMetrics();
	    scoreWidth = fontInfo.stringWidth(score);
        scoreboardGraphics.drawString(score, (260 - (scoreWidth/2)), 550);
        scoreboardImageCanvas.repaint(20,190,480,450);
    }

    public synchronized void paintGuestScore() { 
        String score = scoreGuest < 10 ? "0" + scoreGuest : "" + scoreGuest;
	int scoreWidth;
	FontMetrics fontInfo;

        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.fillRoundRect(1420,190,480,450,20,20);
        scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, scoreFontSize));
        scoreboardGraphics.setColor(scoreColor);
	fontInfo = scoreboardGraphics.getFontMetrics();
	scoreWidth = fontInfo.stringWidth(score);
        scoreboardGraphics.drawString(score, (1660 - (scoreWidth/2)), 550);
        scoreboardImageCanvas.repaint(1420,190,480,450);
    }

    public synchronized void paintPossesionArrow() {
        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, 35));

        scoreboardGraphics.fillRoundRect(540,423,840,90,20,20);
        scoreboardGraphics.setColor(textColor);
        if (scoreboardMode == 1) {
            scoreboardGraphics.drawString("SERVING", 775, 490);
	} else {
            scoreboardGraphics.drawString("POSSESSION", 691, 490);
	}
        scoreboardGraphics.setColor(possesionArrowColor);
        if (possesionArrow == true) {
            scoreboardGraphics.fillOval(564,441,60,60);
        } else {
            scoreboardGraphics.fillOval(1284,441,60,60);
        }
        scoreboardImageCanvas.repaint(540,423,840,90);
    }

    public synchronized void paintFoulsSets() {
        String homeFouls;
        String lastHome;
        String guestFouls;
        String lastGuest;

    if (scoreboardMode == 1) {
        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.fillRoundRect(240,423,480,90,20,20);
        scoreboardGraphics.fillRoundRect(1416,531,480,90,20,20);
        scoreboardGraphics.fillRoundRect(240,531,480,90,20,20);
        scoreboardGraphics.fillRoundRect(1416,423,480,90,20,20);

        scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, 40));
        scoreboardGraphics.setColor(textColor);
        scoreboardGraphics.drawString("SETS", 55, 275);
        scoreboardGraphics.drawString("SETS", 635, 275);
        scoreboardGraphics.drawString(""+setsHome, 95, 335);
        scoreboardGraphics.drawString(""+setsGuest, 675, 335);

        scoreboardImageCanvas.repaint(10,235,200,50);
        scoreboardImageCanvas.repaint(10,295,200,50);
        scoreboardImageCanvas.repaint(590,235,200,50);
        scoreboardImageCanvas.repaint(590,295,200,50);
	// fix screen updates for volleyball mode (this hack works ;-)
	paint();
    } else {
        homeFouls = "Fouls: " + totalHomeFouls;
//   System.out.println(" lastHomePlayer is -" + lastHomePlayer + "-");
	if (lastHomePlayer != null && lastHomePlayer.length() > 0) {
            lastHome = "Last: " + lastHomePlayer + "-" + lastHomeFouls;
	} else {
            lastHome = ".";
	}
        guestFouls = "Fouls: " + totalGuestFouls;
//   System.out.println(" lastGuestPlayer is -" + lastGuestPlayer + "-");
	if (lastGuestPlayer != null && lastGuestPlayer.length() > 0) {
            lastGuest = "Last: " + lastGuestPlayer + "-" + lastGuestFouls;
	} else {
            lastGuest = ".";
	}
        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.fillRoundRect(240,423,480,90,20,20);
        scoreboardGraphics.fillRoundRect(240,531,480,90,10,20);
        scoreboardGraphics.fillRoundRect(1416,423,480,90,20,20);
        scoreboardGraphics.fillRoundRect(1416,531,480,90,20,20);
        if ((totalHomeFouls+totalGuestFouls) > 0) {
            scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, 35));
            scoreboardGraphics.setColor(textColor);
            scoreboardGraphics.drawString(homeFouls, 480, 495);
            scoreboardGraphics.drawString(guestFouls, 1440, 495);
            if (lastHomePlayer != null && totalHomeFouls > 0)
                    scoreboardGraphics.drawString(lastHome, 480, 603);
            if (lastGuestPlayer != null && totalGuestFouls > 0)
                scoreboardGraphics.drawString(lastGuest, 1440, 603);
        }
        scoreboardImageCanvas.repaint(240,423,480,90);
        scoreboardImageCanvas.repaint(240,531,480,90);
        scoreboardImageCanvas.repaint(1416,423,480,90);
        scoreboardImageCanvas.repaint(1416,531,480,90);
      }
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

    public void addHomeFoul(String playerNumberText) {
        int i, playerToAdd;

        i = 0;
        while (homePlayers[i] != null && !playerNumberText.equals(homePlayers[i]))
            i++;
        if (playerNumberText.equals(homePlayers[i])) {
            homeFouls[i]++;
        } else {
            homePlayers[i] = playerNumberText;
            homePlayers[i+1] = null;
            homeFouls[i] = 1;
        }
        prevTeam = 0;
        prevPlayer = lastHomePlayer;
        prevFouls = lastHomeFouls;
        lastIndex = i;
        lastHomePlayer = playerNumberText;
        lastHomeFouls = homeFouls[i];
        totalHomeFouls++;
    }

    public void addGuestFoul(String playerNumberText) {
        int i, playerToAdd;

        i = 0;
        while (guestPlayers[i] != null && !playerNumberText.equals(guestPlayers[i]))
            i++;
        if (playerNumberText.equals(guestPlayers[i])) {
            guestFouls[i]++;
        } else {
            guestPlayers[i] = playerNumberText;
            guestPlayers[i+1] = null;
            guestFouls[i] = 1;
        }
        prevTeam = 1;
        prevPlayer = lastGuestPlayer;
        prevFouls = lastGuestFouls;
        lastIndex = i;
        lastGuestPlayer = playerNumberText;
        lastGuestFouls = guestFouls[i];
        totalGuestFouls++;
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
        } else if (source == starttoButton) {
            timeoutTimer.pause();
            timeoutTimer.setTimer(convertTimeStringToInt(timeoutText.getText()));
            timeoutTimer.cont();
            starttoButton.setEnabled(false);
            cleartoButton.setEnabled(true);
	    paintBonus();
        } else if (source == cleartoButton) {
            timeoutTimer.pause();
            timeoutTimer.setTimer(0);
            starttoButton.setEnabled(true);
            cleartoButton.setEnabled(false);
	    paintBonus();
        } else if (source == stopButton) {
            scoreboardTimer.pause();
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        } else if (source == undoLastButton) {
            if (prevTeam == 0 && lastIndex != -1) {	// Home
		lastHomePlayer = prevPlayer;
	        lastHomeFouls = prevFouls;
	        homeFouls[lastIndex]--;
	        totalHomeFouls--;
	    } else if (prevTeam == 1 && lastIndex != -1) {     // Guest
		lastGuestPlayer = prevPlayer;
	        lastGuestFouls = prevFouls;
	        guestFouls[lastIndex]--;
	        totalGuestFouls--;
            }
	    lastIndex = -1;
            paintFoulsSets();
        } else if (source == resetSlidesButton) {
            if (scoreboardSlides != null) {
                scoreboardSlides.reset(	getCodeBase(), slideShowDir,
					numSlides, slideDelay);
	    }
        } else if (source == resetButton) {
            resetScoreboard();
        } else if (source == modeButton) {
	    if (scoreboardMode == 0) {
		scoreboardMode = 1;
		periodUpButton.setLabel("Game+");
		periodDnButton.setLabel("Game-");
		homeBonusButton.setLabel("Home Set+");
		guestBonusButton.setLabel("Guest Set+");
		homeFoulsButton.setLabel("Home Set-");
		guestFoulsButton.setLabel("Guest Set-");
		clearTeamFoulsButton.setLabel("Clear Sets");
		clearAllFoulsButton.setLabel("Clear Points");
		possesionButton.setLabel("Serving");
		settimeButton.setLabel(" ");
		settimeButton.setEnabled(false);
		startButton.setLabel(" ");
		startButton.setEnabled(false);
		stopButton.setLabel(" ");
		stopButton.setEnabled(false);
		homeuptwoButton.setLabel(" ");
		homeuptwoButton.setEnabled(false);
		guestuptwoButton.setLabel(" ");
		guestuptwoButton.setEnabled(false);
		undoLastButton.setLabel(" ");
		undoLastButton.setEnabled(false);
		modeButton.setLabel("Basketball");
	    } else {
		scoreboardMode = 0;
		periodUpButton.setLabel("Period+");
		periodDnButton.setLabel("Period-");
		homeBonusButton.setLabel("Home Bonus");
		guestBonusButton.setLabel("Guest Bonus");
		homeFoulsButton.setLabel("Home Foul");
		guestFoulsButton.setLabel("Guest Foul");
		clearTeamFoulsButton.setLabel("Clear Team Fouls");
		clearAllFoulsButton.setLabel("Clear All Fouls");
		possesionButton.setLabel("Poss Arrow");
		settimeButton.setLabel("Set Timer");
		settimeButton.setEnabled(true);
		startButton.setLabel("Start");
		stopButton.setLabel("Stop");
		homeuptwoButton.setLabel("Home Pt+2");
		homeuptwoButton.setEnabled(true);
		guestuptwoButton.setLabel("Guest Pt+2");
		guestuptwoButton.setEnabled(true);
		undoLastButton.setLabel("Undo Last Foul");
		undoLastButton.setEnabled(true);
		modeButton.setLabel("Volleyball");
	    }
            resetScoreboard();
        } else if (source == homeuptwoButton) {
            scoreHome += 2;
            paintHomeScore();
        } else if (source == homeupButton) {
            scoreHome++;
            paintHomeScore();
        } else if (source == homednButton) {
            if (scoreHome > 0) scoreHome--;
            paintHomeScore();
        } else if (source == homesetButton) {
            scoreHome = intValue(scoreText.getText());
            paintHomeScore();
        } else if (source == guestuptwoButton) {
            scoreGuest += 2;
            paintGuestScore();
        } else if (source == guestupButton) {
            scoreGuest++;
            paintGuestScore();
        } else if (source == guestdnButton) {
            if (scoreGuest > 0) scoreGuest--;
            paintGuestScore();
        } else if (source == guestsetButton) {
            scoreGuest = intValue(scoreText.getText());
            paintGuestScore();
        } else if (source == possesionButton) {
            possesionArrow = !possesionArrow;
            paintPossesionArrow();
        } else if (source == homeBonusButton) {
	    if (scoreboardMode == 1) {
		setsHome++;
		paintFoulsSets();
	    } else {
                homeInBonus = !homeInBonus;
                paintBonus();
	    }
        } else if (source == guestBonusButton) {
	    if (scoreboardMode == 1) {
		setsGuest++;
		paintFoulsSets();
	    } else {
                guestInBonus = !guestInBonus;
                paintBonus();
	    }
        } else if (source == periodOneButton) {
            periodNumber = 1;
            paintPeriod();
        } else if (source == periodUpButton) {
            periodNumber++;
            paintPeriod();
        } else if (source == periodDnButton) {
            if (periodNumber > 1) periodNumber--;
            paintPeriod();
        } else if (source == homeFoulsButton) {
	    if (scoreboardMode == 1) {
		if (setsHome > 0) setsHome--;
	    } else {
                addHomeFoul(foulPlayerText.getText());
	    }
            paintFoulsSets();
        } else if (source == guestFoulsButton) {
	    if (scoreboardMode == 1) {
		if (setsGuest > 0) setsGuest--;
	    } else {
                addGuestFoul(foulPlayerText.getText());
	    }
            paintFoulsSets();
        } else if (source == hornButton) {
            if (hornSound != null) {
                hornSound.play();
            }
        } else if (source == beepButton) {
            if (beepSound != null) {
                beepSound.play();
            }
        } else if (source == clearTeamFoulsButton) {
	    if (scoreboardMode == 1) {
		setsHome = 0;
		setsGuest = 0;
	    } else {
                totalHomeFouls = 0;
                totalGuestFouls = 0;
	    }
            paintFoulsSets();
        } else if (source == clearAllFoulsButton) {
	    if (scoreboardMode == 1) {
		scoreHome = 0;
		scoreGuest = 0;
		paintHomeScore();
		paintGuestScore();
	    } else {
		homePlayers[0] = null;
		totalHomeFouls = 0;
		lastHomePlayer = null;
		guestPlayers[0] = null;
		totalGuestFouls = 0;
		lastGuestPlayer = null;
		paintFoulsSets();
	    }
        } else if (source == suspendSlidesButton) {
            if (scoreboardSlides != null) {
                scoreboardSlides.pause();
            }
        } else if (source == clearSlidesButton) {
            if (scoreboardSlides != null) {
                scoreboardSlides.clear();
            }
        } else if (source == resumeSlidesButton) {
            if (scoreboardSlides != null) {
                scoreboardSlides.cont();
            }
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
                paintBonus();
                if (timeoutTimer.timerValue == 0) {
                    cleartoButton.setEnabled(false);
                    starttoButton.setEnabled(true);
                    if (beepSound != null) {
                        beepSound.play();
                    }
                }
            }
            if (lastScoreboardTimerValue != scoreboardTimer.timerValue) {
                paintTimer();
                if (scoreboardTimer.timerValue == 0) {
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

    public void update(Graphics g) {
        paint(g);
        scoreboardSlides.repaint();

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
            { "possesionArrowColor", "hexadecimal int", "the color of the possesion arrow (default=red)."},
            { "preferredFont", "String", "the font for all fields (default=Helvetica)."},
            { "slideDelay", "int", "the number of seconds to display slides (default=15)."},
            { "slideShowDir", "String", "the full path to the folder where slide images are stored, no slides displayed if not specified."},
            { "framePositionX", "int", "the left-to-right position of the scoreboard (default 340)."},
            { "framePositionY", "int", "the top-to-bottom position of the scoreboard (default 0)."},
            { "buttonFontSize", "int", "the size of the button font (default=14)."},

        };
        return info;
    }
}

