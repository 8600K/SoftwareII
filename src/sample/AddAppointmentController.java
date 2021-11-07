package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class handles adding and updating Appointments.
 * The scene is able to differentiate itself based on adding or updating is through the use of
 * the Main.editData variable.  For more information, see AppointmentsController.
 */
public class AddAppointmentController implements Initializable {


    @FXML
    private Label minimizeLbl;
    @FXML
    private Label addUpdateAppointment;

    @FXML
    private TextField appointmentIdField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField userIdField;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private TextField startHourField;
    @FXML
    private TextField startMinuteField;
    @FXML
    private TextField endHourField;
    @FXML
    private TextField endMinuteField;

    @FXML
    private RadioButton startAmRdo;
    @FXML
    private RadioButton startPmRdo;
    @FXML
    private RadioButton endAmRdo;
    @FXML
    private RadioButton endPmRdo;

    @FXML
    private Button addAppointmentButton;


    @FXML
    private ComboBox<String> contactComboBox;
    @FXML
    private ComboBox<Integer> customerComboBox;

    private Alert alert;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        setToggleGroups();


        try {
            setComboBoxItems();
            setData();
            setId();
            userIdField.setText(getUserId());
        } catch (SQLException | ParseException throwables) {
            throwables.printStackTrace();
        }

