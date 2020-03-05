#include <fstream>
#include <vector>
#include <algorithm>
#include <map>
#include <iostream>
#include <string>
#include <unordered_set>

int getId(int x, int y, int n)
{
    return x * n + y + 1;
}
std::string coord(int id, int n)
{

    if (id > 0)
    {
        std::string str = "(";
        if (id % n == 0)
        {
            str += std::to_string(id / n);
        }
        else
        {
            str += std::to_string(id / n + 1);
        }

        str += ",";
        if (id % n == 0)
        {
            str += std::to_string(n);
        }
        else
        {
            str += std::to_string(id % n);
        }

        str += ")";
        return str;
    }
    return "";
}

bool dfs(std::unordered_set<int> &visited, std::map<int, std::vector<int>> &graph, int s, int t)
{
    if (s == t)
    {
        return true;
    }
    std::unordered_set<int>::iterator it = visited.find(s);
    if (it != visited.end())
    {
        return false;
    }

    visited.insert(s);
    std::vector<int> list = graph[s];
    for (std::vector<int>::iterator it1 = list.begin(); it1 != list.end(); ++it1)
    {
        if (dfs(visited, graph, *it1, t))
        {
            graph[s].erase(std::remove(graph[s].begin(), graph[s].end(), *it1), graph[s].end());
            graph[*it1].push_back(s);
            return true;
        }
    }
    return false;
}

std::vector<int> getNeighbors(std::vector<std::vector<int>> &graph, int x, int y, int n)
{
    std::vector<int> list;
    if (x == 0)
    {
        if (y == 0)
        {
            if (n > 1)
            {
                if (graph[x][y + 1] == 1)
                    list.push_back(getId(x, y + 1, n));
                if (graph[x + 1][y] == 1)
                    list.push_back(getId(x + 1, y, n));
            }
        }
        else if (y == n - 1)
        {
            if (n > 1)
            {
                if (graph[x][y - 1] == 1)
                    list.push_back(getId(x, y - 1, n));
                if (graph[x + 1][y] == 1)
                    list.push_back(getId(x + 1, y, n));
            }
        }
        else
        {
            if (graph[x][y - 1] == 1)
                list.push_back(getId(x, y - 1, n));
            if (graph[x][y + 1] == 1)
                list.push_back(getId(x, y + 1, n));
            if (graph[x + 1][y] == 1)
                list.push_back(getId(x + 1, y, n));
        }
    }
    else if (x == n - 1)
    {
        if (y == 0)
        {
            if (n > 1)
            {
                if (graph[x - 1][y] == 1)
                    list.push_back(getId(x - 1, y, n));
                if (graph[x][y + 1] == 1)
                    list.push_back(getId(x, y + 1, n));
            }
        }
        else if (y == n - 1)
        {
            if (n > 1)
            {
                if (graph[x - 1][y] == 1)
                    list.push_back(getId(x - 1, y, n));
                if (graph[x][y - 1] == 1)
                    list.push_back(getId(x, y - 1, n));
            }
        }
        else
        {
            if (graph[x - 1][y] == 1)
                list.push_back(getId(x - 1, y, n));
            if (graph[x][y - 1] == 1)
                list.push_back(getId(x, y - 1, n));
            if (graph[x][y + 1] == 1)
                list.push_back(getId(x, y + 1, n));
        }
    }
    else
    {
        if (y == 0)
        {
            if (graph[x - 1][y] == 1)
                list.push_back(getId(x - 1, y, n));
            if (graph[x][y + 1] == 1)
                list.push_back(getId(x, y + 1, n));
            if (graph[x + 1][y] == 1)
                list.push_back(getId(x + 1, y, n));
        }
        else if (y == n - 1)
        {
            if (graph[x - 1][y] == 1)
                list.push_back(getId(x - 1, y, n));
            if (graph[x][y - 1] == 1)
                list.push_back(getId(x, y - 1, n));
            if (graph[x + 1][y] == 1)
                list.push_back(getId(x + 1, y, n));
        }
        else
        {
            if (graph[x - 1][y] == 1)
                list.push_back(getId(x - 1, y, n));
            if (graph[x][y - 1] == 1)
                list.push_back(getId(x, y - 1, n));
            if (graph[x][y + 1] == 1)
                list.push_back(getId(x, y + 1, n));
            if (graph[x + 1][y] == 1)
                list.push_back(getId(x + 1, y, n));
        }
    }
    return list;
}

int main()
{
    std::ifstream f("input.txt");

    if (f.is_open())
    {
        std::string line;

        int n;
        int sizeA = 0, sizeB = 0;
        std::vector<std::vector<int>> matrix;
        std::map<int, std::vector<int>> graph;

        /* read in first line */
        f >> n;
        /* read grids */
        for (int i = 0; i < n; ++i)
        {
            std::vector<int> row;
            f >> line;
            for (int j = 0; j < n; ++j)
            {
                row.push_back(line[j] - '0');
            }
            matrix.push_back(row);
        }


        /* convert to bipartite */
        std::vector<int> vecA;
        std::vector<int> vecB;

        for (int i = 0; i < n; ++i)
        {
            for (int j = 0; j < n; ++j)
            {
                int id = n * i + j + 1;
                if (matrix[i][j] == 1)
                {
                    if ((i + j) % 2 == 0)
                    {
                        ++sizeA;
                        vecA.push_back(id);
                        graph[id] = getNeighbors(matrix, i, j, n);
                    }
                    else
                    {
                        ++sizeB;
                        vecB.push_back(id);
                        std::vector<int> b;
                        b.push_back(0);
                        graph[id] = b;
                    }
                }
            }
        }
        graph[-1] = vecA;
        if (sizeA == sizeB)
        {
            std::unordered_set<int> visited;
            int flow = 0;

            while (dfs(visited, graph, -1, 0))
            {
                ++flow;
                visited.clear();
            }

            if (flow == sizeA)
            {
                std::ofstream out("output.txt");
                out << 1 << std::endl;
                for (std::vector<int>::iterator it = vecB.begin(); it != vecB.end();)
                {
                    out << coord(*it, n) << coord(graph[*it][0], n);
                    ++it;
                    if (it == vecB.end())
                    {
                        out.close();
                    }
                    else
                    {
                        out << std::endl;
                    }
                }
                f.close();
                return 0;
            }
        }

        /* output to file */
        std::ofstream out("output.txt");
        out << 0;
        out.close();

        f.close();
    }

    return 0;
}