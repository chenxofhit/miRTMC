#For Linux,
export PATH=$PATH:/usr/local/MATLAB/R2018b/bin/

#For Mac,
#export PATH=$PATH:/Applications/MATLAB_R2016b.app/bin/

#change directory
cd $1

cp ./run_water.sh $2
cp ./mtis7_utr.fasta $2
cp ./mtis7_utr_withid.txt $2

cd $2

./run_water.sh fasta.txt mtis7_utr.fasta utr_tmp_list
rm -r _tmp_splits
rm ___n___

sort -k1,1 -k2,2 -k3,3nr utr_tmp_list | sort -k1,1 -k2,2 -u > utr_tmp_list_sort
awk 'NR==FNR{a[$1]=$2;next}{print $0,'\t',a[$2]}' mtis7_utr_withid.txt utr_tmp_list_sort > tmp0
awk '{print $3,$4}' tmp0 > tmp0s
sort -k2,2n -k1,1nr tmp0s | sort -k2,2n -u > tmp0
awk '{print $1}' tmp0 > vec

#matlab -nosplash -nodisplay -nodesktop -nojvm -logfile $1'/matlab.out'  -r "new_gene('$1')"
cd $1
matlab -nosplash -nodisplay -nodesktop -nojvm -logfile $1'/matlab.out'  -r "new_gene('$2')"

cd $2
rm tmp_list
rm ./run_water.sh
rm ./mtis7_utr.fasta
rm ./mtis7_utr_withid.txt

