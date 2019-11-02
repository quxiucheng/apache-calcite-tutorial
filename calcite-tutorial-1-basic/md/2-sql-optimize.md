# 基础概念

优化过程一般为以下过程
1. 对SQL进行词法分心得到一个语法树
2. 根据关系代数进行SQL的逻辑优化
3. 根据代价估算算法进行物理查询的优化
4. 执行器执行

## 逻辑优化
在逻辑优化主要解决的问题是,如何找出SQL等价的变换形式,使SQL执行更加高效
优化思路主要包括
* 子句局部优化,例如,谓词重写,WHERE和Having条件化简的大部分情况都属于子句局部优化的范围
* 子句管理优化,例如,外连接消除,连接消除,子查询优化
* 局部与整体优化,例如:or重写,union操作
* 形式变化优化,例如,嵌套消除
* 语义优化
* 其他优化

各种逻辑优化技术,主要依赖于关系代数和启发式规则进行

###  关系代数等价变换

关系代数表达式的等级:相同的关系代替两个表达式中相应的关系,所得到的结果相同的,两个关系表达式E1和E2等价,记为E1 = E2

查询语句可以表示为一颗二叉树:
* 叶子是关系
* 内部节点是云算符(或称算子,操作符,例如 LEFT OUT JOIN) ,表示左右子树的云算方式
* 子树是子表示或者SQL片段
* 根节点是最后操作的运算符
* 根节点运算之后,得到的是SQL查询优化后的结果
* 这样一棵树就是一个查询的路径
* 多个关系连接，连接顺序不同，可以得出多个类似的二叉树
* 查询优化就是找出代价最小的二叉树，即最优的查询路径。
* 基于代价估算的查询优化就是通过计算和比较，找出花费最少的是优二叉树。


### 运算符角度优化考虑
不同的运算符优化可c减少中间生成物的大小和数量，节约IO和内存CPU等，从而提高执行速度。前提是优化前和优化后是等价的。

#### 选择

##### 基本选择性质
对同一个表的同样选择条件，作一次即可。
可优化的原因：
* 幂等性：多次应用同一个选择有同样的效果
```sql
// 执行多次相等
select * from S where S.a = 1
```

* 交换性：应用选择的次序在最终结果中没有影响
```sql
select * from S where S.a = 1 and S.b = 2
// 两个SQL相等
select * from S where S.b = 2 and S.a = 1 
```
* 选择可有效减少在它的操作数中的元组数的运算（元组数减少）


##### 分解有复杂条件的选择

* 合取：合并多个选择为更少的需求值的选择，多个等式可以合并,等价于针对这些单独条件的一系列选择。
```
where A.a = B.b and B.b = C.c 可以合并为={A.a,B.b,C.c} 而不是两个等式={A.a,B.b}和={B.b,C.c}
```

* 析取:：分解它们使得其成员选择可被移动或单独优化, 等价于选择的并集。

```
where A.a=3 OR A.b>8 
如果A.a,A.b分别有索引,也许 select * from A where A.a = 3 union select * from A where A.b>8 可以提高效率 
```

```sql
// 合取 (DNF)
select * from S where  S.a=1 and ( S.b=2 or S.c=3 )

// 析取 (CNF)
select * from S where ( S.a = 1 and S.b=2 ) or ( S.a = 1 and S.c=3 )

```

##### 选择和叉积

* 尽可能选做选择：关系有N和M行，先做积运算将包含N*M行。先做选择运算，减少N和M，则可避免不满足条件的条件参与积的运算，节约时间减少结果的大小。

* 尽可能下推选择：如果积不跟随着选择运算，可尝试使用其他规则从表达式树更高层下推选择。
```sql
select * from S join R where S.a =1 and R.b=2
// 做类似的转换
select * from (select * from S where S.a=1) ST join (select * from R where R.b=2) RT
```

##### 选择和集合运算
* 选择下推到的集合运算中：选择在差集，交集和并集算子上满足分配律

* 选择下推到集合的运算
```sql
select * from (select * from S union select * from R) SR where SR.a = 1
// 做类似的转换
(select * from S where S.a = 1) union (select * from R where R.a = 1)
```

###### 选择和集合运算图解
<!--
* 除运算

原始值: 

![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R&space;\setminus&space;S)) 

优化后等价表达式 

表达式: 

![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)\setminus&space;\sigma_{A}(S)) 

