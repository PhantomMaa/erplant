# 介绍
以一种数据格式为蓝本，通过url-gencode工具定制代码的行为，来生成出来java工程常用的多个层次的代码。
![](https://cdn.nlark.com/yuque/0/2023/png/253086/1695969239128-dcadce5a-0a07-4be5-a5fb-7aaeeaf88a6d.png#averageHue=%23fcfaf9&clientId=u0efd1331-81e7-4&from=paste&id=u05a45127&originHeight=948&originWidth=2208&originalType=url&ratio=1&rotation=0&showTitle=false&status=done&style=none&taskId=u3907c8f3-6700-4d89-9c6b-4393fcc3fa5&title=)

# 主要意义
- 增强的er图。一份蓝本可以看到整个系统的业务模型设计全貌
- 以er图为输入，由uml-gencode工具生成java工程中多个层次的代码
- 可以在plantuml er图源文件上，加入自定义标记，来扩展工具生成的逻辑，工具具备向前进化演进的特点
- 生成代码的逻辑，引入了DDD的概念和一些最佳实践。如：
   - 实体和数据表的分离
   - 仓储类。根据表的类型，如实体表、关系表、子表等业务含义，相应生成的仓储类具备的功能也有所不同
   - 每个实体都业务主键
   - 防重复提交、幂等特性等
- 生成代码自动具备的业务开发常用功能
   - DDD仓储模式推荐的常用操作：根据表类型生成 get、save、remove、count、batchGet等功能方法
   - 两种分页方式。基于页码、基于位移。[open api统一分页方式](https://automq66.feishu.cn/wiki/CaJUwVYheiQ7LskaLr2cOMLIn8e)
   - 通过index索引，或者指定查询列，可以自动生成相应的Query方法
- 底层依赖mybatis，自动生成DAO层代码。包含xml mapper、DAO类、DAOTest类。通过Test类确保生成的dao层逻辑的正确性
- 可以利用数据表的json字段动态schema的特性，将json字段里的属性和数据表里的字段一起成为实体类的属性，可以使传统关系型数据库强schema特性之外，还具备类似mangoDB free schema的特点。方便业务开发过程中增减属性
- 业务模型的注释有一个统一存放的地方。省去了DO、ddl建表语句等文件的comment

# 搭配的DDD概念模型
![](https://cdn.nlark.com/yuque/0/2023/png/253086/1695969239134-48f23302-26a8-4434-a0ab-e1dd394601fd.png#averageHue=%23a98b51&clientId=u0efd1331-81e7-4&from=paste&id=ucfc4e5c0&originHeight=1884&originWidth=2398&originalType=url&ratio=1&rotation=0&showTitle=false&status=done&style=none&taskId=u18700b2b-877d-47f4-b7c9-35ce31b37e1&title=)

# 使用
## 前置准备
安装graphviz，在idea中打开puml文件显示预览图片

mac安装方式。其他可参考：https://plantuml.com/graphviz-dot
```
brew install libtool
brew link libtool
brew install graphviz
brew link --overwrite graphviz
```

## 使用形式
引入maven-plugin。工作方式类似grpc-plugin、lombok等。将代码生成到target/generated-sources目录下，避免工具生成的代码被人工二次修改带来的问题。
这种定位下，每次mvn编译，全部重新生成所有层次代码。puml原文件 + 工具 变化，会印象生成的代码产生变化。
由 编译通过，和生成的DAOTest层代码测试通过，这两个途径来保证生成代码的正确性。
*.puml --> maven-plugin --> target/generated-sources/
```
<plugin>
    <groupId>com.automq</groupId>
    <artifactId>uml-gencode-maven-plugin</artifactId>
    <version>1.0.1</version>
    <configuration>
        <packageName>com.hellocorp.automq.starter</packageName>
        <srcPuml>automq-task.puml</srcPuml>
        <generateTypes>DO,DAO,BK,PageNumQuery,OffsetQuery,Mapper,Ddl,DaoTest</generateTypes>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
可以通过generateTypes指定需要生成代码的种类。

## 一个puml源文件示例
![](https://cdn.nlark.com/yuque/0/2023/png/253086/1695969239132-a231a175-2e27-4428-a474-64ac61e4e0bc.png#averageHue=%23dddddd&clientId=u0efd1331-81e7-4&from=paste&id=u0c439584&originHeight=644&originWidth=526&originalType=url&ratio=1&rotation=0&showTitle=false&status=done&style=none&taskId=u1abc0df7-0ce4-421b-87f8-2d64d5bf178&title=)

## 保留可扩展的能力
工具生成的代码，不可能100%满足真实多变的业务场景，所以需要提供扩展的方式。
可以在以下几个层次，编写自己的扩展类。扩展类具备其继承父类的基本功能，同时可以有自己特殊自定义的部分。

- DAO层 
- Repository层
- Converter层

---
里面对于DDD的一些实现原则，可以参考以下几篇文章：
- [DDD系列 第一讲 - Domain Primitive](https://mp.weixin.qq.com/s/kpXklmidsidZEiHNw57QAQ)
- [DDD系列 第二讲 - 应用架构](https://mp.weixin.qq.com/s/MU1rqpQ1aA1p7OtXqVVwxQ)
- [DDD系列 第三讲 - Repository模式](https://mp.weixin.qq.com/s/1bcymUcjCkOdvVygunShmw)
- [DDD系列 第四讲 - 领域层设计规范](https://mp.weixin.qq.com/s/w1zqhWGuDPsCayiOgfxk6w)
- [DDD系列 第五讲 - 聊聊如何避免写流水账代码](https://mp.weixin.qq.com/s/1rdnkROdcNw5ro4ct99SqQ)
- [DDD 中的那些模式 — CQRS](https://zhuanlan.zhihu.com/p/115685384)
- [A Beginner's Guide to CQRS](https://www.eventstore.com/cqrs-pattern)
