package phonebook;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.io.OutputStream;

public class AddDBController {
	@FXML
	private TextField hostDB;
	@FXML
	private TextField portDB;
	@FXML
	private TextField nameDB;
	@FXML
	private TextField userDB;
	@FXML
	private PasswordField passwdDB;
	
	private Stage dialogStage;
	
	private boolean okClicked = false;
	@FXML
	private void initialize() {
	}
	public boolean isOkClicked() {
		return okClicked;
	}
	@FXML
	private void handleOk() throws IOException {
		if (isInputValid()) {
			Properties properties = new Properties();
			properties.setProperty("host", hostDB.getText());
			properties.setProperty("port",portDB.getText());
			properties.setProperty("nameDatabase",nameDB.getText());
			properties.setProperty("username", userDB.getText());
			properties.setProperty("password", passwdDB.getText());
			File currentDirectory = new File(new File("database.properties").getAbsolutePath());
			OutputStream out = new FileOutputStream(currentDirectory);
			properties.store(out, null);
	        
	        okClicked = true;		
			dialogStage.close();
		}
	}
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
	//Метод который проверят содержимое на наличие данных в водимых полях
	private boolean isInputValid() {
		String errorMessage = "";
		if (hostDB.getText() == null || hostDB.getText().length() == 0) {
			errorMessage += "Поле <Адрес> не заполнено!\n";
		}
		if (portDB.getText() == null || portDB.getText().length() == 0) {
			errorMessage += "Поле <Порт> не заполнено!\n";
		}
		if (nameDB.getText() == null || nameDB.getText().length() == 0) {
			errorMessage += "Поле <Имя базы> не заполнено!\n";
		}
		
		if (userDB.getText() == null || userDB.getText().length() == 0) {
			errorMessage += "Поле <Пользователь> не заполнено!\n";
		}

		if (passwdDB.getText() == null || passwdDB.getText().length() == 0) {
			errorMessage += "Поле <Пароль> не заполнено!\n";
		}
		if (errorMessage.length() == 0) {
			return true;
		} else {
			// Show the error message.
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("Ошибка ввода");
			alert.setHeaderText("Пожалуйста введите данные");
			alert.setContentText(errorMessage);

			alert.showAndWait();

			return false;
		}
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
}
