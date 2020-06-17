package fiftiz.autokeypresser.panel;

import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import fiftiz.autokeypresser.AutoPresserTimerTask;
import fiftiz.autokeypresser.FxmlConstants;
import fiftiz.autokeypresser.MainWindow;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ActivateAutopresserPanel implements NativeKeyListener
{
	/**
	 * Key to type to start or stop the program to auto press the defined key. Works when the program is running in the background.
	 */
	private static final KeyCode START_STOP_AUTOPRESS_KEY = KeyCode.F1;

	/**
	 * Indicates whether the auto presser is active.
	 */
	private static boolean isAutoPressing = false;
	
	/**
	 * Timer used to run {@link MainWindow#autoPresserTask} every {@link MainWindow#autoPresserDelay} ms.
	 */
	private static Timer autoPresserTimer;

	/**
	 * Simulate a key press or mouse click.
	 */
	private static AutoPresserTimerTask autoPresserTask;
	
	/**
	 * Parent window.
	 */
	private static MainWindow parent;
	
	/**
	 * Button to press to start the key autopressing.
	 */
	private static Button autoPressButton;
	
	private static ActivateAutopresserPanel activateAutopresserPanel;
	
	
	private ActivateAutopresserPanel() { }
	
	public void init(Parent root, MainWindow mainWindow)
	{
		try {
			GlobalScreen.registerNativeHook();
			
			Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
			logger.setLevel(Level.WARNING);
			logger.setUseParentHandlers(false);
		}
		catch (NativeHookException e) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(e.getMessage());
			System.exit(1);
		}

		GlobalScreen.addNativeKeyListener(new ActivateAutopresserPanel());
		
		parent = mainWindow;
		autoPressButton = (Button) root.lookup(FxmlConstants.AUTOPRESSER_BUTTON_ID);
		
		autoPresserTimer = new Timer();
		autoPresserTask = new AutoPresserTimerTask();
		
		setAutoPressButtonBehavior();
		enableStartStopAutoPressWithKey(root);
	}
	
	private void setAutoPressButtonBehavior()
	{
		// Activate / Deactivate auto pressing
		autoPressButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event)
			{
				handleClickOnAutoPressKey();
			}
		});
	}

	private void enableStartStopAutoPressWithKey(Parent root)
	{
		// Save the new auto pressing mouse click
		root.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent)
			{
				if (keyEvent.getCode().equals(START_STOP_AUTOPRESS_KEY)) {
					handleClickOnAutoPressKey();
				}
			}
		});
	}
	
	private void handleClickOnAutoPressKey()
	{
		System.out.println("current thread: " + Thread.currentThread().getName());
		if (!parent.isDefiningAutopressedKey()) {
			if (isAutoPressing) {
				deactivateAutoPress();
			}
			else {
				activateAutoPress();
			}
		}
	}
	
	private void activateAutoPress()
	{
		if (parent.getAutopressedKey() != null) {
			System.out.println("AUTOPRESSING ...");
			
			isAutoPressing = true;
			autoPressButton.setText("DÉSACTIVER AUTOPRESSER");
			
			// Update the key that will be auto pressing
			autoPresserTask = new AutoPresserTimerTask();
			autoPresserTask.setUserInput(parent.getAutopressedKey());
	
			autoPresserTimer = new Timer();
			autoPresserTimer.schedule(autoPresserTask, MainWindow.START_DELAY, parent.getAutopresserDelay());
		}
	}
	
	private void deactivateAutoPress()
	{
		System.out.println("STOP AUTOPRESSING");
		
		isAutoPressing = false;
		autoPressButton.setText("ACTIVER AUTOPRESSER");
		autoPresserTimer.cancel();
	}
	
	@Override
	public void nativeKeyReleased(NativeKeyEvent event) {
		System.out.println("background listener thread: " + Thread.currentThread().getName());
		String keyReleased = NativeKeyEvent.getKeyText(event.getKeyCode());
		
		if (keyReleased.equalsIgnoreCase(START_STOP_AUTOPRESS_KEY.getName())) {
			// Make the JavaFX application thread call this function
			Platform.runLater(() -> handleClickOnAutoPressKey());
		}
	}
	
	@Override
	public void nativeKeyTyped(NativeKeyEvent event) {}

	@Override
	public void nativeKeyPressed(NativeKeyEvent event) {}
	
	public static ActivateAutopresserPanel getInstance() {
		if (activateAutopresserPanel == null) {
			activateAutopresserPanel = new ActivateAutopresserPanel();
		}
		return activateAutopresserPanel;
	}
	
	public boolean isAutoPressing() {
		return isAutoPressing;
	}
	
	public void cancelAutoPresserTimer() {
		autoPresserTimer.cancel();
	}
}
