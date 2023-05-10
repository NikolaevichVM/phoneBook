# phoneBook
To run the application, you need to set up an environment variable. 
Download and extract javaSDK 17.0.6, set up environment variables on your PC.(https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) 
Download and extract javaFX 20.(https://gluonhq.com/products/javafx/) 
Download the file database.properties and phonebook.jar.
Run a command line and go to the directory with the phonebook.jar file.
Run the project using the key below (adjust the path to the javaFX libraries if necessary).
java --module-path "C:\java\javafx-sdk-20\lib" --add-modules javafx.controls,javafx.fxml -jar C:\java\jar\phonebook.jar
