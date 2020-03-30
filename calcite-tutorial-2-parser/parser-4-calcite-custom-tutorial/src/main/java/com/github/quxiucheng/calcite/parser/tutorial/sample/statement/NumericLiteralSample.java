package com.github.quxiucheng.calcite.parser.tutorial.sample.statement;

import com.github.quxiucheng.calcite.parser.tutorial.sample.AbstractSample;
import org.apache.calcite.sql.parser.SqlParseException;

/**
 * @author quxiucheng
 * @date 2019-04-24 17:47:00
 */
public class NumericLiteralSample extends AbstractSample {

    public static void main(String[] args) throws SqlParseException {
        System.out.println(parser.parseQuery("numeric_literal_sample 123").getClass());
        System.out.println(parser.parseQuery("numeric_literal_sample -123").getClass());
        System.out.println(parser.parseQuery("numeric_literal_sample +123").getClass());
    }

}
