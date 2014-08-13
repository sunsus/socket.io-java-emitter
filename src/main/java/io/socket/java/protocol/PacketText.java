package io.socket.java.protocol;

import java.io.IOException;
import java.util.ArrayList;

import org.msgpack.packer.Packer;


public class PacketText extends Packet {
	protected ArrayList<String> data = new ArrayList<String>();	

	public PacketText() {}
	
	protected void packData(Packer packer) throws IOException {
		packer.writeArrayBegin(data.size());
		for(String dataRecord : data) {
			packer.write(dataRecord);
		}
		packer.writeArrayEnd(true);
	}
	
	/**
	 * @return the data
	 */
	public ArrayList<String> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(ArrayList<String> data) {
		this.data = data;
	}
}
