package com.imooc.activitiweb.config;

import java.util.List;

import org.flowable.common.engine.impl.persistence.entity.Entity;
import org.flowable.mongodb.persistence.manager.AbstractMongoDbDataManager;
import org.flowable.variable.service.impl.persistence.entity.VariableByteArrayEntity;
import org.flowable.variable.service.impl.persistence.entity.VariableByteArrayEntityImpl;
import org.flowable.variable.service.impl.persistence.entity.data.VariableByteArrayDataManager;
import org.flowable.variable.service.impl.persistence.entity.data.impl.cachematcher.VariableInstanceByExecutionIdMatcher;

import com.mongodb.BasicDBObject;

public class MongoDbVariableByteArrayDataManager extends AbstractMongoDbDataManager<VariableByteArrayEntity>
		implements VariableByteArrayDataManager {
	public static final String COLLECTION_BYTE_ARRAYS = "byteArrays";

	protected VariableInstanceByExecutionIdMatcher variableInstanceByExecutionIdMatcher = new VariableInstanceByExecutionIdMatcher();

	@Override
	public String getCollection() {
		return COLLECTION_BYTE_ARRAYS;
	}

	@Override
	public VariableByteArrayEntity create() {
		return new VariableByteArrayEntityImpl();
	}

	@Override
	public BasicDBObject createUpdateObject(Entity entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<VariableByteArrayEntity> findAll() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteByteArrayNoRevisionCheck(String byteArrayEntityId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
}
