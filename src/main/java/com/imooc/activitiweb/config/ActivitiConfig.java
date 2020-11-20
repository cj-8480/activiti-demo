package com.imooc.activitiweb.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// TODO 下面的包按照项目路径重新引入
import com.imooc.activitiweb.listener.activiti.E3PlusActivitiFlowListener;
import com.imooc.activitiweb.listener.activiti.E3PlusActivitiProcessListener;
import com.imooc.activitiweb.listener.activiti.E3PlusActivitiTaskListener;

/**
 * 工作流引擎全局配置
 * 
 * @author jun.chen
 * @Date 2020-11-17 10:13:01
 * @Description
 *
 */
@Component
public class ActivitiConfig implements ProcessEngineConfigurationConfigurer {

	@Autowired
	private E3PlusActivitiProcessListener processListener;

	@Autowired
	private E3PlusActivitiTaskListener taskListener;

	@Autowired
	private E3PlusActivitiFlowListener flowListener;

	private static List<ActivitiEventType> processEventTypeList;
	private static List<ActivitiEventType> taskEventTypeList;
	private static List<ActivitiEventType> sequenceFlowEventTypeList;

	static {
		// 流程开始，流程完成，流程异常结束，流程取消
		// PROCESS_STARTED,PROCESS_COMPLETED,PROCESS_COMPLETED_WITH_ERROR_END_EVENT,PROCESS_CANCELLED
		processEventTypeList = Arrays.asList(ActivitiEventType.PROCESS_STARTED, ActivitiEventType.PROCESS_COMPLETED,
				ActivitiEventType.PROCESS_COMPLETED_WITH_ERROR_END_EVENT, ActivitiEventType.PROCESS_CANCELLED);
		// 签收，任务创建，任务完成
		// TASK_ASSIGNED,TASK_CREATED,TASK_COMPLETED
		taskEventTypeList = Arrays.asList(ActivitiEventType.TASK_ASSIGNED, ActivitiEventType.TASK_CREATED,
				ActivitiEventType.TASK_COMPLETED);
		sequenceFlowEventTypeList = Arrays.asList(ActivitiEventType.SEQUENCEFLOW_TAKEN);

	}

	@Override
	public void configure(SpringProcessEngineConfiguration configuration) {
		Map<String, List<ActivitiEventListener>> typedListeners = configuration.getTypedEventListeners();
		if (null == typedListeners) {
			typedListeners = new HashMap<>();
		}
		// 配置节点监听ActivitiEventType
		typedListeners.put(gainKey(processEventTypeList), Arrays.asList(processListener));
		typedListeners.put(gainKey(taskEventTypeList), Arrays.asList(taskListener));
		typedListeners.put(gainKey(sequenceFlowEventTypeList), Arrays.asList(flowListener));

		configuration.setTypedEventListeners(typedListeners);
	}

	/**
	 * 按照类型组装key
	 * 
	 * @param list
	 * @return
	 */
	private String gainKey(List<ActivitiEventType> list) {
		StringBuilder keyBuilder = new StringBuilder();
		boolean isFirst = true;
		for (ActivitiEventType type : list) {
			if (isFirst) {
				isFirst = false;
			} else {
				keyBuilder.append(",");
			}
			keyBuilder.append(type);
		}
		return keyBuilder.toString();
	}

}
