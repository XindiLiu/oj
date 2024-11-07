// tle_test.cpp
#include <iostream>
#include <cmath>

int main() {
    // Perform a long computation to exceed the time limit
    double result = 0;
    for (long i = 0; i < 1e9; ++i) { // Large loop to consume time
        result += std::sqrt(i);
    }
    std::cout << "Computation result: " << result << std::endl;
    return 0;
}
