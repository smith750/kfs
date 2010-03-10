/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject.inquiry;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentReportingGroup;
import org.kuali.kfs.module.endow.businessobject.KEMIDHistoricalReportingGroup;
import org.kuali.kfs.module.endow.businessobject.KEMIDHistoricalTaxLot;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.UrlFactory;

public class KEMIDHistoricalReportingGroupInquirable extends KfsInquirableImpl {

    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        KEMIDHistoricalReportingGroup historicalReportingGroup = (KEMIDHistoricalReportingGroup) businessObject;
        if (EndowPropertyConstants.KEMID_CRNT_REP_GRP_UNITS.equals(attributeName) && ObjectUtils.isNotNull(historicalReportingGroup.getUnits())) {

            Properties params = new Properties();
            params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, KEMIDHistoricalTaxLot.class.getName());
            params.put(KNSConstants.DOC_FORM_KEY, "88888888");
            params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "true");
            params.put(KFSConstants.BACK_LOCATION, SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KNSConstants.APPLICATION_URL_KEY) + "/" + KFSConstants.MAPPING_PORTAL + ".do");
            // params.put(KFSConstants.LOOKUP_READ_ONLY_FIELDS, EndowPropertyConstants.KEMID + "," +
            // EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_PURPOSE_CD + "," + EndowPropertyConstants.CURRENT_TAX_LOT_REP_GRP + ","
            // + EndowPropertyConstants.CURRENT_TAX_LOT_IP_IND + "," + EndowPropertyConstants.CURRENT_TAX_LOT_SECURITY_ID + "," +
            // EndowPropertyConstants.CURRENT_TAX_LOT_REGIS_CD + "," + EndowPropertyConstants.CURRENT_TAX_LOT_BALANCE_DATE + "," +
            // EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_CLOSED_IND + "," + EndowPropertyConstants.CURRENT_TAX_LOT_REGIS_DESC +
            // "," + EndowPropertyConstants.CURRENT_TAX_LOT_SEC_DESC + "," + EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_SHORT_TTL
            // + "," + EndowPropertyConstants.CURRENT_TAX_LOT_PURPOSE_DESC + "," +
            // EndowPropertyConstants.CURRENT_TAX_LOT_INC_PRIN_DESC);
             params.put(EndowPropertyConstants.KEMID, UrlFactory.encode(String.valueOf(historicalReportingGroup.getKemid())));
            // params.put(EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_PURPOSE_CD,
            // UrlFactory.encode(historicalReportingGroup.getKemidObj().getPurposeCode()));
            // params.put(EndowPropertyConstants.CURRENT_TAX_LOT_REP_GRP,
            // UrlFactory.encode(historicalReportingGroup.getReportingGroupCode()));
            // params.put(EndowPropertyConstants.CURRENT_TAX_LOT_IP_IND,
            // UrlFactory.encode(historicalReportingGroup.getIpIndicator()));
            params.put(EndowPropertyConstants.KEMID_HIST_REP_GRP_SEC_ID, UrlFactory.encode(historicalReportingGroup.getSecurityId()));
            // params.put(EndowPropertyConstants.CURRENT_TAX_LOT_REGIS_CD,
            // UrlFactory.encode(historicalReportingGroup.getRegistrationCode()));

            params.put("monthEndDateId", UrlFactory.encode(String.valueOf(historicalReportingGroup.getHistoryBalanceDateId())));
            // params.put(EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_CLOSED_IND,
            // historicalReportingGroup.getKemidObj().isClosedIndicator() ? "Yes" : "No");
            // params.put(EndowPropertyConstants.CURRENT_TAX_LOT_REGIS_DESC, historicalReportingGroup.getRegistration().getName());
            // params.put(EndowPropertyConstants.CURRENT_TAX_LOT_SEC_DESC, historicalReportingGroup.getSecurity().getDescription());
            // params.put(EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_SHORT_TTL,
            // historicalReportingGroup.getKemidObj().getShortTitle());
            // params.put(EndowPropertyConstants.CURRENT_TAX_LOT_PURPOSE_DESC,
            // historicalReportingGroup.getKemidObj().getPurpose().getName());
            // params.put(EndowPropertyConstants.CURRENT_TAX_LOT_INC_PRIN_DESC,
            // historicalReportingGroup.getIncomePrincipalIndicator().getName());

            String url = UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, params);

            Map<String, String> fieldList = new HashMap<String, String>();
             fieldList.put(EndowPropertyConstants.KEMID, historicalReportingGroup.getKemid().toString());
            // fieldList.put(EndowPropertyConstants.CURRENT_BAL_PURPOSE_CD,
            // historicalReportingGroup.getKemidObj().getPurposeCode());
            // fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_REP_GRP, historicalReportingGroup.getReportingGroupCode());
            // fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_IP_IND, historicalReportingGroup.getIpIndicator());
            fieldList.put(EndowPropertyConstants.KEMID_HIST_REP_GRP_SEC_ID, historicalReportingGroup.getSecurityId());
            // fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_REGIS_CD, historicalReportingGroup.getRegistrationCode());
            fieldList.put("monthEndDateId", String.valueOf(historicalReportingGroup.getHistoryBalanceDateId()));
            // fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_CLOSED_IND,
            // historicalReportingGroup.getKemidObj().isClosedIndicator() ? "Yes" : "No");
            // fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_REGIS_DESC,
            // historicalReportingGroup.getRegistration().getName());
            // fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_SEC_DESC,
            // historicalReportingGroup.getSecurity().getDescription());
            // fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_KEMID_SHORT_TTL,
            // historicalReportingGroup.getKemidObj().getShortTitle());
            // fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_PURPOSE_DESC,
            // historicalReportingGroup.getKemidObj().getPurpose().getName());
            // fieldList.put(EndowPropertyConstants.CURRENT_TAX_LOT_INC_PRIN_DESC,
            // historicalReportingGroup.getIncomePrincipalIndicator().getName());

            return getHyperLink(KEMIDHistoricalTaxLot.class, fieldList, url);
        }
        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

}
