/**
 * 
 */
package com.adobe.aem.guides.wknd.core.workflows;

import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Route;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

/**
 * @author Anshul.Agarwal
 *
 */
@Component(service = WorkflowProcess.class, property = {
		Constants.SERVICE_DESCRIPTION+"=Custom Process Step to execute Metadata Arguments functionality",
		"process.label="+"Custom Workflow Process Step"
})
public class CustomWorkflowProcess implements WorkflowProcess{
	
	private static final Logger logger = LoggerFactory.getLogger(CustomWorkflowProcess.class);

	@Override
	public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) {
		try {
			
		String processArgs = args.get("PROCESS_ARGS", String.class);
		
		item.getMetaDataMap().put("one", "1");
		item.getMetaDataMap().put("path", item.getWorkflowData().getPayload().toString());
		
		List<Route> routes = session.getRoutes(item, false);
		session.complete(item, routes.get(0));
		} catch(WorkflowException e) {
			logger.error("Workflow Exception : {0}",e);
		}
	}

}
