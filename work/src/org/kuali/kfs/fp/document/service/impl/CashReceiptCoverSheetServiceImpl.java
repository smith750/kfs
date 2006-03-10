/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.financial.service.impl;

import java.io.InputStream;
import java.io.IOException;

import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.service.CashReceiptCoverSheetService;

import com.lowagie.text.DocumentException;

/**
 * Implementation of service for handling creation of the cover sheet of the 
 * <code>{@link CashReceiptDocument}</code>
 * 
 * @author Leo Przybylski
 */
public class CashReceiptCoverSheetServiceImpl 
    implements CashReceiptCoverSheetService {
    
    /**
     * Generate a cover sheet for the <code>{@link CashReceiptDocument}</code>.
     * An <code>{@link InputStream}</code> is returned to handle the
     * coversheet.
     * 
     * @return document
     * @return InputStream
     * @exception DocumentException
     * @exception IOException
     * @see org.kuali.core.module.financial.service.CashReceiptCoverSheetServiceImpl#generateCoverSheet( org.kuali.module.financial.documentCashReceiptDocument )
     */
    public InputStream generateCoverSheet( CashReceiptDocument document ) 
        throws DocumentException, IOException {
        InputStream retval = null;
        
        return retval;
    }
}
