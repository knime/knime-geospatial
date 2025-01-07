# ![Image](https://www.knime.com/files/knime_logo_github_40x40_4layers.png) KNIME geospatial UI extensions

This repository contains the frontend of the KNIME geospatial UI extensions which are based on HTML/Typescript.
They are used in KNIME Analytics Platform and/or KNIME WebPortal.

## Development

### Prerequisites

- Install [Node.js][node], see version in [package.json](package.json).

Newer versions may also work, but have not been tested.

### Install dependencies

```sh
npm install
```

### Git hooks

When committing your changes, a couple of commit hooks will run via [husky].

- `pre-commit` hook to lint and format the changes in your stage zone (via [lintstaged])
- `prepare-commit-msg` hook to format your commit message to conform with the required format by KNIME. In order for this to work you must set environment variables with your Atlassian email and API token. Refer to `@knime/eslint-config/scripts/README.md` for more information.

### Testing

#### Running unit tests

This project contains unit tests written with [vitest]. They are run with

```sh
npm run test
```

You can generate a coverage report with

```sh
npm run coverage
```

The output can be found in the `coverage` folder. It contains a browseable html report as well as raw coverage data in
[LCOV] and [Clover] format, which can be used in analysis software (SonarQube, Jenkins, …).

### Running security audit

npm provides a check against known security issues of used dependencies. Run it by calling

```sh
npm run audit
```

## Building

To build all geospatial extensions:

```sh
npm run build
```

To build a single item, use e.g. the following command:

```sh
npm run build:GeoValueRenderer
```

Results are saved to `/dist`.

This project can also be built via a maven build wrapper

```sh
mvn clean install
```

# Join the Community!

- [KNIME Forum](https://forum.knime.com/)

[node]: https://knime-com.atlassian.net/wiki/spaces/SPECS/pages/905281540/Node.js+Installation
[Java]: https://www.oracle.com/technetwork/java/javase/downloads/index.html
[vitest]: https://vitest.dev/
[LCOV]: https://github.com/linux-test-project/lcov
[Clover]: http://openclover.org/
[husky]: https://www.npmjs.com/package/husky
[lintstaged]: https://github.com/okonet/lint-staged
