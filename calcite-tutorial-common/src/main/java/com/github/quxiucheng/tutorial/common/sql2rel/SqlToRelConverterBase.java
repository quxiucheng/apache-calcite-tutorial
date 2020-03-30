package com.github.quxiucheng.tutorial.common.sql2rel;

import org.apache.calcite.sql2rel.SqlToRelConverter;

/**
 * @author quxiucheng
 * @date 2019-01-31 15:06:00
 */
public class SqlToRelConverterBase {
    public static SqlToRelConverter.Config DEFAULT = SqlToRelConverter.configBuilder()
            // 不转换相关子查询
            .withExpand(false)
            .build();
}
