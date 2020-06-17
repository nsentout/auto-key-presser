package fiftiz.autokeypresser;

import java.awt.Robot;
import java.util.HashMap;
import java.util.TimerTask;

import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class AutoPresserTimerTask extends TimerTask
{
	/**
	 *  Map JavaFx buttons to awt Robot mouse buttons.
	 */
	private HashMap<MouseButton, Integer> mouseButtonsMap;
	
	/**
	 *  Key that will be autopressed.
	 */
	private KeyCode autoPressedKeyCode;
	
	/**
	 *  Mouse button that will be autopressed.
	 */
	private MouseButton autoPressedMouseButton;
	
	public AutoPresserTimerTask()
	{
		mouseButtonsMap = new HashMap<MouseButton, Integer>();
		mouseButtonsMap.put(MouseButton.PRIMARY, java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
		mouseButtonsMap.put(MouseButton.SECONDARY, java.awt.event.InputEvent.BUTTON2_DOWN_MASK);
		mouseButtonsMap.put(MouseButton.MIDDLE, java.awt.event.InputEvent.BUTTON3_DOWN_MASK);
	}
	
	/**
	 *  Set the key or mouse button to be autopressed
	 * @param inputType the key or mouse button to be autopressed
	 */
	public void setUserInput(InputEvent inputType)
	{
		if (inputType instanceof KeyEvent) {
			autoPressedKeyCode = ((KeyEvent) inputType).getCode();
			autoPressedMouseButton = null;
		}
		else if (inputType instanceof MouseEvent) {
			autoPressedKeyCode = null;
			autoPressedMouseButton = ((MouseEvent) inputType).getButton();
		}
		else {
			throw new IllegalArgumentException("You can only autopress keys and mouse buttons!");
		}
	}
	
	@Override
	public void run()
    {
    	try {
    		Robot robot = new Robot();
    		
    		if (autoPressedKeyCode != null) {
    			System.out.println("PRESSED " + autoPressedKeyCode.getName());
    			robot.keyPress(autoPressedKeyCode.getCode());
    		}
    		else if (autoPressedMouseButton != null) {
    			System.out.println("PRESSED " + autoPressedMouseButton.name() + " MOUSE BUTTON ");
    			robot.mousePress(mouseButtonsMap.get(autoPressedMouseButton));
    			robot.mouseRelease(mouseButtonsMap.get(autoPressedMouseButton));
    		}
    		else {
    			throw new Exception("No key/mouse buttons have been assigned");
    		}

		} catch (Exception e) {
			e.printStackTrace();
		}
     }
}
