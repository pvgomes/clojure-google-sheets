# clojure-google-sheets

A clojure google sheets integration example, based on [java quickstart start](https://developers.google.com/sheets/api/quickstart/java) with way more examples.
This sample use a [ibovespa](http://www.b3.com.br/en_us/market-data-and-indices/indices/broad-indices/ibovespa.htm) prices [this spreadsheet](https://docs.google.com/spreadsheets/d/1WNJ-c4qY3qZbnxTOaS9XsEnRJnO6_aO58V_e109U9Ig/edit#gid=0) where the first tab is the stocks prices to read second one is a simulation for stocks that we bought.  

[![Build Status](https://travis-ci.org/pvgomes/clojure-google-sheets.svg?branch=master)](https://travis-ci.org/pvgomes/clojure-google-sheets)

## Configuration and Running

[Video how to integrate using this repo](https://www.youtube.com/watch?v=posNZFWSEgU)

1. Access your [google console developers](https://console.developers.google.com/) and turn on the Google Sheets API and get credentials.json, find for "Authorized redirect URIs" and add `http://localhost:8888`
2. Add your credentials.json on [resources](./resources) path (you can find [template.credentials.json](./resources/template.credentials.json) as an example there)
3. good to go, just run `lein run`
4. A browser will open requesting your auth, accept it
5. You'll see the output of some [ibovespa](http://www.b3.com.br/en_us/market-data-and-indices/indices/broad-indices/ibovespa.htm) prices [this spreadsheet](https://docs.google.com/spreadsheets/d/1WNJ-c4qY3qZbnxTOaS9XsEnRJnO6_aO58V_e109U9Ig/edit#gid=0), and also simple response of write it means that it worked, eg:
```
Stock, Current Price
EMBR3, 6.49
HGTX3, 17.19
CIEL3, 3.93
UNIP6, 27.4
IRBR3, 8.09
HBOR3, 10.68
MEAL3, 3.34
CYRE3, 23.44
ABEV3, 12.53
ABCB4, 11.89
AZUL4, 24.27
GOLL4, 17.39
JBSS3, 20.08
SHUL4, 12.51
JSLG3, 30.4
SCAR3, 35.79
TRIS3, 11.16
KEPL3, 39.51
POMO3, 2.56
TGMA3, 21.05
FESA4, 17.82
ALSO3, 24.33
CGRA4, 27.22
LEVE3, 17.77
ENEV3, 47.1
```

## Moving forward
Now that we can read at least a sample spreadsheet lets do more, but to do it, we'll start to use our [documentation](./doc/intro.md) see you there.

For more examples, look at my [YouTube channel](https://www.youtube.com/channel/UCH6lFcii0mXxcZkDg9AUurw)

### Tests
```
lein test
```

### Tips

**sheet id**

sheet id is a range on a sheet. All indexes are zero-based. it means that a first sheet is 0.

The easiest way to find the sheet id is in the browser URL when you open the spreadsheet / sheet: `https://docs.google.com/spreadsheets/d/{spreadsheetId}/edit#gid={sheetId}`
more info regarding sheet cordinates see [here](https://developers.google.com/resources/api-libraries/documentation/sheets/v4/csharp/latest/classGoogle_1_1Apis_1_1Sheets_1_1v4_1_1Data_1_1GridRange.html)

## License

Copyright © 2020 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
