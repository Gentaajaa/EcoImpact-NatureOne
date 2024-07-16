package fxml_helloworld;

public class Project {

    private String nameProject;
    private String descriptionProject;
    private double donationTarget;
    private double completionPercentage;

    public Project() {

    }

    public Project(String nameProject, String descriptionProject, double donationTarget, double completion) {
        this.nameProject = nameProject;
        this.descriptionProject = descriptionProject;
        this.donationTarget = donationTarget;
        this.completionPercentage = completion;
    }

    public String getNameProject() {
        return nameProject;
    }

    public void setNameProject(String nameProject) {
        this.nameProject = nameProject;
    }

    public String getDescriptionProject() {
        return descriptionProject;
    }

    public void setDescriptionProject(String descriptionProject) {
        this.descriptionProject = descriptionProject;
    }

    public double getDonationTarget() {
        return donationTarget;
    }

    public void setDonationTarget(double donationTarget) {
        this.donationTarget = donationTarget;
    }

    public double getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
}
