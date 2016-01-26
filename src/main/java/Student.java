import java.util.ArrayList;
import java.util.List;
import org.sql2o.*;

public class Student {
  private int mId;
  private String mName;
  private String mEnrollmentDate;
  private int mDepartment;

  public int getId() {
    return mId;
  }

  public String getName() {
    return mName;
  }

  public String getEnrollmentDate() {
    return mEnrollmentDate;
  }

  public int getDepartment() {
    return mDepartment;
  }

  public Student(String name) {
    this.mName = name;
  }

  @Override
  public boolean equals(Object otherStudent) {
    if (!(otherStudent instanceof Student)) {
      return false;
    } else {
      Student newStudent = (Student) otherStudent;
      return this.getName().equals(newStudent.getName()) &&
        this.getEnrollmentDate().equals(newStudent.getEnrollmentDate());
    }
  }

  public static List<Student> all() {
    String sql = "SELECT id AS mId, name AS mName, enrollment_date AS mEnrollmentDate, department_id AS mDepartment FROM students";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Student.class);
    }
  }

  public void setEnrollmentDate(String enrollment) {
    mEnrollmentDate = enrollment;
    String sql = "UPDATE students SET enrollment_date = to_date(:enrollment, 'YYYY-MM-DD') WHERE id = :id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("enrollment", enrollment)
        .addParameter("id", mId)
        .executeUpdate();
    }
  }

  public void save() {
    String sql = "INSERT INTO students(name) VALUES (:name)";
    try(Connection con = DB.sql2o.open()) {
      this.mId = (int) con.createQuery(sql, true)
        .addParameter("name", this.mName)
        .executeUpdate()
        .getKey();
    }
  }

  public void setMajor(int department) {
    this.mDepartment = department;
    String sql = "UPDATE students SET department_id = :departmentId WHERE id = :id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("departmentId", department)
        .addParameter("id", mId)
        .executeUpdate();
    }
  }

    public static Student find(int id) {
      String sql = "SELECT id AS mId, name AS mName, enrollment_date AS mEnrollmentDate, department_id AS mDepartment FROM students WHERE id = :id";
      try(Connection con = DB.sql2o.open()) {
        Student student = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Student.class);
      return student;
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
      String deleteRelationships = "DELETE FROM enrollments WHERE student_id = :id";
      con.createQuery(deleteRelationships)
        .addParameter("id", mId)
        .executeUpdate();
    }

    try(Connection con = DB.sql2o.open()) {
    String deleteStudent = "DELETE FROM students WHERE id = :id;";
    con.createQuery(deleteStudent)
      .addParameter("id", mId)
      .executeUpdate();
    }
  }

  public void update(String newName) {
    mName = newName;
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE students SET name = :name WHERE id = :id";
      con.createQuery(sql)
        .addParameter("name", newName)
        .addParameter("id", mId)
        .executeUpdate();
    }
  }
}
