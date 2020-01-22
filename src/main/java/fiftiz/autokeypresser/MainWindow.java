package fiftiz.autokeypresser;

import java.util.Timer;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
 
public class MainWindow extends Application implements NativeKeyListener
{
	private static final int START_DELAY = 3000;	// delay before the first auto press
	private static final long DEFAULT_DELAY_BETWEEN_KEYPRESS = 1000;

	private static final int WINDOW_WIDTH = 400;
	private static final int WINDOW_HEIGHT = 400;
	private static final String WINDOW_TITLE = "AutoKeyPresser";
	
	private static final String FXML_FILE_NAME = "layout.fxml";
	
	private static final KeyCode START_STOP_AUTOPRESS_KEY = KeyCode.F1;

	private static boolean isAutoPressing = false;
	private static boolean isDefiningAutoPressedKey = false;

	// Timer used to run <autoPresserTask> every <autoPresserDelay> ms
	private static Timer timer;
	private static AutoPresserTask autoPresserTask;

	private static InputEvent autoPressedKey;
	private static long autoPresserDelay = DEFAULT_DELAY_BETWEEN_KEYPRESS;
	
	private Parent root;
	
	// Window's buttons
	private static Button autoPressButton;
	private Button defineKeyButton;
	private Button applyDelayButton;
	
	// Window's label
	private Label keyLabel;
	
	// Window's textfields
	private TextField minDelay;
	private TextField secDelay;
	private TextField msDelay;

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		autoPresserTask = new AutoPresserTask();
		timer = new Timer();
		
		try {
			GlobalScreen.registerNativeHook();
			
			Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
			logger.setLevel(Level.WARNING);
			logger.setUseParentHandlers(false);
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}

		GlobalScreen.addNativeKeyListener(new MainWindow());
		
		try {
			root = new FXMLLoader(getClass().getResource(FXML_FILE_NAME)).load();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		autoPressButton = (Button) root.lookup("#autoPressButton");
		defineKeyButton = (Button) root.lookup("#defineKeyButton");
		applyDelayButton = (Button) root.lookup("#applyDelayButton");
		keyLabel = (Label) root.lookup("#autoPressedKey");

		minDelay = (TextField) root.lookup("#minDelay");
		secDelay = (TextField) root.lookup("#secDelay");
		msDelay = (TextField) root.lookup("#msDelay");
	
		setAutoPressButtonBehavior();
		setDefineKeyButtonBehavior();
		setApplyDelayButtonBehavior();
		setDelayTextFieldsBehavior();
		enableStartStopAutoPressWithKey();

		// Create the main scene and show the stage
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		primaryStage.setTitle(WINDOW_TITLE);
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();	
	}

	@Override
	public void stop()
	{
		System.out.println("Closed window ...");
		if (isAutoPressing) {
			timer.cancel();
		}
		System.exit(0);
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

	private void setDefineKeyButtonBehavior()
	{
		// Ask the user to type the new auto pressed key
		defineKeyButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent)
			{
				if (mouseEvent.getButton() == MouseButton.PRIMARY) {
					System.out.println("DEFINING NEW AUTO PRESSED KEY ...");
					
					defineKeyButton.setText("?");
					isDefiningAutoPressedKey = true;
				}
			}
		});

		// Save the new auto pressed key
		defineKeyButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent)
			{
				if (isDefiningAutoPressedKey) {		
					if (keyEvent.getCode() != KeyCode.UNDEFINED) {
						System.out.println("NEW AUTO PRESSED KEY: " + keyEvent.getCode());
						
						// Center the click button label
						((HBox) root.lookup("#defineKeyBox")).setSpacing(150.0);
	
						// Update the key that will be auto pressing
						autoPressedKey = keyEvent;
	
						// Update the label
						keyLabel.setText(keyEvent.getCode().getName());
	
						// Reset the button allowing to change the key that will be auto pressing
						defineKeyButton.setText("Définir touche");
						isDefiningAutoPressedKey = false;
					}
					else {
						System.err.println("This key is undefined, it can't be autopressed");
					}
				}
			}
		});

		// Save the new auto pressing mouse click
		root.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent)
			{
				if (isDefiningAutoPressedKey) {
					System.out.println("NEW AUTO CLICKED BUTTON : " + mouseEvent.getButton().name());

					// Center the click button label
					((HBox) root.lookup("#defineKeyBox")).setSpacing(40.0);

					// Update the key that will be auto pressing
					autoPressedKey = mouseEvent;

					// Update the label
					keyLabel.setText(mouseEvent.getButton().name());

					// Reset the button allowing to change the key that will be auto pressing
					defineKeyButton.setText("Définir touche");
					isDefiningAutoPressedKey = false;
				}
			}
		});
	}

	private void setApplyDelayButtonBehavior()
	{
		// Apply a new delay
		applyDelayButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event)
			{
				boolean inputFieldsEmpty = true;
				autoPresserDelay = 0;

				if (!minDelay.getText().isEmpty()) {
					inputFieldsEmpty = false;
					autoPresserDelay += Long.parseLong(minDelay.getText()) * 60000;
				}

				if (!secDelay.getText().isEmpty()) {
					inputFieldsEmpty = false;
					autoPresserDelay += Long.parseLong(secDelay.getText()) * 1000;
				}

				if (!msDelay.getText().isEmpty()) {
					inputFieldsEmpty = false;
					autoPresserDelay += Long.parseLong(msDelay.getText());
				}

				if (inputFieldsEmpty) {
					autoPresserDelay = DEFAULT_DELAY_BETWEEN_KEYPRESS;
				}
				
				System.out.println("APPLIED NEW DELAY: " + autoPresserDelay + " ms");
			}
		});
	}

	private void setDelayTextFieldsBehavior()
	{
		// Prevent from typing characters in the delay's input fields
		UnaryOperator<Change> filter = change -> {
			String text = change.getText();

			if (text.matches("[0-9]*")) {
				return change;
			}

			return null;
		};

		minDelay.setTextFormatter(new TextFormatter<>(filter));
		secDelay.setTextFormatter(new TextFormatter<>(filter));
		msDelay.setTextFormatter(new TextFormatter<>(filter));
	}
	
	private void enableStartStopAutoPressWithKey()
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
		if (!isDefiningAutoPressedKey) {
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
		if (autoPressedKey != null) {
			System.out.println("AUTOPRESSING ...");
			
			isAutoPressing = true;
			autoPressButton.setText("DÉSACTIVER AUTOPRESS");
			
			// Update the key that will be auto pressing
			autoPresserTask = new AutoPresserTask();
			autoPresserTask.setUserInput(autoPressedKey);
	
			timer = new Timer();
			timer.schedule(autoPresserTask, START_DELAY, autoPresserDelay);
		}
	}
	
	private void deactivateAutoPress()
	{
		System.out.println("STOP AUTOPRESSING");
		
		isAutoPressing = false;
		autoPressButton.setText("ACTIVER AUTOPRESS");
		timer.cancel();
	}

	public static void main(String[] args)
	{
		Application.launch(args);
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent event) {
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

}