from Viterbi import Viterbi
from Gibbs import Gibbs
from Test import find_logarithmic_probability_y_given_x, get_random_x
from Experimental import MaximizeYgivenX

from random import randint
import scipy.io

mat = scipy.io.loadmat('hidden.mat')
y = list(mat["y"][0])

p = [[0.9, 0.1], [0.2, 0.8]] # p[0][0] = p(1,1)
q = [[0.6, 0.4], [0.3, 0.7]]


x = Viterbi(y, p, q)
v = find_logarithmic_probability_y_given_x(x, y, p, q)

xg = Gibbs(y, p, q)
g = find_logarithmic_probability_y_given_x(xg, y, p, q)

print(f"Viterbi: {v}\nGibbs: {g}")
