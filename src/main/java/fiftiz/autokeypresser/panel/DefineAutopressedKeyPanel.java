package fiftiz.autokeypresser.panel;

import fiftiz.autokeypresser.FxmlConstants;
import fiftiz.autokeypresser.LanguageConstants;
import fiftiz.autokeypresser.MainWindow;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class DefineAutopressedKeyPanel
{
	/**
	 * Indicates whether the user is defining the key to be auto pressed.
	 */
	private static boolean isDefiningAutopressedKey = false;
	
	/**
	 * User-defined key that will be auto pressed every {@link MainWindow#autoPresserDelay} ms when the user activates the autopresser.
	 */
	private static InputEvent autoPressedKey;
	
	private Button defineKeyButton;
	
	private Label autopressedKeyLabel;
	
	private static DefineAutopressedKeyPanel defineAutopressedKeyPanel;
	
	
	private DefineAutopressedKeyPanel() {}
	
	public void init(Parent root)
	{
		defineKeyButton = (Button) root.lookup(FxmlConstants.DEFINE_KEY_BUTTON_ID);
		defineKeyButton.setText(LanguageConstants.DEFINE_KEY_TEXT);
		
		autopressedKeyLabel = (Label) root.lookup(FxmlConstants.AUTOPRESSED_KEY_LABEL_ID);
		
		setDefineKeyButtonBehavior(root);
	}
	
	private void setDefineKeyButtonBehavior(Parent root)
	{
		// Ask the user to type the new auto pressed key
		defineKeyButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent)
			{
				if (mouseEvent.getButton() == MouseButton.PRIMARY) {
					System.out.println("DEFINING NEW AUTO PRESSED KEY ...");
					
					defineKeyButton.setText("?");
					isDefiningAutopressedKey = true;
				}
			}
		});

		// Save the new auto pressed key
		defineKeyButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent)
			{
				if (isDefiningAutopressedKey) {		
					if (keyEvent.getCode() != KeyCode.UNDEFINED) {
						System.out.println("NEW AUTO PRESSED KEY: " + keyEvent.getCode());

						// Update the key that will be auto pressing
						autoPressedKey = keyEvent;

						// Update the label
						Text newAutopressedKey = new Text(keyEvent.getCode().getName());
						autopressedKeyLabel.setText(newAutopressedKey.getText());

						// Reset the button allowing to change the key that will be auto pressing
						defineKeyButton.setText(LanguageConstants.DEFINE_KEY_TEXT);
						isDefiningAutopressedKey = false;

						// Center the key label
						double newKeyWidth = newAutopressedKey.getLayoutBounds().getWidth();
						((HBox) root.lookup(FxmlConstants.DEFINE_KEY_BOX_ID)).setSpacing(MainWindow.WINDOW_WIDTH / 4 - newKeyWidth);
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
				if (isDefiningAutopressedKey) {
					System.out.println("NEW AUTO CLICKED BUTTON : " + mouseEvent.getButton().name());

					// Update the key that will be auto pressing
					autoPressedKey = mouseEvent;

					// Update the label
					Text newAutopressedKey = new Text(mouseEvent.getButton().name());
					autopressedKeyLabel.setText(newAutopressedKey.getText());

					// Reset the button allowing to change the key that will be auto pressing
					defineKeyButton.setText("Définir touche");
					isDefiningAutopressedKey = false;

					// Center the key label
					double newKeyWidth = newAutopressedKey.getLayoutBounds().getWidth();
					((HBox) root.lookup(FxmlConstants.DEFINE_KEY_BOX_ID)).setSpacing(MainWindow.WINDOW_WIDTH / 4 - newKeyWidth);
				}
			}
		});
	}
	
	public boolean isDefiningAutopressedKey() {
		return isDefiningAutopressedKey;
	}
	
	public InputEvent getAutopressedKey() {
		return autoPressedKey;
	}
	
	public static DefineAutopressedKeyPanel getInstance() {
		if (defineAutopressedKeyPanel == null) {
			defineAutopressedKeyPanel = new DefineAutopressedKeyPanel();
		}
		return defineAutopressedKeyPanel;
	}
}
