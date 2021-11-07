package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class handles adding and updating Customers.
 * I decided to allow this class and it's respective scene AddCustomer.fxml to be able to handle
 * both adding and updating to keep classes to a minimum and I thought it'd be a fun challenge.
 * Now then, the way the scene is able to differentiate itself based on adding or updating is through the use of
 * the Main.editData variable.  For more information, see CustomerController.
 */
public class AddCustomerController implements Initializable {

    Locale locale = Locale.getDefault();
    ResourceBundle resource = ResourceBundle.getBundle("sample/Bundle", locale);

    @FXML
    private Label addCustomerLabel;
    @FXML
    private Label name;
    @FXML
    private Label address;
    @FXML
    private Label postalCode;
    @FXML
    private Label phoneNumber;
    @FXML
    private Label countryLabel;


    @FXML
    private Label minimizeLbl;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField postalField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField id;

    @FXML
    private ComboBox<String> countryComboBox;
    @FXML
    private ComboBox<String> divisionComboBox;
    @FXML
    private Label stateProvinceLbl;
    @FXML
    private Button addCustomerButton;
    @FXML
    private Button cancel;

    private Alert alert;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.alert = new Alert(Alert.AlertType.INFORMATION);

        addCustomerLabel.setText(resource.getString("addCustomer"));
        name.setText(resource.getString("name"));
        address.setText(resource.getString("address"));
        postalCode.setText(resource.getString("postalCode"));
        phoneNumber.setText(resource.getString("phoneNumber"));
        countryLabel.setText(resource.getString("country"));
        addCustomerButton.setText(resource.getString("addCustomer"));
        cancel.setText(resource.getString("cancel"));


        try {
            setId();
            setData();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        id.setText(String.valueOf(Main.id));


        countryComboBox.getItems().addAll(resource.getString("unitedStates"), "Canada", resource.getString("unitedKigdom"));

    }

    /**
     * This is how Main.id is set, which is important considering we can't have overlapping values in the
     * customer_id field.
     * @throws SQLException
     */
    public void setId() throws SQLException {
        Statement statement = Main.db.createStatement();
        String query = "SELECT * FROM customers ORDER BY customer_id DESC LIMIT 1";
        ResultSet rs = statement.executeQuery(query);
        if (rs.next()) {
            Main.id = rs.getInt(1) + 1;
        } else {
            Main.id = 0;
        }
    }

