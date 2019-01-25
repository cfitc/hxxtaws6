package com.actionsoft.apps.finance.event;

import java.util.ArrayList;
import java.util.List;

import com.actionsoft.apps.finance.util.bo_util;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.ExecuteListener;
import com.actionsoft.sdk.local.SDK;

public class CLF_PROCESS_AFTER_CREATED extends ExecuteListener {

    public String getDescription() {
        return "测试用例";
    }

    public void execute(ProcessExecutionContext ctx) throws Exception {
    	String bindid= ctx.getProcessInstance().getId();
    	BO boRecordData = new BO();
    	boRecordData.set("XM", "住宿费");
    	BO boRecordData2 = new BO();
    	boRecordData2.set("XM", "餐费");
    	BO boRecordData3 = new BO();
    	boRecordData3.set("XM", "交通费");
    	BO boRecordData4 = new BO();
    	boRecordData4.set("XM", "其他");
    	List<BO> bolist = new   ArrayList();
    	bolist.add(boRecordData);
    	bolist.add(boRecordData2);
    	bolist.add(boRecordData3);
    	bolist.add(boRecordData4);
    	int[] ss = SDK.getBOAPI().create(bo_util.CLF_S, bolist,bindid, ctx.getUserContext().getUID());
    	//bo_util.CLF_P;
        info("流程创建后事件被触发-->" + ctx.getProcessInstance());
    }

}