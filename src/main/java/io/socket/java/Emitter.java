package io.socket.java;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import io.socket.java.protocol.Packet;
import io.socket.java.protocol.PacketBinary;
import io.socket.java.protocol.PacketJson;
import io.socket.java.protocol.PacketText;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Emitter {

	/**
	 * Flags.
	 */
	private static enum Flag {
		JSON, VOLATILE, BROADCAST;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

	private static int EVENT = 2;
	private static int BINARY_EVENT = 5;

	private String key;
	private ArrayList<String> rooms = new ArrayList<String>();
	private HashMap<String, Object> flags = new HashMap<String, Object>();
	private JedisPool redis;

	private static Emitter instance = null;

	private Emitter(JedisPool redis, Map<String, String> opts) {
		if (opts.containsKey("key")) {
			this.key = opts.get("key").toString() + "#emitter";
		} else {
			this.key = "socket.io#";
		}

		if (redis == null) {
			if (!opts.containsKey("host"))
				throw new Error("Missing redis `host`");
			if (!opts.containsKey("port"))
				throw new Error("Missing redis `port`");

			this.redis = new JedisPool(new JedisPoolConfig(), opts.get("host"), Integer.parseInt(opts.get("port")));
		}
	}

	/**
	 * Socket.IO redis based emitter.
	 *
	 * @param redis redis client
	 * @param opts  option values
	 * @return emitter
	 */
	public static synchronized Emitter getInstance(JedisPool redis, Map<String, String> opts) {
		if (instance == null) {
			instance = new Emitter(redis, opts);
		}
		return instance;
	}

	/**
	 * Apply flags from `Socket`.
	 *
	 * @return emitter
	 */
	public Emitter json() {
		return get(Flag.JSON);
	}

	/**
	 * Apply flags from `Socket`.
	 *
	 * @return emitter
	 */
	public Emitter _volatile() {
		return get(Flag.VOLATILE);
	}

	/**
	 * Apply flags from `Socket`.
	 *
	 * @return emitter
	 */
	public Emitter broadcast() {
		return get(Flag.BROADCAST);
	}

	private Emitter get(Flag flag) {
		this.flags.put(flag.toString(), true);
		return this;
	}

	/**
	 * Limit emission to a certain `room`.
	 * 
	 * @param {String} room
	 */
	public Emitter to(String room) {
		if (!rooms.contains(room)) {
			this.rooms.add(room);
		}
		this.key+= room + "#";
		return this;
	};

	public Emitter in(String room) {
		return this.to(room);
	}

	/**
	 * Limit emission to certain `namespace`.
	 *
	 * @param {String} namespace
	 */

	public Emitter of(String nsp) {
		this.flags.put("nsp", nsp);
		this.key+= nsp + "#";
		return this;
	}

	public Emitter emit(String event, String... data) throws IOException {
		PacketText packet = new PacketText();
		packet.setType(EVENT);

		packet.getData().add(event);
		for (int i = 0; i < data.length; i++) {
			packet.getData().add(data[i]);
		}

		return this.emit(packet);
	}

	public Emitter emit(String event, JSONObject data) throws IOException {
		PacketJson packet = new PacketJson();
		packet.setType(EVENT);

		packet.setData(data);
		packet.setEvent(event);

		return this.emit(packet);
	}

	public Emitter emit(byte[] b) throws IOException {
		PacketBinary packet = new PacketBinary();
		packet.setType(BINARY_EVENT);
		packet.setData(b);

		return this.emit(packet);
	}

	private Emitter emit(Packet packet) throws IOException {

		if (this.flags.containsKey("nsp")) {
			packet.setNsp((String) this.flags.get("nsp"));
			this.flags.remove("nsp");
		} else {
			packet.setNsp("/");
		}

		packet.setRooms(this.rooms);
		packet.setFlags(this.flags);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MessagePack msgpack = new MessagePack();

		Packer packer = msgpack.createPacker(out);
		packer.write(packet);

		byte[] msg = out.toByteArray();

		Jedis jedis = null;

		try {
			jedis = this.redis.getResource();
			jedis.publish(this.key.getBytes(Charset.forName("UTF-8")), msg);
		} finally {
			if (jedis != null) {
				jedis.close();
			} else {
				this.redis.destroy();
			}
		}
		// reset state
		this.rooms = new ArrayList<String>();
		this.flags = new HashMap<String, Object>();
		this.key = "socket.io#";

		return this;
	}
}