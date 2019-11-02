package com.github.quxiucheng.calcite.parser.tutorial.sample.type;

import com.github.quxiucheng.calcite.parser.tutorial.sample.AbstractSample;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;

/**
 * @author quxiucheng
 * @date 2019-11-02 17:04:00
 */
public class SqlArrayTypeSample extends AbstractSample {

    public static void main(String[] args) throws SqlParseException {
        SqlNode cast = parser.parseQuery("select cast(123 as array<varchar>) from a");
        System.out.println(((SqlSelect) cast).getSelectList());
        System.out.println(cast.getClass());
    }
}
