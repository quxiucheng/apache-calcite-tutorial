# Calcite SQL解析

## 代码目录
如图:
![](/calcite-tutorial-2-parser/parser-3-calcite-tutorial/md/resource/parserCode.jpg)

### config.fmpp
freemarker的配置模板

### Parser.jj
JavaCC解析器

### parserImpls.ftl/compoundIdentifier.ftl
自定义JavaCC语法格式的解析SQL代码

### 生成解析器的流程
如图:
![](/calcite-tutorial-2-parser/parser-3-calcite-tutorial/md/resource/parserFmpp.jpg)


## Sql解析使用

### 解析示例代码

```java
public class SqlParserSample {
    public static void main(String[] args) throws SqlParseException {
        // Sql语句
        String sql = "select * from emps where id = 1";
        // 解析配置
        SqlParser.Config mysqlConfig = SqlParser.configBuilder().setLex(Lex.MYSQL).build();
        // 创建解析器
        SqlParser parser = SqlParser.create(sql, mysqlConfig);
        // 解析sql
        SqlNode sqlNode = parser.parseQuery();
        // 还原某个方言的SQL
        System.out.println(sqlNode.toSqlString(OracleSqlDialect.DEFAULT));
    }
}
```

### 解析流程

1. 首先生成SQL解析器`SqlParser.Config`,`SqlParser.Config`中存在获取解析工厂类`SqlParser.Config#parserFactory()`方法,可以在`SqlParser.configBuilder()`配置类中设置解析工厂
2. `SqlParserImplFactory`解析工厂中调用`getParser`方法获取解析器
3. `SqlAbstractParserImpl`抽象解析器,JavaCC中生成的解析器的父类,Calcite中默认的解析类名为`SqlParserImpl`
4. `SqlParserImpl`中,有静态字段`FACTORY`,主要是实现`SqlParserImplFactory`,并创建解析器
5. `SqlParser`调用`create`方法,从`SqlParser.Config`中获取工厂`SqlParserImplFactory`,并创建解析器
6. 调用`SqlParser#parseQuery`方法,解析SQL,最终调用`SqlAbstractParserImpl`(默认实现类`SqlParserImpl`)的`parseSqlStmtEof`或者`parseSqlExpressionEof`方法,获取解析后的抽象语法树`SqlNode`


Parser.jj 解析简单介绍
1. 调用`SqlParserImplFactory`的`SqlAbstractParserImpl getParser(Reader stream);`方法,解析获取解器,
   或者,直接调用`SqlParser#parseQuery`传入sql语句,解析器重新传入sql`parser.ReInit(new StringReader(sql));`
2. 解析器入口类`SqlAbstractParserImpl#parseSqlExpressionEof`或者`SqlAbstractParserImpl#parseSqlStmtEof`
3. Parser.jj解析SQL语句入口`SqlStmtEof()` 解析SQL语句，直到文件结束符,`SqlStmtEof()`调用`SqlStmt()`
4. `SqlStmt()`中定义各个类型的解析,例如 `SqlExplain()`(explain语句),`OrderedQueryOrExpr()`(select语句),之后解析各个关键字


