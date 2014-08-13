package io.socket.java.protocol;

import java.io.IOException;

import org.msgpack.packer.Packer;


public class PacketBinary extends Packet {
	private byte[] data;
	
	public PacketBinary() {}
	
	protected void packData(Packer packer) throws IOException {
		packer.write(data);
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return this.data;
	}

	/**
	 * @param b the data to set
	 */
	public void setData(byte[] b) {
		this.data = b;
	}
}
