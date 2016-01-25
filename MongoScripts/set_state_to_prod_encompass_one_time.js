db.getCollection('COMPANY_SETTINGS').find({crm_info : {$exists : true}, "crm_info.crm_source" : "encompass"} ).snapshot().forEach(
    function(record) {
        record.crm_info["state"] = "prod";
        db.COMPANY_SETTINGS.save(record);
    }
);
