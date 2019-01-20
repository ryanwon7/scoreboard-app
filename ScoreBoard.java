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
           homeFoulsButton, hornButton,
           guestFoulsButton, beepButton,
           clearAllFoulsButton, clearTeamFoulsButton,
       homeupthreeButton, guestupthreeButton,
       homednthreeButton, guestdnthreeButton,
	   starttoButton, cleartoButton,
           resetButton, undoLastButton, switchButton;
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
    String tempName;
    int lastHomeFouls, lastGuestFouls;
    // for undo capability
    int lastIndex = -1;
    String prevPlayer;
    int prevFouls;
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
    Color guestColor;

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
        homeText = new TextField(10);
        guestText = new TextField(10);
        timerText = new TextField(10);
        foulPlayerText = new TextField(10);
        timeoutText = new TextField(10);
        scoreText = new TextField(10);


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

        homeupthreeButton = new Button("Home Pt+3");
        homeupthreeButton.addActionListener(this);
	    homeupthreeButton.setFont(buttonFont);

        homeuptwoButton = new Button("Home Pt+2");
        homeuptwoButton.addActionListener(this);
	    homeuptwoButton.setFont(buttonFont);

        homednthreeButton = new Button("Home Pt-3");
        homednthreeButton.addActionListener(this);
        homednthreeButton.setFont(buttonFont);
        
        homedntwoButton = new Button("Home Pt-2");
        homedntwoButton.addActionListener(this);
	    homedntwoButton.setFont(buttonFont);

        homesetButton = new Button("Home Pt Reset");
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

        guestupthreeButton = new Button("Guest Pt+3");
        guestupthreeButton.addActionListener(this);
	    guestupthreeButton.setFont(buttonFont);

        guestuptwoButton = new Button("Guest Pt+2");
        guestuptwoButton.addActionListener(this);
	    guestuptwoButton.setFont(buttonFont);

        guestdntwoButton = new Button("Guest Pt-2");
        guestdntwoButton.addActionListener(this);
        guestdntwoButton.setFont(buttonFont);
        
        guestdnthreeButton = new Button("Guest Pt-3");
        guestdnthreeButton.addActionListener(this);
	    guestdnthreeButton.setFont(buttonFont);

        guestsetButton = new Button("Guest Pt Reset");
        guestsetButton.addActionListener(this);
	    guestsetButton.setFont(buttonFont);

        resetButton = new Button("Reset");
        resetButton.addActionListener(this);
        resetButton.setFont(buttonFont);
        
        switchButton = new Button("Switch Sides");
        switchButton.addActionListener(this);
        switchButton.setFont(buttonFont);

        setLayout(new GridLayout(11,2,3,3)); // 13 rows, 3 cols, 3 pixel gaps

        
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
        timeoutTimer.pause();
        timeoutTimer.setTimer(0);
        paintTimer();
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        nameHome = "RED";
        paintHomeName();
        nameGuest = "YELLOW";
        paintGuestName();
        scoreHome = 0;
        paintHomeScore();
        scoreGuest = 0;
        paintGuestScore();
        periodNumber = 1;
        paintPeriod();
        paintLogos();
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

    public synchronized void paintLogos() {
        scoreboardGraphics.drawImage(logoJBA, 20, 30, this);
        scoreboardImageCanvas.repaint(20, 30, 480, 279);
        scoreboardGraphics.drawImage(logoJesuwon, 1420, 74, this);
        scoreboardImageCanvas.repaint(1402, 74, 480, 192);
    }
    public synchronized void paintTimer() {
        String sMin, sSec, sMil, sTim;
        int dMin, dSec, dMil, dTime = 0;

      if (scoreboardMode == 1) {
        scoreboardGraphics.setColor(bgColor);
        scoreboardGraphics.fillRoundRect(520,20,880,300,40,40);
        scoreboardImageCanvas.repaint(520,20,880,300);
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
            scoreboardGraphics.fillRoundRect(530,20,880,300,40,40);
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
    }

    public synchronized void paintPeriod() { 
        String periodString;

        if (periodNumber <= maxPeriods) {
            periodString = periodNumber + "";
            scoreboardGraphics.setColor(bgColor);
            scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, 300));
            scoreboardGraphics.fillRoundRect(835,600,250,300,40,40);
            scoreboardGraphics.setColor(Color.WHITE);
            scoreboardGraphics.drawString(periodString, 880, 850);
            scoreboardImageCanvas.repaint(835,600,250,300);
        } else {
            periodString = "OT";
            scoreboardGraphics.setColor(bgColor);
            scoreboardGraphics.setFont(new Font(preferredFont, Font.PLAIN, 175));
            scoreboardGraphics.fillRoundRect(835,600,250,300,40,40);
            scoreboardGraphics.setColor(Color.WHITE);
            scoreboardGraphics.drawString(periodString, 835, 820);
            scoreboardImageCanvas.repaint(835,600,250,300);

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
        } else if (source == starttoButton) {
            timeoutTimer.pause();
            timeoutTimer.setTimer(convertTimeStringToInt(timeoutText.getText()));
            timeoutTimer.cont();
            starttoButton.setEnabled(false);
            cleartoButton.setEnabled(true);
        } else if (source == cleartoButton) {
            timeoutTimer.pause();
            timeoutTimer.setTimer(0);
            starttoButton.setEnabled(true);
            cleartoButton.setEnabled(false);
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
        } else if (source == resetButton) {
            resetScoreboard();
        } else if (source == switchButton) {
            tempScore = scoreGuest;
            scoreGuest = scoreHome;
            scoreHome = tempScore;
            tempName = nameGuest;
            nameGuest = nameHome;
            nameHome = tempName;
            if (homeNameColor == Color.RED) {
                homeNameColor = Color.YELLOW;
                guestNameColor = Color.RED;
            } else {
                homeNameColor = Color.RED;
                guestNameColor = Color.YELLOW;
            }
            paintHomeName();
            paintGuestName();
            paintHomeScore();
            paintGuestScore();
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

