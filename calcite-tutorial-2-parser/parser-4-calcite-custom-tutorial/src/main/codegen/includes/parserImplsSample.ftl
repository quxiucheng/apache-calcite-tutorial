/**
 * 文字解析器
 */
SqlNode LiteralSample() :
{}
{
    <LITERAL_SAMPLE>
    {return Literal();}
}

/**
 * 解析无符号数值数据
 */
SqlNumericLiteral UnsignedNumericLiteralSample() :
{}
{
    <UNSIGNED_NUMERIC_LITERAL_SAMPLE>
    {return UnsignedNumericLiteral();}
}

/**
 * 解析数值数据
 */
SqlLiteral NumericLiteralSample() :
{}
{
    <NUMERIC_LITERAL_SAMPLE>
    {return NumericLiteral();}
}

/**
 * 解析字符串数据
 */
SqlNode StringLiteralSample() :
{}
{
    <STRING_LITERAL_SAMPLE>
    {return StringLiteral();}
}

/**
 * 解析日期数据
 */
SqlLiteral DateTimeLiteralSample() :
{}
{
    <DATE_TIME_LITERAL_SAMPLE>
    {return DateTimeLiteral();}
}

/**
 * 解析时间间隔
 */
SqlLiteral IntervalLiteralSample() :
{}
{
    <INTERVAL_LITERAL_SAMPLE>
    {return IntervalLiteral();}
}

/**
 * 解析时间间隔的单位
 */
SqlIntervalQualifier IntervalQualifierSample() :
{}
{
    <INTERVAL_QUALIFIER_SAMPLE>
    {return IntervalQualifier();}
}

/**
 * 解析Identifier,返回类型为字符串
 * 类似于 select Identifier from xxx
 */
IdentifierSampleModel IdentifierSample() :
{}
{
    <IDENTIFIER_SAMPLE>
    {return new IdentifierSampleModel(Identifier(),getPos());}
}

/**
 * 解析Identifier,返回类型SqlIdentifier
 * 类似于 select Identifier from xxx
 */
SqlIdentifier SimpleIdentifierSample() :
{}
{
    <SIMPLE_IDENTIFIER_SAMPLE>
    {return SimpleIdentifier();}
}

/**
 * 解析Identifier, ,返回类型 List<SqlNode>
 * 类似于 select Identifier,Identifier,Identifier from xxx
 */
SimpleIdentifierCommaListSampleModel SimpleIdentifierCommaListSample() :
{
    List<SqlNode> sqlNodeList = new ArrayList<SqlNode>();
}
{
    <SIMPLE_IDENTIFIER_COMMA_LIST_SAMPLE> SimpleIdentifierCommaList(sqlNodeList)

    {return new SimpleIdentifierCommaListSampleModel(sqlNodeList,getPos());}
}

/**
 * 解析(Identifier,) ,返回类型 SqlNodeList
 * 类似于 select (Identifier,Identifier,Identifier) from xxx
 */
SqlNodeList ParenthesizedSimpleIdentifierListSample() :
{
    SqlNodeList sqlNodeList;
}
{
    <SIMPLE_IDENTIFIER_COMMA_LIST_SAMPLE> {
        sqlNodeList = ParenthesizedSimpleIdentifierList();
    }

    {return sqlNodeList;}
}

/**
 * 解析(Compound.Identifier,) ,返回类型 SqlIdentifier
 * 类似于 select (Compound.SqlIdentifier) from xxx
 */
SqlIdentifier CompoundIdentifierSample() :
{}
{
    <COMPOUND_IDENTIFIER_SAMPLE>
    {return CompoundIdentifier();}
}

/**
 * 解析Identifier, ,返回类型 SqlIdentifier和类型
 * 类似于 select SqlIdentifier from xxx
 */
CompoundIdentifierTypeSampleModel CompoundIdentifierTypeSample() :
{
    List<SqlNode> list = new ArrayList<SqlNode>();
    List<SqlNode> extendList = new ArrayList<SqlNode>();
    List<List<SqlNode>> all = new ArrayList<List<SqlNode>>();
}
{
    <COMPOUND_IDENTIFIER_SAMPLE>
    {
        CompoundIdentifierType(list,extendList);
        all.add(list);
        all.add(extendList);
    }
    {return new CompoundIdentifierTypeSampleModel(all,getPos());}
}

/**
* 解析Identifier, ,返回类型 SqlIdentifier和类型
* 类似于 select SqlIdentifier,SqlIdentifier from xxx
*/
CompoundIdentifierTypeCommaListModel CompoundIdentifierTypeCommaListSample() :
{
        List<SqlNode> list = new ArrayList<SqlNode>();
        List<SqlNode> extendList = new ArrayList<SqlNode>();
        List<List<SqlNode>> all = new ArrayList<List<SqlNode>>();
}
{
    <COMPOUND_IDENTIFIER_TYPE_COMMA_LIST_SAMPLE>
    {
        CompoundIdentifierTypeCommaList(list,extendList);
        all.add(list);
        all.add(extendList);
    }
    { return new CompoundIdentifierTypeCommaListModel(all,getPos());}
}






