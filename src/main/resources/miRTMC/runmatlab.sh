cd $1
matlab -nosplash -nodisplay -nodesktop -nojvm -r "LDAP('$2')" > $2/matlab.out
