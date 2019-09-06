package application;

import java.awt.event.KeyEvent;
import java.awt.AWTException;
import java.awt.Robot;
import java.util.TimerTask;

public class PressKey extends TimerTask
{
    public void run()
    {
    	try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_SPACE);

		} catch (AWTException e) {
			e.printStackTrace();
		}
     }
 }