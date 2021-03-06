/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.pdp.dataaccess.impl;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentProcess;
import org.kuali.kfs.pdp.dataaccess.FormatPaymentDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.service.BusinessObjectService;

public class FormatPaymentDaoOjb extends PlatformAwareDaoBaseOjb implements FormatPaymentDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatPaymentDaoOjb.class);

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.pdp.dataaccess.FormatPaymentDao#markPaymentsForFormat(org.kuali.kfs.pdp.businessobject.PaymentProcess,
     *      java.util.List, java.util.Date, java.lang.String)
     */
    public Iterator markPaymentsForFormat(List customerIds, Timestamp paydateTs, String paymentTypes) {
        LOG.debug("markPaymentsForFormat() started");

        Criteria criteria = new Criteria();

        if (customerIds.size() > 0) {
            criteria.addIn(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_BATCH + "." + PdpPropertyConstants.BatchConstants.CUSTOMER_ID, customerIds);
        }

        criteria.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYMENT_STATUS_CODE, PdpConstants.PaymentStatusCodes.OPEN);

        if (PdpConstants.PaymentTypes.DISBURSEMENTS_WITH_SPECIAL_HANDLING.equals(paymentTypes)) {
            // special handling only
            criteria.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_SPECIAL_HANDLING, Boolean.TRUE);
        }
        else if (PdpConstants.PaymentTypes.DISBURSEMENTS_NO_SPECIAL_HANDLING.equals(paymentTypes)) {
            // no special handling only
            criteria.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_SPECIAL_HANDLING, Boolean.FALSE);
        }
        else if (PdpConstants.PaymentTypes.DISBURSEMENTS_WITH_ATTACHMENTS.equals(paymentTypes)) {
            // attachments only
            criteria.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_ATTACHMENT, Boolean.TRUE);
        }
        else if (PdpConstants.PaymentTypes.DISBURSEMENTS_NO_ATTACHMENTS.equals(paymentTypes)) {
            // no attachments only
            criteria.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_ATTACHMENT, Boolean.FALSE);
        }

        if (PdpConstants.PaymentTypes.PROCESS_IMMEDIATE.equals(paymentTypes)) {
            criteria.addEqualTo(PdpPropertyConstants.PaymentGroup.PROCESS_IMMEDIATE, Boolean.TRUE);
        }
        else {
            // (Payment date <= usePaydate OR immediate = TRUE)
            Criteria criteria1 = new Criteria();
            criteria1.addEqualTo(PdpPropertyConstants.PaymentGroup.PROCESS_IMMEDIATE, Boolean.TRUE);

            Criteria criteria2 = new Criteria();
            criteria2.addLessOrEqualThan(PdpPropertyConstants.PaymentGroup.PAYMENT_DATE, paydateTs);
            criteria1.addOrCriteria(criteria2);

            criteria.addAndCriteria(criteria1);
        }

        Iterator groupIterator = getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentGroup.class, criteria));
        return groupIterator;
    }

    /**
     * @see org.kuali.kfs.pdp.dataaccess.FormatPaymentDao#unmarkPaymentsForFormat(org.kuali.kfs.pdp.businessobject.PaymentProcess)
     */
    public Iterator unmarkPaymentsForFormat(PaymentProcess proc) {
        LOG.debug("unmarkPaymentsForFormat() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PROCESS_ID, proc.getId());
        criteria.addEqualTo(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYMENT_STATUS_CODE, PdpConstants.PaymentStatusCodes.FORMAT);

        Iterator groupIterator = getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentGroup.class, criteria));
        return groupIterator;
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
