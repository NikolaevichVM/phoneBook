package phonebook;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class PhoneBook extends Application {

	private Stage primaryStage;

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	private VBox rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		initRootLayout();		
	}

	public void initRootLayout() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(PhoneBook.class.getResource("/phonebook/fxml/Main.fxml"));
			rootLayout = (VBox) fxmlLoader.load();
			Scene scene = new Scene(rootLayout);
			primaryStage.setTitle("Телефонный справочник");
			primaryStage.getIcons().add(new Image(PhoneBook.class.getResourceAsStream("/phonebook/img/address_blue_book_icon_32.png")));
			primaryStage.setScene(scene);
			primaryStage.show();
			BookController controller = fxmlLoader.getController();
			controller.setPhoneBook(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public boolean addDialog(Person person) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(PhoneBook.class.getResource("/phonebook/fxml/add.fxml"));
			AnchorPane page = (AnchorPane) fxmlLoader.load();
			Stage dialogStage = new Stage();
			Scene scene = new Scene(page);
			dialogStage.setTitle("Добавление записи");
			dialogStage.getIcons().add(new Image(PhoneBook.class.getResourceAsStream("/phonebook/img/address_blue_book_icon_32.png")));
			dialogStage.setScene(scene);

			// Set the person into the controller.
			AddController controller = fxmlLoader.getController();
			controller.setDialogStage(dialogStage);
			controller.setPerson(person);
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean DBDialog() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(PhoneBook.class.getResource("/phonebook/fxml/addDB.fxml"));
			AnchorPane page = (AnchorPane) fxmlLoader.load();
			Stage dialogStage = new Stage();
			Scene scene = new Scene(page);
			dialogStage.setTitle("Подключение к БД");
			dialogStage.getIcons().add(new Image(PhoneBook.class.getResourceAsStream("/phonebook/img/address_blue_book_icon_32.png")));
			dialogStage.setScene(scene);

			AddDBController controller = fxmlLoader.getController();
			controller.setDialogStage(dialogStage);
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}