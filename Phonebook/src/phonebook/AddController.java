package phonebook;

import java.util.Base64;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddController {
	private int indexIdInput;
	@FXML
	private TextField lastNameInput;
	@FXML
	private TextField firstNameInput;
	@FXML
	private TextField oldNameInput;
	@FXML
	private TextField phoneInput;
	
	private String avatarInput;
	
	private Person person;
	
	BookController bookController;
	
	private boolean okClicked = false;
	
	
	

	private Stage dialogStage;

	@FXML
	private void initialize() {
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setPerson(Person person) {
		this.person = person;
		indexIdInput = person.getIndexId();
		lastNameInput.setText(person.getLastName());
		firstNameInput.setText(person.getFirstName());
		oldNameInput.setText(person.getOldName());
		phoneInput.setText(person.getPhone());
		avatarInput = person.getAvatar();
	}

	public boolean isOkClicked() {
		return okClicked;
	}
	
	@SuppressWarnings("unused")
	@FXML
	private void addPhoto() throws IOException{
		FileChooser fileChooser = new FileChooser();

		// Задаем фильтр расширений
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
		fileChooser.getExtensionFilters().add(extFilter);

		// Показываем диалог загрузки файла и перекодируем изображение в формат Base64
		File file = fileChooser.showOpenDialog(dialogStage);
		String fileInput = file.getAbsolutePath();
		if (file != null) {
			byte[] fileContent = FileUtils.readFileToByteArray(new File(fileInput));
			avatarInput = Base64.getEncoder().encodeToString(fileContent);
		}
	}
	
	@FXML
	private void handleOk() throws ClassNotFoundException, SQLException{
		if (isInputValid()) {
			person.setIndexId(indexIdInput);
			person.setFirstName(firstNameInput.getText());
			person.setLastName(lastNameInput.getText());
			person.setOldName(oldNameInput.getText());
			person.setPhone(phoneInput.getText());
			person.setAvatar(avatarInput);
			okClicked = true;
			dialogStage.close();
			}
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
	
	private boolean isInputValid() throws ClassNotFoundException, SQLException {
		String errorMessage = "";
		BookController inPhone = new BookController();
		boolean inPhoneext = inPhone.isInputPhone(phoneInput.getText(),person.getIndexId());
		System.out.println(inPhoneext);
		if (lastNameInput.getText() == null || lastNameInput.getText().length() == 0) {
			errorMessage += "Поле <Фамилия> не заполнено!\n";
		}
		if (firstNameInput.getText() == null || firstNameInput.getText().length() == 0) {
			errorMessage += "Поле <Имя> не заполнено!\n";
		}

		if (oldNameInput.getText() == null || oldNameInput.getText().length() == 0) {
			errorMessage += "Поле <Отчество> не заполнено!\n";
		}

		if (phoneInput.getText() == null || phoneInput.getText().length() == 0) {
			errorMessage += "Поле <Номер> не заполнено!\n";
		}
		if (phoneInput.getText() == null || phoneInput.getText().length() == 0 || inPhoneext) {
			errorMessage += "Такой номер уже существует!\n";
		}
//		
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
	
}
