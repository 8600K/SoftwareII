package sample;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * This class is created for the sake of the TableView in AppointmentsController.
 * See AppointmentsController for more details on how and where it is used.
 */
public class Appointments {
    private int appointmentId;
    private String title;
    private String description;
    private String location;
    private String type;
    private String start;
    private String end;
    private Date createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;
    private int customerId;
    private int userId;
    private int contactId;


    public Appointments(int appointmentId, String title, String description, String location, String type, String start, String end, Date createDate,
                        String createdBy, Timestamp lastUpdate, String lastUpdatedBy, int customerId, int userId, int contactId)
    {
        this.appointmentId = appointmentId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.customerId = customerId;
        this.userId = userId;
        this.contactId = contactId;
    }

    /**
     * Setter for lastUpdatedBy.
     * @param lastUpdatedBy (String)
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Getter for lastUpdatedBy.
     * @return lastUpdatedBy
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Setter for contactId.
     * @param contactId (int)
     */
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * Getter for contactId.
     * @return contactId
     */
    public int getContactId() {
        return contactId;
    }

    /**
     * Getter for createDate.
     * @return createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Getter for end.
     * @return end
     */
    public String getEnd() {
        return end;
    }

    /**
     * Getter for start.
     * @return start
     */
    public String getStart() {
        return start;
    }

    /**
     * Getter for appointmentId.
     * @return appointmentId
     */
    public int getAppointmentId() {
        return appointmentId;
    }

    /**
     * Getter for customerId.
     * @return customerId
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Getter for userId.
     * @return userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Getter for createdBy.
     * @return createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Getter for description.
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for location.
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Getter for title.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for type.
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Getter for lastUpdate.
     * @return lastUpdate
     */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Setter for appointmentId.
     * @param appointmentId (int)
     */
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     * Setter for createDate.
     * @param createDate (date)
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * Setter for createdBy.
     * @param createdBy (string)
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Setter for customerId.
     * @param customerId (int)
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Setter for description.
     * @param description (string)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Setter for end.
     * @param end (string)
     */
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * Setter for lastUpdate.
     * @param lastUpdate (timestamp)
     */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Setter for location.
     * @param location (location)
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Setter for start.
     * @param start (string)
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * Setter for title.
     * @param title (string)
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Setter for type.
     * @param type (string)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Setter for userId.
     * @param userId (int)
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Getter for month.
     * @return date.getMonth().getValue()
     */
    public int getMonth()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
        LocalDate date = LocalDate.parse(start, dtf);
        return date.getMonth().getValue();



    }

    /**
     * Getter for week.
     * @return weekofYear
     */
    public int getWeek()
    {
        Locale locale = Locale.getDefault();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
        LocalDate date = LocalDate.parse(start, dtf);
        int weekOfYear = date.get(WeekFields.of(locale).weekOfYear());
        return weekOfYear;
    }

}


