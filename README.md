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
    <version>1.0.2</version>
</dependency>
```

# Documentation

## API Docs

[API docs](https://michaelcowan.github.io/blt-core/apidocs) are available for the latest release of the library.

## Examples

### `Obj`

#### `tap` and `poke`

Return a passed or supplied value after mutating via a consumer.

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

#### `orElseGet`

Returns a passed value if non-null otherwise invokes and returns the result of (throwing) supplier.

This is very similar to `Optional.ofNullable(...).orElseGet(...)` but more concise and supports suppliers that throw:

```java
private URL homepageOrDefault(URL homepage) throws MalformedURLException {
    return Obj.orElseGet(homepage, () -> new URL("https://google.com"));
}
```

### `Str`

#### `requireNotEmpty`

Checks that the specified `string` is not `null` or empty (length == 0).

This is similar to `Objects#requireNonNull(...)` but also checks `String#isEmpty()`.

If `null` `NullPointerException` is thrown.\
If empty `IllegalArgumentException` is thrown.

```java
public User(String name) {
    this.name = Str.requireNotEmpty(name, "'name' must not be empty");
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