        if(Main.editData == -1)
            appointmentIdField.setText(String.valueOf(Main.id));



    }

    /**
     * This method handles the adding and updating into the appointments Table respectively.
     * It handles all of the many many constraints and makes sure the data is valid (hence the name).
     * The method also handles all of the alerts, and querying required to add/update the data into the Table.
     * Truthfully this method is a little more cramped than I would like, but oh well.
     * @param event
     * @throws SQLException
     * @throws ParseException
     * @throws IOException
     */
    public void dataValidation(ActionEvent event) throws SQLException, ParseException, IOException, NumberFormatException {

        ChronoLocalDate dt = LocalDate.from(ZonedDateTime.now());




        if(startHourField.getText().length() > 2 || endHourField.getText().length() > 2 || startMinuteField.getText().length() > 2 || endMinuteField.getText().length() > 2 ||
                (startHourField.getText().isEmpty() || startMinuteField.getText().isEmpty() || endHourField.getText().isEmpty() || endMinuteField.getText().isEmpty()))
        {
            this.alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please make sure you have formatted the time properly in their respective format e.g. 9:00 or 12:15, and that you have selected AM or PM.");
            alert.show();


        } else if( (Integer.parseInt(startHourField.getText()) > 12 || Integer.parseInt(startHourField.getText()) < 1) ||
                (Integer.parseInt(endHourField.getText()) > 12 || Integer.parseInt(endHourField.getText()) < 1 ) ||
                (Integer.parseInt(startMinuteField.getText()) > 60 || Integer.parseInt(startMinuteField.getText()) < 0) ||
                (Integer.parseInt(endMinuteField.getText()) > 60 || Integer.parseInt(endMinuteField.getText()) < 0) ||
                startHourField.getText().isEmpty() || startMinuteField.getText().isEmpty() || endHourField.getText().isEmpty() ||
                endMinuteField.getText().isEmpty())
        {
            this.alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please make sure you have entered a valid time in 12 hour format in the start and end fields, e.g. 12:50 or 9:00.");
            alert.show();

        } else if(this.startDate.getValue() == null || this.startDate.getValue().isBefore(dt) &&
                this.endDate.getValue() == null || this.endDate.getValue().isBefore(dt))
        {
            this.alert = new Alert(Alert.AlertType.ERROR);

            alert.setContentText("Please make sure the date you have entered is valid.");
            alert.show();


        } else if(!this.titleField.getText().isEmpty() && this.titleField.getText().matches("[a-zA-Z].*") &&
            !this.descriptionField.getText().isEmpty() && this.descriptionField.getText().matches("[a-zA-Z].*") &&
            !this.locationField.getText().isEmpty() && this.locationField.getText().matches("[a-zA-Z].*") &&
            !this.contactComboBox.getItems().isEmpty() &&
            !this.typeField.getText().isEmpty() && this.typeField.getText().matches("[a-zA-Z].*") &&
            !this.customerComboBox.getItems().isEmpty() &&
            !this.startHourField.getText().isEmpty() && this.startHourField.getText().matches("[0-9]*") &&
            !this.startMinuteField.getText().isEmpty() && this.startMinuteField.getText().matches("[0-9]*") &&
            !this.endHourField.getText().isEmpty() && this.endHourField.getText().matches("[0-9]*") &&
            !this.endMinuteField.getText().isEmpty() && this.endMinuteField.getText().matches("[0-9]*") &&
                (this.startAmRdo.isSelected() || this.startPmRdo.isSelected()) &&
                (this.endAmRdo.isSelected() || this.endPmRdo.isSelected()))
        {



            Timestamp startTimestamp = getDateTime(startDate.getValue(), startHourField.getText(), startMinuteField.getText(), startSelection());
            Timestamp endTimestamp = getDateTime(endDate.getValue(), endHourField.getText(), endMinuteField.getText(), endSelection());

            if(startTimestamp== null || endTimestamp == null)
            {
                this.alert = new Alert(Alert.AlertType.ERROR);

                alert.setContentText("Please ensure the Start and End dates are valid and are within the company's 8:00 AM - 10:00 PM EST hours.");
                alert.show();
            } else if (overlappingAppointments())
            {
                this.alert = new Alert(Alert.AlertType.ERROR);

                alert.setContentText("Error.  That customer already has an appointment during that time.");
                alert.show();
            } else {


                long millis = System.currentTimeMillis();
                java.sql.Date date = new java.sql.Date(millis);
                Timestamp time = new Timestamp(System.currentTimeMillis());


                if (Main.editData != -1) {
                    PreparedStatement ps = Main.db.prepareStatement("UPDATE appointments SET appointment_id = ?, title = ?, description = ?, location = ?, type = ?," +
                            "start = ?, end = ?, last_update = ?, last_updated_by = ?, customer_id = ?, user_id = ?, contact_id = ? WHERE appointment_id = ?");

                    ps.setInt(1, Main.editData);
                    ps.setString(2, titleField.getText());
                    ps.setString(3, descriptionField.getText());
                    ps.setString(4, locationField.getText());
                    ps.setString(5, typeField.getText());
                    ps.setTimestamp(6, startTimestamp);
                    ps.setTimestamp(7, endTimestamp);
                    ps.setTimestamp(8, time);
                    ps.setString(9, LoginController.username);
                    ps.setInt(10, customerComboBox.getValue());
                    ps.setInt(11, Integer.parseInt(userIdField.getText()));
                    ps.setInt(12, getContactId());

                    ps.setInt(13, Main.editData);


                    ps.executeUpdate();
                    Main.id++;
                    Main.changeScene(event, this.getClass().getResource("Appointments.fxml"));


                } else {
                    PreparedStatement ps = Main.db.prepareStatement("INSERT INTO appointments VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");


                    ps.setInt(1, Main.id);
                    ps.setString(2, titleField.getText());
                    ps.setString(3, descriptionField.getText());
                    ps.setString(4, locationField.getText());
                    ps.setString(5, typeField.getText());
                    ps.setTimestamp(6, startTimestamp);
                    ps.setTimestamp(7, endTimestamp);
                    ps.setDate(8, date);
                    ps.setString(9, LoginController.username);
                    ps.setTimestamp(10, time);
                    ps.setString(11, LoginController.username);
                    ps.setInt(12, customerComboBox.getValue());
                    ps.setInt(13, Integer.parseInt(userIdField.getText()));
                    ps.setInt(14, getContactId());

                    ps.executeUpdate();
                    Main.id++;

                    Main.changeScene(event, this.getClass().getResource("Appointments.fxml"));
                }

            }




        } else {
            this.alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please ensure you have filled out all of the forms and entered the correct type of data into each respective field.");
            alert.show();
        }




    }

    /**
     * This method queries appointments based on customer_id currently attempting to be added/updated.
     * If the current timestamp falls within an existing appointment the method will return true, otherwise it will return false.
     * @return true or false depending on if an overlapping appointment of the same customer_id is found.
     * @throws SQLException
     */
    private boolean overlappingAppointments() throws SQLException {
        PreparedStatement ps = Main.db.prepareStatement("SELECT start, end FROM appointments WHERE customer_id = ? AND appointment_id != ?");

        ps.setInt(1, customerComboBox.getValue());

        ps.setInt(2, Main.editData);

        ResultSet rs = ps.executeQuery();

        Timestamp start = getDateTime(startDate.getValue(), startHourField.getText(), startMinuteField.getText(), startSelection());
        Timestamp end = getDateTime(endDate.getValue(), endHourField.getText(), endMinuteField.getText(), endSelection());

        while(rs.next())
        {

            LocalDateTime ldt = rs.getTimestamp(1).toLocalDateTime();
            LocalDateTime ldt1 = start.toLocalDateTime();

            if( (start.after(rs.getTimestamp(1)) && start.before(rs.getTimestamp(2))
                    || start.equals(rs.getTimestamp(1)) || start.equals(rs.getTimestamp(2)))
            )
             //       && ldt.getDayOfYear() == ldt1.getDayOfYear() )
            {
                return true;
            }


        }

        return false;

    }

    /**
     * This method primary function (see what I did there?) is to handle changing the scene into
     * allowing the user to update, instead of add.  Adding an appointment is the default for the scene, and this checks
     * if Main.editData is something other than -1 (essentially it has a valid value) and then changes the scene so that
     * the user is updating instead of adding into the Table.
     * @throws SQLException
     * @throws ParseException
     */
    private void setData() throws SQLException, ParseException {
        if(Main.editData != -1)
        {

            addUpdateAppointment.setText("Update Appointment");
            addAppointmentButton.setText("Update Appointment");

            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM appointments WHERE appointment_id = ?");

            ps.setInt(1, Main.editData);

            ResultSet rs = ps.executeQuery();

            if(rs.next())
            {
                appointmentIdField.setText(rs.getString(1));
                titleField.setText(rs.getString(2));
                descriptionField.setText(rs.getString(3));
                locationField.setText(rs.getString(4));
                contactComboBox.setValue(setContactId(rs.getInt(14)));


                typeField.setText(rs.getString(5));
                Date startDateValue = rs.getDate(6);
                Date endDateValue = rs.getDate(7);
                LocalDate startLocalDate = Instant.ofEpochMilli(startDateValue.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endLocalDate = Instant.ofEpochMilli(endDateValue.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                startDate.setValue(startLocalDate);
                endDate.setValue(endLocalDate);
                customerComboBox.setValue(rs.getInt(12));
                userIdField.setText(String.valueOf(rs.getInt(13)));



                LocalDateTime startTimestamp = rs.getTimestamp(6).toLocalDateTime();
                LocalDateTime endTimestamp = rs.getTimestamp(7).toLocalDateTime();



                String startTime = setTimeData(startTimestamp);
                String endTime = setTimeData(endTimestamp);

                startHourField.setText(startTime.substring(0,2));
                startMinuteField.setText(startTime.substring(3,5));
                if(startTime.contains("AM"))
                    startAmRdo.fire();
                else
                    startPmRdo.fire();

                endHourField.setText(endTime.substring(0,2));
                endMinuteField.setText(endTime.substring(3,5));
                if(endTime.contains("AM"))
                    endAmRdo.fire();
                else
                    endPmRdo.fire();


            }

        }
    }

    /**
     * Method to get contact_name so that in the dataValidation method the user can get contact_name instead of the number
     * the name represents.  I doubt the users know their contacts enough to label and keep track of each contact by an integer.
     * @param i
     * @return String
     * @throws SQLException
     */
    private String setContactId(int i) throws SQLException {


        PreparedStatement ps = Main.db.prepareStatement("SELECT contact_name FROM contacts WHERE contact_id = ?");

        ps.setInt(1, i);

        ResultSet rs = ps.executeQuery();

        if(rs.next())
        {
            return rs.getString(1);
        } else {
            return "Error";
        }





    }

    /**
     * Method to send the user to Appointments.fxml.
     * @param event
     * @throws IOException
     */
    public void goBackToAppointments(ActionEvent event) throws IOException {
        Main.changeScene(event, this.getClass().getResource("Appointments.fxml"));
    }

    /**
     * This method gets the dateTime from the user adding/updating, gets the system local, converts to make sure the
     * hours fall within the company's operating hours, and then returns null or the valid timestamp.
     * @param date
     * @param hourField
     * @param minuteField
     * @param amOrPm
     * @return Timestamp.valueOf(finalTime)
     */
    private Timestamp getDateTime(LocalDate date, String hourField, String minuteField, String amOrPm)
    {


        if(hourField.length() != 2)
        {
            hourField = "0" + hourField;
        }
        if(minuteField.length() != 2)
        {
            minuteField = "0" + minuteField;
        }


            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a").withLocale(Locale.US).withZone(ZoneId.systemDefault());

            String time = hourField + ':' + minuteField + " " + amOrPm;



            LocalTime localTime = LocalTime.parse(time, timeFormatter);

            //THIS IS IN 24 HOUR TIME NOW

            LocalDateTime localDateTime = LocalDateTime.of(date, localTime);

            ZonedDateTime ldtZoned = localDateTime.atZone(ZoneId.systemDefault());

            ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));

            ZonedDateTime estZoned = ldtZoned.withZoneSameInstant(ZoneId.of("America/New_York"));

            LocalDateTime finalTime = utcZoned.toLocalDateTime();



            LocalTime companyStartTime = LocalTime.of(8, 00);
            LocalTime companyEndTime = LocalTime.of(22, 00);

            LocalDateTime companyStartLdt = LocalDateTime.of(date, companyStartTime);
            LocalDateTime companyEndLdt = LocalDateTime.of(date, companyEndTime);

            ZonedDateTime zonedStartTime = companyStartLdt.atZone(ZoneId.of("America/New_York"));
            ZonedDateTime zonedEndTime = companyEndLdt.atZone(ZoneId.of("America/New_York"));

            if(estZoned.getHour() < zonedStartTime.getHour() || estZoned.getHour() > zonedEndTime.getHour())
            {
                return null;
            }

            return Timestamp.valueOf(finalTime);

    }

    /**
     * This method essentially converts 12 Hour Time into 24 Hour UTC time.
     * Which is what is stored into the database for the sake of keeping things consistent.
     * @param timestamp
     * @return
     * @throws ParseException
     */
    private String setTimeData(LocalDateTime timestamp) throws ParseException {
        ZoneId fromZone = ZoneId.of("UTC");
        ZoneId toZone = ZoneId.systemDefault();

        ZonedDateTime currentUTCTime = timestamp.atZone(fromZone);
        ZonedDateTime currentBestTime = currentUTCTime.withZoneSameInstant(toZone);


        String hoursAndMinutes;


        hoursAndMinutes = currentBestTime.getHour() + ":" + currentBestTime.getMinute();

        SimpleDateFormat twentyFourHourSDF = new SimpleDateFormat("HH:mm");
        SimpleDateFormat twelveHourSDF = new SimpleDateFormat("hh:mm a");

        java.util.Date dateParse = twentyFourHourSDF.parse(hoursAndMinutes);

        return twelveHourSDF.format(dateParse);
    }

    /**
     * This is used for the start radio buttons to determine and return AM or PM.
     * @return String, either AM or PM, depending on which radio button is selected.
     */
    private String startSelection()
    {
        if(startAmRdo.isSelected())
            return "AM";
        return "PM";
    }

    /**
     * This is used for the end radio buttons to determine and return AM or PM.
     * @return String, either AM or PM, depending on which radio button is selected.
     */
    private String endSelection()
    {
        if(endAmRdo.isSelected())
            return "AM";
        return "PM";
    }

    /**
     * This method sets the values in the combo boxes used in the Scene.
     * @throws SQLException
     */
    private void setComboBoxItems() throws SQLException {
        Statement statement = Main.db.createStatement();


        ResultSet rs = statement.executeQuery("SELECT contact_name FROM contacts");


        while(rs.next())
        {
            contactComboBox.getItems().add(rs.getString(1));
        }

        Statement statement1 = Main.db.createStatement();

        ResultSet rs1 = statement1.executeQuery("SELECT customer_id FROM customers");

        while(rs1.next())
        {
            customerComboBox.getItems().add(rs1.getInt(1));
        }

    }

    /**
     * Simple method to set the toggle groups.
     * Mainly used here to keep the initialize method clean.
     */
    private void setToggleGroups()
    {
        ToggleGroup startToggle = new ToggleGroup();
        ToggleGroup endToggle = new ToggleGroup();

        startAmRdo.setToggleGroup(startToggle);
        startPmRdo.setToggleGroup(startToggle);


        endAmRdo.setToggleGroup(endToggle);
        endPmRdo.setToggleGroup(endToggle);
    }

    //TODO
    private LocalDateTime getCompanyStart(String hourStart, String minuteStart)
    {
        LocalDate date = LocalDate.now();

        LocalTime localTime = LocalTime.of(8, 00);

        LocalDateTime companyStartTime = LocalDateTime.of(date, localTime);

        ZonedDateTime zonedStart = companyStartTime.atZone(ZoneId.of("UTC"));

        ZonedDateTime zonedConverted = zonedStart.withZoneSameInstant(ZoneId.of("America/New_York"));

        LocalDateTime finished = zonedConverted.toLocalDateTime();

        LocalTime lt = LocalTime.parse(hourStart + minuteStart);

        return finished;

    }

    private LocalDateTime getCompanyEnd()
    {
        LocalDate date = LocalDate.now();

        LocalTime localTime = LocalTime.of(22, 00);

        LocalDateTime companyEndTime = LocalDateTime.of(date, localTime);

        ZonedDateTime zonedStart = companyEndTime.atZone(ZoneId.of("UTC"));

        ZonedDateTime zonedConverted = zonedStart.withZoneSameInstant(ZoneId.of("America/New_York"));

        LocalDateTime finished = zonedConverted.toLocalDateTime();

        return finished;
    }

    /**
     * This method sets the Main.id value, to be used to keep track and ensure
     * the value is always unique.
     * @throws SQLException
     */
    public void setId() throws SQLException {
        Statement statement = Main.db.createStatement();
        String query = "SELECT * FROM appointments ORDER BY appointment_id DESC LIMIT 1";
        ResultSet rs = statement.executeQuery(query);
        if (rs.next()) {
            Main.id = rs.getInt(1) + 1;
        } else {
            Main.id = 0;
        }
    }

    /**
     * This method is simply a getter for the user_id based on the user_name.
     * Used for adding and updating appointments.
     * @return String
     * @throws SQLException
     */
    private String getUserId() throws SQLException {
        PreparedStatement ps = Main.db.prepareStatement("SELECT user_id FROM users WHERE user_name = ?");
        ps.setString(1, LoginController.username);

        ResultSet resultSet = ps.executeQuery();

        if(resultSet.next())
        {
            return String.valueOf(resultSet.getInt(1));
        } else {
            return "error";
        }

    }

    /**
     * This method is simply a getter for the contact_id based on the contact_name.
     * Used for adding and updating appointments.
     * @return int
     * @throws SQLException
     */
    private int getContactId() throws SQLException {
        PreparedStatement ps = Main.db.prepareStatement("SELECT contact_id FROM contacts WHERE contact_name = ?");

        ps.setString(1, contactComboBox.getValue());

        ResultSet resultSet = ps.executeQuery();

        if(resultSet.next())
        {
            return resultSet.getInt(1);


        } else {
            return -1;
        }


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
