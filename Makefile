all:	ScoreBoard.class ImageCanvas.class SlideShow.class Timer.class

ScoreBoard.class:	ScoreBoard.java
	javac ScoreBoard.java

ImageCanvas.class:	ImageCanvas.java
	javac ImageCanvas.java

SlideShow.class:	SlideShow.java
	javac SlideShow.java

Timer.class:	Timer.java
	javac Timer.java

clean:
	rm -f ScoreBoard.class ImageCanvas.class SlideShow.class Timer.class
