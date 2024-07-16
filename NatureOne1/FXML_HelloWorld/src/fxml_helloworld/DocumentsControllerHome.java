package fxml_helloworld;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class DocumentsControllerHome implements Initializable {

    @FXML
    private Label nameMembers, nameOrganization;

    @FXML
    private BorderPane mainPane;

    @FXML
    private ScrollPane homePageContent;

    @FXML
    private void goToArticle(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("Article");
        mainPane.setCenter(halaman);
    }

    @FXML
    private void goToCampaign(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("Campaign");
        mainPane.setCenter(halaman);
    }

    @FXML
    private void projectOrganization(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("ProjectOrganization");
        mainPane.setCenter(halaman);
    }

    @FXML
    private void projectMembers(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("ProjectMembers");
        mainPane.setCenter(halaman);
    }

    @FXML
    private void reportMembers(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("ReportMembers");
        mainPane.setCenter(halaman);
    }

    @FXML
    private void reportOrganization(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("ReportOrganization");
        mainPane.setCenter(halaman);
    }

    @FXML
    private void donationLog(ActionEvent event) {
        OpenScene object = new OpenScene();
        Pane halaman = object.getPane("DonationLog");
        mainPane.setCenter(halaman);
    }

    @FXML
    private void backHomePage(ActionEvent event) {
        mainPane.setCenter(homePageContent);
    }

    // Controller Pindah Ke Login...
    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        OpenScene.loadFXML("Login.fxml", (Stage) ((Node) event.getSource()).getScene().getWindow(), event);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadUserData();

    }

    private void loadUserData() {
        XStream xstream = new XStream(new StaxDriver());
        xstream.allowTypes(new Class[] { User.class });

        // Mencoba memuat data dari dataMember.xml
        try {
            File memberFile = new File("dataMember.xml");
            if (memberFile.exists()) {
                User memberUser = (User) xstream.fromXML(memberFile);
                if (memberUser != null && nameMembers != null) {
                    nameMembers.setText(memberUser.getUsername());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading member data: " + e.getMessage());
            e.printStackTrace();
        }

        // Mencoba memuat data dari dataOrganization.xml
        try {
            File orgFile = new File("dataOrganization.xml");
            if (orgFile.exists()) {
                User orgUser = (User) xstream.fromXML(orgFile);
                if (orgUser != null && nameOrganization != null) {
                    nameOrganization.setText(orgUser.getUsername());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading organization data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}