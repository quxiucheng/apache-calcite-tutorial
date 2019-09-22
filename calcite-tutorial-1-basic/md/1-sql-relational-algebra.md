# sql和关系代数

sql和关系代数相互转换

|名称|英文|标识|
|--|--|--|
|选择|selection|σ (sigma)
|投影|projection|Π (PI大写)
|笛卡尔积|Cartesian Product|×
|并集|union|∪
|差集|set difference|-
|更名|rename|ρ (rho)
|自然连接|Natural join|⋈
|半链接|Semijoin|⋉/⋊
|左外链接|Left outer join|⟕
|右外链接|Right outer join|⟖
|全链接|Full outer join|⟗
|除| Division|÷

# 常见SQL和关系代数的转换
emp表(E表)

|empName|deptName|
|--|--|
| A | HR |
| B | HR |
| C | Dev |
| D | Dev |
| E | Ops |
| F | Ops |

emp_leave表(EL表)

|empName|deptName|
|--|--|
| A | HR |
| E | Ops |

dep表(D表)

|deptName|deptCname|
|--|--|
| HR | 人力 |
| Dev | 研发 |
| Ops | 运维 |



## 选择(selection)σ

```sql
select * from emp where empName = 'A'
```
等价于关系代数 

![](https://latex.codecogs.com/gif.latex?\sigma_{empName="A"}(emp))


## 投影(projection) π

```sql
select deptName from emp
```
等价于关系代数 

![](https://latex.codecogs.com/gif.latex?\Pi_{deptName}(emp))


## 并集(set union) U


```sql
select * from emp union select * from emp
```
等价于关系代数 

![](https://latex.codecogs.com/gif.latex?emp&space;\cup&space;emp)

## 更名(rename) ρ

```sql
select * from emp e
```
等价于关系代数 

![](https://latex.codecogs.com/gif.latex?\rho_{e}(emp))

## 相等连接 (equijoin)

```sql
select emp.* from emp , dept where emp.deptName = dept.deptName
```
等价于关系代数 

![](https://latex.codecogs.com/gif.latex?emp&space;\Join_{emp.deptName=dept.deptName}&space;dept)

## θ-join (theta join)
<!--R \Join_{\theta} S = \sigma_{\theta}(R \times S)-->
![](https://latex.codecogs.com/gif.latex?R&space;\Join_{\theta}&space;S&space;=&space;\sigma_{\theta}(R&space;\times&space;S))

