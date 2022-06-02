package com.boden.lingvolearner;

public class Dict {
	private String path;
	private String name;
	private int beginFrom;
	public Dict(){}
	public Dict(String s){
		path=s.substring(s.indexOf("<path>")+6, s.indexOf("</path>"));
		name=s.substring(s.indexOf("<name>")+6, s.indexOf("</name>"));
		beginFrom=Integer.parseInt(s.substring(s.indexOf("<from>")+6, s.indexOf("</from>")));		
	}
	public Dict(String path, String name){
		this.path=path;
		this.name=name;
		this.beginFrom=0;		
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getBeginFrom() {
		return beginFrom;
	}
	public void setBeginFrom(int beginFrom) {
		this.beginFrom = beginFrom;
	}
	@Override
	public String toString() {
		return "<dict><path>" + path + "</path><name>" + name + "</name><from>"
				+ beginFrom + "</from></dict>";
	}

}