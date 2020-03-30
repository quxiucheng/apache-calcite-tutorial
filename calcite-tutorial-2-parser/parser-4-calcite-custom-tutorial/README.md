# Calcite自定义SQL解析

## 常用方法和配置

### config.fmpp
calcite 模板配置
```
data: {
  parser: {
    # Generated parser implementation package and class name.
    # 生成解析器实现类包和名称
    # 包名
    package: "com.github.quxiucheng.tutorial.parser.custom",
    # 实体类名
    class: "ExtensionSqlParserImpl",

    # List of import statements.
    # Example. "org.apache.calcite.sql.*", "java.util.List".
    # 导入处理的语句
    imports: [
    ]
    # List of new keywords. Example: "DATABASES", "TABLES". If the keyword is not a reserved
    # keyword add it to 'nonReservedKeywords' section.
    # 新关键字列表。示例:“DATABASES”、“TABLES”。如果关键字不是一个保留关键字，将其添加到“无保留关键字”部分。
    keywords: [
    ]
    
    # List of keywords from "keywords" section that are not reserved.
    # “keywords”部分中未保留的关键字列表。
    nonReservedKeywords: [
        "A"
        "ABSENT"
        "ABSOLUTE"
        "ACTION"
        "ADA"
        "ADD"
        "ADMIN"
        "AFTER"
        "ALWAYS"
        "APPLY"
        "ASC"
        "ASSERTION"
        "ASSIGNMENT"
        "ATTRIBUTE"
        "ATTRIBUTES"
        "BEFORE"
        "BERNOULLI"
        "BREADTH"
        "C"
        "CASCADE"
        "CATALOG"
        "CATALOG_NAME"
        "CENTURY"
        "CHAIN"
        "CHARACTER_SET_CATALOG"
        "CHARACTER_SET_NAME"
        "CHARACTER_SET_SCHEMA"
        "CHARACTERISTICS"
        "CHARACTERS"
        "CLASS_ORIGIN"
        "COBOL"
        "COLLATION"
        "COLLATION_CATALOG"
        "COLLATION_NAME"
        "COLLATION_SCHEMA"
        "COLUMN_NAME"
        "COMMAND_FUNCTION"
        "COMMAND_FUNCTION_CODE"
        "COMMITTED"
        "CONDITION_NUMBER"
        "CONDITIONAL"
        "CONNECTION"
        "CONNECTION_NAME"
        "CONSTRAINT_CATALOG"
        "CONSTRAINT_NAME"
        "CONSTRAINT_SCHEMA"
        "CONSTRAINTS"
        "CONSTRUCTOR"
        "CONTINUE"
        "CURSOR_NAME"
        "DATA"
        "DATABASE"
        "DATETIME_INTERVAL_CODE"
        "DATETIME_INTERVAL_PRECISION"
        "DECADE"
        "DEFAULTS"
        "DEFERRABLE"
        "DEFERRED"
        "DEFINED"
        "DEFINER"
        "DEGREE"
        "DEPTH"
        "DERIVED"
        "DESC"
        "DESCRIPTION"
        "DESCRIPTOR"
        "DIAGNOSTICS"
        "DISPATCH"
        "DOMAIN"
        "DOW"
        "DOY"
        "DYNAMIC_FUNCTION"
        "DYNAMIC_FUNCTION_CODE"
        "ENCODING"
        "EPOCH"
        "ERROR"
        "EXCEPTION"
        "EXCLUDE"
        "EXCLUDING"
        "FINAL"
        "FIRST"
        "FOLLOWING"
        "FORMAT"
        "FORTRAN"
        "FOUND"
        "FRAC_SECOND"
        "G"
        "GENERAL"
        "GENERATED"
        "GEOMETRY"
        "GO"
        "GOTO"
        "GRANTED"
        "HIERARCHY"
        "IMMEDIATE"
        "IMMEDIATELY"
        "IMPLEMENTATION"
        "INCLUDING"
        "INCREMENT"
        "INITIALLY"
        "INPUT"
        "INSTANCE"
        "INSTANTIABLE"
        "INVOKER"
        "ISODOW"
        "ISOYEAR"
        "ISOLATION"
        "JAVA"
        "JSON"
        "K"
        "KEY"
        "KEY_MEMBER"
        "KEY_TYPE"
        "LABEL"
        "LAST"
        "LENGTH"
        "LEVEL"
        "LIBRARY"
        "LOCATOR"
        "M"
        "MAP"
        "MATCHED"
        "MAXVALUE"
        "MICROSECOND"
        "MESSAGE_LENGTH"
        "MESSAGE_OCTET_LENGTH"
        "MESSAGE_TEXT"
        "MILLISECOND"
        "MILLENNIUM"
        "MINVALUE"
        "MORE_"
        "MUMPS"
        "NAME"
        "NAMES"
        "NANOSECOND"
        "NESTING"
        "NORMALIZED"
        "NULLABLE"
        "NULLS"
        "NUMBER"
        "OBJECT"
        "OCTETS"
        "OPTION"
        "OPTIONS"
        "ORDERING"
        "ORDINALITY"
        "OTHERS"
        "OUTPUT"
        "OVERRIDING"
        "PAD"
        "PARAMETER_MODE"
        "PARAMETER_NAME"
        "PARAMETER_ORDINAL_POSITION"
        "PARAMETER_SPECIFIC_CATALOG"
        "PARAMETER_SPECIFIC_NAME"
        "PARAMETER_SPECIFIC_SCHEMA"
        "PARTIAL"
        "PASCAL"
        "PASSING"
        "PASSTHROUGH"
        "PAST"
        "PATH"
        "PLACING"
        "PLAN"
        "PLI"
        "PRECEDING"
        "PRESERVE"
        "PRIOR"
        "PRIVILEGES"
        "PUBLIC"
        "QUARTER"
        "READ"
        "RELATIVE"
        "REPEATABLE"
        "REPLACE"
        "RESTART"
        "RESTRICT"
        "RETURNED_CARDINALITY"
        "RETURNED_LENGTH"
        "RETURNED_OCTET_LENGTH"
        "RETURNED_SQLSTATE"
        "RETURNING"
        "ROLE"
        "ROUTINE"
        "ROUTINE_CATALOG"
        "ROUTINE_NAME"
        "ROUTINE_SCHEMA"
        "ROW_COUNT"
        "SCALAR"
        "SCALE"
        "SCHEMA"
        "SCHEMA_NAME"
        "SCOPE_CATALOGS"
        "SCOPE_NAME"
        "SCOPE_SCHEMA"
        "SECTION"
        "SECURITY"
        "SELF"
        "SEQUENCE"
        "SERIALIZABLE"
        "SERVER"
        "SERVER_NAME"
        "SESSION"
        "SETS"
        "SIMPLE"
        "SIZE"
        "SOURCE"
        "SPACE"
        "SPECIFIC_NAME"
        "SQL_BIGINT"
        "SQL_BINARY"
        "SQL_BIT"
        "SQL_BLOB"
        "SQL_BOOLEAN"
        "SQL_CHAR"
        "SQL_CLOB"
        "SQL_DATE"
        "SQL_DECIMAL"
        "SQL_DOUBLE"
        "SQL_FLOAT"
        "SQL_INTEGER"
        "SQL_INTERVAL_DAY"
        "SQL_INTERVAL_DAY_TO_HOUR"
        "SQL_INTERVAL_DAY_TO_MINUTE"
        "SQL_INTERVAL_DAY_TO_SECOND"
        "SQL_INTERVAL_HOUR"
        "SQL_INTERVAL_HOUR_TO_MINUTE"
        "SQL_INTERVAL_HOUR_TO_SECOND"
        "SQL_INTERVAL_MINUTE"
        "SQL_INTERVAL_MINUTE_TO_SECOND"
        "SQL_INTERVAL_MONTH"
        "SQL_INTERVAL_SECOND"
        "SQL_INTERVAL_YEAR"
        "SQL_INTERVAL_YEAR_TO_MONTH"
        "SQL_LONGVARBINARY"
        "SQL_LONGVARNCHAR"
        "SQL_LONGVARCHAR"
        "SQL_NCHAR"
        "SQL_NCLOB"
        "SQL_NUMERIC"
        "SQL_NVARCHAR"
        "SQL_REAL"
        "SQL_SMALLINT"
        "SQL_TIME"
        "SQL_TIMESTAMP"
        "SQL_TINYINT"
        "SQL_TSI_DAY"
        "SQL_TSI_FRAC_SECOND"
        "SQL_TSI_HOUR"
        "SQL_TSI_MICROSECOND"
        "SQL_TSI_MINUTE"
        "SQL_TSI_MONTH"
        "SQL_TSI_QUARTER"
        "SQL_TSI_SECOND"
        "SQL_TSI_WEEK"
        "SQL_TSI_YEAR"
        "SQL_VARBINARY"
        "SQL_VARCHAR"
        "STATE"
        "STATEMENT"
        "STRUCTURE"
        "STYLE"
        "SUBCLASS_ORIGIN"
        "SUBSTITUTE"
        "TABLE_NAME"
        "TEMPORARY"
        "TIES"
        "TIMESTAMPADD"
        "TIMESTAMPDIFF"
        "TOP_LEVEL_COUNT"
        "TRANSACTION"
        "TRANSACTIONS_ACTIVE"
        "TRANSACTIONS_COMMITTED"
        "TRANSACTIONS_ROLLED_BACK"
        "TRANSFORM"
        "TRANSFORMS"
        "TRIGGER_CATALOG"
        "TRIGGER_NAME"
        "TRIGGER_SCHEMA"
        "TYPE"
        "UNBOUNDED"
        "UNCOMMITTED"
        "UNCONDITIONAL"
        "UNDER"
        "UNNAMED"
        "USAGE"
        "USER_DEFINED_TYPE_CATALOG"
        "USER_DEFINED_TYPE_CODE"
        "USER_DEFINED_TYPE_NAME"
        "USER_DEFINED_TYPE_SCHEMA"
        "UTF8"
        "UTF16"
        "UTF32"
        "VERSION"
        "VIEW"
        "WEEK"
        "WRAPPER"
        "WORK"
        "WRITE"
        "XML"
        "ZONE"
    ]
    
    # List of additional join types. Each is a method with no arguments.
    # Example: LeftSemiJoin()
    # 其他连接类型的列表。每个方法都是没有参数的方法。
    joinTypes: [
    ]
    
    # List of methods for parsing custom SQL statements.
    # Return type of method implementation should be 'SqlNode'.
    # Example: SqlShowDatabases(), SqlShowTables().
    # 用于解析自定义SQL语句的方法列表。
    statementParserMethods: [
    ]
    
    # List of methods for parsing custom literals.
    # Return type of method implementation should be "SqlNode".
    # Example: ParseJsonLiteral().
    # 解析自定义文本的方法列表
    literalParserMethods: [
    ]
    
    # List of methods for parsing custom data types.
    # Return type of method implementation should be "SqlIdentifier".
    # 用于解析自定义数据类型的方法列表。
    dataTypeParserMethods: [
    ]
    
    # List of methods for parsing extensions to "ALTER <scope>" calls.
    # Each must accept arguments "(SqlParserPos pos, String scope)".
    # Example: "SqlUploadJarNode"
    # 每个都必须接受参数 "(SqlParserPos pos, String scope)".
    # 解析扩展到“ALTER ”调用的方法。
    alterStatementParserMethods: [
    
    ]
    
    # List of methods for parsing extensions to "CREATE [OR REPLACE]" calls.
    # Each must accept arguments "(SqlParserPos pos, boolean replace)".
    # 解析扩展以"CREATE [OR REPLACE]"调用的方法列表。
    # 每个都必须接受参数 "(SqlParserPos pos, String scope)".
    createStatementParserMethods: [
    ]
    
    # List of methods for parsing extensions to "DROP" calls.
    # Each must accept arguments "(SqlParserPos pos)".
    # 解析扩展到“DROP”调用的方法列表。
    # 每个都必须接受参数 "(SqlParserPos pos)".
    dropStatementParserMethods: [
    ]
    
    # List of files in @includes directory that have parser method
    # implementations for parsing custom SQL statements, literals or types
    # given as part of "statementParserMethods", "literalParserMethods" or
    # "dataTypeParserMethods".
    # @includes目录中具有解析器方法的文件列表
    # 解析自定义SQL语句、文本或类型的实现
    # 作为“statementParserMethods”、“literalParserMethods”或“dataTypeParserMethods”的一部分给出。
    implementationFiles: [
    "parserImpls.ftl"
    ]
    
    includeCompoundIdentifier: true
    includeBraces: true
    includeAdditionalDeclarations: false
    
  }
}
# freemarker模板的位置
freemarkerLinks: {
    includes: includes/
}
```

