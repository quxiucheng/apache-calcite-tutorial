package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.ProjectToCalcRule;

/**
 * project 转换成cal
 * @author quxiucheng
 * @date 2019-02-01 17:34:00
 */
public class ProjectToCalcRuleTest {
    public static void main(String[] args) {
        String sql = "select * from hr.emps";
        RuleTester.printOriginalCompare(sql, ProjectToCalcRule.INSTANCE);
    }
    /**
     sql:
    select * from hr.emps

    原始:
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      EnumerableTableScan(table=[[hr, emps]])


    优化后:
    LogicalCalc(expr#0..4=[{inputs}], proj#0..4=[{exprs}])
      EnumerableTableScan(table=[[hr, emps]])
     */
}
