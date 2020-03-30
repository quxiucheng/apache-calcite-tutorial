package com.github.quxiucheng.tutorial.common.validate;

import com.github.quxiucheng.tutorial.common.catalog.TutorialCalciteCatalogReader;
import com.github.quxiucheng.tutorial.common.function.DedupFunction;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlConformance;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.sql.validate.SqlValidatorCatalogReader;
import org.apache.calcite.sql.validate.SqlValidatorImpl;

import java.util.List;

/**
 * @author quxiucheng
 * @date 2019-01-31 14:26:00
 */
public class TutorialSqlValidator extends SqlValidatorImpl {


    public TutorialSqlValidator(SqlOperatorTable opTab, SqlValidatorCatalogReader catalogReader, RelDataTypeFactory typeFactory, SqlConformance conformance) {
        super(opTab, catalogReader, typeFactory, conformance);
    }

    public static SqlValidatorImpl createSqlValidator(SqlValidatorCatalogReader catalogReader){
        SqlStdOperatorTable sqlStdOperatorTable = SqlStdOperatorTable.instance();
        // 注册ramp函数
        sqlStdOperatorTable.register(new DedupFunction());
        return new TutorialSqlValidator(sqlStdOperatorTable,catalogReader, new JavaTypeFactoryImpl(), SqlConformanceEnum.DEFAULT);
    }

    public static SqlValidatorImpl createMockSqlValidator(SqlParser.Config parserConfig){
        return createSqlValidator(TutorialCalciteCatalogReader.createMockCatalogReader(parserConfig));
    }

    @Override
    public void validateColumnListParams(
            SqlFunction function,
            List<RelDataType> argTypes,
            List<SqlNode> operands) {
        // throw new UnsupportedOperationException();
    }

    /**
     * 返回表示“未知”类型的对象
     * @return
     */
    @Override
    public RelDataType getUnknownType() {
        return super.getUnknownType();
    }
}
