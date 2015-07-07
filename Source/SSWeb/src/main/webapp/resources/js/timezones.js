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
function convertUserDateToLocalWeekFormt(date){
	var days = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat'];
	var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
	console.info("date:"+date);
	var convertedTimestamp= date.getTime() + (date.getTimezoneOffset())*60*1000 ;
	console.info("Converted time zone:"+new Date(convertedTimestamp));
	var date3= new Date(convertedTimestamp);
	var dayOftheweek = days[ date3.getDay() ];
	var month = months[ date3.getMonth() ];
	var month=((date3.getMonth() + 1)<10)? "0"+(date3.getMonth() + 1) : (date3.getMonth() + 1);
	var day=(date3.getDate()<10) ? "0"+(date3.getDate()) : (date3.getDate());
	var minutes= (date3.getMinutes()<10) ? "0"+(date3.getMinutes()) : (date3.getMinutes());
	var hours= (date3.getHours()<10) ? "0"+(date3.getHours()) : (date3.getHours());
	var sec=  (date3.getSeconds()<10) ? "0"+(date3.getSeconds()) : (date3.getSeconds());
	var date4=   dayOftheweek +" "+month+" "+day+" " + date3.getFullYear() +" "+hours+":"+minutes+":"+sec;
	return date4;
}