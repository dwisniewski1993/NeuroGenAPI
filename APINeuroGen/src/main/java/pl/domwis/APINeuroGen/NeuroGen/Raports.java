package pl.domwis.APINeuroGen.NeuroGen;

import org.json.JSONObject;

public class Raports {
    JSONObject allStudentsRaport;
    JSONObject teacherRecommendation;
    JSONObject studentRaport;

    Raports() {
    }

    public JSONObject getTeacherRecommendation() {
        return teacherRecommendation;
    }

    public void setTeacherRecommendation(JSONObject teacherRecommendation) {
        this.teacherRecommendation = teacherRecommendation;
    }

    public JSONObject getAllStudentsRaport() {
        return this.allStudentsRaport;
    }

    public JSONObject getStudentRaport() {
        return this.studentRaport;
    }

    public void setAllStudentsRaport(JSONObject allStd) {
        this.allStudentsRaport = allStd;
    }

    public void setStudentRaport(JSONObject Stud) {
        this.studentRaport = Stud;
    }
}
