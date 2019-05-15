
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
described in the following sections.  Most of the button are divided into Red(Home) and Yellow
(Away) buttons, as there were the primary jersey colors used during the tournament. In cases of
buttons for both Red and Yellow, they are described in one description.
<ul>
<p><li><b>Set Timer</b><p>
This button is used to set the clock on the scoreboard.  There is a data
entry field left of this button where the user can enter the desired time
in the format Minutes:Seconds.Tenths (such as 5:00 for five minutes, or
10.2 for ten and two tenths seconds).
<p><li><b>Start</b><p>
This button is used to start the clock.  When the clock is running, this button
is greyed out and cannot be clicked.
<p><li><b>Stop</b><p>
This button is used to stop the clock.  When the clock is running, this button
is greyed out and cannot be clicked.
<p><li><b>Red / Yellow</b><p>
Both the Red and Yellow buttons have text fields above them for the names of the
teams that will be the Red and Yellow teams. when these buttons are clicked,
the scoreboard will update to show the team names.
<p><li><b>Red Pt +3 / Yellow Pt +3</b><p>
These buttons are used to add 3 to the team's point total on the scoreboard.
<p><li><b>Red Pt +2 / Yellow Pt +2</b><p>
These buttons are used to add 2 to the team's point total on the scoreboard.
<p><li><b>Red Pt -2 / Yellow Pt -2</b><p>
These buttons are used to subtract 2 from the team's point total on the scoreboard.
<p><li><b>Red Pt -3 / Yellow Pt -3</b><p>
These buttons are used to subtract 3 from the team's point total on the scoreboard.
<p><li><b>Red Pt Reset / Yellow Pt Reset</b><p>
These buttons are used to reset the team's point total back to 0 on the scoreboard.
<p><li><b>Red Timeout / Yellow Timeout</b><p>
These buttons are used to subtract one timeout from each team. Each team starts with
three timeouts available. 
<p><li><b>Red Timeout Reset/ Yellow Timeout Reset</b><p>
These buttons are used to restore the original amount of timeouts (3) back to each
<p><li><b>Start Timeout</b><p>
This button is used to automatically start a 30 second timeout. If the clock is running
when the timeout button is pressed, then the clock will stop, and a new 30 second clock will appear
and start running. The Stop button may be used during timeout clock operation.
<p><li><b>Clear Timeout</b><p>
This button will clear the timeout from the screen and show the regular game clock on the screen again.
The game clock will not start until the "Start" button is pressed.
<p><li><b>Period+</b><p>
This button is used to increment the period indicator. As there are three states (1, 2, OT), the scoreboard
will have no change if the period is already at OT when the Period+ button is pressed.
<p><li><b>Period-</b><p>
This button is used to decrement the period indicator. As there are three states (1, 2, OT), the scoreboard
will have no change if the period is already at 1 when the Period- button is pressed.
<p><li><b>Reset</b><p>
This will reset all the data on the scoreboard, so use this button only when a
game has completed and when you want to start from scratch.
<p><li><b>Switch Sides</b><p>
This button is used to switch sides. This will switch all the Red/Yellow buttons on the applet, and reverse all
names, numbers, and information to the other side on the scoreboard.
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
Default is 2.
<p><li><b>bgColor</b><p>
Sets the background color of the areas of the scoreboard where text is 
displayed.  This is a 6-digit hexadecimal representation similar to the 
color specifications in web pages.  Default is 000000 which is black.
<p><li><b>timeColor</b><p>
Sets the color of the timer text.  Default is ffff00 which is yellow.
<p><li><b>timeFontSize</b><p>
Sets the size of the timer text.  Default is 330.
<p><li><b>lastMinuteTimeColor</b><p>
Sets the color of the timer text when the clock is under one minute.
Default is ff0000 which is red.
<p><li><b>scoreColor</b><p>
Sets the color of the score.  Default is ffffff which is white.
<p><li><b>scoreFontSize</b><p>
Sets the size of the score.  Default is 600.
<p><li><b>textColor</b><p>
Sets the color of the other text such as team name, etc.  Default is 
ffff00 which is yellow.
<p><li><b>fillColor</b><p>
Sets the color of the area around the text areas.  Default is 808080
which is a light gray.
<p><li><b>framePositionX</b><p>
Selects the left-to-right position of the scoreboard window.  Depending on the
configuration of the two monitors, this could be a negative or positive value.
The value of this field depends on how the two monitors are setup.  If the main
screen is to the left, then this will be a positive value about 4 less than the
width of the main screen.  If the main screen is to the right, then this will
be a negative number about 4 more than the width of the window (804, that is).
The default is 1020 which normally places it on the same screen as the control
window.
<p><li><b>framePositionY</b><p>
Selects the top-to-bottom position of the scoreboard window.  Depending on the
configuration of the two monitors, this could be a negative or positive value.
The default is -26 which normally places it at the top of the screen.
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
