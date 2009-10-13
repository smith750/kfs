/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.document.web.struts;

import org.kuali.kfs.module.ld.document.YearEndBenefitExpenseTransferDocument;

/**
 * Struts Action Form for the Year End Benefit Expense Transfer Document.
 */
public class YearEndBenefitExpenseTransferForm extends BenefitExpenseTransferForm {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(YearEndBenefitExpenseTransferForm.class);

    /**
     * Constructs a BenefitExpenseTransferForm instance and sets up the appropriately casted document.
     */
    public YearEndBenefitExpenseTransferForm() {
        super();
    }
    
    @Override
    protected String getDefaultDocumentTypeName() {
        return "YEBT";
    }
}
