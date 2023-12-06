# blt-core
> A selection of core utilities that pull in minimal dependencies (if any)

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/michaelcowan/blt-core/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/michaelcowan/blt-core/tree/master)

# Installation

## Maven

The library is available via [Maven Central](https://central.sonatype.com/artifact/io.blt/blt-core)

e.g., to add the core library to your dependencies:

```xml
<dependency>
    <groupId>io.blt</groupId>
    <artifactId>blt-core</artifactId>
    <version>1.0.3</version>
</dependency>
```

# Documentation

## API Docs

[API docs](https://michaelcowan.github.io/blt-core/apidocs) are available for the latest release of the library.

## Examples

### `Obj`

> Static utility methods for operating on `Object`

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
e.g.

```java
private InputStream openFileOrResource(String name) {
    return orElseOnException(
            () -> new FileInputStream(name),
            getClass().getResourceAsStream(name));
}
```

#### `newInstanceOf`

Creates a new instance of the same type as the input object if possible, otherwise, returns empty. e.g.

```java
public <K, V> Map<K, V> mapOfSameTypeOrHashMap(Map<K, V> map) {
    return newInstanceOf(map).orElse(new HashMap<>());
}
```

### `Ctr`

> Static utility methods for operating on `Collection`
> 
> For methods that accept and return a container, the result will be of the [same type if possible](#newInstanceOf)

#### `transformValues`

Returns a new `Map` containing the entries of another with a transform applied to the values. 
If possible, the returned `Map` is of the same type as the passed `Map`.
e.g.

```java
var birthdays = transformValues(
        Map.of("Greg", Month.NOVEMBER, "Phil", Month.OCTOBER, "Louis", Month.FEBRUARY), 
        e -> e.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
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
