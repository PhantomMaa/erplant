### 功能介绍

- erplant是一款根据ER图生成数据库建表语句和java DO类的工具
- 使用的输入源是plantuml
  编写的ER图，在官方ER图语法的基础上做了扩展。较之[官方语法](http://wiki.plantuml.net/site/ie-diagram)，做了以下额外支持
    - 增加Database层级，可以汇总多个库的设计到一张ER图中，并做好库层面的归类
    - 增加字段类型、是否为空、默认值、是否自增等定义
    - 增加索引定义，普通索引、主键索引、唯一索引等
    - 增加扩展属性的定义，如json字段，可以在ER图中，描述其内部的属性
    - 详细plant uml语法定义，参考下文中的样例
- 面向开发设计阶段，在设计有数据库依赖的软件时，允许设计ER图先行。以描绘ER图为最原始的素材，像建筑工程师的图纸，之后依此生成编程语言需要的类型对象，以及生成数据库表。解决之前，表的设计和类型定义阶段割裂的问题

### 前置准备

安装graphviz，在idea中打开puml文件显示预览图片

mac安装方式。其他可参考：https://plantuml.com/graphviz-dot
```bash
brew install libtool
brew link libtool
brew install graphviz
brew link --overwrite graphviz
```

### 使用方式

### 命令行方式

```
java -jar erplant-1.0.jar   
Usage: erplant [-hV] -p=<packageName> <src>
convert plantuml er diagram to database ddl and java do
      <src>       the puml file
  -h, --help      Show this help message and exit.
  -p, --package=<packageName>
                  assign the package name to generate java do file
  -V, --version   Print version information and exit.
```

一个例子：

```
$ java -jar target/erplant-1.0.jar iam_er11.puml -p com.example
$ ll *.java *.sql                                             
-rw-r--r--  1 mahang  staff   348B Aug 31 10:24 OrgDO.java
-rw-r--r--  1 mahang  staff   324B Aug 31 10:24 TenantDO.java
-rw-r--r--  1 mahang  staff   233B Aug 31 10:24 UserAuthDO.java
-rw-r--r--  1 mahang  staff   1.2K Aug 31 10:24 org.sql
-rw-r--r--  1 mahang  staff   391B Aug 31 10:24 user.sql
$ cat user.sql 
CREATE TABLE IF NOT EXISTS `user_auth` (
    `id` bigint AUTO_INCREMENT ,
    `gmt_create` datetime COMMENT '创建时间',
    `gmt_modified` datetime COMMENT '修改时间',
    `uid` bigint NOT NULL COMMENT '用户id',
    `passwd` varchar(50) COMMENT '密码',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户登录验证';
$ cat UserAuthDO.java 
package com.example;

import java.util.Date;
import lombok.Data;

@Data
public class UserAuthDO {

    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    private Long uid;

    private String passwd;

}

```

生成结果

### idea插件方式

TODO

### 一个plantuml编写的ER图 demo

```
@startuml
' 如果非视网膜屏，可以注释掉这一行
skinparam dpi 300
' 定义库关键字
!define Database(name) package name <<Rectangle>>
' 定义表关键字，包括了表名和描述
!define Table(name,desc) class name as "desc" << (T,#FFAAAA) >>

' 使用下划线表示不能为null
!define not_null(x) <u>x</u>
' 默认值
!define default(x) <color:gray>default x</color>

!define not_null <color:gray>not null</color>

' 字段类型定义，灰色表示
!define tinyint <color:gray>tinyint</color>
!define smallint <color:gray>smallint</color>
!define int <color:gray>int</color>
!define bigint <color:gray>bigint</color>
!define decimal <color:gray>decimal</color>
!define date <color:gray>date</color>
!define datetime <color:gray>datetime</color>
!define timestamp <color:gray>timestamp</color>
!define blob <color:gray>blob</color>
!define text <color:gray>text</color>
!define json <color:gray>json</color>
!define varchar(x) <color:gray>varchar[x]</color>

' 注释，斜体表示
!define comment(x) //x//

' 隐藏图标
hide circle

' 使用直角折线
skinparam linetype ortho

Database(user) #FBFBEF {

    Table(user_auth, user_auth\n(用户登录验证)) {
        id bigint <<AUTO_INCREMENT>>
        gmt_create datetime comment(创建时间)
        gmt_modified datetime comment(修改时间)
        uid bigint not_null comment(用户id)
        passwd varchar(50) comment(密码)
        --
        primary key (id)
        unique key uk_uid (uid)
    }
}

Database(org) #EFFBEF {

    Table(tenant, tenant\n(租户)) {
        id bigint <<AUTO_INCREMENT>>
        gmt_create datetime comment(创建时间)
        gmt_modified datetime comment(修改时间)
        tenant_id bigint not_null comment(租户id) <<PK>>
        name varchar(50) not_null comment(名称)
        owner_uid bigint not_null comment(拥有者)
        extension text comment(扩展信息)
        deleted tinyint default(0) comment(扩展信息)
        --
        primary key (id)
        unique key uk_tenant_id (tenant_id)
    }

    Table(org, org\n(组织)) {
        id bigint <<AUTO_INCREMENT>>
        gmt_create datetime not_null comment(创建时间)
        gmt_modified datetime not_null comment(修改时间)
        org_id bigint not_null comment(组织id) <<PK>>
        tenant_id bigint not_null comment(租户id)
        name varchar(50) not_null comment(组织名)
        brief varchar(500) comment(简介)
        extension text comment(扩展信息)
        --
        primary key (id)
        unique key uk_org_id (org_id)
        key idx_tenant_id (tenant_id)
        --扩展属性--
        extension.ownerStaffId comment(组织leader)
    }
}
@enduml
```

#### 示例说明

- Database层是可选的。Database层，可以适应大型系统，微服务、多模块的区分需求。也可以缺省，只有Table一层
- <<AUTO_INCREMENT>>只可用来修饰主键，对应mysql的AUTO_INCREMENT
- 需要使用--来分隔字段定义部分和索引定义部分
- 可以使用--增加扩展属性的定义部分

### 原理

- 解析ER图语义，转为程序里的对象描述
- 解析器使用按行解析的方式，非字符串拆解，兼容性更高
- 使用freemarker模板，类生成建表语句和DO类

