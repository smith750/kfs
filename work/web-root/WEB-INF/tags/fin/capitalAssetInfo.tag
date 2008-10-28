<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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

<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ tag description="render the given field in the capital asset info object"%>

<%@ attribute name="capitalAssetInfo" required="true" type="java.lang.Object"
	description="The capital asset info object containing the data being displayed"%>
<%@ attribute name="capitalAssetInfoName" required="true" 
	description="The name of the capital asset info object"%>	
<%@ attribute name="readOnly" required="false" description="Whether the capital asset information should be read only" %>	

<script language="JavaScript" type="text/javascript" src="dwr/interface/VendorService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/vendor/objectInfo.js"></script>
	
<c:set var="attributes" value="${DataDictionary.CapitalAssetInformation.attributes}" />	
<c:set var="dataCellCssClass" value="datacell"/>

<table class="datatable" style="border-top: 1px solid rgb(153, 153, 153);" cellpadding="0" cellspacing="0" summary="Capital Asset Information">
   	<tr>
   		<td colspan="2" class="tab-subhead" style="border-right: medium none;">Retrieve Asset to be Updated</td>
   	</tr>
   	<tr>                        
	    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetNumber}" horizontal="true" width="50%"/>        
	    <fin:dataCell dataCellCssClass="${dataCellCssClass}"
			businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
			field="capitalAssetNumber" lookup="true" inquiry="true"
			boClassSimpleName="CapitalAssetManagementAsset" boPackageName="org.kuali.kfs.integration.cam"
			lookupUnkeyedFieldConversions="campusTagNumber:${capitalAssetInfoName}.capitalAssetTagNumber,"
			lookupOrInquiryKeys="capitalAssetNumber"
			businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>
   	</tr>   
</table>

<br/><br/>
		
<table class="datatable" style="border-top: 1px solid rgb(153, 153, 153);"  cellpadding="0" cellspacing="0" summary="Capital Asset Information">
   <c:set var="colspan" value="${readOnly ? 5 : 6 }"/>
   <tr>
   		<td colspan="${colspan}" class="tab-subhead" style="border-top: medium;">Create New Assets</td>
   </tr>
   
   <tr> 
   	    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetQuantity}" labelFor="${capitalAssetInfoName}.capitalAssetQuantity"/> 
   	    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetTypeCode}" labelFor="${capitalAssetInfoName}.capitalAssetTypeCode"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.vendorName}" labelFor="${capitalAssetInfoName}.vendorName"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetManufacturerName}" labelFor="${capitalAssetInfoName}.capitalAssetManufacturerName"/>
		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetManufacturerModelNumber}" labelFor="${capitalAssetInfoName}.capitalAssetManufacturerModelNumber"/>
		<c:if test="${!readOnly}">
			<kul:htmlAttributeHeaderCell literalLabel="Action"/>
		</c:if>
   </tr>
   
   <tr>
		<fin:dataCell dataCellCssClass="${dataCellCssClass}" dataFieldCssClass="amount"
			businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
			field="capitalAssetQuantity" lookup="false" inquiry="false" />
			
		<fin:dataCell dataCellCssClass="${dataCellCssClass}"
			businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
			field="capitalAssetTypeCode" lookup="true" inquiry="true"
			boClassSimpleName="CapitalAssetManagementAssetType" boPackageName="org.kuali.kfs.integration.cam" 
			lookupOrInquiryKeys="capitalAssetTypeCode"
			businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>		
			   
		<fin:dataCell dataCellCssClass="${dataCellCssClass}"
			businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
			field="vendorName" lookup="true" inquiry="true" disabled="true"
			boClassSimpleName="VendorDetail" boPackageName="org.kuali.kfs.vnd.businessobject"
			lookupOrInquiryKeys="vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier,vendorName"
			businessObjectValuesMap="${capitalAssetInfo.valuesMap}" />	
		
		<fin:dataCell dataCellCssClass="${dataCellCssClass}"
			businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
			field="capitalAssetManufacturerName" lookup="false" inquiry="false"/>
		
		<fin:dataCell dataCellCssClass="${dataCellCssClass}"
			businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
			field="capitalAssetManufacturerModelNumber" lookup="false" inquiry="false"/>
		
		<c:if test="${!readOnly}">
		<td rowspan="2" class="infoline">  
			<div style="text-align: center;">	
				<html:image property="methodToCall.addCapitalAssetInfo" 
					src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" 
					title="Add the capital Asset Information"
					alt="Add the capital Asset Information" styleClass="tinybutton" />	
				<br/>	 
				<html:image property="methodToCall.clearCapitalAssetInfo" 
					src="${ConfigProperties.kr.externalizable.images.url}tinybutton-clear1.gif" 
					title="Clear the capital Asset Information"
					alt="Clear the capital Asset Information" styleClass="tinybutton" />
			</div>
		</td>
		</c:if>																										 
   </tr>
   <tr>
 		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetDescription}"/>
		<fin:dataCell dataCellCssClass="${dataCellCssClass}"
			businessObjectFormName="${capitalAssetInfoName}" attributes="${attributes}" readOnly="${readOnly}"
			field="capitalAssetDescription" lookup="false" inquiry="false" colSpan="4"/>
   </tr>   		

   <tr><td colSpan="${colspan}"><center><br/>
		<fin:capitalAssetInfoDetail capitalAssetInfoDetails="${capitalAssetInfo.capitalAssetInformationDetails}" 
			capitalAssetInfoDetailsName="${capitalAssetInfoName}.capitalAssetInformationDetails" readOnly="${readOnly}"/>
	<br/></center></td></tr>
</table>