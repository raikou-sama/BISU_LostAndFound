package lostandfound.model;

import java.util.Date;

public class Claim {
    private int id;
    private int itemId;
    private String itemName;
    private int claimedBy;
    private String claimerName;
    private String claimerStudentId;
    private String claimerCollege;
    private String claimerProgram;
    private String claimerYearSection;
    private String proofDetails;
    private String status;
    private String adminNotes;
    private int reviewedBy;
    private String reviewerName;
    private Date dateClaimed;
    private Date dateReviewed;

    public Claim() {}

    public int getId()                    { return id; }
    public void setId(int id)             { this.id = id; }
    public int getItemId()                { return itemId; }
    public void setItemId(int v)          { this.itemId = v; }
    public String getItemName()           { return itemName; }
    public void setItemName(String v)     { this.itemName = v; }
    public int getClaimedBy()             { return claimedBy; }
    public void setClaimedBy(int v)       { this.claimedBy = v; }
    public String getClaimerName()        { return claimerName; }
    public void setClaimerName(String v)  { this.claimerName = v; }
    public String getClaimerStudentId()   { return claimerStudentId; }
    public void setClaimerStudentId(String v){ this.claimerStudentId = v; }
    public String getClaimerCollege()     { return claimerCollege; }
    public void setClaimerCollege(String v){ this.claimerCollege = v; }
    public String getClaimerProgram()     { return claimerProgram; }
    public void setClaimerProgram(String v){ this.claimerProgram = v; }
    public String getClaimerYearSection() { return claimerYearSection; }
    public void setClaimerYearSection(String v){ this.claimerYearSection = v; }
    public String getProofDetails()       { return proofDetails; }
    public void setProofDetails(String v) { this.proofDetails = v; }
    public String getStatus()             { return status; }
    public void setStatus(String v)       { this.status = v; }
    public String getAdminNotes()         { return adminNotes; }
    public void setAdminNotes(String v)   { this.adminNotes = v; }
    public int getReviewedBy()            { return reviewedBy; }
    public void setReviewedBy(int v)      { this.reviewedBy = v; }
    public String getReviewerName()       { return reviewerName; }
    public void setReviewerName(String v) { this.reviewerName = v; }
    public Date getDateClaimed()          { return dateClaimed; }
    public void setDateClaimed(Date v)    { this.dateClaimed = v; }
    public Date getDateReviewed()         { return dateReviewed; }
    public void setDateReviewed(Date v)   { this.dateReviewed = v; }

    public String getClaimerDept() {
        return claimerCollege;
    }

    public String getClaimerCourse() {
        return claimerProgram;
    }
}