# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

### Added
- Tests covering a reference that matches nothing, for both a field and its subfields. A JSONPath is read with `REQUIRE_PROPERTIES`, so a missing property throws rather than returning nothing; the field binds no value and the rest of the record is still read.

### Fixed
- A value containing a `$` no longer aborts the mapping
- A value containing a `\` is no longer silently dropped

## [4.0.0] - 2026-07-30

### Changed
- **Breaking**: a source field is now either an `IteratorField` or an `ExpressionField`, mirroring MappingLoom's field model. `ConstantField` and `ReferenceField` are replaced by `ExpressionField`, whose expression can be a reference, a constant, or any other function. `FieldBuilder.withReference()` and `withConstant()` are kept as shorthands for the corresponding expression.
- An `ExpressionField` whose expression produces several values produces one record per value, the way an `IteratorField` does.
- An expression is applied to CSV, JSON and XML records alike; it used to be supported for CSV only.
- An expression field applies its subfields to the values it produces, as a reference field already did.

### Added
- `ExtendFunction.asReference()`, telling a field that the function is a bare reference into the record. Such a reference is read straight from the record, which is what allows a path to match several values (a JSON array, an XML node list).

### Fixed
- The subfields of an XML field read the matched element instead of its text content, the way an `IteratorField` already handed its subfields the element. An XPath cannot be applied to text, so a subfield of an XML field used to fail with a Saxon error (`SXXP0003 Content is not allowed in prolog`) whenever the field's path matched an element. The field itself still binds the element's text, so a field without subfields is unaffected.

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

[4.0.0]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v3.0.0...v4.0.0
[3.0.0]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v2.0.3...v3.0.0
[2.0.3]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v2.0.2...v2.0.3
[2.0.2]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v2.0.1...v2.0.2
[2.0.1]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v2.0.0...v2.0.1
[2.0.0]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v1.0.0...v2.0.0
[1.0.0]: https://github.com/RMLio/Algebraic-Mapping-Operators/releases/tag/v1.0.0
