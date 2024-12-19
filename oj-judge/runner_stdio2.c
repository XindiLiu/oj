// executor.c
#include <stdio.h>
#include <stdlib.h>
#include <sys/resource.h>
#include <sys/wait.h>
#include <unistd.h>
#include <errno.h>
#include <seccomp.h>
#include <time.h>
#include <math.h>

typedef long long ll;
// Set memory and CPU time limits using setrlimit
void set_limits(ll time_limit_ms, ll memory_limit_byte) {
    struct rlimit trlim;
    // Set CPU time limit (seconds)
    trlim.rlim_max = trlim.rlim_cur = ceil(time_limit_ms / 1000.0);
    setrlimit(RLIMIT_CPU, &trlim);
    struct rlimit mrlim;
    // Set memory limit (byte)
    mrlim.rlim_max = mrlim.rlim_cur = memory_limit_byte;  // Soft limit
    setrlimit(RLIMIT_AS, &mrlim);
    // printf("Time limit: %ld ms , memory limit: %ld byte", time_limit_ms, memory_limit_byte);
}

// Apply seccomp to limit system calls to prevent unsafe operations
void apply_seccomp() {
    scmp_filter_ctx ctx = seccomp_init(SCMP_ACT_ALLOW);
    // if (ctx == NULL) {
    //     perror("seccomp_init failed");
    //     exit(1);
    // }

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

    // if (seccomp_load(ctx) < 0) {
    //     perror("seccomp_load failed");
    //     seccomp_release(ctx);
    //     exit(1);
    // }

    seccomp_release(ctx);
}

int run_program(const char* executable, ll time_limit_ms, ll memory_limit_MB) {

    ll memory_limit_byte = memory_limit_MB * 1024 * 1024;
    set_limits(time_limit_ms, memory_limit_byte);
    // apply_seccomp();

    int pipe_read, pipe_write;
    int pipefd[2];
    if (pipe(pipefd) == -1) {
        perror("pipe failed");
        exit(21);
    }
    pipe_read = pipefd[0];
    pipe_write = pipefd[1];

    pid_t pid = fork();
    if (pid == -1) {
        perror("fork failed");
        exit(22);
    }

    if (pid == 0) {
        // Child process
        close(pipe_read); // Close unused read end
        dup2(pipe_write, STDOUT_FILENO); // Redirect stdout to pipe
        close(pipe_write); // Close write end after redirect

        execl(executable, executable, (char*)NULL);
        perror("execl failed");
        exit(23);
    }
    else {
        // Parent process
        close(pipe_write); // Close unused write end

        int status;
        struct timespec start, end;
        clock_gettime(CLOCK_MONOTONIC, &start);

        waitpid(pid, &status, 0);

        clock_gettime(CLOCK_MONOTONIC, &end);

        // Calculate elapsed time in milliseconds
        ll time_used_ms = (end.tv_sec - start.tv_sec) * 1000 + (end.tv_nsec - start.tv_nsec) / 1000000;

        struct rusage usage;
        getrusage(RUSAGE_CHILDREN, &usage); // maximum resident set size used (in kilobytes)
        ll memory_used_byte = usage.ru_maxrss * 1024;

        if (time_used_ms > time_limit_ms) {
            printf("TLE\n");
            return 2;
        }
        else if (memory_used_byte > memory_limit_byte) {
            printf("MLE\n");
            return 3;
        }
        else if (WIFSIGNALED(status)) {
            printf("RE\n");
            return 1;
        }
        else if (WIFEXITED(status) && WEXITSTATUS(status) == 0) {
            printf("AC\n");
            printf("%ld\n%ld\n", time_used_ms, memory_used_byte);

            // Read output from child process
            char buffer[1024];
            ssize_t bytes_read;
            while ((bytes_read = read(pipe_read, buffer, sizeof(buffer) - 1)) > 0) {
                buffer[bytes_read] = '\0';
                printf("%s", buffer);
            }
            close(pipe_read);
            return 0;
        }
        else {
            fprintf(stderr, "Status:%d", status);
            printf("RE\n");
            return 1;
        }
    }
}

int main(int argc, char* argv[]) {
    if (argc < 3) {
        fprintf(stderr, "Too few arguments");
        return 20;
    }
    char* executable = argv[1];
    ll time_limit_ms = atoi(argv[2]);
    ll memory_limit_MB = atoi(argv[3]);
    // printf("%ld %ld", time_limit_ms, memory_limit_MB);
    run_program(executable, time_limit_ms, memory_limit_MB);
    return 0;
}
