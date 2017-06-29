## 生成配置文件业务逻辑

- 目前的生成规则:
    - 模板文件位置: "template/ODSConfig_.dtsConfig.ftlh"
    - 目标文件名为: "ConfigFile/ODS/ODSConfig_${业务系统名}.dtsConfig"
    - 医疗机构代码从数据库获取
    - 连接分为ADO和OLEDB两类
    - 每类有四个连接:
        - 目标库： ODS库
        - 日志库： ETL管理实例的CM_DataCenter库
        - 源库： 业务系统库
        - 临时库: ETL管理实例的CM_Temporary库
    - 生成前会先清空ConfigFile/ODS
    - 有配置多少业务库就生成几个配置文件