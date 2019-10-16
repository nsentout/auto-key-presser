package application;

import java.util.Timer;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
	private static final int TIME_BETWEEN_KEYPRESS = 1000;	// 10 minutes (600000)
	
	private static final int WINDOW_WIDTH = 400;
	private static final int WINDOW_HEIGHT = 400;
	
	private UserInput userInputChoice;
	
	private boolean isAutoTyping = false;
	private boolean isChangingKey = false;
	
	private Timer timer;
 
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        userInputChoice = new UserInput();
        
        Parent root = FXMLLoader.load(getClass().getResource("layout.fxml"));
        
        Button autoPressButton = (Button) root.lookup("#autoPressButton");
        Button defineKeyButton = (Button) root.lookup("#defineKeyButton");
        Label keyLabel = (Label) root.lookup("#autoPressedKey");
        keyLabel.setBackground(new Background(new BackgroundFill(Color.rgb(230, 230, 230), CornerRadii.EMPTY, Insets.EMPTY)));
        
        // Activate / Desactivate auto pressing
        autoPressButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event)
            {
            	if (isAutoTyping) {
            		autoPressButton.setText("ACTIVER AUTOPRESS");
            		timer.cancel();
            	}
            	else {
            		autoPressButton.setText("DÉSACTIVER AUTOPRESS");
            		timer = new Timer();
            		timer.schedule(userInputChoice, START_DELAY, TIME_BETWEEN_KEYPRESS);
            	}
        		isAutoTyping = !isAutoTyping;
            }
        });
        
        // Ask the user to input the new auto pressing key
        defineKeyButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
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
            public void handle(KeyEvent ke)
            {
            	if (isChangingKey) {
            		System.out.println("Key that will be autopressed: " + ke.getCode());
            		
            		// Center the click button label
            		((HBox) root.lookup("#defineKeyBox")).setSpacing(150.0);
            		
            		// Update the key that will be auto pressing
            		userInputChoice.setUserInput(ke);
            		
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
            public void handle(MouseEvent me)
            {
            	if (isChangingKey) {
            		System.out.println("Mouse : " + me.getButton().name());
            		
            		// Center the click button label
            		((HBox) root.lookup("#defineKeyBox")).setSpacing(40.0);
            		
            		// Update the key that will be auto pressing
            		userInputChoice.setUserInput(me);
            		
            		// Update the label
            		keyLabel.setText(me.getButton().name());
            		
            		// Reset the button allowing to change the key that will be auto pressing
            		defineKeyButton.setText("Définir touche");
            		isChangingKey = false;
            	}
            }
        });
        
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
    }
 
    public static void main(String[] args)
    {
        Application.launch(args);
    }
 
}