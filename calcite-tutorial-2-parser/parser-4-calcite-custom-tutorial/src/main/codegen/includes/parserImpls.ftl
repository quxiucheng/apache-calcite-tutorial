<#--
// Licensed to the Apache Software Foundation (ASF) under one or more
// contributor license agreements.  See the NOTICE file distributed with
// this work for additional information regarding copyright ownership.
// The ASF licenses this file to you under the Apache License, Version 2.0
// (the "License"); you may not use this file except in compliance with
// the License.  You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
-->
// 创建函数
SqlNode SqlCreateFunction() :
{
    // 声明变量
    SqlParserPos createPos;
    SqlParserPos propertyPos;
    SqlNode functionName = null;
    String className = null;
    String methodName = null;
    String comment = null;
    SqlNodeList properties = null;
}
{
    // create 关键字
    <CREATE>
    {
        // 获取当前token的行列位置
        createPos = getPos();
     }
    // function 关键字
    <FUNCTION>
    // 函数名
    functionName = CompoundIdentifier()
    // as关键字
    <AS>
    // 类名
    { className = StringLiteralValue(); }
    // if语句
    [
        // method关键字
        <METHOD>
        {
            // 方法名称
            methodName = StringLiteralValue();
        }
    ]
    // if
    [
        // property 关键字,设置初始化变量
        <PROPERTY>
            {
                // 获取关键字位置
                propertyPos = getPos();
                SqlNode property;
                properties = new SqlNodeList(propertyPos);
            }
        <LPAREN>
        [
            property = PropertyValue()
            {
                properties.add(property);
            }
            (
                <COMMA>
                {
                    property = PropertyValue();
                    properties.add(property);
                }
            )*
        ]
        <RPAREN>
    ]
    // if
    [
        <COMMENT> {
            // 备注
            comment = StringLiteralValue();
        }
    ]

    {
        return new SqlCreateFunction(createPos, functionName, className, methodName, comment, properties);
    }
}

JAVACODE String StringLiteralValue() {
    SqlNode sqlNode = StringLiteral();
    return ((NlsString) SqlLiteral.value(sqlNode)).getValue();
}



/**
 * 解析SQL中的key=value形式的属性值
 */
SqlNode PropertyValue() :
{
    SqlNode key;
    SqlNode value;
    SqlParserPos pos;
}
{
    key = StringLiteral()
    { pos = getPos(); }
    <EQ> value = StringLiteral()
    {
        return new SqlProperty(getPos(), key, value);
    }
}