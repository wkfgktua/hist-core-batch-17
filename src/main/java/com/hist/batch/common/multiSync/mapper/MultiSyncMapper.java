package com.hist.batch.common.multiSync.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MultiSyncMapper {

	public int insertMultiSync(Map<String, String> param);

	public long selectMultiSyncEndCount(Map<String, String> param);

	public long selectMultiSyncFailCount(Map<String, String> param);

	public int updateMultiSyncStatus(Map<String, String> param);
}
