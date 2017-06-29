package com.winning.ods.deploy.app.dtsx.tool;

import com.winning.javafx.SpinnerAutoCommit;
import com.winning.ods.deploy.app.dtsx.core.RefactorFieldLength;
import com.winning.ods.deploy.app.dtsx.core.TableFileMapping;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.logging.LogManager;

/**
 * Created by tlw@winning.com.cn on 2017/6/22.
 */
public class RefactorFieldLengthApp extends Application{

    public static void main(String[] args) throws IOException {
        RefactorFieldLengthApp.launch(args);
    }

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void start(Stage primaryStage) throws Exception {

        final TableFileMapping tableFileMapping = new TableFileMapping();
        tableFileMapping.init();

        Label tableNameLabel = new Label("表名: ");
        tableNameLabel.setTextAlignment(TextAlignment.RIGHT);
        TextField tableNameTextField = new TextField("VW_MZBRJSK");

        Label fieldNameLabel = new Label("字段名: ");
        TextField fieldNameTextField = new TextField("khyh");

        Label fieldTypeLabel = new Label("字段类型: ");
        ComboBox<String> fieldTypeComboBox = new ComboBox();
        fieldTypeComboBox.setMaxWidth(Double.MAX_VALUE);
        fieldTypeComboBox.getItems().addAll("char", "varchar", "nvarchar");
        fieldTypeComboBox.getSelectionModel().select(1);

        Label fieldLengthLabel = new Label("长度改为: ");
        fieldLengthLabel.setTextFill(Color.DARKBLUE);
        Spinner<Integer> fieldLengthSpinner = new SpinnerAutoCommit();
        fieldLengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 64, 1));
        fieldLengthSpinner.setEditable(true);

        Button refactorButton = new Button("批量修改");
        refactorButton.setOnAction( actionEvent -> {
            String tableName = tableNameTextField.getText();
            String fieldName = fieldNameTextField.getText();
            Integer targetLength = fieldLengthSpinner.getValue();
            Set<Path> pathSet = tableFileMapping.getPathSet(tableName);
            if (pathSet != null && pathSet.size() > 0) {
                pathSet.forEach(path -> {
                    try {
                        ST msgST = new ST("修改'<path>': 更新'<field>'的<type>长度为'<length>'");
                        msgST.add("path", path.toString());
                        msgST.add("type", fieldTypeComboBox.getSelectionModel().getSelectedItem());
                        msgST.add("field", fieldName);
                        msgST.add("length", targetLength);
                        String msg = msgST.render();
                        System.out.println(msg);
                        byte[] bytes = Files.readAllBytes(path);
                        String sourceContent = new String(bytes, "UTF-8");
                        RefactorFieldLength refactorFieldLength = new RefactorFieldLength();
                        refactorFieldLength.setContent(sourceContent);
                        refactorFieldLength.setFieldName(fieldName);
                        refactorFieldLength.setDataType(fieldTypeComboBox.getSelectionModel().getSelectedItem());
                        refactorFieldLength.setTargetLength(targetLength);
                        String targetContent = refactorFieldLength.process();
                        Files.write(path, targetContent.getBytes("UTF-8"), StandardOpenOption.WRITE);
                    } catch (IOException e) {
                        ST warnST = new ST("将文件'<path>'中的字段'<odsFieldName>'长度替换为'<bizFieldLength>'时发生异常。");
                        warnST.add("path", path.toString());
                        warnST.add("odsFieldName", fieldName);
                        warnST.add("bizFieldLength", targetLength);
                        String warn = warnST.render();
                        logger.warn(warn, e);
                    }
                });
            }else{
                logger.warn("当前路径下所有目录中没有名为'"+tableNameTextField.getText()+".dtsx'的文件.");
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
        gridPane.add(fieldTypeLabel,0, 2);
        gridPane.add(fieldLengthLabel, 0, 3);
        gridPane.add(tableNameTextField, 1, 0);
        gridPane.add(fieldNameTextField, 1, 1);
        gridPane.add(fieldTypeComboBox, 1, 2);
        gridPane.add(fieldLengthSpinner, 1, 3);
        gridPane.add(refactorButton, 1, 4);

        gridPane.getColumnConstraints().addAll(column0Constraints, column1Constraints, column2Constraints);

        Scene scene = new Scene(gridPane, 300, 150);

        primaryStage.setScene(scene);
        primaryStage.setTitle("修改DTSX内字段长度");
        primaryStage.show();
    }
}
