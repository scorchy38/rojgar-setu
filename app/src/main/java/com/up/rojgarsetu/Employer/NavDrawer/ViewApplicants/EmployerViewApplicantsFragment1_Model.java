package com.up.rojgarsetu.Employer.NavDrawer.ViewApplicants;

public class EmployerViewApplicantsFragment1_Model {

    private String Name;
    private String jobappliedID;

    public String getJobappliedID() {
        return jobappliedID;
    }

    public void setJobappliedID(String jobappliedID) {
        this.jobappliedID = jobappliedID;
    }

    public EmployerViewApplicantsFragment1_Model(){

    }

    public EmployerViewApplicantsFragment1_Model(String Name){
        this.Name=Name;

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }
}
