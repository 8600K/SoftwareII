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
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This class handles the CustomersController which is primary the viewing/holding Scene for
 * adding, editing, deleting, and viewing Customers.  This scene houses the TableView for all of the
 * customers, and has buttons which direct the user to the creation/updating stage of the program.
 */
public class CustomersController implements Initializable {

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
    private TableView<Customers> recordTable;
    @FXML
    private TableColumn<Customers, String> idCol;
    @FXML
    private TableColumn<Customers, String> nameCol;
    @FXML
    private TableColumn<Customers, String> addressCol;
    @FXML
    private TableColumn<Customers, Integer> postalCol;
    @FXML
    private TableColumn<Customers, String> phoneCol;
    @FXML
    private TableColumn<Customers, String> createDateCol;
    @FXML
    private TableColumn<Customers, Date> createdByCol;
    @FXML
    private TableColumn<Customers, Timestamp> lastUpdate;
    @FXML
    private TableColumn<Customers, String> lastUpdatedBy;
    @FXML
    private TableColumn<Customers, String> divisionCol;



    private ObservableList<Customers> customerObList;


    /**
     * Initialize method of CustomersController.
     * Everything here is pretty self explanatory, however I figured I should expand
     * upon Main.editData housing the data -1.
     * See the comment below for more details.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.alert = new Alert(Alert.AlertType.CONFIRMATION);

        goBack.setText(resource.getString("goBack"));
        add.setText(resource.getString("add"));
        edit.setText(resource.getString("edit"));
        delete.setText(resource.getString("delete"));

        nameCol.setText(resource.getString("name"));
        addressCol.setText(resource.getString("address"));
        postalCol.setText(resource.getString("postalCode"));
        createDateCol.setText(resource.getString("createDate"));
        createdByCol.setText(resource.getString("createdBy"));

        /**
         * Main.editData = -1;
         * This static variable defined in Main is given the value -1
         * upon initialization so that if the user goes to AddCustomerController,
         * that Scene will be for adding a customer.  If the user instead decides to edit an
         * existing customer, the variable will be given the relevant data from the TableView
         * and AddCustomerController will then change its core functions to update an existing Customer.
         * See AddCustomerController for more details.
         */
        Main.editData = -1;

        setColumns();

        this.customerObList = FXCollections.observableArrayList();


        try {
            connect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * This method just sets all of the columns' cell value factories.
     * Primary usage:  Keeping the initialize method clean.
     */
    private void setColumns()
    {
        this.idCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        this.nameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        this.addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        this.postalCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

        this.phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        this.createDateCol.setCellValueFactory(new PropertyValueFactory<>("createDate"));

        this.createdByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));

        this.lastUpdate.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));

        this.lastUpdatedBy.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));

        this.divisionCol.setCellValueFactory(new PropertyValueFactory<>("divisionId"));
    }

    /**
     * This Method deletes customers if conditions are met.
     * The alerts are also in multiple languages!
     * Now then, the method first checks to make sure an actual selection has been made,
     * if positive then the method does a query in the appointments Table of customer_ids that
     * have an appointment_id, if this is negative the method moves on and
     * sends an alert to the user asking them to confirm the deletion of a Customer.
     * If the user confirms then the Customer is deleted.  If any of the above conditions are not met,
     * an Alert will be shown to the user.
     * @throws SQLException
     */
    public void deleteRow() throws SQLException {



        if(this.recordTable.getSelectionModel().isEmpty())
        {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText(resource.getString("deleteError"));
            alert.show();
        } else {
            int i = recordTable.getSelectionModel().getSelectedItem().getCustomerId();
            int j = recordTable.getSelectionModel().getFocusedIndex();

            PreparedStatement ps = Main.db.prepareStatement("SELECT appointment_id FROM appointments WHERE customer_id = ?");

            ps.setInt(1, i);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText(resource.getString("customerAppointmentCondition"));
                alert.show();
            } else {


                alert.setAlertType(Alert.AlertType.CONFIRMATION);
                alert.setContentText(resource.getString("confirm") + " " + i);
                Optional<ButtonType> confirm = alert.showAndWait();
                if (confirm.get() == ButtonType.OK) {

                    System.out.println("Ran!");

                    PreparedStatement ps1 = Main.db.prepareStatement("DELETE FROM customers WHERE customer_id = ?");

                    ps1.setInt(1, i);

                    ps1.executeUpdate();

                    customerObList.remove(j);

                    recordTable.refresh();

                    alert.setAlertType(Alert.AlertType.INFORMATION);
                    alert.setContentText(resource.getString("information"));
                    alert.show();

                }
            }
        }
    }


    /**
     * This method simply fills the TableView with the relevant data
     * from the customers Table.
     * @throws SQLException
     */
    private void connect() throws SQLException {

            Statement statement = Main.db.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM customers");

            while (rs.next()) {

                int s0 = rs.getInt(1);
                String s1 = rs.getString(2);
                String s2 = rs.getString(3);
                String s3 = rs.getString(4);
                String s4 = rs.getString(5);
                Date s5 = rs.getDate(6);
                String s6 = rs.getString(7);
                Timestamp s7 = rs.getTimestamp(8);
                String s8 = rs.getString(9);
                int s9 = rs.getInt(10);


                customerObList.add(new Customers(s0, s1, s2, s3, s4, s5, s6, s7, s8, s9));
                this.recordTable.setItems(customerObList);

            }


    }


    /**
     * Method that sends the user to the AddCustomer.fxml Scene.
     * @param event
     * @throws IOException
     */
    public void goToAddCustomers(ActionEvent event) throws IOException {
        Main.changeScene(event, this.getClass().getResource("AddCustomer.fxml"));
    }

    /**
     * Method that sends the user to the AddCustomer.fxml Scene.
     * However this method also handles the selection of a customer from the user,
     * including an alert if nothing is selected.  Once something is selected, Main.editData stores the relevant
     * data and this will be used by AddCustomerController to altar its core function.
     * See AddCustomerController for more details.
     * @param event
     * @throws IOException
     */
    public void goToEditCustomers(ActionEvent event) throws IOException {

        if(this.recordTable.getSelectionModel().isEmpty()) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText(resource.getString("editError"));
            alert.show();
        } else {
            Main.editData = recordTable.getSelectionModel().getSelectedItem().getCustomerId();
            Main.changeScene(event, this.getClass().getResource("AddCustomer.fxml"));
        }
    }

    /**
     * Method that sends the user to the Home.fxml Scene.
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
