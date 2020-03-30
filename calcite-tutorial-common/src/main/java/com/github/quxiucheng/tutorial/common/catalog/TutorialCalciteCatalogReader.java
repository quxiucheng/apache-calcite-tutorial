package com.github.quxiucheng.tutorial.common.catalog;

import com.github.quxiucheng.tutorial.common.data.MockData;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.RelCollationTraitDef;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.Frameworks;

import java.util.Properties;

/**
 * @author quxiucheng
 * @date 2019-01-30 17:53:00
 */
public class TutorialCalciteCatalogReader {


    public static CalciteCatalogReader createMockCatalogReader(SqlParser.Config parserConfig) {
        SchemaPlus rootSchema = Frameworks.createRootSchema(true);
        rootSchema.add("hr", MockData.hr);
        return createCatalogReader(parserConfig, rootSchema);
    }

    public static CalciteCatalogReader createCatalogReader(SqlParser.Config parserConfig, SchemaPlus rootSchema) {

        Properties prop = new Properties();
        prop.setProperty(CalciteConnectionProperty.CASE_SENSITIVE.camelName(),
                String.valueOf(parserConfig.caseSensitive()));
        // 设置时区
        prop.setProperty(CalciteConnectionProperty.TIME_ZONE.camelName(), "GMT+:08:00");
        CalciteConnectionConfigImpl calciteConnectionConfig = new CalciteConnectionConfigImpl(prop);
        return new CalciteCatalogReader(
                CalciteSchema.from(rootSchema),
                CalciteSchema.from(rootSchema).path(null),
                new JavaTypeFactoryImpl(),
                calciteConnectionConfig
        );
    }

}
