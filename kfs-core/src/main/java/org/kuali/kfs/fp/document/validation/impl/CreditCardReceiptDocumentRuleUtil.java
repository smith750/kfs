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
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.kfs.fp.document.validation.impl.CreditCardReceiptDocumentRuleConstants.CREDIT_CARD_RECEIPT_PREFIX;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.kfs.fp.businessobject.CreditCardDetail;
import org.kuali.kfs.fp.document.CreditCardReceiptDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSKeyConstants.CashReceipt;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Common Credit Card Receipt Document rule utilities.
 */
public class CreditCardReceiptDocumentRuleUtil {
    /**
     * This method method will invoke the data dictionary validation for a CreditCardDetail bo instance, in addition to checking
     * existence of the CreditCardType and CreditCardVendor attributes that hang off of it. This method assumes that the document
     * hierarchy for the error map path is managed outside of this call.
     * 
     * @param creditCardReceipt credit card detail
     * @return true if credit card detail amount is non zero and credit card vendor and type references exist
     */
    public static boolean validateCreditCardReceipt(CreditCardDetail creditCardReceipt) {
        MessageMap errorMap = GlobalVariables.getMessageMap();
        int originalErrorCount = errorMap.getErrorCount();

        // call the DD validation which checks basic data integrity
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(creditCardReceipt);
        boolean isValid = (errorMap.getErrorCount() == originalErrorCount);

        // check that dollar amount is not zero before continuing
        if (isValid) {
            isValid = !creditCardReceipt.getCreditCardAdvanceDepositAmount().isZero();
            if (!isValid) {
                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(CreditCardDetail.class, KFSPropertyConstants.CREDIT_CARD_ADVANCE_DEPOSIT_AMOUNT);
                errorMap.putError(KFSPropertyConstants.CREDIT_CARD_ADVANCE_DEPOSIT_AMOUNT, KFSKeyConstants.ERROR_ZERO_AMOUNT, label);
            }
        }

        if (isValid) {
            isValid = SpringContext.getBean(DictionaryValidationService.class).validateReferenceExists(creditCardReceipt, KFSPropertyConstants.CREDIT_CARD_TYPE);
            if (!isValid) {
                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(CreditCardDetail.class, KFSPropertyConstants.FINANCIAL_DOCUMENT_CREDIT_CARD_TYPE_CODE);
                errorMap.putError(KFSPropertyConstants.FINANCIAL_DOCUMENT_CREDIT_CARD_TYPE_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
            }
        }
        if (isValid) {
            isValid = SpringContext.getBean(DictionaryValidationService.class).validateReferenceExists(creditCardReceipt, KFSPropertyConstants.CREDIT_CARD_VENDOR);
            if (!isValid) {
                String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(CreditCardDetail.class, KFSPropertyConstants.FINANCIAL_DOCUMENT_CREDIT_CARD_VENDOR_NUMBER);
                errorMap.putError(KFSPropertyConstants.FINANCIAL_DOCUMENT_CREDIT_CARD_VENDOR_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, label);
            }
        }

        return isValid;
    }


    /**
     * Checks whether the CashReceiptDocument's cash totals are invalid, generating global errors if so.
     * 
     * @param cashReceiptDocument submitted cash receipt document
     * @return true if any of the cash totals on cash credit card receipt document are invalid
     */
    public static boolean areCashTotalsInvalid(CreditCardReceiptDocument ccrDocument) {
        String documentEntryName = ccrDocument.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();

        boolean isInvalid = isTotalInvalid(ccrDocument, ccrDocument.getTotalDollarAmount(), documentEntryName, KFSPropertyConstants.CREDIT_CARD_RECEIPTS_TOTAL);

        return isInvalid;
    }

    /**
     * Returns true if total is invalid and puts an error message in the error map for that property if the amount is negative
     * 
     * @param cashReceiptDocument
     * @param totalAmount
     * @param documentEntryName
     * @param propertyName
     * @return true if the totalAmount is an invalid value
     */
    private static boolean isTotalInvalid(CreditCardReceiptDocument ccrDocument, KualiDecimal totalAmount, String documentEntryName, String propertyName) {
        boolean isInvalid = false;
        String errorProperty = CREDIT_CARD_RECEIPT_PREFIX + propertyName;

        // treating null totalAmount as if it were a zero
        DataDictionaryService dds = SpringContext.getBean(DataDictionaryService.class);
        String errorLabel = dds.getAttributeLabel(documentEntryName, propertyName);
        if ((totalAmount == null) || totalAmount.isZero()) {
            GlobalVariables.getMessageMap().putError(errorProperty, CashReceipt.ERROR_ZERO_TOTAL, errorLabel);

            isInvalid = true;
        }
        else {
            int precount = GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors();

            DictionaryValidationService dvs = SpringContext.getBean(DictionaryValidationService.class);
            dvs.validateDocumentAttribute(ccrDocument, propertyName, DOCUMENT_ERROR_PREFIX);

            // replace generic error message, if any, with something more readable
            GlobalVariables.getMessageMap().replaceError(errorProperty, KFSKeyConstants.ERROR_MAX_LENGTH, CashReceipt.ERROR_EXCESSIVE_TOTAL, errorLabel);

            int postcount = GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors();
            isInvalid = (postcount > precount);
        }

        return isInvalid;
    }
}
