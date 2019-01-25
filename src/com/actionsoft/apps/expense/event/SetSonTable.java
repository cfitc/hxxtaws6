package com.actionsoft.apps.expense.event;

import java.util.List;

import com.actionsoft.bpms.bo.design.model.BOItemModel;
import com.actionsoft.bpms.bo.engine.BO;
import com.actionsoft.bpms.bpmn.engine.core.delegate.ProcessExecutionContext;
import com.actionsoft.bpms.bpmn.engine.listener.FormGridFilterListener;
import com.actionsoft.bpms.bpmn.engine.listener.FormGridRowLookAndFeel;
import com.actionsoft.bpms.bpmn.engine.listener.ListenerConst;
import com.actionsoft.bpms.form.design.model.FormItemModel;
/**
 * @author CHENDX
 * 
 */
public class SetSonTable extends FormGridFilterListener{

	@Override
	public FormGridRowLookAndFeel acceptRowData(ProcessExecutionContext arg0,
			List<BOItemModel> arg1, BO arg2) {
		String tableName = arg0.getParameterOfString(ListenerConst.FORM_EVENT_PARAM_BONAME);
		if (tableName.equals("BO_ACT_CLFBXD_APPLY_S")) {
			//创建一个对象
	        FormGridRowLookAndFeel diyLookAndFeel = new FormGridRowLookAndFeel();
	        String PJZS = arg2.getString("PJZS");
	        String PJJE = arg2.getString("PJJE");
	        String CCTS = arg2.getString("CCTS");
	        String CCJE = arg2.getString("CCJE");
	        String XMDJ = arg2.getString("XMDJ");
	        String XMJE = arg2.getString("XMJE");
			// 允许显示这条数据
			diyLookAndFeel.setDisplay(true);
			// 设置显示样式
			diyLookAndFeel.setCellCSS("style='height: 25px'");
			// 根据内容来显示样式
			if (PJZS != null && PJZS.equals("0")) {
				setFieldDisplayValue(arg1, arg2, "PJZS", "");
			}
			if (PJJE != null && PJJE.equals("0")) {
				setFieldDisplayValue(arg1, arg2, "PJJE", "");
			}
			if (CCTS != null && CCTS.equals("0")) {
				setFieldDisplayValue(arg1, arg2, "CCTS", "");
			}
			if (CCJE != null && CCJE.equals("0")) {
				setFieldDisplayValue(arg1, arg2, "CCJE", "");
			}
			if (XMDJ != null && XMDJ.equals("0")) {
				setFieldDisplayValue(arg1, arg2, "XMDJ", "");
			}
			if (XMJE != null && XMJE.equals("0")) {
				setFieldDisplayValue(arg1, arg2, "XMJE", "");
			}
			return diyLookAndFeel;
		}
		if (tableName.equals("BO_ACT_BXPD_APPLY_S")) {
			String JE = getFieldValue(0, "JE");
			if (JE != null && JE.equals("0")) {
				setFieldDisplayValue(arg1, arg2, "JE", "");
			}

		}
		return null;
	}

	private String getFieldValue(int i, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	private void setFieldDisplayValue(List<BOItemModel> arg1, BO arg2,
			String string, String string2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCustomeTableHeaderHtml(ProcessExecutionContext arg0,
			FormItemModel arg1, List<String> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String orderByStatement(ProcessExecutionContext arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
