package com.winning.ods.deploy.app.dtsx.tool;

import com.winning.ods.deploy.app.dtsx.core.FieldNullAsRefactor;
import com.winning.ods.deploy.app.dtsx.core.TableFileMapping;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by tlw@winning.com.cn on 2017/6/23.
 */
public class FieldNullAsRefactorApp extends Application{

    public static void main(String[] args) throws IOException {
        FieldLengthRefactorApp.launch(args);
    }

    Logger logger = LoggerFactory.getLogger(getClass());

    public void start(Stage primaryStage) throws Exception {

        final TableFileMapping tableFileMapping = new TableFileMapping();
        tableFileMapping.init();

        Label tableNameLabel = new Label("表名: ");
        tableNameLabel.setTextAlignment(TextAlignment.RIGHT);
        TextField tableNameTextField = new TextField("VW_MZBRJSK");

        Label fieldNameLabel = new Label("字段名: ");
        TextField fieldNameTextField = new TextField("khyh");

        Button refactorButton = new Button("批量NullAs");
        refactorButton.setOnAction( actionEvent -> {
            String tableName = tableNameTextField.getText();
            String fieldName = fieldNameTextField.getText();
            Set<Path> pathSet = tableFileMapping.getPathSet(tableName);
            if (pathSet != null && pathSet.size() > 0) {
                pathSet.forEach(path -> {
                    logger.info("修改文件'{}'中'{}'表的'{}'字段在SELECT语句中为'NULL AS {}'", path.toString(), tableName, fieldName, fieldName);
                    Set<String> fieldNameSet = new HashSet();
                    fieldNameSet.add(fieldName);
                    FieldNullAsRefactor fieldNullAsRefactor = new FieldNullAsRefactor();
                    fieldNullAsRefactor.setTableName(tableName);
                    fieldNullAsRefactor.setFieldNameSet(fieldNameSet);
                    fieldNullAsRefactor.setSourcePath(path);
                    fieldNullAsRefactor.setTargetPath(path);
                    fieldNullAsRefactor.process();
                });
            }else{
                logger.warn("当前路径下所有目录中没有名为'" + tableNameTextField.getText() + ".dtsx'的文件.");
            }
        });

        ColumnConstraints column0Constraints = new ColumnConstraints();
        column0Constraints.setHalignment(HPos.RIGHT);

        ColumnConstraints column1Constraints = new ColumnConstraints();
        column1Constraints.setHgrow(Priority.ALWAYS);
        column1Constraints.setFillWidth(true);

        ColumnConstraints column2Constraints = new ColumnConstraints();
        column2Constraints.setHalignment(HPos.RIGHT);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5,5,5,5));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.add(tableNameLabel, 0, 0);
        gridPane.add(fieldNameLabel, 0, 1);
        gridPane.add(tableNameTextField, 1, 0);
        gridPane.add(fieldNameTextField, 1, 1);
        gridPane.add(refactorButton, 1, 4);

        gridPane.getColumnConstraints().addAll(column0Constraints, column1Constraints, column2Constraints);

        Scene scene = new Scene(gridPane, 300, 120);

        primaryStage.setScene(scene);
        primaryStage.setTitle("替换DTSX内SELECT字段为NULL AS field");
        primaryStage.show();
    }

}
