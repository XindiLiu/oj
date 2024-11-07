// re_test.cpp
int main() {
    int* ptr = nullptr;
    *ptr = 42; // Dereferencing a null pointer to cause a segmentation fault
    return 0;
}
