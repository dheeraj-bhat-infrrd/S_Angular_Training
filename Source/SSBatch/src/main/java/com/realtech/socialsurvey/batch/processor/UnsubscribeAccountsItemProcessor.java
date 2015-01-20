package com.realtech.socialsurvey.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import com.realtech.socialsurvey.core.entities.DisabledAccount;

public class UnsubscribeAccountsItemProcessor implements ItemProcessor<DisabledAccount, DisabledAccount>{

	@Override
	public DisabledAccount process(DisabledAccount item) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
