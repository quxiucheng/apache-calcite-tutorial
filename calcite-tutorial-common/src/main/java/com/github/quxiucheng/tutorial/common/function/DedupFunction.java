package com.github.quxiucheng.tutorial.common.function;

import com.google.common.collect.Lists;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelProtoDataType;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.type.SqlTypeName;

/**
 * @author quxiucheng
 * @date 2019-02-02 14:58:00
 */
public class DedupFunction extends SqlFunction {

    DedupTableFunctionReturnTypeInference returnTypeInference;
    public DedupFunction() {
        super("DEDUP",
                SqlKind.OTHER_FUNCTION,
                null,
                null,
                OperandTypes.VARIADIC,
                SqlFunctionCategory.USER_DEFINED_FUNCTION);
    }

    public RelDataType inferReturnType(SqlOperatorBinding opBinding) {

        final RelDataTypeFactory typeFactory =
                opBinding.getTypeFactory();
        RelDataType name = typeFactory.builder()
                .add("NAME", SqlTypeName.VARCHAR, 1024)
                .build();
        returnTypeInference = new DedupTableFunctionReturnTypeInference(new RelProtoDataType() {
            @Override
            public RelDataType apply(RelDataTypeFactory typeFactory) {
                return  typeFactory.builder()
                        .add("NAME", SqlTypeName.CURSOR)
                        .build();
            }
        }, Lists.newArrayList("NAME"), true);
        returnTypeInference.inferReturnType(opBinding);
        return name;
    }

    @Override
    public SqlReturnTypeInference getReturnTypeInference() {
        return returnTypeInference;
    }
}
