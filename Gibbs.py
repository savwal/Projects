from random import random, randint

def step(x, y, n, p, q):
    k = randint(0, n-1)
    p1_left = p[x[k-1]][0] if k > 0 else 1
    p1_right = p[0][x[k+1]] if k < n-1 else 1
    p1 = p1_left * p1_right * q[0][y[k]-1]
    
    p2_left = p[x[k-1]][1] if k > 0 else 1
    p2_right = p[1][x[k+1]] if k < n-1 else 1
    p2 = p2_left * p2_right * q[1][y[k]-1]

    x[k] = 0 if random() < p1/(p1+p2) else 1
    return x

def Gibbs(y, p, q):
    n = len(y)
    x = [0]*n
    steps = n**2

    for i in range(steps):
        x = step(x, y, n, p, q)
    
    return [X + 1 for X in x]
