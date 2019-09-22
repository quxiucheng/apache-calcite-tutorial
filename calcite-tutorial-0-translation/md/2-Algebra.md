# 代数

关系代数是Calcite的核心。每个查询都被表示为一颗关联操作树。你可以将SQL翻译成关联代数，或者直接建立关联操作树。

规划器规则使用保留语义的数学标识来转换表达式树。例如，如果过滤器不引用来自其他输入的列，则将过滤器推入内部联接的输入是有效的。Calcite是通过不停的将计划规则应用到关联表达式来达到优化查询语句的目的。成本模型在这个过程中起到指导的作用，并且计划引擎会生成一个拥有相同语义但是耗时更少的可替代的表达式来执行。这个计划过程是可以扩展的，你可以添加你自己的执行计划，优化规则，成本模型和统计。

## 代数创建

最简单的创建关联表达式的方式是使用代数方式来创建，[RelBuilder](http://calcite.apache.org/apidocs/org/apache/calcite/tools/RelBuilder.html)，下面给出例子：

### tablescan

```
final FrameworkConfig config;
final RelBuilder builder = RelBuilder.create(config);
final RelNode node = builder
  .scan("EMP")
  .build();
System.out.println(RelOptUtil.toString(node));
```

([RelBuilderExample.java](https://github.com/apache/calcite/blob/master/core/src/test/java/org/apache/calcite/examples/RelBuilderExample.java) 在这个类中你能够找到这个上面的例子和其他的例子的完整代码)



这个代码的是：

```
LogicalTableScan(table=[[scott, EMP]])
```

这行代码扫描整个的`EMP`表，等同于下面的SQL语句：

```
SELECT * FROM scott.EMP;
```



### 添加一个项目

现在，添加一个项目，等同于一下语句：

```
SELECT ename, deptno FROM scott.EMP;
```

我们只是在调用`build`方法之前添加一个调用`project`方法：

```
final RelNode node = builder
  .scan("EMP")
  .project(builder.field("DEPTNO"), builder.field("ENAME"))
  .build();
System.out.println(RelOptUtil.toString(node));
```

并且输出是：

```
LogicalProject(DEPTNO=[$7], ENAME=[$1])
    LogicalTableScan(table=[[scott, EMP]])
```

对`builder.field`的两次调用创建简单表达式，该表达式返回来自输入的关系表达式中的字段，即由`scan`调用创建的TableScan。Calcite已按顺序\$7到\$1将它们转换为字段引用。



### 添加过滤器和聚合

下面是一个带有过滤和聚合的查询：

```
final RelNode node = builder
  .scan("EMP")
  .aggregate(builder.groupKey("DEPTNO"),
      builder.count(false, "C"),
      builder.sum(false, "S", builder.field("SAL")))
  .filter(
      builder.call(SqlStdOperatorTable.GREATER_THAN,
          builder.field("C"),
          builder.literal(10)))
  .build();
System.out.println(RelOptUtil.toString(node));
```

它等同于下面的sql语句：

```
SELECT deptno, count(*) AS c, sum(sal) AS s FROM emp GROUP BY deptno HAVING count(*) > 10
```

并且会产生如下的代码：

```
LogicalFilter(condition=[>($1, 10)])
    LogicalAggregate(group=[{7}], C=[COUNT()], S=[SUM($5)])
        LogicalTableScan(table=[[scott, EMP]])
```



### 压栈和出栈

构建器使用堆栈来存储一步生成的关系表达式，并将其作为输入传递给下一步。这将允许生成关联表达式的方法来生成一个构建器。大部分情况下，你将唯一使用的栈方法是`build()`，以获取最后一个关联表达式，即树的根。有时候堆栈变得如此之深以至于会变得混乱。为了保持(逻辑)清晰，你可以从堆栈中删除表达式。例如，我们能够创建一个非常复杂的join关联：

```
.
               join
             /      \
        join          join
      /      \      /      \
CUSTOMERS ORDERS LINE_ITEMS PRODUCTS
```

创建上面的表达式树，我们能够通过三个阶段来完成。用`left`和`right`变量来存储中间变量，并在创建最终`Join`时使用`push()`将它们放回堆栈。

```
final RelNode left = builder
  .scan("CUSTOMERS")
  .scan("ORDERS")
  .join(JoinRelType.INNER, "ORDER_ID")
  .build();

final RelNode right = builder
  .scan("LINE_ITEMS")
  .scan("PRODUCTS")
  .join(JoinRelType.INNER, "PRODUCT_ID")
  .build();

final RelNode result = builder
  .push(left)
  .push(right)
  .join(JoinRelType.INNER, "ORDER_ID")
  .build();
```



### 字段名称和序号

您可以通过名称或序号来引用字段。序数是基于零的，每步操作都必须保证它输出字段的顺序。例如，Project返回每个标量表达式生成的字段。每个操作的名称必须保证是唯一的，但有时候这也会导致这些名称并不完全符合你的期望。例如，当你加入EMP和EPT，输出列中的其中一个被称为DEPTNO并且另一个可能会被称为DEPTNO_1。

下面的这些关系表达式方法可以更好地控制字段名称：

- `project`可以让你使用`alias(expr, fieldName)`来封装表达式。它会移除这些封装但是会保持建议的名称(只要这些名称是唯一的)。
- `values(String[] fieldNames, Object... values)`允许一系列的字段名称。这些字段名称中只要有一个是null，构建器都会生成唯一的名称。

如果表达式隐射了输入字段或输入字段的强制类型，它将使用该输入字段的名称。一旦唯一的名称已经被分配了，那么名称将不会再改变。如果你有自定义的RelNode实例，你可以依赖这些不变的字段名称。事实上，在整个关联表达式中，这些字段名称都是唯一不变的。

但是，如果一个关联表达式已经通过了几个重新规则（参考[RelOptRule](http://calcite.apache.org/apidocs/org/apache/calcite/plan/RelOptRule.html)）结果表达式中的字段名称将看起来不像原始的那样。在这一点上，最好按顺序引用字段。在构建接受多个输入的关系表达式时，您需要构建将其考虑在内的字段引用。这种情况最多发生在join条件中。

假设你现在在EMP和DEPT表格上创建一个join条件，EMP表格有8个字段 [EMPNO, ENAME, JOB, MGR, HIREDATE, SAL, COMM, DEPTNO]，DEPT表格有3个字段 [DEPTNO, DNAME, LOC]。在内部，Calcite会将这些字段表达成一个有11个字段的混合输入列的偏移量，并且正确的输入中的第一个字段是字段 #8。

但是通过构建器API，您可以指定哪个输入的哪个字段。要引用"SAL"(内部是 列#5)，可以写成`builder.field(2, 0, "SAL")`, `builder.field(2, "EMP", "SAL")`，或者`builder.field(2, 0, 5)`。这就意味着字段#5是两个输入中的输入#0。(为什么需要知道有两个输入？因为它们是被存储在栈中：输入#1是在栈顶，输入#0是在它的下面。如果我们不告诉构建器是两个输入，那么它不知道输入#0在栈中具体的位置)。类似的，要引用"DNAME"(内部是列#9(8+1))，可以写成`builder.field(2, 1, "DNAME")`, `builder.field(2, "DEPT", "DNAME")`,或者`builder.field(2, 1, 1)`。



### API摘要

#### 关系运算符

下面的方法会创建一个关系表达式([RelNode](http://calcite.apache.org/apidocs/org/apache/calcite/rel/RelNode.html))，并且把这个表达式放进栈中，然后返回`RelBuilder`

| method                                                       | description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| `scan(tableName)`                                            | Creates a [TableScan](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/TableScan.html). |
| `values(fieldNames, value...)`   `values(rowType, tupleList)` | Creates a [Values](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Values.html). |
| `filter(expr...)` `filter(exprList)`                         | Creates a [Filter](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Filter.html) over the AND of the given predicates. |
| `project(expr...)` `project(exprList [, fieldNames])`        | Creates a [Project](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Project.html). To override the default name, wrap expressions using `alias`, or specify the `fieldNames` argument. |
| `permute(mapping)`                                           | Creates a [Project](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Project.html) that permutes the fields using `mapping`. |
| `convert(rowType [, rename])`                                | Creates a [Project](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Project.html) that converts the fields to the given types, optionally also renaming them. |
| `aggregate(groupKey, aggCall...)` `aggregate(groupKey, aggCallList)` | Creates an [Aggregate](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Aggregate.html). |
| `distinct()`                                                 | Creates an [Aggregate](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Aggregate.html) that eliminates duplicate records. |
| `sort(fieldOrdinal...)` `sort(expr...)` `sort(exprList)`     | Creates a [Sort](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Sort.html).In the first form, field ordinals are 0-based, and a negative ordinal indicates descending; for example, -2 means field 1 descending.In the other forms, you can wrap expressions in `as`, `nullsFirst` or `nullsLast`. |
| `sortLimit(offset, fetch, expr...)` `sortLimit(offset, fetch, exprList)` | Creates a [Sort](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Sort.html) with offset and limit. |
| `limit(offset, fetch)`                                       | Creates a [Sort](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Sort.html) that does not sort, only applies with offset and limit. |
| `join(joinType, expr...)` `join(joinType, exprList)` `join(joinType, fieldName...)` | Creates a [Join](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Join.html) of the two most recent relational expressions.The first form joins on a boolean expression (multiple conditions are combined using AND).The last form joins on named fields; each side must have a field of each name. |
| `semiJoin(expr)`                                             | Creates a [SemiJoin](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/SemiJoin.html) of the two most recent relational expressions. |
| `union(all [, n])`                                           | Creates a [Union](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Union.html) of the `n` (default two) most recent relational expressions. |
| `intersect(all [, n])`                                       | Creates an [Intersect](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Intersect.html) of the `n` (default two) most recent relational expressions. |
| `minus(all)`                                                 | Creates a [Minus](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Minus.html) of the two most recent relational expressions. |
| `match(pattern, strictStart,` `strictEnd, patterns, measures,` `after, subsets, allRows,` `partitionKeys, orderKeys,` `interval)` | Creates a [Match](http://calcite.apache.org/apidocs/org/apache/calcite/rel/core/Match.html). |

Argument types:

- `expr`, `interval` [RexNode](http://calcite.apache.org/apidocs/org/apache/calcite/rex/RexNode.html)
- `expr...` Array of [RexNode](http://calcite.apache.org/apidocs/org/apache/calcite/rex/RexNode.html)
- `exprList`, `measureList`, `partitionKeys`, `orderKeys` Iterable of[RexNode](http://calcite.apache.org/apidocs/org/apache/calcite/rex/RexNode.html)
- `fieldOrdinal` Ordinal of a field within its row (starting from 0)
- `fieldName` Name of a field, unique within its row
- `fieldName...` Array of String
- `fieldNames` Iterable of String
- `rowType` [RelDataType](http://calcite.apache.org/apidocs/org/apache/calcite/rel/type/RelDataType.html)
- `groupKey` [RelBuilder.GroupKey](http://calcite.apache.org/apidocs/org/apache/calcite/tools/RelBuilder.GroupKey.html)
- `aggCall...` Array of [RelBuilder.AggCall](http://calcite.apache.org/apidocs/org/apache/calcite/tools/RelBuilder.AggCall.html)
- `aggCallList` Iterable of [RelBuilder.AggCall](http://calcite.apache.org/apidocs/org/apache/calcite/tools/RelBuilder.AggCall.html)
- `value...` Array of Object
- `value` Object
- `tupleList` Iterable of List of [RexLiteral](http://calcite.apache.org/apidocs/org/apache/calcite/rex/RexLiteral.html)
- `all`, `distinct`, `strictStart`, `strictEnd`, `allRows` boolean
- `alias` String
- `varHolder` [Holder](http://calcite.apache.org/apidocs/org/apache/calcite/util/Holder.html) of [RexCorrelVariable](http://calcite.apache.org/apidocs/org/apache/calcite/rex/RexCorrelVariable.html)
- `patterns` Map whose key is String, value is [RexNode](http://calcite.apache.org/apidocs/org/apache/calcite/rex/RexNode.html)
- `subsets` Map whose key is String, value is a sorted set of String

构建器的方法执行各种不同的优化，包括以下优化:

- `project` 如果要求按顺序隐射所有列，则返回其输入
- `filter `使条件变平（所以`AND`和`OR`可以有两个以上的孩子），简化（将`x = 1`和TRUE转换为`x = 1`
- 如果你使用了 `sort` 然后 `limit`, 结果就和 `sortLimit`一样

有一些注释方法可将信息添加到堆栈顶部的关系表达式：

| Method                | Description                                                  |
| --------------------- | ------------------------------------------------------------ |
| `as(alias)`           | Assigns a table alias to the top relational expression on the stack |
| `variable(varHolder)` | Creates a correlation variable referencing the top relational expression |

#### 堆栈方法

| Method                | Description                                                  |
| --------------------- | ------------------------------------------------------------ |
| `build()`             | Pops the most recently created relational expression off the stack |
| `push(rel)`           | Pushes a relational expression onto the stack. Relational methods such as `scan`, above, call this method, but user code generally does not |
| `pushAll(collection)` | Pushes a collection of relational expressions onto the stack |
| `peek()`              | Returns the relational expression most recently put onto the stack, but does not remove it |

##### Scalar expression methods

下面的方法会返回一些标量表达式([RexNode](http://calcite.apache.org/apidocs/org/apache/calcite/rex/RexNode.html)).这些方法中有很多可能都会用到栈中的内容。例如，`field("DEPTNO")`会返回刚刚添加到栈中的关系表达式中的“DEPTNO”列的引用。

| Method                                                       | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| `literal(value)`                                             | Constant                                                     |
| `field(fieldName)`                                           | Reference, by name, to a field of the top-most relational expression |
| `field(fieldOrdinal)`                                        | Reference, by ordinal, to a field of the top-most relational expression |
| `field(inputCount, inputOrdinal, fieldName)`                 | Reference, by name, to a field of the (`inputCount` - `inputOrdinal`)th relational expression |
| `field(inputCount, inputOrdinal, fieldOrdinal)`              | Reference, by ordinal, to a field of the (`inputCount` - `inputOrdinal`)th relational expression |
| `field(inputCount, alias, fieldName)`                        | Reference, by table alias and field name, to a field at most `inputCount - 1` elements from the top of the stack |
| `field(alias, fieldName)`                                    | Reference, by table alias and field name, to a field of the top-most relational expressions |
| `field(expr, fieldName)`                                     | Reference, by name, to a field of a record-valued expression |
| `field(expr, fieldOrdinal)`                                  | Reference, by ordinal, to a field of a record-valued expression |
| `fields(fieldOrdinalList)`                                   | List of expressions referencing input fields by ordinal      |
| `fields(mapping)`                                            | List of expressions referencing input fields by a given mapping |
| `fields(collation)`                                          | List of expressions, `exprList`, such that `sort(exprList)` would replicate collation |
| `call(op, expr...)` `call(op, exprList)`                     | Call to a function or operator                               |
| `and(expr...)` `and(exprList)`                               | Logical AND. Flattens nested ANDs, and optimizes cases involving TRUE and FALSE. |
| `or(expr...)` `or(exprList)`                                 | Logical OR. Flattens nested ORs, and optimizes cases involving TRUE and FALSE. |
| `not(expr)`                                                  | Logical NOT                                                  |
| `equals(expr, expr)`                                         | Equals                                                       |
| `isNull(expr)`                                               | Checks whether an expression is null                         |
| `isNotNull(expr)`                                            | Checks whether an expression is not null                     |
| `alias(expr, fieldName)`                                     | Renames an expression (only valid as an argument to `project`) |
| `cast(expr, typeName)` `cast(expr, typeName, precision)` `cast(expr, typeName, precision, scale)` | Converts an expression to a given type                       |
| `desc(expr)`                                                 | Changes sort direction to descending (only valid as an argument to `sort` or `sortLimit`) |
| `nullsFirst(expr)`                                           | Changes sort order to nulls first (only valid as an argument to `sort` or `sortLimit`) |
| `nullsLast(expr)`                                            | Changes sort order to nulls last (only valid as an argument to `sort` or `sortLimit`) |

##### 模式方法

下面的方法将会返回用于`match`的模式。

| Method                               | Description           |
| ------------------------------------ | --------------------- |
| `patternConcat(pattern...)`          | Concatenates patterns |
| `patternAlter(pattern...)`           | Alternates patterns   |
| `patternQuantify(pattern, min, max)` | Quantifies a pattern  |
| `patternPermute(pattern...)`         | Permutes a pattern    |
| `patternExclude(pattern)`            | Excludes a pattern    |

#### 组键方法

下面的方法将会返回一个[RelBuilder.GroupKey](http://calcite.apache.org/apidocs/org/apache/calcite/tools/RelBuilder.GroupKey.html).

| Method                                                       | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| `groupKey(fieldName...)` `groupKey(fieldOrdinal...)` `groupKey(expr...)` `groupKey(exprList)` | Creates a group key of the given expressions                 |
| `groupKey(exprList, exprListList)`                           | Creates a group key of the given expressions with grouping sets |
| `groupKey(bitSet, bitSets)`                                  | Creates a group key of the given input columns with grouping sets |

#### 聚合调用的方法

下面的方法将会返回一个[RelBuilder.AggCall](http://calcite.apache.org/apidocs/org/apache/calcite/tools/RelBuilder.AggCall.html).

| Method                                                       | Description                                                  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| `aggregateCall(op, distinct, approximate, filter, alias, expr...)` `aggregateCall(op, distinct, approximate, filter, alias, exprList)` | Creates a call to a given aggregate function, with an optional filter expression |
| `count(distinct, alias, expr...)`                            | Creates a call to the COUNT aggregate function               |
| `countStar(alias)`                                           | Creates a call to the COUNT(*) aggregate function            |
| `sum(distinct, alias, expr)`                                 | Creates a call to the SUM aggregate function                 |
| `min(alias, expr)`                                           | Creates a call to the MIN aggregate function                 |
| `max(alias, expr)`                                           | Creates a call to the MAX aggregate function                 |





