package fxml_helloworld;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

public class DocumentsControllerArticle implements Initializable {

    @FXML
    private ScrollPane contentSelectionPane;

    @FXML
    private StackPane mainArticlePane;

    @FXML
    private ToggleButton btnArticleSatu, btnArticleDua, btnArticleTiga, btnArticleEmpat;

    @FXML
    private ScrollPane kontenPaneSatu, kontenPaneDua, kontenPaneTiga, kontenPaneEmpat;

    @FXML
    private void handleToggleButton(ActionEvent event) {
        ToggleButton source = (ToggleButton) event.getSource();
        String contentId = (String) source.getUserData();

        // Sembunyikan semua konten
        kontenPaneSatu.setVisible(false);
        kontenPaneDua.setVisible(false);
        kontenPaneTiga.setVisible(false);

        // Tampilkan konten yang dipilih jika tombol aktif
        if (source.isSelected()) {
            switch (contentId) {
                case "konten1":
                    kontenPaneSatu.setVisible(true);
                    kontenPaneSatu.setVvalue(0);
                    break;
                case "konten2":
                    kontenPaneDua.setVisible(true);
                    kontenPaneDua.setVvalue(0);
                    break;
                case "konten3":
                    kontenPaneTiga.setVisible(true);
                    kontenPaneTiga.setVvalue(0);
                    break;
                case "konten4":
                    kontenPaneEmpat.setVisible(true);
                    kontenPaneEmpat.setVvalue(0);
                    break;
                default:
                    System.out.println("Unknown content ID: " + contentId);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Sembunyikan semua konten pada awalnya
        kontenPaneSatu.setVisible(false);
        kontenPaneDua.setVisible(false);
        kontenPaneTiga.setVisible(false);
        kontenPaneEmpat.setVisible(false);

        // Set userData untuk masing-masing tombol
        btnArticleSatu.setUserData("konten1");
        btnArticleDua.setUserData("konten2");
        btnArticleTiga.setUserData("konten3");
        btnArticleEmpat.setUserData("konten4");
    }
}