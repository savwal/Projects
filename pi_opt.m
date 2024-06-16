function [F,Ki,Ti,phim,wc,MS,MT]=pi_opt(G,wc_min,wc_max,phim_min,phim_max)

MS_max = 1.7;
MT_max = 1.3;
Nphim = 20;
Nwc = 30;

for k=1:Nphim % 
   phim(k) = phim_min+(phim_max-phim_min)/(Nphim-1)*(k-1);
   for i=1:Nwc
      wc(i) = wc_min+(wc_max-wc_min)/(Nwc-1)*(i-1);
      j = i+(k-1)*Nwc;
      [F(:,:,j),Ki(j),Ti(j)] = pi_phi(G,phim(k),wc(i));
      kk(j,:) = [k i];
   end
end

warning off;
MS = norm(feedback(1,G*F),inf,1e-3)';
MT = norm(feedback(G*F,1),inf,1e-3)';
stab = norm(feedback(G*F,1))';
[Ki,j] = max(Ki.*(MS <= MS_max).*(MT <= MT_max).*(stab < inf));
F = F(:,:,j); phim = phim(kk(j,1)); wc = wc(kk(j,2)); 
Ti = Ti(j); MS = MS(j); MT = MT(j);

warning on;
if Ki==0, 
    warning('No PI-controller achieved');
    F = []; Ki = []; Ti = []; phim = []; wc = [];
else
   if kk(j,1)==1, warning('Optimum achieved at phim_min'); end
   if kk(j,1)==Nphim, warning('Optimum achieved at phim_max'); end
   if kk(j,2)==1, warning('Optimum achieved at wc_min'); end
   if kk(j,2)==Nwc, warning('Optimum achieved at wc_max'); end
end
warning backtrace;

function [F,Ki,Ti]=pi_phi(G,phim,wc)

[absG,phiG] = bode(G,[wc]); % absG=abs(G(j*omega_c)),  phiG=arg(G(j*omega_c)) in degrees
Ti = 1/wc * tand(phim-90-phiG);  % justera till ett generellt uttryck för Ti och Ki
Ki = wc/(absG*sqrt(1+(wc*Ti)^2));

F = tf([Ki*Ti Ki],[1 0]); %F=Ki*(1+Ti*s)/s
