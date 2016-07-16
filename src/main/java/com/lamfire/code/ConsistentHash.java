package com.lamfire.code;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.lamfire.utils.Maps;

public class ConsistentHash<T extends ConsistentHash.Node> {
	private static final Integer VIRTUAL_NODE_COUNT = 160;
	private static final byte[] NULL_DIGEST = MD5.digest("".getBytes());
	private final TreeMap<Long,T> virtualNodes = Maps.newTreeMap();
	
	private long hash(byte[] digest, int nTime) {
		long rv = ((long) (digest[3+nTime*4] & 0xFF) << 24)
				| ((long) (digest[2+nTime*4] & 0xFF) << 16)
				| ((long) (digest[1+nTime*4] & 0xFF) << 8)
				| (digest[0+nTime*4] & 0xFF);
		
		return rv & 0xffffffffL;
	}
	
	private byte[] digest(String source){
		if(source == null){
			return NULL_DIGEST;
		}
		return MD5.digest(source.getBytes(Charset.forName("utf-8")));
	}
		
	public ConsistentHash(List<T> nodes) {
		this(nodes,VIRTUAL_NODE_COUNT);
    }
	
	public ConsistentHash(List<T> nodes,int virtualNodeCount) {
		for (T node : nodes) {
			for (int i = 0; i < virtualNodeCount / 4; i++) {
				byte[] digest = digest(node.getName() + i);
				for(int h = 0; h < 4; h++) {
					long m = hash(digest, h);
					virtualNodes.put(m, node);
				}
			}
		}
    }
	
	public Map<Long, T> getVirtualNodes(){
		return Collections.unmodifiableMap(virtualNodes);
	}
	
	public T getPrimary(final String key) {
		byte[] digest = digest(key);
		return getPrimaryNode(hash(digest, 0));
	}
	
	public T getSecondary(final String key) {
		byte[] digest = digest(key);
		return getSecondaryNode(hash(digest, 0));
	}
	
	private Long getPrimaryNodeKey(long hash){
		if(virtualNodes.containsKey(hash)) {
			return hash;
		}
		SortedMap<Long, T> tailMap=virtualNodes.tailMap(hash);
		if(!tailMap.isEmpty()) {
			return tailMap.firstKey();
		}
		return virtualNodes.firstKey();
	}

	private T getPrimaryNode(long hash) {
		return virtualNodes.get(getPrimaryNodeKey(hash));
	}
	
	private T getSecondaryNode(long hash) {
		Node primary = getPrimaryNode(hash);
		Long nodekey = getPrimaryNodeKey(hash);
		SortedMap<Long, T> tailMap=virtualNodes.tailMap(nodekey);
		for(Map.Entry<Long, T> e : tailMap.entrySet()){
			if(e.getValue() != primary){
				return e.getValue();
			}
		}
		return virtualNodes.firstEntry().getValue();
	}
	
	public static interface Node{
		public String getName();
	}
	
	public static class HashNode implements Node{
		private String name;
		
		public HashNode(){}
		
		public HashNode(String name){
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return "Node [name=" + name + "]";
		}
	}
}
