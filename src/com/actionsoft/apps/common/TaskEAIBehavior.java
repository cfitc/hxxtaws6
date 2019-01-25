package com.actionsoft.apps.common;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.actionsoft.bpms.bpmn.engine.model.run.delegate.EAITaskInstance;
import com.actionsoft.bpms.commons.mvc.view.ResponseObject;
import com.actionsoft.bpms.server.UserContext;
import com.actionsoft.bpms.server.conf.portal.AWSPortalConf;
import com.actionsoft.bpms.util.UtilString;
import com.actionsoft.bpms.util.UtilURL;

import net.sf.json.JSONObject;

public class TaskEAIBehavior {

    public ResponseObject getBehaviors(UserContext user, List<EAITaskInstance> instances) {
        Map<String, Map<String, String>> behaviors = new HashMap<String, Map<String, String>>();
        for (EAITaskInstance instance : instances) {
            Map<String, String> behavior = new HashMap<String, String>();
            // 假设该示例可自定义的actionParameter只提供了一个url
            // 通常可以由开发者自定义一个json串，用来传递更丰富的信息
            String state = "1";
            String id = "";
            String taskId = "";
            String processGroupName = "";
            if (!UtilString.isEmpty(instance.getActionParameter())) {
              JSONObject json = JSONObject.fromObject(instance.getActionParameter());
              if (json.containsKey("state")) {
                state = json.getString("state");
              }
              if (json.containsKey("processInstId")) {
                id = json.getString("processInstId");
              }
              if (json.containsKey("taskId")) {
                taskId = json.getString("taskId");
              }
              if (json.containsKey("processGroupName")) {
                processGroupName = json.getString("processGroupName");
              }
            }
            if (instance.isHistoryTask()) {
              state = "2";
            }
            String url = AWSPortalConf.getUrl() + "/r/w?sid=" + user.getSessionId() + "&cmd=com.actionsoft.apps.aws52_redirect&url=" + UtilURL.URLEncode(new StringBuilder("/workflow/login.wf?sid=#sid5&cmd=WorkFlow_Execute_Worklist_File_Open&id=").append(id).append("&task_id=").append(taskId).append("&openstate=").append(state).toString());
            if (user.isMobileClient()) {
              url = AWSPortalConf.getUrl() + "/r/w?sid=" + user.getSessionId() + "&cmd=com.actionsoft.apps.aws52_redirect&url=" + UtilURL.URLEncode(new StringBuilder("/workflow/login.wf?sid=#sid5&cmd=com.actionsoft.apps.portal.mobile_Mobile_Portal_Task_Open&processInstanceId=").append(id).append("&taskInstanceId=").append(taskId).append("&openstate=").append(state).append("&title=").append(instance.getTitle()).append("&client=aws6app").toString());
            }
            String trackurl = "./w?sid=" + user.getSessionId() + "&cmd=com.actionsoft.apps.aws52_redirect&url=" + UtilURL.URLEncode(new StringBuilder("/workflow/login.wf?sid=#sid5&cmd=WorkFlow_Monitor_Track_List&id=").append(id).append("&task_id=").append(taskId).append("&openstate=").append(state).toString());

            Map iconTitles = new HashMap();
            iconTitles.put("1", "常规任务");
            iconTitles.put("2", "传阅任务");
            iconTitles.put("3", "常规任务");
            iconTitles.put("4", "等待任务");
            iconTitles.put("9", "通知任务");
            iconTitles.put("11", "加签任务");
            // 对含有的@公式进行处理
            behavior.put("font-color", "");
            behavior.put("background-color", "");
            behavior.put("icon", "../commons/img/worklist/taskstate/state_" + state + ".png");
            behavior.put("icon-title", (String)iconTitles.get(state));
            behavior.put("title", instance.getTitle());
            behavior.put("url", url);
            behavior.put("type", processGroupName);
            behavior.put("trackurl", trackurl);
            
            behaviors.put(instance.getId(), behavior);
        }

        return ResponseObject.newOkResponse().put("behaviors", behaviors);
    }
}