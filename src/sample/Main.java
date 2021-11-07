package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.*;


/**
 * @author Miles Engelbrecht
 * Main Class.
 * Includes the definition of a few static variables that are used to
 * help keep track of data when swapping from CustomersController to AddCustomerController and likewise with
 * Appointments.  See the respective Classes for more details.  I also defined changeScene as a static method,
 * this helps reduce some copy and paste and overall makes some of the code later on cleaner.
 */
public class Main extends Application {

    static Connection db;
    static int id;
    static int editData;

    static {
        try {
            Class.forName("wgudb.ucertify.com");
        } catch (ClassNotFoundException e) {
        }

        try {
            db = DriverManager.getConnection(
                    "jdbc:mysql://wgudb.ucertify.com:3306/WJ08baK", "U08baK", "53689242334");

        } catch (SQLException e) {
            System.out.println("SQL exception occured" + e);
        }
    }



    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }


    public static void main(String[] args) {


        launch(args);
    }

    /**
     * Helps reduce some clutter later on in my code.  Otherwise I'd have to copy and paste
     * the same 8-10 lines of code every time I switch scenes.  This cleans that up some.
     * @param event
     * @param sceneName
     * @throws IOException
     */
    public static void changeScene(ActionEvent event, URL sceneName) throws IOException {


        Parent mainParent = (Parent) FXMLLoader.load(sceneName);
        Scene mainScene = new Scene(mainParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(mainScene);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        window.setX((primScreenBounds.getWidth() - window.getWidth()) / 2);
        window.setY((primScreenBounds.getHeight() - window.getHeight()) / 2);
        window.show();
    }

}
