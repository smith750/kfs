/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CostCategory;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectCode;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectConsolidation;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectLevel;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Overridden to add informative help message
 */
public class CostCategoryMaintainableImpl extends FinancialSystemMaintainable {
    public CostCategoryMaintainableImpl() {
        super();
        initiateInactiveRecordDisplay();
    }

    public CostCategoryMaintainableImpl(PersistableBusinessObject businessObject) {
        super(businessObject);
        initiateInactiveRecordDisplay();
    }

    /**
     * By default, the children record collections will hide inactive records
     */
    protected void initiateInactiveRecordDisplay() {
        inactiveRecordDisplay.put(ArPropertyConstants.OBJECT_CODES, Boolean.FALSE);
        inactiveRecordDisplay.put(ArPropertyConstants.OBJECT_LEVELS, Boolean.FALSE);
        inactiveRecordDisplay.put(ArPropertyConstants.OBJECT_CONSOLIDATIONS, Boolean.FALSE);
    }

    /**
     * Overridden to push the cost category code to all child objects
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map, org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);
        if (StringUtils.equals(KFSConstants.MULTIPLE_VALUE, refreshCaller)) {
            final String collectionName = (String)fieldValues.get(KFSConstants.LOOKED_UP_COLLECTION_NAME);
            CostCategory costCategory = (CostCategory)document.getNewMaintainableObject().getBusinessObject();

            if (StringUtils.equals(collectionName, ArPropertyConstants.OBJECT_CODES) &&!CollectionUtils.isEmpty(costCategory.getObjectCodes())) {
                for (CostCategoryObjectCode objectCode : costCategory.getObjectCodes()) {
                    if (StringUtils.isBlank(objectCode.getCategoryCode())) {
                        objectCode.setCategoryCode(costCategory.getCategoryCode());
                    }
                }
            }
            if (StringUtils.equals(collectionName, ArPropertyConstants.OBJECT_LEVELS) && !CollectionUtils.isEmpty(costCategory.getObjectLevels())) {
                for (CostCategoryObjectLevel objectLevel : costCategory.getObjectLevels()) {
                    if (StringUtils.isBlank(objectLevel.getCategoryCode())) {
                        objectLevel.setCategoryCode(costCategory.getCategoryCode());
                    }
                }
            }
            if (StringUtils.equals(collectionName, ArPropertyConstants.OBJECT_CONSOLIDATIONS) && !CollectionUtils.isEmpty(costCategory.getObjectConsolidations())) {
                for (CostCategoryObjectConsolidation objectConsolidation : costCategory.getObjectConsolidations()) {
                    if (StringUtils.isBlank(objectConsolidation.getCategoryCode())) {
                        objectConsolidation.setCategoryCode(costCategory.getCategoryCode());
                    }
                }
            }
        }
        // is the old collection size bigger than the new?  let's fix that
        if (!ObjectUtils.isNull(document.getOldMaintainableObject()) && !ObjectUtils.isNull(document.getOldMaintainableObject().getBusinessObject())) {
            CostCategory oldCostCategory = (CostCategory)document.getOldMaintainableObject().getBusinessObject();
            CostCategory newCostCategory = (CostCategory)document.getNewMaintainableObject().getBusinessObject();

            procrustesList(newCostCategory.getObjectCodes(), oldCostCategory.getObjectCodes());
            procrustesList(newCostCategory.getObjectLevels(), oldCostCategory.getObjectLevels());
            procrustesList(newCostCategory.getObjectConsolidations(), oldCostCategory.getObjectConsolidations());
        }
    }

    /**
     * This method isn't quite procrustesing a list - it chops but it fails to stretch.  It's violent enough for our needs here, though: if the given visitor List is bigger than the given bed List, all
     * elements will be removed from visitor until it's no bigger than bed
     * @param bed the List that the visitor has to fit to
     * @param visitor maybe the visitor is a lucky List which is exactly the same size (or smaller) than the bed.  Wouldn't that be nice?
     */
    protected void procrustesList(List<?> bed, List<?> visitor) {
        if (visitor.size() > bed.size()) {
            removeAfter(visitor, bed.size());
        }
    }

    /**
     * This pulls an old "three blind mice" on the given List, removing any elements on or after the removalStartPoint
     * @param list the list to remove the tail of
     * @param removalStartPoint the point at which to start chopping
     */
    protected void removeAfter(List<?> list, int removalStartPoint) {
        final int listSize = list.size(); // list.size changes as we remove stuff, do remember
        for (int i = listSize - 1; i >= removalStartPoint; i--) {
            list.remove(i);
        }
    }

    /**
     * Overridden to filter down category code before cost category detail makes it to rules checking
     * @see org.kuali.kfs.sys.document.FinancialSystemMaintainable#processBeforeAddLine(java.lang.String, java.lang.Class, org.kuali.rice.krad.bo.BusinessObject)
     */
    @Override
    public void processBeforeAddLine(String colName, Class colClass, BusinessObject bo) {
        super.processBeforeAddLine(colName, colClass, bo);
        if (bo instanceof CostCategoryObjectCode) {
            ((CostCategoryObjectCode)bo).setCategoryCode(((CostCategory)getBusinessObject()).getCategoryCode());
        } else if (bo instanceof CostCategoryObjectLevel) {
            ((CostCategoryObjectLevel)bo).setCategoryCode(((CostCategory)getBusinessObject()).getCategoryCode());
        } else if (bo instanceof CostCategoryObjectConsolidation) {
            ((CostCategoryObjectConsolidation)bo).setCategoryCode(((CostCategory)getBusinessObject()).getCategoryCode());
        }
    }
}