#include <string>
#include <iostream>
#include <fstream>
#include <list>
#include <queue>
#include <vector>
#include <cstdlib>
#include <algorithm>


int main() {
    std::ifstream f("input.txt");

    if (f.is_open()) {
        std::string line;
        if (getline(f, line)) {
            /* read in first line */
            int n = atoi(line.c_str());
            std::vector<std::list<int> > graph(n, std::list<int>());

            for (int i = 0; i < n && getline(f, line); ++i) {
                /* construct the graph */
                size_t prev = 0, pos = 0;
                while ((pos = line.find('v', prev)) != std::string::npos) {
                    /* parse one line */
                    size_t end = line.find('-', pos);
                    int adj;
                    if (end != std::string::npos) {
                        adj = atoi(line.substr(pos + 1, end - pos - 1).c_str()) - 1;
                    } else {
                        adj = atoi(line.substr(pos + 1, end).c_str()) - 1;
                    }
                    if (adj != i) {
                        graph[i].push_back(adj);
                    }
                    prev = pos + 1;
                }
            }

            if (getline(f, line)) {
                int u = atoi(line.substr(1).c_str()) - 1;
                int len = 1;
                std::vector<int> visited(n, 0);
                std::vector<int> res;
                std::queue<int> q;

                q.push(u);
                res.push_back(u);
                visited[u] = 2;
                
                while (!q.empty()) {
                    int size = q.size();
                    for (int i = 0; i < size; ++i) {
                        /* one level of bfs */
                        int x = q.front();
                        q.pop();
                        for (std::list<int>::iterator it = graph[x].begin(); it != graph[x].end(); ++it) {
                            /* all neighbors of current node */
                            int adj = *it;
                            if (len % 2 == 0 && visited[adj] < 2) {
                                visited[adj] += 2;
                                q.push(adj);
                                res.push_back(adj);
                            }
                            if (len % 2 == 1 && ((visited[adj] & 0x1) == 0)) {
                                visited[adj] += 1;
                                q.push(adj);
                            }
                        }
                    }
                    len = len + 1;
                }

                /* output to file */
                std::sort(res.begin(), res.end());
                std::ofstream out("output.txt");
                out << res.size() << "\n";
                out << "v" << res[0] + 1;
                for (size_t i = 1; i < res.size(); ++i) {
                    out << ",v" << res[i] + 1;
                }
                out.close();
            }

        }
        f.close();
    }

    return 0;
}