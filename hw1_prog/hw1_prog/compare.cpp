#include <iostream>
#include <fstream>
#include <string>

int main() {

    std::ifstream actual("output.txt");
    std::ifstream expect("expect.txt");
    std::string line1;
    std::string line2;

    if (actual.is_open() && expect.is_open()) {
        std::cout << "start test" << std::endl;
        while (getline(expect, line2)) {
            if (getline(actual, line1)) {
                if (line1.compare(line2) != 0) {
                    std::cout << "error! expected: " << line2 << "but get: " << line1 << std::endl;
                    break;
                }
            } else {
                std::cout << "error! miss line" << std::endl;
                break;
            }
        }
        if (getline(actual, line1)) {
            std::cout << "error! extra line" <<std::endl;
        }
    }
    std::cout << "finish test!" << std::endl;
    return 0;
}
