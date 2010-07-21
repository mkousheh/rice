/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.document.rule;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRolePermission;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibility;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibilityAction;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMemberQualifier;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.lookup.KimTypeLookupableHelperServiceImpl;
import org.kuali.rice.kim.rule.event.ui.AddDelegationEvent;
import org.kuali.rice.kim.rule.event.ui.AddDelegationMemberEvent;
import org.kuali.rice.kim.rule.event.ui.AddMemberEvent;
import org.kuali.rice.kim.rule.event.ui.AddPermissionEvent;
import org.kuali.rice.kim.rule.event.ui.AddResponsibilityEvent;
import org.kuali.rice.kim.rule.ui.AddDelegationMemberRule;
import org.kuali.rice.kim.rule.ui.AddDelegationRule;
import org.kuali.rice.kim.rule.ui.AddMemberRule;
import org.kuali.rice.kim.rule.ui.AddPermissionRule;
import org.kuali.rice.kim.rule.ui.AddResponsibilityRule;
import org.kuali.rice.kim.rules.ui.KimDocumentMemberRule;
import org.kuali.rice.kim.rules.ui.KimDocumentPermissionRule;
import org.kuali.rice.kim.rules.ui.KimDocumentResponsibilityRule;
import org.kuali.rice.kim.rules.ui.RoleDocumentDelegationMemberRule;
import org.kuali.rice.kim.rules.ui.RoleDocumentDelegationRule;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.impl.RoleServiceBase;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.MessageMap;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityManagementRoleDocumentRule extends TransactionalDocumentRuleBase implements AddPermissionRule, AddResponsibilityRule, AddMemberRule, AddDelegationRule, AddDelegationMemberRule {
//	protected static final Logger LOG = Logger.getLogger( IdentityManagementRoleDocumentRule.class );
			
    public static final int PRIORITY_NUMBER_MIN_VALUE = 1;
    public static final int PRIORITY_NUMBER_MAX_VALUE = 11;

	protected AddResponsibilityRule addResponsibilityRule;
	protected AddPermissionRule  addPermissionRule;
	protected AddMemberRule  addMemberRule;
	protected AddDelegationRule addDelegationRule;
	protected AddDelegationMemberRule addDelegationMemberRule;
	protected BusinessObjectService businessObjectService;
	protected ResponsibilityService responsibilityService;
	protected Class<? extends AddResponsibilityRule> addResponsibilityRuleClass = KimDocumentResponsibilityRule.class;
	protected Class<? extends AddPermissionRule> addPermissionRuleClass = KimDocumentPermissionRule.class;
	protected Class<? extends AddMemberRule> addMemberRuleClass = KimDocumentMemberRule.class;
	protected Class<? extends AddDelegationRule> addDelegationRuleClass = RoleDocumentDelegationRule.class;
	protected Class<? extends AddDelegationMemberRule> addDelegationMemberRuleClass = RoleDocumentDelegationMemberRule.class;

	protected IdentityService identityService;
	
	protected AttributeValidationHelper attributeValidationHelper = new AttributeValidationHelper();
	
    public IdentityService getIdentityService() {
        if ( identityService == null) {
            identityService = KIMServiceLocator.getIdentityService();
        }
        return identityService;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        if (!(document instanceof IdentityManagementRoleDocument))
            return false;

        IdentityManagementRoleDocument roleDoc = (IdentityManagementRoleDocument)document;

        boolean valid = true;
        boolean validateRoleAssigneesAndDelegations = !KimTypeLookupableHelperServiceImpl.hasDerivedRoleTypeService(roleDoc.getKimType());
        GlobalVariables.getMessageMap().addToErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
        valid &= validDuplicateRoleName(roleDoc);
        valid &= validPermissions(roleDoc);
        valid &= validResponsibilities(roleDoc);
        getDictionaryValidationService().validateDocumentAndUpdatableReferencesRecursively(document, getMaxDictionaryValidationDepth(), true, false);
        validateRoleAssigneesAndDelegations &= validAssignRole(roleDoc);
        if(validateRoleAssigneesAndDelegations){
	        //valid &= validAssignRole(roleDoc);
	        valid &= validateRoleQualifier(roleDoc.getMembers(), roleDoc.getKimType());
	        valid &= validRoleMemberActiveDates(roleDoc.getMembers());
	        valid &= validateDelegationMemberRoleQualifier(roleDoc.getMembers(), roleDoc.getDelegationMembers(), roleDoc.getKimType());
	        valid &= validDelegationMemberActiveDates(roleDoc.getDelegationMembers());
	        valid &= validRoleMembersResponsibilityActions(roleDoc.getMembers());
        }
        valid &= validRoleResponsibilitiesActions(roleDoc.getResponsibilities());
        GlobalVariables.getMessageMap().removeFromErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);

        return valid;
    }
    
	protected boolean validAssignRole(IdentityManagementRoleDocument document){
        boolean rulePassed = true;
        Map<String,String> additionalPermissionDetails = new HashMap<String,String>();
        additionalPermissionDetails.put(KimAttributes.NAMESPACE_CODE, document.getRoleNamespace());
        additionalPermissionDetails.put(KimAttributes.ROLE_NAME, document.getRoleName());
		if((document.getMembers()!=null && document.getMembers().size()>0) ||
				(document.getDelegationMembers()!=null && document.getDelegationMembers().size()>0)){
			if(!getDocumentHelperService().getDocumentAuthorizer(document).isAuthorizedByTemplate(
					document, KimConstants.NAMESPACE_CODE, KimConstants.PermissionTemplateNames.ASSIGN_ROLE, 
					GlobalVariables.getUserSession().getPrincipalId(), additionalPermissionDetails, null)){
	            rulePassed = false;
			}
		}
		return rulePassed;
	}

    @SuppressWarnings("unchecked")
	protected boolean validDuplicateRoleName(IdentityManagementRoleDocument roleDoc){
    	Map<String, String> criteria = new HashMap<String, String>();
    	criteria.put("roleName", roleDoc.getRoleName());
    	criteria.put("namespaceCode", roleDoc.getRoleNamespace());
    	List<RoleImpl> roleImpls = (List<RoleImpl>)getBusinessObjectService().findMatching(RoleImpl.class, criteria);
    	boolean rulePassed = true;
    	if(roleImpls!=null && roleImpls.size()>0){
    		if(roleImpls.size()==1 && roleImpls.get(0).getRoleId().equals(roleDoc.getRoleId()))
    			rulePassed = true;
    		else{
	    		GlobalVariables.getMessageMap().putError("document.roleName", 
	    				RiceKeyConstants.ERROR_DUPLICATE_ENTRY, new String[] {"Role Name"});
	    		rulePassed = false;
    		}
    	}
    	return rulePassed;
    }
    
    protected boolean validRoleMemberActiveDates(List<KimDocumentRoleMember> roleMembers) {
    	boolean valid = true;
		int i = 0;
    	for(KimDocumentRoleMember roleMember: roleMembers) {
   			valid &= validateActiveDate("document.members["+i+"].activeToDate", roleMember.getActiveFromDate(), roleMember.getActiveToDate());
    		i++;
    	}
    	return valid;
    }

    protected boolean validDelegationMemberActiveDates(List<RoleDocumentDelegationMember> delegationMembers) {
    	boolean valid = true;
		int i = 0;
    	for(RoleDocumentDelegationMember delegationMember: delegationMembers) {
   			valid &= validateActiveDate("document.delegationMembers["+i+"].activeToDate", 
   					delegationMember.getActiveFromDate(), delegationMember.getActiveToDate());
    		i++;
    	}
    	return valid;
    }

    protected boolean validPermissions(IdentityManagementRoleDocument document){
    	KimPermissionInfo kimPermissionInfo;
    	boolean valid = true;
    	int i = 0;
    	for(KimDocumentRolePermission permission: document.getPermissions()){
    		kimPermissionInfo = permission.getKimPermission();
    		if(!permission.isActive() && !hasPermissionToGrantPermission(permission.getKimPermission(), document)){
    	        GlobalVariables.getMessageMap().putError("permissions["+i+"].active", RiceKeyConstants.ERROR_ASSIGN_PERMISSION, 
    	        		new String[] {kimPermissionInfo.getNamespaceCode(), kimPermissionInfo.getTemplate().getName()});
    	        valid = false;
    		}
    		i++;
    	}
    	return valid;
    }

    protected boolean validResponsibilities(IdentityManagementRoleDocument document){
    	KimResponsibilityImpl kimResponsibilityImpl;
    	boolean valid = true;
    	int i = 0;
    	for(KimDocumentRoleResponsibility responsibility: document.getResponsibilities()){
    		kimResponsibilityImpl = responsibility.getKimResponsibility();
    		if(!responsibility.isActive() && !hasPermissionToGrantResponsibility(responsibility.getKimResponsibility().toSimpleInfo(), document)){
    	        GlobalVariables.getMessageMap().putError("responsibilities["+i+"].active", RiceKeyConstants.ERROR_ASSIGN_RESPONSIBILITY, 
    	        		new String[] {kimResponsibilityImpl.getNamespaceCode(), kimResponsibilityImpl.getTemplate().getName()});
    	        valid = false;
    		}
    		i++;
    	}
    	return valid;
    }
    
    protected boolean validRoleResponsibilitiesActions(List<KimDocumentRoleResponsibility> roleResponsibilities){
        int i = 0;
        boolean rulePassed = true;
    	for(KimDocumentRoleResponsibility roleResponsibility: roleResponsibilities){
    		if(!getResponsibilityService().areActionsAtAssignmentLevelById(roleResponsibility.getResponsibilityId()))
    			validateRoleResponsibilityAction("document.responsibilities["+i+"].roleRspActions[0].priorityNumber", roleResponsibility.getRoleRspActions().get(0));
        	i++;
    	}
    	return rulePassed;
    }

    protected boolean validRoleMembersResponsibilityActions(List<KimDocumentRoleMember> roleMembers){
        int i = 0;
        int j;
        boolean rulePassed = true;
    	for(KimDocumentRoleMember roleMember: roleMembers){
    		j = 0;
    		if(roleMember.getRoleRspActions()!=null && !roleMember.getRoleRspActions().isEmpty()){
	    		for(KimDocumentRoleResponsibilityAction roleRspAction: roleMember.getRoleRspActions()){
	    			validateRoleResponsibilityAction("document.members["+i+"].roleRspActions["+j+"].priorityNumber", roleRspAction);
		        	j++;
	    		}
    		}
    		i++;
    	}
    	return rulePassed;
    }

    protected boolean validateRoleResponsibilityAction(String errorPath, KimDocumentRoleResponsibilityAction roleRspAction){
    	boolean rulePassed = true;
    	/*if(StringUtils.isBlank(roleRspAction.getActionPolicyCode())){
    		GlobalVariables.getErrorMap().putError(errorPath, 
    				RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Action Policy Code"});
    		rulePassed = false;
    	}
    	if(roleRspAction.getPriorityNumber()==null){
    		GlobalVariables.getErrorMap().putError(errorPath, 
    				RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Priority Number"});
    		rulePassed = false;
    	}
    	if(StringUtils.isBlank(roleRspAction.getActionTypeCode())){
    		GlobalVariables.getErrorMap().putError(errorPath, 
    				RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Action Type Code"});
    		rulePassed = false;
    	}*/
    	if(roleRspAction.getPriorityNumber()!=null && 
    			(roleRspAction.getPriorityNumber()<PRIORITY_NUMBER_MIN_VALUE 
    					|| roleRspAction.getPriorityNumber()>PRIORITY_NUMBER_MAX_VALUE)){
    		GlobalVariables.getMessageMap().putError(errorPath, 
   				RiceKeyConstants.ERROR_PRIORITY_NUMBER_RANGE, new String[] {PRIORITY_NUMBER_MIN_VALUE+"", PRIORITY_NUMBER_MAX_VALUE+""});
    		rulePassed = false;
    	}

    	return rulePassed;
    }

    protected boolean validateRoleQualifier(List<KimDocumentRoleMember> roleMembers, KimTypeInfo kimType){
		AttributeSet validationErrors = new AttributeSet();

		int memberCounter = 0;
		int roleMemberCount = 0;
		AttributeSet errorsTemp;
		AttributeSet attributeSetToValidate;
        KimTypeService kimTypeService = KimCommonUtils.getKimTypeService(kimType);
        GlobalVariables.getMessageMap().removeFromErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
        final AttributeDefinitionMap attributeDefinitions = kimTypeService.getAttributeDefinitions(kimType.getKimTypeId());
        final Set<String> uniqueAttributeNames = figureOutUniqueQualificationSet(roleMembers, attributeDefinitions);
        
		for(KimDocumentRoleMember roleMember: roleMembers) {
			errorsTemp = new AttributeSet();
			attributeSetToValidate = attributeValidationHelper.convertQualifiersToMap(roleMember.getQualifiers());
			if(!roleMember.isRole()){
				errorsTemp = kimTypeService.validateAttributes(kimType.getKimTypeId(), attributeSetToValidate);
				validationErrors.putAll( 
						attributeValidationHelper.convertErrorsForMappedFields("document.members["+memberCounter+"]", errorsTemp) );
		        memberCounter++;
			}
			if (uniqueAttributeNames.size() > 0) {
				validateUniquePersonRoleQualifiersUniqueForRoleMembership(roleMember, roleMemberCount, roleMembers, uniqueAttributeNames, validationErrors);
			}
			
			roleMemberCount += 1;
    	}

		GlobalVariables.getMessageMap().addToErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
		
    	if (validationErrors.isEmpty()) {
    		return true;
    	} else {
    		attributeValidationHelper.moveValidationErrorsToErrorMap(validationErrors);
    		return false;
    	}
    }
    
    /**
     * Finds the names of the unique qualification attributes which this role should be checking against
     * 
     * @param memberships the memberships (we take the qualification from the first)
     * @param attributeDefinitions information about the attributeDefinitions
     * @return a Set of unique attribute ids (with their indices, for error reporting)
     */
    protected Set<String> figureOutUniqueQualificationSet(List<KimDocumentRoleMember> memberships, AttributeDefinitionMap attributeDefinitions) {
    	Set<String> uniqueAttributeIds = new HashSet<String>();
    	
    	if (memberships != null && memberships.size() > 1) { // if there aren't two or more members, doing this whole check is kinda silly
    		KimDocumentRoleMember membership = memberships.get(0);
    		
    		for (KimDocumentRoleQualifier qualifier : membership.getQualifiers()) {
        		if (qualifier != null && qualifier.getKimAttribute() != null && !StringUtils.isBlank(qualifier.getKimAttribute().getAttributeName())) {
    	    		final AttributeDefinition relatedDefinition = attributeDefinitions.getByAttributeName(qualifier.getKimAttribute().getAttributeName());
    	    		
    	    		if (relatedDefinition != null && relatedDefinition.getUnique() != null && relatedDefinition.getUnique().booleanValue()) {
    	    			uniqueAttributeIds.add(qualifier.getKimAttrDefnId()); // it's unique - add it to the Set
    	    		}
        		}
    		}
    	}
    	
    	return uniqueAttributeIds;
    }
    
    /**
     * Checks all the qualifiers for the given membership, so that all qualifiers which should be unique are guaranteed to be unique
     * 
     * @param membership the membership to check
     * @param attributeDefinitions the Map of attribute definitions used by the role
     * @param memberIndex the index of the person's membership in the role (for error reporting purposes)
     * @param validationErrors AttributeSet of errors to report
     * @return true if all unique values are indeed unique, false otherwise
     */
    protected boolean validateUniquePersonRoleQualifiersUniqueForRoleMembership(KimDocumentRoleMember membershipToCheck, int membershipToCheckIndex, List<KimDocumentRoleMember> memberships, Set<String> uniqueQualifierIds, AttributeSet validationErrors) {
    	boolean foundError = false;
    	int count = 0;
    	
    	for (KimDocumentRoleMember membership : memberships) {
    		if (membershipToCheckIndex != count) {
    			if (sameMembership(membershipToCheck, membership)) {
    				if (sameUniqueMembershipQualifications(membershipToCheck, membership, uniqueQualifierIds)) {
    					foundError = true;
    					// add error to each qualifier which is supposed to be unique
    					int qualifierCount = 0;
    					
    					for (KimDocumentRoleQualifier qualifier : membership.getQualifiers()) {
    						if (qualifier != null && uniqueQualifierIds.contains(qualifier.getKimAttrDefnId())) {
    							validationErrors.put("document.members["+membershipToCheckIndex+"].qualifiers["+qualifierCount+"].attrVal", RiceKeyConstants.ERROR_DOCUMENT_IDENTITY_MANAGEMENT_PERSON_QUALIFIER_VALUE_NOT_UNIQUE+":"+qualifier.getKimAttribute().getAttributeName()+";"+qualifier.getAttrVal());
    						}
    						qualifierCount += 1;
    					}
    				}
    			}
    		}
    		count += 1;
    	}
    	
    	return foundError;
    }
    
    /**
     * Determines if two memberships represent the same member being added: that is, the two memberships have the same type code and id
     * 
     * @param membershipA the first membership to check
     * @param membershipB the second membership to check
     * @return true if the two memberships represent the same member; false if they do not, or if it could not be profitably determined if the members were the same
     */
    protected boolean sameMembership(KimDocumentRoleMember membershipA, KimDocumentRoleMember membershipB) {
    	if (!StringUtils.isBlank(membershipA.getMemberTypeCode()) && !StringUtils.isBlank(membershipB.getMemberTypeCode()) && !StringUtils.isBlank(membershipA.getMemberId()) && !StringUtils.isBlank(membershipB.getMemberId())) {
    		return membershipA.getMemberTypeCode().equals(membershipB.getMemberTypeCode()) && membershipA.getMemberId().equals(membershipB.getMemberId());
    	}
    	return false;
    }
    
    /**
     * Given two memberships which represent the same member, do they share qualifications?
     * 
     * @param membershipA the first membership to check
     * @param membershipB the second membership to check
     * @param uniqueAttributeIds the Set of attribute definition ids which should be unique
     * @return
     */
    protected boolean sameUniqueMembershipQualifications(KimDocumentRoleMember membershipA, KimDocumentRoleMember membershipB, Set<String> uniqueAttributeIds) {
    	boolean equalSoFar = true;
    	for (String kimAttributeDefinitionId : uniqueAttributeIds) {
    		final KimDocumentRoleQualifier qualifierA = membershipA.getQualifier(kimAttributeDefinitionId);
    		final KimDocumentRoleQualifier qualifierB = membershipB.getQualifier(kimAttributeDefinitionId);
    		
    		if (qualifierA != null && qualifierB != null) {
    			equalSoFar &= (qualifierA.getAttrVal() == null && qualifierB.getAttrVal() == null) || (qualifierA.getAttrVal() == null || qualifierA.getAttrVal().equals(qualifierB.getAttrVal()));
    		}
    	}
    	return equalSoFar;
    }
    
    protected KimDocumentRoleMember getRoleMemberForDelegation(
    		List<KimDocumentRoleMember> roleMembers, RoleDocumentDelegationMember delegationMember){
    	if(roleMembers==null || delegationMember==null || delegationMember.getRoleMemberId()==null) return null;
    	for(KimDocumentRoleMember roleMember: roleMembers){
    		if(delegationMember.getRoleMemberId().equals(roleMember.getRoleMemberId()))
    			return roleMember;
    	}
    	return null;
    }

    protected boolean validateDelegationMemberRoleQualifier(List<KimDocumentRoleMember> roleMembers, 
    		List<RoleDocumentDelegationMember> delegationMembers, KimTypeInfo kimType){
		AttributeSet validationErrors = new AttributeSet();
		boolean valid;
		int memberCounter = 0;
		AttributeSet errorsTemp;
		AttributeSet attributeSetToValidate;
        KimTypeService kimTypeService = KimCommonUtils.getKimTypeService(kimType);
        GlobalVariables.getMessageMap().removeFromErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
        KimDocumentRoleMember roleMember;
        String errorPath;
        final AttributeDefinitionMap attributeDefinitions = kimTypeService.getAttributeDefinitions(kimType.getKimTypeId());
        final Set<String> uniqueQualifierAttributes = figureOutUniqueQualificationSetForDelegation(delegationMembers, attributeDefinitions);
        
		for(RoleDocumentDelegationMember delegationMember: delegationMembers) {
			errorPath = "delegationMembers["+memberCounter+"]";
			errorsTemp = new AttributeSet();
			attributeSetToValidate = attributeValidationHelper.convertQualifiersToMap(delegationMember.getQualifiers());
			if(!delegationMember.isRole()){
				errorsTemp = kimTypeService.validateAttributes(kimType.getKimTypeId(), attributeSetToValidate);
				validationErrors.putAll(
						attributeValidationHelper.convertErrorsForMappedFields(errorPath, errorsTemp));
			}
			roleMember = getRoleMemberForDelegation(roleMembers, delegationMember);
			if(roleMember==null){
				valid = false;
				GlobalVariables.getMessageMap().putError("document.delegationMembers["+memberCounter+"]", RiceKeyConstants.ERROR_DELEGATE_ROLE_MEMBER_ASSOCIATION, new String[]{});
			} else{
				errorsTemp = kimTypeService.validateUnmodifiableAttributes(
								kimType.getKimTypeId(), 
								attributeValidationHelper.convertQualifiersToMap(roleMember.getQualifiers()), 
								attributeSetToValidate);
				validationErrors.putAll(
						attributeValidationHelper.convertErrorsForMappedFields(errorPath, errorsTemp) );
			}
			if (uniqueQualifierAttributes.size() > 0) {
				validateUniquePersonRoleQualifiersUniqueForRoleDelegation(delegationMember, memberCounter, delegationMembers, uniqueQualifierAttributes, validationErrors);
			}
	        memberCounter++;
    	}
		GlobalVariables.getMessageMap().addToErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
    	if (validationErrors.isEmpty()) {
    		valid = true;
    	} else {
    		attributeValidationHelper.moveValidationErrorsToErrorMap(validationErrors);
    		valid = false;
    	}
    	return valid;
    }
    
    /**
     * Finds the names of the unique qualification attributes which this role should be checking against
     * 
     * @param memberships the memberships (we take the qualification from the first)
     * @param attributeDefinitions information about the attributeDefinitions
     * @return a Set of unique attribute ids (with their indices, for error reporting)
     */
    protected Set<String> figureOutUniqueQualificationSetForDelegation(List<RoleDocumentDelegationMember> memberships, AttributeDefinitionMap attributeDefinitions) {
    	Set<String> uniqueAttributeIds = new HashSet<String>();
    	
    	if (memberships != null && memberships.size() > 1) { // if there aren't two or more members, doing this whole check is kinda silly
    		RoleDocumentDelegationMember membership = memberships.get(0);
    		
    		for (RoleDocumentDelegationMemberQualifier qualifier : membership.getQualifiers()) {
        		if (qualifier != null && qualifier.getKimAttribute() != null && !StringUtils.isBlank(qualifier.getKimAttribute().getAttributeName())) {
    	    		final AttributeDefinition relatedDefinition = attributeDefinitions.getByAttributeName(qualifier.getKimAttribute().getAttributeName());
    	    		
    	    		if (relatedDefinition.getUnique() != null && relatedDefinition.getUnique().booleanValue()) {
    	    			uniqueAttributeIds.add(qualifier.getKimAttrDefnId()); // it's unique - add it to the Set
    	    		}
        		}
    		}
    	}
    	
    	return uniqueAttributeIds;
    }
    
    /**
     * Checks all the qualifiers for the given membership, so that all qualifiers which should be unique are guaranteed to be unique
     * 
     * @param membership the membership to check
     * @param attributeDefinitions the Map of attribute definitions used by the role
     * @param memberIndex the index of the person's membership in the role (for error reporting purposes)
     * @param validationErrors AttributeSet of errors to report
     * @return true if all unique values are indeed unique, false otherwise
     */
    protected boolean validateUniquePersonRoleQualifiersUniqueForRoleDelegation(RoleDocumentDelegationMember delegationMembershipToCheck, int membershipToCheckIndex, List<RoleDocumentDelegationMember> delegationMemberships, Set<String> uniqueQualifierIds, AttributeSet validationErrors) {
    	boolean foundError = false;
    	int count = 0;
    	
    	for (RoleDocumentDelegationMember delegationMembership : delegationMemberships) {
    		if (membershipToCheckIndex != count) {
    			if (sameDelegationMembership(delegationMembershipToCheck, delegationMembership)) {
    				if (sameUniqueDelegationMembershipQualifications(delegationMembershipToCheck, delegationMembership, uniqueQualifierIds)) {
    					foundError = true;
    					// add error to each qualifier which is supposed to be unique
    					int qualifierCount = 0;
    					
    					for (RoleDocumentDelegationMemberQualifier qualifier : delegationMembership.getQualifiers()) {
    						if (qualifier != null && uniqueQualifierIds.contains(qualifier.getKimAttrDefnId())) {
    							validationErrors.put("document.delegationMembers["+membershipToCheckIndex+"].qualifiers["+qualifierCount+"].attrVal", RiceKeyConstants.ERROR_DOCUMENT_IDENTITY_MANAGEMENT_PERSON_QUALIFIER_VALUE_NOT_UNIQUE+":"+qualifier.getKimAttribute().getAttributeName()+";"+qualifier.getAttrVal());
    						}
    						qualifierCount += 1;
    					}
    				}
    			}
    		}
    		count += 1;
    	}
    	
    	return foundError;
    }
    
    /**
     * Determines if two memberships represent the same member being added: that is, the two memberships have the same type code and id
     * 
     * @param membershipA the first membership to check
     * @param membershipB the second membership to check
     * @return true if the two memberships represent the same member; false if they do not, or if it could not be profitably determined if the members were the same
     */
    protected boolean sameDelegationMembership(RoleDocumentDelegationMember membershipA, RoleDocumentDelegationMember membershipB) {
    	if (!StringUtils.isBlank(membershipA.getMemberTypeCode()) && !StringUtils.isBlank(membershipB.getMemberTypeCode()) && !StringUtils.isBlank(membershipA.getMemberId()) && !StringUtils.isBlank(membershipB.getMemberId())) {
    		return membershipA.getMemberTypeCode().equals(membershipB.getMemberTypeCode()) && membershipA.getMemberId().equals(membershipB.getMemberId());
    	}
    	return false;
    }
    
    /**
     * Given two memberships which represent the same member, do they share qualifications?
     * 
     * @param membershipA the first membership to check
     * @param membershipB the second membership to check
     * @param uniqueAttributeIds the Set of attribute definition ids which should be unique
     * @return
     */
    protected boolean sameUniqueDelegationMembershipQualifications(RoleDocumentDelegationMember membershipA, RoleDocumentDelegationMember membershipB, Set<String> uniqueAttributeIds) {
    	boolean equalSoFar = true;
    	for (String kimAttributeDefinitionId : uniqueAttributeIds) {
    		final RoleDocumentDelegationMemberQualifier qualifierA = membershipA.getQualifier(kimAttributeDefinitionId);
    		final RoleDocumentDelegationMemberQualifier qualifierB = membershipB.getQualifier(kimAttributeDefinitionId);
    		
    		if (qualifierA != null && qualifierB != null) {
    			equalSoFar &= (qualifierA.getAttrVal() == null && qualifierB.getAttrVal() == null) || (qualifierA.getAttrVal() == null || qualifierA.getAttrVal().equals(qualifierB.getAttrVal()));
    		}
    	}
    	return equalSoFar;
    }
    
	protected boolean validateActiveDate(String errorPath, Date activeFromDate, Date activeToDate) {
		// TODO : do not have detail bus rule yet, so just check this for now.
		boolean valid = true;
		if (activeFromDate != null && activeToDate !=null && activeToDate.before(activeFromDate)) {
	        MessageMap errorMap = GlobalVariables.getMessageMap();
            errorMap.putError(errorPath, RiceKeyConstants.ERROR_ACTIVE_TO_DATE_BEFORE_FROM_DATE);
            valid = false;
			
		}
		return valid;
	}
	
	/**
	 *
	 * This method checks to see if adding a role to role membership
	 * creates a circular reference.
	 * 
	 * @param addMemberEvent
	 * @return
	 */
	protected boolean checkForCircularRoleMembership(AddMemberEvent addMemberEvent)
	{
		boolean ok = true;
		KimDocumentRoleMember newMember = addMemberEvent.getMember();
		if (newMember == null || StringUtils.isBlank(newMember.getMemberId())){
			ok = false;
		} else {
			List<RoleMemberImpl> roleMembers = null;
			// if the role member is a role, check to make sure we won't be creating a circular reference.
			// Verify that the new role is not already related to the role either directly or indirectly
			if (newMember.isRole()){
				// get all nested role members that are of type role
				try {
					RoleService roleService = KIMServiceLocator.getRoleService();
					roleMembers = ((RoleServiceBase) roleService).getRoleTypeRoleMembers(newMember.getMemberId());
				} catch (Exception ex){
					ok = false;
				}

				// check to see if the document role is not a member of the new member role
				IdentityManagementRoleDocument document = (IdentityManagementRoleDocument)addMemberEvent.getDocument();
				String docRoleNamespace = document.getRoleNamespace();
				String docRoleName = document.getRoleName();
				String docRoleId = document.getRoleId();
				String roleId = KIMServiceLocator.getRoleService().getRoleIdByName(newMember.getMemberNamespaceCode(), newMember.getMemberName());
				if (StringUtils.isEmpty(roleId)){
					ok = false;   // if role doesn't exist, return false
				} else {

					for (RoleMemberImpl member : roleMembers) {
						if (org.kuali.rice.kim.bo.Role.ROLE_MEMBER_TYPE.equals(member.getMemberTypeCode())){
							if (docRoleId.equals(member.getMemberId())){
								ok = false;
								MessageMap errorMap = GlobalVariables.getMessageMap();
								errorMap.putError("member.memberId", RiceKeyConstants.ERROR_ASSIGN_ROLE_MEMBER_CIRCULAR, new String[] {member.getMemberId()});        	
								return false;
							}
						}
					}
				}
			}
		}
		if (ok != true){
			MessageMap errorMap = GlobalVariables.getMessageMap();
			errorMap.putError("member.memberId", RiceKeyConstants.ERROR_INVALID_ROLE, new String[] {""});        	
		}
		return ok;
	}
	
	protected ArrayList getNestedRoleTypeMembers(ArrayList foundMembers){
		
		return foundMembers;
	}
	
	/**
	 * @return the addResponsibilityRule
	 */
	public AddResponsibilityRule getAddResponsibilityRule() {
		if(addResponsibilityRule == null){
			try {
				addResponsibilityRule = addResponsibilityRuleClass.newInstance();
			} catch ( Exception ex ) {
				throw new RuntimeException( "Unable to create AddResponsibilityRule instance using class: " + addResponsibilityRuleClass, ex );
			}
		}
		return addResponsibilityRule;
	}

	/**
	 * @return the addPermissionRule
	 */
	public AddPermissionRule getAddPermissionRule() {
		if(addPermissionRule == null){
			try {
				addPermissionRule = addPermissionRuleClass.newInstance();
			} catch ( Exception ex ) {
				throw new RuntimeException( "Unable to create AddPermissionRule instance using class: " + addPermissionRuleClass, ex );
			}
		}
		return addPermissionRule;
	}
	
	/**
	 * @return the addMemberRule
	 */
	public AddMemberRule getAddMemberRule() {
		if(addMemberRule == null){
			try {
				addMemberRule = addMemberRuleClass.newInstance();
			} catch ( Exception ex ) {
				throw new RuntimeException( "Unable to create AddMemberRule instance using class: " + addMemberRuleClass, ex );
			}
		}
		return addMemberRule;
	}

	/**
	 * @return the addDelegationRule
	 */
	public AddDelegationRule getAddDelegationRule() {
		if(addDelegationRule == null){
			try {
				addDelegationRule = addDelegationRuleClass.newInstance();
			} catch ( Exception ex ) {
				throw new RuntimeException( "Unable to create AddDelegationRule instance using class: " + addDelegationRuleClass, ex );
			}
		}
		return addDelegationRule;
	}

	/**
	 * @return the addDelegationMemberRule
	 */
	public AddDelegationMemberRule getAddDelegationMemberRule() {
		if(addDelegationMemberRule == null){
			try {
				addDelegationMemberRule = addDelegationMemberRuleClass.newInstance();
			} catch ( Exception ex ) {
				throw new RuntimeException( "Unable to create AddDelegationMemberRule instance using class: " + addDelegationMemberRuleClass, ex );
			}
		}
		return addDelegationMemberRule;
	}
	
    public boolean processAddPermission(AddPermissionEvent addPermissionEvent) {
        return getAddPermissionRule().processAddPermission(addPermissionEvent);    
    }

    public boolean hasPermissionToGrantPermission(KimPermissionInfo kimPermissionInfo , IdentityManagementRoleDocument document){
        return getAddPermissionRule().hasPermissionToGrantPermission(kimPermissionInfo, document);    
    }

    public boolean processAddResponsibility(AddResponsibilityEvent addResponsibilityEvent) {
        return getAddResponsibilityRule().processAddResponsibility(addResponsibilityEvent);    
    }

    public boolean hasPermissionToGrantResponsibility(KimResponsibilityInfo kimResponsibilityInfo, IdentityManagementRoleDocument document) {
        return getAddResponsibilityRule().hasPermissionToGrantResponsibility(kimResponsibilityInfo, document);    
    }
    
    public boolean processAddMember(AddMemberEvent addMemberEvent) {
        boolean success = new KimDocumentMemberRule().processAddMember(addMemberEvent);
        success &= validateActiveDate("member.activeFromDate", addMemberEvent.getMember().getActiveFromDate(), addMemberEvent.getMember().getActiveToDate());
        success &= checkForCircularRoleMembership(addMemberEvent);
        return success;
    }

    public boolean processAddDelegation(AddDelegationEvent addDelegationEvent) {
        return getAddDelegationRule().processAddDelegation(addDelegationEvent);    
    }

    public boolean processAddDelegationMember(AddDelegationMemberEvent addDelegationMemberEvent) {
        boolean success = new RoleDocumentDelegationMemberRule().processAddDelegationMember(addDelegationMemberEvent);
        RoleDocumentDelegationMember roleDocumentDelegationMember = addDelegationMemberEvent.getDelegationMember();
        success &= validateActiveDate("delegationMember.activeFromDate", roleDocumentDelegationMember.getActiveFromDate(), roleDocumentDelegationMember.getActiveToDate());
        return success;
    }

	public ResponsibilityService getResponsibilityService() {
		if(responsibilityService == null){
			responsibilityService = KIMServiceLocator.getResponsibilityService();
		}
		return responsibilityService;
	}


	/**
	 * @return the businessObjectService
	 */
	public BusinessObjectService getBusinessObjectService() {
		if(businessObjectService == null){
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
}
