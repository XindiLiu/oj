// executor.c
#include <stdio.h>
#include <stdlib.h>
#include <sys/resource.h>
#include <sys/wait.h>
#include <unistd.h>
#include <errno.h>
#include <seccomp.h>
#include <time.h>

#define TIME_LIMIT 10000 // Default time limit in milliseconds
#define MEMORY_LIMIT 1024 * 1024 * 1024 // Default memory limit in bytes

// Set memory and CPU time limits using setrlimit
void set_limits(int time_limit, int memory_limit) {
    struct rlimit rlim;

    // Set CPU time limit (seconds)
    rlim.rlim_cur = rlim.rlim_max = TIME_LIMIT / 1000;
    setrlimit(RLIMIT_CPU, &rlim);

    // Set memory limit
    rlim.rlim_cur = rlim.rlim_max = MEMORY_LIMIT;
    setrlimit(RLIMIT_AS, &rlim);
}

// Apply seccomp to limit system calls to prevent unsafe operations
void apply_seccomp() {
    scmp_filter_ctx ctx = seccomp_init(SCMP_ACT_ALLOW);
    if (ctx == NULL) {
        perror("seccomp_init failed");
        exit(1);
    }

    // Block network-related syscalls to prevent internet access
    seccomp_rule_add(ctx, SCMP_ACT_KILL, SCMP_SYS(socket), 0);
    seccomp_rule_add(ctx, SCMP_ACT_KILL, SCMP_SYS(connect), 0);
    seccomp_rule_add(ctx, SCMP_ACT_KILL, SCMP_SYS(accept), 0);
    seccomp_rule_add(ctx, SCMP_ACT_KILL, SCMP_SYS(bind), 0);
    seccomp_rule_add(ctx, SCMP_ACT_KILL, SCMP_SYS(listen), 0);
    seccomp_rule_add(ctx, SCMP_ACT_KILL, SCMP_SYS(sendto), 0);
    seccomp_rule_add(ctx, SCMP_ACT_KILL, SCMP_SYS(recvfrom), 0);
    seccomp_rule_add(ctx, SCMP_ACT_KILL, SCMP_SYS(sendmsg), 0);
    seccomp_rule_add(ctx, SCMP_ACT_KILL, SCMP_SYS(recvmsg), 0);

    // Block tracing and debugging
    seccomp_rule_add(ctx, SCMP_ACT_KILL, SCMP_SYS(ptrace), 0);

    if (seccomp_load(ctx) < 0) {
        perror("seccomp_load failed");
        seccomp_release(ctx);
        exit(1);
    }

    seccomp_release(ctx);
}

int run_program(const char* executable, const char* input, const char* output, const char* result, int time_limit, int memory_limit) {
    pid_t pid = fork();
    if (pid == 0) {
        freopen(input, "r", stdin);
        freopen(output, "w", stdout);
        set_limits(time_limit, memory_limit);
        apply_seccomp();

        execl(executable, executable, (char*)NULL);
        exit(127);
    }

    int status;
    struct timespec start, end;
    clock_gettime(CLOCK_MONOTONIC, &start);

    if (waitpid(pid, &status, 0) == -1) {
        // perror("waitpid failed");
        return 1;
    }
    clock_gettime(CLOCK_MONOTONIC, &end);

    // Calculate elapsed time in milliseconds
    long elapsed = (end.tv_sec - start.tv_sec) * 1000 + (end.tv_nsec - start.tv_nsec) / 1000000;

    struct rusage usage;
    getrusage(RUSAGE_CHILDREN, &usage);
    long memory_used = usage.ru_maxrss * 1024; // Convert KB to bytes

    // printf("status: %d\n", status);
    // printf("%d\n", WIFEXITED(status));
    // printf("%d\n", WEXITSTATUS(status));
    // printf("%d\n", WIFSIGNALED(status));
    // printf("%d\n", WTERMSIG(status));
    // printf("%d\n", WIFSTOPPED(status));
    // printf("%d\n", WSTOPSIG(status));
    freopen(result, "w", stdout);
    printf("%ld\n%ld\n", elapsed, memory_used);
    if (elapsed > time_limit) {
        printf("TLE");
        return 1;
    }
    else if (memory_used > memory_limit) {
        printf("MLE");
        return 1;
    }
    else if (WIFSIGNALED(status)) {
        printf("RE");
        return 1;
    }
    else if (WIFEXITED(status) && WEXITSTATUS(status) == 0) {
        printf("AC");
        return 0;
    }
    else {
        printf("RE");
        return 1;
    }
}

int main(int argc, char* argv[]) {
    if (argc < 6) {
        fprintf(stderr, "Too few arguments");
        return 1;
    }
    char* executable = argv[1];
    char* in = argv[2];
    char* out = argv[3];
    char* result = argv[4];
    int time_limit = atoi(argv[5]);
    int memory_limit = atoi(argv[6]);

    return run_program(executable, in, out, result, time_limit, memory_limit);
}
