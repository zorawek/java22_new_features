# Java 22 new features

## Unnamed variables
If variable is not used you can simply skip it by using `_` as variable name.
```java
for (var _ : List.of(1, 2, 3)) {
    System.out.println("Hello");
}
```

Remember pattern matching?
```java
if (o instanceof Location (var name, var gpsPoint)) { 
    System.out.println(name); 
}
```

we can ignore gpsPoint:

```java
if (o instanceof Location (var name, var _)) { 
    System.out.println(name); 
}
```

## Calling code before super
// Code example with a positive big integer

## Class-File API
Java support for reading, writing, and transforming Java class files. This is especially important for libraries like ASM, BCEL, or Javassist.

As java file format changes every six months, it is important to have a way to read and write class files.

## Stream Gatherers
Enhance the Stream API to support custom intermediate operations. This will allow stream pipelines to transform data in ways that are not easily achievable with the existing built-in intermediate operations.
https://openjdk.org/jeps/461

A gatherer represents a transform of the elements of a stream; it is an instance of the java.util.stream.Gatherer interface. Gatherers can transform elements in a one-to-one, one-to-many, many-to-one, or many-to-many fashion. They can track previously seen elements in order to influence the transformation of later elements, they can short-circuit in order to transform infinite streams to finite ones, and they can enable parallel execution. For example, a gatherer can transform one input element to one output element until some condition becomes true, at which time it starts to transform one input element to two output elements.

Build in gatherers:
- *fold* is a stateful many-to-one gatherer which constructs an aggregate incrementally and emits that aggregate when no more input elements exist.
- *mapConcurrent* is a stateful one-to-one gatherer which invokes a supplied function for each input element concurrently, up to a supplied limit.
- *scan* is a stateful one-to-one gatherer which applies a supplied function to the current state and the current element to produce the next element, which it passes downstream.
- *windowFixed* is a stateful many-to-many gatherer which groups input elements into lists of a supplied size, emitting the windows downstream when they are full.
- *windowSliding* is a stateful many-to-many gatherer which groups input elements into lists of a supplied size. After the first window, each subsequent window is created from a copy of its predecessor by dropping the first element and appending the next element from the input stream

## Launch Multi-File Source-Code Programs:
Allows users to run a program supplied as multiple files of Java source code without first having to compile it.

## Panama
https://openjdk.org/projects/panama/

### Interop in Java is hard
- JNI is hard to use
- JNA is slow (Java Native Access)
- JNR is not maintained (Java Native Runtime) https://github.com/jnr/jnr-ffi
- Panama is a new way to interact with native code

In JNI you need to wrap the native library in another native library, which is then wrapped in Java.
This is a lot of work and error-prone.

Example of a function wrapper in C:
```C
/*
 * Class:     Native
 * Method:    func
 * Signature: (ILjava/lang/String;JD)V
 */
JNIEXPORT void JNICALL Java_Native_func
  (JNIEnv *, jobject, jint, jstring, jlong, jdouble);
```

### Memory allocation
```java
MemorySegment buf1, buffer2;
// Create a confined arena, and all of its buffers will be freed after the try {} block.
try (Arena arena = Arena.ofConfined()) {
    // Allocate a 8byte buffer.
    buf1 = arena.allocate(8);
    // Allocate a 16byte buffer.
    buf2 = arena.allocate(16);
    // Store data
    buf1.set(type, offset, value);
    // Load data from the buffer at the given offset, starting with zero.
    buf1.get(type, offset); 
    // Not though bytes and overflow happens! IndexOutOfBound exception will be thrown.
    buf1.get(JAVA_DOUBLE, 5);
}
// Both buffers freed here.
buf1.get(...); /
```

### Basic data types
```java
try (Arena arena = Arena.ofConfined()) {
    MemorySegment buf = arena.allocate(8); // Buffers are zeroed

    // All of the following types use native endianness! Learn more at ValueLayout Javadoc.

    // Load the first 8 bytes as a signed long (int64)
    assert 0 == buf.get(ValueLayout.JAVA_LONG, 0);
    // Load the first 4 bytes as a signed int (int32)
    assert 0 == buf.get(ValueLayout.JAVA_INT, 0);
    // Load the next 4 bytes as a signed int (int32)
    assert 0 == buf.get(ValueLayout.JAVA_INT, 4);

    // Store an int32 into the first 4 bytes
    buf.set(ValueLayout.JAVA_INT, 0, 114514);
    assert 114514 == buf.get(ValueLayout.JAVA_INT, 0);

    // Store an int32 into the next 4 bytes
    buf.set(ValueLayout.JAVA_INT, 0, 1919810);
    assert 1919810 == buf.get(ValueLayout.JAVA_INT, 4);

    // Load as an int64 instead!
    buf.get(ValyeLayout.JAVA_LONG, 0);
}
```

### Extract a wrapper for Java
https://jdk.java.net/jextract/

jextract -d classes -t org.openjdk --include-function hello_world -l rust_panama_helloworld -- lib.h
