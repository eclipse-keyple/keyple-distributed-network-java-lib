[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=eclipse-keyple_keyple-distributed-network-java-lib&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=eclipse-keyple_keyple-distributed-network-java-lib)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=eclipse-keyple_keyple-distributed-network-java-lib&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=eclipse-keyple_keyple-distributed-network-java-lib)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=eclipse-keyple_keyple-distributed-network-java-lib&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=eclipse-keyple_keyple-distributed-network-java-lib)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=eclipse-keyple_keyple-distributed-network-java-lib&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=eclipse-keyple_keyple-distributed-network-java-lib)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=eclipse-keyple_keyple-distributed-network-java-lib&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=eclipse-keyple_keyple-distributed-network-java-lib)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=eclipse-keyple_keyple-distributed-network-java-lib&metric=coverage)](https://sonarcloud.io/summary/new_code?id=eclipse-keyple_keyple-distributed-network-java-lib)

# Keyple Distributed Network Java Library

## Overview

The **Keyple Distributed Network Java Library** contains the common network elements used by the [Keyple Distributed Local Java Lib](https://github.com/eclipse-keyple/keyple-distributed-local-java-lib) and [Keyple Distributed Remote Java Lib](https://github.com/eclipse-keyple/keyple-distributed-remote-java-lib) libraries provided by the **Keyple Distributed** solution.

## Versioning

The three libraries of the **Keyple Distributed** solution
([Network](https://github.com/eclipse-keyple/keyple-distributed-network-java-lib),
[Local](https://github.com/eclipse-keyple/keyple-distributed-local-java-lib) and
[Remote](https://github.com/eclipse-keyple/keyple-distributed-remote-java-lib)) form a single component split into
several artifacts: they share the same Java package and rely on internal (non-public) contracts of each other. Their
versions are therefore aligned according to the following rules:

- The three libraries always share the same **major** and **minor** version numbers (e.g. `2.6.x`). A new major or
  minor version is always released for the three libraries at the same time.
- **Patch** versions are independent: a library can be released alone to fix an issue (e.g.
  `keyple-distributed-local-java-lib` `2.6.1` used with `keyple-distributed-network-java-lib` `2.6.0`).
- Any change of an internal contract between the libraries requires a new minor version of the three libraries.

Applications must use the same major and minor versions for all the Keyple Distributed libraries they import (ideally
with the latest patch of each). The simplest way to do so is to import the
[Keyple Java BOM](https://github.com/eclipse-keyple/keyple-java-bom), which references a set of versions tested
together.

## Documentation & Contribution Guide

The full documentation, including the **user guide**, **download information** and **contribution guide**, is available on the Keyple website [keyple.org](https://keyple.org).

## API documentation

API documentation & class diagram is available online: [docs.keyple.org/keyple-distributed-network-java-lib](https://docs.keyple.org/keyple-distributed-network-java-lib)

## Examples

Examples of implementation are available in the following repository: [github.com/eclipse-keyple/keyple-java-example](https://github.com/eclipse-keyple/keyple-java-example)

## About the source code

The code is built with **Gradle** and is compliant with **Java 1.8** in order to address a wide range of applications.

## Continuous Integration

This project uses **GitHub Actions** for continuous integration. Every push and pull request triggers automated builds
and checks to ensure code quality and maintain compatibility with the defined specifications.
