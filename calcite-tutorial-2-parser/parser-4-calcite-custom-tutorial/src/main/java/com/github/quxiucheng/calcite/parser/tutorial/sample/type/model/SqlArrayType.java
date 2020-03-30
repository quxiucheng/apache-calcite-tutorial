package com.github.quxiucheng.calcite.parser.tutorial.sample.type.model;

import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlWriter;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;

/**
 * @author quxiucheng
 * @date 2019-11-02 16:39:00
 */
public class SqlArrayType extends SqlIdentifier {

    private final SqlDataTypeSpec elementType;

    public SqlArrayType(SqlParserPos pos, SqlDataTypeSpec elementType) {
        super(SqlTypeName.ARRAY.getName(), pos);
        this.elementType = elementType;
    }

    public SqlDataTypeSpec getElementType() {
        return elementType;
    }

    @Override
    public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
        writer.keyword("ARRAY<");
        unparseType(this.elementType, writer, leftPrec, rightPrec);
        writer.keyword(">");
    }

    void unparseType(SqlDataTypeSpec type,
                     SqlWriter writer,
                     int leftPrec,
                     int rightPrec) {
        if (type.getTypeName() instanceof SqlArrayType) {
            type.getTypeName().unparse(writer, leftPrec, rightPrec);
        } else {
            type.unparse(writer, leftPrec, rightPrec);
        }
    }
}
