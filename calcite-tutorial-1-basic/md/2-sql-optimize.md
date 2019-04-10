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

#### 基本选择性质
对同一个表的同样选择条件，作一次即可。
可优化的原因：
* 幂等性：多次应用同一个选择有同样的效果
* 交换性：应用选择的次序在最终结果中没有影响
* 选择可有效减少在它的操作数中的元组数的运算（元组数减少）

#### 分解有复杂条件的选择

* 合取：合并多个选择为更少的需求值的选择，多个等式可以合并,等价于针对这些单独条件的一系列选择。
```
where A.a = B.b and B.b = C.c 可以合并为={A.a,B.b,C.c} 而不是两个等式={A.a,B.b}和={B.b,C.c}
```
* 分解它们使得其成员选择可被移动或单独优化, 等价于选择的并集。

#### 选择和叉积

* 尽可能选做选择：关系有N和M行，先做积运算将包含N*M行。先做选择运算，减少N和M，则可避免不满足条件的条件参与积的运算，节约时间减少结果的大小。
* 尽可能下推选择：如果积不跟随着选择运算，可尝试使用其他规则从表达式树更高层下推选择。

#### 选择和集合运算
选择下推到的集合运算中：选择在差集，交集和并集算子上满足分配律

选择下推到集合的运算

* 除运算

原始值: 

![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R&space;\setminus&space;S)) 

优化后等价表达式 

表达式: 

![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)\setminus&space;\sigma_{A}(S))
![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)\setminus&space;S)

* 并集 

![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R&space;\bigcup&space;S))

优化后等价表达式 

表达式: 

![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)&space;\bigcup&space;\sigma_{A}(S))

* 交集
![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R&space;\bigcap&space;S))

优化后等价表达式 

表达式: 

![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)&space;\bigcap&space;\sigma_{A}(S))
![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)&space;\bigcap&space;S)
![](https://latex.codecogs.com/gif.latex?R&space;\bigcap&space;\sigma_{A}(S))




|初始式| 等级表达式1 | 等价表达式2 | 等价表达式3 |
|---|---|---|---|
|![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R&space;\setminus&space;S))|![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)\setminus&space;\sigma_{A}(S))|
|![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R&space;\bigcup&space;S))|![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)&space;\bigcup&space;\sigma_{A}(S))
|![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R&space;\bigcap&space;S))|![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)&space;\bigcap&space;\sigma_{A}(S))|![](https://latex.codecogs.com/gif.latex?\sigma_{A}(R)&space;\bigcap&space;S)|![](https://latex.codecogs.com/gif.latex?R&space;\bigcap&space;\sigma_{A}(S))

#### 选择和投影
在投影之前进行选择：如果选择条件中引用的字段是投影中的字段的子集，则选择与投影满足交换性。

### 投影

#### 基本投影性质
尽可能先做投影：投影是幂等性的；投影可以减少元组大小。

#### 投影和集合云算
投影下推到集合运算中：投影在差集，交集和并集运算上满足分配律。



### 运算规则度优化考虑

### 子查询优化

### 视图重写

### 等价谓词重写

### 条件简化

### 外连接消除

### 嵌套消除

### 连接消除

### 语义优化

### group by优化

### order by 优化

### 启发式规则在逻辑优化阶段应用

## 物理优化

### 代价估算