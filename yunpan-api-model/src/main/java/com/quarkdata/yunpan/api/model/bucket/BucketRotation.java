package com.quarkdata.yunpan.api.model.bucket;
/**
 * bucket轮训
 * @author typ
 * 	2017年12月22日
 */
public class BucketRotation {
	private String[] buckets;
	
	private int index=0;

	public BucketRotation(String[] buckets) {
		super();
		this.buckets = buckets;
	}
	
	public synchronized String getBucket(){
		if(index>buckets.length-1){
			index=0;
		}
		String bucket=buckets[index++];
		return bucket;
	}
}
