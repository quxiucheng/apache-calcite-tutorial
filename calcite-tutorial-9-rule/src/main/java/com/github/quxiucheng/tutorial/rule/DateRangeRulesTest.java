package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.DateRangeRules;

/**
 *  * Collection of planner rules that convert
 * {@code EXTRACT(timeUnit FROM dateTime) = constant},
 * {@code FLOOR(dateTime to timeUnit} = constant} and
 * {@code CEIL(dateTime to timeUnit} = constant} to
 * {@code dateTime BETWEEN lower AND upper}.
 *
 * 将
 * EXTRACT(timeUnit FROM dateTime) = constant (常量)
 * FLOOR(dateTime to timeUnit) = constant (常量)
 * CEIL(dateTime to timeUnit) = constant (常量)
 *
 * 转换为 dateTime BETWEEN 开始时间 AND 结束实现
 * @author quxiucheng
 * @date 2019-02-02 10:26:00
 */
public class DateRangeRulesTest {
    public static void main(String[] args) {
        String sql = "select * from hr.depts where extract(year from create_time) = 2019";
        RuleTester.printOriginalCompare(sql, DateRangeRules.FILTER_INSTANCE);
    }
    /**
     sql:
    select * from hr.depts where extract(year from create_time) = 2019

    原始:
    LogicalProject(deptno=[$0], name=[$1], create_time=[$2])
      LogicalFilter(condition=[=(EXTRACT(FLAG(YEAR), $2), 2019)])
        EnumerableTableScan(table=[[hr, depts]])


    优化后:
    LogicalProject(deptno=[$0], name=[$1], create_time=[$2])
      LogicalFilter(condition=[AND(>=($2, 2019-01-01), <($2, 2020-01-01))])
        EnumerableTableScan(table=[[hr, depts]])

     */
}
