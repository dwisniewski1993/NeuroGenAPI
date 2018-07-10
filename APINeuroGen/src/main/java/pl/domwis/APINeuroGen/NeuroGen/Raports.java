package pl.domwis.APINeuroGen.NeuroGen;

public class Raports {
    String allStudentsRaport;
    String studentRaport;

    Raports(){}

    Raports(String allStd, String Stud){
        this.allStudentsRaport = allStd;
        this.studentRaport = Stud;
    }

    public String getAllStudentsRaport(){
        return this.allStudentsRaport;
    }

    public String getStudentRaport(){
        return this.studentRaport;
    }

    public void setAllStudentsRaport(String allStd){
        this.allStudentsRaport = allStd;
    }

    public void setStudentRaport(String Stud){
        this.studentRaport = Stud;
    }
}
