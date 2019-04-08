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

# 常用关系代数
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
select * from emp where deptName = 'HR'
```
等价于
![](https://latex.codecogs.com/gif.latex?\sigma_{deptName=HR}(emp))

## 投影(projection) π
## 笛卡尔积(cartesian product ) ×
## 并集(set union) U
## 差集(set difference) -
## 更名(rename) ρ


# 其他操作
## 链接运算
## 自然连接 (Natural join) ⋈
## 相等连接 (equijoin)
## θ-join (theta join)
## 半链接(Semijoin) ⋉/⋊
## 反连接 Antijoin ▷
## 外链接 Outer joins
### 左外链接 Left outer join (⟕)
### 右外链接 Right outer join (⟖)
### 全链接 Full outer join (⟗)
## 除 Division (÷)
## 聚集运算

