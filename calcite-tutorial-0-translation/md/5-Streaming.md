# 流
Calcite扩展了SQL和关系代数，以支持流式查询。

* [介绍](#Introduction)
* [一个schema例子](#An_example_schema)
* [一个简单查询](#A_simple_query)
* [过滤行](#Filtering_rows)
* [投影表达式](#Projecting_expressions)
* [滚动窗口](#Tumbling_windows)
* [改进滚动窗口](#Tumbling_windows_improved)
* [跳动窗口](#Hopping_windows)
* [分组集合](#GROUPING_SETS)
* [聚合之后过滤](#Filtering_after_aggregation)
* [子查询，视图和SQL的闭包属性](#Sub-queries_views_and_SQLs_closure_property)
* [在流和关系之间转换](#Converting_between_streams_and_relations)
* [饼图”问题：关于流的关系查询](#The_pie_chart_problem_Relational_queries_on_streams)
* [排序](#Sorting)
* [表构造函数](#Table_constructor)
* [滑动窗口](#Sliding_windows)
* [级联窗口](#Cascading_windows)
* [流表JOIN](#Joining_streams_to_tables)
* [流流JOIN](#Joining_streams_to_streams)
* [DML](#DML)
* [标点符号](#Punctuation)
* [流状态](#State_of_the_stream)
    * [实现](#Implemented)
    * [不实现](#Not_implemented)
    * [在这个文件中](#To_do_in_this_document)
* [函数](#Functions)
* [参考文献](#References)

## <span id="Introduction">介绍</span>

流是连续地、永远地流的记录的集合。与表不同，它们通常不存储在磁盘上，而是通过网络流动，并在内存中保存较短时间。

流补充了表，因为它们表示当前和未来的情况，而表则表示过去。将流归档到表中是非常常见的。

与表一样，您通常希望使用基于关系代数的高级语言查询流，根据模式进行验证，并优化以利用可用的资源和算法

Calcite的SQL是标准SQL的扩展，而不是另一种“类似SQL”的语言。这种区别很重要，原因如下:
* 对于任何了解常规SQL的人来说，流SQL都很容易学习
* 语义是清晰的，因为我们的目标是在流上生成与表中相同的数据相同的结果
* 可以编写组合流和表(或流的历史记录，基本上是内存中的表)的查询
* 许多现有的工具都可以生成标准SQL。
如果您不使用STREAM关键字，那么您将回到常规的标准SQL中。


## <span id="An_example_schema">一个schema例子</span>
我们的流SQL示例使用以下模式:

* `Orders (rowtime, productId, orderId, units)`  一个流和表
* `Products (rowtime, productId, name)` 一个表
* `Shipments (rowtime, orderId)` 一个流

## <span id="A_simple_query">一个简单查询</span>
让我们从最简单的流查询开始:
```
SELECT STREAM *
FROM Orders;

  rowtime | productId | orderId | units
----------+-----------+---------+-------
 10:17:00 |        30 |       5 |     4
 10:17:05 |        10 |       6 |     1
 10:18:05 |        20 |       7 |     2
 10:18:07 |        30 |       8 |    20
 11:02:00 |        10 |       9 |     6
 11:04:00 |        10 |      10 |     1
 11:09:30 |        40 |      11 |    12
 11:24:11 |        10 |      12 |     4
```

该查询从Orders流中读取所有列和行。像任何流查询一样，它从不终止。每当一条记录按顺序到达时，它就输出一条记录。


键入Control-C终止查询


`STREAM`流关键字是流SQL中的主要扩展。它告诉系统您对传入的订单感兴趣，而不是现有的订单。查询

```
SELECT *
FROM Orders;

  rowtime | productId | orderId | units
----------+-----------+---------+-------
 08:30:00 |        10 |       1 |     3
 08:45:10 |        20 |       2 |     1
 09:12:21 |        10 |       3 |    10
 09:27:44 |        30 |       4 |     2

4 records returned.
```

这个查询也有效，但将打印出所有现有订单，然后终止。我们称之为关系查询，而不是流。它具有传统的SQL语义。

`Orders`是特殊的，因为它有一个流和一个表。如果您试图在表上运行流查询，或在流上运行关系查询，Calcite给出一个错误:

```
SELECT * FROM Shipments;

ERROR: Cannot convert stream 'SHIPMENTS' to a table

SELECT STREAM * FROM Products;

ERROR: Cannot convert table 'PRODUCTS' to a stream
```


## <span id="Filtering_rows">过滤行</span>

和普通SQL一样，使用`WHERE`子句过滤行:

```
SELECT STREAM *
FROM Orders
WHERE units > 3;

  rowtime | productId | orderId | units
----------+-----------+---------+-------
 10:17:00 |        30 |       5 |     4
 10:18:07 |        30 |       8 |    20
 11:02:00 |        10 |       9 |     6
 11:09:30 |        40 |      11 |    12
 11:24:11 |        10 |      12 |     4
```

## <span id="Projecting_expressions">投影表达式</span>

使用`SELECT`子句中的表达式选择要返回的列或计算表达式:

```
SELECT STREAM rowtime,
  'An order for ' || units || ' '
    || CASE units WHEN 1 THEN 'unit' ELSE 'units' END
    || ' of product #' || productId AS description
FROM Orders;

  rowtime | description
----------+---------------------------------------
 10:17:00 | An order for 4 units of product #30
 10:17:05 | An order for 1 unit of product #10
 10:18:05 | An order for 2 units of product #20
 10:18:07 | An order for 20 units of product #30
 11:02:00 | An order by 6 units of product #10
 11:04:00 | An order by 1 unit of product #10
 11:09:30 | An order for 12 units of product #40
 11:24:11 | An order by 4 units of product #10
```

我们建议您始终在`SELECT`子句中包含rowtime列。在每个流和流查询中都有一个排序的时间戳，这使得以后可以进行高级计算，例如`GROUP BY`和`JOIN`。


## <span id="Tumbling_windows">滚动窗口</span>

有几种方法可以在流上计算聚合函数。
不同之处是：
* 每一行有多少行输出?
* 每个传入值是出现在一个总数中，还是多个?
* 如何定义了“窗口”，即生成给定输出行的行的集合?
* 结果是流还是关系？

有不同的窗口类型:
* tumbling window (GROUP BY) 滚动窗口(分组)
* hopping window (multi GROUP BY) 跳跃窗口(多分组)
* sliding window (window functions) 滑动窗口(窗口函数)
* cascading window (window functions) 级联窗口(窗口函数)

以下图表显示了使用它们的查询类型：

![](/calcite-tutoria-chapter0/md/resource/group-by.png)

首先，我们将看一个滚动窗口(tumbling)，由流式`GROUP BY`定义。
这是一个例子：
```
SELECT STREAM CEIL(rowtime TO HOUR) AS rowtime,
  productId,
  COUNT(*) AS c,
  SUM(units) AS units
FROM Orders
GROUP BY CEIL(rowtime TO HOUR), productId;

  rowtime | productId |       c | units
----------+-----------+---------+-------
 11:00:00 |        30 |       2 |    24
 11:00:00 |        10 |       1 |     1
 11:00:00 |        20 |       1 |     7
 12:00:00 |        10 |       3 |    11
 12:00:00 |        40 |       1 |    12
```

*翻译可以有问题*:  

结果是一个流。在11点，Calcite从10点起对每一个从订单表(order)中获取产品ID(productId)分类并发出一个11点的时间戳。
12点的时候，Calcite会把11点到12点之间的订单输出。每个输入行都只有一个输出行。

Calcite是如何知道10:00:00的分类数据在11:00完成的，它就可以发出这些数据的呢?
Calcite知道行时间(rowtime)在增加，也知道CEIL(rowtime TO HOUR)(每个小时一行)也在增加。
因此，一旦它在11:00时或11:00之后看到一行之后，Calcite将永远不会看到10:00总数的行。

递增或递减的列或表达式称为单调递增或递减

如果列或表达式的值有轻微的无序，
并且流有一种机制(如标点符号或水印)(punctuation or watermarks)来声明某个特定的值将永远不会被看到，
那么列或表达式就称为准单调的。

GROUP BY子句中没有单调或准单调的表达式，Calcite不能执行，不允许查询:

```
SELECT STREAM productId,
  COUNT(*) AS c,
  SUM(units) AS units
FROM Orders
GROUP BY productId;

ERROR: Streaming aggregation requires at least one monotonic expression in GROUP BY clause
```

需要在Schema中声明单调和准单调列。
当记录进入流并由从流中读取的查询假设时，单调性被强制执行。
我们建议您为每个流提供一个名为rowtime的时间戳列，但是您可以将其他列声明为单调的，
例如orderId。

## <span id="Tumbling_windows_improved">改进滚动窗口</span>

上一个滚动窗口的例子很容易写，因为窗口是一个小时。对于不是完整时间单位的间隔，比如2小时或2小时17分钟，不能使用CEIL，表达式会变得更复杂。

Calcite支持滚动窗口的另一种语法:

```
SELECT STREAM TUMBLE_END(rowtime, INTERVAL '1' HOUR) AS rowtime,
  productId,
  COUNT(*) AS c,
  SUM(units) AS units
FROM Orders
GROUP BY TUMBLE(rowtime, INTERVAL '1' HOUR), productId;

  rowtime | productId |       c | units
----------+-----------+---------+-------
 11:00:00 |        30 |       2 |    24
 11:00:00 |        10 |       1 |     1
 11:00:00 |        20 |       1 |     7
 12:00:00 |        10 |       3 |    11
 12:00:00 |        40 |       1 |    12
```

如您所见，它返回与前一个查询相同的结果。
`TUMBLE`滚动函数返回一个分组键，对所有的行，都是一样的;
`TUMBLE_END`函数接受相同的参数并返回窗口结束的时间;
`TUMBLE_START`函数,返回窗口的开始时间

`TUMBLE`函数有一个可选参数来对齐窗口。在下面的示例中，我们使用30分钟间隔和0:12作为对齐时间，因此查询在每小时的12和42分钟时发出时间戳
```
SELECT STREAM
  TUMBLE_END(rowtime, INTERVAL '30' MINUTE, TIME '0:12') AS rowtime,
  productId,
  COUNT(*) AS c,
  SUM(units) AS units
FROM Orders
GROUP BY TUMBLE(rowtime, INTERVAL '30' MINUTE, TIME '0:12'),
  productId;

  rowtime | productId |       c | units
----------+-----------+---------+-------
 10:42:00 |        30 |       2 |    24
 10:42:00 |        10 |       1 |     1
 10:42:00 |        20 |       1 |     7
 11:12:00 |        10 |       2 |     7
 11:12:00 |        40 |       1 |    12
 11:42:00 |        10 |       1 |     4
```

## <span id="Hopping_windows">跳跃窗口</span>

(Hopping)跳跃窗口是(TUMBLE)滚动窗口的一种简化(generalization)，它允许数据在窗口中保存的时间长于发射间隔。

例如，下面的查询发出一个row时间戳为11:00的查询，其中包含08:00到11:00之间的数据(如果我们要求严禁，则为10:59.9)，
以及一个row时间戳为12:00的查询，其中包含09:00到12:00之间的数据。

```
SELECT STREAM
  HOP_END(rowtime, INTERVAL '1' HOUR, INTERVAL '3' HOUR) AS rowtime,
  COUNT(*) AS c,
  SUM(units) AS units
FROM Orders
GROUP BY HOP(rowtime, INTERVAL '1' HOUR, INTERVAL '3' HOUR);

  rowtime |        c | units
----------+----------+-------
 11:00:00 |        4 |    27
 12:00:00 |        8 |    50
```


在这个查询中，因为保留是发射数据周期的3倍，所以每个输入行恰好输出3个输出行。
假设`HOP`函数为传入的行生成一组组键，并将其值放在每个组键的累加器中。
例如，`HOP(10:18:00, INTERVAL '1' HOUR, INTERVAL '3')`生成3个周期

```
[08:00, 09:00)
[09:00, 10:00)
[10:00, 11:00)
```

这为那些不喜欢内置函数`HOP`和`TUMBLE`的用户提供了允许用户定义分区函数的可能性。

我们可以建立复杂的表达式，比如指数衰减的移动平均线:

```
SELECT STREAM HOP_END(rowtime),
  productId,
  SUM(unitPrice * EXP((rowtime - HOP_START(rowtime)) SECOND / INTERVAL '1' HOUR))
   / SUM(EXP((rowtime - HOP_START(rowtime)) SECOND / INTERVAL '1' HOUR))
FROM Orders
GROUP BY HOP(rowtime, INTERVAL '1' SECOND, INTERVAL '1' HOUR),
  productId
```

发射:

一行数据在`11:00:00`包含在`[10:00:00,11:00:00)`;

一行数据在`11:00:01`包含在`[10:00:01,11:00:01)`.

这个表达式对最近的订单比旧订单的影响更大。
将窗口从1小时延长到2小时或1年对结果的准确性几乎没有影响(但是会使用更多的内存和计算)。

注意，我们在聚合函数(`SUM`)中使用`HOP_START`，因为它是在聚合子类内的所有行是恒定的值。这对于典型的聚合函数(`SUM`、`COUNT`等)是不允许的。

如果您熟悉分组集`GROUPING SETS`，
您可能会注意到分区函数可以看作是分组集的一般化(generalization of GROUPING SETS)，
因为它们允许一个输入行输出多个聚合子类数据。用于分组集的辅助函数，如`GROUPING()`和`GROUP_ID`，可以在聚合函数中使用，
所以可以以相同的方式使用`HOP_START`和`HOP_END`也就不足为奇了。

## <span id="GROUPING_SETS">分组集合</span>

如果每个分组`GROUPING SETS`集都包含一个单调或准单调表达式，那么分组集对于流查询是有效的

`CUBE`和`ROLLUP`对于流查询无效，因为它们将生成至少一个聚合所有内容的分组集(如`GROUP BY()`)。

## <span id="Filtering_after_aggregation">聚合之后过滤</span>

在标准SQL中，可以应用`HAVING`子句在`GROUP BY`流中，方法如下:

```
SELECT STREAM TUMBLE_END(rowtime, INTERVAL '1' HOUR) AS rowtime,
  productId
FROM Orders
GROUP BY TUMBLE(rowtime, INTERVAL '1' HOUR), productId
HAVING COUNT(*) > 2 OR SUM(units) > 10;

  rowtime | productId
----------+-----------
 10:00:00 |        30
 11:00:00 |        10
 11:00:00 |        40
```

## <span id="queries_views_and_SQLs_closure_property">子查询，视图和SQL的闭包属性</span>

前面的`HAVING`查询可以使用子查询上的`WHERE`子句表示:

```
SELECT STREAM rowtime, productId
FROM (
  SELECT TUMBLE_END(rowtime, INTERVAL '1' HOUR) AS rowtime,
    productId,
    COUNT(*) AS c,
    SUM(units) AS su
  FROM Orders
  GROUP BY TUMBLE(rowtime, INTERVAL '1' HOUR), productId)
WHERE c > 2 OR su > 10;

  rowtime | productId
----------+-----------
 10:00:00 |        30
 11:00:00 |        10
 11:00:00 |        40
```


这是在SQL早期引入的，当时需要一种方法在聚合之后执行筛选器。(回想一下WHERE在输入GROUP BY子句之前过滤行。) - 先聚合在过滤

从那时起，SQL就变成了一种数学上封闭的语言，这意味着可以对表执行的任何操作也可以对查询执行。

SQL的闭包属性非常强大。它不仅使视图过时(或者至少将其简化为语法糖)，而且使视图成为可能:

```
CREATE VIEW HourlyOrderTotals (rowtime, productId, c, su) AS
  SELECT TUMBLE_END(rowtime, INTERVAL '1' HOUR),
    productId,
    COUNT(*),
    SUM(units)
  FROM Orders
  GROUP BY TUMBLE(rowtime, INTERVAL '1' HOUR), productId;

SELECT STREAM rowtime, productId
FROM HourlyOrderTotals
WHERE c > 2 OR su > 10;

  rowtime | productId
----------+-----------
 10:00:00 |        30
 11:00:00 |        10
 11:00:00 |        40
```

`FROM`子句中的子查询有时被称为“内联视图(inline views)”，但实际上，它们比视图更基本。视图只是一种将SQL分割成可管理块的方便方法，方法是给块命名并将其存储在元数据存储库中。

许多人发现嵌套查询和视图在流上比在关系上更有用。流查询是所有连续运行的操作符的管道，这些管道通常很长。嵌套查询和视图有助于表达和管理这些管道。

顺便说一下，`WITH`子句可以实现与子查询或视图相同的功能:

```
WITH HourlyOrderTotals (rowtime, productId, c, su) AS (
  SELECT TUMBLE_END(rowtime, INTERVAL '1' HOUR),
    productId,
    COUNT(*),
    SUM(units)
  FROM Orders
  GROUP BY TUMBLE(rowtime, INTERVAL '1' HOUR), productId)
SELECT STREAM rowtime, productId
FROM HourlyOrderTotals
WHERE c > 2 OR su > 10;

  rowtime | productId
----------+-----------
 10:00:00 |        30
 11:00:00 |        10
 11:00:00 |        40
```

## <span id="Converting_between_streams_and_relations">在流和关系之间转换</span>

回顾`HourlyOrderTotals`的定义。视图是一个流还是一个关系?

它不包含`STRAM`流关键字，因此它是一个关系。但是，它是一个可以转换为流的关系。

您可以在关系查询和流查询中使用它:

```
# A relation; will query the historic Orders table.
# Returns the largest number of product #10 ever sold in one hour.
SELECT max(su)
FROM HourlyOrderTotals
WHERE productId = 10;

# A stream; will query the Orders stream.
# Returns every hour in which at least one product #10 was sold.
SELECT STREAM rowtime
FROM HourlyOrderTotals
WHERE productId = 10;
```

这种方法不限于视图和子查询。按照CQL中设置的方法，STREAM SQL中的每个查询都被定义为关系查询，并使用顶部`SELECT`中的`STREAM`关键字转换为流。

如果`STRAM `流关键字出现在子查询或视图定义中，则没有效果。

在查询准备时，Calcite指出查询中引用的关系是否可以转换为流或历史关系。

有时，流可以提供它的一些历史记录(例如Apache Kafka主题中最近24小时的数据)，但不是全部。在运行时，Calcite计算出是否有足够的历史记录来运行查询，如果没有，则给出一个错误。

## <span id="The_pie_chart_problem_Relational_queries_on_streams">饼图”问题：关于流的关系查询</span>

需要将流转换为关系的一种特殊情况出现在我所说的“饼图问题”中。假设您需要编写一个带有图表的web页面，如下面所示，其中总结了过去一小时内每个产品的订单数量

![](/calcite-tutoria-chapter0/md/resource/pie-chart.png)

但是订单流(`Orders``)只包含一些记录，而不是一个小时的摘要。我们需要在流的历史上运行一个关系查询:

```
SELECT productId, count(*)
FROM Orders
WHERE rowtime BETWEEN current_timestamp - INTERVAL '1' HOUR
              AND current_timestamp;
```

如果订单流的历史记录被录入到`Orders`表中，我们可以使用这个查询查询，尽管代价很高。更好的方法是，告诉系统将一个小时的摘要具体化到一个表中，在流流动时持续维护它，并自动重写查询以使用该表。

## <span id="Sorting">排序</span>

`ORDER BY`的使用类似于`GROUP BY`。语法看起来像普通的SQL，但是Calcite必须确保它能够及时交付结果。因此，它要求按键排序(`ORDER BY`)的前缘有一个单调表达式。

```
SELECT STREAM CEIL(rowtime TO hour) AS rowtime, productId, orderId, units
FROM Orders
ORDER BY CEIL(rowtime TO hour) ASC, units DESC;

  rowtime | productId | orderId | units
----------+-----------+---------+-------
 10:00:00 |        30 |       8 |    20
 10:00:00 |        30 |       5 |     4
 10:00:00 |        20 |       7 |     2
 10:00:00 |        10 |       6 |     1
 11:00:00 |        40 |      11 |    12
 11:00:00 |        10 |       9 |     6
 11:00:00 |        10 |      12 |     4
 11:00:00 |        10 |      10 |     1
```

大多数查询将按照插入的顺序返回结果，因为引擎使用流算法，但您不应该依赖它。例如，考虑一下这个:

```
SELECT STREAM *
FROM Orders
WHERE productId = 10
UNION ALL
SELECT STREAM *
FROM Orders
WHERE productId = 30;

  rowtime | productId | orderId | units
----------+-----------+---------+-------
 10:17:05 |        10 |       6 |     1
 10:17:00 |        30 |       5 |     4
 10:18:07 |        30 |       8 |    20
 11:02:00 |        10 |       9 |     6
 11:04:00 |        10 |      10 |     1
 11:24:11 |        10 |      12 |     4
```

`productId` = 30的行显然是无序的，这可能是因为在`productId`上对订单(`Orders`)流进行了分区，分区的流在不同的时间发送数据。

如果您需要一个特定的订单，添加一个显式`ORDER BY`:

```
SELECT STREAM *
FROM Orders
WHERE productId = 10
UNION ALL
SELECT STREAM *
FROM Orders
WHERE productId = 30
ORDER BY rowtime;

  rowtime | productId | orderId | units
----------+-----------+---------+-------
 10:17:00 |        30 |       5 |     4
 10:17:05 |        10 |       6 |     1
 10:18:07 |        30 |       8 |    20
 11:02:00 |        10 |       9 |     6
 11:04:00 |        10 |      10 |     1
 11:24:11 |        10 |      12 |     4
```

Calcite可能会通过使用rowtime进行合并来实现联合(`UNION ALL`)，这只会稍微降低效率。

您只需要将`ORDER BY`添加到最外层查询。如果需要，例如，在UNION ALL之后执行GROUP BY，Calcite将隐式地添加`ORDER BY`，以便使`GROUP BY`算法成为可能。

## <span id="Table_constructor">表构造函数</span>

VALUES子句创建具有给定行集的内联表。

流无效。行集永远不会改变，因此流永远不会返回任何行。

```
> SELECT STREAM * FROM (VALUES (1, 'abc'));

ERROR: Cannot stream VALUES
```


## <span id="Sliding_windows">滑动窗口</span>

标准SQL的特性是所谓的“分析函数”，可以在`SELECT`子句中使用。
与`GROUP BY`不同，这些不会折叠记录。每输入一条记录，就会输出一条记录。但是聚合函数是基于多行窗口的。

让我们看一个例子:

```
SELECT STREAM rowtime,
  productId,
  units,
  SUM(units) OVER (ORDER BY rowtime RANGE INTERVAL '1' HOUR PRECEDING) unitsLastHour
FROM Orders;
```

这个特性很方便但能提供很多功能。根据多个窗口规范，在`SELECT`子句中可以有多个函数。

下面的示例返回的订单在过去10分钟内的平均订单大小大于上周的平均订单大小。

```
SELECT STREAM *
FROM (
  SELECT STREAM rowtime,
    productId,
    units,
    AVG(units) OVER product (RANGE INTERVAL '10' MINUTE PRECEDING) AS m10,
    AVG(units) OVER product (RANGE INTERVAL '7' DAY PRECEDING) AS d7
  FROM Orders
  WINDOW product AS (
    ORDER BY rowtime
    PARTITION BY productId))
WHERE m10 > d7;
```

为了简洁性，这里我们使用一种语法，使用window子句部分定义窗口，然后在每个`OVER`子句中细化窗口。您还可以在`WINDOW`子句中定义所有窗口，或者内联定义所有窗口。

但真正强大的是, 在后台，这个查询维护两个表，并使用FIFO队列从小计中添加和删除值。但是您可以访问这些表，而不需要在查询中引入连接(without introducing a join)。

窗口聚合语法的其他一些特性:
* 可以根据行数定义窗口。
* 窗口可以引用尚未到达的行。(流(stream)会一直等到他们到达)。
* 您可以计算顺序相关的函数，如`RANK`和中值

## <span id="Cascading_windows">级联窗口</span>

如果我们希望查询为每个记录返回一个结果，比如滑动窗口，但是在一个固定的时间段内重置总数，比如滚动窗口，该怎么办?这种模式称为级联窗口。下面是一个例子:

```
SELECT STREAM rowtime,
  productId,
  units,
  SUM(units) OVER (PARTITION BY FLOOR(rowtime TO HOUR)) AS unitsSinceTopOfHour
FROM Orders;
```

它看起来类似于滑动窗口查询，但是单调表达式出现在窗口的`PARTITION BY`子句中。
当行时间(rowtime)从10:59:59移动到11:00:00时，`FLOOR(rowtime TO HOUR)`从10:00:00更改为11:00:00，因此一个新的分区开始。
第一行到达时，在新的一小时内将开始新的总计;第二行包含两行，以此类推。

Calcite知道旧的分区将永远不会被使用，因此从其内部存储中删除该分区的所有聚合子类(sub-totals)。

可以在同一个查询中组合使用级联和滑动窗口的分析函数。

## <span id="Joining_streams_to_tables">流表JOIN</span>

对于流，有两种连接:流到表的连接和流到流的连接。

如果表的内容没有发生变化，那么流到表的连接很简单。这个查询用每个产品的列表价格丰富了订单流:

```
SELECT STREAM o.rowtime, o.productId, o.orderId, o.units,
  p.name, p.unitPrice
FROM Orders AS o
JOIN Products AS p
  ON o.productId = p.productId;

  rowtime | productId | orderId | units | name   | unitPrice
----------+-----------+---------+-------+ -------+-----------
 10:17:00 |        30 |       5 |     4 | Cheese |        17
 10:17:05 |        10 |       6 |     1 | Beer   |      0.25
 10:18:05 |        20 |       7 |     2 | Wine   |         6
 10:18:07 |        30 |       8 |    20 | Cheese |        17
 11:02:00 |        10 |       9 |     6 | Beer   |      0.25
 11:04:00 |        10 |      10 |     1 | Beer   |      0.25
 11:09:30 |        40 |      11 |    12 | Bread  |       100
 11:24:11 |        10 |      12 |     4 | Beer   |      0.25
```

如果表发生变化，会发生什么?例如，假设产品10的单价在11点增加到0.35。11点之前的订单应该是旧价格，11点之后的订单应该是新价格。

实现这一功能的一种方法是创建一个表，其中包含每个版本的开始和结束生效日期，`ProductVersions`如下所示:

```
SELECT STREAM *
FROM Orders AS o
JOIN ProductVersions AS p
  ON o.productId = p.productId
  AND o.rowtime BETWEEN p.startDate AND p.endDate

  rowtime | productId | orderId | units | productId1 |   name | unitPrice
----------+-----------+---------+-------+ -----------+--------+-----------
 10:17:00 |        30 |       5 |     4 |         30 | Cheese |        17
 10:17:05 |        10 |       6 |     1 |         10 | Beer   |      0.25
 10:18:05 |        20 |       7 |     2 |         20 | Wine   |         6
 10:18:07 |        30 |       8 |    20 |         30 | Cheese |        17
 11:02:00 |        10 |       9 |     6 |         10 | Beer   |      0.35
 11:04:00 |        10 |      10 |     1 |         10 | Beer   |      0.35
 11:09:30 |        40 |      11 |    12 |         40 | Bread  |       100
 11:24:11 |        10 |      12 |     4 |         10 | Beer   |      0.35
```

另一种方法来实现这个是使用一个数据库时间支持(能够找到数据库的内容,因为它是在任何时刻过去),和系统需要知道订单`Orders`流的`rowtime`列对应于产品`Products`表的事务时间戳。


对于许多应用程序，不值得花费时间支持或版本控制表的成本和精力。
应用程序可以接受查询在重播时给出不同的结果:在本例中，在重播时，产品10的所有订单都被分配到后面的单价0.35。

## <span id="Joining_streams_to_streams">流流JOIN</span>

如果连接条件以某种方式强制两个流之间保持有限的距离，那么连接两个流是有意义的。在以下查询中，发货日期为订单日期后1小时内:

```
SELECT STREAM o.rowtime, o.productId, o.orderId, s.rowtime AS shipTime
FROM Orders AS o
JOIN Shipments AS s
  ON o.orderId = s.orderId
  AND s.rowtime BETWEEN o.rowtime AND o.rowtime + INTERVAL '1' HOUR;

  rowtime | productId | orderId | shipTime
----------+-----------+---------+----------
 10:17:00 |        30 |       5 | 10:55:00
 10:17:05 |        10 |       6 | 10:20:00
 11:02:00 |        10 |       9 | 11:58:00
 11:24:11 |        10 |      12 | 11:44:00
```

请注意，有相当多的订单没有出现，因为它们在一个小时内没有发货。当系统接收到时间戳为11:24:11的订单10时，它已经从散列表中删除了包括时间戳为10:18:07的订单8在内的订单。


如您所见，“锁定步骤”，将两个流的单调或准单调列绑在一起，是系统取得进展所必需的。如果不能推断出锁步骤，它将拒绝执行查询。

## <span id="DML">DML</span>

不仅查询对流有意义;对流运行DML语句(`INSERT`, `UPDATE`, `DELETE`, `UPSERT` and `REPLACE`)也是有意义的。

DML很有用，因为它允许您基于流实现流或表，因此在经常使用值时可以节省工作。

考虑流应用程序通常由查询管道组成，每个查询将输入流转换为输出流。管道的组件可以是一个视图:

```
REATE VIEW LargeOrders AS
SELECT STREAM * FROM Orders WHERE units > 1000;
```

或一个插入`INSERT`语句:

```
INSERT INTO LargeOrders
SELECT STREAM * FROM Orders WHERE units > 1000;
```

它们看起来很相似，而且在这两种情况下，管道中的下一步操作都可以从`LargeOrders`中读取，而不必担心它是如何填充的。效率上存在差异:无论有多少消费者，`INSERT`语句都执行相同的工作;视图的工作与使用者的数量成正比，特别是，如果没有使用者，视图就不工作。

其他形式的DML对流是有意义的。例如，下面的`UPSERT`语句维护一个表，该表实现了订单最后一个小时的摘要:

```
UPSERT INTO OrdersSummary
SELECT STREAM productId,
  COUNT(*) OVER lastHour AS c
FROM Orders
WINDOW lastHour AS (
  PARTITION BY productId
  ORDER BY rowtime
  RANGE INTERVAL '1' HOUR PRECEDING)
```

## <span id="Punctuation">标点符号</span>

标点符号允许流查询，即使单调的键中没有足够的值来输出结果。

(我更喜欢术语“rowtime bounds”，水印(watermarks)是一个相关的概念，但对于这些目的，标点符号就足够了。)

如果一个流启用了标点符号，那么它可能不会被排序，但仍然是可排序的。因此，从语义的角度来看，使用已排序的流就足够了。

顺便说一下，如果无序流是t排序(t-sorted)的(即保证每个记录在其时间戳的t秒内到达)或k排序(k-sorted)的(即保证每个记录的无序位置不超过k个)，那么它也是可排序的。因此，对这些流的查询的计划可以类似于对带有标点符号的流的查询。

而且，我们通常希望聚合的属性不是基于时间的，而是单调的。“团队在赢与输之间转换的次数”就是这样一个单调的属性。系统需要自己确定在这样的属性上进行聚合是安全的;标点符号不添加任何额外的信息。

我想到了一些计划者的元数据(成本指标):

1.  这个流是按给定属性(或多个属性)排序的吗?
2.  是否有可能对给定属性的流进行排序?(对于有限关系，答案总是“是”;对于流，它依赖于标点符号的存在，或者属性和排序键之间的链接)。
3.  为了执行排序，我们需要引入什么延迟?
4.  执行这种排序的成本(CPU、内存等)是多少

在[BuiltInMetadata.Collation](https://calcite.apache.org/apidocs/org/apache/calcite/rel/metadata/BuiltInMetadata.Collation.html)已经给了(1)的答案.
对于(2)答案总是“正确”的.但是我们需要为流实现(2)(3)和(4)。

## <span id="State_of_the_stream">流状态</span>

Not all concepts in this article have been implemented in Calcite. And others may be implemented in Calcite but not in a particular adapter such as SamzaSQL [3] [4].

### <span id="Implemented">实现</span>

* 流 SELECT, WHERE, GROUP BY, HAVING, UNION ALL, ORDER BY
* FLOOR and CEIL functions
* Monotonicity(单调性)
* Streaming VALUES is disallowed(不被允许)

### <span id="Not_implemented">不实现</span>

在本文档中，似乎方Calcite支持下列特性，但实际上它(不支持。完全支持意味着引用实现支持特性(包括负面案例)并由TCK测试它。

* Stream-to-stream JOIN
* Stream-to-table JOIN
* Stream on view
* Streaming UNION ALL with ORDER BY (merge)
* Relational query on stream
* Streaming windowed aggregation (sliding and cascading windows)
* Check that STREAM in sub-queries and views is ignored
* Check that streaming ORDER BY cannot have OFFSET or LIMIT
* Limited history; at run time, check that there is sufficient history to run the query.
* Quasi-monotonicity
* HOP and TUMBLE (and auxiliary HOP_START, HOP_END, TUMBLE_START, TUMBLE_END) functions

### <span id="To_do_in_this_document">在这个文件中</span>

* Re-visit whether you can stream VALUES
* OVER clause to define window on stream
* Consider whether to allow CUBE and ROLLUP in streaming queries, with an understanding that some levels of aggregation will never complete (because they have no monotonic expressions) and thus will never be emitted.
* Fix the UPSERT example to remove records for products that have not occurred in the last hour.
* DML that outputs to multiple streams; perhaps an extension to the standard REPLACE statement.


## <span id="Functions">函数</span>

以下函数不在标准SQL中，而是在流SQL中定义的。

标注函数:

* `FLOOR(dateTime TO intervalType)` 将日期、时间或时间戳值四舍五入到给定的时间间隔类型
* `CEIL(dateTime TO intervalType)`  将日期、时间或时间戳值四舍五入到给定的时间间隔类型

分区功能:

* HOP(t, emit, retain) returns a collection of group keys for a row to be part of a hopping window
* HOP(t, emit, retain, align) returns a collection of group keys for a row to be part of a hopping window with a given alignment
* TUMBLE(t, emit) returns a group key for a row to be part of a tumbling window
* TUMBLE(t, emit, align) returns a group key for a row to be part of a tumbling window with a given alignment

TUMBLE(t, e) is equivalent to TUMBLE(t, e, TIME '00:00:00').

TUMBLE(t, e, a) is equivalent to HOP(t, e, e, a).

HOP(t, e, r) is equivalent to HOP(t, e, r, TIME '00:00:00').



## <span id="References">参考文献</span>
