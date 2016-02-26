var postCodeRegex = [
		{
			"code" : "AF",
			"regex" : "\\d{4}"
		},
		{
			"code" : "AX",
			"regex" : "\\d{5}"
		},
		{
			"code" : "AL",
			"regex" : "\\d{4}"
		},
		{
			"code" : "DZ",
			"regex" : "\\d{5}"
		},
		{
			"code" : "AS",
			"regex" : "\\d{5}(-{1}\\d{4,6})"
		},
		{
			"code" : "AD",
			"regex" : "[Aa][Dd]\\d{3}"
		},
		{
			"code" : "AO",
			"regex" : ""
		},
		{
			"code" : "AI",
			"regex" : "[Aa][I][-][2][6][4][0]"
		},
		{
			"code" : "AG",
			"regex" : ""
		},
		{
			"code" : "AR",
			"regex" : "\\d{4}|[A-Za-z]\\d{4}[a-zA-Z]{3}"
		},
		{
			"code" : "AM",
			"regex" : "\\d{4}"
		},
		{
			"code" : "AW",
			"regex" : ""
		},
		{
			"code" : "AC",
			"regex" : "[Aa][Ss][Cc][Nn]\\s{0,1}[1][Zz][Zz]"
		},
		{
			"code" : "AU",
			"regex" : "\\d{4}"
		},
		{
			"code" : "AT",
			"regex" : "\\d{4}"
		},
		{
			"code" : "AZ",
			"regex" : "[Aa][Zz]\\d{4}"
		},
		{
			"code" : "BS",
			"regex" : ""
		},
		{
			"code" : "BH",
			"regex" : "\\d{3,4}"
		},
		{
			"code" : "BD",
			"regex" : "\\d{4}"
		},
		{
			"code" : "BB",
			"regex" : "[Aa][Zz]\\d{5}"
		},
		{
			"code" : "BY",
			"regex" : "\\d{6}"
		},
		{
			"code" : "BE",
			"regex" : "\\d{4}"
		},
		{
			"code" : "BZ",
			"regex" : ""
		},
		{
			"code" : "BJ",
			"regex" : ""
		},
		{
			"code" : "BM",
			"regex" : "[A-Za-z]{2}\\s([A-Za-z]{2}|\\d{2})"
		},
		{
			"code" : "BT",
			"regex" : "\\d{5}"
		},
		{
			"code" : "BO",
			"regex" : "\\d{4}"
		},
		{
			"code" : "BQ",
			"regex" : ""
		},
		{
			"code" : "BA",
			"regex" : "\\d{5}"
		},
		{
			"code" : "BW",
			"regex" : ""
		},
		{
			"code" : "BR",
			"regex" : "\\d{5}-\\d{3}"
		},
		{
			"code" : "",
			"regex" : "[Bb][Ii][Qq]{2}\\s{0,1}[1][Zz]{2}"
		},
		{
			"code" : "IO",
			"regex" : "[Bb]{2}[Nn][Dd]\\s{0,1}[1][Zz]{2}"
		},
		{
			"code" : "VG",
			"regex" : "[Vv][Gg]\\d{4}"
		},
		{
			"code" : "BN",
			"regex" : "[A-Za-z]{2}\\d{4}"
		},
		{
			"code" : "BG",
			"regex" : "\\d{4}"
		},
		{
			"code" : "BF",
			"regex" : ""
		},
		{
			"code" : "BI",
			"regex" : ""
		},
		{
			"code" : "KH",
			"regex" : "\\d{5}"
		},
		{
			"code" : "CM",
			"regex" : ""
		},
		{
			"code" : "CA",
			/*"regex" : "(?=[^DdFfIiOoQqUu\\d\\s])[A-Za-z]\\d(?=[^DdFfIiOoQqUu\\d\\s])[A-Za-z]\\s{0,1}\\d(?=[^DdFfIiOoQqUu\\d\\s])[A-Za-z]\\d"*/
			"regex" : "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$"
		}, {
			"code" : "CV",
			"regex" : "\\d{4}"
		}, {
			"code" : "KY",
			"regex" : "[Kk][Yy]\\d[-\\s]{0,1}\\d{4}"
		}, {
			"code" : "CF",
			"regex" : ""
		}, {
			"code" : "TD",
			"regex" : "\\d{5}"
		}, {
			"code" : "CL",
			"regex" : "\\d{7}\\s\\(\\d{3}-\\d{4}\\)"
		}, {
			"code" : "CN",
			"regex" : "\\d{6}"
		}, {
			"code" : "CX",
			"regex" : "\\d{4}"
		}, {
			"code" : "CC",
			"regex" : "\\d{4}"
		}, {
			"code" : "CO",
			"regex" : "\\d{6}"
		}, {
			"code" : "KM",
			"regex" : ""
		}, {
			"code" : "CG",
			"regex" : ""
		}, {
			"code" : "CD",
			"regex" : "[Cc][Dd]"
		}, {
			"code" : "CK",
			"regex" : ""
		}, {
			"code" : "CR",
			"regex" : "\\d{4,5}"
		}, {
			"code" : "CI",
			"regex" : ""
		}, {
			"code" : "HR",
			"regex" : "\\d{5}"
		}, {
			"code" : "CU",
			"regex" : "\\d{5}"
		}, {
			"code" : "CW",
			"regex" : ""
		}, {
			"code" : "CY",
			"regex" : "\\d{4}"
		}, {
			"code" : "CZ",
			"regex" : "\\d{5}\\s\\(\\d{3}\\s\\d{2}\\)"
		}, {
			"code" : "DK",
			"regex" : "\\d{4}"
		}, {
			"code" : "DJ",
			"regex" : ""
		}, {
			"code" : "DM",
			"regex" : ""
		}, {
			"code" : "DO",
			"regex" : "\\d{5}"
		}, {
			"code" : "TL",
			"regex" : ""
		}, {
			"code" : "EC",
			"regex" : "\\d{6}"
		}, {
			"code" : "SV",
			"regex" : "1101"
		}, {
			"code" : "EG",
			"regex" : "\\d{5}"
		}, {
			"code" : "GQ",
			"regex" : ""
		}, {
			"code" : "ER",
			"regex" : ""
		}, {
			"code" : "EE",
			"regex" : "\\d{5}"
		}, {
			"code" : "ET",
			"regex" : "\\d{4}"
		}, {
			"code" : "FK",
			"regex" : "[Ff][Ii][Qq]{2}\\s{0,1}[1][Zz]{2}"
		}, {
			"code" : "FO",
			"regex" : "\\d{3}"
		}, {
			"code" : "FJ",
			"regex" : ""
		}, {
			"code" : "FI",
			"regex" : "\\d{5}"
		}, {
			"code" : "FR",
			"regex" : "\\d{5}"
		}, {
			"code" : "GF",
			"regex" : "973\\d{2}"
		}, {
			"code" : "PF",
			"regex" : "987\\d{2}"
		}, {
			"code" : "TF",
			"regex" : ""
		}, {
			"code" : "GA",
			"regex" : "\\d{2}\\s[a-zA-Z-_ ]\\s\\d{2}"
		}, {
			"code" : "GM",
			"regex" : ""
		}, {
			"code" : "GE",
			"regex" : "\\d{4}"
		}, {
			"code" : "DE",
			"regex" : "\\d{2}"
		}, {
			"code" : "DE",
			"regex" : "\\d{4}"
		}, {
			"code" : "DE",
			"regex" : "\\d{5}"
		}, {
			"code" : "GH",
			"regex" : ""
		}, {
			"code" : "GI",
			"regex" : "[Gg][Xx][1]{2}\\s{0,1}[1][Aa]{2}"
		}, {
			"code" : "GR",
			"regex" : "\\d{3}\\s{0,1}\\d{2}"
		}, {
			"code" : "GL",
			"regex" : "\\d{4}"
		}, {
			"code" : "GD",
			"regex" : ""
		}, {
			"code" : "GP",
			"regex" : "971\\d{2}"
		}, {
			"code" : "GU",
			"regex" : "\\d{5}"
		}, {
			"code" : "GT",
			"regex" : "\\d{5}"
		}, {
			"code" : "GG",
			"regex" : "[A-Za-z]{2}\\d\\s{0,1}\\d[A-Za-z]{2}"
		}, {
			"code" : "GN",
			"regex" : ""
		}, {
			"code" : "GW",
			"regex" : "\\d{4}"
		}, {
			"code" : "GY",
			"regex" : ""
		}, {
			"code" : "HT",
			"regex" : "\\d{4}"
		}, {
			"code" : "HM",
			"regex" : "\\d{4}"
		}, {
			"code" : "HN",
			"regex" : "\\d{5}"
		}, {
			"code" : "HK",
			"regex" : ""
		}, {
			"code" : "HU",
			"regex" : "\\d{4}"
		}, {
			"code" : "IS",
			"regex" : "\\d{3}"
		}, {
			"code" : "IN",
			"regex" : "\\d{6}"
		}, {
			"code" : "ID",
			"regex" : "\\d{5}"
		}, {
			"code" : "IR",
			"regex" : "\\d{5}-\\d{5}"
		}, {
			"code" : "IQ",
			"regex" : "\\d{5}"
		}, {
			"code" : "IE",
			"regex" : ""
		}, {
			"code" : "IM",
			"regex" : "[Ii[Mm]\\d{1,2}\\s\\d\\[A-Z]{2}"
		}, {
			"code" : "IL",
			"regex" : "\\d{7}"
		}, {
			"code" : "IT",
			"regex" : "\\d{5}"
		}, {
			"code" : "JM",
			"regex" : "\\d{2}"
		}, {
			"code" : "JP",
			"regex" : "\\d{7}\\s\\(\\d{3}-\\d{4}\\)"
		}, {
			"code" : "JE",
			"regex" : "[Jj][Ee]\\d\\s{0,1}\\d[A-Za-z]{2}"
		}, {
			"code" : "JO",
			"regex" : "\\d{5}"
		}, {
			"code" : "KZ",
			"regex" : "\\d{6}"
		}, {
			"code" : "KE",
			"regex" : "\\d{5}"
		}, {
			"code" : "KI",
			"regex" : ""
		}, {
			"code" : "KP",
			"regex" : ""
		}, {
			"code" : "KR",
			"regex" : "\\d{6}\\s\\(\\d{3}-\\d{3}\\)"
		}, {
			"code" : "XK",
			"regex" : "\\d{5}"
		}, {
			"code" : "KW",
			"regex" : "\\d{5}"
		}, {
			"code" : "KG",
			"regex" : "\\d{6}"
		}, {
			"code" : "LV",
			"regex" : "[Ll][Vv][- ]{0,1}\\d{4}"
		}, {
			"code" : "LA",
			"regex" : "\\d{5}"
		}, {
			"code" : "LB",
			"regex" : "\\d{4}\\s{0,1}\\d{4}"
		}, {
			"code" : "LS",
			"regex" : "\\d{3}"
		}, {
			"code" : "LR",
			"regex" : "\\d{4}"
		}, {
			"code" : "LY",
			"regex" : "\\d{5}"
		}, {
			"code" : "LI",
			"regex" : "\\d{4}"
		}, {
			"code" : "LT",
			"regex" : "[Ll][Tt][- ]{0,1}\\d{5}"
		}, {
			"code" : "LU",
			"regex" : "\\d{4}"
		}, {
			"code" : "MO",
			"regex" : ""
		}, {
			"code" : "MK",
			"regex" : "\\d{4}"
		}, {
			"code" : "MG",
			"regex" : "\\d{3}"
		}, {
			"code" : "MW",
			"regex" : ""
		}, {
			"code" : "MV",
			"regex" : "\\d{4,5}"
		}, {
			"code" : "MY",
			"regex" : "\\d{5}"
		}, {
			"code" : "ML",
			"regex" : ""
		}, {
			"code" : "MT",
			"regex" : "[A-Za-z]{3}\\s{0,1}\\d{4}"
		}, {
			"code" : "MH",
			"regex" : "\\d{5}"
		}, {
			"code" : "MR",
			"regex" : ""
		}, {
			"code" : "MU",
			"regex" : ""
		}, {
			"code" : "MQ",
			"regex" : "972\\d{2}"
		}, {
			"code" : "YT",
			"regex" : "976\\d{2}"
		}, {
			"code" : "FM",
			"regex" : "\\d{5}(-{1}\\d{4})"
		}, {
			"code" : "MX",
			"regex" : "\\d{5}"
		}, {
			"code" : "FM",
			"regex" : "\\d{5}"
		}, {
			"code" : "MD",
			"regex" : "[Mm][Dd][- ]{0,1}\\d{4}"
		}, {
			"code" : "MC",
			"regex" : "980\\d{2}"
		}, {
			"code" : "MN",
			"regex" : "\\d{5}"
		}, {
			"code" : "ME",
			"regex" : "\\d{5}"
		}, {
			"code" : "MS",
			"regex" : "[Mm][Ss][Rr]\\s{0,1}\\d{4}"
		}, {
			"code" : "MA",
			"regex" : "\\d{5}"
		}, {
			"code" : "MZ",
			"regex" : "\\d{4}"
		}, {
			"code" : "MM",
			"regex" : "\\d{5}"
		}, {
			"code" : "NA",
			"regex" : "\\d{5}"
		}, {
			"code" : "NR",
			"regex" : ""
		}, {
			"code" : "NP",
			"regex" : "\\d{5}"
		}, {
			"code" : "NL",
			"regex" : "\\d{4}\\s{0,1}[A-Za-z]{2}"
		}, {
			"code" : "NC",
			"regex" : "988\\d{2}"
		}, {
			"code" : "NZ",
			"regex" : "\\d{4}"
		}, {
			"code" : "NI",
			"regex" : "\\d{5}"
		}, {
			"code" : "NE",
			"regex" : "\\d{4}"
		}, {
			"code" : "NG",
			"regex" : "\\d{6}"
		}, {
			"code" : "NU",
			"regex" : ""
		}, {
			"code" : "NF",
			"regex" : "\\d{4}"
		}, {
			"code" : "MP",
			"regex" : "\\d{5}"
		}, {
			"code" : "NO",
			"regex" : "\\d{4}"
		}, {
			"code" : "OM",
			"regex" : "\\d{3}"
		}, {
			"code" : "PK",
			"regex" : "\\d{5}"
		}, {
			"code" : "PW",
			"regex" : "\\d{5}"
		}, {
			"code" : "PA",
			"regex" : "\\d{6}"
		}, {
			"code" : "PG",
			"regex" : "\\d{3}"
		}, {
			"code" : "PY",
			"regex" : "\\d{4}"
		}, {
			"code" : "PE",
			"regex" : "\\d{5}"
		}, {
			"code" : "PH",
			"regex" : "\\d{4}"
		}, {
			"code" : "PN",
			"regex" : "[Pp][Cc][Rr][Nn]\\s{0,1}[1][Zz]{2}"
		}, {
			"code" : "PL",
			"regex" : "\\d{2}[- ]{0,1}\\d{3}"
		}, {
			"code" : "PT",
			"regex" : "\\d{4}"
		}, {
			"code" : "PT",
			"regex" : "\\d{4}[- ]{0,1}\\d{3}"
		}, {
			"code" : "PR",
			"regex" : "\\d{5}"
		}, {
			"code" : "QA",
			"regex" : ""
		}, {
			"code" : "RE",
			"regex" : "974\\d{2}"
		}, {
			"code" : "RO",
			"regex" : "\\d{6}"
		}, {
			"code" : "RU",
			"regex" : "\\d{6}"
		}, {
			"code" : "BL",
			"regex" : "97133"
		}, {
			"code" : "SH",
			"regex" : "[Ss][Tt][Hh][Ll]\\s{0,1}[1][Zz]{2}"
		}, {
			"code" : "KN",
			"regex" : ""
		}, {
			"code" : "LC",
			"regex" : ""
		}, {
			"code" : "MF",
			"regex" : "97150"
		}, {
			"code" : "PM",
			"regex" : "97500"
		}, {
			"code" : "VC",
			"regex" : "[Vv][Cc]\\d{4}"
		}, {
			"code" : "SM",
			"regex" : "4789\\d"
		}, {
			"code" : "ST",
			"regex" : ""
		}, {
			"code" : "SA",
			"regex" : "\\d{5}(-{1}\\d{4})?"
		}, {
			"code" : "SN",
			"regex" : "\\d{5}"
		}, {
			"code" : "RS",
			"regex" : "\\d{5}"
		}, {
			"code" : "RS",
			"regex" : "\\d{5}"
		}, {
			"code" : "SC",
			"regex" : ""
		}, {
			"code" : "SX",
			"regex" : ""
		}, {
			"code" : "SL",
			"regex" : ""
		}, {
			"code" : "SG",
			"regex" : "\\d{2}"
		}, {
			"code" : "SG",
			"regex" : "\\d{4}"
		}, {
			"code" : "SG",
			"regex" : "\\d{6}"
		}, {
			"code" : "SK",
			"regex" : "\\d{5}\\s\\(\\d{3}\\s\\d{2}\\)"
		}, {
			"code" : "SI",
			"regex" : "([Ss][Ii][- ]{0,1}){0,1}\\d{4}"
		}, {
			"code" : "SB",
			"regex" : ""
		}, {
			"code" : "SO",
			"regex" : ""
		}, {
			"code" : "ZA",
			"regex" : "\\d{4}"
		}, {
			"code" : "GS",
			"regex" : "[Ss][Ii][Qq]{2}\\s{0,1}[1][Zz]{2}"
		}, {
			"code" : "KR",
			"regex" : "\\d{6}\\s\\(\\d{3}-\\d{3}\\)"
		}, {
			"code" : "ES",
			"regex" : "\\d{5}"
		}, {
			"code" : "LK",
			"regex" : "\\d{5}"
		}, {
			"code" : "SD",
			"regex" : "\\d{5}"
		}, {
			"code" : "SR",
			"regex" : ""
		}, {
			"code" : "SZ",
			"regex" : "[A-Za-z]\\d{3}"
		}, {
			"code" : "SE",
			"regex" : "\\d{5}\\s\\(\\d{3}\\s\\d{2}\\)"
		}, {
			"code" : "CH",
			"regex" : "\\d{4}"
		}, {
			"code" : "SJ",
			"regex" : "\\d{4}"
		}, {
			"code" : "SY",
			"regex" : ""
		}, {
			"code" : "TW",
			"regex" : "\\d{5}"
		}, {
			"code" : "TJ",
			"regex" : "\\d{6}"
		}, {
			"code" : "TZ",
			"regex" : ""
		}, {
			"code" : "TH",
			"regex" : "\\d{5}"
		}, {
			"code" : "TG",
			"regex" : ""
		}, {
			"code" : "TK",
			"regex" : ""
		}, {
			"code" : "TO",
			"regex" : ""
		}, {
			"code" : "TT",
			"regex" : "\\d{6}"
		}, {
			"code" : "SH",
			"regex" : "[Tt][Dd][Cc][Uu]\\s{0,1}[1][Zz]{2}"
		}, {
			"code" : "TN",
			"regex" : "\\d{4}"
		}, {
			"code" : "TR",
			"regex" : "\\d{5}"
		}, {
			"code" : "TM",
			"regex" : "\\d{6}"
		}, {
			"code" : "TC",
			"regex" : "[Tt][Kk][Cc][Aa]\\s{0,1}[1][Zz]{2}"
		}, {
			"code" : "TV",
			"regex" : ""
		}, {
			"code" : "UG",
			"regex" : ""
		}, {
			"code" : "UA",
			"regex" : "\\d{5}"
		}, {
			"code" : "AE",
			"regex" : ""
		}, {
			"code" : "GB",
			"regex" : "[A-Za-z]{1,2}\\d{1}[A-Za-z0-9]{0,1} *\\d{1}[A-Za-z]{2}"
		}, {
			"code" : "US",
			"regex" : "\\b\\d{5}\\b(?:[- ]{1}\\d{4})?"
		}, {
			"code" : "UY",
			"regex" : "\\d{5}"
		}, {
			"code" : "VI",
			"regex" : "\\d{5}"
		}, {
			"code" : "UZ",
			"regex" : "\\d{3} \\d{3}"
		}, {
			"code" : "VU",
			"regex" : ""
		}, {
			"code" : "VA",
			"regex" : "120"
		}, {
			"code" : "VE",
			"regex" : "\\d{4}(\\s[a-zA-Z]{1})?"
		}, {
			"code" : "VN",
			"regex" : "\\d{6}"
		}, {
			"code" : "WF",
			"regex" : "986\\d{2}"
		}, {
			"code" : "YE",
			"regex" : ""
		}, {
			"code" : "ZM",
			"regex" : "\\d{5}"
		}, {
			"code" : "ZW",
			"regex" : ""
		} ];