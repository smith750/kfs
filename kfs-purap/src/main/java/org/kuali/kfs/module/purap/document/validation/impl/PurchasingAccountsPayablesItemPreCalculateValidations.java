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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.math.BigDecimal;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

public class PurchasingAccountsPayablesItemPreCalculateValidations extends GenericValidation {
    
    private PurApItem item;
    
    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        
        PurchasingAccountsPayableDocumentBase purApDocument = (PurchasingAccountsPayableDocumentBase) event.getDocument();
        String accountDistributionMethod = purApDocument.getAccountDistributionMethod();

        if (PurapConstants.AccountDistributionMethodCodes.SEQUENTIAL_CODE.equalsIgnoreCase(accountDistributionMethod)) {
            return this.checkTotalPercentAndTotalAmountsEqual(item);
        }
        
        return this.checkTotalPercentOrTotalAmountsEqual(item);
    }
    
    /**
     * checks for both percent = 100% and item total = account amount total
     * 
     * @param item
     * @return true when percent = 100% AND total amount = item total
     */
    public boolean checkTotalPercentAndTotalAmountsEqual(PurApItem item) {
        boolean valid = true;
        
        valid &= validateTotalPercent(item, true);
        
        if (valid) {
            valid &= validateTotalAmount(item, true);
        }
        
        return valid;
    }
    
    /**
     * checks for only either percent = 100% or item total = account amount total
     * 
     * @param item
     * @return true when either percent = 100% OR total amount = item total
     */
    public boolean checkTotalPercentOrTotalAmountsEqual(PurApItem item) {
        boolean valid = false;
        
        boolean validPercent = validateTotalPercent(item, false);
        if (validPercent) {
            return true;
        }
        
        boolean validAmount = validateTotalAmount(item, false);
        if (validAmount) {
            return true;
        }
        
        KualiDecimal desiredAmount = (item.getTotalAmount() == null) ? new KualiDecimal(0) : item.getTotalAmount();
        
        if (!validPercent && !validAmount) {
            GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_PERCENT_OR_AMOUNT_INVALID, item.getItemIdentifierString(),desiredAmount.toString());
        } else {
            if (!validPercent) {
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_TOTAL, item.getItemIdentifierString());
            } else {
                if (!validAmount) {
                    GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_TOTAL_AMOUNT, item.getItemIdentifierString(),desiredAmount.toString());
                }        
            }
        }
        
        return valid;
    }
    
    /**
     * Verifies account percent. If the total percent does not equal 100, 
     * the validation fails.
     * @param item
     * @param writeErrorMessage true if error message to be added to global error variables, else false
     * @return true if percent sum = 100%
     */
    public boolean validateTotalPercent(PurApItem item, boolean writeErrorMessage) {
        boolean valid = true;
        
        if (item.getSourceAccountingLines().size() == 0) {
            return valid;
        }
        
        // validate that the percents total 100 for each item
        BigDecimal totalPercent = BigDecimal.ZERO;
        BigDecimal desiredPercent = new BigDecimal("100");
        for (PurApAccountingLine account : item.getSourceAccountingLines()) {
            if (account.getAccountLinePercent() != null) {
                totalPercent = totalPercent.add(account.getAccountLinePercent());
            }
            else {
                totalPercent = totalPercent.add(BigDecimal.ZERO);
            }
        }
        if (desiredPercent.compareTo(totalPercent) != 0) {
            if (writeErrorMessage) {
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_TOTAL, item.getItemIdentifierString());
            }
            
            valid = false;
        }

        return valid;
    }
    
    /**
     * Verifies account amounts = item total. If does not equal then validation fails. 
     * @param item
     * @param writeErrorMessage true if error message to be added to global error variables, else false
     * @return true if account amounts sum = item total
     */
    public boolean validateTotalAmount(PurApItem item, boolean writeErrorMessage) {
        boolean valid = true;
        
        if (item.getSourceAccountingLines().size() == 0) {
            return valid;
        }
        
        if (item.getItemQuantity() == null || item.getItemUnitPrice() == null || item.getTotalAmount().compareTo(KualiDecimal.ZERO) == 0) {
            //extended cost is not available yet so do not run validations....
            return valid;
        }
        
     // validate that the amount total 
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        
        KualiDecimal desiredAmount = (item.getTotalAmount() == null) ? new KualiDecimal(0) : item.getTotalAmount();
        for (PurApAccountingLine account : item.getSourceAccountingLines()) {
            if (account.getAmount() != null) {
                totalAmount = totalAmount.add(account.getAmount());
            }
            else {
                totalAmount = totalAmount.add(KualiDecimal.ZERO);
            }
        }
        
        if (desiredAmount.compareTo(totalAmount) != 0) {
            if (writeErrorMessage) {
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_TOTAL_AMOUNT, item.getItemIdentifierString(),desiredAmount.toString());
            }
            valid = false;
        }

        return valid;
    }
    
    /**
     * Sets the accountingDocumentForValidation attribute value.
     * 
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setItem(PurApItem item) {
        this.item = item;
    }
}
