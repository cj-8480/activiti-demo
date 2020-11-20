package com.imooc.activitiweb.config;

import org.flowable.mongodb.cfg.MongoDbProcessEngineConfiguration;
import org.flowable.variable.service.VariableServiceConfiguration;

public class CusMongoDbProcessEngineConfiguration extends MongoDbProcessEngineConfiguration{
	
    @Override
    protected VariableServiceConfiguration instantiateVariableServiceConfiguration() {
    	CusMongoDbVariableServiceConfiguration mongoDbVariableServiceConfiguration = new CusMongoDbVariableServiceConfiguration();
        mongoDbVariableServiceConfiguration.setMongoDbSessionFactory(mongoDbSessionFactory);
        return mongoDbVariableServiceConfiguration;
    }

}
