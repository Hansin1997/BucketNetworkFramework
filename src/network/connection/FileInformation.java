package network.connection;

import Common.Tool;

public class FileInformation {
	
	public String path;
	public long size;
	
	public FileInformation(String path,long size) {
		setPath(path);
		setSize(size);
	}
	
	public FileInformation() {
		this("",0);
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
	
	public void setSize(long size) {
		this.size = size;
	}
	
	public long getSize() {
		return size;
	}
	
	public String toJSON(){
		return Tool.toJson(this);
	}
	
	public static FileInformation fromJSON(String json){
		return Tool.JSON2E(json, FileInformation.class);
	}
	
}
