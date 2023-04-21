/**
 * 
 */
/**
 * @author Mikhalev
 *
 */
module Phonebook {
	requires transitive javafx.graphics;
	requires javafx.controls;
	requires javafx.fxml;
	requires java.prefs;
	requires javafx.base;
	requires jakarta.xml.bind;
	requires java.sql;
	
	opens phonebook to javafx.fxml, jakarta.xml.bind;
	
	exports phonebook;
}