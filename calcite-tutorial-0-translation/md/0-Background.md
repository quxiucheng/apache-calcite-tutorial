# 前奏

首先，我们需要明确一个概念：Apache Calcite 是一个动态的数据管理框架。

Calcite管理了很多种典型的数据库，但是他并没有这些数据库具有的关键能力：数据存储、数据处理算法、元数据的存储。也可以这样说：Calcite只是对各种数据库\(不同的数据源\)的查询进行了封装，并对外提供了统一的查询入口。

Calcite是有意的屏蔽这些基本的数据库所具有的能力。我们将看到这种“有意识”的做法对于业务应用\(applications\)与一种或多种数据源的数据存储和处理引擎之间的平衡是多么聪明的做法。同时，这种做法也为数据库添加数据\(除查询外唯一的一种数据库操作\)提供了坚实的基础。

让我们举个例子：我们先创建一个空的Calcite实例然后添加一些数据：

```
public static class HrSchema {
  public final Employee[] emps = 0;
  public final Department[] depts = 0;
}
Class.forName("org.apache.calcite.jdbc.Driver");
Properties info = new Properties();
info.setProperty("lex", "JAVA");
Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
SchemaPlus rootSchema = calciteConnection.getRootSchema();
Schema schema = ReflectiveSchema.create(calciteConnection,rootSchema, "hr", new HrSchema());
rootSchema.add("hr", schema);
Statement statement = calciteConnection.createStatement();
ResultSet resultSet = statement.executeQuery(
    "select d.deptno, min(e.empid)\n"
    + "from hr.emps as e\n"
    + "join hr.depts as d\n"
    + "  on e.deptno = d.deptno\n"
    + "group by d.deptno\n"
    + "having count(*) > 1");
print(resultSet);
resultSet.close();
statement.close();
connection.close();
```

看完这段代码，你可能会有疑惑：数据库相关的代码在哪里？传统意义上的数据库连接方式\(比如jdbc连接，需要有配置信息等\)在这段代码中完全没有体现出来。而真正的连接在 `ReflectiveSchema.create`创建了一个Java对象作为schema并且它的集合字段`emps`和`depts`作为表 之前一直是空的。

从这段代码中我们能够看出：Calcite的目的并不是拥有数据，甚至都没有独特的数据格式。上面的例子使用的是内存数据集，并且利用`linq4j`包来对数据做`groupBy`和`join`操作。Calcite也可以利用其他的数据格式来执行数据，例如是用JDBC。第一个例子中，我们可以做如下替换：

将

```
Schema schema = ReflectiveSchema.create(calciteConnection, rootSchema, "hr", new HrSchema());
```

替换成：

```
Class.forName("com.mysql.jdbc.Driver");
BasicDataSource dataSource = new BasicDataSource();
dataSource.setUrl("jdbc:mysql://localhost");
dataSource.setUsername("username");
dataSource.setPassword("password");
Schema schema = JdbcSchema.create(rootSchema, "hr", dataSource, null, "name");
```

calcite将是用jdbc方式执行同样的查询。回到应用本身，数据和api都是一样的，但是背后的实现是完全不一样。Calcite使用优化器规则将JOIN和GROUP BY操作推送到源数据库。

内存方式和jdbc方式\(上面的两个例子\)仅仅是两个相似的例子。Cancite能够处理任何一种数据源和数据格式。如果你要添加一种新的数据源，你需要编写一个适配器，告知Calcite该数据源中的哪些集合在逻辑上可以被认为是“表”。

### 编写一个适配器

Calcite工程的/example/csv目录中的子项目中提供了一个CSV适配器，这个适配器可完全应用在实际的应用中，但是它又是足够的简单以至于能够作为一个案例提供给你，供你自己编写适配器做参考。

tutorial 章节详细讲述了怎么使用CSV适配器并且编写其他的数据源适配器

howto 章节详细讲述了其他适配器的用法和正常情况下Calcite的用法

### 功能

可以支持以下功能:
* 查询解析,校验,优化
* 从json文件中中创建模型
* 支持大多数标准函数和聚合函数
* Linq4j front-end
* 支持SQL功能
* 本地或者远程JDBC,使用Avatica实现
* 支持适配器

