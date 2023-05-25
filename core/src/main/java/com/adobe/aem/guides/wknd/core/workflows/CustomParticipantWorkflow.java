/**
 * 
 */
package com.adobe.aem.guides.wknd.core.workflows;

import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.metadata.MetaDataMap;

/**
 * @author Anshul.Agarwal
 *
 */
@Component(service = ParticipantStepChooser.class, property = {
		ParticipantStepChooser.SERVICE_PROPERTY_LABEL + "=Custom Participant Workflow"
})
public class CustomParticipantWorkflow implements ParticipantStepChooser{
	
	private static Logger logger = LoggerFactory.getLogger(CustomParticipantWorkflow.class);

	@Override
	public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args)
			 {
		String participant = "anshul";
		try {
			
		    Workflow workflow = workItem.getWorkflow();
		    String initiator = workflow.getInitiator();
		    List<HistoryItem> wfHistory;
			
				wfHistory = workflowSession.getHistory(workflow);
			
		    if (!wfHistory.isEmpty()) {
		      participant = initiator;
		    } 
		    logger.info("####### Participant : " + participant + " ##############");
		    
			} catch (WorkflowException e) {
				e.printStackTrace();
				logger.error("Workflow Exception : {}", e);
			
			}
		return participant;
	}

}
