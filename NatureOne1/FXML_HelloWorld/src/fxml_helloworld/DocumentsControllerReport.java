package fxml_helloworld;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DocumentsControllerReport implements Initializable {

    @FXML
    private TextField contactField, damageTypeField, locationField, nameField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Button submitButton;

    @FXML
    private Button deleteButton, seeDetailButton;

    @FXML
    private TableView<Report> reportTable;

    @FXML
    private TableColumn<Report, String> nameColumn;

    @FXML
    private TableColumn<Report, String> contactColumn;

    @FXML
    private TableColumn<Report, String> locationColumn;

    @FXML
    private TableColumn<Report, String> damageTypeColumn;

    @FXML
    private TableColumn<Report, String> descriptionColumn;

    @FXML
    private TableColumn<Report, LocalDate> dateColumn;

    private static final String XML_FILE_PATH = "reports.xml";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadReportsFromXML();

        if (submitButton != null) {
            submitButton.setOnAction(event -> submitReport());
        }

        if (seeDetailButton != null) {
            seeDetailButton.setOnAction(event -> seeDetail());
        }

        if (deleteButton != null) {
            deleteButton.setOnAction(event -> deleteReport());
        }
    }

    private void setupTable() {
        if (reportTable != null) {
            if (nameColumn != null)
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            if (contactColumn != null)
                contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
            if (locationColumn != null)
                locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
            if (damageTypeColumn != null)
                damageTypeColumn.setCellValueFactory(new PropertyValueFactory<>("damageType"));
            if (descriptionColumn != null)
                descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            if (dateColumn != null)
                dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        }
    }

    @FXML
    private void submitReport() {
        String name = nameField.getText();
        String contact = contactField.getText();
        String location = locationField.getText();
        String damageType = damageTypeField.getText();
        String description = descriptionArea.getText();

        if (name.isEmpty() || contact.isEmpty() || location.isEmpty() || damageType.isEmpty()
                || description.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        Report report = new Report(name, contact, location, damageType, description);
        if (datePicker.getValue() != null) {
            report.setDate(datePicker.getValue());
        }
        saveReportToXML(report);
        clearInputFields();
        showAlert("Report submitted successfully.");
    }

    @FXML
    private void seeDetail() {
        Report selectedReport = reportTable.getSelectionModel().getSelectedItem();
        if (selectedReport != null) {
            String name = selectedReport.getName();
            String contact = selectedReport.getContact();
            String location = selectedReport.getLocation();
            String damageType = selectedReport.getDamageType();
            String description = selectedReport.getDescription();
            LocalDate date = selectedReport.getDate();

            String details = "Name: " + name + "\n" +
                    "Contact: " + contact + "\n" +
                    "Location: " + location + "\n" +
                    "Damage Type: " + damageType + "\n" +
                    "Description: \n" + insertLineBreaks(description) + "\n" +
                    "Date: " + date;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Report Details");
            alert.setHeaderText(null);
            alert.setContentText(details);

            alert.getDialogPane().setStyle(
                    "-fx-font-size: 14px; -fx-font-family: 'Arial'; -fx-background-color: #f0f0f0; -fx-padding: 5px;");

            alert.getDialogPane().setPrefWidth(1000);

            VBox vbox = new VBox(10);
            vbox.setPadding(new Insets(10));
            Label nameLabel = new Label("Name: " + name);
            Label contactLabel = new Label("Contact: " + contact);
            Label locationLabel = new Label("Location: " + location);
            Label damageTypeLabel = new Label("Damage Type: " + damageType);
            Label descriptionLabel = new Label("Description:");
            Label descriptionText = new Label(insertLineBreaks(description));
            Label dateLabel = new Label("Date: " + date);

            vbox.getChildren().addAll(nameLabel, contactLabel, locationLabel, damageTypeLabel, descriptionLabel,
                    descriptionText, dateLabel);

            alert.getDialogPane().setContent(vbox);

            alert.showAndWait();
        } else {
            showAlert("Please select a report to see details.");
        }
    }

    // Metode untuk memasukkan paragraf baru di deskripsi yang panjang
    private String insertLineBreaks(String description) {
        int maxLineLength = 130;
        StringBuilder sb = new StringBuilder(description);
        int i = 0;
        while (i + maxLineLength < sb.length() && (i = sb.lastIndexOf(" ", i + maxLineLength)) != -1) {
            sb.replace(i, i + 1, "\n");
        }
        return sb.toString();
    }

    @FXML
    private void deleteReport() {
        Report selectedReport = reportTable.getSelectionModel().getSelectedItem();
        if (selectedReport != null) {
            reportTable.getItems().remove(selectedReport);
            deleteReportFromXML(selectedReport);
            showAlert("Report deleted successfully.");
        } else {
            showAlert("Please select a report to delete.");
        }
    }

    private void saveReportToXML(Report report) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc;
            Element rootElement;
            File xmlFile = new File(XML_FILE_PATH);

            if (xmlFile.exists()) {
                doc = docBuilder.parse(xmlFile);
                rootElement = doc.getDocumentElement();
            } else {
                doc = docBuilder.newDocument();
                rootElement = doc.createElement("reports");
                doc.appendChild(rootElement);
            }

            Element reportElement = doc.createElement("report");
            rootElement.appendChild(reportElement);

            addElement(doc, reportElement, "name", report.getName());
            addElement(doc, reportElement, "contact", report.getContact());
            addElement(doc, reportElement, "location", report.getLocation());
            addElement(doc, reportElement, "damageType", report.getDamageType());
            addElement(doc, reportElement, "description", report.getDescription());
            addElement(doc, reportElement, "date", report.getDate().toString());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error saving report: " + e.getMessage());
        }
    }

    private void deleteReportFromXML(Report report) {
        try {
            File xmlFile = new File(XML_FILE_PATH);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            NodeList reportList = doc.getElementsByTagName("report");
            for (int i = 0; i < reportList.getLength(); i++) {
                Element reportElement = (Element) reportList.item(i);
                if (reportElement.getElementsByTagName("name").item(0).getTextContent().equals(report.getName()) &&
                        reportElement.getElementsByTagName("date").item(0).getTextContent()
                                .equals(report.getDate().toString())) {
                    reportElement.getParentNode().removeChild(reportElement);
                    break;
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error deleting report: " + e.getMessage());
        }
    }

    private void addElement(Document doc, Element parent, String elementName, String text) {
        Element element = doc.createElement(elementName);
        element.appendChild(doc.createTextNode(text));
        parent.appendChild(element);
    }

    private void loadReportsFromXML() {
        try {
            File xmlFile = new File(XML_FILE_PATH);
            if (!xmlFile.exists() || xmlFile.length() == 0) {
                return;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList reportList = doc.getElementsByTagName("report");

            for (int i = 0; i < reportList.getLength(); i++) {
                Node reportNode = reportList.item(i);
                if (reportNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element reportElement = (Element) reportNode;
                    Report report = new Report(
                            getElementText(reportElement, "name"),
                            getElementText(reportElement, "contact"),
                            getElementText(reportElement, "location"),
                            getElementText(reportElement, "damageType"),
                            getElementText(reportElement, "description"));
                    report.setDate(LocalDate.parse(getElementText(reportElement, "date")));
                    if (reportTable != null) {
                        reportTable.getItems().add(report);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading reports: " + e.getMessage());
        }
    }

    private String getElementText(Element element, String tagName) {
        return element.getElementsByTagName(tagName).item(0).getTextContent();
    }

    private void clearInputFields() {
        if (nameField != null)
            nameField.clear();
        if (contactField != null)
            contactField.clear();
        if (locationField != null)
            locationField.clear();
        if (damageTypeField != null)
            damageTypeField.clear();
        if (descriptionArea != null)
            descriptionArea.clear();
        if (datePicker != null)
            datePicker.setValue(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}