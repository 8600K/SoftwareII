package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * This class handles the AppointmentsController which is primary the viewing/holding Scene for
 * adding, editing, deleting, and viewing Appointments.  This scene houses the TableView for all of the
 * appointments, and has buttons which direct the user to the creation/updating stage of the program.
 */
public class AppointmentsController implements Initializable {

    Locale locale = Locale.getDefault();
    ResourceBundle resource = ResourceBundle.getBundle("sample/Bundle", locale);

    private Alert alert;

    @FXML
    private Button goBack;
    @FXML
    private Button add;
    @FXML
    private Button edit;
    @FXML
    private Button delete;
    @FXML
    private Label minimizeLbl;

    @FXML
    private ToggleButton monthToggle;
    @FXML
    private ToggleButton weekToggle;
    @FXML
    private ToggleButton defaultToggle;

    @FXML
    private TableView<Appointments> recordTable;
    @FXML
    private TableColumn<Appointments, Integer> appointmentIdCol;
    @FXML
    private TableColumn<Appointments, String> titleCol;
    @FXML
    private TableColumn<Appointments, String> descriptionCol;
    @FXML
    private TableColumn<Appointments, String> locationCol;
    @FXML
    private TableColumn<Appointments, String> contactCol;
    @FXML
    private TableColumn<Appointments, String>typeCol;
    @FXML
    private TableColumn<Appointments, Date> startCol;
    @FXML
    private TableColumn<Appointments, Date> endCol;
    @FXML
    private TableColumn<Appointments, Integer> customerIdCol;



    private ObservableList<Appointments> appointmentsObList;
    private ObservableList<Appointments> sortedObList;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.alert = new Alert(Alert.AlertType.NONE);

        ToggleGroup toggleGroup = new ToggleGroup();

        weekToggle.setToggleGroup(toggleGroup);
        monthToggle.setToggleGroup(toggleGroup);
        defaultToggle.setToggleGroup(toggleGroup);
        Main.editData = -1;


        this.appointmentsObList = FXCollections.observableArrayList();
        this.sortedObList = FXCollections.observableArrayList();


        setColumns();

        try {
            connect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }


    /**
     * Simple method that sends the user back to the Home Scene.
     * @param event
     * @throws IOException
     */
    public void goBack(ActionEvent event) throws IOException {
        Main.changeScene(event, this.getClass().getResource("Home.fxml"));
    }

    /**
     * This method handles the sorting by month/week of the TableView.
     */
    public void sort() throws SQLException {
        if(monthToggle.isSelected())
        {

            this.recordTable.getItems().clear();
            this.appointmentsObList.clear();
            this.sortedObList.clear();
            connect();

            int month = LocalDate.now().getMonth().getValue();
            for(int i = 0; i < appointmentsObList.size(); i++)
            {
                if(appointmentsObList.get(i).getMonth() == month)
                {
                    sortedObList.add(appointmentsObList.get(i));
                }
            }
            recordTable.getItems().clear();
            recordTable.getItems().addAll(sortedObList);



            //this.recordTable.setItems(appointmentsObList);
        } else if(weekToggle.isSelected())
        {
            //this.appointmentsObList = appointmentsObList.sorted(Comparator.comparingInt(Appointments::getAppointmentId));
            //this.appointmentsObList = appointmentsObList.sorted(Comparator.comparingInt(Appointments::getWeek));

            this.recordTable.getItems().clear();
            this.appointmentsObList.clear();
            this.sortedObList.clear();
            connect();

            LocalDate date = LocalDate.now();
            int weekOfYear = date.get(WeekFields.of(locale).weekOfYear());
            for(int i = 0; i < appointmentsObList.size(); i++)
            {
                if(appointmentsObList.get(i).getWeek() == weekOfYear)
                {
                    sortedObList.add(appointmentsObList.get(i));
                }
            }
            recordTable.getItems().clear();
            recordTable.getItems().addAll(sortedObList);

        } else {
            this.recordTable.getItems().clear();
            this.appointmentsObList.clear();
            this.sortedObList.clear();
            connect();

        }
    }


