package application;

import java.util.Timer;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
 
public class MainWindow extends Application
{
	private static final int START_DELAY = 3000;	// delay before the first auto press
	private static final int TIME_BETWEEN_KEYPRESS = 1000;	// 10 minutes (600000)
	
	private static final int WINDOW_WIDTH = 230;
	private static final int WINDOW_HEIGHT = 230;
	
	private UserInput userInputChoice;
	
	private boolean isAutoTyping = false;
	private boolean isChangingKey = false;
	
	private Timer timer;
 
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FlowPane root = new FlowPane();
        
        userInputChoice = new UserInput();
  
        Button mainButton = new Button("ACTIVER AUTO PRESS");
        //mainButton.setMinSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        Button saveAutoPressKeyButton = new Button("PRESS TO SAVE THE KEY YOU WANT TO AUTO PRESS");
        
        // Autopress key
        Label keyLabel = new Label();
        
        // Activate / Desactivate auto pressing
        mainButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event)
            {
            	if (isAutoTyping) {
            		mainButton.setText("ACTIVER AUTO PRESS");
            		timer.cancel();
            	}
            	else {
            		mainButton.setText("DESACTIVER AUTO PRESS");
            		timer = new Timer();
            		timer.schedule(userInputChoice, START_DELAY, TIME_BETWEEN_KEYPRESS);
            	}
            	
        		isAutoTyping = !isAutoTyping;
            }
        });
        
        // Ask the user to input the new auto pressing key
        saveAutoPressKeyButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me)
            {
            	if (me.getButton() == MouseButton.PRIMARY) {
                   	saveAutoPressKeyButton.setText("?");
                	isChangingKey = true;
            	}
            }
        });
        
        // Save the new auto pressing key
        saveAutoPressKeyButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke)
            {
            	if (isChangingKey) {
            		System.out.println("Key that will be autopressed: " + ke.getCode());
            		
            		// Update the key that will be auto pressing
            		userInputChoice.setUserInput(ke);
            		
            		// Update the label
            		keyLabel.setText(ke.getCode().getName());
            		
            		// Reset the button allowing to change the key that will be auto pressing
            		saveAutoPressKeyButton.setText("Type the key you want to autopress");
            		isChangingKey = false;
            	}
            }
        });
       
        // Save the new auto pressing mouse click
        root.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me)
            {
            	if (isChangingKey) {
            		System.out.println("Mouse : " + me.getButton().name());
            		
            		// Update the key that will be auto pressing
            		userInputChoice.setUserInput(me);
            		
            		// Update the label
            		keyLabel.setText(me.getButton().name());
            		
            		// Reset the button allowing to change the key that will be auto pressing
            		saveAutoPressKeyButton.setText("Type the key you want to autopress");
            		isChangingKey = false;
            	}
            }
        });

        root.getChildren().addAll(mainButton, saveAutoPressKeyButton, keyLabel);
 
        primaryStage.setTitle("AutoKeyPress");
 
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
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
    }
 
    public static void main(String[] args)
    {
        Application.launch(args);
    }
 
}