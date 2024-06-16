from math import log

# We want to maximize P(Y = y | X = x).
# same as maximizing sum of X->Y edges

def MaximizeYgivenX(y, p, q):
    n = len(y)
    logp = [[log(u) for u in P] for P in p]
    logq = [[log(u) for u in Q] for Q in q]

    best_choice = [0 if logq[0][0] > logq[1][0] else 1, 0 if logq[0][1] > logq[1][1] else 1]
    x = [best_choice[Y-1] + 1 for Y in y]
    return x