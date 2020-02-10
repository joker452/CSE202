package baseball;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.StdOut;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BaseballElimination {

    private int numTeams;
    private Map<String, Integer> teamMap;
    private Map<Integer, String> nameMap;
    private int[][] teamWL;
    private int[][] graph;
    private int maxWinIdx;
    // number of vertices in flow graph
    private int numV;

    // 0 not unknown, 1 eliminated, 2 no elinimated
    private int[] isEliminated;
    private Map<String, Iterable<String>> certificateMap;


    // create a baseball division from given filename
    public BaseballElimination(String filename) {

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            teamMap = new HashMap<>();
            certificateMap = new HashMap<>();
            nameMap = new HashMap<>();
            // first line, number of teams
            numTeams = Integer.parseInt(br.readLine());
            isEliminated = new int[numTeams];
            graph = new int[numTeams][numTeams];
            teamWL = new int[numTeams][2];
            numV = numTeams == 1 ? 0 : 2 + numTeams * (numTeams - 1) / 2;
            maxWinIdx = 0;
            for (int i = 0; i < numTeams; ++i) {
                String line = br.readLine();
                String[] ss = line.split("\\s+");

                /* parse one line */
                teamMap.put(ss[0], i);
                nameMap.put(i, ss[0]);
                teamWL[i][0] = Integer.parseInt(ss[1]);
                teamWL[i][1] = Integer.parseInt(ss[2]);
                if (teamWL[i][0] > teamWL[maxWinIdx][0]) {
                    maxWinIdx = i;
                }
                /* empty slot used for total number of remaining games for team i */
                graph[i][i] = Integer.parseInt(ss[3]);
                /* parse reamining game info */
                for (int k = 0; k < numTeams; ++k) {
                    if (k != i) {
                        graph[i][k] = Integer.parseInt(ss[k + 4]);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // number of teams
    public int numberOfTeams() {
        return numTeams;
    }

    // number of wins for a given team
    public int wins(String team) {
        if (teamMap.containsKey(team)) {
            return teamWL[teamMap.get(team)][0];
        }
        throw new IllegalArgumentException("team: " + team + "doesn't exist");
    }

    // all teams
    public Iterable<String> teams() {
        return teamMap.keySet();
    }

    // number of losses for a given team
    public int losses(String team) {
        if (teamMap.containsKey(team)) {
            return teamWL[teamMap.get(team)][1];
        }
        throw new IllegalArgumentException("team " + team + "doesn't exist");
    }

    // number of remaining games for a given team
    public int remaining(String team) {
        if (teamMap.containsKey(team)) {
            int teamIdx = teamMap.get(team);
            return graph[teamIdx][teamIdx];
        }

        throw new IllegalArgumentException("team " + team + "doesn't exist");
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (teamMap.containsKey(team1) && teamMap.containsKey(team2)) {
            int teamIdx1 = teamMap.get(team1), teamIdx2 = teamMap.get(team2);
            return graph[teamIdx1][teamIdx2];
        }
        throw new IllegalArgumentException("team " + team1 + "or team " + team2 + "doesn't exist");
    }

    private void checkTeam(String team) {

        int teamIdx = teamMap.get(team);
        int teamVertexBase = numV - numTeams;
        int teamMax = teamWL[teamIdx][0] + graph[teamIdx][teamIdx];
        int flowVal = 0;

        if (teamWL[maxWinIdx][0] > teamMax) {
            List<String> certificateList = new LinkedList<>();
            certificateList.add(nameMap.get(maxWinIdx));
            isEliminated[teamIdx] = 1;
            certificateMap.put(team, certificateList);
        }
        else {
            /* construct flow graph */
            FlowNetwork g = new FlowNetwork(numV);
            int k = 1;
            for (int i = 0; i < numTeams; ++i) {
                int iPrime = i > teamIdx ? i - 1 : i;
                for (int j = i + 1; j < numTeams; ++j) {
                    if (i != teamIdx && j != teamIdx) {
                        int jPrime = j > teamIdx ? j - 1 : j;
                        /* source to game i-j edge */
                        g.addEdge(new FlowEdge(0, k, graph[i][j]));
                        flowVal += graph[i][j];
                        /* game i-j to team i, team j edge */
                        int teamiIdx = teamVertexBase + iPrime, teamjIdx = teamVertexBase + jPrime;
                        g.addEdge(new FlowEdge(k, teamiIdx, Double.POSITIVE_INFINITY));
                        g.addEdge(new FlowEdge(k, teamjIdx, Double.POSITIVE_INFINITY));
                        ++k;
                    }
                }
                /* team i to sink edge */
                if (i != teamIdx) {
                    g.addEdge(new FlowEdge(teamVertexBase + iPrime, numV - 1, Math.max(0, teamMax - teamWL[i][0])));
                }
            }


            /* calculate maxflow */
            FordFulkerson alg = new FordFulkerson(g, 0, numV - 1);
            if (flowVal != alg.value()) {
                List<String> certificateList = new LinkedList<>();
                for (int i = 0; i < numTeams; ++i) {
                    if (i != teamIdx) {
                        int iPrime = i > teamIdx ? i - 1 : i;
                        if (alg.inCut(iPrime + teamVertexBase)) {
                            certificateList.add(nameMap.get(i));
                        }
                    }
                }
                certificateMap.put(team, certificateList);
                isEliminated[teamIdx] = 1;
            } else {
                isEliminated[teamIdx] = 2;
            }
        }

    }

    // is given team eliminated
    public boolean isEliminated(String team) {
        if (teamMap.containsKey(team)) {
            if (isEliminated[teamMap.get(team)] == 0) {
                checkTeam(team);
            }
            return isEliminated[teamMap.get(team)] == 1;
        }
        throw new IllegalArgumentException("team " + team + "doesn't exist");
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (teamMap.containsKey(team)) {
            return certificateMap.get(team);
        }
        throw new IllegalArgumentException("team " + team + "doesn't exist");
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);

        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = {");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

}
