
<body bgcolor=#ffffff><h1>ScoreBoard README</h1>
  This program runs the "electronic scoreboard" for the annual Jesuwon Community Church
  basketball tournament.
<p>
<p><h3>OVERVIEW</h3>
  The ScoreBoard is used to keep track of the points, show team names and jersey colors, and 
  record the number of timeouts avaiable. There is a control window which is meant 
  to be seen only by the operator to run the scoreboard.  There is another
  window which is contains the scoreboard display. The scoreboard is deigned to run on
  a dual monitor setup, where the operator contorls the scoreboard applet on one display
  and the scoreboard is displayed on another, larger display.
<p>
Included in this distribution is a file called "RUN.BAT" which is used to
start the scoreboard on Wintel PCs.  If you don't know what you're doing,
start there and you'll be able to figure it out.
<p><h3>CONTROLS</h3>
The control window has a variety of buttons and a few data entry fields which
the operator can use to run the scoreboard.  The buttons and fields are
described in the following sections.  Many of the buttons have both a "home"
and a "guest" version to control the two sides.  When this is the case, the
two buttons are described in one paragraph.
<ul>
<p><li><b>Home / Guest</b><p>
These buttons are used to customize the team's name in the upper text boxes
on the scoreboard.  Enter the team's name in the data entry field above
this button, then click this button to update the scoreboard.  The text fields
can also be used by the operator to help keep track of the team colors.
<p><li><b>Set Timer</b><p>
This button is used to set the clock on the scoreboard.  There is a data
entry field above this button where the user can enter the desired time
in the format Minutes:Seconds.Tenths (such as 5:00 for five minutes, or
10.2 for ten and two tenths seconds).  This button is only available in
basketball mode.
<p><li><b>Home Pt+2 / Guest Pt+2</b><p>
These buttons are used to add 2 to the point total on the scoreboard.
These buttons are only available in basketball mode.
<p><li><b>Start</b><p>
This button is used to start the clock.  This button is only available in
basketball mode.
<p><li><b>Home Pt+ / Guest Pt+</b><p>
These buttons are used to increment the point total on the scoreboard.
<p><li><b>Stop</b><p>
This button is used to stop the clock.  This button is only available in
basketball mode.
<p><li><b>Home Pt- / Guest Pt-</b><p>
These buttons are used to decrement the point total on the scoreboard.
<p><li><b>Poss Arrow</b><p>
This button is used to toggle the position of the possession indicator.  This
button is only available in basketball mode.
<p><li><b>Serving</b><p>
This button is used to toggle the position of the serving indicator.  This
button is only available in volleyball mode.
<p><li><b>Home Pt= / Guest Pt=</b><p>
These buttons are used to set the team's score to the value entered in the
text field between the two buttons.
<p><li><b>Home Bonus / Guest Bonus</b><p>
These buttons are used to toggle on and off the bonus indicator.  These
buttons are only available in basketball mode.
<p><li><b>Home Set+ / Guest Set+</b><p>
These buttons are used to increment the sets counters on the scoreboard.
These buttons are only available in volleyball mode.
<p><li><b>Period+</b><p>
This button is used to increment the period indicator.  There are
4 periods, then OT periods (OT, OT2, OT3...).  This button is only
available in basketball mode.
<p><li><b>Game+</b><p>
This button is used to increment the game indicator.  This button is only
available in volleyball mode.
<p><li><b>Clear Team Fouls</b><p>
This button is used to clear out the team foul counters.  This is normally done
at half-time.  This button is only available in basketball mode.
<p><li><b>Clear Sets</b><p>
This button is used to clear out the sets counters.
This button is only available in volleyball mode.
<p><li><b>Period-</b><p>
This button is used to decrement the period indicator.  This button is only
available in basketball mode.
<p><li><b>Game-</b><p>
This button is used to decrement the game indicator.  This button is only
available in volleyball mode.
<p><li><b>Home Foul / Guest Foul</b><p>
These buttons are used to track fouls.  There is a data entry box
between these two buttons that is used to enter a player number, then by
clicking on the appropriate button, that team's foul count will be
incremented and the "Last Foul" field will be filled in with the player
number and the number of fouls that player has.  This is kept in a
database which is stored in memory.  Use the 
"Clear All Fouls" button to clear out this database, use the "Clear Team Fouls"
button to reset the team fouls counters without clearing the personal foul
counters for each player.  This button is only available in basketball mode.
<p><li><b>Home Set- / Guest Set-</b><p>
These buttons are used to decrement the sets counters on the scoreboard.
These buttons are only available in volleyball mode.
<p><li><b>Horn</b><p>
This button is used to play the long horn sound that is used to indicate
the end of a period.
<p><li><b>Volleyball</b><p>
This button is used to switch the mode of the scoreboard from basketball to
volleyball.  This will reset the fouls database and all other data on the
scoreboard, so use this button only when you want to switch modes.  This
button is only available in basketball mode.
<p><li><b>Basketball</b><p>
This button is used to switch the mode of the scoreboard from volleyball to
basketball.  This will reset all the data on the scoreboard, so use this
button only when you want to switch modes.  This button is only available in
volleyball mode.
<p><li><b>Beep</b><p>
This button is used to play the short beep sound that is used to indicate
the end of a time-out.
<p><li><b>Clear All Fouls</b><p>
This button is used to clear out the fouls database completely.  This button
is only available in basketball mode.
<p><li><b>Clear Points</b><p>
This button is used to clear out the point counters.  This is intended for
use when one game completes after incrementing the sets counter for the team
that won the last game.  This button is only available in volleyball mode.
<p><li><b>Start TO</b><p>
This button is used to start a time-out using the value in the text box next
to this button.  After clicking this button, an internal timer is set and
started, and the "Clear TO" button is enabled.  When this timer finishes, the
beep sound is heard and the "Start TO" button is enabled.  The timeout timer
is displayed where the bonus indicators are in basketball mode.
<p><li><b>Clear TO</b><p>
This button is used to stop a time-out that is currently in progress.  After
clicking this button, the internal timer is reset and the "Start TO" button
is enabled and this button is disabled.
<p><li><b>Reset</b><p>
This will reset all the data on the scoreboard, so use this button only when a
game has completed and when you want to start from scratch.
<p><li><b>Undo Last Foul</b><p>
This button allows the operator to undo the last foul entered.  This is a
single-level undo which means that you can only do one undo, then the capability
is disabled.  This button is only available in basketball mode.
<p><li><b>Reset Slides</b><p>
This button is used to re-read the slides and announcements.txt file.  This
is commonly used after a game has completed and the results are entered and
saved in the announcements.txt file.
<p><li><b>Pause Slides</b><p>
This button is used to pause the slide show on the current slide.
<p><li><b>Clear Slide</b><p>
This button is used to blank out the slide area and pause the slide show.
<p><li><b>Start Slides</b><p>
This button is used to start the slide show after it has been cleared or
paused.  If the slide show is running, this button will move it to the next
slide.
</ul>
<p><h3>CONFIGURATION</h3>
There is a file called "ScoreBoard.txt" which can be used to configure
a variety of parameters are available to be configured.  As is comes, it
should work without any changes to this configuration file.  To get an idea
of the things that can be changed here, all the available parameters are
shown below:
<ul>
<p><li><b>maxPeriods</b><p>
Sets the number of regulation periods used for the basketball mode.
Default is 4.
<p><li><b>bgColor</b><p>
Sets the background color of the areas of the scoreboard where text is 
displayed.  This is a 6-digit hexadecimal representation similar to the 
color specifications in web pages.  Default is 000000 which is black.
<p><li><b>timeColor</b><p>
Sets the color of the timer text.  Default is ffff00 which is yellow.
<p><li><b>timeFontSize</b><p>
Sets the size of the timer text.  Default is 100.
<p><li><b>lastMinuteTimeColor</b><p>
Sets the color of the timer text when the clock is under one minute.
Default is ff0000 which is red.
<p><li><b>scoreColor</b><p>
Sets the color of the score.  Default is 00ff00 which is green.
<p><li><b>scoreFontSize</b><p>
Sets the size of the score.  Default is 90.
<p><li><b>textColor</b><p>
Sets the color of the other text such as team name, etc.  Default is 
ffff00 which is yellow.
<p><li><b>fillColor</b><p>
Sets the color of the area around the text areas.  Default is c0c0c0
which is a light gray.
<p><li><b>bonusLightColor</b><p>
Sets the color of the bonus indicator.  Default is ff0000 which is red.
<p><li><b>possesionArrowColor</b><p>
Sets the color of the possession arrow (or serving) indicator.  Default
is ff0000 which is red.
<p><li><b>preferredFont</b><p>
Sets the font used for the scoreboard.  Default is Helvetica.
<p><li><b>slideShowDir</b><p>
Directory where the slide show GIF files are stored.  There is no default
value, but this is usually set to "./slides/".
<p><li><b>numSlides</b><p>
Number of slides in the slide show directory.  Default is 3.  The slide files 
are stored in GIF format and must be named <b>N</b>slide.gif where "N" is the slide
number.  Such as: 1slide.gif, 2slide.gif, 3slide.gif
<p><li><b>slideDelay</b><p>
This selects the amount of time in seconds to display each slide.  Default is
10 seconds.
<p><li><b>framePositionX</b><p>
Selects the left-to-right position of the scoreboard window.  Depending on the
configuration of the two monitors, this could be a negative or positive value.
The value of this field depends on how the two monitors are setup.  If the main
screen is to the left, then this will be a positive value about 4 less than the
width of the main screen.  If the main screen is to the right, then this will
be a negative number about 4 more than the width of the window (804, that is).
The default is 340 which normally places it on the same screen as the control
window.
<p><li><b>framePositionY</b><p>
Selects the top-to-bottom position of the scoreboard window.  Depending on the
configuration of the two monitors, this could be a negative or positive value.
The default is 0 which normally places it at the top of the screen.
</ul>
<p><h3>TECHNICAL</h3>
This program is written in Java and requires the appletviewer application 
which is only included with the Java 2 SDK.  You can obtain this freely
from http://java.sun.com/.  This has been tested using the Java2 1.4.2_19
SDK installation on a PC running Win98, although the development was done
under Linux (Fedora release 7) with the Java2 1.4.2_19 SDK.
<p><h3>CONTACT</h3>
Contact Ryan Won at ryanwon7@gmail.com or rhw37@drexel.edu if you have 
any questions or problems.
</body>
</html>
