// Timer.java
//
// Simple millisecond timer for use with ScoreBoard.java
//
// Written by Bery Rinaldo on December 26, 1999
//
// Version 1 - April 17, 2002
// * Added GPL -- See gpl.txt and LICENSE.txt
//
// Copyright (c) 1999-2009 Bery Rinaldo

class Timer extends Thread
{
    public int timerValue = 0;
    public boolean suspended = false;

    Timer (int passedValue) {
        this.timerValue = passedValue;
    }

    public void setTimer(int passedValue) {
	this.timerValue = passedValue;
    }

    public void pause() {
        suspended = true;
    }

    public void cont() {
        suspended = false;
    }

    public void run() {
	    while (Thread.currentThread() == this) {
	        try {
		        sleep(100);
	        }
	        catch (Exception e) {  }
	        if (this.timerValue > 0 && !suspended) this.timerValue--;
	    }
    }
}
