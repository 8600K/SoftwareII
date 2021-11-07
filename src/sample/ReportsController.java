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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Interface
 * This interface allows a lambda to get the value returned from the areaComboBox.getValue() method.
 * Allowing for one line to to create the ReportInterface and store zip and then call the getStr method multiple times
 * while limiting the amount of code.
 */
@FunctionalInterface
interface ReportInterface
{
    String getStr();
}

/**
 * This class handles almost all of the reports specified for the project.
 * It uses ComboBoxes, TextFields, a TableView, Labels, and some Buttons to handle all of the user input/output.
 */
public class ReportsController implements Initializable {


    @FXML
    private ComboBox<String> selectionComboBox;
    @FXML
    private ComboBox<String> nameComboBox;
    @FXML
    private ComboBox<String> areaComboBox;

    @FXML
    private Button goBack;
    @FXML
    private Button viewReport;

    @FXML
    private TextField typeField;
    @FXML
    private TextField monthField;

    @FXML
    private Label headerLabel;
    @FXML
    private Label contextLabel;
    @FXML
    private Label minimizeLbl;

    @FXML
    private TableView<Appointments> recordTable;
    @FXML
    private TableColumn<Appointments, Integer> appointmentCol;
    @FXML
    private TableColumn<Appointments, String> titleCol;
    @FXML
    private TableColumn<Appointments, String> typeCol;
    @FXML
    private TableColumn<Appointments, String> descCol;
    @FXML
    private TableColumn<Appointments, Timestamp> startCol;
    @FXML
    private TableColumn<Appointments, Timestamp> endCol;
    @FXML
    private TableColumn<Appointments, Integer> customerCol;

    private ObservableList<Appointments> scheduleList;

    private Alert alert;



    final String type = "Number of Customer Appointments by Type";
    final String month = "Number of Customer Appointments by Month";
    final String contact = "Contact Schedule";
    final String area = "Number of Customers with same Area Code";

