<?xml version="1.0"?>
<DTSConfiguration>
  <Configuration ConfiguredType="Property" Path="\Package.Connections[ADODestination].Properties[ConnectionString]" ValueType="String">
    <ConfiguredValue>${targetADOConnectionString}</ConfiguredValue>
  </Configuration>		<!-- 目标数据库 -->
  <Configuration ConfiguredType="Property" Path="\Package.Connections[ADOEtlWorklog].Properties[ConnectionString]" ValueType="String">
    <ConfiguredValue>${logADOConnectionString}</ConfiguredValue>
  </Configuration>		<!-- 日志数据库 -->
  <Configuration ConfiguredType="Property" Path="\Package.Connections[ADOSource].Properties[ConnectionString]" ValueType="String">
    <ConfiguredValue>${sourceADOConnectionString}</ConfiguredValue>
  </Configuration>		<!-- 源数据库 -->
  <Configuration ConfiguredType="Property" Path="\Package.Connections[ADOTempporary].Properties[ConnectionString]" ValueType="String">
    <ConfiguredValue>${tempADOConnectionString}</ConfiguredValue>
  </Configuration>		<!-- 暂存数据库 -->
  <Configuration ConfiguredType="Property" Path="\Package.Connections[OLEDBDestination].Properties[ConnectionString]" ValueType="String">
    <ConfiguredValue>${targetOLEDBConnectionString}</ConfiguredValue>
  </Configuration>		<!-- 目标数据库 -->
  <Configuration ConfiguredType="Property" Path="\Package.Connections[OLEDBETLWorklog].Properties[ConnectionString]" ValueType="String">
    <ConfiguredValue>${logOLEDBConnectionString}</ConfiguredValue>
  </Configuration>		<!-- 日志数据库 -->
  <Configuration ConfiguredType="Property" Path="\Package.Connections[OLEDBSource].Properties[ConnectionString]" ValueType="String">
    <ConfiguredValue>${sourceOLEDBConnectionString}</ConfiguredValue>
  </Configuration>		<!-- 源数据库 -->
  <Configuration ConfiguredType="Property" Path="\Package.Connections[OLEDBTemporary].Properties[ConnectionString]" ValueType="String">
    <ConfiguredValue>${tempOLEDBConnectionString}</ConfiguredValue>
  </Configuration>		<!-- 暂存数据库 -->
  <Configuration ConfiguredType="Property" Path="\Package.Variables[User::dataflag].Properties[Value]" ValueType="String">
    <ConfiguredValue>1</ConfiguredValue>    
  </Configuration>		<!-- CDR专属字段 -->
  <Configuration ConfiguredType="Property" Path="\Package.Variables[User::destinationname].Properties[Value]" ValueType="String">
    <ConfiguredValue>ODS</ConfiguredValue> 
  </Configuration>		<!-- 目标名称标识，如ODS/CDR -->
  <Configuration ConfiguredType="Property" Path="\Package.Variables[User::jobname].Properties[Value]" ValueType="String">
    <ConfiguredValue>ODS_${bizName}</ConfiguredValue> 
  </Configuration>		<!-- 作业名称 -->
  <Configuration ConfiguredType="Property" Path="\Package.Variables[User::orgcode].Properties[Value]" ValueType="String">
    <ConfiguredValue>${orgCode}</ConfiguredValue> 
  </Configuration>		<!-- 医疗机构代码 -->
  <Configuration ConfiguredType="Property" Path="\Package.Variables[User::sourcename].Properties[Value]" ValueType="String">
    <ConfiguredValue>${bizName}</ConfiguredValue> 
  </Configuration>		<!-- 数据源来源标识 -->
</DTSConfiguration>