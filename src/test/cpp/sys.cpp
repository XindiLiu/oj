#include <cstdlib>

int main() {
    // Attempt to execute a shell command (should be blocked by seccomp)
    system("ls");
    return 0;
}