### 配置和Parser.jj文件结合说明

#### package,class,imports
主要是负责导入包,设置编译package目录,编译的类名
![](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/md/resource/class.png)

#### keywords
定义关键字
![](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/md/resource/keywords.png)

#### nonReservedKeywords
keywords定义关键字中,保留的关键字
![](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/md/resource/nonReservedKeywords.png)

#### joinTypes
join类型
![](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/md/resource/joinTypes.png)

#### statementParserMethods
解析自定义SQL语句的方法列表,必须实现`SqlNode`
![](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/md/resource/statementParserMethods.png)

#### literalParserMethods
解析自定义文字的方法列表,必须实现`SqlNode`
![](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/md/resource/literalParserMethods.png)

#### dataTypeParserMethods
解析自定义数据类型的方法列表,必须实现`SqlIdentifier`
![](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/md/resource/dataTypeParserMethods.png)

#### alterStatementParserMethods
解析自定义alter语句,必须有构造方法`(SqlParserPos pos, String scope)`
![](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/md/resource/alterStatementParserMethods.png)

#### createStatementParserMethods
解析自定义create语句,必须有构造方法`(SqlParserPos pos, boolean replace)`
![](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/md/resource/createStatementParserMethods.png)

#### dropStatementParserMethods
解析自定义drop语句,必须有构造方法`(SqlParserPos pos)`
![](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/md/resource/dropStatementParserMethods.png)

