N=300;
tvec = linspace(-pi+2*pi/N, pi, N);
rvec = 3+cos(4*tvec+pi);
rprimvec = -4*sin(4.*tvec+pi);
rbisvec = -16*cos(4.*tvec+pi);
y1 = rvec .*cos(tvec);
y2 = rvec .*sin(tvec);
nu1 = rvec .* cos(tvec) + rprimvec .* sin(tvec);
nu2 = rvec .* sin(tvec) - rprimvec .* cos(tvec);
nu1 = nu1 ./ sqrt( rvec.^2+ rprimvec.^2 );
nu2 = nu2 ./ sqrt( rvec.^2+ rprimvec.^2 );

A_aug = zeros(N,N);
vecdsdt = sqrt(rprimvec.^2+rvec.^2);
for i=1:N
    for j=1:N
        nu = [nu1(i), nu2(i)];
        
        r_i = [y1(i), y2(i)];
        r_j = [y1(j), y2(j)];
        
        param = norm(r_i-r_j)^2;
        namnare = 2*pi .* param;

        A_aug(i,j) = dot(nu, (r_i-r_j)/namnare);
    end
end

for i=1:N
    taljare = rprimvec(i)^2-0.5*rbisvec(i)*rvec(i) + 0.5*rvec(i)^2;
    namnare = 2*pi*(rprimvec(i)^2+rvec(i)^2)^(3/2);
    A_aug(i,i) = taljare/namnare;    
end

M = 300; % pick something here
x1field = linspace(-4, 4, M);
x2field = linspace(-4, 4, M);
ufield= zeros(M,M);
exactfield = zeros(M,M);
u_func = @(x, y) exp((x + 0.3 * y) / 3) .* sin((0.3 .* x - y) / 3);
dx_u_func = @(x,y) exp((x + 0.3 * y) / 3) .* ((1/3).*sin((0.3.*x - y) / 3) + 0.1.*cos((0.3.* x - y)/3));
dy_u_func = @(x,y) exp((x + 0.3 * y) / 3) .* (0.1.*sin((0.3.*x - y) / 3) - (1/3).*cos((0.3.*x - y)/3));

g_x = dx_u_func(y1,y2).*nu1;
g_y = dy_u_func(y1,y2).*nu2;
gvec = g_y'+g_x';
%Finding the augmented matrix
Inv_matrix = (-eye(N)/2+ 2*pi/N* A_aug* diag(vecdsdt));
Aug_matrix = Inv_matrix + ones(N)* 2*pi/N * diag(vecdsdt) ;
num_cond_nr = cond(-eye(N)/2+ 2*pi/N* A_aug* diag(vecdsdt));
aug_cond_nr = cond(Aug_matrix); %Finding the conditional number using the augmented matrix
hvec = Aug_matrix\gvec; %computing h using the augmented matrix
hvec = hvec.';

for ix1=1:M
    for ix2=1:M
        x1=x1field(ix1);
        x2=x2field(ix2);
        t=angle(complex(x1,x2));
        radius= 3+cos(4*t+pi); %formula for rvec
        if x1^2+ x2^2< radius^2
           phivec = log(vecnorm([y1;y2] - [x1;x2]))/(2*pi); %kernel expression in terms of xi, yi, nui, i=1,2
           exactfield(ix1,ix2) = u_func(x1,x2);
           ufield(ix1,ix2) = (phivec *(hvec.* vecdsdt).')*2*pi/N;
        end
    end
end

num_Sub_matrix = ufield(M/3:M*2/3,M/3:M*2/3);
mean_sub_matrix = mean(num_Sub_matrix, 'all');
real_sub_matrix = exactfield(M/3:M*2/3,M/3:M*2/3);
mean_real_sub = mean(real_sub_matrix,'all');
%%
ufield_1 = ufield-mean_sub_matrix+mean_real_sub;
ufield_2=ufield-mean_sub_matrix;
errorfield = log10(abs(ufield_1-exactfield));

imagesc(x1field, x2field, exactfield.')

axis xy
colormap turbo
pbaspect([1 1 1])
colorbar
title('Numerical solution of u(x,y) with augmentation step')

%%
imagesc(x1field, x2field, errorfield.')

axis xy
colormap turbo
pbaspect([1 1 1])
colorbar
title('Error plot')