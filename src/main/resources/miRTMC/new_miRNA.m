function [] = new_miRNA(resultpath)
vec = importdata('vec');
vec = vec./100;

sim_m = importdata('sim_m.mat');
bmmat = importdata('bmMat.mat');
sim_g = importdata('sim_g.mat');

[m,n]=size(bmmat);

new_sim_m = zeros(n+1,n+1);
new_sim_m(1:n,1:n) = sim_m;
new_sim_m(n+1,1:n) = vec;
new_sim_m(n+1,n+1) = 1;
new_sim_m(1:n,n+1) = vec;

%% save('new_sim_m.mat','new_sim_m');

new_bmmat = zeros(m,n+1);
new_bmmat(1:m,1:n)=bmmat;

%%save('new_bmMat.mat','new_bmmat');

result = miRTMC(sim_g,new_sim_m,new_bmmat);
result = result./255;
% get_top_N2('m',2589,result,50,resultpath)
% small_data
get_top_N2('m',151,result,50,resultpath)
