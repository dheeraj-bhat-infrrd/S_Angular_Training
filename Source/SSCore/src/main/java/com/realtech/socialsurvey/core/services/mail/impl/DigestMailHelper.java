package com.realtech.socialsurvey.core.services.mail.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.EmailTemplateConstants;
import com.realtech.socialsurvey.core.entities.MonthlyDigestAggregate;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.utils.FileOperations;


@Component
public class DigestMailHelper
{

    public static final Logger LOG = LoggerFactory.getLogger( DigestMailHelper.class );

    @Value ( "${APPLICATION_NEW_LOGO_URL}")
    private String appNewLogoUrl;


    @Value ( "${APPLICATION_WORD_PRESS_SITE_URL}")
    private String applicationWordPressSite;

    @Autowired
    private FileOperations fileOperations;


    public String getDigestMailIncreasedIndicatorIcon()
    {
        return "<div style=\"color: darkgreen; font-size: 16px; line-height: 30px;\">&#9650;</div>";
    }


    public String getDigestMailDroppedIndicatorIcon()
    {
        return "<div style=\"color: darkred; font-size: 16px; line-height: 30px;\">&#9660;</div>";
    }


    public String getDigestMailNoChangeIndicatorIcon()
    {
        return "<div style=\"color: grey; font-size: 30px; line-height: 30px;\">&bull;</div>";
    }


    public String getDigestMailNotAvailableIndicatorIcon()
    {
        return "<div style=\"color: grey; font-size: 30px; line-height: 30px;\"><b>--</b></div>";
    }


    public String getBoldTextForHtml( String text )
    {
        return "<b>" + text + "</b>";
    }


    public String getDigestMailSectionPaddingWithSeperator()
    {
        return "<tr><td height=\"60\" style=\"font-size:0;line-height:0;border-bottom: 2px solid grey;\"></td></tr>";
    }


    public String getDigestMailIntraSectionPadding()
    {
        return "<tr><td height=\"40\" style=\"font-size:0;line-height:0;\"></td></tr>";
    }


    public String getDigestMailInterLinePadding()
    {
        return "<tr><td height=\"20\" style=\"font-size:0;line-height:0;\"></td></tr>";
    }


    public String WrapDigestMailSection( String sectionData )
    {
        return "<tr><td><table width=\"100%\" bgcolor=\"#FFFFFF\" cellspacing=\"0\" cellpadding=\"0\"><tbody>" + sectionData
            + "</tbody></table></td></tr>";
    }


    public String wrapDigestMailSectionTitle( String sectionTitle )
    {
        return "<tr><td class=\"font-heading-bold\" align=\"center\" valign=\"middle\" style=\"color: #333; font-size: 20px; line-height: 22px; font-family: Helvetica, Arial, sans-serif;\">"
            + sectionTitle + "</td></tr>";
    }


    public String wrapDigestMailSectionDescription( String sectionDescription )
    {
        return "<tr><td align=\"center\"><table class=\"content-wrap wrap-240\" width=\"100%\" bgcolor=\"#FFFFFF\" cellspacing=\"0\" cellpadding=\"0\"><tbody><tr><td class=\"font-light\" align=\"center\" style=\"color: #666; font-size: 16px; line-height: 20px; font-family: Helvetica, Arial, sans-serif;\">"
            + sectionDescription + "</td></tr></tbody></table></td></tr>";
    }


    public String wrapDigestMailSectionInference( String sectionInference )
    {
        return "<tr><td align=\"center\"><table class=\"content-wrap\" width=\"420\" cellspacing=\"0\" cellpadding=\"0\"><tbody><tr><td class=\"font-light\" align=\"center\" style=\"color: #555;font-size: 16px;line-height: 20px;font-family: Helvetica, Arial, sans-serif;\">"
            + sectionInference + "</td></tr></tbody></table></td></tr>";
    }


