/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kom.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.OjbCharBooleanConversion;

/**
 * This is a description of what this class does - pberres don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class OrganizationsContexts extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 9021359708162166484L;
    private Long id;
    private Long organizationId;
    private Long contextId;
    private String active;
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getOrganizationId() {
        return this.organizationId;
    }
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
    public Long getContextId() {
        return this.contextId;
    }
    public void setContextId(Long contextId) {
        this.contextId = contextId;
    }
    public Boolean getActive() {
        return (Boolean)(new OjbCharBooleanConversion()).sqlToJava(active);
    }

    public void setActive(Boolean active) {
        this.active = (String)(new OjbCharBooleanConversion()).javaToSql(active);
    }
    /**
     * This overridden method ...
     * 
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("organizationId", getOrganizationId());
        propMap.put("contextId", getContextId());
        propMap.put("active", getActive());
        return propMap;
    }

}
