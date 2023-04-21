package phonebook;
import javafx.beans.property.SimpleStringProperty;

public class Person {
	
	SimpleStringProperty LastName;
	SimpleStringProperty FirstName;
	SimpleStringProperty OldName;
	SimpleStringProperty Phone;

	public Person() {
		this( null, null, null, null);
	}

	public Person(  String lastName, String firstName, String oldName, String phone) {
		LastName = new SimpleStringProperty(lastName);
		FirstName = new SimpleStringProperty(firstName);
		OldName = new SimpleStringProperty(oldName);
		Phone = new SimpleStringProperty(phone);
	}
	public String getLastName() {
		return LastName.get();
	}
	
	public String getFirstName() {
		return FirstName.get();
	}
	
	public String getOldName() {
		return OldName.get();
	}
	

	public String getPhone() {
		return Phone.get();
	}
		
	public void setLastName(String lastName) {
		this.LastName.set(lastName);
	}
	
	public void setFirstName(String firstName) {
		this.FirstName.set(firstName);
	}
	
	public void setOldName(String oldName) {
		this.OldName.set(oldName);
	}
	
	public void setPhone(String phone) {
		this.Phone.set(phone);
	}
}
