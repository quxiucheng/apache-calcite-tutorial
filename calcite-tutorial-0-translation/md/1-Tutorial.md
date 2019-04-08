

# 快速入门

本章主要介绍一个简单的CSV适配器如何一步一步的创建和连接到Calcite。这个适配器能够将一个目录下面的csv文件表现成一个包含各种表的schema。Calcite已经实现了rest接口，也提供了一套完整的SQL接口。

Calcite-example-CSV是一个功能完整的适配器，他能够使Calcite读CSV格式的文件。值得注意的是，几百行Java代码就足以提供完整的SQL查询功能。

CSV还可以作为构建适配器到其他数据格式的模板。尽管没有多少行代码，但它涵盖了几个重要的概念：

* 通过SchemaFactory和Schema 接口自定义schema

* 用json模型文件描述schema

* 用json模型文件描述视图view

* 通过表接口自定义表

* 确定表的记录类型

* 创建一个表的简单方法是使用`ScannableTable`接口直接列举所有的行

* 创建一个表的更高级的方法是实现`FilterableTable`接口，并且能够根据一些简单的判断来进行过滤

* 创建一个表的高级的方法是使用`TranslatableTable`类，这个类能使用规划规则转换为关系操作符。

### 下载安装

##### **环境准备：** java版本\(8 9 10）

```
$ git clone https://github.com/apache/calcite.git
$ cd calcite
$ mvn install -DskipTests -Dcheckstyle.skip=true
$ cd example/csv
```

### 开始：

现在我们需要用到sqline来连接Calcite，这个工程里面包含了`SQL shell`脚本.



继续执行如下命令：

```
 ./sqlline
sqlline> !connect jdbc:calcite:model=target/test-classes/model.json admin admin
```

（windows下请执行sqlline.bat命令）

执行元数据查询：

```
sqlline> !tables
+------------+--------------+-------------+---------------+----------+------+
| TABLE_CAT  | TABLE_SCHEM  | TABLE_NAME  |  TABLE_TYPE   | REMARKS  | TYPE |
+------------+--------------+-------------+---------------+----------+------+
| null       | SALES        | DEPTS       | TABLE         | null     | null |
| null       | SALES        | EMPS        | TABLE         | null     | null |
| null       | SALES        | HOBBIES     | TABLE         | null     | null |
| null       | metadata     | COLUMNS     | SYSTEM_TABLE  | null     | null |
| null       | metadata     | TABLES      | SYSTEM_TABLE  | null     | null |
+------------+--------------+-------------+---------------+----------+------+
```

