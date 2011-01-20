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
package org.kuali.rice.kns.web.spring;

import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;
import org.springframework.validation.AbstractPropertyBindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;

/**
 * This is a description of what this class does - delyea don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KradServletRequestDataBinder extends ServletRequestDataBinder {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KradServletRequestDataBinder.class);

	private KualiBeanPropertyBindingResult bindingResult;
	private ConversionService conversionService;

	/**
     * This constructs a ...
     * 
     * @param target
     * @param objectName
     */
    public KradServletRequestDataBinder(Object target, String objectName) {
	    super(target, objectName);
	    
	    if(target instanceof KualiForm) {
	    	((KualiForm) target).setUsingSpring(true);
	    }
    }

	/**
     * This constructs a ...
     * 
     * @param target
     */
    public KradServletRequestDataBinder(Object target) {
	    super(target);
	    
	    if(target instanceof KualiForm) {
	    	((KualiForm) target).setUsingSpring(true);
	    }
    }

    /**
     * This overridden method allows for a custom binding result class.
     * 
     * @see org.springframework.validation.DataBinder#initBeanPropertyAccess()
     */
    @Override
	public void initBeanPropertyAccess() {
		Assert.state(this.bindingResult == null,
				"DataBinder is already initialized - call initBeanPropertyAccess before other configuration methods");
		this.bindingResult = new KualiBeanPropertyBindingResult(getTarget(), getObjectName(), isAutoGrowNestedPaths());
		if (this.conversionService != null) {
			this.bindingResult.initConversion(this.conversionService);
		}
	}

    /**
     * This overridden method allows for the setting attributes to use to find the data dictionary data from Kuali
     * 
     * @see org.springframework.validation.DataBinder#getInternalBindingResult()
     */
    @Override
	protected AbstractPropertyBindingResult getInternalBindingResult() {
		if (this.bindingResult == null) {
			initBeanPropertyAccess();
//			this.bindingResult.setDocumentEntry(documentEntry);
//			this.bindingResult.setBusinessObjectEntry(businessObjectEntry);
		}
		return this.bindingResult;
	}

	/**
     * This overridden method disallows direct field access for Kuali.
     * 
     * @see org.springframework.validation.DataBinder#initDirectFieldAccess()
     */
    @Override
	public void initDirectFieldAccess() {
    	LOG.error("Direct Field access is not allowed in Kuali");
		throw new RuntimeException("Direct Field access is not allowed in Kuali");
	}

}