    /**
     * This is the main method that handles whether or not the scene will be adding or updating.
     * If Main.editData != -1, this means the program is updating instead of adding.
     * @throws SQLException
     */
    public void setData() throws SQLException {



        if (Main.editData != -1) {
            addCustomerLabel.setText(resource.getString("editCustomer"));
            addCustomerButton.setText(resource.getString("updateCustomer"));


            id.setText(String.valueOf(Main.id));


            PreparedStatement ps = Main.db.prepareStatement("SELECT * FROM customers WHERE customer_id = ?");

            ps.setInt(1, Main.editData);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString(2));
                addressField.setText(rs.getString(3));
                postalField.setText(rs.getString(4));
                phoneField.setText(rs.getString(5).replaceAll("[^\\d]", ""));



                PreparedStatement ps1 = Main.db.prepareStatement("SELECT * FROM first_level_divisions WHERE division_id = ?");
                int i = rs.getInt(10);
                ps1.setInt(1, i);

                ResultSet rs1 = ps1.executeQuery();

                if (rs1.next()) {
                    stateProvinceLbl.setVisible(true);
                    divisionComboBox.setVisible(true);
                    divisionComboBox.getSelectionModel().select(rs1.getString(2));


                    PreparedStatement ps2 = Main.db.prepareStatement("SELECT country FROM countries WHERE country_id = ?");

                    ps2.setInt(1, rs1.getInt(7));
                    ResultSet rs2 = ps2.executeQuery();

                    if (rs2.next()) {
                        countryComboBox.getSelectionModel().select(rs2.getString(1));
                        divisionComboBox.setVisible(true);
                        stateProvinceLbl.setVisible(true);
                        if(countryComboBox.getValue().equals(resource.getString("unitedStates")))
                        {
                            Statement state = Main.db.createStatement();

                            ResultSet rs3 = state.executeQuery("SELECT division FROM first_level_divisions WHERE country_id = 1");


                            stateProvinceLbl.setText(resource.getString("state"));
                            divisionComboBox.getItems().clear();
                            while(rs3.next())
                            {
                                divisionComboBox.getItems().add(rs3.getString(1));
                            }

                        } else if(countryComboBox.getValue().equals(resource.getString("unitedKigdom"))) {
                            Statement state = Main.db.createStatement();

                            ResultSet rs3 = state.executeQuery("SELECT division FROM first_level_divisions WHERE country_id = 2");

                            stateProvinceLbl.setText("Region");
                            divisionComboBox.getItems().clear();
                            while(rs3.next())
                            {
                                divisionComboBox.getItems().add(rs3.getString(1));
                            }
                        } else {
                            Statement state = Main.db.createStatement();

                            ResultSet rs3 = state.executeQuery("SELECT division FROM first_level_divisions WHERE country_id = 3");

                            stateProvinceLbl.setText("Province");
                            divisionComboBox.getItems().clear();
                            while(rs3.next())
                            {
                                divisionComboBox.getItems().add(rs3.getString(1));
                            }
                        }



                    }

                }
            }
        }
    }


    /**
     * This method handles when the user tries to add or update into the customers table.
     * This includes all of the constraints to make sure the user isn't trying to add or update something incorrectly.
     * Regex is a useful tool here, I also implement some parsing techniques to make the phone number more presentable, and
     * alerts for the user's convenience.
     * @param event
     * @throws SQLException
     * @throws IOException
     */
    public void dataValidation(ActionEvent event) throws SQLException, IOException {




        if ((!this.nameField.getText().isEmpty() && this.nameField.getText().matches("[a-zA-Z].*")) &&
                (!this.addressField.getText().isEmpty()) &&
                (!this.postalField.getText().isEmpty() && this.postalField.getText().matches("[0-9].*")) &&
                (!this.phoneField.getText().isEmpty() && this.phoneField.getText().matches("[0-9]*"))) {



            long millis = System.currentTimeMillis();
            java.sql.Date date = new java.sql.Date(millis);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

            Timestamp time = new Timestamp(System.currentTimeMillis());

            Connection db = Main.db;


            PreparedStatement ps1 = db.prepareStatement("SELECT division_id FROM (first_level_divisions) WHERE division = ?");

            ps1.setString(1, divisionComboBox.getValue());

            ResultSet rs1 = ps1.executeQuery();

            if (Main.editData != -1) {
                PreparedStatement ps = db.prepareStatement("UPDATE customers SET customer_id = ?, customer_name = ?, address = ?, " +
                        "postal_code = ?, phone = ?, create_date = ?, created_by = ?, last_update = ?, last_updated_by = ?, division_id = ? WHERE customer_id = ?");

                if (rs1.next()) {

                    String numberFormatted = phoneField.getText().replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");

                    ps.setInt(1, (Main.editData));
                    ps.setString(2, nameField.getText());
                    ps.setString(3, addressField.getText());
                    ps.setString(4, postalField.getText());
                    ps.setString(5, numberFormatted);
                    ps.setDate(6, date);
                    ps.setString(7, LoginController.username);
                    ps.setTimestamp(8, time);
                    ps.setString(9, LoginController.username);

                    ps.setInt(10, rs1.getInt(1));

                    ps.setInt(11, (Main.editData));

                    ps.executeUpdate();

                    Main.changeScene(event, this.getClass().getResource("Customers.fxml"));
                }


            } else {


                if (rs1.next()) {

                    PreparedStatement ps = db.prepareStatement("INSERT INTO customers VALUES (?,?,?,?,?,?,?,?,?,?)");


                    String numberFormatted = phoneField.getText().replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");

                    int i = Main.id;
                    ps.setInt(1, i);
                    ps.setString(2, nameField.getText());
                    ps.setString(3, addressField.getText());
                    ps.setString(4, postalField.getText());
                    ps.setString(5, numberFormatted);
                    ps.setDate(6, date);
                    ps.setString(7, LoginController.username);
                    ps.setTimestamp(8, time);
                    ps.setString(9, LoginController.username);

                    ps.setInt(10, rs1.getInt(1));
                    ps.executeUpdate();
                    Main.id++;


                    Main.changeScene(event, this.getClass().getResource("Customers.fxml"));

                }
            }


        } else {
            alert.setContentText(resource.getString("fieldError"));
            alert.show();
        }
    }

    /**
     * This method handles the countryComboBox and sets the divisionComboBox to visible when the countryComboBox has a value entered,
     * It uses queries to implement all the data from the first_level_divisions Table so that the combo box is filled with the appropriate data.
     */
    public void countryHasData() throws SQLException {
        if(!countryComboBox.getItems().isEmpty())
        {
            divisionComboBox.setVisible(true);
            stateProvinceLbl.setVisible(true);
            if(countryComboBox.getValue().equals(resource.getString("unitedStates")))
            {
                Statement state = Main.db.createStatement();

                ResultSet rs = state.executeQuery("SELECT division FROM first_level_divisions WHERE country_id = 1");


                stateProvinceLbl.setText(resource.getString("state"));
                divisionComboBox.getItems().clear();
                while(rs.next())
                {
                    divisionComboBox.getItems().add(rs.getString(1));
                }

            } else if(countryComboBox.getValue().equals(resource.getString("unitedKigdom"))) {
                Statement state = Main.db.createStatement();

                ResultSet rs = state.executeQuery("SELECT division FROM first_level_divisions WHERE country_id = 2");

                stateProvinceLbl.setText("Region");
                divisionComboBox.getItems().clear();
                while(rs.next())
                {
                    divisionComboBox.getItems().add(rs.getString(1));
                }
            } else {
                Statement state = Main.db.createStatement();

                ResultSet rs = state.executeQuery("SELECT division FROM first_level_divisions WHERE country_id = 3");

                stateProvinceLbl.setText("Province");
                divisionComboBox.getItems().clear();
                while(rs.next())
                {
                    divisionComboBox.getItems().add(rs.getString(1));
                }
            }
        }
    }

    /**
     * This method sends the user to the Customers.fxml Scene.
     * @param event
     * @throws IOException
     */
    public void goBackToCustomers(ActionEvent event) throws IOException {
        Main.changeScene(event, this.getClass().getResource("Customers.fxml"));
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
