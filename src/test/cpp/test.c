// executor.c
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
// #include <sys/resource.h>
// #include <sys/wait.h>
// #include <unistd.h>
// #include <errno.h>
// #include <seccomp.h>
// #include <time.h>

typedef long long ll;

int main() {
    ll time_limit_ms = 1500000;
    ll d = ceil(time_limit_ms / 999.0);
    printf("%d", d);
    return 0;
}