    public String wrapUserRankingTable( String DigestUserRankingData )
    {
        return "<tr><td height=\"40\" style=\"font-size:0;line-height:0;\"><table cellspacing=\"0\" style=\"margin:0;padding:0;border:0;font:inherit;font-size:100%;vertical-align:baseline;border-collapse:collapse;border-spacing:0;font-size:1rem;line-height:1.28571rem;margin-bottom:1.28571rem;color:#555;border-collapse: separate; font-size: 12px; width: 100%;\">"
            + DigestUserRankingData + "</table></td></tr>";
    }


    public String wrapUserRankingTableHeadOrBody( String tableHeadOrBody, boolean isHead )
    {
        if ( isHead ) {
            return "<thead style=\"margin:0;padding:0;border:0;font:inherit;font-size:100%;vertical-align:baseline\">"
                + tableHeadOrBody + "</thead>";
        } else {
            return "<tbody style=\"margin:0;padding:0;border:0;font:inherit;font-size:100%;vertical-align:baseline\">"
                + tableHeadOrBody + "</tbody>";
        }
    }


    public String wrapUserRankingTableRow( String DigestUserRankingRow, boolean isHead, boolean isRowNumberEven )
    {
        if ( isHead ) {
            return "<tr style=\"margin:0;padding:0;border:0;font:inherit;font-size:100%;vertical-align:baseline;background-color:#f1f1f1\">"
                + DigestUserRankingRow + "</tr>";
        } else {
            if ( isRowNumberEven ) {
                return "<tr style=\"margin:0;padding:0;border:0;font:inherit;font-size:100%;vertical-align:baseline;background-color:#f8f8f8\">"
                    + DigestUserRankingRow + "</tr>";
            } else {
                return "<tr style=\"margin:0;padding:0;border:0;font:inherit;font-size:100%;vertical-align:baseline;\">"
                    + DigestUserRankingRow + "</tr>";
            }
        }
    }


    public String wrapUserRankingTableCell( String tableCellHeadOrBody, boolean isHead )
    {
        if ( isHead ) {
            return "<th style=\"margin:0;padding:0;border:0;font:inherit;font-size:100%;vertical-align:baseline;font-weight:normal;text-align:left;vertical-align:middle;font-size:1rem;line-height:1.28571rem;margin-bottom:1.28571rem;border-bottom: 1px dotted#ccc; font-weight:700;padding:4.5px .5em;padding: 4px;border-top: 2px solid #2f69aa;border-bottom:1px solid #d2d2d2;\">"
                + tableCellHeadOrBody + "</th>";
        } else {
            return "<td style=\"margin:0;padding:0;border:0;font:inherit;font-size:100%;vertical-align:baseline;font-weight:normal;text-align:left;vertical-align:middle;font-size:1rem;line-height:1.28571rem;margin-bottom:1.28571rem;border-bottom: 1px dotted #ccc; padding:4.5px .5em;padding: 4px;\">"
                + tableCellHeadOrBody + "</td>";
        }
    }


    public String wrapDigestMailSectionComparison( String sectionComparisonHtml )
    {
        return "<tr><td><table width=\"100%\" bgcolor=\"#FFFFFF\" cellspacing=\"0\" cellpadding=\"0\"><tbody><tr>"
            + sectionComparisonHtml + "</tr></tbody></table></td></tr>";
    }


    public String wrapDigestMailSectionComparisonElement( String comparisonHtml, String intraAlignment )
    {
        return "<td class=\"column\" width=\"33%\" align=\"" + intraAlignment
            + "\"><table width=\"160\" bgcolor=\"#FFFFFF\" cellspacing=\"0\" cellpadding=\"0\"><tbody>" + comparisonHtml
            + "</tbody></table></td>";
    }


    public String getSectionComparisonElementInterPadding()
    {
        return "<tr><td class=\"top-20\" height=\"0\" style=\"font-size:0;line-height:0;\"></td></tr>";
    }


