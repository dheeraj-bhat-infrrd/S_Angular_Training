//Get all the agents with social media tokens
var agents = db.getCollection('AGENT_SETTINGS').find({socialMediaTokens : {$exists : true}});
var branches = db.getCollection('BRANCH_SETTINGS').find({socialMediaTokens : {$exists : true}});
var regions = db.getCollection('REGION_SETTINGS').find({socialMediaTokens : {$exists : true}});
var companies = db.getCollection('COMPANY_SETTINGS').find({socialMediaTokens : {$exists : true}});

// Define sets for each social media
var facebookSet = {};
var twitterSet = {};
var linkedinSet = {};
var googleSet = {};
var yelpSet = {};
var lendingtreeSet = {};
var realtorSet = {};
var zillowSet = {};

// create a duplicate check method
function checkForDuplicates(mediaTokens, entity, id){
  if (mediaTokens.facebookToken != undefined) {
    if (facebookSet[mediaTokens.facebookToken.facebookPageLink] == undefined) {
      facebookSet[mediaTokens.facebookToken.facebookPageLink] = [];
    }
    facebookSet[mediaTokens.facebookToken.facebookPageLink].push(entity + " : " + id);
  }
  if (mediaTokens.twitterToken != undefined) {
    if (twitterSet[mediaTokens.twitterToken.twitterPageLink] == undefined ) {
      twitterSet[mediaTokens.twitterToken.twitterPageLink] = [];
    }
    twitterSet[mediaTokens.twitterToken.twitterPageLink].push(entity + " : " + id);
  }
  if (mediaTokens.linkedInToken != undefined) {
    if (linkedinSet[mediaTokens.linkedInToken.linkedInPageLink] == undefined ) {
      linkedinSet[mediaTokens.linkedInToken.linkedInPageLink] = [];
    }
    linkedinSet[mediaTokens.linkedInToken.linkedInPageLink].push(entity + " : " + id);
  }
  if (mediaTokens.googleToken != undefined) {
    if (googleSet[mediaTokens.googleToken.profileLink] == undefined ) {
      googleSet[mediaTokens.googleToken.profileLink] = [];
    }
    googleSet[mediaTokens.googleToken.profileLink].push(entity + " : " + id);
  }
  if (mediaTokens.yelpToken != undefined) {
    if (yelpSet[mediaTokens.yelpToken.yelpPageLink] == undefined ) {
      yelpSet[mediaTokens.yelpToken.yelpPageLink] = [];
    }
    yelpSet[mediaTokens.yelpToken.yelpPageLink].push(entity + " : " + id);
  }
  if (mediaTokens.lendingTreeToken != undefined) {
    if (lendingtreeSet[mediaTokens.lendingTreeToken.lendingTreeProfileLink] == undefined ) {
      lendingtreeSet[mediaTokens.lendingTreeToken.lendingTreeProfileLink] = [];
    }
    lendingtreeSet[mediaTokens.lendingTreeToken.lendingTreeProfileLink].push(entity + " : " + id);
  }
  if (mediaTokens.realtorToken != undefined) {
    if (realtorSet[mediaTokens.realtorToken.realtorProfileLink] == undefined  ) {
      realtorSet[mediaTokens.realtorToken.realtorProfileLink] = [];
    }
    realtorSet[mediaTokens.realtorToken.realtorProfileLink].push(entity + " : " + id);
  }
  if (mediaTokens.zillowToken != undefined) {
    if (zillowSet[mediaTokens.zillowToken.zillowProfileLink] == undefined ) {
      zillowSet[mediaTokens.zillowToken.zillowProfileLink] = [];
    }
    zillowSet[mediaTokens.zillowToken.zillowProfileLink].push(entity + " : " + id);
  }
}

// iterate through agents' social media tokens and check for duplicates
agents.forEach(function(agent){
  var mediaTokens = agent.socialMediaTokens;
  checkForDuplicates(mediaTokens, "agentId", agent.iden);
});

// iterate through branches' social media tokens and check for duplicates
branches.forEach(function(branch){
  var mediaTokens = branch.socialMediaTokens;
  checkForDuplicates(mediaTokens, "branchId", branch.iden);
});

// iterate through regions' social media tokens and check for duplicates
regions.forEach(function(region){
  var mediaTokens = region.socialMediaTokens;
  checkForDuplicates(mediaTokens, "regionId", region.iden);
});

// iterate through companies' social media tokens and check for duplicates
companies.forEach(function(company){
  var mediaTokens = company.socialMediaTokens;
  checkForDuplicates(mediaTokens, "companyId", company.iden);
});

//TODO: for each set, iterate and display if there are any with count greater than 1
for (var key in facebookSet) {
  if (facebookSet[key].length > 1) {
    print("The facebook link : " + key + " is duplicated at ids : " + facebookSet[key]);
  }
}

for (var key in twitterSet) {
  if (twitterSet[key].length > 1) {
    print("The twitter link : " + key + " is duplicated at ids : " + twitterSet[key]);
  }
}

for (var key in linkedinSet) {
  if (linkedinSet[key].length > 1) {
    print("The linkedIn link : " + key + " is duplicated at ids : " + linkedinSet[key]);
  }
}

for (var key in googleSet) {
  if (googleSet[key].length > 1) {
    print("The google plus link : " + key + " is duplicated at ids : " + googleSet[key]);
  }
}

for (var key in yelpSet) {
  if (yelpSet[key].length > 1) {
    print("The yelp link : " + key + " is duplicated at ids : " + yelpSet[key]);
  }
}

for (var key in lendingtreeSet) {
  if (lendingtreeSet[key].length > 1) {
    print("The lending tree link : " + key + " is duplicated at ids : " + lendingtreeSet[key]);
  }
}

for (var key in linkedinSet) {
  if (linkedinSet[key].length > 1) {
    print("The linkedIn link : " + key + " is duplicated at ids : " + linkedinSet[key]);
  }
}

for (var key in realtorSet) {
  if (realtorSet[key].length > 1) {
    print("The realtor link : " + key + " is duplicated at ids : " + realtorSet[key]);
  }
}

for (var key in zillowSet) {
  if (zillowSet[key].length > 1) {
    print("The zillow link : " + key + " is duplicated at ids : " + zillowSet[key]);
  }
}
