/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.vnd.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.CommodityCode;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * Values Finder for <code>CommodityCode</code>.
 * 
 * @see org.kuali.kfs.vnd.businessobject.CommodityCode
 */
public class CommodityCodeValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection sscs = boService.findAll(CommodityCode.class);
        List labels = new ArrayList();
        labels.add(new KeyLabelPair("", ""));
        for (Iterator iter = sscs.iterator(); iter.hasNext();) {
            CommodityCode cc = (CommodityCode) iter.next();
            labels.add(new KeyLabelPair(cc.getPurchasingCommodityCode(), cc.getCommodityDescription()));
        }

        return labels;
    }


}
