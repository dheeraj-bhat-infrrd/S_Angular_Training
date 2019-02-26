db.getCollection('COMPANY_SETTINGS').update({},{$set:{"branchAdminAllowedToDeleteUser": true}} , { multi : true});

db.getCollection('COMPANY_SETTINGS').update({},{$set:{"branchAdminAllowedToAddUser": true}} , { multi : true});

db.getCollection('COMPANY_SETTINGS').update({},{$set:{"regionAdminAllowedToDeleteUser": true}} , {  multi : true});

db.getCollection('COMPANY_SETTINGS').update({},{$set:{"regionAdminAllowedToAddUser": true}} , { multi : true});
