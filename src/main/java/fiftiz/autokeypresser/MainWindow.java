package fiftiz.autokeypresser;

import java.io.IOException;
import fiftiz.autokeypresser.panel.ActivateAutopresserPanel;
import fiftiz.autokeypresser.panel.ApplyDelayPanel;
import fiftiz.autokeypresser.panel.DefineAutopressedKeyPanel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.InputEvent;
import javafx.stage.Stage;
 
public class MainWindow extends Application
{
	/**
	 * Delay before the defined key starts to be auto pressed after the user pressed the "ACTIVATE AUTOPRESSER" button.
	 */
	public static final int START_DELAY = 2000;
	
	/**
	 * Name of the FXML file describing the window layout.
	 */
	private static final String FXML_FILE_NAME = "layout.fxml";
	
	public static final long DEFAULT_DELAY_BETWEEN_KEYPRESS = 1000;

	public static final int WINDOW_WIDTH = 400;
	public static final int WINDOW_HEIGHT = 400;
	public static final String WINDOW_TITLE = "AutoKeyPresser";
	
	private static DefineAutopressedKeyPanel defineAutopressedKeyPanel;
	private static ApplyDelayPanel applyDelayPanel;
	private static ActivateAutopresserPanel activateAutopresserPanel;
	
	/**
	 * Main panel of the window.
	 */
	private Parent root;
	
	@Override
	public void init() throws Exception
	{
		try {
			root = new FXMLLoader(getClass().getResource(FXML_FILE_NAME)).load();
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		applyDelayPanel = ApplyDelayPanel.getInstance();
		applyDelayPanel.init(root);
		
		defineAutopressedKeyPanel = DefineAutopressedKeyPanel.getInstance();
		defineAutopressedKeyPanel.init(root);
		
		activateAutopresserPanel = ActivateAutopresserPanel.getInstance();
		activateAutopresserPanel.init(root, this);
	}
	
	public boolean isDefiningAutopressedKey() {
		return defineAutopressedKeyPanel.isDefiningAutopressedKey();
	}
	
	public InputEvent getAutopressedKey() {
		return defineAutopressedKeyPanel.getAutopressedKey();
	}
	
	public long getAutopresserDelay() {
		return applyDelayPanel.getAutopresserDelay();
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		primaryStage.setTitle(WINDOW_TITLE);
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		System.out.println("javafx thread: " + Thread.currentThread().getName());
		primaryStage.show();	
	}
	
	public static void main(String[] args)
	{
		Application.launch(args);
	}

	@Override
	public void stop()
	{
		System.out.println("Closed window ...");
		if (activateAutopresserPanel.isAutoPressing()) {
			activateAutopresserPanel.cancelAutoPresserTimer();
		}
		System.exit(0);
	}

}
