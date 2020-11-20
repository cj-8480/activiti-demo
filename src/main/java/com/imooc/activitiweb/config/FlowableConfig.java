package com.imooc.activitiweb.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.mongodb.cfg.MongoDbProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.imooc.activitiweb.listener.activiti.E3PlusFlowableProcessListener;
import com.imooc.activitiweb.listener.activiti.E3PlusFlowableTaskListener;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Configuration
public class FlowableConfig {

	private static List<FlowableEngineEventType> processEventTypeList;
	private static List<FlowableEngineEventType> taskEventTypeList;

	static {
		// 流程开始，流程完成，流程异常结束，流程取消
		// PROCESS_STARTED,PROCESS_COMPLETED,PROCESS_COMPLETED_WITH_ERROR_END_EVENT,PROCESS_CANCELLED
		processEventTypeList = Arrays.asList(FlowableEngineEventType.PROCESS_STARTED,
				FlowableEngineEventType.PROCESS_COMPLETED,
				FlowableEngineEventType.PROCESS_COMPLETED_WITH_ERROR_END_EVENT,
				FlowableEngineEventType.PROCESS_CANCELLED);
		// 签收，任务创建，任务完成
		// TASK_ASSIGNED,TASK_CREATED,TASK_COMPLETED
		taskEventTypeList = Arrays.asList(FlowableEngineEventType.TASK_ASSIGNED, FlowableEngineEventType.TASK_CREATED,
				FlowableEngineEventType.TASK_COMPLETED);

	}

	/**
	 * 按照类型组装key
	 * 
	 * @param list
	 * @return
	 */
	private String gainKey(List<FlowableEngineEventType> list) {
		StringBuilder keyBuilder = new StringBuilder();
		boolean isFirst = true;
		for (FlowableEngineEventType type : list) {
			if (isFirst) {
				isFirst = false;
			} else {
				keyBuilder.append(",");
			}
			keyBuilder.append(type);
		}
		return keyBuilder.toString();
	}

	private Map<String, List<FlowableEventListener>> gainMap() {
		Map<String, List<FlowableEventListener>> typedEventListeners = new HashMap<>();
		// 配置节点监听ActivitiEventType
		typedEventListeners.put(gainKey(processEventTypeList), Arrays.asList(processListener));
		typedEventListeners.put(gainKey(taskEventTypeList), Arrays.asList(taskListener));
		return typedEventListeners;
	}

	@Autowired
	private E3PlusFlowableProcessListener processListener;

	@Autowired
	private E3PlusFlowableTaskListener taskListener;

	@Bean(name = "default_engine")
	public ProcessEngine initDefaultProcessEngine() {
		ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration().setJdbcUrl(
				"jdbc:mysql://192.168.150.48:3306/flowable-demo?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true")
				.setJdbcUsername("root").setJdbcPassword("root").setJdbcDriver("com.mysql.cj.jdbc.Driver")

				// 如果数据表不存在的时候，自动创建数据表
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		Map<String, List<FlowableEventListener>> typedEventListeners = cfg.getTypedEventListeners();
		if (null == typedEventListeners) {
			typedEventListeners = new HashMap<>();
		}
		typedEventListeners.putAll(gainMap());

		cfg.setTypedEventListeners(typedEventListeners);
		// 执行完成后，就可以开始创建我们的流程了 ProcessEngine processEngine =
		ProcessEngine engine = cfg.buildProcessEngine();
		return engine;
	}

	@Bean(name = "mongodb_engine")
	public ProcessEngine initMongoDbProcessEngine() {
		MongoDbProcessEngineConfiguration cfg = new CusMongoDbProcessEngineConfiguration();
		Map<String, List<FlowableEventListener>> typedEventListeners = cfg.getTypedEventListeners();
		if (null == typedEventListeners) {
			typedEventListeners = new HashMap<>();
		}
		typedEventListeners.putAll(gainMap());

		MongoClient mongoClient = // new MongoClient("192.168.150.48");
				new MongoClient(new MongoClientURI("mongodb://host.mongo.internal:27017/flowable?ssl=false"));

		ProcessEngine processEngine = cfg.setMongoClient(mongoClient)
				// .setServerAddresses(Arrays.asList(new ServerAddress("host.mongo.internal",
				// 27017)))
				.setDisableIdmEngine(true).buildProcessEngine();
		return processEngine;
	}
}
