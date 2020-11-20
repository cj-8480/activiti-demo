package com.imooc.activitiweb.listener.activiti;

import java.io.Serializable;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
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
public class E3PlusActivitiTaskListener implements ActivitiEventListener, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void onEvent(ActivitiEvent event) {

		if (null == event.getType()) {
			return;
		}

		log.info("任务监听\t当前Type:" + event.getType());

		TaskEntity taskEntity = null;
		if (event instanceof ActivitiEntityEventImpl) {
			Object entity = ((ActivitiEntityEventImpl) event).getEntity();
			if (entity instanceof TaskEntity) {
				taskEntity = (TaskEntity) entity;
			}
		}

		if (null == taskEntity) {
			return;
		}

		log.info(String.format("任务【%s】 => formKey:%s,", taskEntity.getName(), taskEntity.getTaskDefinitionKey()));

		switch (event.getType()) {
		case TASK_CREATED:
			taskCreate(taskEntity);
			break;
		case TASK_ASSIGNED:
			taskAssigned(taskEntity);
			break;
		case TASK_COMPLETED:
			taskCompleted(taskEntity);
			break;
		default:
			break;
		}

	}

	private void taskCreate(TaskEntity taskEntity) {
		// TODO 任务创建时处理的额外事件

	}

	private void taskAssigned(TaskEntity taskEntity) {
		// TODO 任务签收时处理的额外事件

	}

	private void taskCompleted(TaskEntity taskEntity) {
		// TODO 任务完成时处理的额外事件

	}

	@Override
	public boolean isFailOnException() {
		return false;
	}
}
