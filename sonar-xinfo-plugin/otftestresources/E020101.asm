*       Exercise 2.1.1 ******
Test    Start 0
        Print NoGen
        BAKR  14,0          SAVE CALLERS ENVIRONMENT
        BASR  12,0          Establish a base register
        Using *,12          tell the assembler about it
        OPEN  (RECDD,OUTPUT)
*--------------------------------------------------------------------*
*       exercise starts here
*       (a) ******
        L     4,=B'0000 0000 0000 0000 0000 0000 0001 0110'
        BAS   7,DigStr      call subroutine
*       (b) ******
        L     4,=B'0000 0000 0000 0000 0000 0000 0010 1100'
        BAS   7,DigStr      call subroutine
*       (c) ******
        L     4,=B'0000 0000 0000 0000 0000 0000 1010 1010'
        BAS   7,DigStr      call subroutine
*       (d) ******
        L     4,=B'0000 0000 0000 0000 0000 0000 0111 1111'
        BAS   7,DigStr      call subroutine
*        exercise ends here
*--------------------------------------------------------------------*
        CLOSE (RECDD)
        J     EXIT
*--------------------------------------------------------------------*
*       Subroutine DigStr
*         converts register value to digits string
*         representation and prints it
*       Register Usage:
*         4 value to get converted (will be destroyed)
*         5 auxiliar register
*         6 counter
*         7 return address
*       Input parameters:
*         Base - number base used for conversion
*         StrLen - length of string,
*                  i.e. number of digits for output
*       References: J.R. Ehrman,
*         Exercises 17.3.19 and 22.4.8
*         Sections 18.6.1, 37.1.1 and 37.1.2
*       Author: Elmar Melcher, UFCG
DigStr  L     6,StrLen      Get number of characters to produce
NxtDig  SRDA  4,32          Sign-extend to double length
        D     4,Base        Divide by base, remainder in 4
        IC    4,EBCDIC(4)   Get character form of remainder
        STC   4,StrBuf(6)   Store remainder as character in string
        SLDL  4,32          Copy quotient from 5 to 4
        BCT   6,NxtDig      Repeat
        PUT   RECDD,StrBuf  Send string to output
        BR    7             Return - End of subroutine
*--------------------------------------------------------------------*
EXIT    DS    0H             HALFWORD BOUNDRY ALIGNMENT
        PR                   RETURN TO CALLER
        EJECT
Base    DC    F'10'          Converson base (max 16)
StrLen  DC    F'3'           Length of string (max 16)
EBCDIC  DC    C'0123456789ABCDEF'   EBCDIC form of hex digits
StrBuf  DC    C'                 '  buffer for converted string
RECDD   DCB   DSORG=PS,MACRF=PM,DDNAME=RECDD,RECFM=FB,LRECL=17
        END Test