（温馨提示：在执行sqline的`!tables`命令后，后台执行了[`DatabaseMetaData.getTables()`](http://docs.oracle.com/javase/7/docs/api/java/sql/DatabaseMetaData.html#getTables%28java.lang.String, java.lang.String, java.lang.String, java.lang.String[]%29)。相同的元数据查询命令有`!columns`和`!describe`）

你现在能够看到在这个系统中有5个表：`EMPS`，`DEPTS`在当前的`SALES` schema中，并且`COLUMNS`和`TABLES`是在系统`metadata`的schema中。系统表始终存在于Calcite中，但是其他表都是由特定的schema的实现来生成；例如：`EMPS`和`DEPTS`表是基于`target/test-classes`下的`EMPS.CSV`和`DEPTS.csv`文件来生成的。

让我们在这些表的基础上做些查询操作来展示Calcite是怎样提供完整的SQL查询的实现。先来扫描一张表：

```
sqlline> SELECT * FROM emps;
+--------+--------+---------+---------+----------------+--------+-------+---+
| EMPNO  |  NAME  | DEPTNO  | GENDER  |      CITY      | EMPID  |  AGE  | S |
+--------+--------+---------+---------+----------------+--------+-------+---+
| 100    | Fred   | 10      |         |                | 30     | 25    | t |
| 110    | Eric   | 20      | M       | San Francisco  | 3      | 80    | n |
| 110    | John   | 40      | M       | Vancouver      | 2      | null  | f |
| 120    | Wilma  | 20      | F       |                | 1      | 5     | n |
| 130    | Alice  | 40      | F       | Vancouver      | 2      | null  | f |
+--------+--------+---------+---------+----------------+--------+-------+---+
```

加入 JION 和 GROUP BY：

```
sqlline> SELECT d.name, COUNT(*)
. . . .> FROM emps AS e JOIN depts AS d ON e.deptno = d.deptno
. . . .> GROUP BY d.name;
+------------+---------+
|    NAME    | EXPR$1  |
+------------+---------+
| Sales      | 1       |
| Marketing  | 2       |
+------------+---------+
```

最后，VALUES操作能够生成单独的一行，这是一种非常方便的方式来测试表达式和内置的SQL函数：

```
sqlline> VALUES CHAR_LENGTH('Hello, ' || 'world!');
+---------+
| EXPR$0  |
+---------+
| 13      |
+---------+
```

Calcite有很多种其他的SQL特征。我们不需要完整的介绍他们。你们可以写一些查询来测试。

### 模式发现

现在，你一定会疑惑：Calcite是怎么找到这些表的？请记住：Calcite并不关心也不会获取CSV文件的任何信息。（你可以讲Calcite理解成是一个不包含存储层的数据库，它不需要关心任何文件格式）Calcite之所以能获取到这些表是因为我们执行了 calcite-example-csv 项目中的代码。

整个执行链条中有很多步骤。首先，我们基于在model文件中定义的schema工厂类定义了一个schema；然后，schema工厂创建了一个schema，并且这个schema创建了多张表，每张表都清楚怎样扫描csv文件来获取数据；最后，Calcite解析了查询语句并且创建了执行计划来使用这些表，在执行查询时，Calcite利用表来读取数据。让我们来更详细的了解这些步骤的细节。

在JDBC连接字符串上，我们给出了JSON格式的模型路径。模型如下：

```
{
  version: '1.0',
  defaultSchema: 'SALES',
  schemas: [
    {
      name: 'SALES',
      type: 'custom',
      factory: 'org.apache.calcite.adapter.csv.CsvSchemaFactory',
      operand: {
        directory: 'target/test-classes/sales'
      }
    }
  ]
}
```

上面的模型定义了一个叫做"SALES"的单模式。这个schema是由[org.apache.calcite.adapter.csv.CsvSchemaFactory](https://github.com/apache/calcite/blob/master/example/csv/src/main/java/org/apache/calcite/adapter/csv/CsvSchemaFactory.java)插件类所提供，这个插件类是calcite-example-csv项目的一部分并且实现了[SchemaFactory](http://calcite.apache.org/apidocs/org/apache/calcite/schema/SchemaFactory.html) 接口。它的create方法实例化一个模式，从模型文件传入目录参数:

```
public Schema create(SchemaPlus parentSchema, String name,
    Map<String, Object> operand) {
  String directory = (String) operand.get("directory");
  String flavorName = (String) operand.get("flavor");
  CsvTable.Flavor flavor;
  if (flavorName == null) {
    flavor = CsvTable.Flavor.SCANNABLE;
  } else {
    flavor = CsvTable.Flavor.valueOf(flavorName.toUpperCase());
  }
  return new CsvSchema(
      new File(directory),
      flavor);
}
```

在该模型的驱动下，模式工厂实例化一个名为“SALES”的模式。该模式是[org.apache.calcite.adapter.csv.CsvSchema](https://github.com/apache/calcite/blob/master/example/csv/src/main/java/org/apache/calcite/adapter/csv/CsvSchema.java)的一个实例，并实现了Calcite接口[Schema](http://calcite.apache.org/apidocs/org/apache/calcite/schema/Schema.html)。

schame的工作是提供一系列的表。（它也可以列出sub-schema和表函数，但是还有很多先进的特性只是calcite-example-csv案例没有支持而已）。这些表实现了calcite的 [Table](http://calcite.apache.org/apidocs/org/apache/calcite/schema/Table.html) 接口。`CsvSchema`生成 [CsvTable](https://github.com/apache/calcite/blob/master/example/csv/src/main/java/org/apache/calcite/adapter/csv/CsvTable.java)及其子类的实例的表。

这是来自`CsvSchema`的相关代码，覆盖了AbstractSchema基类中的[`getTableMap()`](http://calcite.apache.org/apidocs/org/apache/calcite/schema/impl/AbstractSchema.html#getTableMap%28%29)方法。

```
protected Map<String, Table> getTableMap() {
  // Look for files in the directory ending in ".csv", ".csv.gz", ".json",
  // ".json.gz".
  File[] files = directoryFile.listFiles(
      new FilenameFilter() {
        public boolean accept(File dir, String name) {
          final String nameSansGz = trim(name, ".gz");
          return nameSansGz.endsWith(".csv")
              || nameSansGz.endsWith(".json");
        }
      });
  if (files == null) {
    System.out.println("directory " + directoryFile + " not found");
    files = new File[0];
  }
  // Build a map from table name to table; each file becomes a table.
  final ImmutableMap.Builder<String, Table> builder = ImmutableMap.builder();
  for (File file : files) {
    String tableName = trim(file.getName(), ".gz");
    final String tableNameSansJson = trimOrNull(tableName, ".json");
    if (tableNameSansJson != null) {
      JsonTable table = new JsonTable(file);
      builder.put(tableNameSansJson, table);
      continue;
    }
    tableName = trim(tableName, ".csv");
    final Table table = createTable(file);
    builder.put(tableName, table);
  }
  return builder.build();
}

/** Creates different sub-type of table based on the "flavor" attribute. */
private Table createTable(File file) {
  switch (flavor) {
  case TRANSLATABLE:
    return new CsvTranslatableTable(file, null);
  case SCANNABLE:
    return new CsvScannableTable(file, null);
  case FILTERABLE:
    return new CsvFilterableTable(file, null);
  default:
    throw new AssertionError("Unknown flavor " + flavor);
  }
}
```

schema扫描模型文件中定义的目录并且根据以.csv结尾的文件创建对应的表。在这个例子中，目录是`target/test-classes/sales` 并且包含`EMPS.csv`和`DEPTS.csv`，根据这两个csv文件最终生成了`EMPS`表和`DEPTS`表。

### schemas中的Tables和views

你应该关注到：我们怎样做到不需要在模型中定义任何表，但是schema却自动的创建了这些表。你可以使用schema的`tables`属性来定义除了自动创建的表以外的额外表。

我们来看看怎样创建一个重要并且有用的表格类型，即视图。

当你编写一个查询语句的时候视图看起来像一张表，但是它不存储数据。它会执行查询语句生成查询结果。当查询被计划时，视图被展开。因此，查询计划通常会执行优化，比如从SELECT子句中删除最终结果中没有使用的表达式。

下面是定义了一个视图的schema：

```
{
  version: '1.0',
  defaultSchema: 'SALES',
  schemas: [
    {
      name: 'SALES',
      type: 'custom',
      factory: 'org.apache.calcite.adapter.csv.CsvSchemaFactory',
      operand: {
        directory: 'target/test-classes/sales'
      },
      tables: [
        {
          name: 'FEMALE_EMPS',
          type: 'view',
          sql: 'SELECT * FROM emps WHERE gender = \'F\''
        }
      ]
    }
  ]
}
```

`type: 'view'` 这一行使用`FEMALE_EMPS`标记了一个视图，而不是常规表格或自定义表格。请注意，视图定义中的单引号会以JSON的正常方式使用反斜杠进行转义。

JSON中通常不会有太长的字符串，因此Calcite支持另一种语法。如果你的视图有一个很长的SQL语句，你可以将它拆成几行而不是一个完整的字符串：

```
{
  name: 'FEMALE_EMPS',
  type: 'view',
  sql: [
    'SELECT * FROM emps',
    'WHERE gender = \'F\''
  ]
}
```

现在我们已经定义了一个视图，我们可以在查询中使用它，就像是定义了一张表一样：

```
sqlline> SELECT e.name, d.name FROM female_emps AS e JOIN depts AS d on e.deptno = d.deptno;
+--------+------------+
|  NAME  |    NAME    |
+--------+------------+
| Wilma  | Marketing  |
+--------+------------+

```

### 自定义表

自定义表由用户自定义的代码来实现。这些表不需要存在于自定义的schema。下面是一个自定义的模型文件：

```
{
  version: '1.0',
  defaultSchema: 'CUSTOM_TABLE',
  schemas: [
    {
      name: 'CUSTOM_TABLE',
      tables: [
        {
          name: 'EMPS',
          type: 'custom',
          factory: 'org.apache.calcite.adapter.csv.CsvTableFactory',
          operand: {
            file: 'target/test-classes/sales/EMPS.csv.gz',
            flavor: "scannable"
          }
        }
      ]
    }
  ]
}
```

我们做个简单的查询：

```
sqlline> !connect jdbc:calcite:model=target/test-classes/model-with-custom-table.json admin admin
sqlline> SELECT empno, name FROM custom_table.emps;
+--------+--------+
| EMPNO  |  NAME  |
+--------+--------+
| 100    | Fred   |
| 110    | Eric   |
| 110    | John   |
| 120    | Wilma  |
| 130    | Alice  |
+--------+--------+
```

这是一个常规的schema，并且包含了一个由[org.apache.calcite.adapter.csv.CsvTableFactory](https://github.com/apache/calcite/blob/master/example/csv/src/main/java/org/apache/calcite/adapter/csv/CsvTableFactory.java)支持的自定义表，它实现了Calcite的接口[TableFactory](http://calcite.apache.org/apidocs/org/apache/calcite/schema/TableFactory.html) ，它的`create`方法实例化一个`CsvScannableTable`，传入来自模型文件的`file`参数：

```
public CsvTable create(SchemaPlus schema, String name,
    Map<String, Object> map, RelDataType rowType) {
  String fileName = (String) map.get("file");
  final File file = new File(fileName);
  final RelProtoDataType protoRowType =
      rowType != null ? RelDataTypeImpl.proto(rowType) : null;
  return new CsvScannableTable(file, protoRowType);
}
```

实现自定义表通常是实现自定义模式的更简单的替代方法。两种方法可能最后都创建了一个相似的Tbale接口的实现，但是，对于自定义表来说，你不需要实现元数据的发现。`CsvTableFactory`创建了一个`CsvScannableTable`，就像`CsvSchema`一样，但是表的实现过程中没有扫描文件系统去找`csv`文件。

自定义表需要model编写人员做更多的工作（编写人员需要明确指定每个表格及其文件），但是相对的，编写人员也就有了更大的自主权，比如给每个表都提供不同的参数。

### 模型中的注释

模型文件中能够使用 `/* … */`和 `//` 语法来添加注释（注释不是标准的json规范，但是非常有好处）

```
{
  version: '1.0',
  /* Multi-line
     comment. */
  defaultSchema: 'CUSTOM_TABLE',
  // Single-line comment.
  schemas: [
    ..
  ]
}
```

### 执行计划优化查询

到目前为止，只要表中没有大量的数据，我们看到表的实现就没有什么问题。但是，如果你的自定义的表有上百列和上百万行的数据，你宁愿系统没有在每次查询的时候检索所有的数据。你希望Calcite与适配器进行协商并找到访问数据的更有效的方法。

这种协商是查询优化的一种简单的方式。Calcite支持通过添加查询计划规则来优化查询。优化器规则通过在查询分析树中查找模式来操作，并用已经实现的实现优化的新的一组节点来替换书中的匹配节点。

优化规则和schema和tables一样也是可以扩展的。因此，如果你想用SQL去访问数据，你首先需要定义一个表或者schema，然后你需要定义一些规则来使这些访问更加高效。

为了证明这一点，我们现在使用一个查询规则去访问一个CSV文件中的一部分列。针对两个相似的schema我们执行同样的查询：

```
sqlline> !connect jdbc:calcite:model=target/test-classes/model.json admin admin
sqlline> explain plan for select name from emps;
+-----------------------------------------------------+
| PLAN                                                |
+-----------------------------------------------------+
| EnumerableCalcRel(expr#0..9=[{inputs}], NAME=[$t1]) |
|   EnumerableTableScan(table=[[SALES, EMPS]])        |
+-----------------------------------------------------+
sqlline> !connect jdbc:calcite:model=target/test-classes/smart.json admin admin
sqlline> explain plan for select name from emps;
+-----------------------------------------------------+
| PLAN                                                |
+-----------------------------------------------------+
| EnumerableCalcRel(expr#0..9=[{inputs}], NAME=[$t1]) |
|   CsvTableScan(table=[[SALES, EMPS]])               |
+-----------------------------------------------------+
```

很明显，这两个plan不同。为什么呢？让我们来看看`smart.json`模型文件，这个文件中有这么一行：

```
flavor: "TRANSLATABLE"
```

这一行会导致创建一个`CsvSchema`，并且他的`createTable`方法创建的是[CsvTranslatableTable](https://github.com/apache/calcite/blob/master/example/csv/src/main/java/org/apache/calcite/adapter/csv/CsvTranslatableTable.java)而不是`CsvScannableTable`。

`CsvTranslatableTable`实现了[TranslatableTable.toRel()](http://calcite.apache.org/apidocs/org/apache/calcite/schema/TranslatableTable.html#toRel())方法来创建[CsvTableScan](https://github.com/apache/calcite/blob/master/example/csv/src/main/java/org/apache/calcite/adapter/csv/CsvTableScan.java)。扫描表其实是扫描运算符树的叶子。通常的实现是[EnumerableTableScan](http://calcite.apache.org/apidocs/org/apache/calcite/adapter/enumerable/EnumerableTableScan.html)，但是我们已经创建了一个独特的子类型来引发规则的触发.

下面是整个的规则：

```
public class CsvProjectTableScanRule extends RelOptRule {
  public static final CsvProjectTableScanRule INSTANCE =
      new CsvProjectTableScanRule();

  private CsvProjectTableScanRule() {
    super(
        operand(Project.class,
            operand(CsvTableScan.class, none())),
        "CsvProjectTableScanRule");
  }

  @Override
  public void onMatch(RelOptRuleCall call) {
    final Project project = call.rel(0);
    final CsvTableScan scan = call.rel(1);
    int[] fields = getProjectFields(project.getProjects());
    if (fields == null) {
      // Project contains expressions more complex than just field references.
      return;
    }
    call.transformTo(
        new CsvTableScan(
            scan.getCluster(),
            scan.getTable(),
            scan.csvTable,
            fields));
  }

  private int[] getProjectFields(List<RexNode> exps) {
    final int[] fields = new int[exps.size()];
    for (int i = 0; i < exps.size(); i++) {
      final RexNode exp = exps.get(i);
      if (exp instanceof RexInputRef) {
        fields[i] = ((RexInputRef) exp).getIndex();
      } else {
        return null; // not a simple projection
      }
    }
    return fields;
  }
}
```

构造函数声明了能够触发规则的关联表达式的模式。`onMatch`方法生成了一个新的关联表达式并调用[`RelOptRuleCall.transformTo()`](http://calcite.apache.org/apidocs/org/apache/calcite/plan/RelOptRuleCall.html#transformTo(org.apache.calcite.rel.RelNode))方法来标明规则被成功触发。

### 查询优化过程

Calcite的查询计划非常巧妙，但是这里我们先不做介绍。

首先，Calcite并没有按照规定的顺序去触发规则。查询优化过程会尝试分支树中的很多分支，就像国际象棋程序中会测试众多的走法一样。如果规则A和规则B都符合查询运算符树中的给定部分，Calcite会同时触发他们。

然后，Calcite会使用"成本"来筛选计划，但是，成本模型并不会阻止Calcite去选择在短期内看起来更成本更昂贵的规则。

很多优化器都会有一个线性优化方案。在面对选择规则A或者规则B时，如上所述，这样的优化器需要立即选择。一般的优化器可能会有这样的策略，例如"将规则A应用于整棵树，然后将规则B应用于整棵树"，或者基于成本的策略，使用成本更便宜的结果的规则。

其实这些做法是一种妥协，但是Calcite并没有这么做，这使得组合各种规则变得简单。如果你想要将规则组合在一起以识别具有规则的物化视图从而能够从csv或者jdbc源系统中读取数据的话，你只需要将Calcite设置为所有规则的集合并告知她即可。

Calcite使用了一个成本模型。这个成本模型决定哪个计划最终得到执行，并且有时候会对搜索树进行剪纸操作以防止搜索空间爆炸，但是它不会强制你在规则A还是B之间做出选择。这个非常重要，因为这能避免实际上并不是最佳的搜索空间中陷入局部极小值。

这个成本模型也是"可插拔"的，就像它基于的表和查询运算符统计一样。这个会在后面讲到。

### JDBC适配器

JDBC适配器将JDBC数据源中的schema映射为Calcite schema。下面是一个从MySQL "foodmart"数据库中读取数据的schema的例子：

```
{
  version: '1.0',
  defaultSchema: 'FOODMART',
  schemas: [
    {
      name: 'FOODMART',
      type: 'custom',
      factory: 'org.apache.calcite.adapter.jdbc.JdbcSchema$Factory',
      operand: {
        jdbcDriver: 'com.mysql.jdbc.Driver',
        jdbcUrl: 'jdbc:mysql://localhost/foodmart',
        jdbcUser: 'foodmart',
        jdbcPassword: 'foodmart'
      }
    }
  ]
}
```

（因为这是Mondrian的主要测试数据集，所以使用Mondrian OLAP引擎的用户将熟悉FoodMart数据库。要加载数据集，请按照Mondrian的安装说明进行操作。）

**目前的限制：**JDBC适配器目前只能下推表扫描的操作；所有的其他的操作（过滤，链接，聚合等等）都没有发生在Calcite中。我们的目标是将尽可能多的操作压缩到源系统中，随时随地的翻译语法、数据类型和内置函数。如果Calcite查询是建立在单一的JDBC数据库中的表上，原则上整个查询应该都转到数据库中执行。如果数据表是来自于多个JDBC源，或者是JDBC和非JDBC的混合源，Calcite将使用最有效的分布式查询方法。

### 克隆JDBC适配器

克隆的JDBC适配器将创建一个混合的数据库。这些数据是来自于JDBC数据库，但是每张表第一次被访问时，会被读进内存中。Calcite会基于这些内存表来进行评估查询，实际上是数据库的缓存。

下面的模型从MySQL中的"foodmart"库中读取表：

```
{
  version: '1.0',
  defaultSchema: 'FOODMART_CLONE',
  schemas: [
    {
      name: 'FOODMART_CLONE',
      type: 'custom',
      factory: 'org.apache.calcite.adapter.clone.CloneSchema$Factory',
      operand: {
        jdbcDriver: 'com.mysql.jdbc.Driver',
        jdbcUrl: 'jdbc:mysql://localhost/foodmart',
        jdbcUser: 'foodmart',
        jdbcPassword: 'foodmart'
      }
    }
  ]
}
```

另一种技术是在现有的schema之上构建一个克隆模型。你可以使用source属性来引用模型中早先定义的schema，就像下面的例子：

```
{
  version: '1.0',
  defaultSchema: 'FOODMART_CLONE',
  schemas: [
    {
      name: 'FOODMART',
      type: 'custom',
      factory: 'org.apache.calcite.adapter.jdbc.JdbcSchema$Factory',
      operand: {
        jdbcDriver: 'com.mysql.jdbc.Driver',
        jdbcUrl: 'jdbc:mysql://localhost/foodmart',
        jdbcUser: 'foodmart',
        jdbcPassword: 'foodmart'
      }
    },
    {
      name: 'FOODMART_CLONE',
      type: 'custom',
      factory: 'org.apache.calcite.adapter.clone.CloneSchema$Factory',
      operand: {
        source: 'FOODMART'
      }
    }
  ]
}
```

你可以使用这种方式基于任何类型的schema创建一个克隆的schema，不仅仅是JDBC。

目前的系统中克隆的适配器功能不是所有的也不是最终的。我们计划开发更多的更复杂的缓存策略，以及更完整更有效的内存表的实现，但是现有的克隆的JDBC适配器像我们展示了有哪些功能我们可以用，并允许我们根据这些功能做出自己的尝试开发。

### 更多的主题

有很多你的其他的方式来扩展Calcite而不仅仅是在这节中所介绍的这些方法。[adapter specification](http://calcite.apache.org/docs/adapter.html) 介绍了包含的APIs。