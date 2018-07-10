package pl.domwis.APINeuroGen.NeuroGen;

public class Raports {
    String allStudentsRaport;
    String teacherRecommendation;
    String studentRaport;

    Raports() {
    }

    public String getTeacherRecommendation() {
        return teacherRecommendation;
    }

    public void setTeacherRecommendation(String teacherRecommendation) {
        this.teacherRecommendation = teacherRecommendation;
    }

    public String getAllStudentsRaport() {
        return this.allStudentsRaport;
    }

    public String getStudentRaport() {
        return this.studentRaport;
    }

    public void setAllStudentsRaport(String allStd) {
        this.allStudentsRaport = allStd;
    }

    public void setStudentRaport(String Stud) {
        this.studentRaport = Stud;
    }
}
