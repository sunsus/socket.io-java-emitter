socket.io-java-emitter
======================

[![Build Status](https://travis-ci.org/sunsus/socket.io-java-emitter.svg)](https://travis-ci.org/sunsus/socket.io-java-emitter)

A Java implementation of socket.io-emitter

This project uses [msgpack-java](https://github.com/msgpack/msgpack-java) and [phpredis](https://github.com/nicolasff/phpredis).

## Usage

### Using an existing redis instance
```java
JedisPool jedis = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379);
Emitter emitter = Emitter.getInstance(jedis, new HashMap<String,String>());
Emitter emitter = Emitter.getInstance(null, opts);
emitter.of("/namespace").emit("event", "Hello World!");
```

### Emitting without manually creating a redis instance
```java
HashMap<String,String> opts = new HashMap<String,String>();
opts.put("host", "127.0.0.1");
opts.put("port", "6379");
Emitter emitter = Emitter.getInstance(null, opts);
emitter.of("/namespace").emit("event", "Hello World!");
```

### Broadcasting and other flags
Possible flags
*  json
*  volatile
*  broadcast

```java
Emitter emitter = Emitter.getInstance(null, ImmutableMap.of("host", "127.0.0.1", "port", "6379"));
// broadcast can be replaced by any of the other flags
emitter.of("/namespace").broadcast().emit("event", "Hello World!");
```

### Emitting objects
```java
Emitter emitter = Emitter.getInstance(null, ImmutableMap.of("host", "127.0.0.1", "port", "6379"));
String jsonData = "{\"test\":[\"test\",\"test1\"],\"name\":\"xxxxx\",\"id\":1234,\"float\":1234.00,\"bool\":true,\"object\":{\"name\":\"xxxxx\",\"id\":1234}}";
JSONObject jsonRoot = new JSONObject(jsonData);
emitter.of("/namespace").emit("event", jsonRoot);
```


## Dependencies

This module requires a [Redis][redis] to be available on the network.


### License

Copyright &copy; 2014 sunsus GmbH

Distributed under the [MIT License][mit].

[Redis]: http://redis.io/
[MIT]: http://www.opensource.org/licenses/mit-license.php
