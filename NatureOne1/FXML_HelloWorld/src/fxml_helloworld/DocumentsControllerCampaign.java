package fxml_helloworld;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class DocumentsControllerCampaign implements Initializable {

    @FXML
    private VBox campaignContainer;

    @FXML
    private BorderPane campaignBorderPane;

    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private static final String XML_FILE_PATH = "projects.xml";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProjectsFromXML();
        updateProjectList();

        String css = this.getClass().getResource("Resource/Campaign.css").toExternalForm();
        campaignContainer.getStylesheets().add(css);
        campaignContainer.setId("campaign-container");
    }

    private void loadProjectsFromXML() {
        File xmlFile = new File(XML_FILE_PATH);
        if (!xmlFile.exists()) {
            return;
        }

        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            NodeList nodeList = doc.getElementsByTagName("project");

            projects.clear();
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element element = (Element) nodeList.item(i);
                    projects.add(new Project(
                            getElementValue(element, "name"),
                            getElementValue(element, "description"),
                            Double.parseDouble(getElementValue(element, "donationTarget")),
                            Double.parseDouble(getElementValue(element, "completion"))));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getElementValue(Element element, String tagName) {
        return element.getElementsByTagName(tagName).item(0).getTextContent();
    }

    private void updateProjectList() {
        campaignContainer.getChildren().clear();
        for (Project project : projects) {
            campaignContainer.getChildren().add(createProjectBox(project));
        }
    }

    private VBox createProjectBox(Project project) {
        VBox box = new VBox(10);
        box.getStyleClass().add("project-box");

        Label nameLabel = new Label("Project: " + project.getNameProject());
        nameLabel.getStyleClass().add("project-name");

        Label targetLabel = new Label("Target: " + formatToRupiah(project.getDonationTarget()));
        targetLabel.getStyleClass().add("project-target");

        Button detailsButton = new Button("Details");
        detailsButton.getStyleClass().add("details-button");
        detailsButton.setOnAction(event -> showProjectDetails(project));

        Button paymentButton = new Button("Payment");
        paymentButton.getStyleClass().add("payment-button");
        paymentButton.setOnAction(event -> showPaymentPage(project));

        Button supportButton = new Button("Support");
        supportButton.getStyleClass().add("support-button");
        supportButton.setOnAction(event -> supportProject(project));

        HBox buttonBox = new HBox(10, detailsButton, paymentButton, supportButton);
        buttonBox.getStyleClass().add("button-box");

        box.getChildren().addAll(nameLabel, targetLabel, buttonBox);

        return box;
    }

    private void showProjectDetails(Project project) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Project Details");
        alert.setHeaderText(project.getNameProject());

        // Buat elemen teks untuk setiap bagian konten
        Text descriptionLabel = new Text("Description: ");
        Text descriptionText = new Text(project.getDescriptionProject() + "\n");
        Text targetLabel = new Text("Target: ");
        Text targetText = new Text(formatToRupiah(project.getDonationTarget()) + "\n");
        Text completionLabel = new Text("Completion: ");
        Text completionText = new Text(project.getCompletionPercentage() + "%");

        // Tambahkan kelas CSS pada elemen teks
        descriptionLabel.getStyleClass().add("label");
        targetLabel.getStyleClass().add("label");
        completionLabel.getStyleClass().add("label");
        descriptionText.getStyleClass().add("value");
        targetText.getStyleClass().add("value");
        completionText.getStyleClass().add("value");

        // Bungkus teks dalam TextFlow untuk memastikan pembungkusan teks
        TextFlow descriptionFlow = new TextFlow(descriptionLabel, descriptionText);
        TextFlow targetFlow = new TextFlow(targetLabel, targetText);
        TextFlow completionFlow = new TextFlow(completionLabel, completionText);

        // Batasi lebar maksimal dari TextFlow
        descriptionFlow.setMaxWidth(550);
        targetFlow.setMaxWidth(550);
        completionFlow.setMaxWidth(550);

        // Buat VBox dan tambahkan TextFlow
        VBox content = new VBox(20); // Jarak antar elemen adalah 10px
        content.getChildren().addAll(descriptionFlow, targetFlow, completionFlow);
        content.setPadding(new Insets(10)); // Padding di sekitar VBox
        content.setMaxWidth(320); // Batasi lebar maksimal dari VBox

        // Set VBox sebagai konten dari DialogPane
        alert.getDialogPane().setContent(content);

        // Tambahkan file CSS ke alert
        alert.getDialogPane().getStylesheets().add(getClass().getResource("Resource/Alert.css").toExternalForm());

        alert.showAndWait();
    }

    private void showPaymentPage(Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Payment.fxml"));
            Pane paymentPane = loader.load();

            DocumentsControllerPayments paymentController = loader.getController();
            paymentController.setProject(project);

            campaignBorderPane.setCenter(paymentPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void supportProject(Project project) {
        ProjectDataManager.getInstance().addSupportedProject(project);
        updateProjectList();
    }

    private String formatToRupiah(double amount) {
        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return rupiahFormat.format(amount);
    }
}