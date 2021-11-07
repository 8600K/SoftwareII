Software II Appointment/Customer GUI.  

The purpose of the application is to be able to connect to a remote database consisting of six tables that includes countries, first-level divisions, customers, users, appointments, and contacts.  The program allows the user to be able to not only view the contents of the customers and appointments tables, but also allows the user to add, update, and delete rows within these columns.  The user can also view specified reports from the various tables, and the program contains systems in place to notify the user if an upcoming appointment is within fifteen minutes of starting.  The program also has a log in screen, and tracks both successful and unsuccessful logins, along with when they were attempted.  The system works with other time mechanics, involving the ability to convert time zones from the system default to UTC, and then to EST where the company that runs the program is located, thus ensuring that when a user attempts to add an appointment, the appointment will fall within company hours.  The login and home page are also fully compatible with both English and French, including both United Kingdom and Canadian versions.  

Author: Miles Engelbrecht.

Student Application Version: 1.1.1.

Date: 31/07/2021

IntelliJ Version: IntelliJ IDEA 2020.2 (Community Edition)
Build #IC-202.6397.94, built on July 27, 2020
Runtime version: 11.0.7+10-b944.20 amd64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
Windows 10 10.0
JDK Version: 11.0.8
JavaFX Version: 11.0.2

Directions:
Upon running the program, the user will be met with the login page.  The message near the bottom left-hand corner will say either good morning, good afternoon, or good evening depending on the time of day from the user’s system’s locale settings.  If the user enters ‘test’ as both username and password, they will move on to the home page.  If anything else is inputted, an error will occur, and the user will not be permitted access.  Regardless of success, this will trigger the login_activity.txt file to be created (if this is the first time the user is running the program) and the timestamp and success or lack thereof will be appended to the file. 
Once at the home scene, an alert will appear informing the user if they have an appointment scheduled within the next 15 minutes or not.  Either way, the user will be able to sign out, view reports, edit customer records, or edit appointment records.  (Along with minimize and exit the program, but that’s on just about every scene) Sign out is self-explanatory, it will take the user back to the login screen.  If the user chooses customer records, they will be brought to a new scene that has a table view which consists of 10 columns that are populated with data from the customer table.  From this scene the user can go back to the home scene, or add, edit or delete a customer.  In order to do the latter 2, the user must select a row before they can add or update, otherwise they will be met with an alert.  If the user adds or updates a customer, they will be met with a new scene that is either prepopulated with data in the various text fields and combo boxes, or mostly empty based on the user’s previous decision.  From there, the user just needs to add or edit the data, and then press the Add Customer or Update Customer button at the bottom.  The user can also select cancel and it will take them back to the previous scene should they change their mind.  
The appointments scene is very similar to the customer scene; however, this scene also has two radio buttons titled Months, and Weeks.  These buttons will sort the appointments data from the table view by months or weeks, respectively.  Adding and updating values into appointments has a lot more constraints as this requires the user to set dates and times.  One such constraint ensures the user cannot set the date to a previous day, as that would not make sense, another ensures the user’s time falls within company hours, and yet another ensures the time is formatted properly.  All of these and other constraints will alert the user to their error should something not fall in line with the program’s constraints.  
Lastly, the view reports will bring the user to a scene with a combo box with four different reports, a view reports button, and a go back button.  We’ve already established what the go back button does, and if the user attempts to view report without having anything in the combo box an alert will appear to inform the user of his/her mistake.  Otherwise the user will select one of the reports and then either a combo box or text field will appear atop the reports combo box.  Once the user enters a value in there, the user can then press view report and the report will appear as either two labels or a table view.  

Additional Note:  The Two required Lambda statements are in ReportsController, and LoginController.  
In the JavaDoc I have the method's say, "Lambda Usage within this class." in bold for easy naviagation.  
ZipReport()
Login(ActionEvent event)
Are the two methods the Lambda statements can be found in.

Additional Report: The additional report I created is “Number of Customers with Same Area Code”, in the reports scene the user will be given a combo box upon selection of this report.  Within said combo box will be all the area codes currently being used, (they won’t overlap with the same value as the DISTINCT keyword was used to obtain these results).  Once the user selects which area code and presses view reports, the scene will inform the user the number of customers within said area code.

MySQL Connector Driver: mysql-connector-java-8.0.25.
