package application;

import java.util.Timer;
import java.util.function.UnaryOperator;

import javafx.application.Application;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
 
public class MainWindow extends Application
{
	private static final int START_DELAY = 3000;	// delay before the first auto press
	private static final long DEFAULT_DELAY_BETWEEN_KEYPRESS = 1000;

	private static final int WINDOW_WIDTH = 400;
	private static final int WINDOW_HEIGHT = 400;

	private boolean isAutoPressing = false;
	private boolean isDefiningAutoPressedKey = false;

	// Timer used to run <autoPresserTask> every <autoPresserDelay> ms
	private Timer timer;
	private AutoPresserTask autoPresserTask;

	private InputEvent autoPressedKey;
	private long autoPresserDelay = DEFAULT_DELAY_BETWEEN_KEYPRESS;

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		autoPresserTask = new AutoPresserTask();
		timer = new Timer();

		Parent root = FXMLLoader.load(getClass().getResource("layout.fxml"));

		Button autoPressButton = (Button) root.lookup("#autoPressButton");
		Button defineKeyButton = (Button) root.lookup("#defineKeyButton");
		Button applyDelayButton = (Button) root.lookup("#applyDelayButton");
		Label keyLabel = (Label) root.lookup("#autoPressedKey");

		TextField minDelay = (TextField) root.lookup("#minDelay");
		TextField secDelay = (TextField) root.lookup("#secDelay");
		TextField msDelay = (TextField) root.lookup("#msDelay");

		setAutoPressButtonBehavior(autoPressButton);
		setDefineKeyButtonBehavior(defineKeyButton, keyLabel, root);
		setApplyDelayButtonBehavior(applyDelayButton, minDelay, secDelay, msDelay);
		setDelayTextFieldsBehavior(minDelay, secDelay, msDelay);

		// Create the main scene and show the stage
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		primaryStage.setTitle("AutoKeyPress");
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

	public void setAutoPressButtonBehavior(Button autoPressButton)
	{
		// Activate / Deactivate auto pressing
		autoPressButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event)
			{
				if (autoPressedKey != null) {
					isAutoPressing = !isAutoPressing;

					if (isAutoPressing) {
						System.out.println("AUTOPRESSING ...");
						
						autoPressButton.setText("DÉSACTIVER AUTOPRESS");

						// Update the key that will be auto pressing
						autoPresserTask = new AutoPresserTask();
						autoPresserTask.setUserInput(autoPressedKey);

						timer = new Timer();
						timer.schedule(autoPresserTask, START_DELAY, autoPresserDelay);

					}
					else {
						System.out.println("STOP AUTOPRESSING");
						
						autoPressButton.setText("ACTIVER AUTOPRESS");
						timer.cancel();
					}
				}  		
			}
		});
	}

	public void setDefineKeyButtonBehavior(Button defineKeyButton, Label keyLabel, Parent root)
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

	public void setApplyDelayButtonBehavior(Button applyDelayButton, TextField minDelay, TextField secDelay, TextField msDelay)
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

	public void setDelayTextFieldsBehavior(TextField minDelay, TextField secDelay, TextField msDelay)
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

	public static void main(String[] args)
	{
		Application.launch(args);
	}

}