# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

## [3.0.0] - 2026-07-28

### Added
- `ExtendFunction.applyMulti()`, returning all values a function produces. A Reference or a Constant yields a single value, so the default implementation wraps `apply()`; functions that produce several values override it, allowing the field they belong to to produce one record per value.
- Source fields accept an expression (`FieldBuilder.withExpression()`): a function applied to the record data, whose result becomes the field's value. Used by logical-view fields whose value is computed, e.g. `toUpperCase(name)`.

## [2.0.3] - 2026-07-07

### Fixed
- remove check on common attributes in union method of `SolutionMapping`
- Updated dependency on dataio

## [2.0.2] - 2025-10-02

### Fixed
- `TempateSerializer` and `TargetOperator`: check for null values (and ignore them)

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

[3.0.0]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v2.0.3...v3.0.0
[2.0.3]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v2.0.2...v2.0.3
[2.0.2]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v2.0.1...v2.0.2
[2.0.1]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v2.0.0...v2.0.1
[2.0.0]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v1.0.0...v2.0.0
[1.0.0]: https://github.com/RMLio/Algebraic-Mapping-Operators/releases/tag/v1.0.0
