package com.imooc.activitiweb.listener.activiti;

import java.io.Serializable;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.delegate.event.impl.ActivitiProcessStartedEventImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jun.chen
 * @Date 2020-11-17 10:08:39
 * @Description 全流程监听
 *
 */
@Slf4j
@Component
public class E3PlusActivitiProcessListener implements ActivitiEventListener, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void onEvent(ActivitiEvent event) {

		if (null == event.getType()) {
			return;
		}

		log.info("流程监听\t当前Type:" + event.getType());

		ExecutionEntity executionEntity = null;
		if (event instanceof ActivitiProcessStartedEventImpl) {
			Object entity = ((ActivitiProcessStartedEventImpl) event).getEntity();
			if (entity instanceof ExecutionEntity) {
				executionEntity = (ExecutionEntity) entity;
			}
		} else if (event instanceof ActivitiEntityEventImpl) {
			Object entity = ((ActivitiEntityEventImpl) event).getEntity();
			if (entity instanceof ExecutionEntity) {
				executionEntity = (ExecutionEntity) entity;
			}
			
			if(ActivitiEventType.PROCESS_COMPLETED.equals(event.getType())) {
				log.info(String.format("流程【%s】 结束！", executionEntity.getName(),
						executionEntity.getProcessDefinitionKey()));
			}
		}

		if (null == executionEntity) {
			return;
		}

		log.info(String.format("流程【%s】 => formKey:%s,", executionEntity.getName(),
				executionEntity.getProcessDefinitionKey()));
		
		switch (event.getType()) {
		case PROCESS_STARTED:
			start();
			break;
		case PROCESS_COMPLETED:
			completed();
			break;
		case PROCESS_COMPLETED_WITH_ERROR_END_EVENT:
			completedWithError();
			break;
		case PROCESS_CANCELLED:
			cancled();
			break;
		default:
			break;
		}
	}

	private void start() {
		// TODO 完善流程启动时额外的事件处理

	}

	private void completed() {
		// TODO 完善流程结束时额外的事件处理

	}
	
	private void completedWithError() {
		// TODO 完善流程异常结束时额外的事件处理
		
	}

	private void cancled() {
		// TODO 完善流程取消时额外的事件处理

	}
	
	@Override
	public boolean isFailOnException() {
		return false;
	}
}
