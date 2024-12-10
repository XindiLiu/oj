// compiler.c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
int main(int argc, char* argv[]) {
    if (argc < 3) {
        // fprintf(stderr, "Too few arguments");
        return 1;
    }
    char* source = argv[1];
    char* executable = argv[2];
    // Compile the program, return 0 if successful, 1 if compile error
    char command[256];
    snprintf(command, sizeof(command), "g++ -g -O2 -o %s -std=gnu++23 -static -lrt -Wl,--whole-archive -lpthread -Wl,--no-whole-archive %s", executable, source);
    FILE* fp = popen(command, "r");
    if (!fp) return -1; // becomes 255
    // char error[512] = {0};
    // fread(error, 1, sizeof(error) - 1, fp);
    // freopen(fp, "w", stderr);
    int status = pclose(fp);
    return status == 0 ? 0 : 1; // If return status directly, 256 will become 0 in the container.
}
