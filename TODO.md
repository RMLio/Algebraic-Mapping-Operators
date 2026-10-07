# TODO

## Fix now

## Fix later

- [ ] Run every test class in CI: the matrix in `.gitlab-ci.yml` omits `NodeTests`, `CollectionNodeTest`, `ExtendMultiValueTest`, `ExpressionFieldTest`, `FieldReuseTest`, `MissingReferenceTest` and `BlocksIOTest`. Add them, or derive the matrix from the test sources.
- [ ] Decide on `LeftJoinTest`, which is `@Disabled` while waiting for a go-ahead.
- [ ] Decide on the two `@Disabled` timing tests in `performance/bgp/BGPReplaceTest`.

## To triage
