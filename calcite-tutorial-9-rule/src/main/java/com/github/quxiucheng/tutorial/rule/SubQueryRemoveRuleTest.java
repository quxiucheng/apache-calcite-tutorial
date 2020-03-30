package com.github.quxiucheng.tutorial.rule;

import org.apache.calcite.rel.rules.SubQueryRemoveRule;

/**
 * 去除先关子查询
 * @author quxiucheng
 * @date 2019-02-20 11:22:00
 */
public class SubQueryRemoveRuleTest {
    public static void main(String[] args) {
        final String sql = "select *\n"
                + "from hr.emps t1\n"
                + "where name in (\n"
                + "  select name from hr.emps t2)";
        RuleTester.printProcessRule(sql,SubQueryRemoveRule.FILTER);
    }
    /**
     sql:
    select *
    from hr.emps t1
    where name in (
      select name from hr.emps t2)

    原始:
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalFilter(condition=[IN($2, {
    LogicalProject(name=[$2])
      EnumerableTableScan(table=[[hr, emps]])
    })])
        EnumerableTableScan(table=[[hr, emps]])


    规则:SubQueryRemoveRule:Filter
    LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
      LogicalProject(empid=[$0], deptno=[$1], name=[$2], salary=[$3], commission=[$4])
        LogicalJoin(condition=[=($2, $5)], joinType=[inner])
          EnumerableTableScan(table=[[hr, emps]])
          LogicalAggregate(group=[{0}])
            LogicalProject(name=[$2])
              EnumerableTableScan(table=[[hr, emps]])
     */
}
