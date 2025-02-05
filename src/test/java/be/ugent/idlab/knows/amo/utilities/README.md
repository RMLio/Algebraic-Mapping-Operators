# BlocksIO

The following document describes the serialization JSON format we use for `SolutionMapping` and `MappingTuple`. It is designed to be flexible and concise.

## SolutionMapping
A SolutionMapping is described as a JSON object. Keys of this object are the variables. Each key has a value of a JSON object consisting of at least one field `type`.

`type` (String) controls how the value will be modelled in SolutionMapping. It can be one of the following: `iri`, `blank`, `literal` or `null`.
- If the type is `blank`, then key `value` will be considered a label for the blank node.
- If the type is `null`, then key `value` is ignored and can be omitted from the specification, as a null node is rendered.
- Otherwise, the key `value` will be considered the value for the node.

`value` (String) contains the value associated with the key.

This serialization format furthermore allows for following optional fields:
- `datatype`(String):  determines the type of `value`. If omitted, `value` is assumed to be a String. Can be one of the XSD datatypes, as supported by Jena. 
- `language`(String): determines the language of `value`. Only really applicable if the value is a String Literal.

An example of a serialized `SolutionMapping`

```json
{
  "?literal": {
    "value": "bar",
    "type": "literal",
    "language": "be-nl"
  },
  "?lit_detatype": {
    "value": "0",
    "type": "literal",
    "datatype": "integer"
  },
  "?iri": {
    "value": "http://example.com",
    "type": "iri"
  },
  "?blank": {
    "value": "blankNode",
    "type": "blank"
  },
  "?null": {
    "value": "ignored",
    "type": "null"
  }
}
```

## MappingTuple
`MappingTuple` is a collection of fragments, each having 1 or more SolutionMappings. As such, serializing `MappingTuple`s relies heavily on `SolutionMapping` serialization.

A `MappingTuple` gets is serialized into a JSON object, where keys are the names of different fragments. Each key's value is either a JSON object in case the fragment only has a single `SolutionMapping` or a list of JSON objects, each in the format of the `SolutionMapping` as described above.

An example of a serialized MappingTuple

```json
{
  "f_single": {
    "?foo": {
      "value": "bar",
      "type": "literal"
    }
  },
  "f_multi": [
    {
      "?bar": {
        "value": "baz"
      }
    },
    {
      "?baz": {
        "value": "0",
        "type": "literal",
        "datatype": "integer"
      }
    }
  ]
}
```