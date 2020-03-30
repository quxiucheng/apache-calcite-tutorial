package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.AggregateReduceFunctionsRule;

/**
 *
 * 聚合中的聚合函数简化为更简单的形式。
 * 平均: AVG(x) → SUM(x) / COUNT(x)
 * 标准差:STDDEV_POP(x) → SQRT( (SUM(x * x) - SUM(x) * SUM(x) / COUNT(x)) / COUNT(x))
 * 该函数计算累积样本标准偏离，并返回总体变量的平方根:STDDEV_SAMP(x) → SQRT( (SUM(x * x) - SUM(x) * SUM(x) / COUNT(x)) / CASE COUNT(x) WHEN 1 THEN NULL ELSE COUNT(x) - 1 END)
 * 该函数返回非空集合的总体变量:VAR_POP(x) → (SUM(x * x) - SUM(x) * SUM(x) / COUNT(x)) / COUNT(x)
 * 该函数返回非空集合的样本变量:VAR_SAMP(x) → (SUM(x * x) - SUM(x) * SUM(x) / COUNT(x)) / CASE COUNT(x) WHEN 1 THEN NULL ELSE COUNT(x) - 1 END
 * 返回一对表达式的总体协方差:COVAR_POP(x, y) → (SUM(x * y) - SUM(x, y) * SUM(y, x) / REGR_COUNT(x, y)) / REGR_COUNT(x, y)
 * 返回一对表达式的样本协方差:COVAR_SAMP(x, y) → (SUM(x * y) - SUM(x, y) * SUM(y, x) / REGR_COUNT(x, y)) / CASE REGR_COUNT(x, y) WHEN 1 THEN NULL ELSE REGR_COUNT(x, y) - 1 END
 * 返回线性回归模型中使用的独立表达式的平方和:REGR_SXX(x, y) → REGR_COUNT(x, y) * VAR_POP(y)
 * 返回可以计算回归模型统计有效性的值: REGR_SYY(x, y) → REGR_COUNT(x, y) * VAR_POP(x)
 *
 * @author quxiucheng
 * @date 2019-02-01 10: 07:00
 */
public class AggregateReduceFunctionsRuleTest {
    public static void main(String[] args) {
        String sql = "select  avg(deptno) from hr.depts group by name";
        RuleTester.printOriginalCompare(sql, AggregateReduceFunctionsRule.INSTANCE);
    }

    /**
     原始:
     LogicalProject(EXPR$0=[$1])
      LogicalAggregate(group=[{0}], EXPR$0=[AVG($1)])
       LogicalProject(name=[$1], deptno=[$0])
        EnumerableTableScan(table=[[hr, depts]])


     优化后:
     LogicalProject(EXPR$0=[$1])
      LogicalProject(name=[$0], EXPR$0=[CAST(/($1, $2)):JavaType(int) NOT NULL])
       LogicalAggregate(group=[{0}], agg#0=[$SUM0($1)], agg#1=[COUNT()])
        LogicalProject(name=[$1], deptno=[$0])
         EnumerableTableScan(table=[[hr, depts]])
     */
}
