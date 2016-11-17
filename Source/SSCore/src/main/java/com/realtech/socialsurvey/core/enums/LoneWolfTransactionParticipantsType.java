package com.realtech.socialsurvey.core.enums;

public enum LoneWolfTransactionParticipantsType
{
    SELLER( "S" ), BUYER( "B" ), SELLERBUYERBOTH( "SB" ), UNKNOWN( "U" ), NONE( "N" ) ;

    private final String participantType;


    LoneWolfTransactionParticipantsType( String participantType )
    {
        this.participantType = participantType;
    }


    public String getParticipantsType()
    {
        return participantType;
    }

}
