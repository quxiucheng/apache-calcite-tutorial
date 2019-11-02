package com.github.quxiucheng.calcite.parser.tutorial.sample.statement.model;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;

import java.util.List;

/**
 * @author quxiucheng
 * @date 2019-04-25 11:07:00
 */
public class SimpleIdentifierCommaListSampleModel extends SqlCall {

    private List<SqlNode> list;

    public SimpleIdentifierCommaListSampleModel(List<SqlNode> list, SqlParserPos pos) {
        super(pos);
        this.list = list;
    }

    @Override
    public SqlOperator getOperator() {
        return null;
    }

    @Override
    public List<SqlNode> getOperandList() {
        return null;
    }

    public List<SqlNode> getList() {
        return list;
    }
}
