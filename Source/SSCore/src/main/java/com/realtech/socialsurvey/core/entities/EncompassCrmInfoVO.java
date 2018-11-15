package com.realtech.socialsurvey.core.entities;

import java.io.Serializable;


public class EncompassCrmInfoVO implements Serializable
{
    private static final long serialVersionUID = 1L;

        private String crm_username;
        private String crm_password;
        private String url;
        private String crm_fieldId;
        private String state;
        private int numberOfDays;
        private String emailAddressForReport;
        private boolean generateReport;
        private String version;

        private String loanOfficerEmail;
        private String loanOfficerName;

        private String buyerAgentEmail;
        private String buyerAgentName;
        private String sellerAgentEmail;
        private String sellerAgentName;

        private String propertyAddress;
        private String loanProcessorName;
        private String loanProcessorEmail;
        private String customFieldOne;
        private String customFieldTwo;
        private String customFieldThree;
        private String customFieldFour;
        private String customFieldFive;

        private boolean allowPartnerSurvey;

        public String getCrm_username() {
            return crm_username;
        }
        public void setCrm_username(String crm_username) {
            this.crm_username = crm_username;
        }
        public String getCrm_password() {
            return crm_password;
        }
        public void setCrm_password(String crm_password) {
            this.crm_password = crm_password;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
        public String getCrm_fieldId() {
            return crm_fieldId;
        }
        public void setCrm_fieldId(String crm_fieldId) {
            this.crm_fieldId = crm_fieldId;
        }
        public String getState() {
            return state;
        }
        public void setState(String state) {
            this.state = state;
        }
        public int getNumberOfDays() {
            return numberOfDays;
        }
        public void setNumberOfDays(int numberOfDays) {
            this.numberOfDays = numberOfDays;
        }
        public String getEmailAddressForReport() {
            return emailAddressForReport;
        }
        public void setEmailAddressForReport(String emailAddressForReport) {
            this.emailAddressForReport = emailAddressForReport;
        }
        public boolean isGenerateReport() {
            return generateReport;
        }
        public void setGenerateReport(boolean generateReport) {
            this.generateReport = generateReport;
        }
        public String getVersion() {
            return version;
        }
        public void setVersion(String version) {
            this.version = version;
        }
        public String getLoanOfficerEmail() {
            return loanOfficerEmail;
        }
        public void setLoanOfficerEmail(String loanOfficerEmail) {
            this.loanOfficerEmail = loanOfficerEmail;
        }
        public String getLoanOfficerName() {
            return loanOfficerName;
        }
        public void setLoanOfficerName(String loanOfficerName) {
            this.loanOfficerName = loanOfficerName;
        }
        public String getBuyerAgentEmail() {
            return buyerAgentEmail;
        }
        public void setBuyerAgentEmail(String buyerAgentEmail) {
            this.buyerAgentEmail = buyerAgentEmail;
        }
        public String getBuyerAgentName() {
            return buyerAgentName;
        }
        public void setBuyerAgentName(String buyerAgentName) {
            this.buyerAgentName = buyerAgentName;
        }
        public String getSellerAgentEmail() {
            return sellerAgentEmail;
        }
        public void setSellerAgentEmail(String sellerAgentEmail) {
            this.sellerAgentEmail = sellerAgentEmail;
        }
        public String getSellerAgentName() {
            return sellerAgentName;
        }
        public void setSellerAgentName(String sellerAgentName) {
            this.sellerAgentName = sellerAgentName;
        }
        public String getPropertyAddress() {
            return propertyAddress;
        }
        public void setPropertyAddress(String propertyAddress) {
            this.propertyAddress = propertyAddress;
        }
        public String getLoanProcessorName() {
            return loanProcessorName;
        }
        public void setLoanProcessorName(String loanProcessorName) {
            this.loanProcessorName = loanProcessorName;
        }
        public String getLoanProcessorEmail() {
            return loanProcessorEmail;
        }
        public void setLoanProcessorEmail(String loanProcessorEmail) {
            this.loanProcessorEmail = loanProcessorEmail;
        }
        public String getCustomFieldOne() {
            return customFieldOne;
        }
        public void setCustomFieldOne(String customFieldOne) {
            this.customFieldOne = customFieldOne;
        }
        public String getCustomFieldTwo() {
            return customFieldTwo;
        }
        public void setCustomFieldTwo(String customFieldTwo) {
            this.customFieldTwo = customFieldTwo;
        }
        public String getCustomFieldThree() {
            return customFieldThree;
        }
        public void setCustomFieldThree(String customFieldThree) {
            this.customFieldThree = customFieldThree;
        }
        public String getCustomFieldFour() {
            return customFieldFour;
        }
        public void setCustomFieldFour(String customFieldFour) {
            this.customFieldFour = customFieldFour;
        }
        public String getCustomFieldFive() {
            return customFieldFive;
        }
        public void setCustomFieldFive(String customFieldFive) {
            this.customFieldFive = customFieldFive;
        }
        public boolean isAllowPartnerSurvey() {
            return allowPartnerSurvey;
        }
        public void setAllowPartnerSurvey(boolean allowPartnerSurvey) {
            this.allowPartnerSurvey = allowPartnerSurvey;
        }

        public static EncompassCrmInfoVO encompassCrmInfoVOMapper( EncompassCrmInfo encompassCrmInfo,
            boolean allowPartnerSurvey ){
            EncompassCrmInfoVO encompassCrmInfoVO = new EncompassCrmInfoVO();
            encompassCrmInfoVO.setCrm_username( encompassCrmInfo.getCrm_username() );
            encompassCrmInfoVO.setCrm_password( encompassCrmInfo.getCrm_password() );
            encompassCrmInfoVO.setUrl( encompassCrmInfo.getUrl() );
            encompassCrmInfoVO.setCrm_fieldId(encompassCrmInfo.getCrm_fieldId()  );
            encompassCrmInfoVO.setState( encompassCrmInfo.getState() );
            encompassCrmInfoVO.setNumberOfDays( encompassCrmInfo.getNumberOfDays() );
            encompassCrmInfoVO.setEmailAddressForReport( encompassCrmInfo.getEmailAddressForReport() );
            encompassCrmInfoVO.setGenerateReport( encompassCrmInfo.isGenerateReport() );
            encompassCrmInfoVO.setVersion( encompassCrmInfo.getVersion() );
            encompassCrmInfoVO.setLoanOfficerEmail( encompassCrmInfo.getLoanOfficerEmail() );
            encompassCrmInfoVO.setLoanOfficerName( encompassCrmInfo.getLoanOfficerName() );
            encompassCrmInfoVO.setBuyerAgentEmail( encompassCrmInfo.getBuyerAgentEmail() );
            encompassCrmInfoVO.setBuyerAgentName( encompassCrmInfo.getBuyerAgentName() );
            encompassCrmInfoVO.setSellerAgentEmail( encompassCrmInfo.getSellerAgentEmail() );
            encompassCrmInfoVO.setSellerAgentName( encompassCrmInfo.getBuyerAgentName() );
            encompassCrmInfoVO.setPropertyAddress( encompassCrmInfo.getPropertyAddress() );
            encompassCrmInfoVO.setLoanProcessorEmail( encompassCrmInfo.getLoanProcessorEmail() );
            encompassCrmInfoVO.setLoanProcessorName( encompassCrmInfo.getLoanProcessorName() );
            encompassCrmInfoVO.setCustomFieldOne( encompassCrmInfo.getCustomFieldOne() );
            encompassCrmInfoVO.setCustomFieldTwo( encompassCrmInfo.getCustomFieldTwo() );
            encompassCrmInfoVO.setCustomFieldThree( encompassCrmInfo.getCustomFieldThree() );
            encompassCrmInfoVO.setCustomFieldFour( encompassCrmInfo.getCustomFieldFour() );
            encompassCrmInfoVO.setCustomFieldFive( encompassCrmInfo.getCustomFieldFive() );
            encompassCrmInfoVO.setAllowPartnerSurvey( allowPartnerSurvey );
            return encompassCrmInfoVO;
        }
}
