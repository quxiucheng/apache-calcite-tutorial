# Calcite校验

## 常见类
### SqlValidator
验证SQL语句的解析树

#### SqlValidatorImpl
SqlValidator默认实现类,校验的核心类,整体代码最为复杂

### SqlValidatorScope
SQL校验名称解析范围
```
SqlValidatorScope describes the tables and columns accessible at a particular point in the query
```
代表了在某一个程序运行点，当前可见的字段名和表名

例子:
Sql:
```sql
SELECT expr1
FROM t1,
    t2,
    (SELECT expr2 FROM t3) AS q3
WHERE c1 IN (SELECT expr3 FROM t4)
ORDER BY expr4
```

Scope:
表示表达式可能取的数据范围,scope解析为以下四个表达式
```
expr1 数据来源于 t1, t2, q3
expr2 数据来源于 t3
expr3 数据来源于 t4, t1, t2
expr4 数据来源于 t1, t2, q3 加上（取决于方言）SELECT子句中定义的任何别名
```

具体继承类如下:
```
SqlValidatorScope (org.apache.calcite.sql.validate) 校验的scope
    AggregatingScope (org.apache.calcite.sql.validate) 聚合scope
        AggregatingSelectScope (org.apache.calcite.sql.validate) select聚合scope
    DelegatingScope (org.apache.calcite.sql.validate) 将所有的操作委派给父类,继承类为各个类型的SQL的Scope
        OrderByScope (org.apache.calcite.sql.validate) 
        CatalogScope (org.apache.calcite.sql.validate)
        ListScope (org.apache.calcite.sql.validate)
            JoinScope (org.apache.calcite.sql.validate)
            MatchRecognizeScope (org.apache.calcite.sql.validate)
            TableScope (org.apache.calcite.sql.validate)
            CollectScope (org.apache.calcite.sql.validate)
            OverScope (org.apache.calcite.sql.validate)
            SelectScope (org.apache.calcite.sql.validate)
            WithScope (org.apache.calcite.sql.validate)
            Anonymous in convertMultisets() in SqlToRelConverter (org.apache.calcite.sql2rel)
        AggregatingSelectScope (org.apache.calcite.sql.validate)
        GroupByScope (org.apache.calcite.sql.validate)
    EmptyScope (org.apache.calcite.sql.validate) 为了防止父类为空,生成一个EmptyScope
        ParameterScope (org.apache.calcite.sql.validate)
```
### SqlValidatorNamespace
SQL校验命名空间
```
a SqlValidatorNamespace is a description of a data source used in a query
```
它代表了SQL查询的数据源，它是一个逻辑上数据源，可以是一张表，也可以是一个子查询；

例子:
Sql:
```sql
SELECT expr1
FROM t1,
    t2,
    (SELECT expr2 FROM t3) AS q3
WHERE c1 IN (SELECT expr3 FROM t4)
ORDER BY expr4
```

Namespace:
表示表,或者一个子查询,该SQL可以解析成以下4个namespace
```
t1
t2
(SELECT expr2 FROM t3) AS q3
(SELECT expr3 FROM t4)
```

具体继承如下:
``` 
SqlValidatorNamespace (org.apache.calcite.sql.validate)
    DelegatingNamespace (org.apache.calcite.sql.validate) SqlValidatorNamespace的实现，它将所有方法委托给底层对象。无人使用..
    AbstractNamespace (org.apache.calcite.sql.validate) 命名空间抽象类,一下继承类为各个类型SQL的namespace
        IdentifierNamespace (org.apache.calcite.sql.validate) 
        UnnestNamespace (org.apache.calcite.sql.validate)
        JoinNamespace (org.apache.calcite.sql.validate)
        FieldNamespace (org.apache.calcite.sql.validate)
        ParameterNamespace (org.apache.calcite.sql.validate)
        TableConstructorNamespace (org.apache.calcite.sql.validate)
        MatchRecognizeNamespace (org.apache.calcite.sql.validate)
        SetopNamespace (org.apache.calcite.sql.validate)
        AliasNamespace (org.apache.calcite.sql.validate)
        WithNamespace (org.apache.calcite.sql.validate)
        CollectNamespace (org.apache.calcite.sql.validate)
        TableNamespace (org.apache.calcite.sql.validate)
        SchemaNamespace (org.apache.calcite.sql.validate)
        ProcedureNamespace (org.apache.calcite.sql.validate)
        SelectNamespace (org.apache.calcite.sql.validate)
        WithItemNamespace (org.apache.calcite.sql.validate)
```
## SqlValidatorImpl执行过程

