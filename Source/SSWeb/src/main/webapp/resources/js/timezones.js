function convertUTCToUserDate(date) {
	return new Date(Date.UTC(date.getFullYear(), date.getMonth(), date
			.getDate(), date.getHours(), date.getMinutes(), date.getSeconds()));
}

function convertUserDateToUTC(date) {
	return new Date(date.getUTCFullYear(), date.getUTCMonth(), date
			.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date
			.getUTCSeconds());
}
