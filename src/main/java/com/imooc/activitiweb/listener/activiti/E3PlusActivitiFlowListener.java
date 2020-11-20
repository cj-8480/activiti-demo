package com.imooc.activitiweb.listener.activiti;

import java.io.Serializable;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.impl.ActivitiSequenceFlowTakenEventImpl;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jun.chen
 * @Date 2020-11-17 10:08:39
 * @Description 全流程线监听
 *
 */
@Slf4j
@Component
public class E3PlusActivitiFlowListener implements ActivitiEventListener, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void onEvent(ActivitiEvent event) {

		if (null == event.getType()) {
			return;
		}

		ActivitiSequenceFlowTakenEventImpl impl = null;
		if (event instanceof ActivitiSequenceFlowTakenEventImpl) {
			impl = (ActivitiSequenceFlowTakenEventImpl) event;
		}

		log.info("线监听\t当前Type:" + event.getType());
		log.info(String.format("Source:%s(%s) Target:%s(%s)", impl.getSourceActivityName(),
				impl.getSourceActivityType(), impl.getTargetActivityName(), impl.getTargetActivityType()));

	}

	@Override
	public boolean isFailOnException() {
		return false;
	}
}