#### implementationFiles
模板文件
![](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/md/resource/implementationFiles.png)

#### includeCompoundIdentifier
是否包含CompoundIdentifier解析
![](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/md/resource/includeCompoundIdentifier.png)

#### includeBraces
![](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/md/resource/includeBraces.png)

#### includeAdditionalDeclarations
![](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/md/resource/includeAdditionalDeclarations.png)

### Parser.jj常用方法
[javacc教程](/calcite-tutorial-2-parser/parser-2-javacc-tutorial)

#### getPos()
获取当前token的配置,自定义解析的时候使用

#### StringLiteral
主要解析是语句中的string类型的字段
```sql
select * from table where a='string'
```
#### Identifier
解析Identifier字段,返回string类型
```sql
select SqlIdentifier from table;
```
#### SimpleIdentifier
解析Identifier字段,返回`Identifier`
```sql
select SqlIdentifier from table;
```

#### CompoundIdentifier
解析Identifier字段,返回`Identifier`
```sql
select Compound.SqlIdentifier from table;
select SqlIdentifier from table;
```

#### 更多示例代码

[更多示例代码-JavaCC](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/src/main/codegen/includes/parserImplsSample.ftl)

[更多示例代码-Java代码](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/src/main/java/com/github/quxiucheng/calcite/parser/tutorial/sample)


