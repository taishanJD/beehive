package com.quarkdata.yunpan.api.model.bucket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 缓存bucket轮训数据
 * @author typ
 * 	2017年12月22日
 */
public class BucketRotationCache {
	/**
	 * key为组织id(incId)
	 * value为bucket轮训数据
	 */
	private static final ConcurrentMap<Long, BucketRotation> cache=new ConcurrentHashMap<Long, BucketRotation>();
	
	public static BucketRotation get(Long incId){
		return cache.get(incId);
	}
	
	public static void put(Long incId,BucketRotation bucketRotation){
		cache.put(incId, bucketRotation);
	}
	
}
