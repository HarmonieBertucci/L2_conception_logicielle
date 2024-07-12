package application;

import application.controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import test.*;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ressources/OverlayEditor.fxml"));
        Controller controller = new Controller();
        Scene scene = new Scene(root, 640, 480);
        primaryStage.setTitle("> Editeur de livre dont vous êtes le héros <");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        boolean ok = true;
        TestLivre livreTest = new TestLivre();
        ok = ok && livreTest.addTest();
        ok = ok && livreTest.setTextTest();
        ok = ok && livreTest.deleteEtapeTest();
        ok = ok && livreTest.linkTest();
        ok = ok && livreTest.delinkTest();
        ok = ok && livreTest.firstTest();
        ok = ok && livreTest.lastTest();
        System.out.println(ok ? "All tests passed" : "At least one test failed");
        //si tous les tests ont été effectués avec succès "All tests passed" sera affiché en console
        launch(args);

    }

}
