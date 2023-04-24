package phonebook;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

public class BookController {

	@FXML
	private TextField filterField;
	@FXML
	public TableView<Person> personTable;
	@FXML
	public TableColumn<Person, String> lastName;
	@FXML
	public TableColumn<Person, String> firstName;
	@FXML
	public TableColumn<Person, String> oldName;
	@FXML
	public TableColumn<Person, String> phone;
	@FXML
	public ImageView avatar;
	@FXML
	private Button editButton, addButton, delButton;
	@FXML
	private MenuItem fileOpen, dbSave, fileSaveAs, newFile, exitApp, openDB, newDB, saveDB;
	private PhoneBook phoneBook;
	
	private ObservableList<Person> personData = FXCollections.observableArrayList();
	
	private File personFile = getPersonFilePath();

	public BookController() throws ClassNotFoundException, SQLException {
		File file = getPersonFilePath();
		if (file != null) {
	        loadPersonDataFromFile(file);
	       }
	    else {
	    	loadDB();
	    	}
	}
	
	@FXML
	public void initialize() {
		avatar.setImage(new Image(PhoneBook.class.getResourceAsStream("/phonebook/img/default.jpg")));
		FilteredList<Person> filteredData = new FilteredList<>(personData, p -> true);
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(person -> {
				// If filter text is empty, display all persons.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();

				if (person.getLastName().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches first name.
				} else if (person.getPhone().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches phone.
				}
				return false; // Does not match.
			});
		});
		SortedList<Person> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(personTable.comparatorProperty());
		personTable.setItems(sortedData);
		
		lastName.setCellValueFactory(new PropertyValueFactory<>("LastName"));
		firstName.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
		oldName.setCellValueFactory(new PropertyValueFactory<>("OldName"));
		phone.setCellValueFactory(new PropertyValueFactory<>("Phone"));
		//Edit table colum
		
		lastName.setCellFactory(TextFieldTableCell.forTableColumn());
		lastName.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setLastName(e.getNewValue()));

		firstName.setCellFactory(TextFieldTableCell.forTableColumn());
		firstName.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setFirstName(e.getNewValue()));

		oldName.setCellFactory(TextFieldTableCell.forTableColumn());
		oldName.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setOldName(e.getNewValue()));

		phone.setCellFactory(TextFieldTableCell.forTableColumn());
		phone.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setPhone(e.getNewValue()));
		personTable.setEditable(false);

	}
	
	 @FXML
	   void setClickAvatar(MouseEvent event) throws IOException {
		 Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
			String selectedAvatar = personTable.getSelectionModel().getSelectedItem().getAvatar();
			if (selectedPerson != null) {
				if(selectedAvatar != null) {
					String encodedString = personTable.getSelectionModel().getSelectedItem().getAvatar();
					WritableImage writableImage = encodedString(encodedString);
			        avatar.setImage(writableImage);
					}else {
					avatar.setImage(new Image(PhoneBook.class.getResourceAsStream("/phonebook/img/default.jpg")));
					}
			}
	 }
	
	public WritableImage encodedString (String encodedString) throws IOException{
		byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
		ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
		BufferedImage image = ImageIO.read(bais);
		WritableImage writableImage = null;
        if (image != null) {
        	writableImage = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = writableImage.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }
        return writableImage;
	}
	 
	public void setPhoneBook(PhoneBook phoneBook) {
		this.phoneBook = phoneBook;
	}

	@FXML
	void handleButtonClick(ActionEvent event) throws ClassNotFoundException, SQLException, IOException {
		if (event.getSource() == editButton) {
			handleEditPerson();
		}
		if (event.getSource() == addButton) {
			handleNewPerson();
		}
		if (event.getSource() == delButton) {
			handleDeletePerson();
		}
		if (event.getSource() == fileOpen) {
			fileOpen();
		}
		if (event.getSource() == dbSave) {
			fileSave();
		}
		if (event.getSource() == newFile) {
			handleNew();
		}
		if (event.getSource() == fileSaveAs) {
			handleSaveAs();
		}
		if (event.getSource() == exitApp) {
			handleExit();
		}
		if (event.getSource() == openDB) {
			setPersonFilePath(null);
			handleOpenDB();			
		}
		if (event.getSource() == newDB) {
			setPersonFilePath(null);
			handleNewDB();			
		}
		if (event.getSource() == saveDB) {
			setPersonFilePath(null);
			handleSaveDB();		
		}
					
	}
	
	private void handleNew() {
		personData.clear();
		
	}
	//Метод для удаления выбранной записи
	private void handleDeletePerson() throws ClassNotFoundException, SQLException {
		Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
		int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			if (personFile != null) {
				personData.remove(selectedIndex);
				avatar.setImage(new Image(PhoneBook.class.getResourceAsStream("/phonebook/img/default.jpg")));
			}
			else {
				deletePersonDB(selectedPerson);
				personData.remove(selectedIndex);
				avatar.setImage(new Image(PhoneBook.class.getResourceAsStream("/phonebook/img/default.jpg")));
			}
		}
	}
	
	private void handleExit() {
		System.exit(0);
	}
	private void handleNewPerson() throws ClassNotFoundException, SQLException, IOException {
		Person tempPerson = new Person();
		boolean okClicked = phoneBook.addDialog(tempPerson);
		if (okClicked) {
			if (personFile != null) {
			personData.add(tempPerson);
			String encodedString = tempPerson.getAvatar();
			WritableImage writableImage = encodedString(encodedString);
	        avatar.setImage(writableImage);
			}
			else {
				addPersonDB(tempPerson);
				personData.add(tempPerson);
				String encodedString = tempPerson.getAvatar();
				WritableImage writableImage = encodedString(encodedString);
		        avatar.setImage(writableImage);
			}
		}
			
	}
	private void handleEditPerson() throws IOException, ClassNotFoundException, SQLException {
		Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
		int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
		if (selectedPerson != null) {
				boolean okClicked = phoneBook.addDialog(selectedPerson);
				if (okClicked) {
					if (personFile != null) {
						personData.set(selectedIndex, selectedPerson);
						String encodedString = personTable.getSelectionModel().getSelectedItem().getAvatar();
						WritableImage writableImage = encodedString(encodedString);
				        avatar.setImage(writableImage);
						}
					else {
						personData.set(selectedIndex, selectedPerson);
						editPersonDB(selectedPerson);
						String encodedString = personTable.getSelectionModel().getSelectedItem().getAvatar();
						WritableImage writableImage = encodedString(encodedString);
				        avatar.setImage(writableImage);
					}
				}	
			} 
		else {
			// Nothing selected.
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.initOwner(phoneBook.getPrimaryStage());
			alert.setTitle("Ошибка");
			alert.setHeaderText("Запись не выбрана");
			alert.setContentText("Для изменения выберите конкретную запись в таблице");

			alert.showAndWait();
		}
	}
	private void fileOpen() {
		FileChooser fileChooser = new FileChooser();

		// Задаем фильтр расширений
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// Показываем диалог загрузки файла
		File file = fileChooser.showOpenDialog(phoneBook.getPrimaryStage());

		if (file != null) {
			loadPersonDataFromFile(file);
		}
	}
	private void fileSave() {
		File personFile = getPersonFilePath();

		if (personFile != null) {
			savePersonDataToFile(personFile);
		} else {
			handleSaveAs();
		}
	}
	//Метод для создание новой записи
	



	public File getPersonFilePath() {
		Preferences prefs = Preferences.userNodeForPackage(BookController.class);
		String filePath = prefs.get("filePath", null);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}

	public void setPersonFilePath(File file) {
		Preferences prefs = Preferences.userNodeForPackage(BookController.class);
		if (file != null) {
			prefs.put("filePath", file.getPath());

		} else {
			prefs.remove("filePath");

		}
	}

	public void loadPersonDataFromFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			// Чтение XML из файла и демаршализация.
			PersonListWrapper wrapper = (PersonListWrapper) unmarshaller.unmarshal(file);

			personData.clear();
			personData.addAll(wrapper.getPersons());

			// Сохраняем путь к файлу в реестре.
			setPersonFilePath(file);

		} catch (Exception e) { // catches ANY exception
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Ошибка");
			alert.setHeaderText("Данные не загружены");
			alert.setContentText("Не удалось загрузить данные из файла:\n" + file.getPath());

			alert.showAndWait();
		}
	}

	public void savePersonDataToFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Оборачиваем наши данные об адресатах.
			PersonListWrapper wrapper = new PersonListWrapper();
			wrapper.setPersons(personData);

			// Маршаллируем и сохраняем XML в файл.
			marshaller.marshal(wrapper, file);

			// Сохраняем путь к файлу в реестре.
			setPersonFilePath(file);
		   

		} catch (Exception e) { // catches ANY exception
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Ошибка");
			alert.setHeaderText("Данные не сохранены");
			alert.setContentText("Данные не сохранены в файл:\n" + file.getPath());

			alert.showAndWait();
		}
	}
	
	//Метод для меню Сохранить как...
	private void handleSaveAs() {
		FileChooser fileChooser = new FileChooser();

		// Задаем фильтр расширений
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);

		// Показываем диалог сохранения файла
		File file = fileChooser.showSaveDialog(phoneBook.getPrimaryStage());

		if (file != null) {
			// Make sure it has the correct extension
			if (!file.getPath().endsWith(".xml")) {
				file = new File(file.getPath() + ".xml");
			}
			savePersonDataToFile(file);
		}
	}
	
	private ArrayList<Person> dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<Person> data =  new ArrayList<>();
        while (resultSet.next()) {
            Person person = new Person();
            person.setIndexId(resultSet.getInt("indexId"));
            person.setLastName(resultSet.getString("lastName"));
            person.setFirstName(resultSet.getString("firstName"));
            person.setOldName(resultSet.getString("oldName"));
            person.setPhone(resultSet.getString("phone"));
            person.setAvatar(resultSet.getString("avatar"));
            data.add(person);
        }
        return data;
    }
	
	@SuppressWarnings("exports")
	public static Connection getConnection() throws SQLException, IOException{
        
        Properties props = new Properties();
        try(InputStream in = Files.newInputStream(Paths.get("src/database.properties"))){
            props.load(in);
        }
        String url = "jdbc:mysql://"+props.getProperty("host")+":"+props.getProperty("port")+"/"+props.getProperty("nameDatabase")+"?enabledTLSProtocols=TLSv1.2";
        String username = props.getProperty("username");
        String password = props.getProperty("password");
        
        return DriverManager.getConnection(url, username, password);
    }
	private void handleOpenDB() throws ClassNotFoundException, SQLException {
		boolean okClicked = phoneBook.DBDialog();
		if (okClicked) {
			personData.clear();
			loadDB();
			
		}	
	}
	private void handleNewDB() throws ClassNotFoundException, SQLException {
		boolean okClicked = phoneBook.DBDialog();
		if (okClicked) {
			personData.clear();
			try {Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
			Properties props = new Properties();
			InputStream in = Files.newInputStream(Paths.get("src/database.properties"));
	        props.load(in);
	        String url = "jdbc:mysql://"+props.getProperty("host")+":"+props.getProperty("port")+"/";
	        String username = props.getProperty("username");
	        String password = props.getProperty("password");
	        
				try (Connection dbConnection = DriverManager.getConnection(url, username, password);){
					String createBaseQeury ="CREATE DATABASE IF NOT EXISTS "+props.getProperty("nameDatabase");
					String useQuery ="USE "+props.getProperty("nameDatabase");
					String createQuery = "create table `persons` (indexId int, lastName VARCHAR(25),firstName VARCHAR(25),oldName VARCHAR(25),phone VARCHAR(25),PRIMARY KEY(indexId),avatar TEXT() NULL);";
					Statement statement = dbConnection.createStatement();
					statement.executeUpdate(createBaseQeury);
					statement.executeUpdate(useQuery);
	            	statement.executeUpdate(createQuery);
	            	System.out.println("New creat Base.");
	            	statement.close();
	        	}
				catch (SQLException e) {
	        		e.printStackTrace();
	        	}
			}
			catch(Exception ex){
				System.out.println("Connection failed...");
             
				System.out.println(ex);
			}
		}	
	}
	
	private void handleSaveDB() throws ClassNotFoundException, SQLException {
		try {Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
			try (Connection dbConnection = getConnection()){
				String delQuery = "DELETE FROM persons;";
				Statement statement = dbConnection.createStatement();
			 	statement.executeUpdate(delQuery);
				try (PreparedStatement preparedStatement = dbConnection.prepareStatement("INSERT INTO `persons` (`lastName`, `firstName`, `oldName`, `phone`, `avatar`) values(?,?,?,?,?)")) {
                    for (Person person : personData) {
                    	preparedStatement.setString(1, person.getLastName());
                        preparedStatement.setString(2, person.getFirstName());
                        preparedStatement.setString(3, person.getOldName());
                        preparedStatement.setString(4, person.getPhone());
                        preparedStatement.setString(5, person.getAvatar());
                        preparedStatement.addBatch();
                    }
                    preparedStatement.executeBatch();
                }
				statement.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
			catch(Exception ex){
				System.out.println("Connection failed...");
             
				System.out.println(ex);
			}
       
	}
	private void loadDB() throws ClassNotFoundException, SQLException {
		try {Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
			try (Connection dbConnection = getConnection()){
				Statement statement = dbConnection.createStatement();
				ResultSet resultSet = null;
				String selectQeury = "SELECT * FROM persons;";
				try {
					PreparedStatement preparedStatement = dbConnection.prepareStatement(selectQeury);
					resultSet = preparedStatement.executeQuery();
					ObservableList<Person> personData_db = FXCollections.observableArrayList(dataBaseArrayList(resultSet));
					personData.addAll(personData_db);
					System.out.println("Connect OK.");
					statement.close();
					}
				catch (SQLException e) {
					e.printStackTrace();
					}
				}	
			}
			catch(Exception ex){
			System.out.println("Connection failed...");
            System.out.println(ex);
			}
	}
	

	private void addPersonDB(Person tempPerson) throws ClassNotFoundException, SQLException {
		try {Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
			try (Connection dbConnection = getConnection()){
				Statement statement = dbConnection.createStatement();
			 	try (PreparedStatement preparedStatement = dbConnection.prepareStatement("INSERT INTO `persons` (`lastName`, `firstName`, `oldName`, `phone`, `avatar`) values(?,?,?,?,?)")) {
				 		preparedStatement.setString(1, tempPerson.getLastName());
                        preparedStatement.setString(2, tempPerson.getFirstName());
                        preparedStatement.setString(3, tempPerson.getOldName());
                        preparedStatement.setString(4, tempPerson.getPhone());
                        preparedStatement.setString(5, tempPerson.getAvatar());
                        preparedStatement.addBatch();
                        preparedStatement.executeBatch();
                }
			 	
				statement.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
			catch(Exception ex){
				System.out.println("Connection failed...");
             
				System.out.println(ex);
			}
       
	}
	
	private void editPersonDB(Person selectedPerson) throws ClassNotFoundException, SQLException {
		try {Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
			try (Connection dbConnection = getConnection()){
				Statement statement = dbConnection.createStatement();
				String query = "UPDATE `persons` SET `lastName` = (?), `firstName` = (?), `oldName` = (?), `phone` = (?), `avatar` = (?) WHERE `persons`.`indexId` = "+selectedPerson.getIndexId();
				try (PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
				 		preparedStatement.setString(1, selectedPerson.getLastName());
                        preparedStatement.setString(2, selectedPerson.getFirstName());
                        preparedStatement.setString(3, selectedPerson.getOldName());
                        preparedStatement.setString(4, selectedPerson.getPhone());
                        preparedStatement.setString(5, selectedPerson.getAvatar());
                        preparedStatement.addBatch();
                        preparedStatement.executeBatch();
                }
			 	
				statement.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
			catch(Exception ex){
				System.out.println("Connection failed...");
             
				System.out.println(ex);
			}
       
	}
	
	private void deletePersonDB(Person selectedPerson) throws ClassNotFoundException, SQLException {
		try {Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
			try (Connection dbConnection = getConnection()){
				String deleteQeury = "DELETE FROM persons WHERE `persons`.`indexId` ="+selectedPerson.getIndexId();
				try {
					Statement statement = dbConnection.createStatement();
					statement.executeUpdate(deleteQeury);
					statement.close();
					}
				catch (SQLException e) {
					e.printStackTrace();
					}
				}	
			}
			catch(Exception ex){
			System.out.println("Connection failed...");
            System.out.println(ex);
			}
	}
	
	public ObservableList<Person> getPersonData() {
		return personData;
	}

	public void setPersonData(ObservableList<Person> personData) {
		this.personData = personData;
	}
	
	public boolean isInputPhone(String phoneIn, int indexIn) {
		boolean inPhone = false;
		for (Person person : personData) {
			if (phoneIn.equalsIgnoreCase(person.getPhone()) && indexIn != person.getIndexId()) {
				System.out.println(phoneIn+" "+person.getPhone()+" "+indexIn+" "+person.getIndexId());
				inPhone = true;
				break;
				}	
			}
		return inPhone;
	}
	
}
