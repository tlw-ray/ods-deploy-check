package com.winning.ods.deploy.domain;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.XmlType;

/**
 * Created by tlw@winning.com.cn on 2017/6/13.
 */
@XmlType
public class Database {

    public static final String TABLE = "CM_DB_CONFIG";
    public static final String FIELD_SERVER = "serveraddress";
    public static final String FIELD_PORT = "portnumber";
    public static final String FIELD_USER_NAME = "username";
    public static final String FIELD_PASSWORD = "pwd";
    public static final String FIELD_NAME = "code";
    public static final String FIELD_BIZ_CODE = "dbname";
    public static final String FIELD_INSTANCE = "examplename";

    protected String server;
    protected Integer port;
    protected String userName;
    protected String password;
    protected String instance;
    protected String name;

    public String getJdbcConnectionString(){
        //https://docs.microsoft.com/en-us/sql/connect/jdbc/building-the-connection-url
        //Examples:
        //jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]
        //jdbc:sqlserver://localhost;user=MyUserName;password=*****;
        //jdbc:sqlserver://;servername=server_name;integratedSecurity=true;authenticationScheme=JavaKerberos
        StringBuilder builder = new StringBuilder("jdbc:sqlserver://");
        builder.append(getServer());
        if(StringUtils.isNotEmpty(instance)){
            builder.append("\\");
            builder.append(instance);
        }
        if(getPort() != null){
            builder.append(":");
            builder.append(getPort());
        }

        if(StringUtils.isNotEmpty(getName())){
            builder.append(";");
            builder.append("databaseName=");
            builder.append(getName());
        }

        if(StringUtils.isNotEmpty(getUserName())){
            builder.append(";");
            builder.append("user=");
            builder.append(getUserName());
        }

        if(StringUtils.isNotEmpty(getPassword())){
            builder.append(";");
            builder.append("password=");
            builder.append(getPassword());
        }

        String result = builder.toString();
        return result;
    }

    public String getADOConnectionString(){
        //Data Source=${server}/${instance}
        // :${port?string("######")}
        // ;User ID=${userName}
        // ;password=${password}
        // ;Initial Catalog=${initialCatalog}
        // ;Persist Security Info=True;
        StringBuilder stringBuilder = new StringBuilder("Data Source=");
        stringBuilder.append(getServer());

        if(StringUtils.isNotEmpty(getInstance())){
            stringBuilder.append("\\");
            stringBuilder.append(instance);
        }

        if(getPort() != null){
            stringBuilder.append(":");
            stringBuilder.append(getPort());
        }

        if(StringUtils.isNotEmpty(getUserName())){
            stringBuilder.append(";User ID=");
            stringBuilder.append(getUserName());
        }

        if(StringUtils.isNotEmpty(getPassword())){
            stringBuilder.append(";password=");
            stringBuilder.append(getPassword());
        }

        stringBuilder.append(";Persist Security Info=True;");

        if(StringUtils.isNotEmpty(getName())){
            stringBuilder.append("Initial Catalog=");
            stringBuilder.append(getName());
            stringBuilder.append(";");
        }

        return stringBuilder.toString();
    }

    public String getOLEDBConnectionString(){
        return getADOConnectionString() + "Provider=SQLNCLI10.1;";
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Database{" +
                "server='" + server + '\'' +
                ", port=" + port +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", instance='" + instance + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
