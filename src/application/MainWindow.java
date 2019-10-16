package application;

import java.util.Timer;
import java.util.function.UnaryOperator;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
 
public class MainWindow extends Application
{
	private static final int START_DELAY = 3000;	// delay before the first auto press
	private static final long DEFAULT_DELAY_BETWEEN_KEYPRESS = 1000;
	
	private static final int WINDOW_WIDTH = 400;
	private static final int WINDOW_HEIGHT = 400;
	
	// Task that will be run every <autoPressDelay> ms
	private AutoPressTask autoPressTask;
	
	private boolean isAutoTyping = false;
	private boolean isChangingKey = false;
	
	// Timer used to run <autoPressTask> every <autoPressDelay> ms
	private Timer timer;
	
	private InputEvent autoPressedKey;
	private long autoPressDelay = DEFAULT_DELAY_BETWEEN_KEYPRESS;
 
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        autoPressTask = new AutoPressTask();
        timer = new Timer();
        
        Parent root = FXMLLoader.load(getClass().getResource("layout.fxml"));
        
        Button autoPressButton = (Button) root.lookup("#autoPressButton");
        Button defineKeyButton = (Button) root.lookup("#defineKeyButton");
        Button applyDelayButton = (Button) root.lookup("#applyDelayButton");
        Label keyLabel = (Label) root.lookup("#autoPressedKey");
        keyLabel.setBackground(new Background(new BackgroundFill(Color.rgb(230, 230, 230), CornerRadii.EMPTY, Insets.EMPTY)));
        
        TextField minDelay = (TextField) root.lookup("#minDelay");
        TextField secDelay = (TextField) root.lookup("#secDelay");
        TextField msDelay = (TextField) root.lookup("#msDelay");
        
        // Activate / Desactivate auto pressing
        autoPressButton.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
            public void handle(ActionEvent event)
            {
        		if (autoPressedKey != null) {
	        		isAutoTyping = !isAutoTyping;
	        		
	        		if (isAutoTyping) {
	        			autoPressButton.setText("DÉSACTIVER AUTOPRESS");
	            		
	            		// Update the key that will be auto pressing
	            		autoPressTask = new AutoPressTask();
	            		autoPressTask.setUserInput(autoPressedKey);
	            		
	            		timer = new Timer();
	            		timer.schedule(autoPressTask, START_DELAY, autoPressDelay);
	            		
	            	}
	            	else {
	            		autoPressButton.setText("ACTIVER AUTOPRESS");
	            		timer.cancel();
	            	}
        		}  		
            }
        });
        
        // Ask the user to input the new auto pressing key
        defineKeyButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
        	@Override
            public void handle(MouseEvent me)
            {
            	if (me.getButton() == MouseButton.PRIMARY) {
                   	defineKeyButton.setText("?");
                	isChangingKey = true;
            	}
            }
        });
        
        // Save the new auto pressing key
        defineKeyButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
        	@Override
            public void handle(KeyEvent ke)
            {
            	if (isChangingKey) {
            		System.out.println("Key that will be autopressed: " + ke.getCode());
            		
            		// Center the click button label
            		((HBox) root.lookup("#defineKeyBox")).setSpacing(150.0);
            		
            		// Update the key that will be auto pressing
            		//userInputChoice.setUserInput(ke);
            		autoPressedKey = ke;
            		
            		// Update the label
            		keyLabel.setText(ke.getCode().getName());
            		
            		// Reset the button allowing to change the key that will be auto pressing
            		defineKeyButton.setText("Définir touche");
            		isChangingKey = false;
            	}
            }
        });
       
        // Save the new auto pressing mouse click
        root.setOnMouseClicked(new EventHandler<MouseEvent>() {
        	@Override
            public void handle(MouseEvent me)
            {
            	if (isChangingKey) {
            		System.out.println("Mouse : " + me.getButton().name());
            		
            		// Center the click button label
            		((HBox) root.lookup("#defineKeyBox")).setSpacing(40.0);
            		
            		// Update the key that will be auto pressing
            		//userInputChoice.setUserInput(me);
            		autoPressedKey = me;
            		
            		// Update the label
            		keyLabel.setText(me.getButton().name());
            		
            		// Reset the button allowing to change the key that will be auto pressing
            		defineKeyButton.setText("Définir touche");
            		isChangingKey = false;
            	}
            }
        });
        
        // Apply a new delay
        applyDelayButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
        	@Override
            public void handle(MouseEvent me)
            {
        		boolean inputFieldsEmpty = true;
        		autoPressDelay = 0;
        		
        		if (!minDelay.getText().isEmpty()) {
        			inputFieldsEmpty = false;
        			autoPressDelay += Long.parseLong(minDelay.getText()) * 60000;
        		}
        		
				if (!secDelay.getText().isEmpty()) {
					inputFieldsEmpty = false;
					autoPressDelay += Long.parseLong(secDelay.getText()) * 1000;
				}
				
				if (!msDelay.getText().isEmpty()) {
					inputFieldsEmpty = false;
					autoPressDelay += Long.parseLong(msDelay.getText());
				}
				
				if (inputFieldsEmpty) {
					autoPressDelay = DEFAULT_DELAY_BETWEEN_KEYPRESS;
				}
            }
        });
        
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
        if (isAutoTyping) {
        	timer.cancel();
        }
        System.exit(0);
    }
 
    public static void main(String[] args)
    {
        Application.launch(args);
    }
 
}