    /**
     * This method handles the deletion of appointments if conditions are met.
     * Firstly, something must be selected before it can be deleted.  Then a confirmation is sent
     * to the user.  If the user accepts the record is deleted, and the user is notified of the successful
     * deletion.
     * @throws SQLException
     */
    public void deleteAppointment(ActionEvent event) throws SQLException, NullPointerException, IOException {



        if(this.recordTable.getSelectionModel().isEmpty())
        {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText(resource.getString("deleteError"));
            alert.show();
        } else {
            int i = recordTable.getSelectionModel().getSelectedItem().getAppointmentId();
            int j = recordTable.getSelectionModel().getSelectedIndex();


            alert.setAlertType(Alert.AlertType.CONFIRMATION);
            alert.setContentText(resource.getString("confirm") + " " + i);
            Optional<ButtonType> confirm = alert.showAndWait();
            if (confirm.get() == ButtonType.OK) {

                PreparedStatement ps0 = Main.db.prepareStatement("SELECT type FROM appointments WHERE appointment_id = ?");

                ps0.setInt(1, i);

                ResultSet rs = ps0.executeQuery();

                PreparedStatement ps1 = Main.db.prepareStatement("DELETE FROM appointments WHERE appointment_id = ?");

                ps1.setInt(1, i);

                ps1.executeUpdate();

                try {
                    appointmentsObList.remove(j);
                } catch (UnsupportedOperationException uoe)
                {
                    //System.out.println("Exception Caught!  Continuing the program...");
                    Main.changeScene(event, this.getClass().getResource("Appointments.fxml"));
                }


                recordTable.refresh();
                if(rs.next())
                {
                    String typeStr = rs.getString(1);
                    alert.setAlertType(Alert.AlertType.INFORMATION);
                    alert.setContentText("Appointment ID: " + i + " Of type: " + typeStr + " Successfully Deleted.");
                    alert.show();

                }
                //Main.editData = recordTable.getSelectionModel().getSelectedItem().getAppointmentId();
            }
        }



    }

    /**
     * This method sends the user to the AddAppointment Scene.
     * @param event
     * @throws IOException
     */
    public void goToAddAppointments(ActionEvent event) throws IOException {
        Main.changeScene(event, this.getClass().getResource("AddAppointment.fxml"));
    }

    /**
     * This method sends the user to the AddAppointment Scene if a valid row is selected.
     * Also, Main.editData stores the value of the row based on the appointmentId Column.
     * @param event
     * @throws IOException
     */
    public void goToEditAppointments(ActionEvent event) throws IOException {

        if(this.recordTable.getSelectionModel().isEmpty()) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText(resource.getString("editError"));
            alert.show();
        } else {
            Main.editData = recordTable.getSelectionModel().getSelectedItem().getAppointmentId();

            Main.changeScene(event, this.getClass().getResource("AddAppointment.fxml"));
        }
    }

    /**
     * This method queries everything from appointments and creates a
     * new Appointments object (See Appointments for more details).
     * Then, the TableView is given these values for the user to see.
     * @throws SQLException
     */
    private  void connect() throws SQLException {
        Statement statement = Main.db.createStatement();

        ResultSet rs = statement.executeQuery("SELECT * FROM appointments");

        while (rs.next())
        {
            int a0 = rs.getInt(1);
            String a1 = rs.getString(2);
            String a2 = rs.getString(3);
            String a3 = rs.getString(4);
            String a4 = rs.getString(5);
            String a5 = changeTimeZone(rs.getTimestamp(6));
            String a6 = changeTimeZone(rs.getTimestamp(7));
            Date a7 = rs.getDate(8);
            String a8 = rs.getString(9);
            Timestamp a9 = rs.getTimestamp(10);
            String a10 = rs.getString(11);
            int a11 = rs.getInt(12);
            int a12 = rs.getInt(13);
            int a13 = rs.getInt(14);


            appointmentsObList.add(new Appointments(a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13));
            this.recordTable.setItems(appointmentsObList);

        }


    }

    /**
     * This method handles showing the user the date time in their system locale in 12 hour format.
     * While the data is in the database as 24 hour UTC, for viewing purposes it gets re-converted back to
     * 12 hour AM/PM time based on the user's zoneId.
     * @param ts (TimeStamp)
     * @return convertedLdt.format(dtf) convertedLocalDateTime formatted.
     */
    private String changeTimeZone(Timestamp ts)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
        LocalDateTime ldt = ts.toLocalDateTime();

        ZonedDateTime zdt = ldt.atZone(ZoneId.of("UTC"));

        ZonedDateTime convertedZdt = zdt.withZoneSameInstant(ZoneId.systemDefault());

        LocalDateTime convertedLdt = convertedZdt.toLocalDateTime();

        return convertedLdt.format(dtf);

    }

    /**
     * Simple method to set TableColumns' CellValueFactories.  This method is used to keep
     * initialize cleaner.
     */
    private void setColumns()
    {
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));

        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactId"));

        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));

        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));

        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));


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
