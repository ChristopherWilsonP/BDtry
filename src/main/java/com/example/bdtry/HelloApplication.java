package com.example.bdtry;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    private static HelloApplication applicationInstance;
    private static Stage primaryStage;

    public static HelloApplication getApplicationInstance() {return applicationInstance;}

    public static Stage getPrimaryStage() {return primaryStage;}

    @Override
    public void start(Stage stage) throws IOException {
        HelloApplication.applicationInstance = this;
        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
