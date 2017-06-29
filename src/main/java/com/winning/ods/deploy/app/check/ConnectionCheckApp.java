package com.winning.ods.deploy.app.check;

import com.winning.ods.deploy.domain.Database;
import com.winning.ods.deploy.util.SqlServer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.xml.bind.JAXB;
import java.io.File;

/**
 * Created by tlw@winning.com.cn on 2017/6/29.
 */
public class ConnectionCheckApp extends Application {

    public static void main(String[] args) throws ClassNotFoundException {
        ConnectionCheckApp.launch(args);
    }

    Label serverLabel = new Label("服务器:");
    TextField serverTextField = new TextField();
    Label instanceLabel = new Label("实例名: ");
    TextField instanceTextField = new TextField();
    Label portLabel = new Label("端口:");
    Spinner<Integer> portSpinner = new Spinner(new SpinnerValueFactory.IntegerSpinnerValueFactory(1433, Integer.MAX_VALUE));
    CheckBox portCheckBox = new CheckBox();
    Label userNameLabel = new Label("用户名: ");
    TextField userNameTextField = new TextField();
    Label passwordLabel = new Label("密码: ");
    PasswordField passwordField = new PasswordField();
    Button button = new Button("测通");
    Label connectionInfoLabel = new Label("连接字符串: ");
    TextArea connectionInfoTextArea = new TextArea();

    File serializeFile = new File("config/testConnect.xml");

    Database database = new Database();

    @Override
    public void start(Stage primaryStage) throws Exception {
        //initialize GUI
        serverTextField.setPromptText("IP地址");
        portSpinner.setDisable(true);
        connectionInfoTextArea.setEditable(false);
        connectionInfoTextArea.setWrapText(true);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5,5,5,5));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.add(serverLabel, 0, 0);
        gridPane.add(instanceLabel, 0, 1);
        gridPane.add(portLabel, 0, 2);
        gridPane.add(userNameLabel, 0, 3);
        gridPane.add(passwordLabel, 0, 4);
        gridPane.add(connectionInfoLabel, 0, 5);
        gridPane.add(serverTextField, 1, 0);
        gridPane.add(instanceTextField, 1, 1);
        gridPane.add(portSpinner, 1, 2);
        gridPane.add(userNameTextField, 1, 3);
        gridPane.add(passwordField, 1, 4);
        gridPane.add(connectionInfoTextArea, 1, 5);
        gridPane.add(button, 1, 6);
        gridPane.add(portCheckBox, 2, 2);

        GridPane.setColumnSpan(serverTextField, 2);
        GridPane.setColumnSpan(instanceTextField, 2);
        GridPane.setColumnSpan(userNameTextField, 2);
        GridPane.setColumnSpan(passwordField, 2);
        GridPane.setColumnSpan(connectionInfoTextArea, 2);
        GridPane.setValignment(connectionInfoLabel, VPos.TOP);
        Scene scene = new Scene(gridPane, 400, 300);

        //initialize data
        if(serializeFile.exists()) {
            database = JAXB.unmarshal(serializeFile, Database.class);
            uiRefresh();
        }

        //initialize event
        portCheckBox.setOnAction(actionEvent -> portSpinner.setDisable(!portCheckBox.isSelected()));
        button.setOnAction(actionEvent -> {
            connectionInfoTextArea.setText("");
            uiApply();
            new Thread(){
                public void run(){
                    String connectionString = database.getJdbcConnectionString();
                    String connectionInfo = connectionString + "\n\n\n";
                    try {
                        scene.setCursor(Cursor.WAIT);
                        SqlServer.testConnect(database);
                        connectionInfo += "连接成功!";
                        connectionInfoTextArea.setStyle("-fx-text-fill: blue");
                    } catch (Exception e) {
                        connectionInfo += "连接失败... \n";
                        connectionInfo += e.toString();
                        connectionInfoTextArea.setStyle("-fx-text-fill: red");
                    }finally{
                        scene.setCursor(Cursor.DEFAULT);
                    }
                    connectionInfoTextArea.setText(connectionInfo);
                }
            }.start();
        });
        primaryStage.setOnCloseRequest(windowEvent -> {
            uiApply();
            JAXB.marshal(database, serializeFile);
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void uiRefresh(){
        serverTextField.setText(database.getServer());
        userNameTextField.setText(database.getUserName());
        passwordField.setText(database.getPassword());
        instanceTextField.setText(database.getInstance());
        if(database.getPort() != null){
            portCheckBox.setSelected(true);
            portSpinner.setDisable(false);
            portSpinner.getEditor().setText(database.getPort().toString());
        }else{
            portCheckBox.setSelected(false);
            portSpinner.setDisable(true);
        }
    }

    public void uiApply(){
        database.setServer(serverTextField.getText());
        database.setUserName(userNameTextField.getText());
        database.setPassword(passwordField.getText());
        database.setInstance(instanceTextField.getText());
        if(portSpinner.isDisable()){
            database.setPort(null);
        }else{
            database.setPort(portSpinner.getValue());
        }
    }
}
