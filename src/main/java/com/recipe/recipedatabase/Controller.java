package com.recipe.recipedatabase;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.function.UnaryOperator;

public class Controller {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox container;

    // keeps track of current input field
    private TextArea currentInputField;

    // the DatabaseManager to interact with database
    private DatabaseManager databaseManager;

    // home path of the application
    private static final String HOME = "/home/recipe> ";

    // styles for the label and input
    private static String style =
            "-fx-text-fill: #ffffff;" +
                    "-fx-font-size: 16px;" +
                    "-fx-control-inner-background: #000000;" +
                    "-fx-highlight-fill: #ADD8E6;" +
                    "-fx-border-style: none;" +
                    "-fx-border-width: 0px;" +
                    "-fx-border-insets: 0;" +
                    "-fx-background-color: -fx-control-inner-background;";


    @FXML
    public void initialize() {
        // create a new input element and add it to container
        this.currentInputField = createInputElement();
        container.getChildren().add(this.currentInputField);

        // bind the height of scroll pane for auto-scroll
        scrollPane.vvalueProperty().bind(container.heightProperty());

        // create database manager
        this.databaseManager = new DatabaseManager("recipe");
    }


    // creates a new input field
    private TextArea createInputElement() {
        TextArea inputField = new TextArea(HOME);
        inputField.setBackground(Background.fill(Paint.valueOf("black")));
        inputField.setWrapText(true);
        inputField.setStyle(style);
        inputField.setPrefWidth(680);
        inputField.setPrefHeight(50);
        inputField.requestFocus();
        inputField.end();
        addOnChangeListener(inputField);
        return inputField;
    }

    private void addOnChangeListener(TextArea textArea) {
        // HOME prefix should always be preserved
        textArea.setTextFormatter(new TextFormatter<String>(change -> {
            if (change.getCaretPosition() < HOME.length() || change.getAnchor() < HOME.length()) return null;
            return change;
        }));
        // set onKeyPress listener
        textArea.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                // get the query
                String query = this.currentInputField.getText().trim();
                // check if the command is empty
                if (query.isBlank())    return;
                // remove the input field and create a placeholder label
                container.getChildren().remove(this.currentInputField);
                Label inputLabel = new Label(this.currentInputField.getText());
                setLabelStyle(inputLabel);
                container.getChildren().add(inputLabel);
                // execute query
                executeQuery(query);
                // create a new input field and add it to container
                this.currentInputField = createInputElement();
                container.getChildren().add(this.currentInputField);
                this.currentInputField.requestFocus();
            }
        });
    }

    // executes the query and generates the UI
    private void executeQuery(String query) {
        // take away the prefix
        query = query.substring(HOME.length());
        // execute query and get result
        String output = databaseManager.executeQuery(query);
        Label outputLabel = new Label(output);
        setLabelStyle(outputLabel);
        container.getChildren().add(outputLabel);
    }

    // updates the common styles for display
    private void setLabelStyle(Label label) {
        label.setStyle(style);
    }
}