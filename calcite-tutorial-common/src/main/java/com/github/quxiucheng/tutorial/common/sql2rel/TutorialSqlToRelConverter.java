package com.github.quxiucheng.tutorial.common.sql2rel;

import com.github.quxiucheng.tutorial.common.catalog.TutorialCalciteCatalogReader;
import com.github.quxiucheng.tutorial.common.validate.TutorialSqlValidator;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.plan.Contexts;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptCostImpl;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.prepare.PlannerImpl;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlValidatorImpl;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.sql2rel.StandardConvertletTable;
import org.apache.calcite.tools.Frameworks;

/**
 * @author quxiucheng
 * @date 2019-01-31 14:38:00
 */
public class TutorialSqlToRelConverter {

    public static SqlToRelConverter createSqlToRelConverter(SqlParser.Config parserConfig,
                                                            SqlToRelConverter.Config sqlToRelConverterConfig,
                                                            RelOptPlanner planner) {

        PlannerImpl plannerImpl = new PlannerImpl(Frameworks
                .newConfigBuilder()
                //不知道是啥
                // .traitDefs(ConventionTraitDef.INSTANCE, RelCollationTraitDef.INSTANCE)
                .sqlToRelConverterConfig(sqlToRelConverterConfig)
                .parserConfig(parserConfig)
                .build());
        RexBuilder rexBuilder = new RexBuilder(new JavaTypeFactoryImpl());
        RelOptCluster cluster = RelOptCluster.create(planner, rexBuilder);
        CalciteCatalogReader catalogReader = TutorialCalciteCatalogReader.createMockCatalogReader(parserConfig);
        SqlValidatorImpl validator = TutorialSqlValidator.createMockSqlValidator(parserConfig);

        return new SqlToRelConverter(
                plannerImpl,
                validator,
                catalogReader,
                cluster,
                StandardConvertletTable.INSTANCE,
                sqlToRelConverterConfig);
    }

    /**
     * 未经过任何优化的转换
     *
     * @param parserConfig
     * @param sqlToRelConverterConfig
     * @return
     */
    public static SqlToRelConverter createSqlToRelConverter(SqlParser.Config parserConfig,
                                                            SqlToRelConverter.Config sqlToRelConverterConfig) {
        CalciteCatalogReader catalogReader = TutorialCalciteCatalogReader.createMockCatalogReader(parserConfig);
        VolcanoPlanner planner = new VolcanoPlanner(RelOptCostImpl.FACTORY, Contexts.of(catalogReader.getConfig()));
        planner.addRelTraitDef(ConventionTraitDef.INSTANCE);
        return createSqlToRelConverter(parserConfig, sqlToRelConverterConfig, planner);
    }

}
