package com.imooc.activitiweb.config;

import org.flowable.mongodb.cfg.MongoDbVariableServiceConfiguration;

public class CusMongoDbVariableServiceConfiguration extends MongoDbVariableServiceConfiguration {

	@Override
	public void initDataManagers() {
		super.initDataManagers();

		MongoDbVariableByteArrayDataManager mongoDbVariableByteArrayDataManager = new MongoDbVariableByteArrayDataManager();
		mongoDbSessionFactory.registerDataManager(MongoDbVariableByteArrayDataManager.COLLECTION_BYTE_ARRAYS,
				mongoDbVariableByteArrayDataManager);
		this.byteArrayDataManager = mongoDbVariableByteArrayDataManager;

	}

}
