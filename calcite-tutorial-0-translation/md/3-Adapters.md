# 适配器

## schema 适配器
模式适配器允许Calcite读取特定类型的数据，将数据呈现到对应的表中
* Cassandra adapter ([calcite-cassandra](https://calcite.apache.org/apidocs/org/apache/calcite/adapter/cassandra/package-summary.html))
* CSV adapter (example/csv)
* Druid adapter (calcite-druid)
* Elasticsearch adapter (calcite-elasticsearch)
* File adapter (calcite-file)
* Geode adapter (calcite-geode)
* JDBC adapter (part of calcite-core)
* MongoDB adapter (calcite-mongodb)
* OS adapter (calcite-os)
* Pig adapter (calcite-pig)
* Solr cloud adapter (solr-sql)
* Spark adapter (calcite-spark)
* Splunk adapter (calcite-splunk)
* Eclipse Memory Analyzer (MAT) adapter (mat-calcite-plugin)


## 引擎
许多项目和产品使用Apache Calcite进行SQL解析，查询优化，数据虚拟化/联合和物化视图重写。

## 驱动
驱动程序允许您从应用程序连接到Calcite
* [JDBC driver](https://calcite.apache.org/apidocs/org/apache/calcite/jdbc/package-summary.html)

JDBC驱动程序由[Avatica](https://calcite.apache.org/avatica/docs/)提供支持。
连接可以是本地连接或远程连接（基于HTTP的JSON或基于HTTP的Protobuf）。
JDBC连接字符串的基本形式是  
jdbc:calcite:property=value;property2=value2
其中`property`，`property2`是如下所述的属性。
（连接字符串符合OLE DB Connect String语法，由Avatica的[ConnectStringParser](https://calcite.apache.org/avatica/apidocs/org/apache/calcite/avatica/ConnectStringParser.html)实现。）

JDBC 链接字符串

| 属性                               | 描述           |
| ------------------------------------ | --------------------- |
| `approximateDecimal`          | 是否可以接受来自`DECIMAL`类型的聚合函数的近似结果|
| `approximateDistinctCount`          | 是否可以接受来自`COUNT(DISTINCT ...)`聚合函数的近似结果|
| `approximateTopN`          | 是否可以接受来自“前N个”查询的近似结果`(ORDER BY aggFun() DESC LIMIT n)`|
| `caseSensitive`          | 标识符是否区分大小写。如果未指定，则使用`lex`的值。|
| `conformance`          | SQL接口一致性级别。默认(PRAGMATIC_2003), LENIENT, MYSQL_5, ORACLE_10, ORACLE_12, PRAGMATIC_99, PRAGMATIC_2003, STRICT_92, STRICT_99, STRICT_2003, SQL_SERVER_2008.|
| `createMaterializations`          | Whether Calcite should create materializations. Default false.|
| `defaultNullCollation`          | NULL 如何排序,NULL排在第一位或者NULL排最后,默认HIGH,和Oracle相同|
| `druidFetch`          | 执行SELECT查询时，Druid适配器应该一次获取多少行。|
| `forceDecorrelate`          | Whether the planner should try de-correlating as much as possible. Default true |
| `fun`          |内置函数和运算符的集合。有效值是“标准”（默认），“oracle”，“spatial”，并且可以使用逗号组合，例如“oracle，spatial”。 |
| `lex`          |词汇政策 ORACLE (default), MYSQL, MYSQL_ANSI, SQL_SERVER, JAVA. |
| `materializationsEnabled`          | Whether Calcite should use materializations. Default false. |
| `model`          | JSON/YAML inline:{...} for JSON and inline:... for YAML.|
| `parserFactory`          |解析器工厂 [interface SqlParserImplFactory](https://calcite.apache.org/apidocs/org/apache/calcite/sql/parser/SqlParserImplFactory.html) 有一个公共默认构造函数或一个`INSTANCE`常量。|
| `quoting`          | 如何引用标识符。值为DOUBLE_QUOTE(")，BACK_QUOTE(`)，BRACKET(`[`)。如果未指定，则使用lex的值|
| `schema`          | 初始schema|
| `schemaFactory`          | schema工厂[interface SchemaFactory](https://calcite.apache.org/apidocs/org/apache/calcite/schema/SchemaFactory.html) 有一个公共默认构造函数或一个`INSTANCE`常量。|
| `schemaType`          | schema类型。值必须为“MAP”（默认值）“JDBC”或“CUSTOM”（如果指定了`schemaFactory`，则为隐式）。如果指定了`model`，则忽略。|
| `spark`          | 指定是否应将Spark用作无法推送到源系统的处理引擎。如果为false（默认值），则Calcite会生成实现Enumerable接口的代码。|
| `timeZone`          |时区,默认JVM时区 |
| `typeSystem`          | 类型系统 [interface RelDataTypeSystem](https://calcite.apache.org/apidocs/org/apache/calcite/rel/type/RelDataTypeSystem.html)有一个公共默认构造函数或一个`INSTANCE`常量。|
| `unquotedCasing`          | 如果未引用标识符，则如何存储标识符 UNCHANGED, TO_UPPER, TO_LOWER。如果未指定，则使用`lex`的值。|


如果链接单个数据库可以使用如下链接,类似

jdbc:calcite:schemaType=JDBC; schema.jdbcUser=SCOTT; schema.jdbcPassword=TIGER; schema.jdbcUrl=jdbc:hsqldb:res:foodmart

创建与通过JDBC模式适配器映射到foodmart数据库的模式的连接

jdbc:calcite:schemaFactory=org.apache.calcite.adapter.cassandra.CassandraSchemaFactory; schema.host=localhost; schema.keyspace=twissandra

建立与Cassandra适配器的连接，相当于编写以下模型文件：
```
{
  "version": "1.0",
  "defaultSchema": "foodmart",
  "schemas": [
    {
      type: 'custom',
      name: 'twissandra',
      factory: 'org.apache.calcite.adapter.cassandra.CassandraSchemaFactory',
      operand: {
        host: 'localhost',
        keyspace: 'twissandra'
      }
    }
  ]
}
```

## Server 服务器

`calcite-core` 只支持`select`查询和DML(`INSERT`,`UPDATE`,`DELETE`,`MERGE`),DDL(`REATE SCHEMA` 或 `CREATE TABLE`)并不支持
正如我们将要看到的，DDL使存储库的状态模型复杂化并使解析器更难扩展，因此我们将DDL排除在核心之外。

server模块(`calcite-server`)可以支持DDL语句,继承SQL parser,可以使用如下DDL语句
* `CREATE` and `DROP SCHEMA`
* `CREATE` and `DROP FOREIGN SCHEMA`
* `CREATE` and `DROP TABLE (including CREATE TABLE ... AS SELECT)`
* `CREATE` and `DROP MATERIALIZED VIEW`
* `CREATE` and `DROP VIEW`


添加`calcite-server.jar`添加到class path中,并在链接字符串中添加如下信息`parserFactory=org.apache.calcite.sql.parser.ddl.SqlDdlParserImpl#FACTORY`
`sqlline`使用例子: 


```
$ ./sqlline
sqlline version 1.3.0
> !connect jdbc:calcite:parserFactory=org.apache.calcite.sql.parser.ddl.SqlDdlParserImpl#FACTORY sa ""
> CREATE TABLE t (i INTEGER, j VARCHAR(10));
No rows affected (0.293 seconds)
> INSERT INTO t VALUES (1, 'a'), (2, 'bc');
2 rows affected (0.873 seconds)
> CREATE VIEW v AS SELECT * FROM t WHERE i > 1;
No rows affected (0.072 seconds)
> SELECT count(*) FROM v;
+---------------------+
|       EXPR$0        |
+---------------------+
| 1                   |
+---------------------+
1 row selected (0.148 seconds)
> !quit
```
`calcite-server`是calcite的一个可选项,其目标之一是使用可以从SQL命令行尝试的简明示例来展示Calcite的功能（例如，物化视图，外部表和生成的列)
`calcite-server`使用的api都可以在`calcite-core`找到

如果您是子项目的作者，那么您的语法扩展不太可能与calcite-server中的语法扩展匹配，因此我们建议您通过扩展核心解析器来添加SQL语法扩展;
如果需要DDL命令，可以从calcite-server复制粘贴到项目中。

目前，存储库不是持久化的。在执行DDL命令时，需要通过添加和删除根[Schema](https://calcite.apache.org/apidocs/org/apache/calcite/schema/Schema.html)中可访问的对象来修改内存存储库。
同一SQL会话中的所有命令都将看到这些对象。通过执行相同的SQL命令脚本，可以在将来的会话中创建相同的对象。

calcite还可以充当数据虚拟化或联合服务器:calcite在多个外部schema中管理数据，但对于客户机来说，数据似乎都在相同的位置。
calcite选择在哪里进行处理，以及是否创建数据副本以提高效率。
calcite-server模块是朝着这一目标迈出的一步;行业强度的解决方案需要进一步的打包(使calcite作为服务可运行)、存储库持久性、授权和安全性。

## 可扩展性

还有许多其他api允许您扩展方calcite的功能。

在本节中，我们将简要描述这些api，以便让您了解什么是可能的。要完全使用这些api，您需要阅读其他文档，比如接口的javadoc，并可能找到我们为它们编写的测试。

## 函数与操作符

有几种方法可以向calcite中添加运算符或函数。我们将首先描述最简单的(最强大)方法。

用户定义的函数最简单(但最强大)。它们很容易编写(您只需编写一个Java类并将其注册到模式中)，但是在参数的数量和类型、解析重载的函数或派生返回类型方面没有提供太多灵活性。

如果需要这种灵活性，可能需要编写一个用户定义的操作符([interface SqlOperator](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlOperator.html))

如果操作符不遵循标准的SQL函数语法`f(arg1, arg2，...)`，则需要扩展解析器[extend the parser](https://calcite.apache.org/docs/adapter.html#extending-the-parser)。

测试中有很多很好的例子:类[UdfTest](https://github.com/apache/calcite/blob/master/core/src/test/java/org/apache/calcite/test/UdfTest.java)测试用户定义的函数和用户定义的聚合函数。

## 聚合函数

用户定义的聚合函数类似于用户定义的函数，但是每个函数都有几个对应的Java方法，每个方法对应于聚合生命周期中的每个阶段:

* `init` 创建一个累加器
* `add` 将一行的值添加到累加器
* `merge` 将两个累加器合二为一
* `result` 完成累加器并将其转换为结果


例子: `SUM(int)`
```
struct Accumulator {
  final int sum;
}
Accumulator init() {
  return new Accumulator(0);
}
Accumulator add(Accumulator a, int x) {
  return new Accumulator(a.sum + x);
}
Accumulator merge(Accumulator a, Accumulator a2) {
  return new Accumulator(a.sum + a2.sum);
}
int result(Accumulator a) {
  return new Accumulator(a.sum + x);
}
```

下面是计算具有列值4和7的两行之和的调用顺序
```
a = init()    # a = {0}
a = add(a, 4) # a = {4}
a = add(a, 7) # a = {11}
return result(a) # returns 11
```

## 窗口函数

窗口函数类似于聚合函数，但它应用于由`OVER`子句收集的一组行，而不是由`GROUP by`子句收集的一组行。
每个聚合函数都可以用作窗口函数，但是有一些关键的区别。窗口函数看到的行可能是有序的，依赖于顺序(例`RANK`)的窗口函数不能用作聚合函数。

另一个区别是窗口是非分离的:特定行可以出现在多个窗口中。例如，10:37出现在9:00-10:00和9:15-9:45两个窗口中

窗口函数是增量计算的:当时钟从10:14滴答到10:15时，可能有两行进入窗口，三行离开。为此，窗口函数具有额外的生命周期操作:
* `remove` 从累加器中移除一个值。

`SUM(int)`
```
Accumulator remove(Accumulator a, int x) {
  return new Accumulator(a.sum - x);
}
```

下面是计算前两行的移动和的调用序列，这四行分别是4,7,2和3:
```
a = init()       # a = {0}
a = add(a, 4)    # a = {4}
emit result(a)   # emits 4
a = add(a, 7)    # a = {11}
emit result(a)   # emits 11
a = remove(a, 4) # a = {7}
a = add(a, 2)    # a = {9}
emit result(a)   # emits 9
a = remove(a, 7) # a = {2}
a = add(a, 3)    # a = {5}
emit result(a)   # emits 5
```

## 分组窗口函数

分组窗口函数是操作`GROUP BY`子句以将记录收集到集合中的函数。
内置的分组窗口函数有`HOP`滚动,`TUMBLE`滑动,`SESSION`Session。您可以通过实现接口SqlGroupedWindowFunction[interface SqlGroupedWindowFunction](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlGroupedWindowFunction.html)来定义其他函数。

## 表函数和表宏 - 不理解含义

用户定义的表函数的定义方式与普通的“scalar”用户定义函数类似，但是在查询的`FROM`子句中使用。下面的查询使用了一个名为`Ramp`的表函数:

```
SELECT * FROM TABLE(Ramp(3, 4))
```

用户定义的表宏使用与表函数相同的SQL语法，但是定义不同。它们不是生成数据，而是生成关系表达式。
在查询准备期间调用表宏，然后可以优化它们生成的关系表达式。(calcite视图的实现使用了表宏。)

类[TableFunctionTest](https://github.com/apache/calcite/blob/master/core/src/test/java/org/apache/calcite/test/TableFunctionTest.java)测试表函数，并包含几个有用的示例。

## 扩展解析

假设您需要扩展方calcite的SQL语法，使其与语法的未来更改兼容。不建议复制语法文件`Parser.jj`在您的项目中，因为语法经常被编辑。

幸运的是,Parser.jj实际上是一个Apache FreeMarker模板，其中包含可以替换的变量。`calcite-core`中的解析器使用变量的默认值(通常为空)实例化模板，但是您可以覆盖它。
如果您的项目需要不同的解析器，您可以提供自己的配置。`config.fmpp`和`parserImpls.ftl`然后生成一个扩展的解析器。

在[[CALCITE-707](https://issues.apache.org/jira/browse/CALCITE-707)]中创建并添加DDL语句(如`CREATE TABLE`)的`calcite-server`模块就是一个可以遵循的示例。
还请参见类[ExtensionSqlParserTest](https://github.com/apache/calcite/blob/master/core/src/test/java/org/apache/calcite/sql/parser/parserextensiontesting/ExtensionSqlParserTest.java)。

## 生成并使用SQL方言

要定制解析器应该接受的SQL扩展，请实现[interface SqlConformance](https://calcite.apache.org/apidocs/org/apache/calcite/sql/validate/SqlConformance.html)或使用[enum SqlConformanceEnum](https://calcite.apache.org/apidocs/org/apache/calcite/sql/validate/SqlConformanceEnum.html)中的内置值之一

要控制如何为外部数据库生成SQL(通常通过JDBC适配器)，请使用[class SqlDialect](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlDialect.html)还描述了引擎的功能，比如它是否支持`OFFSET`和`FETCH`。

## 声明自定义Schema

要定义自定义schema，您需要实现
[interface SchemaFactory](https://calcite.apache.org/apidocs/org/apache/calcite/schema/SchemaFactory.html)

在查询准备期间，calcite将调用此接口来找出您的模式包含哪些表和子schema。当在查询中引用模式中的表时，calcite将要求schema创建接口[interface Table](https://calcite.apache.org/apidocs/org/apache/calcite/schema/Table.html)表的实例。

该表将被包装在一个[TableScan](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/TableScan.html)，并将经历查询优化过程。

## 反射的Schema

反射模式([class ReflectiveSchema](https://calcite.apache.org/apidocs/org/apache/calcite/adapter/java/ReflectiveSchema.html))是一种包装Java对象的方法，以便将其显示为schema。它的集合值字段将显示为table。

## 声明自定义表

要定义自定义表，需要实现[interface TableFactory](https://calcite.apache.org/apidocs/org/apache/calcite/schema/TableFactory.html).
schema factory是一组已命名的表，而table factory在绑定到具有特定名称(以及可选的一组额外操作数)的schema时只生成一个表。

## 修改数据
如果您的表要支持DML操作(插入`INSERT`、更新`UPDATE`、删除`DELETE`、合并`MERGE`)，那么接口表的实现必须实现[interface ModifiableTable](https://calcite.apache.org/apidocs/org/apache/calcite/schema/ModifiableTable.html)

## 流
如果您的表要支持流查询，那么`interface Table`的实现必须实现[interface StreamableTable](https://calcite.apache.org/apidocs/org/apache/calcite/schema/StreamableTable.html)

例子:[class StreamTest](https://github.com/apache/calcite/blob/master/core/src/test/java/org/apache/calcite/test/StreamTest.java)


## 将操作下推到表中

如果希望将处理下推到自定义表的源系统，可以考虑实现[interface FilterableTable](https://calcite.apache.org/apidocs/org/apache/calcite/schema/FilterableTable.html)或[interface ProjectableFilterableTable](https://calcite.apache.org/apidocs/org/apache/calcite/schema/ProjectableFilterableTable.html)。

如果你想要更多的控制，你应该写一个计划表规则(planner rule)。这将允许您下推表达式，根据成本决定是否下推处理，以及下推更复杂的操作，如连接、聚合和排序。


## 类型系统

通过实现[interface RelDataTypeSystem](https://calcite.apache.org/apidocs/org/apache/calcite/rel/type/RelDataTypeSystem.html)，您可以自定义类型系统

## 关系运算符

所有关系运算符都实现接口[RelNode](https://calcite.apache.org/apidocs/org/apache/calcite/rel/RelNode.html)，大多数扩展类[AbstractRelNode](https://calcite.apache.org/apidocs/org/apache/calcite/rel/AbstractRelNode.html)。
核心运算符（由[SqlToRelConverter](https://calcite.apache.org/apidocs/org/apache/calcite/sql2rel/SqlToRelConverter.html)使用并涵盖传统的关系代数）是
[TableScan](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/TableScan.html)，
[TableModify](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/TableModify.html)，
[Values](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Values.html)，
[Project](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Project.html)，
[Filter](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Filter.html)，
[Aggregate](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Aggregate.html)，
[Join](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Join.html)，
[Sort](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Sort.html)，
[Union](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Union.html)，
[Intersect](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Intersect.html)，
[Minus](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Minus.html)，
[Window](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Window.html)
和
[Match](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Match.html)

每一个都有一个“pure”逻辑子类，[LogicalProject](https://calcite.apache.org/apidocs/org/apache/calcite/rel/logical/LogicalProject.html)等等。
任何给定的适配器都有对应于其引擎可以有效实现的操作的相应适配器;例如，Cassandra适配器有[CassandraProject](https://calcite.apache.org/apidocs/org/apache/calcite/adapter/cassandra/CassandraProject.html)，但没有`CassandraJoin`。

您可以定义自己的`RelNode`子类来添加新操作符，或者在特定引擎中添加现有操作符的实现。

为了使一个操作符更加强大，您将需要planner rule来将其与现有的操作符结合起来。(并提供元数据(metadata)。这是代数和效率是组合的，你可以编写一些规则，它们组合起来处理指数数量的查询模式

如果可能，使您的操作符成为现有操作符的子类;然后您就可以重用或调整它的规则。更好的是，如果您的操作符是一个逻辑(logical)操作，您可以根据现有的操作符重写(同样，通过planner规则)，那么您应该这样做。您将能够重用这些操作符的规则、元数据和实现，而无需额外的工作。


## 计划规则

计划器规则([class RelOptRule](https://calcite.apache.org/apidocs/org/apache/calcite/plan/RelOptRule.html)))将关系表达式(RelNode)转换为等效关系表达式(RelNode)。

(planer)计划器引擎有许多已注册的计划器规则，并触发这些规则以将输入查询转换为更有效的查询。因此，规划规则是优化过程的核心，但令人惊讶的是，每个规划规则本身并不关心成本。planner引擎负责以生成最佳计划的顺序触发规则，但是每个单独的规则只关注其自身的正确性。

calcite有两个内置的计划器引擎:[class VolcanoPlanner](https://calcite.apache.org/apidocs/org/apache/calcite/plan/volcano/VolcanoPlanner.html)使用动态规划，适合穷极搜索，而[class HepPlanner](https://calcite.apache.org/apidocs/org/apache/calcite/plan/hep/HepPlanner.html)则以更固定的顺序触发一系列规则。



## 调用约定 Calling conventions - 不懂

Calling conventions 是特定数据引擎使用的协议。例如，Cassandra引擎有一组关系操作符、`CassandraProject`、`CassandraFilter`等，这些操作符可以相互连接，而不需要将数据从一种格式转换为另一种格式。

如果需要将数据从一种calling convention转换为为另一种，Calcite 使用一种特殊子类称为转换器的关系表达式转换(参见[class Converter](https://calcite.apache.org/apidocs/org/apache/calcite/rel/convert/Converter.html))。当然，转换数据需要运行时成本。

在规划使用多个引擎的查询时，Calcite根据调用约定“着色”关系表达式树的区域。计划员通过触发规则将操作推入数据源。如果引擎不支持特定操作，则规则将不会触发。有时一个操作可能发生在多个地方，最终根据成本选择最佳计划。

Calling convention 是实现[interface Convention](https://calcite.apache.org/apidocs/org/apache/calcite/plan/Convention.html)、辅助接口(例如 [interface CassandraRel](https://calcite.apache.org/apidocs/org/apache/calcite/adapter/cassandra/CassandraRel.html))和
[class RelNode](https://calcite.apache.org/apidocs/org/apache/calcite/rel/RelNode.html)的一组子类，这些子类为核心关系操作符(
[Project](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Project.html)，
[Filter](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Filter.html)，
[Aggregate](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Aggregate.html)，
[Join](https://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Join.html)等)实现该接口。

## 内置的SQL实现

如果适配器没有实现所有核心关系运算符，calcite如何实现SQL ?

答案是一个特定的内置calling convention,[EnumerableConvention](https://calcite.apache.org/apidocs/org/apache/calcite/adapter/enumerable/EnumerableConvention.html)。枚举约定的关系表达式实现为“内置”(built-ins):Calcite生成Java代码，编译它，并在自己的JVM中执行。枚举约定的效率不如运行在面向列的数据文件上的分布式引擎，但是它可以实现所有核心关系操作符和所有内置SQL函数和操作符。如果数据源不能实现关系运算符(relational operator)，则枚举约定(enumerable convention)是一种低效运行。


## 统计数据和成本

calcite有一个元数据系统，允许您定义关于关系运算符的成本函数和统计信息，统称为元数据。每种元数据都有一个接口(通常)有一个方法。例如，选择性由[interface RelMdSelectivity](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdSelectivity.html)和方法[getSelectivity(RelNode rel, RexNode predicate)](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMetadataQuery.html#getSelectivity-org.apache.calcite.rel.RelNode-org.apache.calcite.rex.RexNode-)定义。

有许多内置的元数据类型，包括
排序[collation](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdCollation.html)、
列源[column origins](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdColumnOrigins.html)、
列惟一性[column uniqueness](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdColumnUniqueness.html)、
不同的行计数[distinct row count](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdDistinctRowCount.html)、
分布[distribution](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdDistribution.html)、
解释可见性[explain visibility](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdExplainVisibility.html)、
表达式沿袭[expression lineage](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdExpressionLineage.html)、
最大行计数[max row count](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdMaxRowCount.html)、
节点类型[node types](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdNodeTypes.html)、
并行性[parallelism](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdParallelism.html)、
原始行的百分比[percentage original rows](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdPercentageOriginalRows.html)、
总体大小[population size](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdPopulationSize.html)、
谓词[predicates](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdPredicates.html)、
行计数[row count](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdRowCount.html)、
选择性[selectivity](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdSelectivity.html)、
大小[size](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdSize.html)、
表引用[table references](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdTableReferences.html)
和
惟一键[unique keys](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/RelMdUniqueKeys.html);
你也可以自己定义。

然后，您可以提供一个元数据提供程序，该提供程序为`RelNode`的特定子类计算此类元数据。元数据提供程序可以处理内置和扩展元数据类型，以及内置和扩展RelNode类型。在准备查询时，calcite结合所有适用的元数据提供程序并维护一个缓存，以便只计算给定的元数据片段(例如特定筛选操作符中的条件x > 10 `Filter` operator)一次。


