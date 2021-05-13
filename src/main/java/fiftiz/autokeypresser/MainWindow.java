package fiftiz.autokeypresser;

import java.io.IOException;
import java.text.ParseException;

import fiftiz.autokeypresser.config.UserParameters;
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
	
	/******************************************************************************************/
	
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
		applyDelayPanel.init(root, this);
		
		defineAutopressedKeyPanel = DefineAutopressedKeyPanel.getInstance();
		defineAutopressedKeyPanel.init(root, this);
		
		activateAutopresserPanel = ActivateAutopresserPanel.getInstance();
		activateAutopresserPanel.init(root, this);
		
		readUserParameters();
		
		disableActivateAutopresserPanel();
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

	@Override
	public void stop()
	{
		System.out.println("Closed window ...");
		if (activateAutopresserPanel.isAutoPressing()) {
			activateAutopresserPanel.cancelAutoPresserTimer();
		}
		System.exit(0);
	}
	
	public void readUserParameters() throws ParseException
	{
		UserParameters userParameters = new UserParameters();
		boolean parametersFileExist = userParameters.readParametersFile();
		if (parametersFileExist) {
			// START_STOP_AUTOPRESS_KEY parameter
			String startStopAutopressKeyParameter = userParameters.getParameter(UserParameters.START_STOP_AUTOPRESS_KEY_KEY);
			if (startStopAutopressKeyParameter != null) {
				activateAutopresserPanel.setStartStopAutoPressKey(startStopAutopressKeyParameter);
			}

			// DEFAULT_AUTOPRESS_DELAY parameter
			String autoPressDelayParameter = userParameters.getParameter(UserParameters.DEFAULT_AUTOPRESS_DELAY_KEY);
			if (autoPressDelayParameter != null) {
				String [] autoPressDelayParameters = autoPressDelayParameter.split(":");
				if (autoPressDelayParameters.length == 3) {
					try {
						int minute = 0, second = 0, ms = 0; 
		
						if (autoPressDelayParameters[0] != "") {
							minute = Integer.valueOf(autoPressDelayParameters[0]);
						}
						if (autoPressDelayParameters[1] != "") {
							second = Integer.valueOf(autoPressDelayParameters[1]);
						}
						if (autoPressDelayParameters[2] != "") {
							ms = Integer.valueOf(autoPressDelayParameters[2]);
						}
						
						applyDelayPanel.setAutopresserDelay(minute, second, ms);
					}
					catch (NumberFormatException e) {
						System.err.println("The DEFAULT_AUTOPRESS_DELAY parameter is not valid");
					}
				}
				else {
					System.err.println("The DEFAULT_AUTOPRESS_DELAY parameter is not valid");
				}
			}
		}
		else {
			System.out.println("File does not exist or is not parsable");
		}
	}
	
	public static void main(String[] args)
	{
		Application.launch(args);
	}
	
	/******************************************************************************************/
	
	public boolean isDefiningAutopressedKey() {
		return defineAutopressedKeyPanel.isDefiningAutopressedKey();
	}
	
	public InputEvent getAutopressedKey() {
		return defineAutopressedKeyPanel.getAutopressedKey();
	}
	
	public long getAutopresserDelay() {
		return applyDelayPanel.getAutopresserDelay();
	}
	
	public void disableDefineKeyPanel() {
		defineAutopressedKeyPanel.disablePanel();
	}
	
	public void enableDefineKeyPanel() {
		defineAutopressedKeyPanel.enablePanel();
	}
	
	public void disableDelayPanel() {
		applyDelayPanel.disablePanel();
	}
	
	public void enableDelayPanel() {
		applyDelayPanel.enablePanel();
	}
	
	public void disableActivateAutopresserPanel() {
		activateAutopresserPanel.disablePanel();
	}
	
	public void enableActivateAutopresserPanel() {
		activateAutopresserPanel.enablePanel();
	}

}
