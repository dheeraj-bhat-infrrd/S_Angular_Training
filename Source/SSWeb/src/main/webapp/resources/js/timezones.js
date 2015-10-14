var offset=new Date().getTimezoneOffset()*60*1000;
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
	var convertedTimestamp= date.getTime() + offset ;
	return new Date(convertedTimestamp);
}
function convertUserDateToLocalWeekFormt(date){
	var days = ['Sun','Mon','Tue','Wed','Thu','Fri','Sat'];
	var convertedTimestamp= date.getTime() + offset;
	var date3= new Date(convertedTimestamp);
	var dayOftheweek = days[ date3.getDay() ];
	var month=((date3.getMonth() + 1)<10)? "0"+(date3.getMonth() + 1) : (date3.getMonth() + 1);
	var day=(date3.getDate()<10) ? "0"+(date3.getDate()) : (date3.getDate());
	var minutes= (date3.getMinutes()<10) ? "0"+(date3.getMinutes()) : (date3.getMinutes());
	var hours= (date3.getHours()<10) ? "0"+(date3.getHours()) : (date3.getHours());
	var sec=  (date3.getSeconds()<10) ? "0"+(date3.getSeconds()) : (date3.getSeconds());
	var date4=   dayOftheweek +" "+month+" "+day+" " + date3.getFullYear() +" "+hours+":"+minutes+":"+sec;
	return date4;
}

function convertUserDateToWeekFormt(date) {
	var days = [ 'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat' ];
	var months = [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug',
			'Sep', 'Oct', 'Nov', 'Dec' ];
	var date3 = new Date(date.getTime());
	var dayOftheweek = days[date3.getDay()];
	var month = months[date3.getMonth()];
	var day = (date3.getDate() < 10) ? "0" + (date3.getDate()) : (date3
			.getDate());
	var minutes = (date3.getMinutes() < 10) ? "0" + (date3.getMinutes())
			: (date3.getMinutes());
	var hours = date3.getHours();
	var ampm = hours >= 12 ? 'pm' : 'am';
	hours = hours % 12;
	hours = hours ? hours : 12;
	var date4 = dayOftheweek + ", " + month + " " + day + ", "
			+ date3.getFullYear() + " at " + hours + ":" + minutes + " " + ampm;
	return date4;
}

function  convertTimeStampToLocalTimeStamp(generalTimestamp){
	var convertedTimestamp= generalTimestamp.getTime() - offset;
	var date3=new Date(convertedTimestamp);	
	var month=((date3.getMonth() + 1)<10)? "0"+(date3.getMonth() + 1) : (date3.getMonth() + 1);
	var day=(date3.getDate()<10) ? "0"+(date3.getDate()) : (date3.getDate());
	var minutes= (date3.getMinutes()<10) ? "0"+(date3.getMinutes()) : (date3.getMinutes());
	var hours= (date3.getHours()<10) ? "0"+(date3.getHours()) : (date3.getHours());
	var sec=  (date3.getSeconds()<10) ? "0"+(date3.getSeconds()) : (date3.getSeconds());
	var date4= month +'-'+day+'-'+ date3.getFullYear() +" "+hours+":"+ minutes +":"+ sec+"."+date3.getMilliseconds() ;
	return date4;
}

