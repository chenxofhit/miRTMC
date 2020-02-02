function [status] = get_top_N(m_or_g,i,result_url,bmMat_url,N,resultpath)
result_matrix=importdata(result_url);
result_matrix=result_matrix';
bmMat = importdata(bmMat_url);
[m,n] = size(result_matrix);
[m1,n1] =size(bmMat);
if(m~=m1||n~=n1)
    status = 1;
    return;
end

P = find(bmMat==1);
result_matrix(P)=-10;

if(m_or_g=='m')
    known = find(bmMat(:,i)==1);
    vec_i = result_matrix(:,i);
    [score,ind]=sort(vec_i,'descend');
    score_need = score(1:N,:);
    ind_need = ind(1:N,:);
end

if(m_or_g=='g')
     known = find(bmMat(i,:)==1);
     known = known';
     vec_i = result_matrix(i,:);
    [score,ind]=sort(vec_i,'descend');
    score = score';
    ind = ind';
    score_need = score(1:N,:);
    ind_need = ind(1:N,:);
end

dlmwrite([resultpath '/score.txt'], score_need, 'precision', '%8f', 'delimiter', '\t')
dlmwrite([resultpath  '/ind.txt'], ind_need, 'delimiter', '\t')
dlmwrite([resultpath '/known.txt'], known, 'delimiter', '\t')

wi=fopen([resultpath '/success.txt'],'a');
fclose(wi);

exit;