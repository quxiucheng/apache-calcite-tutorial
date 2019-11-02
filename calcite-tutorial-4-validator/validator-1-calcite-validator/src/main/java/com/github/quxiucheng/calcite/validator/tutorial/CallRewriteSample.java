package com.github.quxiucheng.calcite.validator.tutorial;

import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.Lex;
import org.apache.calcite.jdbc.CalcitePrepare;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.server.CalciteServerStatement;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.dialect.OracleSqlDialect;
import org.apache.calcite.sql.fun.SqlNullifFunction;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * @author quxiucheng
 * @date 2019-05-23 15:49:00
 */
public class CallRewriteSample {
    public static void main(String[] args) throws Exception {
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
        String sql = "select coalesce('carrot','apple'),SECOND(timestamp '2008-9-23 01:23:45'),{fn ABS(1)},NULLIF('','1') from tutorial.user_info";
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
        SqlStdOperatorTable instance = SqlStdOperatorTable.instance();
        instance.register(new SqlNullifFunction());
        SqlValidator validator = SqlValidatorUtil.newValidator(instance,
                calciteCatalogReader, factory, SqlConformanceEnum.DEFAULT
        );
        // 校验后的SqlNode
        // SqlCoalesceFunction
        // SqlDatePartFunction
        // SqlJdbcFunctionCall - SqlFunction - rewrite - call =SqlJdbcFunctionCall
        // SqlNewOperator NEW UDT（1,2）。
        // SqlNullifFunction
        // SqlProcedureCallOperator SELECT f(x) FROM VALUES(0) to SELECT * FROM TABLE f(x)
        validator.setCallRewrite(true);
        SqlNode validateSqlNode = validator.validate(sqlNode);
        System.out.println(validateSqlNode);
    }
}
