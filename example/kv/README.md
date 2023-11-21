# KV存储服务

基于关系型数据库实现KV存储服务，减少对其它组件的依赖。主要解决在私有部署的场景，只依赖一个数据库，提供一些通用能力。

如：
- 配置服务。nacos、k8s configmap
- 缓存。redis、leveldb、oss