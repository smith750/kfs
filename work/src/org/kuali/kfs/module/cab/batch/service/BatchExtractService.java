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
package org.kuali.kfs.module.cab.batch.service;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.batch.ExtractProcessLog;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;

/**
 * Declares the service methods used by CAB batch program
 */
public interface BatchExtractService {

    /**
     * Returns the list of CAB eligible GL entries, filter parameters are pre-configured
     * 
     * @return Eligible GL Entries meeting batch parameters configured under parameter group KFS-CAB:Batch
     */
    Collection<Entry> findElgibleGLEntries();

    /**
     * Returns the list of Purchasing GL transactions that occurred after last GL process
     * 
     * @return List of pending gl transactions waiting to be posted to GL
     */
    Collection<GeneralLedgerPendingEntry> findPurapPendingGLEntries();

    /**
     * Saves financial transaction lines which dont have Purchase Order number associated with it
     * 
     * @param fpLines Financial transaction lines
     * @param processLog Process Log
     */
    void saveFPLines(List<Entry> fpLines, ExtractProcessLog processLog);

    /**
     * Saved purchasing line transactions, this method implementation internally uses
     * {@link org.kuali.kfs.gl.batch.service.ReconciliationService} to qa the data before saving
     * 
     * @param poLines Eligible GL Lines
     * @param processLog Process Log
     */
    void savePOLines(List<Entry> poLines, ExtractProcessLog processLog);

    /**
     * Separates out transaction lines associated with purchase order from the rest
     * 
     * @param fpLines Non-purchasing lines
     * @param purapLines Purchasing lines
     * @param elgibleGLEntries Full list of eligible GL entries
     */
    void separatePOLines(List<Entry> fpLines, List<Entry> purapLines, Collection<Entry> elgibleGLEntries);

    /**
     * Updates the last extract time stamp system parameter, usually done when a batch process is finished successfully.
     * 
     * @param time Last extract start time
     */
    void updateLastExtractTime(Timestamp time);
}
