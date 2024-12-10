# Use an official GCC image as the base image
FROM gcc:latest

# Install necessary libraries for seccomp
RUN apt-get update && \
    apt-get install -y libseccomp-dev && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set the working directory inside the container
WORKDIR /workspace

# Copy the source files into the container
COPY compiler.c /workspace/
COPY runner_file.c /workspace/
COPY runner_stdio2.c /workspace/

# Compile compiler.c and executor.c to create `compiler` and `executor` executables
RUN gcc -o /usr/local/bin/compiler compiler.c && rm compiler.c
RUN gcc -o /usr/local/bin/runner_file runner_file.c -lseccomp && rm runner_file.c
RUN gcc -o /usr/local/bin/runner_stdio runner_stdio2.c -lseccomp -lm && rm runner_stdio2.c
# Need -lm to use math.h

# RUN chmod +x /usr/local/bin/compiler /usr/local/bin/runner


# Set the entrypoint to bash for interactive use
ENTRYPOINT ["/bin/bash","-l", "-c" ]
