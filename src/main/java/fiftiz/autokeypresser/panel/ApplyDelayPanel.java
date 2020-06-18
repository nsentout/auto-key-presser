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

public class ApplyDelayPanel implements Panel
{
	/**
	 * Delay between two key press.
	 */
	private static long autoPresserDelay = MainWindow.DEFAULT_DELAY_BETWEEN_KEYPRESS;
	
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
	
	private static ApplyDelayPanel applyDelayPanel;
	
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

		minDelay = (TextField) root.lookup(FxmlConstants.MINUTE_INPUT_ID);
		secDelay = (TextField) root.lookup(FxmlConstants.SECOND_INPUT_ID);
		msDelay = (TextField) root.lookup(FxmlConstants.MILLISECOND_INPUT_ID);
		
		secDelay.setText(String.valueOf(autoPresserDelay / 1000));
		
		setApplyDelayButtonBehavior();
		setDelayTextFieldsBehavior();
	}
	
	@Override
	public void disablePanel() {
		applyDelayButton.setDisable(true);
		minDelay.setDisable(true);
		secDelay.setDisable(true);
		msDelay.setDisable(true);
	}
	
	@Override
	public void enablePanel() {
		applyDelayButton.setDisable(false);
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
					
					if (autoPresserDelay != newAutoPresserDelay) {
						autoPresserDelay = newAutoPresserDelay;
						System.out.println("APPLIED NEW DELAY: " + autoPresserDelay + " ms");
					}
				}
			}
		});
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
	
	public long getAutopresserDelay() {
		return autoPresserDelay;
	}
	
	public static ApplyDelayPanel getInstance() {
		if (applyDelayPanel == null) {
			applyDelayPanel = new ApplyDelayPanel();
		}
		return applyDelayPanel;
	}
	
}
