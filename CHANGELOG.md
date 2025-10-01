# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

## [2.0.1] - 2025-10-01

### Fixed
- Use DataIO's `JSONSourceIterator` instead of "manual" parsing; it already handles exceptions.
- Update dependency on DataIO to 2.1.5 for fix in handling empty XML and CSV records and address some vulnerabilities 

## [2.0.0] - 2025-09-16

### Changed
- Operators now each have a set of input- and output fragments.
- RDFNode: remove method `getStringRepr()`; use `toString()`.
- TargetOperator works in `String`s now

### Removed
- Operator builders; use their constructors.

### Fixed
- Generation of fields for CSV source, if not given.
- Removed unused code
- LiteralNode value - datatype - language stuff
- Updated Jena to version 5.5.0

## [1.0.0] - 2025-09-03

### Added
- A set of operators

[2.0.1]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v2.0.0...v2.0.1
[2.0.0]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v1.0.0...v2.0.0
[1.0.0]: https://github.com/RMLio/Algebraic-Mapping-Operators/releases/tag/v1.0.0
