# limbo-stomp-broker
基于Kafka的STOMP代理服务，可用于IM消息后台

# RoadMap

## kafka-broker-server
实现一个STOMP服务器的基本功能，解析STOMP1.2协议中的所有帧。

## routing-server
路由服务，用于连接到broker的客户端路由，以及broker-server启动时与Kafka队列直接的绑定（发号器服务器）

## routing-client
routing-server的客户端侧，用于kafka-broker-server依赖

## kafka-broker-server
实现被代理侧的连接接入、用户信息广播、用户路由信息保存、用户消息转发等功能。
被代理侧可以是另一个基于STOMP协议的服务器（如：spring-messaging使用了STOMP时）， 也可以直接是STOMP连接的用户侧，如浏览器通过websocket使用STOMP。