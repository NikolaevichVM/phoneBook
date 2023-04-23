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
	requires java.desktop;
	requires org.apache.commons.io;
	
	opens phonebook to javafx.fxml, jakarta.xml.bind;
	
	exports phonebook;
}