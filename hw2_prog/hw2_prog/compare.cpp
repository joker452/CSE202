#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <cstdlib>

bool compare_line(std::string &line1, std::string &line2, std::vector<int> &col1, std::vector<int> &col2)
{
    int row1 = 0, row2 = 0;
    size_t prev = 0, pos = 0;
    size_t i1 = 0, i2 = 0;
    line2.append(",");
    line1.append(",");
    while ((pos = line2.find(',', prev)) != std::string::npos)
    {
        int r = atoi(line2.substr(prev, pos - prev).c_str());
        if (i2 >= col2.size()) {
            col2.push_back(r);
        } else {
            col2[i2] += r;
        }
        row2 += r;
        prev = pos + 1;
        ++i2;
    }

    prev = 0, pos = 0;
    while ((pos = line1.find(',', prev)) != std::string::npos)
    {
        int r = atoi(line1.substr(prev, pos - prev).c_str());
        if (i1 >= col1.size()) {
            col1.push_back(r);
        } else {
            col1[i1] += r;
        }
        row1 += r;
        prev = pos + 1;
        ++i1;
    }
    return row1 == row2 && i1 == i2;
}


int main()
{

    std::ifstream actual("output.txt");
    std::ifstream expect("expect.txt");
    std::string line1;
    std::string line2;

    if (actual.is_open() && expect.is_open())
    {
        std::cout << "start test" << std::endl;
        int i = 0;
        std::vector<int> col1, col2;
        bool error = false;
        while (getline(expect, line2))
        {
            if (getline(actual, line1))
            {
                if (i == 0)
                {
                    if (line1.compare(line2) != 0)
                    {
                        std::cout << "error! expected: " << line2 << "but get: " << line1 << std::endl;
                        error = true;
                        break;
                    }
                } else {
                    if (!compare_line(line1, line2, col1, col2)) {
                        std::cout << "error! sum of row " << i << " not equal " << std::endl;
                        error = true;
                        break;
                    }
                }
                ++i;
            }
            else
            {
                std::cout << "error! miss line" << std::endl;
                error = true;
                break;
            }
        }

        if (error == false ) {
            for (size_t j = 0; j < col2.size(); ++j) {
                if (j >= col1.size() || col2[j] != col1[j]) {
                    std::cout << "error! sum of col " << j + 1 << " not equal" << std::endl;
                    break; 
                }
            }
            if (getline(actual, line1))
            {
                std::cout << "error! extra line" << std::endl;
            }
        }


    }
    std::cout << "finish test!" << std::endl;
    return 0;
}
