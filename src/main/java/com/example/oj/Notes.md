#### Get logged-In user after loggin in:
```java
User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
```

#### Detecting zip file:
- `Tika.detect()` and `MultipartFile.getContentType()` seems to work correctly,
- `Files.probeContentType()` seems to have system dependent implementation, gave some different result
- `URLConnection.guessContentTypeFromStream()` does not work
- `MultipartFile.getContentType()` does not throw IOException

#### Default value of fields (Submission.fileName):

Default value does not work with `@NoArgsConstructor`. Create explicit constructor instead.

#### Global Exception Handling:

1. **Exception Interception:** When an exception occurs during request processing, the `DispatcherServlet` catches it.
2. **Handler Resolution:** Spring searches for methods within classes annotated with `@RestControllerAdvice` that are marked with `@ExceptionHandler` for the specific exception type.
3. **Exception Handling:** The corresponding `@ExceptionHandler` method is invoked to process the exception.

## Key Takeaways

- **Dynamic Filtering with Specifications**: Custom filtering based on multiple criteria. Need to extend `JpaSpecificationExecutor` in repository.
- **Projected interface**: Used to get custom columns from the database. Nested projection gives correct result but need to query all columns in the joined table. Naming convention: `findProjectedById` for all repositories. Works directly for `Page`.

```java
public interface UserSimple {
    Long getId();
    String getName();
}
```

```java
public UserSimple getSimpleById(Long id) {
    return userRepository.findProjectedById(id);
}
```
