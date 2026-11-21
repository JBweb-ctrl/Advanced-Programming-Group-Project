package jm.edu.utech.helpdesk.service;
//this was a class for demo info and testing the GUI 
/*import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import jm.edu.utech.helpdesk.model.Technician;

public class TechnicianService {

	private static final TechnicianService INSTANCE = new TechnicianService();
	
	private final ObservableList<Technician> technicians = FXCollections.observableArrayList();
	
	private TechnicianService() {
		//demo technicians
		technicians.add(new Technician("tech"));
		technicians.add(new Technician("tech2"));
	}
	
	public static TechnicianService getInstance() {
		return INSTANCE;
	}
	public ObservableList<Technician> getTechnicians(){
		return technicians;
	}
	//returnd the usernames of techs
	public List <String> getTechnicianNames(){
		List<String> names = new ArrayList<>();
		for (Technician technician : technicians) {
			names.add(technician.getUsername());
		}
		return names;
	}
	//if tech with that username exists
	 public boolean exists(String username) {
	        for (Technician technician : technicians) {
	            if (technician.getUsername().equalsIgnoreCase(username)) {
	                return true;
	            }
	        }
	        return false;
	    }
	 //add a technician
	 public synchronized boolean addTechnician(String username) {
	        if (exists(username)) {
	            return false;
	        }
	        technicians.add(new Technician(username));
	        return true;
	    }
	 //remove a technician
	 public synchronized void removeTechnician(Technician technician) {
	        technicians.remove(technician);
	    }
} */
