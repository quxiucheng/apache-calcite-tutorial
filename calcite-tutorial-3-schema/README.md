# Apache Calcite - schema

schema其实就是表的结构(行列信息),
schema相关类主要存放在`org.apache.calcite.schema`包下,
此包中的接口定义SQL验证器使用的对象，以验证SQL抽象语法树并将标识符解析为对象。

## schema - 常用类
本文只重点介绍`Schema`,`Table`,`ScheamFactory`,`TableFactory`的使用,其他类的使用会在日后文章中介绍

### Schema
Schema为表和函数的命名空间,一个schema可以包括多个子schema

常用方法

* Table getTable(String name); 根据表名称,获取表信息
* Set<String> getTableNames(); 获取全部表名称
* Collection<Function> getFunctions(String name); 获取function信息
* Set<String> getFunctionNames(); 获取所有function名称
* Schema getSubSchema(String name); 获取子schema
* Set<String> getSubSchemaNames(); 获取所有子schema的名称


#### AbstractSchema
Schema的默认实现类,实现了部分方法
用户自定义的schema一般基于这个类实现

#### SchemaPlus
是`Schema`的包装类,用户使用的时候不需要实现这个类的接口

### SchemaFactory
Schema的工厂类,模式工厂允许您在模型文件中包含自定义模式。
例如从一个json文件中共创建schema
用户一般基于这个类创建为自定义工厂

### Table
表信息

* RelDataType getRowType(RelDataTypeFactory typeFactory); 获取表的行类型
* Statistic getStatistic(); 获取表的信息
* Schema.TableType getJdbcTableType();  获取jdbc的tableType
* boolean isRolledUp(String column); 给定列是否可以累加
* boolean rolledUpColumnValidInsideAgg(String column, SqlCall call, SqlNode parent, CalciteConnectionConfig config); 
给定列是否可以使用聚合函数 
   
#### AbstractTable
`Table`接口的默认抽象类
用户一般看情况,是否基于这个类来实现,或者基于这个类的子类实现

#### ScannableTable
可以在不创建中间关系表达式的情况下扫描的表。

#### QueryableTable
可以将`Table`转换为`Queryable`

#### TranslatableTable
将`Table`翻译成`RelNode`

#### FilterableTable
将过滤器传递进入表中,从而可以减少部分计算逻辑

#### ProjectableFilterableTable
将过滤器和投影传递进入表中,从而可以减少部分计算路基

#### StreamableTable
将表转换成一个流

#### AbstractQueryableTable
`AbstractTable`和`QueryableTable`的抽象类


### TableFactory
Table工厂类,模式工厂允许您在模型文件中包含自定义模式。
例如从一个json文件中共创建table
用户一般基于这个类创建为自定义工厂

### Statistic
表的统计系统

### Function
函数

#### ScalarFunction
带返回值的函数

#### TableFunction
执行期间的函数信息

### FunctionParameter
函数参数

# Apache Calcite - model

`org.apache.calcite.model`
提供JSON格式的模型文件或者YAML格式的模型的解析
用户可以通过实现`SchemaFactory`或`TableFactory`定义自己的模式

## JsonXXX 
配置信息的解析对应的实体类

## ModelHandler
主要解析json和yaml文件
calcite主要通过driver建立连接时解析对应的schema和table.

Driver类调用`org.apache.calcite.jdbc.Driver.createHandler`,创建ModelHandler解析schema和table,之后将schema和table设置回connect

# 具体实现代码

列信息
```java
/**
 * 列信息
 * @author quxiucheng
 * @date 2019-04-26 11:18:00
 */
@Data
public class TutorialColumn implements Serializable {
    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段类型名称
     */
    private String type;

}
``` 

