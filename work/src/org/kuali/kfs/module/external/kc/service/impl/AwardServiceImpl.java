/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.dto.HashMapElement;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.module.external.kc.businessobject.Agency;
import org.kuali.kfs.module.external.kc.businessobject.Award;
import org.kuali.kfs.module.external.kc.businessobject.AwardFundManager;
import org.kuali.kfs.module.external.kc.businessobject.AwardOrganization;
import org.kuali.kfs.module.external.kc.businessobject.LetterOfCreditFund;
import org.kuali.kfs.module.external.kc.businessobject.Proposal;
import org.kuali.kfs.module.external.kc.dto.AwardDTO;
import org.kuali.kfs.module.external.kc.service.AccountDefaultsService;
import org.kuali.kfs.module.external.kc.service.BillingFrequencyService;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.kfs.module.external.kc.service.KfsService;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.module.external.kc.webService.AwardWebSoapService;
import org.kuali.kra.external.award.service.AwardWebService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * This class was generated by Apache CXF 2.2.10
 * Thu Sep 30 05:29:28 HST 2010
 * Generated source version: 2.2.10
 *
 */

public class AwardServiceImpl implements ExternalizableBusinessObjectService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AwardServiceImpl.class);

    private AccountDefaultsService accountDefaultsService;
    private BillingFrequencyService billingFrequencyService;

    protected AwardWebService getWebService() {
        // first attempt to get the service from the KSB - works when KFS & KC share a Rice instance
        AwardWebService awardWebService = (AwardWebService) GlobalResourceLoader.getService(KcConstants.Award.SERVICE);

        // if we couldn't get the service from the KSB, get as web service - for when KFS & KC have separate Rice instances
        if (awardWebService == null) {
            LOG.warn("Couldn't get AwardWebService from KSB, setting it up as SOAP web service - expected behavior for bundled Rice, but not when KFS & KC share a standalone Rice instance.");
            AwardWebSoapService ss =  null;
            try {
                ss = new AwardWebSoapService();
            }
            catch (MalformedURLException ex) {
                LOG.error("Could not intialize AwardWebSoapService: " + ex.getMessage());
                throw new RuntimeException("Could not intialize AwardWebSoapService: " + ex.getMessage());
            }
            awardWebService = ss.getAwardWebServicePort();
        }

        return awardWebService;
    }

    @Override
    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        //use the proposal number as its the awardId on the KC side.
        AwardDTO dto  = this.getWebService().getAward((Long)primaryKeys.get("proposalNumber"));
        return awardFromDTO(dto);
    }

    @Override
    public Collection findMatching(Map fieldValues) {
        java.util.List <HashMapElement> hashMapList = new ArrayList<HashMapElement>();
        List<AwardDTO> result = null;

        for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            String key = (String) e.getKey();
            String val = (String) e.getValue();

            if ( KcConstants.Award.KC_ALLOWABLE_CRITERIA_PARAMETERS.contains(key)  && (val.length() > 0)) {
                HashMapElement hashMapElement = new HashMapElement();
                hashMapElement.setKey(key);
                hashMapElement.setValue(val);
                hashMapList.add(hashMapElement);
            }
        }
        try {
          result  = this.getWebService().getMatchingAwards(hashMapList);
        } catch (WebServiceException ex) {
            GlobalVariablesExtractHelper.insertError(KcConstants.WEBSERVICE_UNREACHABLE, KfsService.getWebServiceServerName());
        }

        if (result == null) {
            return new ArrayList();
        } else {
            List<Award> awards = new ArrayList<Award>();
            for (AwardDTO dto : result) {
                awards.add(awardFromDTO(dto));
            }
            return awards;
        }
    }

    protected Award awardFromDTO(AwardDTO kcAward) {
        Award award = new Award();
        award.setProposalNumber(kcAward.getAwardId());
        award.setAwardNumber(kcAward.getAwardNumber());
        award.setAwardBeginningDate(kcAward.getAwardStartDate());
        award.setAwardEndingDate(kcAward.getAwardEndDate());
        award.setAwardTotalAmount(kcAward.getAwardTotalAmount());
        award.setAwardDirectCostAmount(kcAward.getAwardDirectCostAmount());
        award.setAwardIndirectCostAmount(kcAward.getAwardIndirectCostAmount());
        award.setAwardDocumentNumber(kcAward.getAwardDocumentNumber());
        award.setAwardLastUpdateDate(kcAward.getAwardLastUpdateDate());
        award.setAwardCreateTimestamp(kcAward.getAwardCreateTimestamp());
        award.setProposalAwardTypeCode(kcAward.getProposalAwardTypeCode());
        award.setAwardStatusCode(kcAward.getAwardStatusCode());
        award.setAgencyNumber(kcAward.getSponsorCode());
        award.setAwardTitle(kcAward.getTitle());
        award.setAwardCommentText(kcAward.getAwardCommentText());
        award.setAgency(new Agency(kcAward.getSponsor()));
        award.setProposal(new Proposal(kcAward.getProposal()));
        award.getProposal().setAward(award);
        award.setAdditionalFormsRequiredIndicator(kcAward.isAdditionalFormsRequired());
        award.setAutoApproveIndicator(kcAward.isAutoApproveInvoice());
        award.setMinInvoiceAmount(kcAward.getMinInvoiceAmount());
        award.setAdditionalFormsDescription(kcAward.getAdditionalFormsDescription());
        award.setStopWorkIndicator(kcAward.isStopWork());
        award.setCommentText(kcAward.getStopWorkReason());
        award.setInvoicingOptions(kcAward.getInvoicingOption());
        award.setDunningCampaign(kcAward.getDunningCampaignId());
        if (StringUtils.isNotEmpty(kcAward.getFundManagerId())) {
            award.setAwardPrimaryFundManager(new AwardFundManager(award.getProposalNumber(), kcAward.getFundManagerId()));
        }
        AccountAutoCreateDefaults defaults = getAccountDefaultsService().getAccountDefaults(kcAward.getUnitNumber());
        if (defaults != null) {
            AwardOrganization awardOrg = new AwardOrganization();
            awardOrg.setActive(true);
            awardOrg.setAwardPrimaryOrganizationIndicator(true);
            awardOrg.setChartOfAccountsCode(defaults.getChartOfAccountsCode());
            awardOrg.setChartOfAccounts(defaults.getChartOfAccounts());
            awardOrg.setOrganization(defaults.getOrganization());
            awardOrg.setOrganizationCode(defaults.getOrganizationCode());
            awardOrg.setProposalNumber(award.getProposalNumber());
            award.setPrimaryAwardOrganization(awardOrg);
        }
        award.setLetterOfCreditFundCode(kcAward.getMethodOfPayment().getMethodOfPaymentCode());
        award.setLetterOfCreditFund(new LetterOfCreditFund(kcAward.getMethodOfPayment().getMethodOfPaymentCode(), kcAward.getMethodOfPayment().getDescription()));
        award.setBillingFrequency(getBillingFrequencyService().createBillingFrequency(kcAward.getInvoiceBillingFrequency()));
        return award;
    }

    protected AccountDefaultsService getAccountDefaultsService() {
        return accountDefaultsService;
    }

    public void setAccountDefaultsService(AccountDefaultsService accountDefaultsService) {
        this.accountDefaultsService = accountDefaultsService;
    }

    protected BillingFrequencyService getBillingFrequencyService() {
        return billingFrequencyService;
    }

    public void setBillingFrequencyService(BillingFrequencyService billingFrequencyService) {
        this.billingFrequencyService = billingFrequencyService;
    }

 }
