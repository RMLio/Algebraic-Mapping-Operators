# Algebraic Mapping Operators Handbook

Written for a CS student who wants to understand the Algebraic Mapping Operators library as code.

## Contents

- [Preface](#preface)
- [Agent request contract (for AI agents/LLMs)](#agent-request-contract-for-ai-agentsllms)
- [Architecture](#architecture)
- [Build and test](#build-and-test)
- [Test resources](#test-resources)
- [Release process](#release-process)

## Preface

Algebraic Mapping Operators (AMO) is a Java 21 library (`be.ugent.idlab.knows:algebraic-mapping-operators`) that implements the operators of the mapping algebra described in the papers linked from `README.md`. A mapping plan is a graph of these operators: Source operators as leaves, intermediate operators (Project, Extend, Fragment, Rename, joins, serializers) as inner nodes, and a Target operator writing the result. The library supplies the building blocks and the operators; the engine that builds a plan from a mapping language (such as RML) and executes it lives in a separate project. The library has no command-line interface.

Where things live:

- `src/main/java/be/ugent/idlab/knows/amo/` holds all source code (package layout in [Architecture](#architecture)).
- `src/test/java/be/ugent/idlab/knows/amo/` holds the JUnit 5 tests, mirroring the main package layout, plus the `utilities/BlocksIO` test helper.
- `src/test/resources/` holds the JSON, CSV and XML fixtures the tests read.
- `pom.xml` is the Maven build; `.gitlab-ci.yml` is the CI pipeline; `bump-version.sh` drives releases.
- `README.md` explains the algebra concepts and how to include the library; `CHANGELOG.md` follows Keep a Changelog.

## Agent request contract (for AI agents/LLMs)

<!-- software-handbook contract: 2026-10-08 -->

Every implementation request handled by an AI agent/LLM follows these constraints:

- If the request is a feature or bugfix:
  - fix the specific failing case or issue named in the request;
  - preserve existing passing behavior unless explicitly asked not to;
  - add or update a regression test when needed.
- Make the smallest coherent patch. A documentation error found along the way is fixed in the same patch.
- Leave the code leaner after every request: remove what the change makes redundant (duplicate tests, parameters and options that no longer do anything, helpers that duplicate each other, comments that only repeat the code), and reuse shared functionality instead of adding a local variant. Use SpotBugs (`mvn compile spotbugs:check`), compiler warnings (`mvn compile`) and IDE inspection to find unused code, and keep Javadoc valid, since CI runs a Javadoc check.
- Fix a transient environment problem (a stale PATH, a shell or editor that needs a restart) in the environment, by restarting or reconfiguring it; add no code that works around it.
- **Push back** when a request would violate an established principle (e.g. breaking test hermeticity). Explain the principle and suggest a documentation-only fix instead of silently implementing the harmful change.
- Update this handbook so the change is documented as well as implemented.
  - Document only the latest state, integrated in the surrounding narrative (principles, behavior, rationale), including the choices made and why.
  - This contract holds only general rules for handling a request; project-specific guidance goes in the chapter on that topic.
- Do not stop at making tests green; align the implementation with the specification or intended design, and document the semantic reason in this handbook.
- Never remove or change existing tests (code or fixtures) without explicit permission. A change to an existing fixture (expected output, input, or data) is validated by the maintainer before it is kept, also when a tool writes it: propose the change with its reason, and keep it only after approval.
- Update `CHANGELOG.md` for every change, internal ones included (tests, CI, refactoring, removed code): keep `## Unreleased` a short summary of what changed since the last release. A feature that is new since the last release is one Added line, which later fixes update instead of getting lines of their own.
- Before a release, propose a review of everything changed since the previous release: the code for correctness, and the documentation and changelog for accuracy and brevity.
- Check whether `README.md` needs updates for user-visible behavior or workflow changes, and update it when needed.
- Write documentation (this handbook, READMEs, `TODO.md`, `CHANGELOG.md`, code comments) as plain positive statements: say what is true and leave out the contrast ("X, not Y"). Keep a negative only when it is the point itself, such as a prohibition, a warning, or a known limitation.
- If there are difficulties during fulfillment, document them in the most appropriate existing handbook location (create a new chapter only when truly necessary) so future requests start with better context.
- A preference or principle that the maintainer states while handling a request is documented so that every later request follows it: a general one in this contract (and in the software-handbook skill it comes from), a project-specific one in the handbook chapter it belongs to. When it is unclear which, ask.
- When a request is a list of feedback (such as a `TODO.md`), clean up after handling it: remove the items that are done, keep every open item as a clear task (an open question or an offered follow-up is an open item), and remove temporary files created along the way.

## Architecture

All code is under the package `be.ugent.idlab.knows.amo`.

- `blocks/` holds the data model of the algebra.
  - `SolutionMapping` is a `HashMap<String, RDFNode>` from variable names (such as `?name`) to values, with `union` and compatibility checks used by the joins.
  - `MappingTuple` maps fragment names to a multiset of solution mappings (backed by `util/Multimap`).
  - `BGP` is a Basic Graph Pattern parsed from a string; `apply(SolutionMapping)` replaces its variables and returns a Jena `DatasetGraph`.
  - `nodes/` holds the value types: `IRINode`, `BlankNode`, `LiteralNode`, `NullNode` and `CollectionNode` (several terms standing as one value, as produced by a multi-valued function; it has no Jena node of its own). `LiteralNode` lets Jena derive the XSD datatype from the Java value when none is given.
- `functions/` holds the pluggable functions operators are parameterised with: `ExtendFunction`, `FragmentFunction`, `JoinCondition` and `TargetSink`.
- `operators/` holds the operators. `Operator` is the abstract base, with a name and input/output fragment sets, and accepts an `OperatorVisitor` with one method per kind (`visitSource`, `visitUnary`, `visitBinary`, `visitTarget`); an engine walks a plan through this visitor.
  - `source/SourceOperator` produces mapping tuples. `source/dataio/` implements it on top of the KNoWS `dataio` library: `CSVSourceOperator` (optionally with a CSVW dialect), `JSONSourceOperator` and `XMLSourceOperator`, with `fields/` describing how records are read into variables (`ReferenceExpression`, `ConstantExpression`, `IteratorField`, `ExpressionField`, `RecordReader`).
  - `intermediate/unary/` holds the operators on one input: `ProjectOperator`, `ExtendOperator`, `FragmenterOperator`, `RenameOperator`, `SerializeOperator` (fills a BGP) and `TemplateSerializer` (fills a string template once per combination of collection members).
  - `intermediate/binary/` holds the joins: `NaturalJoinOperator`, `ThetaJoinOperator`, `LeftJoinOperator`, `CrossJoin` and `JoinOperator`, typed by `BinaryType`.
  - `target/TargetOperator` writes the serialized variable (`?serialized_output` by default) of each mapping tuple to a `TargetSink`, optionally through `postprocessing/RDFFormatter`.

Null-safety is annotated with JSpecify (`@NonNull`, `@Nullable`, `@NullMarked`). Main dependencies are Apache Jena ARQ, KNoWS `dataio`, json-smart and the SLF4J API.

## Build and test

- Build: `mvn compile`; package the library jar with `mvn package`.
- Run all tests: `mvn test`. Run one class: `mvn -Dtest=ProjectTest test`.
- The dataio version is the property `dataio.version`. It defaults to a dataio release on Maven Central (2.4.0), so the committed build and CI resolve everything from Maven Central. A local build against a development version overrides it, e.g. `mvn install -Ddataio.version=2.4.1-SNAPSHOT` after installing that dataio locally.
- Surefire's default excludes are cleared in `pom.xml` so that JUnit 5 `@Nested` test classes run.
- CI (`.gitlab-ci.yml`, image `maven:3-eclipse-temurin-21`) has a lint stage from the shared `rml/util/ci-templates` project that checks that `CHANGELOG.md` is updated and that the Javadoc builds, and a unit-test stage that runs a parallel matrix with one job per test class via `mvn -Dtest="$TEST" test`. A test class runs in CI only when it is listed in that matrix; `TODO.md` lists the omitted classes, which run with a local `mvn test`.
- `TODO.md` lists the `@Disabled` tests.
- Linter: SpotBugs 4.10.3 is configured in `pom.xml` under `pluginManagement` and is not bound to a build phase, so findings never fail `mvn verify`. Run it with `mvn compile spotbugs:check`; it analyses the compiled main classes and exits with an error while findings exist.
- Formatter: Spotless 3.10.3 with palantir-java-format 2.102.0 is configured in `pom.xml` under `pluginManagement`, next to SpotBugs and likewise not bound to a build phase. `mvn spotless:check` reports the Java sources that deviate from the format and `mvn spotless:apply` reformats them in place. Palantir-java-format is a Java-native formatter (4-space indent, line width 120, lambda- and stream-friendly line breaking), so it runs in seconds without Node.js.

## Test resources

Operator tests compare an operator's output with an expected fixture. Fixtures are JSON serializations of `SolutionMapping` and `MappingTuple`, read with `BlocksIO.readSolutionMapping` and `BlocksIO.readMappingTuple`; paths are relative to `src/test/resources/` and resolved from the working directory, so tests run from the repository root. The JSON format, including the `collection` type, is documented in `src/test/java/be/ugent/idlab/knows/amo/utilities/README.md`.

Layout of `src/test/resources/`:

- `operators/<operator>/<case>/` holds `input.json` and `output.json` per test case (extend, fragment, project, rename, naturalJoin, leftJoin, thetaJoin, serialize, target).
- `operators/source/{csv,json,xml}/` holds the raw source data read by the source operator tests, with expected outputs in `operators/source/`.
- `serialization/` holds fixtures for `BlocksIOTest`, including malformed inputs under `solution_map/errors/`.
- `blockTests/bgp/` and `performance/bgp/` hold BGP fixtures.

## Release process

Step-by-step instructions are in [RELEASE.md](RELEASE.md); this section explains the tooling.

Publishing to Maven Central uses the `release` Maven profile (sources jar, Javadoc jar, GPG signing, `central-publishing-maven-plugin` with auto-publish) driven by the shared `Maven-Central` CI template, with credentials from `.m2/settings.xml` (`MAVEN_REPO_USER`, `MAVEN_REPO_PASS`). The jar contains only this library; the POM declares its dependencies. A version `testrelease-<name>` is tagged with its bare name and stays the current version afterwards, for trying out the pipeline.
