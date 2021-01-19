package io.socket.java.protocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.msgpack.MessagePackable;
import org.msgpack.annotation.Message;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

@Message // Annotation
public abstract class Packet implements MessagePackable {
	protected int type; 
	protected String nsp = "/";
	
	protected ArrayList<String> rooms = new ArrayList<String>();
	protected HashMap<String,Object> flags = new HashMap<String,Object>();
	
	public Packet() {}

	public void readFrom(Unpacker arg0) throws IOException {
		throw new UnsupportedOperationException();
		
	}

	public void writeTo(Packer packer) throws IOException {
		packer.writeArrayBegin(3);
		packer.write("emitter");		
		packer.writeMapBegin(3);
		packer.write("type");
		packer.write(this.type);
		packer.write("data");
		this.packData(packer);
		
		packer.write("nsp");
		packer.write(this.nsp);
		packer.writeMapEnd(true);
		

		packer.writeMapBegin(2);
		
		packer.write("rooms");
		packer.writeArrayBegin(rooms.size());
		for(String room : rooms) {
			packer.write(room);
		}
		packer.writeArrayEnd(true);
		
		packer.write("flags");
		packer.writeMapBegin(flags.size());
		Iterator<Entry<String, Object>> it = flags.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, Object> pairs = (Map.Entry<String, Object>)it.next();
	        packer.write(pairs.getKey());
	        packer.write( pairs.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		packer.writeMapEnd(true);
		
		packer.writeMapEnd(true);
		
		packer.writeArrayEnd(true);
		
	}

	protected abstract void packData(Packer packer) throws IOException;

	
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the nsp
	 */
	public String getNsp() {
		return nsp;
	}

	/**
	 * @param nsp the nsp to set
	 */
	public void setNsp(String nsp) {
		this.nsp = nsp;
	}

	/**
	 * @return the rooms
	 */
	public ArrayList<String> getRooms() {
		return rooms;
	}

	/**
	 * @param rooms the rooms to set
	 */
	public void setRooms(ArrayList<String> rooms) {
		this.rooms = rooms;
	}

	/**
	 * @return the flags
	 */
	public HashMap<String,Object> getFlags() {
		return flags;
	}

	/**
	 * @param flags the flags to set
	 */
	public void setFlags(HashMap<String,Object> flags) {
		this.flags = flags;
	}
}