    /**
     * Initialize method.
     * All of the methods used within are pretty self-explanatory.
     * setComboBoxItems merely adds the values within the two combo boxes, while setColumns initializes the TableColumns.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        selectionComboBox.getItems().addAll(type, month, contact, area);
        this.alert = new Alert(Alert.AlertType.ERROR);



        try {
            setComboBoxItems();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        setColumns();
        this.scheduleList = FXCollections.observableArrayList();


    }

    /**
     * This method determines which report is being requested, and then changes the Scene based on that information.
     * @throws SQLException
     * @throws NullPointerException
     */
    public void goToReport() throws SQLException, NullPointerException {

        try {

            if (typeField.isVisible() && typeField.getText().length() > 0 && selectionComboBox.getValue().equals(type)) {
                typeReport();
            } else if (monthField.isVisible() && monthField.getText().length() > 0 && selectionComboBox.getValue().equals(month)) {
                monthReport();
            } else if (selectionComboBox.getValue().equals(contact) && !nameComboBox.getValue().isEmpty()) {

                this.recordTable.setVisible(true);

                scheduleReport();

            } else if (selectionComboBox.getValue().equals(area) && !areaComboBox.getValue().isEmpty()) {
                zipReport();
            } else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("Please make a valid selection");
                alert.show();
            }
        } catch (NullPointerException e)
        {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Please make a valid selection");
            alert.show();
        }


    }

    /**
     * This method essentially takes a step before the goToReport method can run.  This method fires
     * based on the value of the selectionComboBox, and then changes the Scene to allow user input based off of
     * the value.
     */
    public void getSelection() {

        switch (selectionComboBox.getValue())
        {
            case type:
                monthField.setVisible(false);
                typeField.setVisible(true);
                nameComboBox.setVisible(false);
                areaComboBox.setVisible(false);
                recordTable.setVisible(false);
                break;
            case month:
                typeField.setVisible(false);
                monthField.setVisible(true);
                nameComboBox.setVisible(false);
                areaComboBox.setVisible(false);
                recordTable.setVisible(false);
                break;
            case contact:
                typeField.setVisible(false);
                monthField.setVisible(false);
                nameComboBox.setVisible(true);
                areaComboBox.setVisible(false);
                break;
            case area:
                typeField.setVisible(false);
                monthField.setVisible(false);
                nameComboBox.setVisible(false);
                areaComboBox.setVisible(true);
                recordTable.setVisible(false);
                break;
            default:
                monthField.setVisible(false);
                typeField.setVisible(false);
                headerLabel.setVisible(false);
                contextLabel.setVisible(false);
                recordTable.setVisible(false);
                break;
        }

    }

    /**
     * Adds the items in the combo boxes.  Two queries take place within this method from two tables.
     * One thing to note is the DISTINCT keyword with the second query, this allows the zipReport() method
     * to function without having to counteract a time where there are 0 results, because such a thing cannot happen in
     * the first place.
     * @throws SQLException
     */
    private void setComboBoxItems() throws SQLException {
        Statement statement = Main.db.createStatement();


        ResultSet rs = statement.executeQuery("SELECT contact_name FROM contacts");


        while(rs.next())
        {
            nameComboBox.getItems().add(rs.getString(1));
        }

        Statement statement1 = Main.db.createStatement();
        ResultSet rs1 = statement1.executeQuery("SELECT DISTINCT postal_code FROM customers ");

        while(rs1.next())
        {
            areaComboBox.getItems().addAll(rs1.getString(1));
        }


    }

    /**
     * The first and probably most simple report, This method simply gets the value of the typeField, then checks that against
     * the appointments database type column, and then counts the number of "hits" based on the amount of times a loop through the ResultSet is processed.
     * The method then changes the labels to reveal the results, with an edge case if 0 hits are counted.
     * @throws SQLException
     */
    private void typeReport() throws SQLException
    {
        String type = typeField.getText();

        PreparedStatement ps = Main.db.prepareStatement("SELECT appointment_id FROM appointments WHERE type = ?");

        ps.setString(1, type);

        ResultSet rs = ps.executeQuery();

        int hits = 0;

        while (rs.next())
        {
            hits++;

            headerLabel.setVisible(true);
            contextLabel.setVisible(true);
            headerLabel.setText("Number of Customer Appointments by Type: " + type);
            contextLabel.setText(String.valueOf(hits));


        }

        if(hits == 0)
        {
            headerLabel.setVisible(true);
            contextLabel.setVisible(false);
            headerLabel.setText("Could not find any Appointments with Type: " + type);
        }
    }

    /**
     * The second report method, this one is more complex than the typeReport.  For one, I used an array to get all the start times
     * from appointments, then I loop through the array and match what the user entered in the month field with the data from the array
     * (which pareses the month at this point), any matches are counted as a hit, and then the hits are added up at the end, and change the
     * Scene's labels to display the results.  Once again, I also included a response if 0 hits are found.
     * @throws SQLException
     */
    private void monthReport() throws SQLException, NumberFormatException
    {
        String month = monthField.getText();
        LocalDate temp;
        ArrayList<Timestamp> start = new ArrayList<Timestamp>();
        int hits = 0;
        Month monthDisplay = Month.JANUARY;

        Statement statement = Main.db.createStatement();

        ResultSet rs = statement.executeQuery("SELECT start FROM appointments");

        while(rs.next())
        {
            start.add(rs.getTimestamp(1));
        }

        
        for(Timestamp i : start)
        {
            temp = i.toLocalDateTime().toLocalDate();
            int monthTemp = temp.getMonth().getValue();

            try{
                if(monthTemp == Integer.parseInt(month))
                {
                    hits++;
                    monthDisplay = temp.getMonth();
                }
            } catch (NumberFormatException nfe)
            {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("Please enter the month by number, e.g. 12 for December, 7 for July");
                alert.show();
                break;
            }


            headerLabel.setVisible(true);
            contextLabel.setVisible(true);
            headerLabel.setText("Number of Customer Appointments by Month: " + monthDisplay);
            contextLabel.setText(String.valueOf(hits));
        }

        try {
            if (hits == 0) {
                headerLabel.setVisible(true);
                contextLabel.setVisible(false);
                monthDisplay = Month.of(Integer.parseInt(monthField.getText()));
                headerLabel.setText("Could not find any Appointments by Month: " + monthDisplay);
            }
        } catch (NumberFormatException nfe)
        {
            headerLabel.setText("Error");
        }
    }

    /**
     * The third report requires two queries to be made, and gets a value from the a comboBox in the scene.
     * This report also utilizes a TableView, thus a new Appointments object must be defined,
     * even though I'm only after a couple of the values in the Table.
     * @throws SQLException
     */
    private void scheduleReport() throws SQLException {
        recordTable.getItems().clear();

        PreparedStatement ps0 = Main.db.prepareStatement("SELECT contact_id FROM contacts WHERE contact_name = ?");

        String contactName = nameComboBox.getValue();

        ps0.setString(1, contactName);

        ResultSet rs0 = ps0.executeQuery();

        if(rs0.next())
        {
            int contactId = rs0.getInt(1);

            PreparedStatement ps1 = Main.db.prepareStatement("SELECT * FROM appointments WHERE contact_id = ?");

            ps1.setInt(1, contactId);

            ResultSet rs1 = ps1.executeQuery();

            while(rs1.next())
            {
                int a0 = rs1.getInt(1);
                String a1 = rs1.getString(2);
                String a2 = rs1.getString(3);
                String a3 = rs1.getString(4);
                String a4 = rs1.getString(5);
                String a5 = changeTimeZone(rs1.getTimestamp(6));
                String a6 = changeTimeZone(rs1.getTimestamp(7));
                Date a7 = rs1.getDate(8);
                String a8 = rs1.getString(9);
                Timestamp a9 = rs1.getTimestamp(10);
                String a10 = rs1.getString(11);
                int a11 = rs1.getInt(12);
                int a12 = rs1.getInt(13);
                int a13 = rs1.getInt(14);

                scheduleList.add(new Appointments(a0, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13));
                this.recordTable.setItems(scheduleList);

            }


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
     * <b>Lambda usage here.  This Lambda gets the value within the areaComboBox and stores that in zip.
     * Zip is then used in both the following SELECT query and report.</b>
     * After that, the method works similarly to the first method, however instead of getting a value from a TextField, this
     * method instead gets a value from a comboBox.  From there it's just a simple query and the amount of Customers
     * within a certain Area Code or Zip Code are gathered and shown to the user.  One thing to note, this method
     * does not have an edge case for if there are 0 hits, because no such occurrence could happen due to using a combo box
     * with populated data from a Table.  See setComboBoxItems() for more details.
     * @throws SQLException
     */
    private void zipReport() throws SQLException
    {


        ReportInterface zip = () -> areaComboBox.getValue();
        int hits = 0;

        PreparedStatement ps = Main.db.prepareStatement("SELECT customer_id FROM customers WHERE postal_code = ?");

        ps.setString(1, zip.getStr());
        ResultSet rs = ps.executeQuery();

        while (rs.next())
        {
            hits++;
        }



        headerLabel.setVisible(true);
        contextLabel.setVisible(true);
        headerLabel.setText("Number of Customers within AreaCode: " + zip.getStr());
        contextLabel.setText(String.valueOf(hits));


    }

    /**
     * Very simple method that simply sets the TableColumns for the TableView.
     * I created this method to keep the initialize method cleaner.
     */
    private void setColumns()
    {
        appointmentCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));

        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));

        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));

        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));


    }

    /**
     * This method controls scene transitions.
     * @param event
     * @throws IOException
     */
    public void goBack(ActionEvent event) throws IOException {
        Main.changeScene(event, this.getClass().getResource("Home.fxml"));
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
