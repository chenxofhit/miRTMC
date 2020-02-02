function [ Result_Auc ] = Fun_Auc( R_Wdr,WdrOrg,TestIds )
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here
    Tfnum = length(TestIds);
    Known_Ids = find(WdrOrg==1);
    [dn,dr] = size(R_Wdr);

    A_DresultMat_TPR = zeros(Tfnum,dn);
    A_DresultMat_FPR = zeros(Tfnum,dn);
    DresultMat = zeros(dn,dr);
    Qvalue = R_Wdr(TestIds);

	thresh_value = min(R_Wdr(:))-10;
    thresh_value = ceil(thresh_value);
	% disp(thresh_value);
    R_Wdr(Known_Ids)= thresh_value;
    DresultMat(Known_Ids)= thresh_value;

    S_ResultMat = sort(R_Wdr,'descend');
    DresultMat = sort(DresultMat,'descend');
    for k=1:Tfnum
        rdPos = TestIds(k);
        rindex = ceil(rdPos/dn);
        eQvalue = Qvalue(k);
        TfindposMat =  find(S_ResultMat(:,rindex)==eQvalue);
        TfindposMatlen = size(TfindposMat,1);
        Tfindpos = TfindposMat(TfindposMatlen);
        result_Mat = DresultMat(:,rindex);
        result_Mat(result_Mat==thresh_value)=[];
        result_Mat(Tfindpos) = 1;
        result_len = length(result_Mat);

        TPRArray = zeros(1,dn);
        FPRArray = zeros(1,dn);
        
        CountP =  1;
        CountN =  result_len-1;
        
        Tpnum = 0;
        Fpnum = 0;
        for m =1:result_len
            if(result_Mat(m)==1)
                Tpnum = Tpnum + 1;
            else
                Fpnum = Fpnum + 1;
            end
            TPRArray(m) = Tpnum/CountP;
            FPRArray(m) = Fpnum/CountN;
        end
        TPRArray(result_len+1:dn) = TPRArray(result_len);
        FPRArray(result_len+1:dn) = FPRArray(result_len);
        
        A_DresultMat_TPR(k,:) = TPRArray;
        A_DresultMat_FPR(k,:) = FPRArray;
        
    end %end for k;
	
    DresultMat_TPR = mean(A_DresultMat_TPR);
    DresultMat_FPR = mean(A_DresultMat_FPR);
    Result_Auc = trapz(DresultMat_FPR,DresultMat_TPR);
    
end