自定义table
```java

import lombok.Data;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义table
 * @author quxiucheng
 * @date 2019-04-26 11:17:00
 */
@Data
public class TutorialTable extends AbstractTable implements Serializable {
    /**
     * 表名称
     */
    private String name;

    /**
     * 列信息
     */
    private List<TutorialColumn> columnList;


    /**
     * 获取行类型
     * @param typeFactory
     * @return
     */
    @Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        List<String> names = new ArrayList<>();
        List<RelDataType> types = new ArrayList<>();
        for (TutorialColumn sqlExecuteColumn : columnList) {
            names.add(sqlExecuteColumn.getName());
            RelDataType sqlType = typeFactory.createSqlType(SqlTypeName.get(sqlExecuteColumn.getType().toUpperCase()));
            types.add(sqlType);
        }
        return typeFactory.createStructType(Pair.zip(names, types));
    }
}
```

自定义schema
```java

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NonNull;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 自定义schema
 * @author quxiucheng
 * @date 2019-04-26 11:16:00
 */
@Data
public class TutorialTableSchema extends AbstractSchema implements Serializable {

    /**
     * schema名称
     */
    private String name;

    /**
     * table信息
     */
    private List<TutorialTable> tableList;

    public TutorialTableSchema(@NonNull String name, @NonNull List<TutorialTable> tableList) {
        this.name = name;
        this.tableList = tableList;
    }


    /**
     * 获取该schema中所有的表信息
     *
     * @return
     */
    @Override
    protected Map<String, Table> getTableMap() {
        Map<String, Table> tableMap = Maps.newHashMap();
        for (TutorialTable table : this.tableList) {
            tableMap.put(table.getName(), table);
        }
        return tableMap;
    }

}
```

table工厂
```java

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.TableFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * table工厂
 * @author quxiucheng
 * @date 2019-04-26 11:20:00
 */
public class TutorialTableFactory implements TableFactory {

    /**
     * yaml解析器
     */
    private static final ObjectMapper YAML_MAPPER = new YAMLMapper();

    /**
     * 创建table
     * @param schema
     * @param name
     * @param operand
     * @param rowType
     * @return
     */
    @Override
    public Table create(SchemaPlus schema, String name, Map operand, RelDataType rowType) {
        try {
            String ddl = (String) operand.get("ddl");
            return YAML_MAPPER.readValue(new File(ddl), TutorialTable.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

schema工厂
```java

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaFactory;
import org.apache.calcite.schema.SchemaPlus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * schema工厂类
 * @author quxiucheng
 * @date 2019-04-26 11:19:00
 */
public class TutorialSchemaFactory implements SchemaFactory {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
            .configure(JsonParser.Feature.ALLOW_COMMENTS, true);

    /**
     * 创建schema
     * @param parentSchema
     * @param name
     * @param operand
     * @return
     */
    @Override
    public Schema create(SchemaPlus parentSchema, String name, Map<String, Object> operand) {
        try {
            List<TutorialTable> tableList = new ArrayList<>();

            ArrayList tables = (ArrayList) operand.get("tables");
            for (Object table : tables) {
                String ddl = (String) ((HashMap) table).get("ddl");
                TutorialTable tutorialTable = JSON_MAPPER.readValue(new File(ddl), TutorialTable.class);
                tableList.add(tutorialTable);

            }
            return new TutorialTableSchema(name, tableList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
```
model文件
```json
{
  "version": "1.0",
  "defaultSchema": "tutorial",
  "schemas": [ {
    "name": "tutorial",
    "type": "custom",
    "factory": "com.github.quxiucheng.calcite.schema.tutorial.TutorialSchemaFactory",
    "operand": {
      "tables": [
        {
        "ddl": "calcite-tutorial-3-schema/src/main/resources/user/user.json",
        "data": "calcite-tutorial-3-schema/src/main/resources/user/user.txt"
      },{
        "ddl": "calcite-tutorial-3-schema/src/main/resources/role/role.json",
        "data": "calcite-tutorial-3-schema/src/main/resources/role/role.txt"
      }
      ]
    }
  } ]
}

```
# 后记

本文只介绍schema相关的信息,不介绍如何进行查询等,等信息

1. type等类在日后type中介绍.
2. relXXX等相关实体类在日后转换关系表达式介绍.
3. `Enumerable`,`Queryable`等实体信息在日后查询时介绍

