package sample;

import java.sql.Timestamp;
import java.util.Date;


/**
 * This class is created for the sake of the TableView in CustomersController.
 * See CustomersController for more details on how and where it is used.
 */
public class Customers {
    private int customerId;
    private String customerName;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private Date createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;
    private int divisionId;

    public Customers(int customerId, String customerName, String address, String postalCode,
                     String phoneNumber, Date createDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy,
                     int divisionId)
    {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.divisionId = divisionId;
    }

    /**
     * Getter for customerId.
     * @return
     */
    public int getCustomerId(){return this.customerId;}

    /**
     * Setter for customerId.
     * @param customerId
     */
    public void setCustomerId(int customerId) { this.customerId = customerId;}

    /**
     * Getter for customerName.
     * @return
     */
    public String getCustomerName(){return this.customerName;}

    /**
     * Setter for customerName.
     * @param customerName
     */
    public void setCustomerName(String customerName) { this.customerName = customerName;}

    /**
     * Getter for address.
     * @return
     */
    public String getAddress(){return this.address;}

    /**
     * Setter for address.
     * @param address
     */
    public void setAddress(String address) { this.address = address;}

    /**
     * Getter for postalCode.
     * @return
     */
    public String getPostalCode(){return this.postalCode;}

    /**
     * Setter for postalCode.
     * @param postalCode
     */
    public void setPostalCode(String postalCode) { this.postalCode = postalCode;}

    /**
     * Getter for phoneNumber.
     * @return
     */
    public String getPhoneNumber() { return phoneNumber; }

    /**
     * Setter for phoneNumber.
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    /**
     * Getter for createDate.
     * @return
     */
    public Date getCreateDate(){return this.createDate;}

    /**
     * Setter for createDate.
     * @param createDate
     */
    public void setCreateDate(Date createDate) { this.createDate = createDate;}

    /**
     * Getter for createdBy.
     * @return
     */
    public String getCreatedBy(){return this.createdBy;}

    /**
     * Setter for createdBy.
     * @param createdBy
     */
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy;}

    /**
     * Getter for lastUpdate.
     * @return
     */
    public Timestamp getLastUpdate() { return lastUpdate; }

    /**
     * Setter for lastUpdate
     * @param lastUpdate
     */
    public void setLastUpdate(Timestamp lastUpdate){this.lastUpdate = lastUpdate;}

    /**
     * Getter for lastUpdatedBy.
     * @return
     */
    public String getLastUpdatedBy(){return this.lastUpdatedBy;}

    /**
     * Setter for lastUpdatedBy.
     * @param postalCode
     */
    public void setLastUpdatedBy(String postalCode) { this.lastUpdatedBy = lastUpdatedBy;}

    /**
     * Getter for divisionId.
     * @return
     */
    public int getDivisionId() {
        return divisionId;
    }

    /**
     * Setter for divisionId.
     * @param divisionId
     */
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }
}
