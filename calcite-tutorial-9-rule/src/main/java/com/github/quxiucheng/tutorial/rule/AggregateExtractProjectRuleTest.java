package com.github.quxiucheng.tutorial.rule;

import com.google.common.collect.Lists;
import org.apache.calcite.rel.core.Aggregate;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rel.core.TableScan;
import org.apache.calcite.rel.rules.AggregateExtractProjectRule;
import org.apache.calcite.rel.rules.AggregateProjectMergeRule;

/**
 * 将 聚合,投影 merge之后的结构,提取出来
 * @author quxiucheng
 * @date 2019-01-31 16:22:00
 */
public class AggregateExtractProjectRuleTest {

    public static void main(String[] args) {
        String sql = "select sum(salary) from hr.emps ";
        final AggregateExtractProjectRule rule =
                new AggregateExtractProjectRule(Aggregate.class, TableScan.class,
                        RelFactories.LOGICAL_BUILDER);
        RuleTester.printRuleCompare(sql,
                // 执行聚合
                Lists.newArrayList(AggregateProjectMergeRule.INSTANCE),
                // 提取投影 project
                Lists.newArrayList(rule));

    }

    /**
     注意: sum(salary)

     sql:
     select sum(salary) from hr.emps

     原始:
     LogicalAggregate(group=[{}], EXPR$0=[SUM($0)])
      LogicalProject(salary=[$3])
       EnumerableTableScan(table=[[hr, emps]])


     pre执行:
     LogicalAggregate(group=[{}], EXPR$0=[SUM($3)])
      EnumerableTableScan(table=[[hr, emps]])


     after执行:
     LogicalAggregate(group=[{}], EXPR$0=[SUM($0)])
      LogicalProject(salary=[$3])
       EnumerableTableScan(table=[[hr, emps]])
     */
}
