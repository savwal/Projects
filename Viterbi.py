from math import log

def Viterbi(y, p, q):
    n = len(y)
    d = [[None]*2 for i in range(n)]
    backtrack = [[None]*2 for i in range(n)]
    logp = [[log(u) for u in P] for P in p]
    logq = [[log(u) for u in Q] for Q in q]

    d[0][0] = log(q[0][y[0]-1])
    d[0][1] = -float("inf")
    for t in range(1, n):
        for i in range(2):
            p1 = d[t-1][0] + logp[0][i] + logq[i][y[t]-1]
            p2 = d[t-1][1] + logp[1][i] + logq[i][y[t]-1]

            d[t][i] = max(p2, p1)
            backtrack[t][i] = 0 if p1 > p2 else 1

    optimal_x = [None]*n
    optimal_x[n-1] = 0 if d[n-1][0] > d[n-1][1] else 1
    for t in range(n-2, -1, -1):
        best = backtrack[t+1][optimal_x[t+1]]
        optimal_x[t] = best
    
    return [x + 1 for x in optimal_x]