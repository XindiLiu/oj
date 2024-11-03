Get logged-In user after loggin in:

User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

Detecting zip file:

- Tika.detect() and MultipartFile.getContentType() seems to work correctly,
- Files.probeContentType() seems to have system dependent implementation, gave some different result
- URLConnection.guessContentTypeFromStream() does not work
- MultipartFile.getContentType() does not throw IOException

Default value of fields (Submission.fileName):

Default value does not work with NoArgsConstructor. Create explicit constructor instead.