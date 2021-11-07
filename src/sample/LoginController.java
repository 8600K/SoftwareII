package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Interface.
 * This interface gets the password and returns a hash as an int.
 * The hashcode is then checked against the hashcode equivalent of "test"
 * and allows or disallows access to the program.
 */
@FunctionalInterface
interface PasswordInterface
{
    int getPassword();
}

/**
 * Sets up the different Labels, TextFields, PasswordField, Button and the Alert.
 * I also define a static String of username, this is used in adding customers and appointments,
 * as well as updating, there are fields which request the user that created or last edited the field, and
 * I use username to satisfy said field.
 */
public class LoginController implements Initializable {

    Locale locale = Locale.getDefault();
    ResourceBundle resource = ResourceBundle.getBundle("sample/Bundle", locale);

    static String username;

    @FXML
    private Label minimizeLbl;
    @FXML
    private Label signLbl;
    @FXML
    private Label userLbl;
    @FXML
    private Label passwordLbl;
    @FXML
    private Label locationLbl;
    @FXML
    private Label timeLbl;
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginBtn;


    private Alert alert;


    /**
     * Initialize method.
     * Nothing too crazy here, I get some system information such as Locale, ZoneId, and LocalTime.
     * Then I set in the bottom left corner of the Login Scene a different message depending on the time of day
     * in the user's respective time zone.
     * Also, this scene is required to be compatible with different languages.  So I define a resource variable which stores
     * ResourceBundle.getBundle().  The page is fully compatible with US English, UK English, CA French, and FR French.
     * @param url
     * @param resourceBundle
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Locale locale = Locale.getDefault();
        ResourceBundle resource = ResourceBundle.getBundle("sample/Bundle", locale);
        this.alert = new Alert(Alert.AlertType.ERROR);

        ZoneId zoneId = ZoneId.systemDefault();
        LocalTime time = LocalTime.now(zoneId);




        if(time.isBefore(LocalTime.of(12,00,00)))
        {
            this.timeLbl.setText(resource.getString("morning"));
        } else if(time.isBefore(LocalTime.of(17,00,00)))
        {
            this.timeLbl.setText(resource.getString("afternoon"));
        } else {
            this.timeLbl.setText(resource.getString("evening"));
        }

        this.locationLbl.setText(zoneId.getDisplayName(TextStyle.FULL, Locale.ROOT));

        this.alert = new Alert(Alert.AlertType.INFORMATION);

        //Helps keep Initialize clean.
        language(resource);

    }


    /**
     * <b>Lambda usage here.  The Lambda gets the text from the passwordField
     * and hashes it which is then stored in the PasswordInterface pi.</b>  From there
     * the method handles when the user attempts a login by tracking successful and unsuccessful
     * login attempts, this method also handles alerts in multiple languages.  Finally, if the login is proven
     * successful, the user will be sent to the Home Scene.
     * @param event ActionEvent
     * @throws IOException
     */
    public void login(ActionEvent event) throws IOException {
        username = this.userField.getText().toLowerCase();

        /**
         * Interface is called here.
         */
        PasswordInterface pi;


        pi = () -> this.passwordField.getText().hashCode();


        if(pi.getPassword() == 3556498 && username.equals("test"))
        {
            trackLogin(true);
            Main.changeScene(event, this.getClass().getResource("Home.fxml"));
            try {
                appointmentsWithin15Minutes();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            trackLogin(false);
            this.alert.show();
        }



    }

    /**
     * As the name suggests, this method checks for appointments that are scheduled within
     * 15 minutes of when the user logs on.  If one is found, an alert notifies the user.
     * @throws SQLException
     */
    private void appointmentsWithin15Minutes() throws SQLException {

        OffsetDateTime nowUTC = OffsetDateTime.now(ZoneOffset.UTC);

        Statement statement = Main.db.createStatement();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYY/MM/dd-hh:mm");

        ResultSet rs = statement.executeQuery("SELECT start, end, appointment_id FROM appointments");
        while(rs.next())
        {
            LocalDateTime time = rs.getTimestamp(1).toLocalDateTime();
            LocalDateTime end = rs.getTimestamp(2).toLocalDateTime();

            ZonedDateTime zdt = time.atZone(ZoneId.of("UTC"));
            ZonedDateTime endZdt = end.atZone(ZoneId.of("UTC"));

            ZonedDateTime zdtConverted = zdt.withZoneSameInstant(ZoneId.systemDefault());
            ZonedDateTime endZdtConverted = endZdt.withZoneSameInstant(ZoneId.systemDefault());


            Duration difference = Duration.between(time, nowUTC);

            if(difference.toDays() == 0 && difference.toMinutes() >= -15 && difference.toMinutes() <= 0)
            {

                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setHeaderText("");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.setContentText(resource.getString("appointmentIn15") + " Appointment ID: " + rs.getInt(3) + "\n" + "Date & Time: "
                        + zdtConverted.format(dtf) + " - " + endZdtConverted.format(dtf));
                alert.show();
            } else {
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setHeaderText("");
                alert.setContentText(resource.getString("notAppointmentIn15"));
                alert.show();
            }

        }


    }

    /**
     * This method handles when a user either logs successfully or unsuccessfully into the program, it creates or appends
     * to a txt file, and gives the ZonedDateTime and Date of when the user tried to log in, or logged in if successful.
     * @param b (Boolean)
     */
    private void trackLogin(boolean b) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        LocalDateTime ldt = LocalDateTime.now();

        ZonedDateTime zdt = ldt.atZone(ZoneId.of("UTC"));

        String finished = zdt.format(dateTimeFormatter);


        try {
            File file = new File("login_activity.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fWriter = new FileWriter(file, true);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            PrintWriter pWriter = new PrintWriter(bWriter);

            if(b)
            {
                pWriter.println("User: " + username + "Successfully logged into the System at: " + finished);
            } else {
                pWriter.println("User attempted to login with username: " + username + "Access denied to the System at: " + finished);
            }
            pWriter.flush();
            pWriter.close();

        } catch (IOException e){
            e.printStackTrace();
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
     * This method is used just to keep the initialize method clean, all this does is set
     * all the text in the scene to be compatible with french or english.
     * @param resource
     */
    private void language(ResourceBundle resource)
    {
        this.signLbl.setText(resource.getString("signIn"));
        this.userLbl.setText(resource.getString("username"));
        this.passwordLbl.setText(resource.getString("password"));
        this.loginBtn.setText(resource.getString("login"));

        this.alert.setTitle("");
        this.alert.setHeaderText(resource.getString("error"));
        this.alert.setContentText(resource.getString("loginError"));
    }

    /**
     * This method exits the program if the x button is pressed.
     */
    public void exitTheProgram() {
        System.exit(0);
    }


}
