package fxml_helloworld;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProjectDataManager {
    private static ProjectDataManager instance;
    private ObservableList<Project> supportedProjects = FXCollections.observableArrayList();

    private ProjectDataManager() {
        // Constructor private untuk singleton
    }

    public static ProjectDataManager getInstance() {
        if (instance == null) {
            instance = new ProjectDataManager();
        }
        return instance;
    }

    public ObservableList<Project> getSupportedProjects() {
        return supportedProjects;
    }

    public void addSupportedProject(Project project) {
        supportedProjects.add(project);
    }

    public void removeSupportedProject(Project project) {
        supportedProjects.remove(project);
    }
}
