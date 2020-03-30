package com.github.quxiucheng.tutorial.common.data;

import org.apache.calcite.schema.Schema;

/**
 * @author quxiucheng
 * @date 2019-01-30 17:59:00
 */
public class MockData {

    static {
        /**
         public final int empid;
         public final int deptno;
         public final String name;
         public final float salary;
         public final Integer commission;
         */
        TutorialColumn empid = new TutorialColumn("empid", "INTEGER", true);
        TutorialColumn deptno = new TutorialColumn("deptno", "INTEGER");
        TutorialColumn name = new TutorialColumn("name", "VARCHAR", 10);
        TutorialColumn salary = new TutorialColumn("salary", "FLOAT");
        TutorialColumn commission = new TutorialColumn("commission", "INTEGER");

        TutorialTable emps = new TutorialTable("emps", empid, deptno, name, salary, commission);

        /**
         public final int empid;
         public final String name;
         */

        TutorialColumn deptname = new TutorialColumn("name", "VARCHAR", 20);
        TutorialColumn createTime = new TutorialColumn("create_time", "DATE");
        TutorialTable depts = new TutorialTable("depts", deptno, deptname, createTime);

        TutorialTable depts2 = new TutorialTable("depts2", deptname, deptno, createTime);

        hr = new TutorialTableSchema("hr", emps, depts, depts2);
    }


    public static Schema hr;

}
