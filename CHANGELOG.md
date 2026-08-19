# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

### Added
- A `CSVSourceOperator` may be given a `CSVWConfiguration`, the dialect its rows are written in, and reads the source through a `CSVWSourceIterator` instead of the plain one. A source that says how it is written — a CSV on the Web table naming its delimiter, quote character, encoding or null values — is read the way it says. Without a dialect nothing changes: a plain CSV is a CSVW read with the defaults, which is what dataio's `CSVSourceIterator` already is.
- A solution mapping read from JSON may hold a collection, written as `{"type": "collection", "value": [<term>, ...]}`, so that the operator test fixtures can carry one. Its members are terms themselves and may be of any type.
- `CollectionNode`, a value standing for several RDF terms. A function producing more than one value, as a split does, returns them as one value, so that a function taking the result as an argument sees all of them. A collection is not an RDF term itself and has no Jena node.

### Fixed
- Git ignore `pom.xml.versionsBackup`

### Changed
- A `TemplateSerializer` fills a template in once for every combination of the terms its variables stand for: a variable holding a collection gives a statement per member, and several such variables multiply out. A variable used twice takes the same term both times, and an empty collection states nothing.
- `bump-version.sh`: added option to also commit changes and tag (necessary to make a release)

## [4.1.0] - 2026-08-11

### Changed
- An `IteratorField` builds one source iterator and points it at each object it reads, instead of building one per object: its reference formulation and iterator expression never change, so the iterator's expression is compiled once. Reading 300 objects of 10 records measured ~2x faster for XML and ~6x faster for JSON. Requires the `SourceIterator.reset(Access)` added in dataio 2.3.1.
- An `ExpressionField` reads its records through a reader of its own, which keeps its source iterators between records rather than building one per value read. A field's subfields are read once per matched record, so this is where an XML field spent most of its time: reading 300 objects of 10 records with one subfield measured ~2x faster again, on top of the above. JSON is unaffected, as its reads never went through a source iterator.
- A field is stateful while it reads, as it now keeps a parser over the object being read. Fields were already applied one object at a time by the source operators, but a `Field` instance must no longer be shared between operators running concurrently.
- An `ExtendOperator` extending a variable with a function that produces several values now gives a mapping per value, through the new `applyMulti()`. A rule using that variable is applied to every value instead of only the first, which is what a split in an object map needs. Several such variables multiply out.
- Tests covering a reference that matches nothing, for both a field and its subfields. A JSONPath is read with `REQUIRE_PROPERTIES`, so a missing property throws rather than returning nothing; the field binds no value and the rest of the record is still read.

### Added
- `ExtendFunction.applyMultiToNode()`, giving a node per value a function produces. The counterpart of `applyMulti()` for the places that build terms rather than read values.

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

[4.1.0]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v4.0.0...v4.1.0
[4.0.0]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v3.0.0...v4.0.0
[3.0.0]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v2.0.3...v3.0.0
[2.0.3]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v2.0.2...v2.0.3
[2.0.2]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v2.0.1...v2.0.2
[2.0.1]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v2.0.0...v2.0.1
[2.0.0]: https://github.com/RMLio/Algebraic-Mapping-Operators/compare/v1.0.0...v2.0.0
[1.0.0]: https://github.com/RMLio/Algebraic-Mapping-Operators/releases/tag/v1.0.0
