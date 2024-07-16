package fxml_helloworld;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class OpenScene {
    // Open Scene Satu
    public static void loadFXML(String fxml, Stage stage, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(FXMLDocumentController.class.getResource(fxml));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Open Scene Dua
    private Pane halaman;

    public Pane getPane(String fileName) {
        try {
            URL fileHalaman = MainProgram.class.getResource(fileName + ".fxml");

            if (fileHalaman == null) {
                throw new java.io.FileNotFoundException("Halaman Tidak ditemukan");
            }

            new FXMLLoader();
            halaman = FXMLLoader.load(fileHalaman);
        } catch (Exception e) {
            System.out.println("Tidak ditemukan halaman tersebut");
        }
        return halaman;
    }
}