#### Get logged-In user after loggin in:

```java
User user=(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
2. **Handler Resolution:** Spring searches for methods within classes annotated with `@RestControllerAdvice` that are
   marked with `@ExceptionHandler` for the specific exception type.
3. **Exception Handling:** The corresponding `@ExceptionHandler` method is invoked to process the exception.

## Dynamic qeuries

- **Dynamic Filtering with Specifications**: Custom filtering based on multiple criteria. Need to
  extend `JpaSpecificationExecutor` in repository.
- **Projected interface**: Used to get custom columns from the database. Nested projection gives correct result but need
  to query all columns in the joined table. Naming convention: `findProjectedById` for all repositories. Works directly
  for `Page`.

```java
public interface UserSimple {
	Long getId();

	String getName();
}
```

```java
public UserSimple getSimpleById(Long id){
		return userRepository.findProjectedById(id);
		}
```

#### Specificaion<T>
Is a function taking (Root<T> root, CriteriaQuery<T> query, CriteriaBuilder cb) and returning a Predicate.
A predicate represents the where clause in the query.
Use CriteriaBuilder to build Predicates, and use root.get(field) to get fields from the entity. For example:
```java
Predicate predicate = cb.and(cb.equal(root.get("id"), id), cb.equal(root.get("name"), name));
```
Convert Predicate to Specification using `Specification.where(predicate)`, convert Specification to Predicate using `toPredicate(Root<T> root, CriteriaQuery<T> query, CriteriaBuilder cb)`.

#### Custom queries
1. Get entitymanager from Bean
```java
@PersistenceContext
EntityManager entityManager;
```
2. Define the Root, CriteriaQuery and CriteriaBuilder
```java
Root<Entity> root = query.from(Entity.class);
CriteriaQuery<DTO> query = entityManager.getCriteriaBuilder().createQuery(DTO.class);
CriteriaBuilder cb = entityManager.getCriteriaBuilder();
```
3. (Optional) Join table.
```java
Join<T, Other> join = root.join("otherattribute");
```
`join` can be used similar to `root` to get attributes of the joined table. 
Use `join.on(Predicate)` to specify the ON condition (filter before join).

4. Create the predicate.

5. Create the query.
```java
query.where(predicate);
```
Can select custom columns in the `select` method, including columns from joined tables.
```java
query.select(cb.construct(DTO.class, root.get("id"), root.get("name"), join.get("otherattribute")...));
```

6. Execute the query.
```java
List<DTO> result = entityManager.createQuery(query).getResultList();
```

7. (Optional) Use paging. Add a count query. Almost same as the query above
```java
CriteriaQuery<Long> countQuery = entityManager.getCriteriaBuilder().createQuery(Long.class);
countQuery.select(cb.countDistinct(root)).where(predicate);
Long count = entityManager.createQuery(countQuery).getSingleResult();
return new PageImpl<>(result, PageRequest.of(page, size), count);
```



