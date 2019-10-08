package application;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.HashMap;
import java.util.TimerTask;

import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class UserInput extends TimerTask
{
	// Map JavaFx buttons to Robot mouse buttons
	private HashMap<MouseButton, Integer> mouseButtonsMap;
	
	private KeyCode keyCode;
	private MouseButton mouseButton;
	
	public UserInput()
	{
		mouseButtonsMap = new HashMap<MouseButton, Integer>();
		mouseButtonsMap.put(MouseButton.PRIMARY, java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
		mouseButtonsMap.put(MouseButton.SECONDARY, java.awt.event.InputEvent.BUTTON2_DOWN_MASK);
		mouseButtonsMap.put(MouseButton.MIDDLE, java.awt.event.InputEvent.BUTTON3_DOWN_MASK);
	}
	
	public void setUserInput(InputEvent inputType)
	{
		if (inputType instanceof KeyEvent) {
			keyCode = ((KeyEvent) inputType).getCode();
			mouseButton = null;
		}
		else if (inputType instanceof MouseEvent) {
			keyCode = null;
			mouseButton = ((MouseEvent) inputType).getButton();
		}
		else {
			throw new IllegalArgumentException("You can only autopress keys and mouse buttons!");
		}
	}
	
	public void run()
    {
    	try {
    		Robot robot = new Robot();
    		
    		if (keyCode != null) {
    			System.out.println("PRESSED " + keyCode.getName());
    			robot.keyPress(keyCode.impl_getCode());
    		}
    		else if (mouseButton != null) {
    			System.out.println("PRESSED " + mouseButton.name() + " MOUSE BUTTON ");
    			robot.mousePress(mouseButtonsMap.get(mouseButton));
    			robot.mouseRelease(mouseButtonsMap.get(mouseButton));
    		}
    		else {
    			// Press Space by default
    			System.out.println("PRESSED SPACE");
    			robot.keyPress(java.awt.event.KeyEvent.VK_SPACE);
    		}

		} catch (AWTException e) {
			e.printStackTrace();
		}
     }
}
