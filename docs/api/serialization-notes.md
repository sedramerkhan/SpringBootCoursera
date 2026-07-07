# JSON & XML Serialization — Notes

How in-memory objects become data that can be stored, transmitted, and rebuilt
across different platforms and languages.

## What serialization is

**Serialization** = converting an object/data structure into a format that can be
stored or transmitted, then later reconstructed. The flow:

1. **Object state** — the in-memory data to capture (e.g. a user's details).
2. **Conversion** — encode it into JSON, XML, or binary, depending on where it's used.
3. **Transfer** — send it over an API, save it to a file, or move it across a network.
4. **Deserialization** — convert the serialized data back into its original object
   form so the app can use it again.
5. **Cross-platform use** — a program in one language can serialize data that a
   program in another language deserializes and understands.

It's the foundation for data sharing in distributed systems, APIs, and modern apps.

## JSON (JavaScript Object Notation)

One of the most widely used exchange formats today.

- **Structure** — simple **key–value pairs** (e.g. `"name": "Alice"`); also
  supports arrays and nested objects, so it can represent complex data.
- **Language independent** — despite the name, works in Python, Java, C#, and
  almost any modern language.
- **Advantages** — lightweight and fast to parse, human-readable at a glance,
  widely supported across frameworks and tools.
- **Used in** — REST APIs, client–server communication, config files, web apps.

```json
{ "name": "Alice", "age": 30, "roles": ["admin", "editor"] }
```

## XML (Extensible Markup Language)

A flexible, structured format for data representation and document storage.

- **Structure** — hierarchical, **tag-based** (opening/closing tags, like HTML);
  supports attributes and nested elements for detailed description.
- **Self-descriptive** — the tags themselves say what the data represents.
- **Advantages** — highly structured and reliable, platform/language-independent,
  well-suited to complex data needing extra metadata.
- **Used in** — SOAP-based web services, config files, document exchange, legacy
  enterprise systems.

```xml
<user><name>Alice</name><age>30</age><roles><role>admin</role></roles></user>
```

## JSON vs XML

| | **JSON** | **XML** |
|---|---|---|
| **Format** | lightweight, key–value | verbose, tag-based hierarchy |
| **Readability** | concise, human-friendly | more verbose, harder to read |
| **Data types** | native numbers, strings, arrays, nested objects | text-based — numbers/arrays need extra parsing or conventions |
| **Schema** | less verbose but weaker schema enforcement | strong, strict structure/schema support |
| **Usage** | REST APIs, config, most web apps | SOAP services, document storage, legacy enterprise |
| **Performance** | faster parsing, less overhead | slower — heavy tags, more processing |

**Bottom line:** JSON is the default for modern apps (light, fast, readable); XML
stays strong where **strict structure, metadata, or legacy/SOAP systems** are
involved.