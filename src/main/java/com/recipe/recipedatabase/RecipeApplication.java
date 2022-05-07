package com.recipe.recipedatabase;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class RecipeApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RecipeApplication.class.getResource("recipe-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500, Color.BLACK);
        stage.setTitle("Recipe database!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}