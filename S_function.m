%% Uppgift 1) a)
s = tf('s');
G_y = (s^2-12*s+48)/((1+s)*(s^2+12*s+48));
G_u = 2/(1+0.5*s);
G = G_y * G_u;
w_c = [0.4, 0.67];
phi = [30, 54];

[Ki, Ti, phim, wc, MS, MT] = pi_opt(G, w_c(1), w_c(2), phi(1), phi(2))

%% c)i)
K_i = 0.3442;
T_i = 1.0405;
F = K_i*(1+T_i*s)/s;

G_ry = G*F/(1+G*F);
G_vy = G_y/(1+G*F);

step(G_vy, G_ry)
title('Stegsvar')
legend('G_{vy}', 'G_{ry}')

%%

sigma(G_vy, G_ry);
title('Beloppfunktioner')
legend('G_{vy}', 'G_{ry}')

%% Uppgift 2) a)i)

G_vy_i = G_y/(1+G*F);

%% a)ii)

G_vy_ii = G_y * (1-G_u/2)/(1+G*F);

%% a)iii)

tau = 0.1;
F_f = -(1+0.5*s)/(2*(1+tau*s));
G_vy_iii = G_y * (1+F_f*G_u)/(1+G*F);

%% a)iii)2)

tau = 0.2;
F_f = -(1+0.5*s)/(2*(1+tau*s));
G_vy_iv = G_y * (1+F_f*G_u)/(1+G*F);

%% Plots
step(G_vy_i, G_vy_ii, G_vy_iii, G_vy_iv)
legend('G_vy ingen framkoppling', 'G_vy statisk framkoppling', 'G_vy dynamisk framkoppling tau=0.1','G_vy dynamisk framkoppling tau=0.2');
title('Stegsvar för olika G_{vy} metoder')
%%

sigma(G_vy_i, G_vy_ii, G_vy_iii, G_vy_iv)
legend('G_vy ingen framkoppling', 'G_vy statisk framkoppling', 'G_vy dynamisk framkoppling tau=0.1','G_vy dynamisk framkoppling tau=0.2', 'Location', 'southeast');
title('Beloppfunktion för olika G_{vy} metoder')

%% Uppgift 2) b) ingen framkoppling
G_u_50 = 2/(1+0.75*s);
G_50 = G_u_50 * G_y;
G_vy_i_50 =  G_y/(1+G_50*F);

step(G_vy_i_50, G_vy_i)
legend('G_vy', 'G_vy + 50%')
title('Stegsvar för utan framkoppling')

%%
sigma(G_vy_i_50, G_vy_i)
legend('G_vy', 'G_vy + 50%')
title('Beloppfunktion utan framkoppling')


%% statisk framkoppling

G_vy_ii_50 = G_y * (1-G_u_50/2)/(1+G_50*F);

step(G_vy_ii, G_vy_ii_50)
legend('G_vy', 'G_vy + 50%')
title('Stegsvar för statisk framkoppling')


%%
sigma(G_vy_ii, G_vy_ii_50)
legend('G_vy', 'G_vy + 50%')
title('Beloppfunktion för statisk framkoppling')

%% dynamisk framkoppling, tau=0.1

tau = 0.1;
F_f = (1+0.5*s)/(2*(1+tau*s));
G_vy_iv_50 = G_y * (1-F_f*G_u)/(1+G*F);

step(G_vy_iv, G_vy_iv_50)
legend('G_vy', 'G_vy + 50%')
title('Stegsvar för dynamisk framkoppling')

%%
sigma(G_vy_iv, G_vy_iv_50)
legend('G_vy', 'G_vy + 50%')
title('Beloppfunktion för dynamisk framkoppling')

