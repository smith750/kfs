/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.document.validation.impl;


import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.document.EquipmentLoanOrReturnDocument;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.PostalCode;
import org.kuali.kfs.sys.businessobject.State;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class EquipmentLoanOrReturnDocumentRule extends TransactionalDocumentRuleBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EquipmentLoanOrReturnDocumentRule.class);

    private AssetService assetService;

    /**
     * Does not fail on rules failure
     * 
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument = (EquipmentLoanOrReturnDocument) document;

        if (getAssetService().isAssetLocked(equipmentLoanOrReturnDocument.getDocumentNumber(), equipmentLoanOrReturnDocument.getCapitalAssetNumber())) {
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument = (EquipmentLoanOrReturnDocument) document;

        if (getAssetService().isAssetLocked(equipmentLoanOrReturnDocument.getDocumentNumber(), equipmentLoanOrReturnDocument.getCapitalAssetNumber())) {
            return false;
        }

        boolean valid = processValidation(equipmentLoanOrReturnDocument);

        return valid;
    }

    /**
     * This method applies business rules
     * 
     * @param document equipmentLoanOrReturnDocument Document
     * @return true if all rules are pass
     */
    protected boolean processValidation(EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument) {
        boolean valid = true;
        // validate if both loan return date and expected loan return date are valid
        valid &= validateLoanDate(equipmentLoanOrReturnDocument);
        // validate if borrower id is valid
        // valid &= validBorrowerId(equipmentLoanOrReturnDocument);
        // validate if borrower and storage state codes are avlid
        valid &= validStateZipCode(equipmentLoanOrReturnDocument);

        return valid;
    }

    /**
     * Implementation of the rule that if a document has a valid expect loan date and loan return date, the both dates should come
     * before the 2 years limit.
     * 
     * @param equipmentLoanOrReturnDocument the equipmentLoanOrReturn document to be validated
     * @return boolean false if the expect loan date or loan return date is not before the 2 years limit.
     */
    protected boolean validateLoanDate(EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument) {
        boolean valid = true;
        Date loanDate = equipmentLoanOrReturnDocument.getLoanDate();
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(loanDate);
        cal.add(Calendar.YEAR, 2);
        Date maxDate = new Date(cal.getTime().getTime());

        // Loan can not be before today
        Date loanReturnDate = equipmentLoanOrReturnDocument.getLoanReturnDate();
        DateUtils.clearTimeFields(loanDate);
        if (loanDate.before(DateUtils.clearTimeFields(new java.util.Date()))) {
            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.LOAN_DATE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_LOAN_DATE);
        }

        // expect return date must be >= loan date and within 2 years limit
        Date expectReturnDate = equipmentLoanOrReturnDocument.getExpectedReturnDate();
        if (expectReturnDate != null) {
            DateUtils.clearTimeFields(expectReturnDate);
            if (loanDate.after(expectReturnDate)) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.EXPECTED_RETURN_DATE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_EXPECTED_RETURN_DATE);
            }
        }
        
        if (maxDate.before(expectReturnDate)) {
            valid &= false;
            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.EXPECTED_RETURN_DATE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_LOAN_RETURN_DATE);
        }

        // loan return date must be >= loan date and withing 2 years limit
        if (loanReturnDate != null) {
            DateUtils.clearTimeFields(loanReturnDate);
            if (loanDate.after(loanReturnDate) || maxDate.before(loanReturnDate)) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.LOAN_RETURN_DATE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_LOAN_RETURN_DATE);
            }
        }

        return valid;
    }

    /**
     * Implementation of the rule that if borrower id is valid
     * 
     * @param equipmentLoanOrReturnDocument the equipmentLoanOrReturn document to be validated
     * @return boolean false if the borrower id does not exist. private boolean validBorrowerId(EquipmentLoanOrReturnDocument
     *         equipmentLoanOrReturnDocument) { boolean valid = false; if
     *         (StringUtils.isBlank(equipmentLoanOrReturnDocument.getBorrowerUniversalIdentifier())) {
     *         GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." +
     *         CamsPropertyConstants.EquipmentLoanOrReturnDocument.BORROWER_UNIVERSAL_USER + "." +
     *         KFSPropertyConstants.PERSON_USER_IDENTIFIER, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_BORROWER_ID); }
     *         else { valid = true; } return valid; }
     */


    /**
     * Implementation of the rule that if borrower and storage state codes are valid
     * 
     * @param equipmentLoanOrReturnDocument the equipmentLoanOrReturn document to be validated
     * @return boolean false if the borrower or storage state code is not valid
     */
    private boolean validStateZipCode(EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument) {
        boolean valid = true;
        // validate borrower state and postal zip code
        if (StringUtils.isBlank(equipmentLoanOrReturnDocument.getBorrowerCountryCode()  )) {
            equipmentLoanOrReturnDocument.setBorrowerCountryCode(KFSConstants.COUNTRY_CODE_UNITED_STATES);
        }
        State borrowStateCode = equipmentLoanOrReturnDocument.getBorrowerState();
        if (ObjectUtils.isNull(borrowStateCode)) {
            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.BORROWER_STATE_CODE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_BORROWER_STATE, equipmentLoanOrReturnDocument.getBorrowerStateCode());
            valid &= false;
        }

        PostalCode borrowerZipCode = equipmentLoanOrReturnDocument.getBorrowerPostalZipCode();
        if (ObjectUtils.isNull(borrowerZipCode)) {
            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.BORROWER_POSTAL_ZIP_CODE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_BORROWER_ZIP_CODE, equipmentLoanOrReturnDocument.getBorrowerStateCode());
            valid &= false;
        }
        else {
            // validate postal zip code against state code
            if (StringUtils.isNotBlank(equipmentLoanOrReturnDocument.getBorrowerStateCode())) {
                if (!equipmentLoanOrReturnDocument.getBorrowerStateCode().equals(borrowerZipCode.getPostalStateCode())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.BORROWER_STATE_CODE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_BORROWER_STATE_ZIP_CODE, equipmentLoanOrReturnDocument.getBorrowerStateCode(), equipmentLoanOrReturnDocument.getBorrowerStateCode());
                    valid &= false;
                }
            }
        }

        // validate borrower storage state and postal zip code
        if (StringUtils.isNotBlank(equipmentLoanOrReturnDocument.getBorrowerStorageStateCode())) {
            if (StringUtils.isBlank(equipmentLoanOrReturnDocument.getBorrowerStorageCountryCode()  )) {
                equipmentLoanOrReturnDocument.setBorrowerStorageCountryCode(KFSConstants.COUNTRY_CODE_UNITED_STATES);
            }
            State borrowStorageStateCode = equipmentLoanOrReturnDocument.getBorrowerStorageState();
            if (ObjectUtils.isNull(borrowStorageStateCode)) {
                GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.BORROWER_STAORAGE_STATE_CODE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_BORROWER_STORAGE_STATE, equipmentLoanOrReturnDocument.getBorrowerStorageStateCode());
                valid = false;
            }
        }

        if (StringUtils.isNotBlank(equipmentLoanOrReturnDocument.getBorrowerStorageZipCode())) {
            PostalCode borrowStorageZipCode = equipmentLoanOrReturnDocument.getBorrowerStoragePostalZipCode();
            if (ObjectUtils.isNull(borrowStorageZipCode)) {
                GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.BORROWER_STORAGE_POSTAL_ZIP_CODE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_BORROWER_STORAGE_ZIP_CODE, equipmentLoanOrReturnDocument.getBorrowerStorageStateCode());
                valid = false;
            }
            else {
                // validate postal zip code against state code
                if (StringUtils.isNotBlank(equipmentLoanOrReturnDocument.getBorrowerStorageStateCode())) {
                    if (!equipmentLoanOrReturnDocument.getBorrowerStorageStateCode().equals(borrowStorageZipCode.getPostalStateCode())) {
                        GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + CamsPropertyConstants.EquipmentLoanOrReturnDocument.BORROWER_STAORAGE_STATE_CODE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_BORROWER_STORAGE_STATE_ZIP_CODE, equipmentLoanOrReturnDocument.getBorrowerStorageStateCode(), equipmentLoanOrReturnDocument.getBorrowerStorageStateCode());
                        valid &= false;
                    }
                }
            }
        }

        return valid;
    }

    public AssetService getAssetService() {
        if (this.assetService == null) {
            this.assetService = SpringContext.getBean(AssetService.class);
        }
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }
}
