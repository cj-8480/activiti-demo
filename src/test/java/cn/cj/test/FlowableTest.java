package cn.cj.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.imooc.activitiweb.ActivitiwebApplication;
import com.imooc.activitiweb.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ActivitiwebApplication.class })
@Slf4j
public class FlowableTest {

	@Autowired
	@Qualifier("default_engine")
	private ProcessEngine defaultEngine;

	@Autowired
	@Qualifier("mongodb_engine")
	private ProcessEngine mongodbEngine;

	@Autowired
	private SecurityUtil securityUtil;

	private String deploymentId;
	private String definitionId;
	private String processInstanceId;

	private ProcessEngine currentEngine;
	private RepositoryService repositoryService;
	private RuntimeService runtimeService;
	private TaskService taskService;
	private boolean isMongo = true;

	private void initDefault() {
		if (!isMongo) {
			return;
		}
		Model model = defaultEngine.getRepositoryService().createModelQuery().orderByCreateTime().desc().list().get(0);
		// model发布
		byte[] source = defaultEngine.getRepositoryService().getModelEditorSource(model.getId());

		Model newModel = repositoryService.newModel();
		newModel.setMetaInfo("123");
		newModel.setVersion(1000);
		newModel.setCategory("default");
		repositoryService.saveModel(newModel);
		String modelId = newModel.getId();
		// 存储模型
		repositoryService.addModelEditorSource(modelId, source);
	}
	
	@Before
	public void before() {
		currentEngine = mongodbEngine;
		repositoryService = currentEngine.getRepositoryService();
		runtimeService = currentEngine.getRuntimeService();
		taskService = currentEngine.getTaskService();
		initDefault();
		securityUtil.logInAs("bajie");
		Model model = repositoryService.createModelQuery().orderByCreateTime().desc().list().get(0);
		// model发布
		byte[] source = repositoryService.getModelEditorSource(model.getId());
//		log.info(new String(source));
		Deployment deploy = repositoryService.createDeployment().category("Mongo").name("测试流程Demo")
				.addBytes("demo.bpmn", source).deploy();
		deploymentId = deploy.getId();
		log.info("deploymentId:" + deploymentId);
		// 获取流程发布对应的流程定义
		ProcessDefinition singleResult = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId)
				.singleResult();
		definitionId = singleResult.getId();
		log.info("definitionKey:" + definitionId);
	}

	@Test
	public void testFlowable() {

		Map<String, Object> variables = new HashMap<>();
		ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder().name("阿俊Demo流程实例")
				.businessKey("AD1234").variables(variables).processDefinitionId(definitionId).start();
		processInstanceId = processInstance.getId();
		log.info("processInstanceId:" + processInstanceId + " Name:" + processInstance.getName());
		List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId)
				.processDefinitionId(definitionId).deploymentId(deploymentId).list();
		Boolean firstLeaderAudit = true;
		Boolean firstHrAudit = true;
		while (!list.isEmpty()) {
			for (Task task : list) {
				String id = task.getId();
				if (task.getTaskDefinitionKey().equals("Activity_04dxo3m")) {
					JSONObject json = new JSONObject();
					json.put("select", 1);
					if (firstLeaderAudit) {
						// 首次拒绝
						firstLeaderAudit = false;
						json.put("select", 0);
					}
					taskService.setVariable(id, "task_leader", json);

				} else if (task.getTaskDefinitionKey().equals("Activity_0ahf6v6")) {
					JSONObject json = new JSONObject();
					json.put("select", 1);
					if (firstHrAudit) {
						// 首次拒绝
						firstHrAudit = false;
						json.put("select", 0);
					}
					taskService.setVariable(id, "task_hr", json);
				}
				log.info("taskId:" + task.getId() + "\tName:" + task.getName());
				taskService.complete(task.getId());
			}
			list = taskService.createTaskQuery().processInstanceId(processInstanceId).processDefinitionId(definitionId)
					.deploymentId(deploymentId).list();
		}
	}

	@After
	public void after() {
		repositoryService.deleteDeployment(deploymentId);
	}

}
