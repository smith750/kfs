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
package org.kuali.kfs.module.ar.report.service.impl;

import java.util.List;

import org.kuali.kfs.module.ar.businessobject.TicklersReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.TicklersReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;

/**
 * Implementation of ContractsGrantsReportDataBuilderService for the Ticklers Report
 */
public class TicklersReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService<TicklersReport> {
    /**
     * Builds the report
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<TicklersReport> displayList, String sortPropertyName) {
        ContractsGrantsReportDataHolder arTicklersReportDataHolder = new ContractsGrantsReportDataHolder();
        List<TicklersReportDetailDataHolder> ticklersReportDetails = arTicklersReportDataHolder.getDetails();
        for (TicklersReport tr : displayList) {
            TicklersReportDetailDataHolder trDataHolder = new TicklersReportDetailDataHolder(tr);
            ticklersReportDetails.add(trDataHolder);
        }
        return arTicklersReportDataHolder;
    }

    /**
     * Returns the class of TicklersReport
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#getDetailsClass()
     */
    @Override
    public Class<TicklersReport> getDetailsClass() {
        return TicklersReport.class;
    }
}