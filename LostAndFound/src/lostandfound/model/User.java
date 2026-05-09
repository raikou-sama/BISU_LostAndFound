package lostandfound.model;

public class User {
    private int id;
    private String fullName;
    private String email;
    private String password;
    private String role;
    private String studentId;
    private String college;
    private String program;
    private int yearLevel;
    private String section;

    public User() {}

    public User(int id, String fullName, String email, String password,
                String role, String studentId) {
        this(id, fullName, email, password, role, studentId, null, null, 0, null);
    }

    public User(int id, String fullName, String email, String password, String role,
                String studentId, String college, String program, int yearLevel, String section) {
        this.id = id; this.fullName = fullName; this.email = email;
        this.password = password; this.role = role; this.studentId = studentId;
        this.college = college; this.program = program;
        this.yearLevel = yearLevel; this.section = section;
    }

    public int getId()               { return id; }
    public void setId(int id)        { this.id = id; }
    public String getFullName()      { return fullName; }
    public void setFullName(String v){ this.fullName = v; }
    public String getEmail()         { return email; }
    public void setEmail(String v)   { this.email = v; }
    public String getPassword()      { return password; }
    public void setPassword(String v){ this.password = v; }
    public String getRole()          { return role; }
    public void setRole(String v)    { this.role = v; }
    public String getStudentId()     { return studentId; }
    public void setStudentId(String v){ this.studentId = v; }
    public String getCollege()       { return college; }
    public void setCollege(String v) { this.college = v; }
    public String getProgram()       { return program; }
    public void setProgram(String v) { this.program = v; }
    public int getYearLevel()        { return yearLevel; }
    public void setYearLevel(int v)  { this.yearLevel = v; }
    public String getSection()       { return section; }
    public void setSection(String v) { this.section = v; }

    public String getYearSection()   { return yearLevel > 0 ? yearLevel + "-" + section : ""; }

    @Override public String toString() { return fullName; }

    public String getDepartment() {
        return college;
    }

    public String getCourse() {
        return program;
    }

    public int getYear() {
        return yearLevel;
    }
}