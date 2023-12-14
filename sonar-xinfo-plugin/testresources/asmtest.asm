         TITLE 'REZ09 - SETZEN RETURNCODE UEBER PARM'
* Macro for initializing         
REZ09    YINITP
         PACK  DWORT,2(3,R14)
         CVB   R4,DWORT
         LR    R15,R4
         B     RETURN                  GOBACK
         SPACE 1
DWORT    DC    D'0'
SIGN     DC    UPPER('tz')
         END   REZ09
