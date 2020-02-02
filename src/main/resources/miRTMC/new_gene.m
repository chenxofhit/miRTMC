function [] = new_gene(resultpath)
vec = importdata('vec');
vec = vec./100;

sim_m = importdata('sim_m.mat');
bmmat = importdata('bmMat.mat');
sim_g = importdata('sim_g.mat');

[m,n]=size(bmmat);

new_sim_g = zeros(m+1,m+1);
new_sim_g(1:m,1:m) = sim_g;
new_sim_g(m+1,1:m) = vec;
new_sim_g(m+1,m+1) = 1;
new_sim_g(1:m,m+1) = vec;

%% save('new_sim_g.mat','new_sim_g');

new_bmmat = zeros(m+1,n);
new_bmmat(1:m,1:n)=bmmat;

%% save('new_bmMat.mat','new_bmmat');

result = miRTMC(new_sim_g,sim_m,new_bmmat);
result = result./255;
% get_top_N2('g',14743,result,50,resultpath)
% small data set
get_top_N2('g',101,result,50,resultpath)
