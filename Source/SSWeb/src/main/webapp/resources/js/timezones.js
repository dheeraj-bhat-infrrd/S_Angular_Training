function convertUTCToUserDate(date) {
	return new Date(Date.UTC(date.getFullYear(), date.getMonth(), date
			.getDate(), date.getHours(), date.getMinutes(), date.getSeconds()));
}

function convertUserDateToUTC(date) {
	return new Date(date.getUTCFullYear(), date.getUTCMonth(), date
			.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date
			.getUTCSeconds());
}

function convertUserDateToLocale(date){
	console.info("date:"+date);
	var convertedTimestamp= date.getTime() + (date.getTimezoneOffset())*60*1000 ;
	console.info("Converted time zone:"+new Date(convertedTimestamp));
	return new Date(convertedTimestamp);
}