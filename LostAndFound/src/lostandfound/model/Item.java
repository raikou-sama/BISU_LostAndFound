package lostandfound.model;

import java.util.Date;

public class Item {
    private int id;
    private String itemName;
    private String description;
    private String category;
    private String location;
    private String status;
    private String type;
    private String contactInfo;
    private int reportedBy;
    private String reporterName;
    private Date dateReported;

    public Item() {}

    public Item(int id, String itemName, String description, String category,
                String location, String status, String type, String contactInfo,
                int reportedBy, String reporterName, Date dateReported) {
        this.id = id; this.itemName = itemName; this.description = description;
        this.category = category; this.location = location; this.status = status;
        this.type = type; this.contactInfo = contactInfo;
        this.reportedBy = reportedBy; this.reporterName = reporterName;
        this.dateReported = dateReported;
    }

    public int getId()                { return id; }
    public void setId(int id)         { this.id = id; }
    public String getItemName()       { return itemName; }
    public void setItemName(String v) { this.itemName = v; }
    public String getDescription()    { return description; }
    public void setDescription(String v){ this.description = v; }
    public String getCategory()       { return category; }
    public void setCategory(String v) { this.category = v; }
    public String getLocation()       { return location; }
    public void setLocation(String v) { this.location = v; }
    public String getStatus()         { return status; }
    public void setStatus(String v)   { this.status = v; }
    public String getType()           { return type; }
    public void setType(String v)     { this.type = v; }
    public String getContactInfo()    { return contactInfo; }
    public void setContactInfo(String v){ this.contactInfo = v; }
    public int getReportedBy()        { return reportedBy; }
    public void setReportedBy(int v)  { this.reportedBy = v; }
    public String getReporterName()   { return reporterName; }
    public void setReporterName(String v){ this.reporterName = v; }
    public Date getDateReported()     { return dateReported; }
    public void setDateReported(Date v){ this.dateReported = v; }
}