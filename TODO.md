# TODO

## Fix now

## Fix later

- [ ] Run every test class in CI: the matrix in `.gitlab-ci.yml` omits `NodeTests`, `CollectionNodeTest`, `ExtendMultiValueTest`, `ExpressionFieldTest`, `FieldReuseTest`, `MissingReferenceTest` and `BlocksIOTest`. Add them, or derive the matrix from the test sources.
- [ ] Decide on `LeftJoinTest`, which is `@Disabled` while waiting for a go-ahead.
- [ ] Decide on the two `@Disabled` timing tests in `performance/bgp/BGPReplaceTest`.
- [ ] Fix the SpotBugs findings (`mvn compile spotbugs:check`: 34, mostly `SE_NO_SERIALVERSIONID`, `EI_EXPOSE_REP`/`EI_EXPOSE_REP2` and `CT_CONSTRUCTOR_THROW`).
- [ ] Format the Java sources once with `mvn spotless:apply` (68 of 75 files deviate).

## To triage
