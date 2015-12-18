db.getCollection('SURVEY_DETAILS').find({
    "createdOn": {
        "$lte": ISODate("2015-12-10T00:00:00.000Z")
    }
}).snapshot().forEach(function(e) {

    //get remindersForSocialPosts
    var remindersForSocialPosts = e.remindersForSocialPosts;
    if (remindersForSocialPosts == undefined || remindersForSocialPosts == null || remindersForSocialPosts.length <= 0) {
        remindersForSocialPosts = [];
        remindersForSocialPosts.push(new ISODate("2015-12-17T06:00:02.018Z"));
        e.remindersForSocialPosts = remindersForSocialPosts;
	//update last reminder for social post
	e.lastReminderForSocialPost = new ISODate("2015-12-17T06:00:02.018Z");
        db.getCollection('SURVEY_DETAILS').save(e);
    }

})
