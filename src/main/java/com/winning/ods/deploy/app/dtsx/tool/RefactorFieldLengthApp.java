package com.winning.ods.deploy.app.dtsx.tool;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Created by tlw@winning.com.cn on 2017/6/22.
 */
public class RefactorFieldLengthFrame extends Application{

    public static void main(String[] args){
        RefactorFieldLengthFrame.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Label tableNameLabel = new Label("表名: ");
        tableNameLabel.setTextAlignment(TextAlignment.RIGHT);
        TextField tableNameTextField = new TextField();

        Label fieldNameLabel = new Label("字段名: ");
        TextField fieldNameTextField = new TextField();
        Label typeLabel = new Label();

        Label fieldLengthLabel = new Label("字段长度(新): ");
        Spinner<Integer> fieldLengthSpinner = new Spinner();

        Button refactorButton = new Button("重构");

        GridPane gridPane = new GridPane();

        ColumnConstraints column0Constraints = new ColumnConstraints();
        column0Constraints.setHalignment(HPos.RIGHT);
        
        ColumnConstraints column1Constraints = new ColumnConstraints();
        column1Constraints.setHgrow(Priority.ALWAYS);
        column1Constraints.setFillWidth(true);

        ColumnConstraints column2Constraints = new ColumnConstraints();
        column2Constraints.setHalignment(HPos.RIGHT);

        gridPane.add(tableNameLabel, 0, 0);
        gridPane.add(fieldNameLabel, 0, 1);
        gridPane.add(fieldLengthLabel, 0, 2);
        gridPane.add(tableNameTextField, 1, 0);
        gridPane.add(fieldNameTextField, 1, 1);
        gridPane.add(fieldLengthSpinner, 1, 2);
        gridPane.add(typeLabel, 2, 1);
        gridPane.add(refactorButton, 2, 2);
        
        gridPane.getColumnConstraints().addAll(column0Constraints, column1Constraints, column2Constraints);

        Scene scene = new Scene(gridPane);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
