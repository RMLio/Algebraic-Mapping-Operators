# Algebraic Mapping Operators

A library of Algebraic Mapping Operators. These operators serve the purpose of constructing a mapping language-independent mapping plan.

This document contains an introduction and explanation for the operators, as first described [here](https://ceur-ws.org/Vol-3632/ISWC2023_paper_412.pdf) and further finalized in [this paper](http://doi.org/10.1007/978-3-031-94575-5_1).
## Building blocks
### Mapping plan
A Mapping Plan is a graph consisting of operators that can be used to perform a mapping. This plan has a Serialize operator as its root, with Source operators as leafs.
Inner nodes of this graph can be different intermediate operators that perform operations on the mapping.

### Solution Mapping
Mathematical definition: a partial function $\mu$ mapping from the set of variables (V) to the set of data values (D)

In its essence, a solution mapping boils down to a key-value pair containing the name of the variable and its value. 
It's Java equivalent is Map.Entry. It is used to realize string templates, replacing variables placed in them with the values.

### Fragment
Mathematical definition: a grouping of a multiset of solution mappings.

In its essence, a fragment is a set of key-value pairs containing names of the variables in the dataset and their values. 
The simplest way to model a fragment is a dictionary. 

### Mapping tuple
Mathematical definition: a partial multivalued function that maps fragments to solution mappings. 

As such, it is a filter for the key-value pairs contained in the fragment.

## Operators
Using the building blocks above, we can now define operators that work on these.
### Source operator
A Source operator generates mapping tuples from heterogenous data sources. 
The inner working relies on configuration _C_, a root iterator _r_ and a set of subiterator _I_. 

Configuration _C_ contains metadata required for the consumption of the data source (think of URL to a database, a file path...).
Using iterators, data sources can further be queried to generate individual data records, on which the mappings will be performed.
The use of a root iterator _r_ together with subiterators _I_ allows for nested querying. 

Source operator produces a multiset of mapping tuples: a set of filters to be applied on the original fragments.
In this set, the default fragment, f_0, is mapped onto a multiset of solution mappings.

To sketch an example: suppose following JSON document
```json
{
  "peoples": [
    {
      "name": "John Doe",
      "age": 23,
      "email": "john.doe@example.com",
      "pet": {
        "type": "dog",
        "name": "Bax"
      }
    },
    {
      "name": "Susan Sue",
      "age": 23,
      "email": "susan.sue@example.com"
    }
  ]
}
```

A Source operator's fields could look like following:
- C: path to the JSON file
- r: `$.peoples[*]`
- I: `[$.pet.type, $.pet.name]`

Applying this Source operator would produce results similar to ones found in Table 2 of the paper:

| fragment | solution mapping | ?name     | ?age | ?email                | ?$pet.type | ?$pet.name |
|----------|------------------|-----------|------|-----------------------|------------|------------|
| default  | m1               | John Doe  | 23   | john.doe@example.com  | dog        | Bax        |
| default  | m2               | Susan Sue | 25   | susan.sue@example.com |            |            |


### Project operator
The Project operator will restrict the solution mappings to a set of attributes provided. This can be used to reduce the number of data that needs to be processed by later operators.

The operator will accept a set of variables P and will restrict the solution mappings to only contain variables in P.

### Extend operator
The Extend operator will derive new values from existing values in the data record.

When provided with a particular expression, the Extend operator will evaluate it and bind the results to a new variable.

### Fragment operator
The Fragment operator fragments a mapping tuple into a new fragment f_new, using a partial transformation function.

The function is applied on the mapping tuple and on all mapping tuples within a mapping tuple multiset.

### Join operators
Join operators can be used to combine different multisets together.

#### Natural Join
Natural join will produce mapping tuples that are combinations of mapping tuples coming from different multisets.
Only the tuples that are equal on their fragments and all common variables in the underlying solution mappings will be combined.
Only equality is checked, no further predicates such as "less than or equal to".

#### Theta Join
A more general version of natural join that performs joining based on a predicate Theta.

Solution mappings s1, s2 will thus only be joined if the Theta(s1, s2) evaluates to true.

Mapping tuples t1, t2 will only be joined if Theta(s1, s2) will evaluate to true for all solution mappings s1, s2 in all fragments in the mapping tuples. 

### Further operators
Further operators will be implemented as they're defined. The above is only a subset of the complete algebra.

## Project status
This library is currently in alpha state and under active development.

## How to include

### Maven

```xml
<dependency>
    <groupId>be.ugent.idlab.knows</groupId>
    <artifactId>algebraic-mapping-operators</artifactId>
    <version>1.0.0</version>
</dependency>
```