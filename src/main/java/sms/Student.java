package sms;

/**
 * Simple data model for one student.
 * Each Student object maps directly to one MongoDB document.
 */
public class Student {

    private String id;       // MongoDB _id (as String), empty for new students
    private String name;
    private String rollNo;
    private String course;
    private String semester;
    private String marks;

    public Student() {
    }

    public Student(String id, String name, String rollNo, String course, String semester, String marks) {
        this.id = id;
        this.name = name;
        this.rollNo = rollNo;
        this.course = course;
        this.semester = semester;
        this.marks = marks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }
}
