package fiftiz.autokeypresser.panel;

import fiftiz.autokeypresser.MainWindow;
import javafx.scene.Parent;

public interface Panel
{
	public void init(Parent root, MainWindow mainWindow);
	public void enablePanel();
	public void disablePanel();
}