### 其他常见类
[Calcite SQL解析](/calcite-tutorial-2-parser/parser-3-calcite-tutorial)


## 自定义SQL

### 自定义SQL语法
```sql
create function function_name as class_name
[method]
[with] [(key=value)]
```

```sql
# 创建函数关键字
create function 
# 函数名
  hr.custom_function 
# as关键字
as 
# 类名称
  'com.github.quxiucheng.calcite.func.CustomFunction' 
# 可选 方法名称
method 'eval' 
# 可选 备注信息
comment 'comment' 
# 可选 附件变量
property ('a'='b','c'='1')
```

### 定义解析结果类
SqlCreateFunction.java
```java

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;

import java.util.List;


public class SqlCreateFunction extends SqlCall {

    private SqlNode functionName;

    private String className;

    private SqlNodeList properties;

    private String methodName;

    private String comment;


    public SqlCreateFunction(SqlParserPos pos,
                             SqlNode functionName, String className, String methodName, String comment,
                             SqlNodeList properties) {

        super(pos);
        this.functionName = functionName;
        this.className = className;
        this.properties = properties;
        this.methodName = methodName;
    }

    public SqlNode getFunctionName() {
        return functionName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public SqlNodeList getProperties() {
        return properties;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public SqlOperator getOperator() {
        return null;
    }

    @Override
    public List<SqlNode> getOperandList() {
        return null;
    }

    @Override
    public SqlKind getKind() {
        return SqlKind.OTHER_DDL;
    }

    @Override
    public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
        writer.keyword("CREATE");
        writer.keyword("FUNCTION");
        functionName.unparse(writer, leftPrec, rightPrec);
        writer.keyword("AS");
        writer.print("'" + className + "'");
        if (methodName != null) {
            writer.newlineAndIndent();
            writer.keyword("METHOD");
            writer.print("'" + methodName + "'");
        }
        if (properties != null) {
            writer.newlineAndIndent();
            writer.keyword("PROPERTY");
            SqlWriter.Frame propertyFrame = writer.startList("(", ")");
            for (SqlNode property : properties) {
                writer.sep(",", false);
                writer.newlineAndIndent();
                writer.print("  ");
                property.unparse(writer, leftPrec, rightPrec);
            }
            writer.newlineAndIndent();
            writer.endList(propertyFrame);
        }
       if (comment != null) {
            writer.newlineAndIndent();
            writer.keyword("COMMENT");
            writer.print("'" + COMMENT + "'");
        }
    }
}
```

