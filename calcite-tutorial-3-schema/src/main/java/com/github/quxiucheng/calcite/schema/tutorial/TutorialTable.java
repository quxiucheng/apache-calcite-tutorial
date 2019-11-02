package com.github.quxiucheng.calcite.schema.tutorial;

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
