# Calcite自定义SQL解析

## 自定义SQL
```
create function function_name as class_name
[with](
    [method]='',
    [return]='',
    [params]=''
)
[comment] ''
```
## 常用Parser.jj方法

```
/**
 * 将标识符解析成字符串
 */
String Identifier() :

```
```java


```

/**
 * Parses a compound identifier with optional type.
 */
void CompoundIdentifierType(List<SqlNode> list, List<SqlNode> extendList) :

/**
 * Parses a literal expression, allowing continued string literals.
 * Usually returns an SqlLiteral, but a continued string literal
 * is an SqlCall expression, which concatenates 2 or more string
 * literals; the validator reduces this.
 */
SqlNode Literal() :


/** Parses a numeric literal (can be signed) */
SqlLiteral NumericLiteral() :

/** Parse a special literal keyword */
SqlLiteral SpecialLiteral() 

/**
 * Parses a string literal. The literal may be continued onto several
 * lines.  For a simple literal, the result is an SqlLiteral.  For a continued
 * literal, the result is an SqlCall expression, which concatenates 2 or more
 * string literals; the validator reduces this.
 *
 * @see SqlLiteral#unchain(SqlNode)
 * @see SqlLiteral#stringValue(SqlNode)
 *
 * @return a literal expression
 */
SqlNode StringLiteral() :

/**
 * Parses a date/time literal.
 */
SqlLiteral DateTimeLiteral() :

/**
 * Parses an interval literal.
 */
SqlLiteral IntervalLiteral() :

/**
 * Parses a simple identifier as an SqlIdentifier.
 */
SqlIdentifier SimpleIdentifier() :
```

```
/**
 * Parses a comma-separated list of simple identifiers.
 */
void SimpleIdentifierCommaList(List<SqlNode> list) :

/**
 * Parses a compound identifier.
 */
SqlIdentifier CompoundIdentifier() :

/**
 * Parses a comma-separated list of compound identifiers.
 */
void CompoundIdentifierTypeCommaList(List<SqlNode> list, List<SqlNode> extendList) :

int IntLiteral() :

// Some SQL type names need special handling due to the fact that they have
// spaces in them but are not quoted.
SqlIdentifierTypeName() :
```

```java
/**
 * Parses a row expression or a parenthesized expression of any kind.
 */
SqlNode Expression(ExprContext exprContext) :
```












