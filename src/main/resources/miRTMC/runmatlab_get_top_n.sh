#!bin/bash
#Author: chenx<chenxofhit@gmail.com>
#NOTE: I am not responsible for the script itself, Dr. Jianghui asked me to do this script, if any question please cue him.
#Since: 2020-01-16 15:10:03 happy Chinese new year!
#If you want to be a researcher, please note that you do not have free time but only to  think and code always. Freedom bless you! 

#You can comment on below line of export, if you find the Matlab has been configurated successfully!

#For Linux,
export PATH=$PATH:/usr/local/MATLAB/R2018b/bin/

#For Mac,
#export PATH=$PATH:/Applications/MATLAB_R2016b.app/bin/

cd $1
matlab -nosplash -nodisplay -nodesktop -nojvm -logfile $7'/matlab.out'  -r "get_top_N('$2',$3,'$4','$5',$6,'$7')"

#This is the symbol that the shell has exited!
echo 'finished'
