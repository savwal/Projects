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

A = zeros(N,N); %Initialize the matrix
vecdsdt = sqrt(rprimvec.^2+rvec.^2); %dsdt definition

%Compute elements in A matrix
for i=1:N
    for j=1:N
        % Element wise concatination
        nu = [nu1(i), nu2(i)];
        r_i = [y1(i), y2(i)];
        r_j = [y1(j), y2(j)];
        
        param = norm(r_i-r_j)^2;
        namnare = 2*pi .* param;
        % Computation
        A(i,j) = dot(nu, (r_i-r_j)/namnare);
    end
end
% Update the diagonal according to the limit
for i=1:N
    taljare = rprimvec(i)^2-0.5*rbisvec(i)*rvec(i) + 0.5*rvec(i)^2;%Numerator
    namnare = 2*pi*(rprimvec(i)^2+rvec(i)^2)^(3/2);%denominator
    A(i,i) = taljare/namnare;    %Computation
end
%Initializing field computation
M = 300; % pick something here
x1field = linspace(-4, 4, M); %defining the boundary points
x2field = linspace(-4, 4, M);
ufield= zeros(M,M); %Numeric solution
exactfield = zeros(M,M); %Analytical function
%Defining given u and partial derivatives
u_func = @(x, y) exp((x + 0.3 * y) / 3) .* sin((0.3 .* x - y) / 3);
dx_u_func = @(x,y) exp((x + 0.3 * y) / 3) .* ((1/3).*sin((0.3.*x - y) / 3) + 0.1.*cos((0.3.* x - y)/3));
dy_u_func = @(x,y) exp((x + 0.3 * y) / 3) .* (0.1.*sin((0.3.*x - y) / 3) - (1/3).*cos((0.3.*x - y)/3));
% Dot product of the derivative and normal vector on the boundary of D
g_x = dx_u_func(y1,y2).*nu1;
g_y = dy_u_func(y1,y2).*nu2;
gvec = g_y'+g_x';
hvec = (-eye(N)/2+ 2*pi/N* A* diag(vecdsdt))\gvec; %Numerical solution to h
hvec = hvec.'; % Transponation
%Computation for loop of the analytical and numeric fields using the given
for ix1=1:M
    for ix2=1:M
        x1=x1field(ix1);
        x2=x2field(ix2);
        t=angle(complex(x1,x2));
        radius= 3+cos(4*t+pi); %formula for rvec
        if x1^2+ x2^2< radius^2
           phivec = log(vecnorm([y1;y2] - [x1;x2]))/(2*pi); %kernel expression in terms of xi, yi, i=1,2
           exactfield(ix1,ix2) = u_func(x1,x2); % using our analytical function
           ufield(ix1,ix2) = (phivec *(hvec.* vecdsdt).')*2*pi/N; %computing the numeric field
        end
    end
end
%Finding the average over a square matrix.
mean_num_matrix = mean(ufield(M/3:M*2/3,M/3:M*2/3), 'all');
mean_anal_matrix = mean(exactfield(M/3:M*2/3,M/3:M*2/3),'all');
% Finding the numeric solution adjusted with the constant
ufield_1 = ufield-mean_num_matrix+mean_anal_matrix;
errorfield = log10(abs(ufield_1-exactfield));

%% plotting using the given code in the appendix
imagesc(x1field, x2field, exactfield.')
axis xy
colormap turbo
pbaspect([1 1 1])
colorbar
title('Analytic solution of u(x,y)')

%%
imagesc(x1field, x2field, ufield_1.')
axis xy
colormap turbo
pbaspect([1 1 1])
colorbar
title('Numerical solution of u(x,y)')


%%
imagesc(x1field, x2field, errorfield.')
axis xy
colormap turbo
pbaspect([1 1 1])
colorbar
title('Error plot')
