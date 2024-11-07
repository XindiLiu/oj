// mle_test.cpp
#include <vector>
#include <iostream>

int main() {
    // Attempt to allocate 600 MB in chunks of 50 MB
    const size_t chunk_size = 50 * 1024 * 1024; // 50 MB
    const size_t num_chunks = 12; // 600 MB total
    std::vector<std::vector<int>> memory_chunks;

    for (size_t i = 0; i < num_chunks; ++i) {
        // try {
        memory_chunks.emplace_back(chunk_size / sizeof(int), 1);
    }

    return 0;
}
