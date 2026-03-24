import java.util.Objects;

public class User {
    private String login;
    private String password;
    private Role role;
    private String rentedVehicleID;
    private boolean online;

    public User(String login, String password, Role role, String rentedVehicleID){
        this.login = login;
        this.password=password;
        this.role =role;
        this.rentedVehicleID=rentedVehicleID;
        online=false;
    }

    public boolean isOnline(){
        return online;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getRentedVehicleID() {
        return rentedVehicleID;
    }

    public Role getRole() {
        return role;
    }

    public void setRentedVehicleID(String rentedVehicleID) {
        this.rentedVehicleID = rentedVehicleID;
    }

    public User copy(){
        return new User(login, password, role, rentedVehicleID);
    }

    public String toCSV() {
        String vehicleStr = (rentedVehicleID != null) ? rentedVehicleID : "";
        return login + ";" + password + ";" + role + ";" + vehicleStr;
    }
}
