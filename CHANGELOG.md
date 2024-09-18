# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Fixed
- Fixed the backward compatibility for clients that do not transmit the `apiLevel` field
  (issue [eclipse-keyple/keyple-distributed-remote-java-lib#15]).

## [2.5.0] - 2024-09-06
:warning: API level: `3`
### Added
- Added a property indicating if a local reader is contactless or not.

## [2.4.0] - 2024-06-03
### Added
- Allows configuration of the timeout value used by server nodes (issue [#13]).
### Changed
- Logging improvement.

## [2.3.1] - 2024-04-12
### Changed
- Java source and target levels `1.6` -> `1.8`
### Upgraded
- Keyple Util Lib `2.3.1` -> `2.4.0`
- Gradle `6.8.3` -> `7.6.4`

## [2.3.0] - 2023-11-28
### Added
- Added a property indicating the Distributed JSON API level in exchanged JSON data (current value: `"apiLevel": 2`).
- Added project status badges on `README.md` file.
### Fixed
- CI: code coverage report when releasing.
### Upgraded
- Keyple Util Library `2.3.0` -> `2.3.1` (source code not impacted)

## [2.2.0] - 2023-04-04
### Added
- `CHANGELOG.md` file (issue [eclipse-keyple/keyple#6]).
- CI: Forbid the publication of a version already released (issue [#6])
### Changed
- All JSON property names are now "lowerCamelCase" formatted.
### Upgraded
- "Keyple Util Library" to version `2.3.0`.
- "Google Gson Library" (com.google.code.gson) to version `2.10.1`.

## [2.0.0] - 2021-10-06
This is the initial release.
It follows the extraction of Keyple 1.0 components contained in the `eclipse-keyple/keyple-java` repository to dedicated repositories.
It also brings many major API changes.

[unreleased]: https://github.com/eclipse-keyple/keyple-distributed-network-java-lib/compare/2.5.0...HEAD
[2.5.0]: https://github.com/eclipse-keyple/keyple-distributed-network-java-lib/compare/2.4.0...2.5.0
[2.4.0]: https://github.com/eclipse-keyple/keyple-distributed-network-java-lib/compare/2.3.1...2.4.0
[2.3.1]: https://github.com/eclipse-keyple/keyple-distributed-network-java-lib/compare/2.3.0...2.3.1
[2.3.0]: https://github.com/eclipse-keyple/keyple-distributed-network-java-lib/compare/2.2.0...2.3.0
[2.2.0]: https://github.com/eclipse-keyple/keyple-distributed-network-java-lib/compare/2.0.0...2.2.0
[2.0.0]: https://github.com/eclipse-keyple/keyple-distributed-network-java-lib/releases/tag/2.0.0

[#13]: https://github.com/eclipse-keyple/keyple-distributed-network-java-lib/issues/13
[#6]: https://github.com/eclipse-keyple/keyple-distributed-network-java-lib/issues/6

[eclipse-keyple/keyple-distributed-remote-java-lib#15]: https://github.com/eclipse-keyple/keyple-distributed-remote-java-lib/issues/15

[eclipse-keyple/keyple#6]: https://github.com/eclipse-keyple/keyple/issues/6