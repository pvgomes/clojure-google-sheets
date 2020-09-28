# clojure-google-sheets

A clojure google sheets integration example, based on [java quickstart start](https://developers.google.com/sheets/api/quickstart/java) with way more examples

[![Build Status](https://travis-ci.org/pvgomes/clojure-google-sheets.svg?branch=master)](https://travis-ci.org/pvgomes/clojure-google-sheets)

## Installation

1. Access your [google console developers](https://console.developers.google.com/) and turn on the Google Sheets API and get credentials.json, find for "Authorized redirect URIs" and add `http://localhost:8888`
2. Add your credentials.json on [resources](./resources) path (you can find [template.credentials.json](./resources/template.credentials.json) as an example there)
3. good to go, just run `lein run`
4. A browser will open requesting your auth, accept it
5. You'll see the output of some [ibovespa](http://www.b3.com.br/en_us/market-data-and-indices/indices/broad-indices/ibovespa.htm) prices [this spreadsheet](https://docs.google.com/spreadsheets/d/1WNJ-c4qY3qZbnxTOaS9XsEnRJnO6_aO58V_e109U9Ig/edit#gid=0), it means that it worked, eg:
```
Stock, Current Price
EMBR3, 6.12
HGTX3, 17.38
CIEL3, 4.15
UNIP6, 27.82
IRBR3, 7.17
HBOR3, 10.83
MEAL3, 3.36
CYRE3, 23.6
ABEV3, 12.84
ABCB4, 12.3
AZUL4, 26.78
GOLL4, 17.84
JBSS3, 21.6
SHUL4, 12.48
JSLG3, 30.4
SCAR3, 37.4
TRIS3, 11
KEPL3, 39.11
POMO3, 2.63
TGMA3, 22.57
FESA4, 17.73
ALSO3, 24.75
CGRA4, 26.65
LEVE3, 16.81
ENEV3, 50.43
...
```

## Moving forward
Now that we can read at least a sample spreadsheet lets do more, but to do it, we'll start to use our [documentation](./doc/intro.md) see you there.

For more examples, look at my [YouTube channel](https://www.youtube.com/channel/UCH6lFcii0mXxcZkDg9AUurw)

### Tests
```
lein test
```

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