### validate()
`SqlValidatorImpl`的`validate()`是校验的入口函数，具体实现方法如下：
```java
public SqlNode validate(SqlNode topNode) {
    // root 对应的 Scope
    SqlValidatorScope scope = new EmptyScope(this);
    scope = new CatalogScope(scope, ImmutableList.of("CATALOG"));
    // 语法检查
    final SqlNode topNode2 = validateScopedExpression(topNode, scope);
    // 节点的类型
    final RelDataType type = getValidatedNodeType(topNode2);
    // 取消编译器告警-没用的代码
    Util.discard(type);
    return topNode2;
  }
```

### validateScopedExpression()
`validate()`函数最主要方法为`validateScopedExpression()`,这个函数负责检查SQL语法,具体实现方法如下:
```java
private SqlNode validateScopedExpression(
      SqlNode topNode,
      SqlValidatorScope scope) {
    // 将其标准化，便于后面的逻辑计划优化,主要做以下转换
    // SqlOrderBy -> SqlSelect
    // SqlDelete -> SqlSelect
    // SqlMerge -> SqlSelect
    // SqlUpdate -> SqlSelect
    // VALUES函数 -> SqlSelect
    // explicit table类似'select * from (TABLE t)'-> SqlSelect
    SqlNode outermostNode = performUnconditionalRewrites(topNode, false);
    cursorSet.add(outermostNode);
    top = outermostNode;
    TRACER.trace("After unconditional rewrite: {}", outermostNode);
    // 注册scope和namespace
    if (outermostNode.isA(SqlKind.TOP_LEVEL)) {
      registerQuery(scope, null, outermostNode, outermostNode, null, false);
    }
    // 验证，调用 SqlNode 的 validate 方法
    outermostNode.validate(this, scope);
    if (!outermostNode.isA(SqlKind.TOP_LEVEL)) {
      // 推断出类型
      deriveType(scope, outermostNode);
    }
    TRACER.trace("After validation: {}", outermostNode);
    return outermostNode;
  }
```

最主要的校验方法执行逻辑如下:
1. 将非标准化的SQL转化为SqlSelect标准的SQL,并不修改原有逻辑,例如`SqlOrderBy`转换为`SqlSelect`
2. 生成SQL的`scope`和`namespace`用于日后校验使用
3. `deriveType`用户生成对应的`AST`中的类型
4. 返回标准化的`SqlNode`


### performUnconditionalRewrites()
![](/calcite-tutorial-4-validator/validator-1-calcite-validator/md/resource/performUnconditionalRewrites.png)
该方法将SQL进行标准化

### registerQuery()
![](/calcite-tutorial-4-validator/validator-1-calcite-validator/md/resource/registerQuery.png)
生成SQL的`scope`和`namespace`

### outermostNode.validate()
核心校验validate方法,该方法主要是调用`SqlNode和子类的`的validate方法  
主要以`SqlSelect`的方法来`validate()`来说明
```
public void validate(SqlValidator validator, SqlValidatorScope scope) {
    validator.validateQuery(this, scope, validator.getUnknownType());
}
```
该方法主要调用`validator.validateQuery()`方法
```java
public void validateQuery(SqlNode node, SqlValidatorScope scope,
      RelDataType targetRowType) {
    final SqlValidatorNamespace ns = getNamespace(node, scope);
    if (node.getKind() == SqlKind.TABLESAMPLE) {
      List<SqlNode> operands = ((SqlCall) node).getOperandList();
      SqlSampleSpec sampleSpec = SqlLiteral.sampleValue(operands.get(1));
      if (sampleSpec instanceof SqlSampleSpec.SqlTableSampleSpec) {
        validateFeature(RESOURCE.sQLFeature_T613(), node.getParserPosition());
      } else if (sampleSpec
          instanceof SqlSampleSpec.SqlSubstitutionSampleSpec) {
        validateFeature(RESOURCE.sQLFeatureExt_T613_Substitution(),
            node.getParserPosition());
      }
    }
    // 校验namespace
    validateNamespace(ns, targetRowType);
    switch (node.getKind()) {
    case EXTEND:
      // Until we have a dedicated namespace for EXTEND
      deriveType(scope, node);
    }
    if (node == top) {
      validateModality(node);
    }
    validateAccess(
        node,
        ns.getTable(),
        SqlAccessEnum.SELECT);
  }
```
该函数最主要方法为`validateNamespace`,校验Sql中命名空间是否正确

