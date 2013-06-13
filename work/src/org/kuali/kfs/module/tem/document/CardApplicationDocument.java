/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document;

import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.rice.krad.bo.DocumentHeader;


public interface CardApplicationDocument {

    public abstract TEMProfile getTemProfile();

    public abstract void setTemProfile(TEMProfile temProfile);

    public abstract Integer getTemProfileId();

    public abstract void setTemProfileId(Integer temProfileId);

    public abstract boolean isUserAgreement();

    public abstract void setUserAgreement(boolean userAgreement);
    
    public abstract String getUserAgreementText();

    public DocumentHeader getDocumentHeader();
    
    public abstract void applyToBank();
    
    public abstract void approvedByBank();
    
    public abstract void sendAcknowledgement();
    
    public abstract boolean saveAppDocStatus();
}