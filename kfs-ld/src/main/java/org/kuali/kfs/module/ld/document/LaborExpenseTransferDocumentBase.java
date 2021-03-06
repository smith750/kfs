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
package org.kuali.kfs.module.ld.document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.document.Copyable;

/**
 * Labor Base class for Expense Transfer Documents
 */
public abstract class LaborExpenseTransferDocumentBase extends LaborLedgerPostingDocumentBase implements AmountTotaling, Copyable, Correctable, LaborExpenseTransferDocument {
    protected static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LaborExpenseTransferDocumentBase.class);
    protected String emplid;
    protected Person user;

    /**
     * Constructor
     */

    public LaborExpenseTransferDocumentBase() {
        super();
    }

    /**
     * Determine whether target accouting lines have the same amounts as source accounting lines for each object code
     * 
     * @return true if target accouting lines have the same amounts as source accounting lines for each object code; otherwise,
     *         false
     */
    public Map<String, KualiDecimal> getUnbalancedObjectCodes() {
        Map<String, KualiDecimal> amountsFromSourceLine = summerizeByObjectCode(getSourceAccountingLines());
        Map<String, KualiDecimal> amountsFromTargetLine = summerizeByObjectCode(getTargetAccountingLines());

        Map<String, KualiDecimal> unbalancedAmounts = new HashMap<String, KualiDecimal>();
        for (String objectCode : amountsFromSourceLine.keySet()) {
            KualiDecimal sourceAmount = amountsFromSourceLine.get(objectCode);

            if (!amountsFromTargetLine.containsKey(objectCode)) {
                unbalancedAmounts.put(objectCode, sourceAmount.negated());
            }
            else {
                KualiDecimal targetAmount = amountsFromTargetLine.get(objectCode);
                KualiDecimal amountDifference = targetAmount.subtract(sourceAmount);
                if (amountDifference.isNonZero()) {
                    unbalancedAmounts.put(objectCode, amountDifference);
                }
            }
        }

        for (String objectCode : amountsFromTargetLine.keySet()) {
            if (!amountsFromSourceLine.containsKey(objectCode)) {
                KualiDecimal targetAmount = amountsFromTargetLine.get(objectCode);
                unbalancedAmounts.put(objectCode, targetAmount);
            }
        }

        return unbalancedAmounts;
    }

    /**
     * summerize the amounts of accounting lines by object codes
     * 
     * @param accountingLines the given accounting line list
     * @return the summerized amounts by object codes
     */
    protected Map<String, KualiDecimal> summerizeByObjectCode(List accountingLines) {
        Map<String, KualiDecimal> amountByObjectCode = new HashMap<String, KualiDecimal>();

        for (Object accountingLine : accountingLines) {
            AccountingLine line = (AccountingLine) accountingLine;
            String objectCode = line.getFinancialObjectCode();
            KualiDecimal amount = line.getAmount();

            if (amountByObjectCode.containsKey(objectCode)) {
                amount = amount.add(amountByObjectCode.get(objectCode));
            }
            amountByObjectCode.put(objectCode, amount);
        }

        return amountByObjectCode;
    }

    /**
     * Gets the emplid
     * 
     * @return Returns the emplid.
     * @see org.kuali.kfs.module.ld.document.LaborExpenseTransferDocument#getEmplid()
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid
     * 
     * @see org.kuali.kfs.module.ld.document.LaborExpenseTransferDocument#setEmplid(String)
     * @param emplid
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }
    
    /**
     * Gets the user attribute. 
     * @return Returns the user.
     */
    public Person getUser() {
        if(user == null || !StringUtils.equals(user.getEmployeeId(), emplid)) {
            this.user = SpringContext.getBean(PersonService.class).getPersonByEmployeeId(emplid);
        }
        
        return user;
    }

    /**
     * Sets the user attribute value.
     * @param user The user to set.
     */
    public void setUser(Person user) {
        this.user = user;
    }    

    /**
     * Overrides the base implementation to return "From".
     * 
     * @see org.kuali.rice.krad.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.FROM;
    }

    /**
     * Overrides the base implementation to return "To".
     * 
     * @see org.kuali.rice.krad.document.AccountingDocument#getTargetAccountingLinesSectionTitle()
     */
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.TO;
    }

    /**
     * @return Returns the ExpenseTransferSourceAccountingLine
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    public Class getSourceAccountingLineClass() {
        return ExpenseTransferSourceAccountingLine.class;
    }

    /**
     * @return Returns the ExpenseTransferTargetAccountingLine
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getTargetAccountingLineClass()
     */
    public Class getTargetAccountingLineClass() {
        return ExpenseTransferTargetAccountingLine.class;
    }
    
}