    public String wrapDigestMailSectionComparisonElementMonth( String month )
    {
        return "<tr><td align=\"center\" style=\"color: #999; font-size: 14px; font-family: Helvetica, Arial, sans-serif;\">"
            + month + "</td></tr>";
    }


    public String getSectionComparisonElementIntraPadding()
    {
        return "<tr><td height=\"15\" style=\"font-size:0;line-height:0;\"></td></tr>";
    }


    public String wrapDigestMailSectionComparisonElementCount( String count )
    {
        return "<tr><td class=\"font-light\" align=\"center\" style=\"color: #333; font-size: 18px; line-height: 30px; font-family: Helvetica, Arial,sans-serif; white-space: nowrap;\">"
            + count + "</td></tr>";
    }


    public String wrapDigestMailTextWithSpan( String text, int fontInPx )
    {
        return "<span style=\"font-size: " + fontInPx + "px;\">" + text + "</span>";
    }


    public String wrapDigestMailSectionComparisonElementStats( String statsHtml )
    {
        return "<tr><td class=\"font-light\" align=\"center\" style=\"color: #333; font-size: 18px; line-height: 30px; font-family: Helvetica, Arial,sans-serif; white-space: nowrap;\">"
            + statsHtml + "</td></tr>";
    }


    public String wrapDigestMailPromoterCountValue( String value )
    {
        return "<span style=\"color: darkgreen;\">" + value + "</span>";
    }


    public String wrapDigestMailDetractorCountValue( String value )
    {
        return "<span style=\"color: darkred;\">" + value + "</span>";
    }


    public String wrapDigestMailSectionComparisonElementValue( String value, int lineHeightInPx )
    {
        return "<tr><td class=\"font-light\" align=\"center\" style=\"color: #333; font-size: 36px; line-height: "
            + lineHeightInPx + "px; font-family: Helvetica, Arial,sans-serif; white-space: nowrap;\">" + value + "</td></tr>";
    }


    public String getEmailDisclaimer( String email )
    {
        return "<center>" + ""
            + "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" id=\"canspamBarWrapper\" style=\"background-color:#FFFFFF;\">\n"
            + "<tbody>\n" + "<tr>\n" + "<td align=\"center\" valign=\"top\" style=\"padding-top:20px; padding-bottom:20px;\">\n"
            + "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" id=\"canspamBar\">\n" + "<tbody>\n" + "<tr>\n"
            + "<td align=\"center\" valign=\"top\" style=\"color:#606060; font-family:Helvetica, Arial, sans-serif; font-size:11px; line-height:150%; padding-right:20px; padding-bottom:5px; padding-left:20px; text-align:center;\">\n"
            + "<p>This email was sent to <a href=\"mailto:" + email
            + "\" style=\"color:#989ea6;font-weight:normal;text-decoration:underline;word-break:break-word;\" target=\"_blank\">"
            + email
            + "</a>. You received this email because you have a SocialSurvey account. If you feel this was sent to you in error please contact <a href=\"mailto:support@socialsurvey.com\" style=\"color:#989ea6;font-weight:normal;text-decoration:underline;word-break:break-word;\" target=\"_blank\">support@socialsurvey.com</a>. <br>\n"
            + "<br>© Copyright " + Calendar.getInstance().get( Calendar.YEAR )
            + " SocialSurvey. Created by BuyersRoad, Inc. All Rights Reserved. 3000 Executive Pkwy, Suite 315, San Ramon, CA 94583. <a href=\"http://socialsurvey.com/privacy-policy\" style=\"color:#989ea6 !important;font-weight:bold;text-decoration:none;word-break:break-word;\" target=\"_blank\">Privacy Policy</a>\n"
            + "</p>\n" + "</td>\n" + "</tr>\n" + "</tbody>\n" + "</table>\n" + "</td>\n" + "</tr>\n" + "</tbody>\n"
            + "</table>\n" + "</center>";
    }


