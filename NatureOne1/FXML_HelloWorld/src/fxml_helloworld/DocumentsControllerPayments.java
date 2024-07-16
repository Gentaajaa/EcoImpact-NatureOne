package fxml_helloworld;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.*;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DocumentsControllerPayments implements Initializable {

    @FXML
    private Label labelNameProject;
    @FXML
    private TextField amountCardTfield, cityCardTfield, countryCardTfield, cvvCardTfield, nameCardTfield,
            numberCardTfield, postalCardTfield, stateCardTfield, streetCardTfield, firstNameTfield, lastNameTfield;
    @FXML
    private ComboBox<String> monthCardComBox;
    @FXML
    private Button sumbitPayments;
    @FXML
    private Spinner<Integer> yearCardSpinner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        monthCardComBox.getItems().addAll("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
        yearCardSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2020, 2050));
        sumbitPayments.setOnAction(event -> handlePengirimanPembayaran());
        amountCardTfield.setTextFormatter(createDonationFormatter());
    }

    public void setProject(Project project) {
        if (project != null)
            labelNameProject.setText(project.getNameProject());
    }

    private void handlePengirimanPembayaran() {
        if (!validasiInput())
            return;

        PaymentData paymentData = getPaymentDataFromFields();
        if (prosesPembayaran(paymentData)) {
            simpanDataKeXML(paymentData);
            tampilkanPesanSukses();
            resetForm();
        } else {
            tampilkanPesanError("Pembayaran gagal. Silakan coba lagi.");
        }
    }

    private PaymentData getPaymentDataFromFields() {
        return new PaymentData(
                String.valueOf(parseRupiah(amountCardTfield.getText())),
                nameCardTfield.getText(),
                numberCardTfield.getText(),
                cvvCardTfield.getText(),
                monthCardComBox.getValue(),
                yearCardSpinner.getValue().toString(),
                streetCardTfield.getText(),
                cityCardTfield.getText(),
                stateCardTfield.getText(),
                postalCardTfield.getText(),
                countryCardTfield.getText());
    }

    private boolean validasiInput() {
        if (amountCardTfield.getText().isEmpty() || nameCardTfield.getText().isEmpty()
                || numberCardTfield.getText().isEmpty() || firstNameTfield.getText().isEmpty()) {
            tampilkanPesanError("Harap isi semua field yang diperlukan.");
            return false;
        }
        return true;
    }

    private boolean prosesPembayaran(PaymentData paymentData) {
        // Implementasi logika pemrosesan pembayaran di sini
        return true; // Anggap pembayaran selalu berhasil
    }

    private void simpanDataKeXML(PaymentData paymentData) {
        try {
            File file = new File("dataPayment.xml");
            Document doc;
            Element rootElement;

            if (file.exists()) {
                doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
                rootElement = doc.getDocumentElement();
            } else {
                doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                rootElement = doc.createElement("pembayaran-list");
                doc.appendChild(rootElement);
            }

            Element pembayaranElement = doc.createElement("pembayaran");
            rootElement.appendChild(pembayaranElement);

            tambahElemen(doc, pembayaranElement, "namaDepan", firstNameTfield.getText());
            tambahElemen(doc, pembayaranElement, "namaBelakang", lastNameTfield.getText());
            tambahElemen(doc, pembayaranElement, "jumlah", paymentData.getAmount());
            tambahElemen(doc, pembayaranElement, "tanggalTransaksi",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(file));

        } catch (Exception e) {
            e.printStackTrace();
            tampilkanPesanError("Gagal menyimpan data pembayaran.");
        }
    }

    private void tambahElemen(Document doc, Element parent, String name, String value) {
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(value));
        parent.appendChild(element);
    }

    private void tampilkanPesanSukses() {
        tampilkanAlert(Alert.AlertType.INFORMATION, "Pembayaran Berhasil",
                "Pembayaran Anda telah berhasil diproses dan disimpan.");
    }

    private void tampilkanPesanError(String message) {
        tampilkanAlert(Alert.AlertType.ERROR, "Error", message);
    }

    private void tampilkanAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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

    private String formatRupiahInput(long amount) {
        return String.format("%,d", amount).replace(",", ".");
    }

    private long parseRupiah(String rupiahString) {
        return Long.parseLong(rupiahString.replaceAll("[^\\d]", ""));
    }

    private void resetForm() {
        Arrays.asList(amountCardTfield, cityCardTfield, countryCardTfield, cvvCardTfield, nameCardTfield,
                numberCardTfield, postalCardTfield, stateCardTfield, streetCardTfield, firstNameTfield, lastNameTfield)
                .forEach(TextField::clear);
        amountCardTfield.setText(formatRupiahInput(0));
        monthCardComBox.getSelectionModel().clearSelection();
        yearCardSpinner.getValueFactory().setValue(2024);
    }
}