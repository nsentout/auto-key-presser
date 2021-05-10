package fiftiz.autokeypresser.panel;

import fiftiz.autokeypresser.MainWindow;
import fiftiz.autokeypresser.constants.FxmlConstants;
import fiftiz.autokeypresser.constants.LanguageConstants;
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

public class DefineAutopressedKeyPanel implements Panel
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
	
	private static DefineAutopressedKeyPanel instance;
	
	/**
	 * Parent window.
	 */
	private static MainWindow parent;
	
	
	private DefineAutopressedKeyPanel() {}
	
	@Override
	public void init(Parent root, MainWindow mainWindow)
	{
		parent = mainWindow;
		
		defineKeyButton = (Button) root.lookup(FxmlConstants.DEFINE_KEY_BUTTON_ID);
		defineKeyButton.setText(LanguageConstants.DEFINE_KEY_TEXT);
		
		autopressedKeyLabel = (Label) root.lookup(FxmlConstants.AUTOPRESSED_KEY_LABEL_ID);
		
		setDefineKeyButtonBehavior(root);
	}
	
	@Override
	public void enablePanel()
	{
		defineKeyButton.setDisable(false);
	}
	
	@Override
	public void disablePanel()
	{
		defineKeyButton.setDisable(true);
	}
	
	private void saveNewAutoPressedKey(Parent root, InputEvent keyEvent)
	{
		if (isDefiningAutopressedKey) {
			String keyString = "";

			if (keyEvent instanceof MouseEvent) {
				keyString = ((MouseEvent) (keyEvent)).getButton().name();
			}
			else if (keyEvent instanceof KeyEvent) {
				if (((KeyEvent) keyEvent).getCode() == KeyCode.UNDEFINED) {
					System.err.println("This key is undefined, it can't be autopressed");
					return;
				}
				keyString = ((KeyEvent) keyEvent).getCode().getName();
			}

			System.out.println("NEW AUTO PRESSED BUTTON : " + keyString);
			
			parent.enableActivateAutopresserPanel();

			// Update the key that will be auto pressing
			autoPressedKey = keyEvent;

			// Update the label
			Text newAutopressedKey = new Text(keyString);
			autopressedKeyLabel.setText(newAutopressedKey.getText());

			// Reset the button allowing to change the key that will be auto pressing
			defineKeyButton.setText(LanguageConstants.DEFINE_KEY_TEXT);
			isDefiningAutopressedKey = false;

			// Center the key label
			double newKeyWidth = newAutopressedKey.getLayoutBounds().getWidth();
			((HBox) root.lookup(FxmlConstants.DEFINE_KEY_BOX_ID)).setSpacing(MainWindow.WINDOW_WIDTH / 4 - newKeyWidth);
		}
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

					parent.disableActivateAutopresserPanel();
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
				saveNewAutoPressedKey(root, keyEvent);
			}
		});

		// Save the new auto pressing mouse click
		root.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent)
			{
				saveNewAutoPressedKey(root, mouseEvent);
			}
		});
	}

	public boolean isDefiningAutopressedKey()
	{
		return isDefiningAutopressedKey;
	}
	
	public InputEvent getAutopressedKey()
	{
		return autoPressedKey;
	}
	
	public static DefineAutopressedKeyPanel getInstance()
	{
		if (instance == null) {
			instance = new DefineAutopressedKeyPanel();
		}
		return instance;
	}
}
