package com.github.quxiucheng.calcite.parser.tutorial.sample.statement.model;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;

import java.util.List;

/**
 * @author quxiucheng
 * @date 2019-04-25 14:02:00
 */
public class CompoundIdentifierTypeCommaListModel extends SqlCall {

    private List<List<SqlNode>> all;

    public CompoundIdentifierTypeCommaListModel(List<List<SqlNode>> all, SqlParserPos pos) {
        super(pos);
        this.all = all;
    }

    @Override
    public SqlOperator getOperator() {
        return null;
    }

    @Override
    public List<SqlNode> getOperandList() {
        return null;
    }

    public List<List<SqlNode>> getAll() {
        return all;
    }
}