## 常用类
### Span
[Span](https://calcite.apache.org/apidocs/org/apache/calcite/sql/parser/Span.html)
SqlParserPos的建造者
具体使用还不太清楚


### SqlAbstractParserImpl
[SqlAbstractParserImpl](https://calcite.apache.org/apidocs/org/apache/calcite/sql/parser/SqlAbstractParserImpl.html)
抽象解析器,Calcite所有的解析的父类,主要是设置一些解析的配置信息

### SqlParseException
[SqlParseException](https://calcite.apache.org/apidocs/org/apache/calcite/sql/parser/SqlParseException.html)
SQL解析异常

### SqlParser
[SqlParser](https://calcite.apache.org/apidocs/org/apache/calcite/sql/parser/SqlParser.html)
解析SQL语句

### SqlParserImplFactory
[SqlParserImplFactory](https://calcite.apache.org/apidocs/org/apache/calcite/sql/parser/SqlParserImplFactory.html)
解析器的工厂类接口,可以自定义解析工厂

### SqlParserPos
[SqlParserPos](https://calcite.apache.org/apidocs/org/apache/calcite/sql/parser/SqlParserPos.html)
表示SQL语句文本中已解析标记的位置

### SqlParserUtil
[SqlParserUtil](https://calcite.apache.org/apidocs/org/apache/calcite/sql/parser/SqlParserUtil.html)
SQL解析工具类

### SqlNode
SQL解析树,是所有解析的节点的父类
[SqlNode](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlNode.html)

#### SqlCall
[SqlCall](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlCall.html)

SqlCall是对操作符的调用.
操作符可以用来描述任何语法结构，因此在实践中，SQL解析树中的每个非叶节点都是某种类型的SqlCall

常用类子类
```
// update语句
SqlUpdate (org.apache.calcite.sql)
// insert语句
SqlInsert (org.apache.calcite.sql)
// case语句
SqlCase (org.apache.calcite.sql.fun)
// explain语句
SqlExplain (org.apache.calcite.sql)
// delete语句
SqlDelete (org.apache.calcite.sql)
// with 列语句，mysql不支持，oracle支持
SqlWithItem (org.apache.calcite.sql)
// merge语法，mysql不支持，oracle支持
SqlMerge (org.apache.calcite.sql)
// ddl语句中的check语句
SqlCheckConstraint (org.apache.calcite.sql.ddl)
// 保存所有的操作
SqlBasicCall (org.apache.calcite.sql)
// 模式匹配
SqlMatchRecognize (org.apache.calcite.sql)
// alter语句
SqlAlter (org.apache.calcite.sql)
// UNIQUE，PRIMARY KEY，FOREIGN KEY解析
SqlKeyConstraint (org.apache.calcite.sql.ddl)
// with语句
SqlWith (org.apache.calcite.sql)
// order by 语句
SqlOrderBy (org.apache.calcite.sql)
// DESCRIBE SCHEMA 语句
SqlDescribeSchema (org.apache.calcite.sql)
// ddl语句
SqlDdl (org.apache.calcite.sql)
// join语句
SqlJoin (org.apache.calcite.sql)
// window语句
SqlWindow (org.apache.calcite.sql)
// select语句
SqlSelect (org.apache.calcite.sql)
// 
SqlAttributeDefinition (org.apache.calcite.sql.ddl)
// DESCRIBE TABLE 语句
SqlDescribeTable (org.apache.calcite.sql)
// UNIQUE，PRIMARY KEY，FOREIGN KEY解析
SqlColumnDeclaration (org.apache.calcite.sql.ddl)
```
#### SqlLiteral
[SqlLiteral](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlLiteral.html)

常量,表示输入的常量,需要返回值,则调用`public Object getValue()`方法,或者` public <T> T getValueAs(Class<T> clazz)`获取字段值

常用子类

* SqlNumericLiteral  
[SqlNumericLiteral](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlNumericLiteral.html)
数字常量

* SqlAbstractStringLiteral
[SqlAbstractStringLiteral](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlAbstractStringLiteral.html)
字符和二进制字符串文字常量

    * SqlBinaryStringLiteral
    [SqlBinaryStringLiteral](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlBinaryStringLiteral.html)
     二进制(或十六进制)字符串。
        
    * SqlCharStringLiteral
    [SqlCharStringLiteral](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlCharStringLiteral.html)
    类型为`SqlTypeName.CHAR`的信息
    
* SqlAbstractDateTimeLiteral
[SqlAbstractDateTimeLiteral](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlAbstractDateTimeLiteral.html)
表示日期、时间或时间戳值的常量

    * SqlDateLiteral   
    [SqlDateLiteral](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlDateLiteral.html) 
    样例: `2004-10-22`
    
    * SqlTimestampLiteral  
    [SqlTimestampLiteral](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlTimestampLiteral.html)
    样例: `1969-07-21 03:15 GMT`
    
    * SqlTimeLiteral  
    [SqlTimeLiteral](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlTimeLiteral.html)
    样例: `14:33:44.567`
    
* SqlIntervalLiteral
[SqlIntervalLiteral](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlIntervalLiteral.html)
时间间隔常量
例子:
```
INTERVAL '1' SECOND
INTERVAL '1:00:05.345' HOUR
INTERVAL '3:4' YEAR TO MONTH
```

#### SqlIdentifier
[SqlIdentifier](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlIdentifier.html)
Sql中的Id标示符

#### SqlNodeList
[SqlNodeList](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlNodeList.html)
SqlNode的集合

#### SqlDataTypeSpec
[SqlDataTypeSpec](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlDataTypeSpec.html)
SQL数据类型规范.  

目前，它只支持简单的数据类型，如CHAR、VARCHAR和DOUBLE

#### SqlDynamicParam
[SqlDynamicParam](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlDynamicParam.html)
表示SQL语句中的动态参数标记


#### SqlIntervalQualifier
[SqlIntervalQualifier](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlIntervalQualifier.html)

标示区间定义
```
Examples include:

INTERVAL '1:23:45.678' HOUR TO SECOND
INTERVAL '1 2:3:4' DAY TO SECOND
INTERVAL '1 2:3:4' DAY(4) TO SECOND(4)
```

### SqlKind
[SqlKind](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlKind.html)
SqlNode类型

### SqlOperator
[SqlOperator](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlOperator.html)
Sql解析的节点类型,包括:函数,操作符(=),语法结构(case)等操作


### SqlOperatorTable
[SqlOperatorTable](https://calcite.apache.org/apidocs/org/apache/calcite/sql/SqlOperatorTable.html)
定义了一个用于枚举和查找SQL运算符(=)和函数(cast)的目录接口。

#### SqlStdOperatorTable
[SqlStdOperatorTable](https://calcite.apache.org/apidocs/org/apache/calcite/sql/fun/SqlStdOperatorTable.html)
包含标准运算符和函数的SqlOperatorTable的实现

#### OracleSqlOperatorTable
[OracleSqlOperatorTable](https://calcite.apache.org/apidocs/org/apache/calcite/sql/fun/OracleSqlOperatorTable.html)
仅包含Oracle特定功能和运算符的运算符表

## SqlParser.Config 配置信息

### 配置项
```java
public interface Config {
    /** 默认配置. */
    Config DEFAULT = configBuilder().build();

    /**
     * 最大字段长度
     */
    int identifierMaxLength();

    /**
     * 转义内 大小写转换
     */
    Casing quotedCasing();

    /**
     * 转义字符外 大小写转换
     */
    Casing unquotedCasing();

    /**
     * 转义字符符号
     */
    Quoting quoting();

    /**
     * 大小写匹配 - 在planner内生效
     */
    boolean caseSensitive();

    /**
     * sql模式
     */
    SqlConformance conformance();
    
    @Deprecated // to be removed before 2.0
    boolean allowBangEqual();

    /**
     * 解析工厂类
     */
    SqlParserImplFactory parserFactory();
}
```
### 默认配置项
```java
public static class ConfigBuilder {
    // Casing.UNCHANGED
    private Casing quotedCasing = Lex.ORACLE.quotedCasing;
    // Quoting.DOUBLE_QUOTE
    private Casing unquotedCasing = Lex.ORACLE.unquotedCasing;
    // Casing.TO_UPPER
    private Quoting quoting = Lex.ORACLE.quoting;
    // 128
    private int identifierMaxLength = DEFAULT_IDENTIFIER_MAX_LENGTH;
    // true
    private boolean caseSensitive = Lex.ORACLE.caseSensitive;
    // Calcite's default SQL behavior.
    private SqlConformance conformance = SqlConformanceEnum.DEFAULT;
    // 解析工厂类
    private SqlParserImplFactory parserFactory = SqlParserImpl.FACTORY;
}
```

