# Apache Calcite - 校验

## Calcite校验流程

## Calcite自定义规则校验

```
interface SqlValidator {

}
```

```
 SqlValidatorScope describes the tables and columns accessible at a particular point in the query
 描述了查询中特定点可访问的表和列
SqlValidatorScope (org.apache.calcite.sql.validate)
    AggregatingScope (org.apache.calcite.sql.validate)
        AggregatingSelectScope (org.apache.calcite.sql.validate)
    DelegatingScope (org.apache.calcite.sql.validate)
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
    EmptyScope (org.apache.calcite.sql.validate)
        ParameterScope (org.apache.calcite.sql.validate)
```
``` 
 a SqlValidatorNamespace is a description of a data source used in a query.     
SqlValidatorNamespace是查询中使用的数据源的描述。
SqlValidatorNamespace (org.apache.calcite.sql.validate)
    DelegatingNamespace (org.apache.calcite.sql.validate)
    AbstractNamespace (org.apache.calcite.sql.validate)
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