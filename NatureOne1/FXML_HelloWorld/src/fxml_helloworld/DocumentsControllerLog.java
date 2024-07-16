package fxml_helloworld;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DocumentsControllerLog implements Initializable {

    @FXML
    private Button btnDeleteDonation, btnGraph;

    @FXML
    private TableColumn<DonationEntry, String> dateTransaction;

    @FXML
    private TableColumn<DonationEntry, String> donorName;

    @FXML
    private TableColumn<DonationEntry, Double> fundsIncoming;

    @FXML
    private TableView<DonationEntry> transactionTable;

    @FXML
    private LineChart<String, Number> chartDonation;

    private ObservableList<DonationEntry> donationList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (transactionTable != null && donorName != null && fundsIncoming != null && dateTransaction != null) {
            initializeTable();
            loadDataFromXML();

            if (btnGraph != null) {
                btnGraph.setOnAction(event -> updateChart());
            }
            if (btnDeleteDonation != null) {
                btnDeleteDonation.setOnAction(event -> deleteAllData());
            }
        }
    }

    private void initializeTable() {
        if (donorName != null && fundsIncoming != null && dateTransaction != null) {
            donorName.setCellValueFactory(new PropertyValueFactory<>("donorName"));
            fundsIncoming.setCellValueFactory(new PropertyValueFactory<>("fundsIncoming"));
            dateTransaction.setCellValueFactory(new PropertyValueFactory<>("dateTransaction"));

            if (transactionTable != null) {
                transactionTable.setItems(donationList);
            } else {
                System.err.println("Error: transactionTable is null");
            }
        } else {
            System.err.println("Error: One or more table columns are null");
        }
    }

    private void loadDataFromXML() {
        try {
            File file = new File("dataPayment.xml");
            if (!file.exists())
                return;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("pembayaran");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Element eElement = (Element) nList.item(temp);
                String name = eElement.getElementsByTagName("namaDepan").item(0).getTextContent();
                double amount = Double.parseDouble(eElement.getElementsByTagName("jumlah").item(0).getTextContent());
                String date = eElement.getElementsByTagName("tanggalTransaksi").item(0).getTextContent();

                donationList.add(new DonationEntry(name, amount, date));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Donasi");

        for (DonationEntry entry : donationList) {
            series.getData().add(new XYChart.Data<>(entry.getDateTransaction(), entry.getFundsIncoming()));
        }

        chartDonation.getData().clear();
        chartDonation.getData().add(series);
    }

    @FXML
    private void deleteAllData() {
        donationList.clear();
        chartDonation.getData().clear();

        File file = new File("dataPayment.xml");
        if (file.exists()) {
            file.delete();
        }
    }
}