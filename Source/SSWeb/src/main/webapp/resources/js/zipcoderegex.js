var postCodeRegex = [
      {
        "code": "GB",
        "regex": "GIR[ ]?0AA|((AB|AL|B|BA|BB|BD|BH|BL|BN|BR|BS|BT|CA|CB|CF|CH|CM|CO|CR|CT|CV|CW|DA|DD|DE|DG|DH|DL|DN|DT|DY|E|EC|EH|EN|EX|FK|FY|G|GL|GY|GU|HA|HD|HG|HP|HR|HS|HU|HX|IG|IM|IP|IV|JE|KA|KT|KW|KY|L|LA|LD|LE|LL|LN|LS|LU|M|ME|MK|ML|N|NE|NG|NN|NP|NR|NW|OL|OX|PA|PE|PH|PL|PO|PR|RG|RH|RM|S|SA|SE|SG|SK|SL|SM|SN|SO|SP|SR|SS|ST|SW|SY|TA|TD|TF|TN|TQ|TR|TS|TW|UB|W|WA|WC|WD|WF|WN|WR|WS|WV|YO|ZE)(\\d[\\dA-Z]?[ ]?\\d[ABD-HJLN-UW-Z]{2}))|BFPO[ ]?\\d{1,4}"
      },
      {
        "code": "JE",
        "regex": "JE\\d[\\dA-Z]?[ ]?\\d[ABD-HJLN-UW-Z]{2}"
      },
      {
        "code": "GG",
        "regex": "GY\\d[\\dA-Z]?[ ]?\\d[ABD-HJLN-UW-Z]{2}"
      },
      {
        "code": "IM",
        "regex": "IM\\d[\\dA-Z]?[ ]?\\d[ABD-HJLN-UW-Z]{2}"
      },
      {
        "code": "US",
        "regex": "\\d{5}([ \\-]\\d{4})?"
      },
      {
        "code": "CA",
        "regex": "[ABCEGHJKLMNPRSTVXY]\\d[ABCEGHJ-NPRSTV-Z][ ]?\\d[ABCEGHJ-NPRSTV-Z]\\d"
      },
      {
        "code": "DE",
        "regex": "\\d{5}"
      },
      {
        "code": "JP",
        "regex": "\\d{3}-\\d{4}"
      },
      {
        "code": "FR",
        "regex": "\\d{2}[ ]?\\d{3}"
      },
      {
        "code": "AU",
        "regex": "\\d{4}"
      },
      {
        "code": "IT",
        "regex": "\\d{5}"
      },
      {
        "code": "CH",
        "regex": "\\d{4}"
      },
      {
        "code": "AT",
        "regex": "\\d{4}"
      },
      {
        "code": "ES",
        "regex": "\\d{5}"
      },
      {
        "code": "NL",
        "regex": "\\d{4}[ ]?[A-Z]{2}"
      },
      {
        "code": "BE",
        "regex": "\\d{4}"
      },
      {
        "code": "DK",
        "regex": "\\d{4}"
      },
      {
        "code": "SE",
        "regex": "\\d{3}[ ]?\\d{2}"
      },
      {
        "code": "NO",
        "regex": "\\d{4}"
      },
      {
        "code": "BR",
        "regex": "\\d{5}[\\-]?\\d{3}"
      },
      {
        "code": "PT",
        "regex": "\\d{4}([\\-]\\d{3})?"
      },
      {
        "code": "FI",
        "regex": "\\d{5}"
      },
      {
        "code": "AX",
        "regex": "22\\d{3}"
      },
      {
        "code": "KR",
        "regex": "\\d{3}[\\-]\\d{3}"
      },
      {
        "code": "CN",
        "regex": "\\d{6}"
      },
      {
        "code": "TW",
        "regex": "\\d{3}(\\d{2})?"
      },
      {
        "code": "SG",
        "regex": "\\d{6}"
      },
      {
        "code": "DZ",
        "regex": "\\d{5}"
      },
      {
        "code": "AD",
        "regex": "AD\\d{3}"
      },
      {
        "code": "AR",
        "regex": "([A-HJ-NP-Z])?\\d{4}([A-Z]{3})?"
      },
      {
        "code": "AM",
        "regex": "(37)?\\d{4}"
      },
      {
        "code": "AZ",
        "regex": "\\d{4}"
      },
      {
        "code": "BH",
        "regex": "((1[0-2]|[2-9])\\d{2})?"
      },
      {
        "code": "BD",
        "regex": "\\d{4}"
      },
      {
        "code": "BB",
        "regex": "(BB\\d{5})?"
      },
      {
        "code": "BY",
        "regex": "\\d{6}"
      },
      {
        "code": "BM",
        "regex": "[A-Z]{2}[ ]?[A-Z0-9]{2}"
      },
      {
        "code": "BA",
        "regex": "\\d{5}"
      },
      {
        "code": "IO",
        "regex": "BBND 1ZZ"
      },
      {
        "code": "BN",
        "regex": "[A-Z]{2}[ ]?\\d{4}"
      },
      {
        "code": "BG",
        "regex": "\\d{4}"
      },
      {
        "code": "KH",
        "regex": "\\d{5}"
      },
      {
        "code": "CV",
        "regex": "\\d{4}"
      },
      {
        "code": "CL",
        "regex": "\\d{7}"
      },
      {
        "code": "CR",
        "regex": "\\d{4,5}|\\d{3}-\\d{4}"
      },
      {
        "code": "HR",
        "regex": "\\d{5}"
      },
      {
        "code": "CY",
        "regex": "\\d{4}"
      },
      {
        "code": "CZ",
        "regex": "\\d{3}[ ]?\\d{2}"
      },
      {
        "code": "DO",
        "regex": "\\d{5}"
      },
      {
        "code": "EC",
        "regex": "([A-Z]\\d{4}[A-Z]|(?:[A-Z]{2})?\\d{6})?"
      },
      {
        "code": "EG",
        "regex": "\\d{5}"
      },
      {
        "code": "EE",
        "regex": "\\d{5}"
      },
      {
        "code": "FO",
        "regex": "\\d{3}"
      },
      {
        "code": "GE",
        "regex": "\\d{4}"
      },
      {
        "code": "GR",
        "regex": "\\d{3}[ ]?\\d{2}"
      },
      {
        "code": "GL",
        "regex": "39\\d{2}"
      },
      {
        "code": "GT",
        "regex": "\\d{5}"
      },
      {
        "code": "HT",
        "regex": "\\d{4}"
      },
      {
        "code": "HN",
        "regex": "(?:\\d{5})?"
      },
      {
        "code": "HU",
        "regex": "\\d{4}"
      },
      {
        "code": "IS",
        "regex": "\\d{3}"
      },
      {
        "code": "IN",
        "regex": "\\d{6}"
      },
      {
        "code": "ID",
        "regex": "\\d{5}"
      },
      {
        "code": "IL",
        "regex": "\\d{5}"
      },
      {
        "code": "JO",
        "regex": "\\d{5}"
      },
      {
        "code": "KZ",
        "regex": "\\d{6}"
      },
      {
        "code": "KE",
        "regex": "\\d{5}"
      },
      {
        "code": "KW",
        "regex": "\\d{5}"
      },
      {
        "code": "LA",
        "regex": "\\d{5}"
      },
      {
        "code": "LV",
        "regex": "\\d{4}"
      },
      {
        "code": "LB",
        "regex": "(\\d{4}([ ]?\\d{4})?)?"
      },
      {
        "code": "LI",
        "regex": "(948[5-9])|(949[0-7])"
      },
      {
        "code": "LT",
        "regex": "\\d{5}"
      },
      {
        "code": "LU",
        "regex": "\\d{4}"
      },
      {
        "code": "MK",
        "regex": "\\d{4}"
      },
      {
        "code": "MY",
        "regex": "\\d{5}"
      },
      {
        "code": "MV",
        "regex": "\\d{5}"
      },
      {
        "code": "MT",
        "regex": "[A-Z]{3}[ ]?\\d{2,4}"
      },
      {
        "code": "MU",
        "regex": "(\\d{3}[A-Z]{2}\\d{3})?"
      },
      {
        "code": "MX",
        "regex": "\\d{5}"
      },
      {
        "code": "MD",
        "regex": "\\d{4}"
      },
      {
        "code": "MC",
        "regex": "980\\d{2}"
      },
      {
        "code": "MA",
        "regex": "\\d{5}"
      },
      {
        "code": "NP",
        "regex": "\\d{5}"
      },
      {
        "code": "NZ",
        "regex": "\\d{4}"
      },
      {
        "code": "NI",
        "regex": "((\\d{4}-)?\\d{3}-\\d{3}(-\\d{1})?)?"
      },
      {
        "code": "NG",
        "regex": "(\\d{6})?"
      },
      {
        "code": "OM",
        "regex": "(PC )?\\d{3}"
      },
      {
        "code": "PK",
        "regex": "\\d{5}"
      },
      {
        "code": "PY",
        "regex": "\\d{4}"
      },
      {
        "code": "PH",
        "regex": "\\d{4}"
      },
      {
        "code": "PL",
        "regex": "\\d{2}-\\d{3}"
      },
      {
        "code": "PR",
        "regex": "00[679]\\d{2}([ \\-]\\d{4})?"
      },
      {
        "code": "RO",
        "regex": "\\d{6}"
      },
      {
        "code": "RU",
        "regex": "\\d{6}"
      },
      {
        "code": "SM",
        "regex": "4789\\d"
      },
      {
        "code": "SA",
        "regex": "\\d{5}"
      },
      {
        "code": "SN",
        "regex": "\\d{5}"
      },
      {
        "code": "SK",
        "regex": "\\d{3}[ ]?\\d{2}"
      },
      {
        "code": "SI",
        "regex": "\\d{4}"
      },
      {
        "code": "ZA",
        "regex": "\\d{4}"
      },
      {
        "code": "LK",
        "regex": "\\d{5}"
      },
      {
        "code": "TJ",
        "regex": "\\d{6}"
      },
      {
        "code": "TH",
        "regex": "\\d{5}"
      },
      {
        "code": "TN",
        "regex": "\\d{4}"
      },
      {
        "code": "TR",
        "regex": "\\d{5}"
      },
      {
        "code": "TM",
        "regex": "\\d{6}"
      },
      {
        "code": "UA",
        "regex": "\\d{5}"
      },
      {
        "code": "UY",
        "regex": "\\d{5}"
      },
      {
        "code": "UZ",
        "regex": "\\d{6}"
      },
      {
        "code": "VA",
        "regex": "00120"
      },
      {
        "code": "VE",
        "regex": "\\d{4}"
      },
      {
        "code": "ZM",
        "regex": "\\d{5}"
      },
      {
        "code": "AS",
        "regex": "96799"
      },
      {
        "code": "CC",
        "regex": "6799"
      },
      {
        "code": "CK",
        "regex": "\\d{4}"
      },
      {
        "code": "RS",
        "regex": "\\d{6}"
      },
      {
        "code": "ME",
        "regex": "8\\d{4}"
      },
      {
        "code": "CS",
        "regex": "\\d{5}"
      },
      {
        "code": "YU",
        "regex": "\\d{5}"
      },
      {
        "code": "CX",
        "regex": "6798"
      },
      {
        "code": "ET",
        "regex": "\\d{4}"
      },
      {
        "code": "FK",
        "regex": "FIQQ 1ZZ"
      },
      {
        "code": "NF",
        "regex": "2899"
      },
      {
        "code": "FM",
        "regex": "(9694[1-4])([ \\-]\\d{4})?"
      },
      {
        "code": "GF",
        "regex": "9[78]3\\d{2}"
      },
      {
        "code": "GN",
        "regex": "\\d{3}"
      },
      {
        "code": "GP",
        "regex": "9[78][01]\\d{2}"
      },
      {
        "code": "GS",
        "regex": "SIQQ 1ZZ"
      },
      {
        "code": "GU",
        "regex": "969[123]\\d([ \\-]\\d{4})?"
      },
      {
        "code": "GW",
        "regex": "\\d{4}"
      },
      {
        "code": "HM",
        "regex": "\\d{4}"
      },
      {
        "code": "IQ",
        "regex": "\\d{5}"
      },
      {
        "code": "KG",
        "regex": "\\d{6}"
      },
      {
        "code": "LR",
        "regex": "\\d{4}"
      },
      {
        "code": "LS",
        "regex": "\\d{3}"
      },
      {
        "code": "MG",
        "regex": "\\d{3}"
      },
      {
        "code": "MH",
        "regex": "969[67]\\d([ \\-]\\d{4})?"
      },
      {
        "code": "MN",
        "regex": "\\d{6}"
      },
      {
        "code": "MP",
        "regex": "9695[012]([ \\-]\\d{4})?"
      },
      {
        "code": "MQ",
        "regex": "9[78]2\\d{2}"
      },
      {
        "code": "NC",
        "regex": "988\\d{2}"
      },
      {
        "code": "NE",
        "regex": "\\d{4}"
      },
      {
        "code": "VI",
        "regex": "008(([0-4]\\d)|(5[01]))([ \\-]\\d{4})?"
      },
      {
        "code": "PF",
        "regex": "987\\d{2}"
      },
      {
        "code": "PG",
        "regex": "\\d{3}"
      },
      {
        "code": "PM",
        "regex": "9[78]5\\d{2}"
      },
      {
        "code": "PN",
        "regex": "PCRN 1ZZ"
      },
      {
        "code": "PW",
        "regex": "96940"
      },
      {
        "code": "RE",
        "regex": "9[78]4\\d{2}"
      },
      {
        "code": "SH",
        "regex": "(ASCN|STHL) 1ZZ"
      },
      {
        "code": "SJ",
        "regex": "\\d{4}"
      },
      {
        "code": "SO",
        "regex": "\\d{5}"
      },
      {
        "code": "SZ",
        "regex": "[HLMS]\\d{3}"
      },
      {
        "code": "TC",
        "regex": "TKCA 1ZZ"
      },
      {
        "code": "WF",
        "regex": "986\\d{2}"
      },
      {
        "code": "XK",
        "regex": "\\d{5}"
      },
      {
        "code": "YT",
        "regex": "976\\d{2}"
      }
    ];