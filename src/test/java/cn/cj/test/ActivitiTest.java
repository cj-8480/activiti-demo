package cn.cj.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.imooc.activitiweb.ActivitiwebApplication;
import com.imooc.activitiweb.SecurityUtil;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ActivitiwebApplication.class })
@Slf4j
public class ActivitiTest {

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;
	
	@Autowired
	private HistoryService historyService;

	@Autowired
	private SecurityUtil securityUtil;

	private String deploymentId;
	private String definitionId;
	private String processInstanceId;

	@Before
	public void before() {
		securityUtil.logInAs("bajie");
		Model model = repositoryService.createModelQuery().orderByCreateTime().desc().list().get(0);
		// model发布
		byte[] source = repositoryService.getModelEditorSource(model.getId());
		Deployment deploy = repositoryService.createDeployment().category(model.getCategory()).name("测试流程Demo")
				.addBytes("demo.bpmn", source).deploy();
		deploymentId = deploy.getId();
		log.info("deploymentId:" + deploymentId);
		// 获取流程发布对应的流程定义
		ProcessDefinition singleResult = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId)
				.singleResult();
		definitionId = singleResult.getId();
		log.info("definitionKey:" + definitionId);
		/*
		// 任务创建：解析配置（自己的表中） -> 向任务中设置候选参数（候选人/和侯选组）  
		// 部门/用户组
		taskService.addCandidateGroup(taskId, groupId);
		// 用户
		taskService.addCandidateUser(taskId, userId);
		
		// 用户任务列表： 代办  -> 任务查询接口
		// 用户id -> 部门/用户组
		// 用户id+组+部门岗位 -> 任务清单
		
		// 我的代办
		taskService.createTaskQuery().taskCandidateOrAssigned("用户id", null);
		// 我处理过的历史任务
		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskCandidateUser("用户id", null).list();
		
		// 当前审批中的
		List<ProcessInstance> list2 = runtimeService.createProcessInstanceQuery().variableValueEquals("userid_key", "userid").list();
		// 我发起过的
		List<HistoricProcessInstance> list3 = historyService.createHistoricProcessInstanceQuery().variableValueEquals("userid_key", "userid").list();
		*/
		
		
		
		
	}

	@Test
	public void testActiviti() {
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
