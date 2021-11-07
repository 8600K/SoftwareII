package sample;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * This Controller handles the Scene after the user has completed login.
 * The main purpose of this stage is the user can pick if they want to go to
 * Appointments, Reports, or Customers.
 */
public class HomeController implements Initializable {

    Locale locale = Locale.getDefault();
    ResourceBundle resource = ResourceBundle.getBundle("sample/Bundle", locale);

    @FXML
    private Label customerRecords;
    @FXML
    private Label appointments;
    @FXML
    private Button editCustomers;
    @FXML
    private Button editAppointments;
    @FXML
    private Button signOut;


    @FXML
    private Label minimizeLbl;


    private Alert alert;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerRecords.setText(resource.getString("customerRecords"));
        appointments.setText(resource.getString("appointments"));
        editCustomers.setText(resource.getString("edit"));
        editAppointments.setText(resource.getString("edit"));
        signOut.setText(resource.getString("signOut"));






    }



    /**
     * Method that sends the user to the Reports.fxml Scene.
     * @param event
     * @throws IOException
     */
    public void goToReports(ActionEvent event) throws IOException {
        Main.changeScene(event, this.getClass().getResource("Reports.fxml"));
    }

    /**
     * Method that sends the user to the Login.fxml Scene.
     * @param event
     * @throws IOException
     */
    public void goBackToLoginScene(ActionEvent event) throws IOException {
        Main.changeScene(event, this.getClass().getResource("Login.fxml"));

    }

    /**
     * Method that sends the user to the Customers.fxml Scene.
     * @param event
     * @throws IOException
     */
    public void goToCustomersScene(ActionEvent event) throws IOException {
        Main.changeScene(event, this.getClass().getResource("Customers.fxml"));

    }

    /**
     * Method that sends the user to the Appointments.fxml Scene.
     * @param event
     * @throws IOException
     */
    public void goToAppointmentsScene(ActionEvent event) throws IOException {
        Main.changeScene(event, this.getClass().getResource("Appointments.fxml"));
    }

    /**
     * This Method simply minimizes the program if the minimize label is pressed.
     */
    public void minimize()
    {
        Stage stage = (Stage) minimizeLbl.getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * This method exits the program if the x button is pressed.
     */
    public void exitTheProgram() {
        System.exit(0);
    }


}
