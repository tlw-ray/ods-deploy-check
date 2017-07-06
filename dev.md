# 开发文档

撰写: tlw@winning.com.cn

时间: 20170703

密级: 内部

代码仓库: https://github.com/tlw-ray/ods-deploy-check

## 功能1. 生成配置文件业务逻辑

- 生成规则:
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
    - 能够生成医疗机构代码

## 功能2. 字段冲突检查

将某数据库导入，结构被定义的库时需要检查字段是否兼容：

1. 字段未定义错误: 该数据库使用了未定义的字段。

2. 字段类型冲突错误: 该数据库使用了与定义类型冲突的字段。

3. 字段长度不兼容错误： 该数据库使用了类型兼容，但长度无法导入的字段。

4. 字段缺失警告：已定义的字段在该数据库中不存在。

## 功能3. DTSX调包

1. 长度重构

	1. 批量修改DTSX文件中<outputColumn>和<externalMetadataColumn>标签中对字段长度的定义

	2. 如果有对DTSX文件中内容进行成功修改，则修改ODS表中字段长度

2. NullAs重构

	1. 批量修改DTSX文件中componentClassID为{BCEFE59B-6819-47F7-A125-63753B33ABB7}的<component>标签中，SQL语句使用到的未定义字段改为NULL AS fieldName模式
	
	2. 批量修改DTSX文件中<DTS:Property DTS:Name="Expression">的标签中，SQL语句使用到的未定义字段改为NULL AS fieldName模式

## 检查与调包主流程

下图提供主要的流程，目前已有程序都是该流程的一部分
![](diagram\ODS字段检测.png)

## 代码目录说明

- com.winning.ods.deploy ODS部署改进程序
    - dao 数据访问模块
        - Repository    通用访问功能（取INFORMATION_SCHEMA数据)
        - EtlRepository ETL管理库访问功能，获取要抽取的数据库、表、时间戳字段、医疗机构代码等
    - domain 领域模型
        - BizDatabase   业务库: 包含业务系统名和实体数据库两部分
        - Database      数据库: 对应实际的数据库，包含服务器、用户名、密码、实例名等属性
        - Field         字段: 对应数据库中的Column概念，包含字段名、类型、最大宽度等属性
        - FieldCheckResult 字段检查结果: 对源库目标库字段进行检查后的结果
        - OrganizationCode 医疗机构代码:
        - Table         表: 对应实际的表，包含表名、描述等信息
    - util 工具类
        - DBTablePrinter 表输出工具: 将JDBC的ResultSet以方便阅读的方式输出
        - SqlServer      SQLServer工具: 测通等
        - SqlUtil        SQL语句工具: 从集合类生成where in的条件等。
    - app 应用程序
        - check 检查程序
            - core 核心功能
                - FieldChecker  检查字段的差异
                - TimeTempFieldCheck TimeTemp字段缺失检查
            - service 服务功能
                - ChecksService 对若干种检查的包装类
                - FieldLengthCheckService 字段长度检查服务，能够连接具体的库并根据检查结果输出日志
                - TimeTempFieldCheckService TimeTemp字段缺失检查服务，能够连接到具体的库并根据检查结果输出日志
            - ConnectCheckApp 连接检查App： 带有界面，能够让用户输入连接信息并检测连接成功状态
            - FieldCheckMain 字段检查Main函数: 检查ETL管理库中配置的业务库字段缺失、类型冲突、长度冲突并生成报告
        - dtsx  DTSX调包程序
            - core 核心功能
                - FileRefactor 文件重构: 从一个文件进行某些操作，删除该文件，将新的文件写入
                - FieldLengthRefactor 字段长度重构: 将DTSX文件内容中指定字段的长度改为目标长度
                - FieldNullAsRefactor 字段NullAs: 将DTSX文件内容中制定字段的Select语句改为 Null as fieldName的形式
                - FieldLengthAlter 改表SQL语句生成: 生成该字段长度的SQL语句，并执行
                - TableFileMapping 表名导文件映射: 扫描当前路径，建立单个表名到多个文件名的映射规则
            - service 服务
                - FieldLengthRefactorService 字段长度重构服务: 根据实际的库进行字段长度重构，并输出日志
            - tool 工具
                - FieldLengthRefactorApp 字段长度重构工具: 界面输入要重构的表名、字段名、类型、目标长度，点击重构按钮执行重构
                - FieldNullAsRefactorApp 字段NullAs重构工具: 界面输入要重构的表名和字段名，将DTSX文件中有关SELECT语句中该字段改为NULL AS fieldName模式
            - GenerateConfigMain    配置文件生成: 根据ETL管理库中业务库的配置以及配置文件模板批量生成配置文件
            - RefactorFieldLengthMain 字段长度重构: 遍历ETL管理库中定义的所有业务库，检查字段长度冲突，并自动修正DTSX和目标库

- com.winning.javafx.*  界面工具中整数选择器需要按回车来使输入的数据生效，改为输入完毕后立即生效
- com.winning.logback.* 日志采用HTML格式时乱码问题的修正