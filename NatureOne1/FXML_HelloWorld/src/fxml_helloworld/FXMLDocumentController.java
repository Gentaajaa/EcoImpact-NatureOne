package fxml_helloworld;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLDocumentController implements Initializable {

    @FXML
    private Button btnCreateAcc, btnLogin, btnLoginAcc, btnSignUp;

    @FXML
    private PasswordField passLogin, passRegister;

    @FXML
    private TextField userNameLogin, emailRegister, phoneRegister, userNameRegister;

    @FXML
    private ChoiceBox<String> userChoice;

    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Controller Untuk Login...
    @FXML
    private void adminLogin(ActionEvent event) {
        try {
            if (userNameLogin.getText().isEmpty() || passLogin.getText().isEmpty()) {
                showAlert(AlertType.ERROR, "Login Gagal", "Error", "Username atau Password Tidak Boleh Kosong");
                return;
            }

            XStream xStream = new XStream(new StaxDriver());
            xStream.allowTypes(new Class[] { User.class });

            User loadedUser = null;
            String userType = "";
            String[] fileNames = { "dataMember.xml", "dataOrganization.xml" };

            for (String fileName : fileNames) {
                try {
                    File file = new File(fileName);
                    if (file.exists()) {
                        loadedUser = (User) xStream.fromXML(file);
                        if (loadedUser != null && userNameLogin.getText().equals(loadedUser.getUsername())
                                && passLogin.getText().equals(loadedUser.getPassword())) {
                            userType = fileName.contains("Member") ? "Members" : "Organization";
                            break;
                        }
                    } else {
                        System.out.println("File not found: " + fileName);
                    }
                } catch (Exception e) {
                    System.err.println("Error reading file " + fileName + ": " + e.getMessage());
                }
            }

            if (loadedUser != null && userNameLogin.getText().equals(loadedUser.getUsername())
                    && passLogin.getText().equals(loadedUser.getPassword())) {
                showAlert(AlertType.INFORMATION, null, "Login Berhasil", "Login Berhasil!!");
                String fxmlFile = userType.equals("Members") ? "Members.fxml" : "Organization.fxml";
                try {
                    OpenScene.loadFXML(fxmlFile, (Stage) ((Node) event.getSource()).getScene().getWindow(), event);
                } catch (IOException e) {
                    showAlert(AlertType.ERROR, "Error", "Gagal Membuka Halaman", "Tidak dapat membuka " + fxmlFile);
                    e.printStackTrace();
                }
            } else {
                showAlert(AlertType.ERROR, "Login Gagal", "Error", "Username atau Password Salah");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Terjadi Kesalahan", "Detail: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Selesai...

    // Controller Untuk Pindah Ke Register
    @FXML
    private void goToRegister(ActionEvent event) throws IOException {
        OpenScene.loadFXML("Register.fxml", (Stage) ((Node) event.getSource()).getScene().getWindow(), event);
    }
    // Selesai...

    // Controller Untuk Pindah Ke Login...
    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        OpenScene.loadFXML("Login.fxml", (Stage) ((Node) event.getSource()).getScene().getWindow(), event);
    }
    // Selesai...

    // Controller Untuk Registrasi...
    @FXML
    private void registerUser(ActionEvent event) {
        try {
            if (userNameRegister.getText().isEmpty() || passRegister.getText().isEmpty() ||
                    emailRegister.getText().isEmpty() || phoneRegister.getText().isEmpty() ||
                    userChoice.getValue() == null) {
                showAlert(AlertType.ERROR, "Registrasi Gagal", "Error", "Semua Data Harus di Isi");
                return;
            }

            User newUser = new User(userNameRegister.getText(), passRegister.getText(),
                    emailRegister.getText(), phoneRegister.getText(), userChoice.getValue());

            XStream xstream = new XStream(new StaxDriver());
            xstream.allowTypes(new Class[] { User.class });

            String xml = xstream.toXML(newUser);
            String filePath = userChoice.getValue().equals("Members") ? "dataMember.xml" : "dataOrganization.xml";

            try (FileOutputStream output = new FileOutputStream(filePath)) {
                output.write(xml.getBytes());
                showAlert(AlertType.INFORMATION, "Registrasi Berhasil", null, "Registrasi berhasil. Silakan login.");
                OpenScene.loadFXML("Login.fxml", (Stage) ((Node) event.getSource()).getScene().getWindow(), event);
            } catch (IOException e) {
                showAlert(AlertType.ERROR, "Error", "Gagal Menyimpan Data", "Detail: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Terjadi Kesalahan", "Detail: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Selesai...

    // Controller Untuk Tutup Aplikasi...
    @FXML
    private void closeButton() {
        System.exit(0);
    }
    // Selesai...

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (userChoice != null) {
            userChoice.getItems().addAll("Members", "Organization");
        }

    }
}