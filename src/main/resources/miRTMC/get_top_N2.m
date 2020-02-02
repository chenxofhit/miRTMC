function [status] = get_top_N2(m_or_g,i,result,N,resultpath)
result=result';
[m,n] = size(result);

if(m_or_g=='m')
    vec_i = result(:,i);
    [score,ind]=sort(vec_i,'descend');
    score_need = score(1:N,:);
    ind_need = ind(1:N,:);
end

if(m_or_g=='g')
    vec_i = result(i,:);
    [score,ind]=sort(vec_i,'descend');
    score = score';
    ind = ind';
    score_need = score(1:N,:);
    ind_need = ind(1:N,:);
end

dlmwrite([resultpath '/score.txt'], score_need, 'precision', '%8f', 'delimiter', '\t')
dlmwrite([resultpath  '/ind.txt'], ind_need, 'delimiter', '\t')

wi=fopen([resultpath '/success.txt'],'a');
fclose(wi);

exit;

