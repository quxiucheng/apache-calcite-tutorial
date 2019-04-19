# JavaCC

使用递归下降语法解析，LL(k)。
其中，第一个L表示从左到右扫描输入；
第二个L表示每次都进行最左推导(在推导语法树的过程中每次都替换句型中最左的非终结符为终结符。类似还有最右推导)；
k表示的是每次向前探索(lookahead)k个终结符

[官网](https://javacc.or)

[语法文件](https://javacc.org/javaccgrm)


## 语法描述文件
```
options {
    JavaCC的选项
}

PARSER_BEGIN(解析器类名)
package 包名;
import 库名;

public class 解析器类名 {
    任意的Java代码
}
PARSER_END(解析器类名)

扫描器的描述

解析器的描述

```
## JavaCC类介绍

### SimpleCharStream
词法分析器的输入流
 ```java
     // 构造函数种类 ,可以接受Reader和InputStream
public class SimpleCharStream {

    public SimpleCharStream(java.io.Reader dstream, int startline, 
        int startcolumn, int buffersize);
    
    public SimpleCharStream(java.io.Reader dstream, 
        int startline, int startcolumn);
    
    public SimpleCharStream(java.io.Reader dstream);
    
    public SimpleCharStream(java.io.InputStream dstream, String encoding, int startline,
      int startcolumn, int buffersize) throws java.io.UnsupportedEncodingException;
      
    public SimpleCharStream(java.io.InputStream dstream, int startline,
         int startcolumn, int buffersize);
    
    public SimpleCharStream(java.io.InputStream dstream, String encoding, int startline,
         int startcolumn) throws java.io.UnsupportedEncodingException;
                          
    public SimpleCharStream(java.io.InputStream dstream, int startline,
                          int startcolumn);
    
    public SimpleCharStream(java.io.InputStream dstream, String encoding) throws java.io.UnsupportedEncodingException;
 
    public SimpleCharStream(java.io.InputStream dstream);
}
```

### XXXXXConstants
Token常量,SKIP TOKEN 和TOKEN
```
// 忽律的字符
SKIP:{
    " "
}


// 关键字
TOKEN:{
    <PLUS :"+">
}
```
```java
// 和常量申明对应
public interface XXXXXConstants {
    
  int EOF = 0;
  int PLUS = 2;

  int DEFAULT = 0;

  String[] tokenImage = {
    // EOF 文件结尾
    "<EOF>",
    // 忽律字符串
    "\" \"",
    // PLUSA
    "\"+\"",
  };

}
```
### XXXXXTokenManager
词法分析器
```java
// 常见方法说明
public class XXXXXTokenManager implements XXXXXConstants {
    
    // 输入流
    protected SimpleCharStream input_stream;
    
    // 构造函数
    public XXXXXTokenManager(SimpleCharStream stream);
    
    // 获取下一个Token
    public Token getNextToken();
}
```

### Token
Token类
```java
public class Token {

  // Constants.java的种类
  public int kind;

  // 开始行和开始列,结束行和结束列
  public int beginLine, beginColumn, endLine, endColumn;

  // token的字符串
  public String image;

  // 下一个token
  public Token next;

  // 特殊令牌
  public Token specialToken;

  // Returns the image.
  public String toString()
  {
     return image;
  }


}
```

### XXXXX
解析类入口

### ParseException
语法解析异常

### TokenMgrError
语法错误提示

## 常见配置 options


## JavaCC工作原理
如图:
![](/calcite-tutorial-2-parser/parser-2-javacc-tutorial/md/resource/javacc.jpg)




### 语法二义性解决
```
void S():{}
{
    IFStat()
    |
    E();
}
void IFStat():{}
{
    "if" "(" E() ")"
    S()
    (
        "else" S();
    )?
}

```
```
if E if E S else S 
```
存在两种解释
```
if E
   if E
     S
   else
     S   
```

```
if E
   if E
     S
else
   S
```

可以借助`LOOKAHEAD(K)`关键字解决
```
// 词法分析执行完if-S之后先找slse,找到则匹配最近的if,否则执行后面的语句
void S():{}
{
    IFStat()
    |
    E();
}
void IFStat():{}
{
    "if" "(" E() ")"
    S()
    (
        LOOKAHEAD(1)
        "else" S();
    )?
}

```


## JavaCC语法

### Java代码
java代码块用`{}`声明
```
// 定义java代码块
void javaCodeDemo():
{}
{
    {
        int i = 0;
        System.out.println(i);
    }
}
```

### JAVA函数
需要用 JAVACODE声明
```
JAVACODE void print(Token t){
    System.out.println(t);
}
```

### 条件

* if语句
```
// if语句
void ifExpr():
{}
{
    [
        <SELECT>
        {
            System.out.println("if select");
        }
    ]

    // 循环，出现一次
    (<SELECT>)?
}
```

* if else语句

```
// if - else
void ifElseExpr():
{}
{
    (
        <SELECT> {System.out.println("if else select");}
        |
        <UPDATE>  {System.out.println("if else update");}
        |
        <DELETE>  {System.out.println("if else delete");}
        |
        {
           System.out.println("other");
        }
    )
}
```
### 循环

* while 0~n
```
// while 0~n
void while1Expr():{
}
{
    (<SELECT>)*
}
```

* while 1~n
```
// while 1~n
void while2Expr():{
}
{
    (<SELECT>)+
}

```

### 正则表达式
1. `[]`: 内容可选
2. `+`: 内容出现一次或者多次
3. `*`: 内容出现0次或者多次
4. `?`: 内容出现0次或者一次
5. `|`: 或
6. `()`: 优先级改变或者整体操作

## 代码示例

### 简单计算器
[Calculator.jj](/calcite-tutorial-2-parser/parser-2-javacc-tutorial/src/main/codegen/Calculator.jj)


### 简单语法示例
[JavaCCGrammar.jj](/calcite-tutorial-2-parser/parser-2-javacc-tutorial/src/main/codegen/JavaCCGrammar.jj)




