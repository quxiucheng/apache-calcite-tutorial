package com.github.quxiucheng.calcite.validator.tutorial;

import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.type.RelDataTypeSystem;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;

import java.util.Properties;

/**
 * @author quxiucheng
 * @date 2019-04-26 10:35:00
 */
public class Test1 {
    public static void main(String[] args) {
        // 系统类型
        // SqlTypeFactoryImpl factory = new SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);
        // CalciteCatalogReader calciteCatalogReader = new CalciteCatalogReader(
        //     factory,
        //     new CalciteConnectionConfigImpl(new Properties()));
    }
}
