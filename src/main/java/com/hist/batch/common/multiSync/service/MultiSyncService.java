package com.hist.batch.common.multiSync.service;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hist.batch.common.multiSync.mapper.MultiSyncMapper;

/**
 * MultiSyncService
 *
 * @author dlehdusmusic@histmate.co.kr
 * @version 0.1
 * @since 2024.05.09
 */
@Service
public class MultiSyncService {

	private SqlSessionFactory multiSyncSqlSessionFactory;

    @Autowired
    public MultiSyncService(@Qualifier("MultiSyncSqlSessionFactory") SqlSessionFactory multiSyncSqlSessionFactory) {
        this.multiSyncSqlSessionFactory = multiSyncSqlSessionFactory;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public int insertMultiSync(Map<String, String> param) {
        try (SqlSession sqlSession = multiSyncSqlSessionFactory.openSession()) {
            MultiSyncMapper multiSyncMapper = sqlSession.getMapper(MultiSyncMapper.class);
            return multiSyncMapper.insertMultiSync(param);
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long selectMultiSyncEndCount(Map<String, String> param) {
        try (SqlSession sqlSession = multiSyncSqlSessionFactory.openSession()) {
            MultiSyncMapper multiSyncMapper = sqlSession.getMapper(MultiSyncMapper.class);
            return multiSyncMapper.selectMultiSyncEndCount(param);
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public long selectMultiSyncFailCount(Map<String, String> param) {
        try (SqlSession sqlSession = multiSyncSqlSessionFactory.openSession()) {
            MultiSyncMapper multiSyncMapper = sqlSession.getMapper(MultiSyncMapper.class);
            return multiSyncMapper.selectMultiSyncFailCount(param);
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public int updateMultiSyncStatus(Map<String, String> param) {
    	try (SqlSession sqlSession = multiSyncSqlSessionFactory.openSession()) {
            MultiSyncMapper multiSyncMapper = sqlSession.getMapper(MultiSyncMapper.class);
            return multiSyncMapper.updateMultiSyncStatus(param);
        }
    }
}