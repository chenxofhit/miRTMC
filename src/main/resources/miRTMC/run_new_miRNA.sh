#For Linux,
#export PATH=$PATH:/usr/local/MATLAB/R2018b/bin/

#For Mac,
export PATH=$PATH:/Applications/MATLAB_R2016b.app/bin/

#change directory
cd $1

./run_needle.sh fasta.txt miRNA3.fa tmp_list
rm -r _tmp_splits
rm ___n___

awk 'NR==FNR{a[$2]=$1;next}{print a[$2],'\t',$0}' mtis7_miRNA_list_final_withid.txt tmp_list >tmp0
sort -k1,1n tmp0 > tmp_sort
awk '{print $4}' tmp_sort > vec

rm tmp0
rm tmp_sort

#matlab -nosplash -nodisplay -nodesktop -nojvm -logfile $1'/matlab.out'  -r "new_miRNA('$1')"
matlab -nosplash -nodisplay -nodesktop -nojvm -logfile $1'/matlab.out'  -r "new_miRNA('$2')"


rm vec



