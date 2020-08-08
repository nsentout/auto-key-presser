package fiftiz.autokeypresser.panel;

import java.util.function.UnaryOperator;

import fiftiz.autokeypresser.FxmlConstants;
import fiftiz.autokeypresser.LanguageConstants;
import fiftiz.autokeypresser.MainWindow;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.input.KeyEvent;

public class ApplyDelayPanel implements Panel
{
	/**
	 * Delay between two key press.
	 */
	private static long autoPresserDelay = MainWindow.DEFAULT_DELAY_BETWEEN_KEYPRESS;
	
	private boolean applyButtonMustStayDisable;
	
	/**
	 * Input for the minutes.
	 */
	private TextField minDelay;
	
	/**
	 * Input for the seconds.
	 */
	private TextField secDelay;
	
	/**
	 * Input for the milliseconds.
	 */
	private TextField msDelay;

	/**
	 * Button to press to apply a new delay.
	 */
	private Button applyDelayButton;
	
	/**
	 * Singleton instance;
	 */
	private static ApplyDelayPanel instance;
	
	/**
	 * Parent window.
	 */
	private static MainWindow parent;
	
	
	private ApplyDelayPanel() { }
	
	@Override
	public void init(Parent root, MainWindow mainWindow)
	{
		parent = mainWindow;
		
		applyDelayButton = (Button) root.lookup(FxmlConstants.APPLY_DELAY_BUTTON_ID);
		applyDelayButton.setText(LanguageConstants.APPLY_DELAY_TEXT);
		applyDelayButton.setDisable(true);

		minDelay = (TextField) root.lookup(FxmlConstants.MINUTE_INPUT_ID);
		secDelay = (TextField) root.lookup(FxmlConstants.SECOND_INPUT_ID);
		msDelay = (TextField) root.lookup(FxmlConstants.MILLISECOND_INPUT_ID);
		
		secDelay.setText(String.valueOf(autoPresserDelay / 1000));
		
		setApplyDelayButtonBehavior();
		setDelayTextFieldsBehavior();
		
		minDelay.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				handleNewDelayInput();
			}
		});
		
		secDelay.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				handleNewDelayInput();
			}
		});
		
		msDelay.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				handleNewDelayInput();
			}
		});
		
	}
	
	@Override
	public void disablePanel() {
		applyButtonMustStayDisable = applyDelayButton.isDisable();
		applyDelayButton.setDisable(true);
		minDelay.setDisable(true);
		secDelay.setDisable(true);
		msDelay.setDisable(true);
	}
	
	@Override
	public void enablePanel() {
		applyDelayButton.setDisable(applyButtonMustStayDisable);
		minDelay.setDisable(false);
		secDelay.setDisable(false);
		msDelay.setDisable(false);
	}
	
	private void setApplyDelayButtonBehavior()
	{
		applyDelayButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event)
			{
				long newAutoPresserDelay = getTotalDelayMs();

				if (autoPresserDelay != newAutoPresserDelay) {
					autoPresserDelay = newAutoPresserDelay;
					applyDelayButton.setDisable(true);
					System.out.println("APPLIED NEW DELAY: " + autoPresserDelay + " ms");
				}
			}
		});
	}
	
	private long getTotalDelayMs() {
		long newAutoPresserDelay = 0;
		
		if (!minDelay.getText().isEmpty() || !secDelay.getText().isEmpty() || !msDelay.getText().isEmpty()) {
			if (!minDelay.getText().isEmpty()) {
				newAutoPresserDelay += Long.parseLong(minDelay.getText()) * 60000;
			}

			if (!secDelay.getText().isEmpty()) {
				newAutoPresserDelay += Long.parseLong(secDelay.getText()) * 1000;
			}

			if (!msDelay.getText().isEmpty()) {
				newAutoPresserDelay += Long.parseLong(msDelay.getText());
			}
			
		}
		
		return newAutoPresserDelay;
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
	
	private void handleNewDelayInput() {
		long newAutoPresserDelay = getTotalDelayMs();
		
		if (autoPresserDelay == newAutoPresserDelay) {
			applyDelayButton.setDisable(true);
		}
		else {
			applyDelayButton.setDisable(false);
		}
	}
	
	public long getAutopresserDelay() {
		return autoPresserDelay;
	}
	
	public static ApplyDelayPanel getInstance() {
		if (instance == null) {
			instance = new ApplyDelayPanel();
		}
		return instance;
	}
	
}
