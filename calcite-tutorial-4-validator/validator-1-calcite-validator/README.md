# Calcite校验

## 常见类

### SqlValidatorScope
```sql
SELECT expr1
FROM t1,
    t2,
    (SELECT expr2 FROM t3) AS q3
WHERE c1 IN (SELECT expr3 FROM t4)
ORDER BY expr4
```

```
expr1 can see t1, t2, q3
expr2 can see t3
expr3 can see t4, t1, t2
expr4 can see t1, t2, q3 加上（取决于方言）SELECT子句中定义的任何别名
```
### SqlValidatorNamespace
校验命名空间 
```sql
SELECT expr1
FROM t1,
    t2,
    (SELECT expr2 FROM t3) AS q3
WHERE c1 IN (SELECT expr3 FROM t4)
ORDER BY expr4
```
```
t1
t2
(SELECT expr2 FROM t3) AS q3
(SELECT expr3 FROM t4)
```
### SqlValidator

### SqlValidatorWithHints

#### SqlValidatorImpl



### SqlValidatorTable