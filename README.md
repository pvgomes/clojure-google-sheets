# clojure-google-sheets

A clojure google sheets integration example, based on [java quickstart start](https://developers.google.com/sheets/api/quickstart/java) with way more examples

[![Build Status](https://travis-ci.org/pvgomes/clojure-google-sheets.svg?branch=master)](https://travis-ci.org/pvgomes/clojure-google-sheets)

## Installation

1. Access your [google console developers](https://console.developers.google.com/) and turn on the Google Sheets API and get credentials.json, find for "Authorized redirect URIs" and add `http://localhost:8888`
2. Add your credentials.json on [resources](./resources) path (you can find [template.credentials.json](./resources/template.credentials.json) as an example there)
3. good to go, just run `lein run`
4. A browser will open requesting your auth, accept it
5. You'll see the output of [this spreadsheet](https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit#gid=0), it means that it worked, eg:
```
name, major
Alexandra, English
Andrew, Math
Anna, English
Becky, Art
Benjamin, English
Carl, Art
Carrie, English
Dorothy, Math
Dylan, Math
Edward, English
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
