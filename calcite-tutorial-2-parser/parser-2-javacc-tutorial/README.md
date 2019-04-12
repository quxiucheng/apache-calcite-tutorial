# JavaCC
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

## JavaCC语法

### 条件
```
( 
term = < TERM > 
|  
term = < STAR > 
|  
term = < PREFIXTERM > 
|  
term = < WILDTERM > 
|  
term = < NUMBER > 
)
```