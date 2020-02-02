function KK = GM( Kernel )
%GM Summary of this function goes here
%   Detailed explanation goes here
K1=Kernel(:,:,1);
K2=Kernel(:,:,2);
KK=(K1.^(1/2))*(((K1.^(-1/2))*K2*(K1.^(-1/2))).^(1/2))*(K1.^(1/2));

end

