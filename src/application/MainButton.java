package application;

import java.util.Timer;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
 
public class MainButton extends Application
{
	private static final int START_DELAY = 3000;	// delay before the first auto press
	private static final int TIME_BETWEEN_KEYPRESS = 600000;	// 10 minutes
	
	private static final int WINDOW_WIDTH = 230;
	private static final int WINDOW_HEIGHT = 230;
	
	private boolean isAutoTyping = false;
	
	private Timer timer;
 
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FlowPane root = new FlowPane();
  
        Button mainButton = new Button("ACTIVER AUTO SPACE");
        //mainButton.setMinSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        mainButton.setOnAction(new EventHandler<ActionEvent>() {
        	 
            @Override
            public void handle(ActionEvent event) {
            	
            	if (isAutoTyping) {
            		mainButton.setText("ACTIVER AUTO SPACE");
            		timer.cancel();
            	}
            	else {
            		mainButton.setText("DESACTIVER AUTO SPACE");
            		timer = new Timer();
            		timer.schedule(new PressKey(), START_DELAY, TIME_BETWEEN_KEYPRESS);
            	}
            	
        		isAutoTyping = !isAutoTyping;
            }
        });
        
        root.getChildren().add(mainButton);
 
        primaryStage.setTitle("Auto Space");
 
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