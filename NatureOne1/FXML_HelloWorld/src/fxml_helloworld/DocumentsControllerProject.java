package fxml_helloworld;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.chart.PieChart;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class DocumentsControllerProject implements Initializable {

    @FXML
    private TextField donationTargetField, projectNameField, completionPercentageField;

    @FXML
    private TextArea projectDescriptionField;

    @FXML
    private Button btnSubmit;

    @FXML
    private VBox projectsContainer;

    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private static final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private static final String XML_FILE_PATH = "projects.xml";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        loadProjectsFromXML();
        updateProjectList();
    }

    private void setupUI() {
        btnSubmit.setOnAction(event -> submitProject());
        donationTargetField.setTextFormatter(createDonationFormatter());
        projectsContainer.getStylesheets().add(getClass().getResource("Resource/Project.css").toExternalForm());
        projectsContainer.setSpacing(15);
    }

    private TextFormatter<String> createDonationFormatter() {
        return new TextFormatter<>(change -> {
            if (change.getControlNewText().isEmpty())
                return change;
            try {
                String newText = change.getControlNewText().replaceAll("[^\\d]", "");
                long value = Long.parseLong(newText);
                change.setText(formatRupiahInput(value));
                change.setRange(0, change.getControlText().length());
                change.setAnchor(change.getCaretPosition());
            } catch (NumberFormatException e) {
                return null;
            }
            return change;
        });
    }

    @FXML
    private void submitProject() {
        try {
            String name = projectNameField.getText();
            String description = projectDescriptionField.getText();
            double donationTarget = Double.parseDouble(donationTargetField.getText().replaceAll("[^\\d]", ""));
            double completionPercentage = Double.parseDouble(completionPercentageField.getText());

            if (name.isEmpty() || description.isEmpty()) {
                showAlert("Error", "Tolong Lengkapi Data");
                return;
            }

            if (completionPercentage < 0 || completionPercentage > 100) {
                showAlert("Error", "Persentase penyelesaian harus antara 0 dan 100");
                return;
            }

            Project newProject = new Project(name, description, donationTarget, completionPercentage);
            projects.add(newProject);
            clearFields();
            updateProjectList();
            saveProjectsToXML();
        } catch (NumberFormatException e) {
            showAlert("Error", "Tolong Masukan Angka yang Benar untuk Target Donasi dan Persentase Penyelesaian");
        }
    }

    private void clearFields() {
        projectNameField.clear();
        projectDescriptionField.clear();
        donationTargetField.clear();
        completionPercentageField.clear();
    }

    private void updateProjectList() {
        projectsContainer.getChildren().clear();
        for (int i = 0; i < projects.size(); i++) {
            projectsContainer.getChildren().add(createProjectBox(projects.get(i)));
            if (i < projects.size() - 1) {
                projectsContainer.getChildren().add(new Region() {
                    {
                        setPrefHeight(20);
                    }
                });
            }
        }
    }

    private VBox createProjectBox(Project project) {
        VBox box = new VBox(10);
        box.getStyleClass().add("project-box");

        PieChart progressChart = createProgressPieChart(project);
        progressChart.getStyleClass().add("project-progress");

        VBox projectDetails = new VBox(5);
        projectDetails.getStyleClass().add("project-details");

        projectDetails.getChildren().addAll(
                createStyledLabel("Project: " + project.getNameProject(), "project-name"),
                createStyledLabel("Description: " + project.getDescriptionProject(), "project-description"),
                createStyledLabel("Target: " + formatRupiahOutput(project.getDonationTarget()), "project-target"),
                createStyledLabel("Penyelesaian: " + String.format("%.2f%%", project.getCompletionPercentage()),
                        "project-completion"));

        HBox projectContainer = new HBox(10);
        projectContainer.getStyleClass().add("project-container");
        HBox.setHgrow(projectDetails, Priority.ALWAYS); // Allow projectDetails to grow
        projectContainer.getChildren().addAll(projectDetails, progressChart);

        HBox projectActions = new HBox(10);
        projectActions.getStyleClass().add("project-actions");
        projectActions.getChildren().addAll(
                new Button("Edit") {
                    {
                        setOnAction(event -> editProject(projects.indexOf(project)));
                    }
                },
                new Button("Delete") {
                    {
                        setOnAction(event -> deleteProject(projects.indexOf(project)));
                    }
                });

        box.getChildren().addAll(projectContainer, projectActions);

        return box;
    }

    private PieChart createProgressPieChart(Project project) {
        PieChart.Data completedSlice = new PieChart.Data("Completed", project.getCompletionPercentage());
        PieChart.Data remainingSlice = new PieChart.Data("Remaining", 100 - project.getCompletionPercentage());

        PieChart chart = new PieChart();
        chart.getData().addAll(completedSlice, remainingSlice);
        chart.setLegendVisible(false);
        chart.setLabelsVisible(false);

        remainingSlice.getNode().setStyle("-fx-pie-color: #97BCD7;");
        completedSlice.getNode().setStyle("-fx-pie-color: #0077B6;");

        return chart;
    }

    private Label createStyledLabel(String text, String styleClass) {
        return new Label(text) {
            {
                getStyleClass().add(styleClass);
            }
        };
    }

    private String formatRupiahInput(long amount) {
        return String.format("%,d", amount).replace(",", ".");
    }

    private String formatRupiahOutput(double amount) {
        return rupiahFormat.format(amount).replace("Rp", "Rp ");
    }

    private void showAlert(String title, String content) {
        new Alert(Alert.AlertType.ERROR, content, ButtonType.OK) {
            {
                setTitle(title);
                setHeaderText(null);
            }
        }.showAndWait();
    }

    private void saveProjectsToXML() {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element rootElement = doc.createElement("projects");
            doc.appendChild(rootElement);

            for (Project project : projects) {
                Element projectElement = doc.createElement("project");
                rootElement.appendChild(projectElement);

                addElement(doc, projectElement, "name", project.getNameProject());
                addElement(doc, projectElement, "description", project.getDescriptionProject());
                addElement(doc, projectElement, "donationTarget", String.valueOf(project.getDonationTarget()));
                addElement(doc, projectElement, "completion", String.valueOf(project.getCompletionPercentage()));
            }

            TransformerFactory.newInstance().newTransformer().transform(
                    new DOMSource(doc), new StreamResult(new File(XML_FILE_PATH)));
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save projects to XML");
        }
    }

    private void addElement(Document doc, Element parent, String name, String value) {
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(value));
        parent.appendChild(element);
    }

    private void loadProjectsFromXML() {
        File xmlFile = new File(XML_FILE_PATH);
        if (!xmlFile.exists())
            return;

        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            NodeList nodeList = doc.getElementsByTagName("project");

            projects.clear();
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nodeList.item(i);
                    projects.add(new Project(
                            getElementValue(element, "name"),
                            getElementValue(element, "description"),
                            Double.parseDouble(getElementValue(element, "donationTarget")),
                            Double.parseDouble(getElementValue(element, "completion"))));
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load projects from XML");
        }
    }

    private String getElementValue(Element element, String tagName) {
        return element.getElementsByTagName(tagName).item(0).getTextContent();
    }

    private void updateProject(int index, Project updatedProject) {
        if (index >= 0 && index < projects.size()) {
            projects.set(index, updatedProject);
            saveProjectsToXML();
            updateProjectList();
        }
    }

    private void deleteProject(int index) {
        if (index >= 0 && index < projects.size()) {
            projects.remove(index);
            saveProjectsToXML();
            updateProjectList();
        }
    }

    private void editProject(int index) {
        Project project = projects.get(index);
        projectNameField.setText(project.getNameProject());
        projectDescriptionField.setText(project.getDescriptionProject());
        donationTargetField.setText(formatRupiahInput((long) project.getDonationTarget()));
        completionPercentageField.setText(String.format("%.2f", project.getCompletionPercentage()));

        btnSubmit.setText("Update");
        btnSubmit.setOnAction(event -> {
            updateProject(index, new Project(
                    projectNameField.getText(),
                    projectDescriptionField.getText(),
                    Double.parseDouble(donationTargetField.getText().replaceAll("[^\\d]", "")),
                    Double.parseDouble(completionPercentageField.getText())));
            clearFields();
            btnSubmit.setText("Submit");
            btnSubmit.setOnAction(e -> submitProject());
        });
    }
}