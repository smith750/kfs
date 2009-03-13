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
package org.kuali.kfs.sys.document.workflow;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.rice.kew.docsearch.DocumentSearchContext;
import org.kuali.rice.kew.docsearch.SearchableAttributeFloatValue;
import org.kuali.rice.kew.docsearch.SearchableAttributeStringValue;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.datadictionary.DataDictionaryEntry;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.KeyLabelPair;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.workflow.attribute.DataDictionarySearchableAttribute;

public class FinancialSystemSearchableAttribute extends DataDictionarySearchableAttribute {

    public List<Row> getSearchingRows(DocumentSearchContext documentSearchContext) {
        List<Row> docSearchRows = super.getSearchingRows(documentSearchContext);
        
        DocumentEntry entry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(documentSearchContext.getDocumentTypeName());
        if (entry == null) {
            return docSearchRows;
        }
        Class<? extends Document> docClass = entry.getDocumentClass();
        Document doc = null;
        try {
            doc = docClass.newInstance();
        } catch (Exception e){}
        if (doc instanceof AmountTotaling) {
          //   "FinancialSystemDocumentHeader";
            final DataDictionaryEntry boEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDictionaryObjectEntry("FinancialSystemDocumentHeader");
            String businessObjectClassName = boEntry.getFullClassName();
            Class boClass = null;
            try {
                boClass = Class.forName(businessObjectClassName);
            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException(cnfe);
            }
            
            Field searchField = FieldUtils.getPropertyField(boClass, "financialDocumentTotalAmount", true);

            
            List<Field> fieldList = new ArrayList<Field>();
            fieldList.add(searchField);
            docSearchRows.add(new Row(fieldList));
            
        }
        
//        if (doc instanceof AccountingDocument) {
//            final DataDictionaryEntry boEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDictionaryObjectEntry("AccountingLineBase");
//            String businessObjectClassName = boEntry.getFullClassName();
//            Class boClass = null;
//            try {
//                boClass = Class.forName(businessObjectClassName);
//            } catch (ClassNotFoundException cnfe) {
//                throw new RuntimeException(cnfe);
//            }
//            
//            Field chartField = FieldUtils.getPropertyField(boClass, "chartOfAccountsCode", true);
//            Field accountField = FieldUtils.getPropertyField(boClass, "accountNumber", true);
//
//            
//            List<Field> fieldList = new ArrayList<Field>();
//            fieldList.add(chartField);
//            fieldList.add(accountField);
//
//            docSearchRows.add(new Row(fieldList));
//        }
        Row resultType = createSearchResultReturnRow();
        docSearchRows.add(resultType);
        return docSearchRows;
    }
    
    public List<SearchableAttributeValue> getSearchStorageValues(DocumentSearchContext documentSearchContext) {
        List<SearchableAttributeValue> searchAttrValues =  super.getSearchStorageValues(documentSearchContext);
        
        String docId = documentSearchContext.getDocumentId();
        DocumentService docService = SpringContext.getBean(DocumentService.class);
        Document doc = null;
        try  {
            doc = docService.getByDocumentHeaderIdSessionless(docId);
        } catch (WorkflowException we) {
            
        }
        
        if (doc instanceof AmountTotaling) {
            SearchableAttributeFloatValue searchableAttributeValue = new SearchableAttributeFloatValue();
            searchableAttributeValue.setSearchableAttributeKey("financialDocumentTotalAmount");
            searchableAttributeValue.setSearchableAttributeValue(((AmountTotaling)doc).getTotalDollarAmount().bigDecimalValue());
            searchAttrValues.add(searchableAttributeValue);
        }
//        if (doc instanceof AccountingDocument) {
//            SearchableAttributeStringValue searchableAttributeValue = new SearchableAttributeStringValue();
//            searchableAttributeValue.setSearchableAttributeKey("chartOfAccountsCode");
//            searchableAttributeValue.setSearchableAttributeValue(((AccountingDocument)doc).getChartOfAccountsCode());
//           
//            searchAttrValues.add(searchableAttributeValue);
//        }
//        
        
        return searchAttrValues;
    }
    
    private Row createSearchResultReturnRow() {
        String attributeName = "displayType";
        Field searchField = new Field();
        searchField.setPropertyName(attributeName);
        searchField.setFieldType(Field.RADIO);
        searchField.setFieldLabel("Search Result Type");
        searchField.setIndexedForSearch(false);
        searchField.setBusinessObjectClassName("");
        searchField.setFieldHelpName("");
        searchField.setFieldHelpSummary("");
        searchField.setColumnVisible(false);
        List<KeyLabelPair> values = new ArrayList<KeyLabelPair>();
        values.add(new KeyLabelPair("document", "Document Specific Data"));
        values.add(new KeyLabelPair("workflow", "Workflow Data"));
        searchField.setFieldValidValues(values);
   //     searchField.setDefaultValue("document");
        searchField.setPropertyValue("document");

        List<Field> fieldList = new ArrayList<Field>();
        fieldList.add(searchField);

        return new Row(fieldList);
        
    }
    
}