![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)\setminus&space;S)

* 并集 

原始值: 

![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R&space;\bigcup&space;S))

优化后等价表达式 

表达式: 

![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)&space;\bigcup&space;\sigma_{A}(S))

* 交集 

原始值: 

![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R&space;\bigcap&space;S))

优化后等价表达式 

表达式: 

![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)&space;\bigcap&space;\sigma_{A}(S)) 

![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)&space;\bigcap&space;S) 

![](https://latex.codecogs.com/gif.latex?R&space;\bigcap&space;\sigma_{A}(S)) 

-->



|初始式| 等级表达式1 | 等价表达式2 | 等价表达式3 |
|---|---|---|---|
|![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R&space;\setminus&space;S))|![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)\setminus&space;\sigma_{A}(S))|![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)\setminus&space;S)
|![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R&space;\bigcup&space;S))|![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)&space;\bigcup&space;\sigma_{A}(S))|
|![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R&space;\bigcap&space;S))|![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)&space;\bigcap&space;\sigma_{A}(S))|![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)&space;\bigcap&space;S)|![](https://latex.codecogs.com/gif.latex?R&space;\bigcap&space;\sigma_{A}(S))

##### 选择和投影
在投影之前进行选择：如果选择条件中引用的字段是投影中的字段的子集，则选择与投影满足交换性。

#### 投影

##### 基本投影性质
尽可能先做投影：投影是幂等性的；投影可以减少元组大小。
```sql
select a from (select * from S ) ST
// 做类似的换换
select a from (select a from S ) ST
```

#### 投影和集合云算
投影下推到集合运算中：投影在差集，交集和并集运算上满足分配律。
```sql
select a from (select * from S union select * from S) ST
// 做类似的换换
select a from (select a from S union select * from S) ST
```
##### 选择和集合运算图解
|初始式| 等级表达式|
|---|---|
|![](https://latex.codecogs.com/gif.latex?\Pi_{A_{1},A_{2}...A_{n}}(R&space;\setminus&space;S))|![](https://latex.codecogs.com/gif.latex?\Pi_{A_{1},A_{2}...A_{n}}(R)\setminus&space;\Pi_{A_{1},A_{2}...A_{n}}(S))
|![](https://latex.codecogs.com/gif.latex?\Pi_{A_{1},A_{2}...A_{n}}(R&space;\bigcup&space;S))|![](https://latex.codecogs.com/gif.latex?\Pi_{A_{1},A_{2}...A_{n}}(R)\bigcup&space;\Pi_{A_{1},A_{2}...A_{n}}(S))
|![](https://latex.codecogs.com/gif.latex?\Pi_{A_{1},A_{2}...A_{n}}(R&space;\bigcap&space;S))|![](https://latex.codecogs.com/gif.latex?\Pi_{A_{1},A_{2}...A_{n}}(R)\bigcap&space;\Pi_{A_{1},A_{2}...A_{n}}(S))

### 运算规则度优化考虑

#### 连接,笛卡尔积交换律

连接、做积运算，可交换前后位置，其结果不变。如两表连接算法中嵌套连接算法，对外表和内表有要求，外表尽可能小则有利于做“基于块的嵌套循环连接“，所以，通过交换律可以把元组少的表作为外表。

公式:
![](https://latex.codecogs.com/gif.latex?\\E_{1}&space;\times&space;E_{2}\equiv&space;E_{2}&space;\times&space;E_{1}\\&space;E_{1}&space;\times&space;E_{2}\Join&space;E_{2}&space;\times&space;E_{1}\\)


```sql
select * from S join R
// 等价于
select * from R join S
```


#### 连接,笛卡尔积结合律
做连接、做积运算，如果新的结合有利于减少中间关系的大小，则可优先处理。

公式:
![](https://latex.codecogs.com/gif.latex?\\(E_{1}&space;\times&space;E_{2})\times&space;E_{3}&space;\equiv&space;E_{1}&space;\times&space;(E_{2}\times&space;E_{3})\\&space;(E_{1}&space;\Join&space;E_{2})\Join&space;E_{3}&space;\equiv&space;E_{1}&space;\Join&space;(E_{2}\Join&space;E_{3})\\)

```sql
select * from ( S join R)  join Q
// 等价于
select * from S join ( R  join Q )
```

#### 投影的串接定律
在同一个关系上，只需要做一次投影运算，且一次投影时选择多列同时完成。
所以许多数据库优化引擎为同一个关系收集齐本关系上的所有列（目标列和 WHERE， GROUP BY 等子句的本关系的列）

公式:

![](https://latex.codecogs.com/gif.latex?\\\Pi_{A_{1},A_{2}...A_{n}}(\Pi_{B_{1},B_{2}...B_{m}}(E))&space;\equiv&space;\Pi_{A_{1}...A_{n}}(E))&space;\\&space;A\in&space;B)


```sql
select a from ( select a,b,c from S)
//  串接定律
select a from ( select a from S)
```

#### 选择的串接定律
选择条件可以合并，使得可一次就检查全部条件，不必多次过滤元组，所以可以把同层的合取条件收集在一起，统一判断。

公式:

![](https://latex.codecogs.com/gif.latex?\sigma_{F_{1}}(\sigma_{F_{2}}(E))&space;\equiv&space;\sigma_{F_{1}\wedge&space;F_{2}}(E))

```sql
select * from (select * from S where a = 1) ST where b = 2
// 等价于
select * from S where a = 1 and b = 2
```

#### 投影与选择交换律
* 先投影后选择，可以改为先选择后投影，这对于以行为存储格式的主流数据库而言，很有优化意义。存储方式总是在先获得元组后才能解析得到其中的列。
```sql
select a from (select * from S where a = 1)
// 等价于
select a from (select a from S ) ST where a = 1
```

* 先择选后投影，可以改为带有选择条件中列的投影后再选择，最后完成最外层的投影，这样，使得内层的选择和投影可以同时进行。
```sql
select a from (select a from S ) ST where a = 1
// 等价于
select a from (select * from S where a = 1)
```

#### 选择与笛卡尔积的分配律
条件下推到相关的关系上，先做选择后做积运算，这样可以减小中间结果的大小。
```sql
select * from S join R where S.a =1 and R.b=2
// 做类似的转换
select * from (select * from S where S.a=1) ST join (select * from R where R.b=2) RT
```

#### 选择与并的分配律
条件下推到相关的关系上，先做选择后做并运算，可以减小每个关系输出结果的大小。
```sql
select * from (select * from S union select * from R) SR where SR.a = 1
// 分配律
(select * from S where S.a = 1) union (select * from R where R.a = 1)
```

#### 选择与差的分配律
条件下推到相关的关系上，先做选择后做差运算，可以减小每个关系输出结果的大小
```sql
select * from (select * from S except select * from R) SR where SR.a = 1
// 分配律
(select * from S where S.a = 1) except (select * from R where R.a = 1)
```

#### 投影与笛卡儿积的分配律
先做投影后做积，可减少做积前每个元组的长度，使得再做积后得到新元组的长度变短。
```sql
select a from (select * from S union select * from R) SR 
// 分配律
(select a from S) union (select a from R )
```

#### 投影与并的分配律
先做投影后做并，可减少做并前每个元组的长度。
```sql
select a from (select * from S except select * from R) SR 
// 分配律
(select a from S) except (select a from R )
```

### 规则重写

#### 选择操作
对应的是限制条件（格式类似 field<op>consant）优化方式是选择操作下推，
目的是尽量减少连接操作前的元组数，使得中间临时关系`尽量少`。
这可减少IO和CPU等的消耗。
```sql
select * from S where sex = '男' and username = 'join'
// 优化为
select* from s where username = 'join' and sex = '男'
```


#### 投影操作
对应SELECT查询的目的的列对象。优化方式是投影操作下推。
目的是尽量减少连接操作前的列数，使得中间临时关系`尽量小`（选择操作是使元组个数”尽量少“，投影操作，是使一条元组”尽量小“）。
这样，虽然不能减少IO（多数数据库存储方式是行存储，元组是读取的最基本单位，所以想要操作列必须读取一行数据）。
但可以减少连接后的中间关系的元组大小，节约内存。
```sql
select a from (select * from S where a = 1) ST
// 优化为
select a from (select a from S where a = 1) ST
```

#### 连接操作
对应的是`连接条件`。（格式为field1<op>field2, field1和field表示”不同表“上的列对象。表示两个表连接条件。

1. ”多表连接中每个表被连接的顺序决定着效率。“，即如果ABC三个表，ABC， ACB， CBA， BCA等不同的连接后结果一样的话，
则要计算哪种效率最高。
2. 多表连接每个表被连接的顺序由用户语义决定，这决定着表之间的前后连接次序是不能随意更换的。


### 子查询优化

#### 子查询分类

##### 相关子查询
子查询的执行依赖于外层父查询的一些属性值.子查询因依赖于父查询的参数,当父查询改变事,子查询需要根据参数重新执行
```sql
SELECT * FROM S WHERE S.a IN (SELECT b FROM R WHERE S.a=R.b)
```

##### 非相关子查询
子查询的执行不依赖于外层父查询的任何属性值,这样的子查询具有独立性,可以独自求解
```sql
SELECT * FROM S WHERE S.a IN (SELECT b FROM R WHERE R.b='a')
```

#### 子查询合并
多个子查询能够合并成一个子查询.

等价的情况下。多个子查询能够合并成一个子查询。这样可以把多次表扫描，多次连接减少为单次表扫描和单次连接。

```sql
select * from S where a > 10 and a in (select c from R where b =1) or a in (select c from R where b=2 )
// 合并为
select * from S where a > 10 and a in (select c from R where b=1 or b=2)
```

#### 子查询展开

又称子查询反嵌套，又称为子查询上拉。
把一些子查询置于外层的父查询中，作为连接关系与外层父查询并列。实质上是把某些子查询重写为等价的多表连接操作。
```sql
select * from t1, ( select * from t2 where t2.a2 > 10) v_t2 where t1.a1 < 10 and v_t2.a2 < 20
// 优化为
select * from t1, t2 where t1.a1 < 10 and t2.a2 < 20 and t2.a2 > 10
```

注意:
* 如果子查询出现了聚集、GROUP BY， DISTINCT 子句，则子查询只能单独求解，不可以上拉到上层。
* 如果子查询只是一个简单格式（SPJ`select project join`）的查询语句，则可以上拉到上层，这样往往能提高查询效率

##### 子查询展开规则

1. 如果上层查询的结果没有重复（即SELECT子句中包含`主键`），
则可以展开其子查询，并且展开后的查询的SELECT子句前就回上`DISTINCT`标志。

2. 如果上层有`DISTINCT`标志，则可以直接展开子查询

3. 如果内层查询结果没有重复元组，则可以展开。

##### 子查询展开的步骤
1. 将子查询和上层查询的FROM子句连接，为同一个FROM子句，并修改相应的运行参数

2. 将子查询的谓词符号进行相应修改。如 IN修改为=ANY

3. 将子查询的WHERE条件作为一个整体与上层查询的WHERE条件合并，并用AND条件连接词连接。

##### ALL/SOME/ANY类型
如果子查询没有 GROUP BY 子句，也没有聚集函数。则可以使用如下表达式做等价转换：

* val > ALL (select ...) 等价为 val > MAX(select ...)
* val < ALL (select ...) 等价为 val < min( select ...)
* val > any (select ...) 等价为 val > min(select ....）
* val < any (select ...) 等价为 val<max(select ....)
* val => ALL (select ...) 等价为 val => MAX(select ...)
* val <= ALL (select ...) 等价为 val <= min( select ...)
* val => any (select ...) 等价为 val => min(select ....）
* val <= any (select ...) 等价为 val <= max(select ....)


#### 聚集子查询消除
将聚集函数上推，将子查询转变为一个新的不包含聚集函数的子查询，并与父查询的部分或者全部表做左外连接。


### 等价谓词重写

#### LIKE规则
如：
```sql
name like 'abc%'
//重写为
name >= 'abc' and name < 'abd';
```
应用like规则的好处：转换前针对 like 谓词只能进行全表扫描。如果name列上存在索引，则转换后可以进行索引范围扫描。

如果没有通配符（%或_）。则是与 = 等价
```sql
name like 'abc'
//重写为
name = 'abc'
```

#### BETWEEN-AND规则
```sql
sno BETWEEN 10 AND 20 
重写为
sno >= 10 and sno <=20
```
好处：如果sno建立了索引，则可以用索引扫描代替原来的BETWEEN-AND谓词限定的全表扫描，从而提高了查询的效率。

####IN转换OR规则
IN只是IN操作符，而不是IN子查询。改为OR可以更好地利用索引进行优化。将IN改为若干个OR可能会提高效率。
```sql
age IN (8, 12, 21)
//重写为
age = 8 or age = 12 or age = 21
```
效率是否提高，需要看数据库对IN谓词是否只支持全表扫描。如果数据库对IN谓词只支持全表扫描且OR谓词中表的age列存在索引，则转换后的查询效率会更好。

#### IN转换ANY规则
因为IN可以转换为OR，而OR可转换为ANY，所以可以直接把IN转换为ANY。这可能会提高效率。
```sql
age IN (8, 12, 21)
//重写为
age any (8, 12, 21)
```
效率是否提高，依赖于数据库对ANY操作的支持情况。
如：PostgreSQL没有显式支持 ANY 操作，但在内部实现时把IN操作转换为了ANY操作。（通过 explain 知道）


#### OR转换为ANY规则
这样可以更好地利用 MIN/MAX 操作进行优化。但（PG9.2.3 和 MySQL 5.6.10 目前都还没有支持）

#### ALL/ANT 转换为集函数规则
这样可以更好地利用 MIN/MAX 操作进行优化。如：
```sql
sno > ANY (10, 2*5+3, sqrt(9))
重写为
sno > sqrt(9)
```
通常，聚集函数MAX(), MIN()等的效率比ANY， ALL谓词的执行效率高。

#### NOT规则
```sql
NOT (col_1 != 2) 
//重写为 
col_1 = 2 
```
其他类似
好处：如果 col_1 上建立了索引，则可以用索引扫描代替原来的全表扫描。

#### OR重写并集规则

这条SQL会强迫查询优化器使用顺序存取，因为这个语句要检索的是OR操作的集合。假设，sex, age 上有索引，则可优化为：

如：
```sql
select * from student where ( sex = 'f' and sno > 15 ) or age > 18
// 重写
select * from student where sex = 'f' and sno > 15 union select * from student where age > 18
```

### 条件简化

1. 把HAVING条件并入WHERE条件。（只有SQL语句不存在 GROUP BY 条件 或聚集函数的情况下才可以使用）

2. 去除表达式中冗余的括号。这样子可以减少语法分析时产生的AND和OR树的层次。

3. 常量传递。如：col_1 = col_2 and col_2 = 3 。改为：col_1 = 3 and col_2 = 3;

4. 消除死码。如：永恒为假的条件。

5. 表达式计算：如：where col_1 = 1 + 2 ，改为 where col_1 = 3

6. 等式变换：化简条件（如反转关系操作符的操作数顺序）。如： -a = 3; 简化为 a = -3；

7. 不等式变换。化简条件。如：a > 10 and b = 6 and a > 2 ，简化为 b = 6 and a > 10

8. 布尔表达式变换。

9. 谓词传递闭包。

10. 任何一个布尔表达式都能被转换为一个等价的合取范式（CNF）。如：and 操作符是可交换的，所以优化器可以按先易后难的顺序计算表达式。

11. 索引的利用。


### group by优化
#### 分组操作下移
可以减少元组个数,可以提高表连接速度

#### 分组操作上移
如果之前有连接 or 过滤的操作,可以考虑分组上移,提高分组效率

### order by 优化

#### 排序消除
如果有默认的排序(如,索引等),可以将排序消除

#### 排序下推
将排序结果尽量下推到基表中,有序基表进行连接,结果符合预期,防止最终在大表操作

### distinct 优化

#### distinct消除
类似主键,唯一约束,索引,可以直接擦除distinct

#### distinct推入
生成含有distinct的反半连接查询时,先进行反半连接,再distinct,也许先distinct,在反半连接更优

#### distinct迁移
对连接操作结果distinct,可以把distinct移动到一个子查询中

## 物理优化

### 代价估算
```
总代价 = IO 代价 + CPU 代价
COST = P * a_page_cpu_time + W * T

P：计划运行时访问的页数，a_page_cpu_time 是每个页读取的时间花费，其积反映了IO代价
T：访问的元组。反映了CPU花费。（存储层是以页面为单位，数据以页面的形式读入内存，每个页面上可能有多个元组，访问元组需要解析元组结构，才能把元组上的字段读出，这消耗的是CPU）。如果是索引扫描，则还会包括索引读取的花费。
W：权重因子。表明IO到CPU的相关性，又称选择率（selectivity）。选择率用于表示在关系R中，满足条件“A<op>a”的元组数与R的所有元组N的比值。
```

其他情况不予说明,重点解释逻辑优化