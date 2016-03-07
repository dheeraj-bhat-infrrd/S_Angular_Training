package com.realtech.socialsurvey.core.services.admin;

import java.util.List;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.vo.SubscriptionVO;
import com.realtech.socialsurvey.core.vo.TransactionVO;

public interface AdminService
{
    public SubscriptionVO getSubscriptionVOBySubscriptionId(String subscriptionId) throws InvalidInputException;

    List<TransactionVO> getTransactionListBySubscriptionIs( String subscriptionId ) throws InvalidInputException;

    List<SubscriptionVO> getActiveSubscriptionsList() throws InvalidInputException;

    List<Company> getAllAutoBillingModeCompanies();

    boolean generateTransactionListExcelAndMail( List<TransactionVO> transactionVOs, List<String> recipientMailIds , String subscriptionId );

    boolean generateSubscriptionListExcelAndMail( List<SubscriptionVO> subscriptionVOs, List<String> recipientMailIds );

}
