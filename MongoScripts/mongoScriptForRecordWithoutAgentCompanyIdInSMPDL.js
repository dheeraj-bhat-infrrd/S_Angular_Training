db.getCollection('SURVEY_DETAILS').find({
    stage: -1
}).snapshot().forEach(function(e) {

    //get social media post detail
    var socialMediaPostDetails;
    if (e.socialMediaPostDetails != undefined && e.socialMediaPostDetails != null) {
        socialMediaPostDetails = e.socialMediaPostDetails;
        //get agent media post detail
        var agentMediaPostDetails = socialMediaPostDetails.agentMediaPostDetails;
        if (agentMediaPostDetails != undefined && agentMediaPostDetails != null) {
            var agentId = agentMediaPostDetails.agentId;
            if (agentId == undefined || agentId == null) {
                agentMediaPostDetails.agentId = e.agentId;
            }
        } else {
            agentMediaPostDetails = {};
            agentMediaPostDetails.agentId = e.agentId;
            agentMediaPostDetails.sharedOn = [];
        }
        socialMediaPostDetails.agentMediaPostDetails = agentMediaPostDetails;

        //get company medial post detail
        var companyMediaPostDetails = socialMediaPostDetails.companyMediaPostDetails;
        if (companyMediaPostDetails != undefined && companyMediaPostDetails != null) {
            var companyId = companyMediaPostDetails.companyId;
            if (companyId == undefined || companyId == null) {
                companyMediaPostDetails.companyId = e.companyId;
            }
        } else {
            companyMediaPostDetails = {};
            companyMediaPostDetails.companyId = e.companyId;
            companyMediaPostDetails.sharedOn = [];
        }
        socialMediaPostDetails.companyMediaPostDetails = companyMediaPostDetails;

        e.socialMediaPostDetails = socialMediaPostDetails;
        db.getCollection('SURVEY_DETAILS').save(e);
    }
})
