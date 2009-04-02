<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="person" required="true" type="org.kuali.kfs.module.cg.businessobject.BudgetUser"%>
<%@ attribute name="firstInList" required="true" %>
<%@ attribute name="listIndex" required="true" %>

<c:set var="budgetUserAttributes" value="${DataDictionary.BudgetUser.attributes}" />

<c:set var="viewOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

            <div align="center">
              <!-- TAB -->
              <c:choose>
	              <c:when test="${empty person.principalId}">
	                <c:set var="name" value="TO BE NAMED" />
	                <c:set var="personId" value="-1" />
	              </c:when>
	              <c:otherwise>
	                <c:set var="name" value="${person.user.name}" />
	                <c:set var="personId" value="${person.principalId }"/>
	              </c:otherwise>
              </c:choose>
		    <c:set var="transparentBackground" value="false" />
			  <c:if test="${listIndex eq 0}"><c:set var="transparentBackground" value="true" /></c:if>
	          <kul:tab 
	          	tabTitle="${fn:substring(name, 0, 22)}" 
	          	tabDescription="${(not empty person.role) ? fn:substring(person.role, 0, 16) : ' '}" 
	          	leftSideHtmlProperty="document.budget.personFromList[${listIndex}].delete" 
	          	leftSideHtmlAttribute="${budgetUserAttributes.delete}" 
	          	leftSideHtmlDisabled="${person.personProjectDirectorIndicator or viewOnly}" 
	          	defaultOpen="false" transparentBackground="${firstInList}" 
	          	tabErrorKey="document.budget.personFromList[${listIndex}]*,document.budget.personnel[${listIndex}]*" 
	          	auditCluster="personnelAuditErrors"
	          	tabAuditKey="document.budget.audit.personnel.${personId}*"
	          	useCurrentTabIndexAsKey="true">
	          	<div class="tab-container-error"><div class="left-errmsg-tab"><kra:auditErrors cluster="personnelAuditErrors" keyMatch="document.budget.audit.personnel.${personId}*" isLink="false" includesTitle="true"/></div></div>
              <div class="tab-container" id="G02" style="" align="center">
                <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
                  <tbody>
                  <tr>
                    <td colspan="14" class="subhead">
                      <span class= "subhead-left">
                        ${name}
                      </span>
                    </td>
                  </tr>

                  <tr bgcolor="#FFFFFF">
                    <td height="30" colspan="14" class="tab-subhead">
                      General Info
                    </td>
                  </tr>

                  <tr bgcolor="#FFFFFF">
                    <th height="30" colspan="5" class="bord-l-b">
                      <div align="right">
                        Chart/Org:
                      </div>
                    </th>

                    <td height="30" colspan="10" class="datacell">
                      <c:if test="${person.fiscalCampusCode ne null and person.primaryDepartmentCode ne null}">${person.fiscalCampusCode} / ${person.primaryDepartmentCode}</c:if>
                      <c:if test="${!viewOnly}">
                        <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Organization" fieldConversions="chartOfAccounts.chartOfAccountsCode:document.budget.personnel[${listIndex}].fiscalCampusCode,organizationCode:document.budget.personnel[${listIndex}].primaryDepartmentCode" anchor="${currentTabIndex}" />
                      </c:if>
                    </td>
                  </tr>

                  <tr bgcolor="#FFFFFF">
                    <th height="30" colspan="5" class="bord-l-b">
                      <div align="right">
                        Prefix/Suffix:
                      </div>
                    </th>

                    <td height="30" colspan="10" class="datacell">
                      <kul:htmlControlAttribute property="document.budget.personFromList[${listIndex}].personNamePrefixText" attributeEntry="${budgetUserAttributes.personNamePrefixText}" readOnly="${viewOnly}" />
                      /
                      <kul:htmlControlAttribute property="document.budget.personFromList[${listIndex}].personNameSuffixText" attributeEntry="${budgetUserAttributes.personNameSuffixText}" readOnly="${viewOnly}" />
                    </td>
                  </tr>

                  <tr bgcolor="#FFFFFF">
                    <th height="30" colspan="5" class="bord-l-b">
                      <div align="right">
                        Project Role:
                      </div>
                    </th>

                    <td height="30" colspan="10" class="datacell">
                      <c:choose>
	                      <c:when test="${person.personProjectDirectorIndicator}">
                          <html:hidden property="document.budget.personFromList[${listIndex}].role" write="true" />
                        </c:when>
	                      <c:otherwise>
	                        <kul:htmlControlAttribute property="document.budget.personFromList[${listIndex}].role" attributeEntry="${budgetUserAttributes.role}" readOnly="${viewOnly}" />
	                      </c:otherwise>
                      </c:choose>
                    </td>
                  </tr>

                  <tr bgcolor="#FFFFFF">
                    <th height="30" colspan="5" class="bord-l-b">
                      <div align="right">
                        Additional Role Type:
                      </div>
                    </th>

                    <td height="30" colspan="10" class="datacell">
                      <kul:htmlControlAttribute property="document.budget.personFromList[${listIndex}].personSeniorKeyIndicator" attributeEntry="${budgetUserAttributes.personSeniorKeyIndicator}" readOnly="${viewOnly}" />
                      <label for="document.budget.personFromList[${listIndex}].personSeniorKeyIndicator">Senior Key Personnel</label><br />
                      <kul:htmlControlAttribute property="document.budget.personFromList[${listIndex}].personSecretarialClericalIndicator" attributeEntry="${budgetUserAttributes.personSecretarialClericalIndicator}" readOnly="${viewOnly}" />
                      <label for="document.budget.personFromList[${listIndex}].personSecretarialClericalIndicator">Secretarial/Clerical</label><br />
                      <kul:htmlControlAttribute property="document.budget.personFromList[${listIndex}].personPostDoctoralIndicator" attributeEntry="${budgetUserAttributes.personPostDoctoralIndicator}" readOnly="${viewOnly}" />
                      <label for="document.budget.personFromList[${listIndex}].personPostDoctoralIndicator">Post Doctoral</label>
                    </td>
                  </tr> 

                  <tr bgcolor="#FFFFFF">
                    <th height="30" colspan="5" class="bord-l-b">
                      <div align="right">
                        Appointment Type:
                      </div>
                    </th>

                    <td height="30" colspan="10" class="datacell">
                      <span class="tab-subhead">
                        <kul:htmlControlAttribute property="document.budget.personFromList[${listIndex}].appointmentTypeCode" extraReadOnlyProperty="document.budget.personFromList[${listIndex}].appointmentTypeDescription" attributeEntry="${budgetUserAttributes.appointmentTypeCode}" readOnly="${viewOnly}" />
                        <c:if test="${! viewOnly }">
                          &nbsp;&nbsp;&nbsp; 
                          <html:image src="${ConfigProperties.externalizable.images.url}tinybutton-updateview.gif" styleClass="tinybutton" property="methodToCall.updateView.anchor${currentTabIndex}" title="update view" alt="update view"/>
                        </c:if>
                      </span>
                    </td>
                  </tr>
                  <c:choose>
                    <c:when test="${not fn:contains(KualiForm.appointmentTypeGridMappings['hourly'], person.appointmentTypeCode) and not fn:contains(KualiForm.appointmentTypeGridMappings['gradResAssistant'], person.appointmentTypeCode)}">
		                  <tr bgcolor="#FFFFFF">
		                    <td height="30" colspan="14" class="tab-subhead"><div align="left"><strong>Salary</strong></div></td>
		                  </tr>
		                  <tr>
	                      <th colspan="5" class="bord-l-b"><div align="right">Base Salary:</div></th>
	                      <td colspan="4" class="datacell">
	                        <kul:htmlControlAttribute accessibilityHint=" for Base Salary" property="document.budget.personFromList[${listIndex}].baseSalary" attributeEntry="${budgetUserAttributes.baseSalary}" readOnly="${viewOnly}" styleClass="amount"/>
	                      </td>
		                    <th class="bord-l-b"><div align="right">Justification for rate change:</div></th>
		                    <td colspan="4" class="datacell">
		                      <kul:htmlControlAttribute property="document.budget.personFromList[${listIndex}].personSalaryJustificationText" attributeEntry="${budgetUserAttributes.personSalaryJustificationText}" readOnly="${viewOnly}" />
		                    </td>
		                  </tr>
                    </c:when>
                    
                    <c:when test="${fn:contains(KualiForm.appointmentTypeGridMappings['hourly'], person.appointmentTypeCode)}">
				              <tr>
				                <th colspan="5" class="bord-l-b"><div align="right"># of Hourlies:</div></th>
				                <td colspan="10" class="datacell">
				                  <kul:htmlControlAttribute property="document.budget.personFromList[${listIndex}].personHourlyNumber" attributeEntry="${budgetUserAttributes.personHourlyNumber}"  readOnly="${viewOnly}" />
                        </td>
				              </tr>
				              <tr>
				                <th colspan="5" class="bord-l-b"><div align="right">Notes:</div></th>
				                <td colspan="10" class="datacell"><kul:htmlControlAttribute property="document.budget.personFromList[${listIndex}].personSalaryJustificationText" attributeEntry="${budgetUserAttributes.personSalaryJustificationText}"  readOnly="${viewOnly}" />
				                </td>
				              </tr>
                    </c:when>
                  </c:choose>

		              <tr bgcolor="#ffffff">
		                <td colspan="14" class="tab-subhead" height="30"><span class="left"><strong>Salary Disbursement </strong></span><span class="right">View by task:
		                  <html:select title="View by task" property="document.budget.personFromList[${listIndex}].currentTaskNumber">
		                    <c:set var="budgetTasks" value="${KualiForm.budgetDocument.budget.tasks}"/>
		                    <html:options collection="budgetTasks" property="budgetTaskSequenceNumber" labelProperty="budgetTaskName"/>
		                  </html:select>
		                  <html:image src="${ConfigProperties.externalizable.images.url}tinybutton-updateview.gif" styleClass="tinybutton" property="methodToCall.updateView.anchor${currentTabIndex}" title="update view" alt="update view"/> </span></td>
		              </tr>

                 <kra-b:budgetPersonnelIndividualGrid person="${person}" personListIndex="${listIndex}" />
                  
                 <c:if test="${! viewOnly }">
                   <tr>
                     <th colspan="14" class="bord-l-b"><html:image src="${ConfigProperties.externalizable.images.url}tinybutton-calccomp.gif" styleClass="tinybutton" property="methodToCall.calculateCompensation.anchor${currentTabIndex}" title="Calculate Compensation" alt="Calculate Compensation"/></th>
                   </tr>
                 </c:if>
               </tbody>
               </table>
             </div>
           </kul:tab>
         </div>
 