### validateNamespace()
调用namespace中的validate方法校验namespace是否正确
```java
 protected void validateNamespace(final SqlValidatorNamespace namespace,
      RelDataType targetRowType) {
    // 调用namespace中validate
    namespace.validate(targetRowType);
    if (namespace.getNode() != null) {
      // 设置节点类型
      setValidatedNodeType(namespace.getNode(), namespace.getType());
    }
  }
```

### namespace.validate()
以SelectNamespace为例子说明namespace校验

`AbstractNamespace`为namespace的抽象类,校验namespace的入口类
```java
 public final void validate(RelDataType targetRowType) {
    switch (status) {
    case UNVALIDATED:
      try {
        status = SqlValidatorImpl.Status.IN_PROGRESS;
        Preconditions.checkArgument(rowType == null,
            "Namespace.rowType must be null before validate has been called");
        // 模板方法,调用子类的validateImpl校验
        RelDataType type = validateImpl(targetRowType);
        Preconditions.checkArgument(type != null,
            "validateImpl() returned null");
        setType(type);
      } finally {
        status = SqlValidatorImpl.Status.VALID;
      }
      break;
    case IN_PROGRESS:
      throw new AssertionError("Cycle detected during type-checking");
    case VALID:
      break;
    default:
      throw Util.unexpected(status);
    }
  }
```

`SelectNamespace`中调用`validator.validateSelect()`校验
```java
  public RelDataType validateImpl(RelDataType targetRowType) {
    validator.validateSelect(select, targetRowType);
    return rowType;
  }
```

### validator.validateSelect()
![](/calcite-tutorial-4-validator/validator-1-calcite-validator/md/resource/validateSelect.png)
调用具体的validate方法

### 执行具体时序如下
![](/calcite-tutorial-4-validator/validator-1-calcite-validator/md/resource/validate.jpg)

## 代码例子
```java

import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.Lex;
import org.apache.calcite.jdbc.CalcitePrepare;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.server.CalciteServerStatement;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.dialect.OracleSqlDialect;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.sql.validate.SqlMoniker;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorNamespace;
import org.apache.calcite.sql.validate.SqlValidatorScope;
import org.apache.calcite.sql.validate.SqlValidatorUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ValidateSample {

    public static void main(String[] args) throws SqlParseException, SQLException {

        Properties info = new Properties();
        info.put("lex", "mysql");
        String model = "calcite-tutorial-3-schema/src/main/resources/model.json";
        info.put("model", model);
        Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
        CalciteServerStatement statement = connection.createStatement().unwrap(CalciteServerStatement.class);
        CalcitePrepare.Context prepareContext = statement.createPrepareContext();
        // 解析配置 - mysql设置
        SqlParser.Config mysqlConfig = SqlParser.configBuilder().setLex(Lex.MYSQL).build();
        // 创建解析器
        SqlParser parser = SqlParser.create("", mysqlConfig);
        // Sql语句
        String sql = "select * from tutorial.user_info where id = 1 order by id";
        // 解析sql
        SqlNode sqlNode = parser.parseQuery(sql);
        // 还原某个方言的SQL
        System.out.println(sqlNode.toSqlString(OracleSqlDialect.DEFAULT));
        // sql validate（会先通过Catalog读取获取相应的metadata和namespace）
        SqlTypeFactoryImpl factory = new SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
        CalciteCatalogReader calciteCatalogReader = new CalciteCatalogReader(
                prepareContext.getRootSchema(),
                prepareContext.getDefaultSchemaPath(),
                factory,
                new CalciteConnectionConfigImpl(new Properties()));

        // 校验（包括对表名，字段名，函数名，字段类型的校验。）
        SqlValidator validator = SqlValidatorUtil.newValidator(SqlStdOperatorTable.instance(),
                calciteCatalogReader, factory, SqlConformanceEnum.DEFAULT
        );
        // 校验后的SqlNode
        SqlNode validateSqlNode = validator.validate(sqlNode);
        // scope
        SqlValidatorScope selectScope = validator.getSelectScope((SqlSelect) validateSqlNode);
        // namespace
        SqlValidatorNamespace namespace = validator.getNamespace(sqlNode);
        System.out.println(validateSqlNode);
        List<SqlMoniker> sqlMonikerList = new ArrayList<>();
        selectScope.findAllColumnNames(sqlMonikerList);
        System.out.println(selectScope);
        for (SqlMoniker sqlMoniker : sqlMonikerList) {
            System.out.println(sqlMoniker.id());
        }
        System.out.println(namespace);
        System.out.println(namespace.fieldExists("nameCC"));
    }
}
```
