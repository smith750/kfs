/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.document.web.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.batch.service.AssetBarcodeInventoryLoadService;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.module.cam.document.validation.event.ValidateBarcodeInventoryEvent;
import org.kuali.kfs.module.cam.util.BarcodeInventoryErrorDetailPredicate;
import org.kuali.kfs.module.ld.businessobject.LaborGeneralLedgerEntry;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.exception.WorkflowException;


public class BarcodeInventoryErrorAction extends FinancialSystemTransactionalDocumentActionBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorAction.class);
    private static final KualiRuleService kualiRuleService = SpringContext.getBean(KualiRuleService.class);
    private static final DocumentService documentService = SpringContext.getBean(DocumentService.class);
    private static final AssetBarcodeInventoryLoadService assetBarcodeInventoryLoadService = SpringContext.getBean(AssetBarcodeInventoryLoadService.class);
    private static final BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     *
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
    }*/

    /**
     * Adds handling for cash control detail amount updates.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BarcodeInventoryErrorForm apForm = (BarcodeInventoryErrorForm) form;
        String command = ((BarcodeInventoryErrorForm) form).getCommand();
        String docID = ((BarcodeInventoryErrorForm) form).getDocId();

        LOG.info("***BarcodeInventoryErrorAction.execute() - menthodToCall: " + apForm.getMethodToCall() + " - Command:" + command + " - DocId:" + docID);
        return super.execute(mapping, form, request, response);
    }


    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        LOG.info("***BarcodeInventoryErrorAction.load()");

        super.loadDocument(kualiDocumentFormBase);

        BarcodeInventoryErrorForm bcieForm = (BarcodeInventoryErrorForm) kualiDocumentFormBase;
        BarcodeInventoryErrorDocument document = bcieForm.getBarcodeInventoryErrorDocument();

        //To validate all rows
        document.setBarcodeInventoryErrorSelectedRows(true);

        this.invokeRules(document);
    }


    /**
     * 
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward searchAndReplace(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BarcodeInventoryErrorForm barcodeInventoryErrorForm = (BarcodeInventoryErrorForm) form;
        BarcodeInventoryErrorDocument document = barcodeInventoryErrorForm.getBarcodeInventoryErrorDocument();
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = document.getBarcodeInventoryErrorDetail(); 

        BarcodeInventoryErrorDetailPredicate predicatedClosure = new BarcodeInventoryErrorDetailPredicate(barcodeInventoryErrorForm);
        CollectionUtils.forAllDo(barcodeInventoryErrorDetails, predicatedClosure);

        document.setBarcodeInventoryErrorDetail(barcodeInventoryErrorDetails);
        barcodeInventoryErrorForm.setDocument(document);

        this.save(mapping, form, request, response);        
        this.invokeRules(document);

        barcodeInventoryErrorForm.resetSearchFields();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);        
    }


    /**
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#save(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {        
        KualiDocumentFormBase kForm = (KualiDocumentFormBase)form;

        ActionForward af = super.save(mapping, form, request, response);

        loadDocument(kForm);

        return af;
    }


    /**
     * 
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward validateLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BarcodeInventoryErrorForm barcodeInventoryErrorForm = (BarcodeInventoryErrorForm) form;
        BarcodeInventoryErrorDocument document = barcodeInventoryErrorForm.getBarcodeInventoryErrorDocument();
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = document.getBarcodeInventoryErrorDetail(); 

        //To not validate all the rows
        document.setBarcodeInventoryErrorSelectedRows(false);

        
        //Iterating over the array of checkboxes that hold the number of lines selected
        int selectedCheckboxes[]= barcodeInventoryErrorForm.getRowCheckbox();
        for(int i=0;i<selectedCheckboxes.length;i++) {
            LOG.info("*******XXXXXXX SElected ROw BEFORE:"+selectedCheckboxes[i]);                
            barcodeInventoryErrorDetails.get(selectedCheckboxes[i]-1).setRowSelected(true);
        }

        document.setBarcodeInventoryErrorDetail(barcodeInventoryErrorDetails);
        barcodeInventoryErrorForm.setDocument(document);

        this.save(mapping, form, request, response);
        this.invokeRules(document);

        for(BarcodeInventoryErrorDetail barcodeInventoryErrorDetail:barcodeInventoryErrorDetails) {
            LOG.info("XXXXXXX SelectedROw flag AFTER:"+barcodeInventoryErrorDetail.isRowSelected());
            // if the record 
            if (barcodeInventoryErrorDetail.isRowSelected() && barcodeInventoryErrorDetail.getErrorCorrectionStatusCode().equals(CamsConstants.BarcodeInventoryError.STATUS_CODE_CORRECTED)) {
                assetBarcodeInventoryLoadService.updateAssetInformation(barcodeInventoryErrorDetail);
            }
        }        

        //To validate all rows
        document.setBarcodeInventoryErrorSelectedRows(true);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);                
    }

    
    /**
     * 
     * This method deletes selected lines from the document
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BarcodeInventoryErrorForm barcodeInventoryErrorForm = (BarcodeInventoryErrorForm) form;
        BarcodeInventoryErrorDocument document = barcodeInventoryErrorForm.getBarcodeInventoryErrorDocument();
        List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails = document.getBarcodeInventoryErrorDetail(); 
        List<BarcodeInventoryErrorDetail> tmpBarcodeInventoryErrorDetails = new ArrayList<BarcodeInventoryErrorDetail>();
        BarcodeInventoryErrorDetail barcodeInventoryErrorDetail;

//      for(BarcodeInventoryErrorDetail detail:barcodeInventoryErrorDetails) {
//            LOG.info("*** Data Before deleting :"+detail.toString());
//        }        

      
      int selectedCheckboxes[]= barcodeInventoryErrorForm.getRowCheckbox();
      for(int i=0;i<selectedCheckboxes.length;i++) {
          LOG.info("*******XXXXXXX SElected ROw BEFORE:"+selectedCheckboxes[i]);                
          businessObjectService.delete(barcodeInventoryErrorDetails.get(selectedCheckboxes[i]-1));
      }

      this.loadDocument((KualiDocumentFormBase)form);
      
        //Iterating over the array of checkboxes that hold the number of lines selected
//        int selectedCheckboxes[]= barcodeInventoryErrorForm.getRowCheckbox();
//        for(int i=0;i<selectedCheckboxes.length;i++) {
//            for (Iterator<BarcodeInventoryErrorDetail> it = barcodeInventoryErrorDetails.iterator();it.hasNext();) {
//                barcodeInventoryErrorDetail = (BarcodeInventoryErrorDetail)it.next();
//
//                //if no error found, then update asset table and delete the element from the collection
//                if (barcodeInventoryErrorDetail.getUploadRowNumber().compareTo(new Long(selectedCheckboxes[i])) == 0){
//                    LOG.info("***XXXXXXX Deleing row:"+barcodeInventoryErrorDetail.getUploadRowNumber());
//
//                    it.remove();
//                    break;
//                }
//            }
//        }
//
//        
//        for(BarcodeInventoryErrorDetail detail:barcodeInventoryErrorDetails) {
//            LOG.info("*** Data after deleting:"+detail.toString());
//        }        

        //Reorganizing the order of each line
//        Long i=new Long(0);
//        for(BarcodeInventoryErrorDetail detail:barcodeInventoryErrorDetails) {
//            i++;
//            detail.setUploadRowNumber(i);
//            tmpBarcodeInventoryErrorDetails.add(detail);
//        }
//        //Reassigning the changes to the document.
//        document.setBarcodeInventoryErrorDetail(tmpBarcodeInventoryErrorDetails);

        
//        document.setBarcodeInventoryErrorDetail(barcodeInventoryErrorDetails);
//        barcodeInventoryErrorForm.setDocument(document);
//        this.invokeRules(document);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * 
     * This method...
     * @param document
     */
    private void invokeRules(BarcodeInventoryErrorDocument document) {
        kualiRuleService.applyRules(new ValidateBarcodeInventoryEvent("", document));
    }

}
