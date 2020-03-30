# 空间 -  全篇没有看明白啥意思
Calcite的目标([aiming](https://issues.apache.org/jira/browse/CALCITE-1968))是实现SQL的1.2([version 1.2](http://www.opengeospatial.org/standards/sfs))版本OpenGIS简单特征实现规范，
这是由[PostGIS](https://postgis.net/)和[H2GIS](http://www.h2gis.org/)等空间数据库实现的标准。

我们还旨在为空间索引[spatial indexes](https://issues.apache.org/jira/browse/CALCITE-1861)和其他形式的查询优化添加优化器支持。

* 介绍
* 启用空间支持
* 致谢

## 介绍

空间数据库是针对存储和查询表示在几何空间中定义的对象的数据进行优化的数据库。

Calcite对空间数据的支持包括

* [GEOMETRY]()数据类型和子类型([sub-types]())，包括`POINT`，`LINESTRING`和`POLYGON`
* 空间函数（前缀为ST_;我们在OpenGIS规范中实现了150个中的大约35个）

并且在某些时候还会包含查询重写以使用空间索引。

## 用空间支持

虽然`GEOMETRY`数据类型是内置的，但默认情况下不启用这些功能。
您需要在JDBC连接字符串中添加`fun = spatial`以启用这些功能。
例如，`sqlline`：
```
$ ./sqlline
> !connect jdbc:calcite:fun=spatial "sa" ""
SELECT ST_PointFromText('POINT(-71.064544 42.28787)');
+-------------------------------+
| EXPR$0                        |
+-------------------------------+
| {"x":-71.064544,"y":42.28787} |
+-------------------------------+
1 row selected (0.323 seconds)
```

* 致谢

Calcite的OpenGIS实现使用[Esri geometry API](https://github.com/Esri/geometry-api-java)。感谢我们从他们的社区获得的帮助

在开发此功能时，我们广泛使用了PostGIS文档和测试以及H2GIS文档，并在规范不明确时作为参考实现进行了咨询。
谢谢你们这些很棒的项目。

