

db.COMPANY_SETTINGS.update({"profileImageUrlThumbnail": {$ne : null}},{$set:{"isProfileImageProcessed": false}} ,  false, true);

db.REGION_SETTINGS.update({"profileImageUrlThumbnail": {$ne : null}},{$set:{"isProfileImageProcessed": false}} ,  false, true);

db.BRANCH_SETTINGS.update({"profileImageUrlThumbnail": {$ne : null}},{$set:{"isProfileImageProcessed": false}} ,  false, true);

db.AGENT_SETTINGS.update({"profileImageUrlThumbnail": {$ne : null}},{$set:{"isProfileImageProcessed": false}} ,  false, true);
