package sample;

import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.IntStream;

import static javafx.collections.FXCollections.observableList;

public class Controller implements Initializable {

    private final static PseudoClass errorClass = PseudoClass.getPseudoClass("error");

    @FXML
    private Button run;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ChoiceBox transtype;
    @FXML
    private TextField input;
    @FXML
    private TextField output;

    private File ditaDir;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        ditaDir = new File(System.getProperty("dita.dir")).getAbsoluteFile();

        final List<String> list = getTranstypes();
        final ObservableList obList = observableList(list);
        transtype.getItems().clear();
        transtype.setItems(obList);
        transtype.valueProperty().addListener((observable, oldValue, newValue) -> {
            validate();
        });

        input.textProperty().addListener((observable, oldValue, newValue) -> {
            final boolean v = !new File(newValue).exists();
            input.pseudoClassStateChanged(errorClass, v);
            validate();
        });

        output.textProperty().addListener((observable, oldValue, newValue) -> {
            final boolean v = !new File(newValue).exists();
            output.pseudoClassStateChanged(errorClass, v);
            validate();
        });

        validate();
    }

    private List<String> getTranstypes() {
        final Document plugins;
        try (final InputStream in = getClass().getResourceAsStream("/plugins.xml")) {
            plugins = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

            final List<String> res = new ArrayList<>();
            final NodeList transtypes = plugins.getElementsByTagName("transtype");
            for (int i = 0; i < transtypes.getLength(); i++) {
                final Element transtype = (Element) transtypes.item(i);
                res.add(transtype.getAttribute("name"));
            }
            Collections.sort(res);

            return res;
        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException("Failed to read plugin configuration: " + e.getMessage(), e);
        }
    }

    private void validate() {
        boolean valid = true;
        if (valid) {
            valid = !input.getText().isEmpty();
        }
        if (valid) {
            valid = transtype.getValue() != null;
        }
        if (valid) {
            valid = !output.getText().isEmpty();
        }

        run.setDisable(!valid);
    }

    @FXML
    protected void run(ActionEvent event) {
        System.err.println("run " + transtype.getValue() + " for " + input.getText());
        final Runnable job = new Runnable() {
            @Override
            public void run() {
                final Processor p = new Processor(ditaDir, transtype.getValue().toString(), Collections.emptyMap());
                p.setInput(new File(input.getText()));
                p.setOutput(new File(output.getText()));

                run.setDisable(true);
                p.run();
                run.setDisable(false);
            }
        };

        new Thread(job).start();
    }

    @FXML
    protected void changeInput(ActionEvent event) {
        System.out.println("change");
    }

    @FXML
    protected void findInput(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Input file");
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter("DITA map", "ditamap"));
        final Window window = ((Node) event.getTarget()).getScene().getWindow();
        final File in = fileChooser.showOpenDialog(window);
        input.setText(in != null ? in.getAbsolutePath() : null);
    }

    @FXML
    protected void findOutput(ActionEvent event) {
        final DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Output directory");
        final Window window = ((Node) event.getTarget()).getScene().getWindow();
        final File out = dirChooser.showDialog(window);
        output.setText(out != null ? out.getAbsolutePath() : null);
    }
}
