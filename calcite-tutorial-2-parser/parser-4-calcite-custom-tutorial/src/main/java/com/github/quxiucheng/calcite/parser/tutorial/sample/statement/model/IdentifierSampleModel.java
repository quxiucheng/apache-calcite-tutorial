package com.github.quxiucheng.calcite.parser.tutorial.sample.statement.model;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.parser.SqlParserPos;

import java.util.List;

/**
 * @author quxiucheng
 * @date 2019-04-25 10:59:00
 */
public class IdentifierSampleModel extends SqlCall {

    private String identifier;

    public IdentifierSampleModel(String identifier, SqlParserPos pos) {
        super(pos);
        this.identifier = identifier;
    }

    @Override
    public SqlOperator getOperator() {
        return null;
    }

    @Override
    public List<SqlNode> getOperandList() {
        return null;
    }

    public String getIdentifier() {
        return identifier;
    }
}
