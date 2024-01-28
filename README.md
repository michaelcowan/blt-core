# blt-core
> A selection of core utilities with zero dependencies

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/michaelcowan/blt-core/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/michaelcowan/blt-core/tree/master)

# Installation

## Maven

The library is available via [Maven Central](https://central.sonatype.com/artifact/io.blt/blt-core)

e.g., to add the core library to your dependencies:

```xml
<dependency>
    <groupId>io.blt</groupId>
    <artifactId>blt-core</artifactId>
    <version>1.0.7</version>
</dependency>
```

# Documentation

## API Docs

[API docs](https://michaelcowan.github.io/blt-core/apidocs) are available for the latest release of the library.

## Examples

### `Obj`

> Static utility methods for operating on `Object`
> 
> Includes object construction, mutation, fallbacks and validation

#### `tap` and `poke`

Return a passed or supplied value after mutating via a (throwing) consumer.

These can be handy when building an object without the need of a helper method:

```java
var user = Obj.tap(User::new, u -> {
    u.setName("Greg");
    u.setAge(15);
});
```

Or mutating an object to be passed as a parameter without the noise of a temporary variable:

```java
repository.persist(Obj.poke(user, u -> u.createDate(now())));
```

Any exception thrown by a consumer will bubble up and be thrown by `tap` or `poke`.

#### `orElseGet`

Returns a passed value if non-null, otherwise invokes and returns the result of a (throwing) supplier.

This is very similar to `Optional.ofNullable(...).orElseGet(...)` but more concise and supports suppliers that throw:

```java
private URL homepageOrDefault(URL homepage) throws MalformedURLException {
    return Obj.orElseGet(homepage, () -> new URL("https://google.com"));
}
```

#### `orElseOnException`

Invokes and returns the result of a supplier, unless it throws an exception, in which case a passed value is returned. 
e.g.,

```java
private InputStream openFileOrResource(String name) {
    return Obj.orElseOnException(
            () -> new FileInputStream(name),
            getClass().getResourceAsStream(name));
}
```

#### `throwIf` and `throwUnless`

Throws a provided exception if the given `value` satisfies (or doesn't satisfy) the provided `predicate`.
e.g.,

```java
public Map<String, String> loadProperties() {
    return Obj.throwIf(Properties.loadFromJson(FILENAME), Map::isEmpty,
            () -> new IllegalStateException("Properties must not be empty"));
}
```

#### `newInstanceOf`

Creates a new instance of the same type as the input object if possible, otherwise, returns empty. e.g.,

```java
public <K, V> Map<K, V> mapOfSameTypeOrHashMap(Map<K, V> map) {
    return Obj.newInstanceOf(map).orElse(new HashMap<>());
}
```

### `Ctr`

> Static utility methods for operating on `Collection` and `Map` i.e., Containers
> 
> For methods that accept and return a container, the result will be of the [same type if possible](#newInstanceOf)

#### `transformValues`

Returns a new `Map` containing the entries of another with a transform applied to the values. 
If possible, the returned `Map` is of the same type as the passed `Map`.
e.g.,

```java
var birthdays = Ctr.transformValues(
        Map.of("Greg", Month.NOVEMBER, "Phil", Month.OCTOBER, "Louis", Month.FEBRUARY), 
        e -> e.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
```

#### `computeIfAbsent`

Computes a value for a `Map` if one is not currently present and returns the value.

This is very similar to `map.computeIfAbsent(...)` but supports both functions and suppliers as well as exceptions:

```java
public String fetch(URL url) throws IOException {
    return Ctr.computeIfAbsent(cache, url, () ->
            IOUtils.toString(url, StandardCharsets.UTF_8));
}
```

### `Ex`

> Static utility methods centred around `Exception` and `Throwable`
> 
> Includes raising and handling exceptions

#### `transformExceptions`

Executes a function, transforming any thrown checked exceptions (i.e., `Exception`).
Unchecked exceptions (i.e., `RuntimeException`) will bubble up unaltered.

This can be useful when a method or lambda throws many checked exception types which should be mapped to a higher-level
exception.

e.g., say we have a custom `XmlProcessingException` that we want to raise for any exception related to parsing XML:

```java
public Document parseXml(String pathname) throws XmlProcessingException {
    return Ex.transformExceptions(
            () -> DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()       // throws ParserConfigurationException
                    .parse(new File(pathname)), // throws SAXException, IOException
            XmlProcessingException::new);
}
```

### `SingletonCollectors`

Implementations of `Collector` that reduce to exactly one or zero elements.

Useful when it is not valid for more than one element to be present:

```java
var activeStep = sequentialSteps.stream()
        .filter(SequentialStep::isActive)
        .collect(SingletonCollectors.toNullable());
```

```java
var maybeActiveStep = sequentialSteps.stream()
        .filter(SequentialStep::isActive)
        .collect(SingletonCollectors.toOptional());
```
