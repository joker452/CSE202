#include <fstream>
#include <cstdlib>
#include <vector>

int main()
{
    std::ifstream f("input.txt");

    if (f.is_open())
    {
        std::string line;
        if (getline(f, line))
        {
            /* read in first line */
            size_t pos = 0;
            if ((pos = line.find(',', 0)) != std::string::npos)
            {
                int n = atoi(line.substr(0, pos).c_str());
                while (pos < line.size() && line[pos] == ' ') {
                    ++pos;
                }
                int k = atoi(line.substr(pos + 1).c_str());

                std::vector<int> dp(k + 1, 0);

                int i;
                for (i = 0; dp[k] < n - 1; ++i) {
                    for (int j = k; j > 0; --j) {
                        dp[j] = dp[j] + dp[j - 1] + 1;
                    }
                }

                /* output to file */
                std::ofstream out("output.txt");
                out << i;
                out.close();
            }

        }
        f.close();
    }

    return 0;
}