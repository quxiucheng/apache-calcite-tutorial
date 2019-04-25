# Apache Calcite - SQL解析

## fmpp使用
[fmpp官方文档](http://fmpp.sourceforge.net/)

[fmpp配置](http://fmpp.sourceforge.net/settings.htm)

[FreeMarker官方地址](http://freemarker.foofun.cn/index.html)

[FreeMarker具体语法地址](http://freemarker.foofun.cn/dgui_template_exp.html)

FMPP是一种使用FreeMarker模板的通用文本文件预处理工具。
它以递归方式处理整个目录。
它可以用于生成完整的静态网站，源代码，配置文件等。
它可以将数据从CSV，XML和JSON等源插入到生成的文件中。

Calcite基于FreeMarker生成代码,之后编译运行,fmpp例子请看系统代码
[fmpp基本例子](/calcite-tutorial-2-parser/parser-1-fmpp-tutorial/README.md)

## JavaCC使用

[官网](https://javacc.or)

[语法文件](https://javacc.org/javaccgrm)

使用递归下降语法解析，LL(k)。
其中，第一个L表示从左到右扫描输入；
第二个L表示每次都进行最左推导(在推导语法树的过程中每次都替换句型中最左的非终结符为终结符。类似还有最右推导)；
k表示的是每次向前探索(lookahead)k个终结符

[JavaCC基本例子](/calcite-tutorial-2-parser/parser-2-javacc-tutorial/README.md)

## Calcite SQL解析
[Calcite SQL解析](/calcite-tutorial-2-parser/parser-3-calcite-tutorial/README.md)


## Calcite自定义解析SQL
[Calcite自定义解析SQL](/calcite-tutorial-2-parser/parser-4-calcite-custom-tutorial/README.md)