    public String buildDigestMailReplacents( MonthlyDigestAggregate digestAggregate,
        List<String> messageSubjectReplacementsList, List<String> messageBodyReplacementsList, String digestRecipient,
        boolean justSaveCopy ) throws InvalidInputException
    {

        LOG.debug( "method populateDigestMail started" );
        if ( digestAggregate == null ) {
            LOG.warn( "digest data is not specified, Unable to construct digest Mail" );
            throw new InvalidInputException( "digest data is not specified, Unable to construct digest Mail" );
        }


        if ( !justSaveCopy && StringUtils.isEmpty( digestRecipient ) ) {
            LOG.warn( "digest recipient is not specified, Unable to construct digest Mail" );
            throw new InvalidInputException( "digest recipient is not specified, Unable to construct digest Mail" );
        } else if ( !justSaveCopy && ( messageBodyReplacementsList == null || messageSubjectReplacementsList == null ) ) {
            LOG.warn( "target digest dataHolders not specified, Unable to construct digest Mail" );
            throw new InvalidInputException( "target digest dataHolders not specified, Unable to construct digest Mail" );
        }


        // create list object to hold digest values when saving digest instead of sending mail
        if ( messageBodyReplacementsList == null ) {
            if ( justSaveCopy ) {
                messageBodyReplacementsList = new ArrayList<>();
            } else {

            }
        }


        String monthYearForDisplay = StringUtils.capitalize( digestAggregate.getMonthUnderConcern() ) + " "
            + digestAggregate.getYearUnderConcern();


        // set up digest mail subject for sending mail
        if ( !justSaveCopy ) {
            messageSubjectReplacementsList.add( monthYearForDisplay );
        }


        // set up digest mail body
        messageBodyReplacementsList.add( applicationWordPressSite );
        messageBodyReplacementsList.add( appNewLogoUrl );
        messageBodyReplacementsList.add( digestAggregate.getEntityName() );
        messageBodyReplacementsList.add( monthYearForDisplay );

        // adding average rating score data
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getAverageScoreRatingIcon() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getAverageScoreRating() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getUserCount() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getMonth() ) ) );

        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getAverageScoreRatingIcon() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getAverageScoreRating() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getUserCount() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getMonth() ) ) );

        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getAverageScoreRatingIcon() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getAverageScoreRating() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getUserCount() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getMonth() ) ) );

        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getAvgRatingTxt() ) );


        // adding survey completion rate data
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getSurveyCompletionRateIcon() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getSurveyCompletionRate() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getCompletedTransactions() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getTotalTransactions() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getMonth() ) ) );

        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getSurveyCompletionRateIcon() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getSurveyCompletionRate() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getCompletedTransactions() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getTotalTransactions() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getMonth() ) ) );

        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getSurveyCompletionRateIcon() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getSurveyCompletionRate() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getCompletedTransactions() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getTotalTransactions() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getMonth() ) ) );

        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getSurveyPercentageTxt() ) );


        // adding satisfaction rating data
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getSpsIcon() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getSps() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getPromoters() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getDetractors() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getCompletedTransactions() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 2 ).getMonth() ) ) );

        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getSpsIcon() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getSps() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getPromoters() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getDetractors() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getCompletedTransactions() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 1 ).getMonth() ) ) );

        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getSpsIcon() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getSps() ) );
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getPromoters() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getDetractors() ) );
        messageBodyReplacementsList
            .add( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getCompletedTransactions() ) );
        messageBodyReplacementsList
            .add( StringUtils.upperCase( StringUtils.defaultString( digestAggregate.getDigestList().get( 0 ).getMonth() ) ) );

        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getStatisfactionRatingTxt() ) );


        // NPS rating section
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getNpsHtmlsection() ) );

        // top ten ranked users HTML
        messageBodyReplacementsList.add( StringUtils.defaultString( digestAggregate.getUserRankingHtmlSection() ) );

        // newsletter footer
        if( justSaveCopy ) {
            messageBodyReplacementsList.add( getDefaultFooter() );
        } else {
            messageBodyReplacementsList.add( getNewsLetterFooter() );
        }

        // generate digest email disclaimer when needed
        if ( !justSaveCopy && StringUtils.isNotEmpty( digestRecipient ) ) {
            messageBodyReplacementsList.add( getEmailDisclaimer( digestRecipient ) );
        } else {
            messageBodyReplacementsList.add( "" );
        }

        if ( justSaveCopy ) {
            return populateDigestMail( messageBodyReplacementsList );
        } else {
            return null;
        }

    }


    public void validateDigestAggregate( MonthlyDigestAggregate digestAggregate, boolean isForSavingCopy ) throws InvalidInputException
    {
        if ( digestAggregate == null ) {
            EmailServicesImpl.LOG.error( "Digest Aggregate object is null" );
            throw new InvalidInputException( "Data for the monthly digest/snapshot mail is missing." );
        }

        if ( digestAggregate.getEntityId() < 1 ) {
            EmailServicesImpl.LOG.error( "Entity ID must be greater that one for Monthly digest/snapshot mail " );
            throw new InvalidInputException( "Entity ID cannot be less than one for Monthly digest/snapshot mail." );
        }

        if ( !isForSavingCopy
            && ( digestAggregate.getRecipientMailIds() == null || digestAggregate.getRecipientMailIds().isEmpty() ) ) {
            EmailServicesImpl.LOG.error( "Recipient email Id list is empty or null for Monthly digest/snapshot mail " );
            throw new InvalidInputException( "Recipient email Id list is empty or null for Monthly digest/snapshot mail." );
        }

        if ( digestAggregate.getDigestList() == null || digestAggregate.getDigestList().size() != 3 ) {
            EmailServicesImpl.LOG.error( "Digest data for three months required." );
            throw new InvalidInputException( "Digest data for three months required." );
        }

        if ( StringUtils.isEmpty( digestAggregate.getEntityName() ) ) {
            EmailServicesImpl.LOG.error( "Entity name for the digest not specified." );
            throw new InvalidInputException( "Entity name for the digest not specified." );
        }

        if ( StringUtils.isEmpty( digestAggregate.getMonthUnderConcern() ) ) {
            EmailServicesImpl.LOG.error( "Month for the digest not specified." );
            throw new InvalidInputException( "Month for the digest not specified." );
        }

        if ( StringUtils.isEmpty( digestAggregate.getYearUnderConcern() ) ) {
            EmailServicesImpl.LOG.error( "Year for the digest not specified." );
            throw new InvalidInputException( "Year for the digest not specified." );
        }

    }


    private String populateDigestMail( List<String> replacements ) throws InvalidInputException
    {
        String digest = fileOperations
            .getContentFromFile( EmailTemplateConstants.EMAIL_TEMPLATES_FOLDER + EmailTemplateConstants.DIGEST_MAIL_BODY );
        for ( String replacement : replacements ) {
            digest = digest.replaceFirst( "%s", Matcher.quoteReplacement( replacement ) );
        }
        return digest;
    }
    
    private String getNewsLetterFooter() {
        return "<table width=\"100%\" bgcolor=\"#FFFFFF\" cellspacing=\"0\" cellpadding=\"0\"><tbody>" + 
            "<tr> <td height=\"40\" style=\"font-size:0;line-height:0;\"></td></tr>" + 
            "<tr> <td height=\"40\" style=\"text-align:  center;font-size: 16px;\">" + 
            "<span>To stay updated with latest happenings at SocialSurvey, checkout our Wow newsletter <a href=\"https://www.socialsurvey.com/newsletter\">https://www.socialsurvey.com/newsletter</a><a></a></span></td></tr>" + 
            "<tr> <td height=\"40\" style=\"font-size:0;line-height:0;\"></td></tr></tbody></table>";
    }
    
    private String getDefaultFooter() {
        return "<td height=\"80\" style=\"font-size:0;line-height:0;\"></td>";
    }

}
