package sm.tools.rctl.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.core.io.ClassPathResource;

public class MainUI extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(new ClassPathResource("jfxui/form-main.fxml").getURL());

        primaryStage.setTitle("Remote Control");
        primaryStage.setScene(new Scene(parent, 300, 200));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