解析key=value语句
SqlProperty.java
```java

import com.google.common.collect.ImmutableList;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSpecialOperator;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.util.NlsString;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class SqlProperty extends SqlCall {

    /**
     * 定义特殊操作符
     */
    protected static final SqlOperator OPERATOR =
            new SqlSpecialOperator("Property", SqlKind.OTHER);

    private SqlNode key;

    private SqlNode value;

    public SqlProperty(SqlParserPos pos, SqlNode key, SqlNode value) {
        super(pos);
        this.key = requireNonNull(key, "Property key is missing");
        this.value = requireNonNull(value, "Property value is missing");
    }

    @Override
    public SqlOperator getOperator() {
        return OPERATOR;
    }

    @Override
    public List<SqlNode> getOperandList() {
        return ImmutableList.of(key, value);
    }

    @Override
    public SqlKind getKind() {
        return SqlKind.OTHER;
    }

    @Override
    public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
        key.unparse(writer, leftPrec, rightPrec);
        writer.keyword("=");
        value.unparse(writer, leftPrec, rightPrec);
    }

    public SqlNode getKey() {
        return key;
    }

    public SqlNode getValue() {
        return value;
    }

    public String getKeyString() {
        return key.toString();
    }

    public String getValueString() {
        return ((NlsString) SqlLiteral.value(value)).getValue();
    }

}
```
### 语法模板 parserImpls.ftl
```
// 创建函数
SqlNode SqlCreateFunction() :
{
    // 声明变量
    SqlParserPos createPos;
    SqlParserPos propertyPos;
    SqlNode functionName = null;
    String className = null;
    String methodName = null;
    String comment = null;
    SqlNodeList properties = null;
}
{
    // create 关键字
    <CREATE>
    {
        // 获取当前token的行列位置
        createPos = getPos();
     }
    // function 关键字
    <FUNCTION>
    // 函数名
    functionName = CompoundIdentifier()
    // as关键字
    <AS>
    // 类名
    { className = StringLiteralValue(); }
    // if语句
    [
        // method关键字
        <METHOD>
        {
            // 方法名称
            methodName = StringLiteralValue();
        }
    ]
    // if
    [
        // property 关键字,设置初始化变量
        <PROPERTY>
            {
                // 获取关键字位置
                propertyPos = getPos();
                SqlNode property;
                properties = new SqlNodeList(propertyPos);
            }
        <LPAREN>
        [
            property = PropertyValue()
            {
                properties.add(property);
            }
            (
                <COMMA>
                {
                    property = PropertyValue();
                    properties.add(property);
                }
            )*
        ]
        <RPAREN>
    ]
    // if
    [
        <COMMENT> {
            // 备注
            comment = StringLiteralValue();
        }
    ]

    {
        return new SqlCreateFunction(createPos, functionName, className, methodName, comment, properties);
    }
}

JAVACODE String StringLiteralValue() {
    SqlNode sqlNode = StringLiteral();
    return ((NlsString) SqlLiteral.value(sqlNode)).getValue();
}



/**
 * 解析SQL中的key=value形式的属性值
 */
SqlNode PropertyValue() :
{
    SqlNode key;
    SqlNode value;
    SqlParserPos pos;
}
{
    key = StringLiteral()
    { pos = getPos(); }
    <EQ> value = StringLiteral()
    {
        return new SqlProperty(getPos(), key, value);
    }
}
```
### 定义class 和 imports
```
    package: "com.github.quxiucheng.calcite.parser.tutorial"
    
    class: "CustomSqlParserImpl",

    imports: [
        "com.github.quxiucheng.calcite.parser.tutorial.SqlCreateFunction"
        "com.github.quxiucheng.calcite.parser.tutorial.sample.statements.model.*"
    ]
```

### 定义关键字 config.fmpp
```
    keywords: [
        "PARAMS"
        "COMMENT"
        "PROPERTY" 
    ]
```

### 定义自定义解析 statementParserMethods
```
  statementParserMethods: [
        "SqlCreateFunction()"
    ]
```

### 测试类
```java

import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.dialect.OracleSqlDialect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;

public class SqlCreateFunctionMain {
    public static void main(String[] args) throws SqlParseException {
        // 解析配置 - mysql设置
        SqlParser.Config mysqlConfig = SqlParser.configBuilder()
                // 定义解析工厂
                .setParserFactory(CustomSqlParserImpl.FACTORY)
                .setLex(Lex.MYSQL)
                .build();
        // 创建解析器
        SqlParser parser = SqlParser.create("", mysqlConfig);
        // Sql语句
        String sql = "create function " +
                "hr.custom_function as 'com.github.quxiucheng.calcite.func.CustomFunction' " +
                "method 'eval'  " +
                "property ('a'='b','c'='1') ";
        // 解析sql
        SqlNode sqlNode = parser.parseQuery(sql);
        // 还原某个方言的SQL
        System.out.println(sqlNode.toSqlString(OracleSqlDialect.DEFAULT));
    }
}

```








