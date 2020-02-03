#include <string>
#include <iostream>
#include <fstream>
#include <vector>
#include <cstdlib>
#include <algorithm>

struct mygtr
{
    bool operator()(const std::vector<int> &x, const std::vector<int> &y) const
    {
        return x[1] < y[1];
    }
};

int main()
{
    std::ifstream f("input.txt");

    if (f.is_open())
    {
        std::string line;
        if (getline(f, line))
        {
            /* read in first line */
            int n = atoi(line.c_str());

            std::vector<int> rows;
            std::vector<std::vector<int> > cols;
            int sum = 0;

            if (getline(f, line))
            {
                size_t prev = 0, pos = 0;
                while ((pos = line.find(',', prev)) != std::string::npos)
                {
                    int r = atoi(line.substr(prev, pos - prev).c_str());
                    rows.push_back(r);
                    sum += r;
                    prev = pos + 1;
                }
                int r = atoi(line.substr(prev, std::string::npos).c_str());
                rows.push_back(r);
                sum += r;
            }

            if (getline(f, line))
            {
                int i = 0;
                size_t prev = 0, pos = 0;
                while ((pos = line.find(',', prev)) != std::string::npos)
                {
                    std::vector<int> col;
                    int c = atoi(line.substr(prev, pos - prev).c_str());
                    col.push_back(i);
                    col.push_back(c);
                    cols.push_back(col);
                    sum += c;
                    prev = pos + 1;
                    ++i;
                }
                std::vector<int> col;
                int c = atoi(line.substr(prev, std::string::npos).c_str());
                col.push_back(i);
                col.push_back(c);
                cols.push_back(col);
                sum += c;
            }

            std::make_heap(cols.begin(), cols.end(), mygtr());
            std::vector<std::vector<int> > matrix(n, std::vector<int>(n, 0));

            for (int i = 0; i < n && sum > 0; ++i)
            {
                int r = rows[i];
                std::vector<std::vector<int> > tmp;
                while (r > 0)
                {
                    if (cols.size() > 0 && cols.front()[1] > 0)
                    {
                        std::vector<int> col = cols.front();
                        std::pop_heap(cols.begin(), cols.end(), mygtr());
                        cols.pop_back();
                        matrix[i][col[0]] = 1;
                        col[1] -= 1;
                        tmp.push_back(col);
                        sum -= 2;
                        --r;
                    }
                    else
                    {
                        sum = -1;
                        break;
                    }
                }

                while (tmp.size() > 0) {
                    cols.push_back(tmp.back());
                    std::push_heap(cols.begin(), cols.end(), mygtr());
                    tmp.pop_back();
                }
            }

            /* output to file */
            std::ofstream out("output.txt");
            if (sum == 0) {
                out << 1 << "\n";
                for (int i = 0; i < n; ++i) {
                    out << matrix[i][0];
                    for (int j = 1; j < n; ++j)
                    {
                        out << "," << matrix[i][j];
                    }
                    if (i < n - 1)
                    {
                        out << "\n";
                    }
                }
            } else {
                out << 0;
            }
            out.close();
        }
        f.close();
    }

    return 0;
}