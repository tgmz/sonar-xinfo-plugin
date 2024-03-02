ASMPGM   TITLE 'contest program'                                        00040000
ASMPGM   CSECT                                                          00050000
*--------------------------------------------------------------------*  00060000
*        register equates                                            *  00070000
*--------------------------------------------------------------------*  00080000
R0       EQU   0                       register 0                       00090000
BASEREG  EQU   12                      base register                    00100000
SAVEREG  EQU   13                      save area register               00110000
RETREG   EQU   14                      caller's return address          00120000
ENTRYREG EQU   15                      entry address                    00130000
RETCODE  EQU   15                      return code                      00140000
         EJECT                                                          00150000
*--------------------------------------------------------------------*  00160000
*        standard entry setup, save area chaining, establish         *  00170000
*        base register and addressibility                            *  00180000
*--------------------------------------------------------------------*  00190000
         USING ASMPGM,ENTRYREG         establish addressibility         00200000
         B     SETUP                   branch around eyecatcher         00210000
         DC    CL8'ASMPGM'             program name                     00220000
         DC    CL8'&SYSDATE'           program assembled date           00230000
SETUP    STM   RETREG,BASEREG,12(SAVEREG)  save caller's registers      00240000
         BALR  BASEREG,R0              establish base register          00250000
         DROP  ENTRYREG                drop initial base register       00260000
         USING *,BASEREG               establish addressibilty          00270000
         LA    ENTRYREG,SAVEAREA       point to this program save area  00280000
         ST    SAVEREG,4(,ENTRYREG)    save address of caller           00290000
         ST    ENTRYREG,8(,SAVEREG)    save address of this program     00300000
         LR    SAVEREG,ENTRYREG        point to this program savearea   00310000
         EJECT                                                          00320000
*--------------------------------------------------------------------*  00330000
*        program body                                                *  00340000
*--------------------------------------------------------------------*  00350000
         L     2,=C'Begin'                                              00360001
         LA    2,=C'Begin'                                              00370001
LOOPINIT DS    0H                                                       00380000
         SR    2,2                                                      00390000
         L     2,=F'4'                                                  00400001
         L     3,=F'1'                                                  00410000
LOOP     DS    0H                                                       00420000
         A     3,=F'1'                                                  00430001
         BCT   2,LOOP                                                   00440001
STOP1    LH    3,HALFCON                                                00450001
STOP2    A     3,FULLCON                                                00460001
STOP3    ST    3,HEXCON                                                 00470001
         EJECT                                                          00471001
*--------------------------------------------------------------------*  00480000
*        standard exit -  restore caller's registers and             *  00490000
*        return to caller                                            *  00500000
*--------------------------------------------------------------------*  00510000
EXIT     DS    0H                      halfword boundary alignment      00520000
         L     SAVEREG,4(,SAVEREG)     restore caller's save area addr  00530000
         L     RETREG,12(,SAVEREG)     restore return address register  00540000
         LM    R0,BASEREG,20(SAVEREG)  restore all regs. except reg15   00550000
         WTO   'Giving control back to system'                          00551001
         BR    RETREG                  return to caller                 00560000
         EJECT                                                          00570000
*--------------------------------------------------------------------*  00580000
*        storage and constant definitions.                           *  00590000
*        print output definition.                                    *  00600000
*--------------------------------------------------------------------*  00610000
SAVEAREA DC    18F'-1'                 register save area               00710000
FULLCON  DC    F'-1'                                                    00711001
HEXCON   DC    XL4'9ABC'                                                00712001
HALFCON  DC    H'32'                                                    00713001
         END   ASMPGM                                                   00720000
