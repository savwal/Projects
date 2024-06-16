from math import log
from random import randint

def find_logarithmic_probability_y_and_x(x, y, p, q):
    # returns log P(X = x and Y = y)
    logp = [[log(u) for u in P] for P in p]
    logq = [[log(u) for u in Q] for Q in q]

    log_prob = 0
    n = len(x)
    for t in range(n-1):
        log_prob += logp[x[t]-1][x[t+1]-1] + logq[x[t]-1][y[t]-1]
    log_prob += logq[x[n-1]-1][y[n-1]-1]
    return log_prob

def find_logarithmic_probability_y_given_x(x, y, p, q):
    # returns log P(Y = y | X = x)
    logp = [[log(u) for u in P] for P in p]
    logq = [[log(u) for u in Q] for Q in q]

    log_prob = 0
    n = len(x)
    for t in range(n):
        log_prob += logq[x[t]-1][y[t]-1]
    return log_prob


def get_random_x(n):
    return [randint(1, 2) for i in range(n)]