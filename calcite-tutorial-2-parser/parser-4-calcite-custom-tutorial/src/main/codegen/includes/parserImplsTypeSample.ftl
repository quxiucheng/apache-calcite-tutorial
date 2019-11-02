/**
* 解析Array类型 - 自定义
*/
SqlIdentifier SqlArrayType() :
{
    SqlParserPos pos;
    SqlDataTypeSpec elementType;
    boolean nullable = true;
}
{
    <ARRAY> { pos = getPos(); }
    <LT>
    elementType = DataType()
    <GT>
    {
        return new SqlArrayType(pos, elementType);
    }
}