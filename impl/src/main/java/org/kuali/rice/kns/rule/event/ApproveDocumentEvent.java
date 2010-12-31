/*
 * Copyright 2006-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.rule.event;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.ApproveDocumentRule;
import org.kuali.rice.kns.rule.BusinessRule;

/**
 * This class represents the approve event that is part of an eDoc in Kuali. This could be triggered when a user presses the approve
 * button for a given document enroute or it could happen when another piece of code calls the approve method in the document
 * service.
 * 
 * 
 */
public class ApproveDocumentEvent extends KualiDocumentEventBase {
    /**
     * Constructs an ApproveDocumentEvent with the specified errorPathPrefix and document
     * 
     * @param errorPathPrefix
     * @param document
     */
    public ApproveDocumentEvent(String errorPathPrefix, Document document) {
        this("approve", errorPathPrefix, document);
    }

    /**
     * Constructs an ApproveDocumentEvent with the given document
     * 
     * @param document
     */
    public ApproveDocumentEvent(Document document) {
        this("approve", "", document);
    }

    /**
     * Constructs a ApproveDocumentEvent, allowing the eventType to be passed in so that subclasses can specify a more accurate
     * message.
     * 
     * @param eventType
     * @param errorPathPrefix
     * @param document
     */
    protected ApproveDocumentEvent(String eventType, String errorPathPrefix, Document document) {
        super("creating " + eventType + " event for document " + getDocumentId(document), errorPathPrefix, document);
    }


    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class<? extends BusinessRule> getRuleInterfaceClass() {
        return ApproveDocumentRule.class;
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.kns.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((ApproveDocumentRule) rule).processApproveDocument(this);
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#generateEvents()
     */
    @Override
    public List<KualiDocumentEvent> generateEvents() {
        List<KualiDocumentEvent> events = new ArrayList<KualiDocumentEvent>();
        events.add(new RouteDocumentEvent(getDocument()));
        return events;
    }
}
