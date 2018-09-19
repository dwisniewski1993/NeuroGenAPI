package pl.domwis.APINeuroGen.NeuroGen;

import org.json.JSONObject;

public class Raports {
    public String allStudentsRaport;
    public String teacherRecommendation;
    public String studentRaport;

    public String getTeacherRecommendation() {
        return teacherRecommendation;
    }

    public void setTeacherRecommendation(String teacherRecommendation) {
        this.teacherRecommendation = teacherRecommendation;
    }

    public String getAllStudentsRaport() {
        return allStudentsRaport;
    }

    public String getStudentRaport() {
        return studentRaport;
    }

    public void setAllStudentsRaport(String allStd) {
        this.allStudentsRaport = allStd;
    }

    public void setStudentRaport(String Stud) {
        this.studentRaport = Stud;
    }
}
