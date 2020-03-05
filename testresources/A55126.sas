/*-------------------------------------------------------------------*/
/*                  SAS Programming by Example                       */
/*                 by Ron Cody and Ray Pass                          */
/*       Copyright(c) 1995 by SAS Institute Inc., Cary, NC, USA      */
/*                   SAS Publications order # 55126                  */
/*                        ISBN 1-1-55544-681-7                       */
/*-------------------------------------------------------------------*/
/*                                                                   */
/* This material is provided "as is" by SAS Institute Inc.  There    */
/* are no warranties, expressed or implied, as to merchantability or */
/* fitness for a particular purpose regarding the materials or code  */
/* contained herein. The Institute is not responsible for errors     */
/* in this material as it now exists or will exist, nor does the     */
/* Institute provide technical support for it.                       */
/* Several programs reference one of three SAS data sets, CLINICAL,  */ 
/* MEDICAL, and SALES.  Three SAS programs, CLINICAL.SAS,            */ 
/* MEDICAL.SAS, and SALES.SAS, can be downloaded and run on your     */ 
/* computer to create these data sets.  These programs appear at     */ 
/* the end of this file.                                             */
/*                                                                   */
/*-------------------------------------------------------------------*/
/* Questions or problem reports concerning this material may be      */
/* addressed to the author:                                          */
/*                                                                   */
/* SAS Institute Inc.                                                */
/* Books by Users                                                    */
/* Attn: Ron Cody and Ray Pass                                       */
/* SAS Campus Drive                                                  */
/* Cary, NC   27513                                                  */
/*                                                                   */
/*                                                                   */
/* If you prefer, you can send email to:  saspress@sas.com           */
/* Use this for subject field:                                       */
/*     Comments for Ron Cody and Ray Pass                            */
/*                                                                   */
/*-------------------------------------------------------------------*/
/* Date Last Updated: 12Sep06                                        */
/*-------------------------------------------------------------------*/

***Chapter 1 Code and data;
*--------------- EXAMPLE 1 ------------------;
DATA LISTINP;
   INPUT ID HEIGHT WEIGHT GENDER $ AGE;
DATALINES;
1 68 144 M 23
2 78 202 M 34
3 62 99 F 37
4 61 101 F 45
;

PROC PRINT;
   TITLE 'Example 1';
RUN;
*---------------------------------------------;

*--------------- EXAMPLE 2.1 ----------------;
DATA COMMAS;
   INFILE DATALINES DLM=',';
   INPUT ID HEIGHT WEIGHT GENDER $ AGE;
DATALINES;
1,68,144,M,23
2,78,202,M,34
3,62,99,F,37
4,61,101,F,45
;

PROC PRINT;
   TITLE 'Example 2.1';
RUN;
*---------------------------------------------;

*--------------- EXAMPLE 2.2 ----------------;
DATA COMMAS;
   INFILE DATALINES DSD;
   INPUT X Y TEXT;
DATALINES;
1,2,XYZ
3,,STRING 
4,5,"TESTING"
6,,"ABC,XYZ"
;

PROC PRINT;
   TITLE 'Example 2.2';
RUN;
*---------------------------------------------;

*---------------- EXAMPLE 3.1 --------------------;
DATA INFORMS; 
   INFORMAT LASTNAME $20. DOB MMDDYY8. GENDER $1.; 
   INPUT ID LASTNAME DOB HEIGHT WEIGHT GENDER AGE; 
   FORMAT DOB MMDDYY8.;
DATALINES; 
1 SMITH 1/23/66 68 144 M 26
2 JONES 3/14/60 78 202 M 32 
3 DOE 11/26/47 62 99 F 45
4 WASHINGTON 8/1/70 66 101 F 22 
;

PROC PRINT;
   TITLE 'Example 3.1';
RUN;
*-------------------------------------------------;

*---------------- EXAMPLE 3.2 --------------;
DATA COLONS; 
 INPUT ID LASTNAME : $20. DOB : MMDDYY8. 
       HEIGHT WEIGHT GENDER : $1. AGE; 
FORMAT DOB MMDDYY8.;
DATALINES; 
1 SMITH 01/23/66 68 144 M 26
2 JONES 3/14/60 78 202 M 32 
3 DOE 11/26/47 62 99 F 45 
4 WASHINGTON 8/1/70 66 101 F 22 
; 

PROC PRINT;
   TITLE 'Example 3.2';
RUN;
*--------------------------------------------;

*--------------- EXAMPLE 4 --------------;
DATA AMPERS; 
 INPUT NAME & $25. AGE GENDER : $1.; 
DATALINES; 
RASPUTIN    45 M
BETSY ROSS  62 F
ROBERT LOUIS STEVENSON  75 M
; 

PROC PRINT;
   TITLE 'Example 4';
RUN;
*-----------------------------------------;

*---------------------- EXAMPLE 5.1 ----------------------;
DATA COLINPUT; 
   INPUT ID 1 HEIGHT 2-3 WEIGHT 4-6 GENDER $ 7 AGE 8-9; 
DATALINES; 
168144M23 
278202M34 
362 99F37 
461101F45 
; 

PROC PRINT;
   TITLE 'Example 5.1';
RUN;
*----------------------------------------------------------;

*--------------------- EXAMPLE 5.2 -----------------------;
DATA COLINPUT; 
   INPUT ID       1 
      HEIGHT    2-3 
      WEIGHT    4-6 
      GENDER  $   7 
      AGE       8-9; 
DATALINES; 
168144M23 
278202M34 
362 99F37 
461101F45 
; 

PROC PRINT;
   TITLE 'Example 5.2';
RUN;
*----------------------------------------------------------;

*--------------- EXAMPLE 5.3 -------------;
DATA COLINPUT; 
   INPUT  ID  1 
          AGE 8-9; 
DATALINES; 
168144M23 
278202M34 
362 99F37 
461101F45 
; 

PROC PRINT;
   TITLE 'Example 5.3';
RUN;
*------------------------------------------;

*--------------- EXAMPLE 5.4 -------------;
DATA COLINPUT; 
   INPUT  AGE      8-9 
          ID       1 
          WEIGHT   4-6 
          HEIGHT   2-3 
          GENDER $ 7; 
DATALINES; 
168144M23 
278202M34 
362 99F37 
461101F45 
; 

PROC PRINT;
   TITLE 'Example 5.4';
RUN;
*------------------------------------------;

*------------ EXAMPLE 6.1 ---------;
DATA POINTER; 
   INPUT @1  ID       3. 
         @5  GENDER  $1. 
         @7  AGE      2. 
         @10 HEIGHT   2. 
         @13 DOB      MMDDYY6.; 
FORMAT DOB MMDDYY8.;
DATALINES; 
101 M 26 68 012366
102 M 32 78 031460
103 F 45 62 112647
104 F 22 66 080170
;

PROC PRINT;
   TITLE 'Example 6.1';
RUN;
*-----------------------------------; 

*------------- EXAMPLE 7.1 -------------;
DATA POINTER; 
   INPUT #1 @1  ID       3. 
            @5  GENDER  $1. 
            @7  AGE      2. 
            @10 HEIGHT   2. 
            @13 DOB      MMDDYY6.
         #2 @5  SBP   3.
            @9  DBP   3.
            @13 HR    3.; 
FORMAT DOB MMDDYY8.;
DATALINES; 
101 M 26 68 012366
101 120  80 68
102 M 32 78 031460
102 162  92 74  
103 F 45 62 112647
103 134  86 74
104 F 22 66 080170
104 116  72 67
;

PROC PRINT;
   TITLE 'Example 7.1';
RUN;
*----------------------------------------;

*------------- EXAMPLE 7.2 ---------;
DATA SKIPSOME;
   INPUT #2 @1  ID   3.
            @12 SEX $6.
         #4;
DATALINES;
101 256 RED   9870980
101 898245 FEMALE 7987644
101 BIG   9887987
101 CAT 397  BOAT 68
102 809 BLUE  7918787
102 732866 MALE   6856976
102 SMALL 3884987 
102 DOG 111  CAR  14
;

PROC PRINT;
TITLE 'Example 7.2';
RUN;
*------------------------------------;

*----------------- EXAMPLE 8.1 -----------------;
DATA PARTS;
   INPUT @1   PARTID    $14.
         @1   ST         $2.
         @6   WT          3.
         @13  YR          2.
         @16  PARTDESC  $24.  
         @41  QUANT       4.; 
DATALINES;
NY101110060172 LEFT-HANDED WHIZZER       233
MA102085112885 FULL-NOSE BLINK TRAP     1423
CA112216111291 DOUBLE TONE SAND BIT       45
NC222845071970 REVERSE SPIRAL RIPSHANK   876
;

PROC PRINT;
   TITLE 'Example 8.1';
RUN;
*------------------------------------------------;

*------------- EXAMPLE 9.1 ----------;
DATA LONGWAY; 
   INPUT ID     1-3 
         Q1     4 
         Q2     5 
         Q3     6 
         Q4     7 
         Q5     8 
         Q6     9-10 
         Q7     11-12 
         Q8     13-14 
         Q9     15-16 
         Q10    17-18 
         HEIGHT 19-20 
         AGE    21-22; 
DATALINES; 
1011132410161415156823
1021433212121413167221
1032334214141212106628
1041553216161314126622
;

PROC PRINT;
   TITLE 'Example 9.1';
RUN;
*-------------------------------------;

*------------- EXAMPLE 9.2 ----------;
DATA SHORTWAY; 
   INPUT ID 1-3 
      @4 (Q1-Q5)(1.) 
      @9 (Q6-Q10 HEIGHT AGE)(2.); 
DATALINES; 
1011132410161415156823
1021433212121413167221
1032334214141212106628
1041553216161314126622
;

PROC PRINT;
   TITLE 'Example 9.2';
RUN;
*-------------------------------------;

*------------- EXAMPLE 9.3 -----------;
DATA PAIRS; 
    INPUT @1 ID 3. 
     @6  (QN1-QN5)(1. +3) 
     @7  (QC1-QC5)($1. +3)
     @26 (HEIGHT AGE)(2. +1 2.); 
DATALINES; 
101  1A  3A  4B  4A  6A  68 26
102  1A  3B  2B  2A  2B  78 32
103  2B  3D  2C  4C  4B  62 45
104  1C  5C  2D  6A  6A  66 22
;

PROC PRINT;
   TITLE 'Example 9.3';
RUN;
*--------------------------------------;

*------------- EXAMPLE 10 ------------;
DATA MIXED; 
   INPUT @20 TYPE $1. @;
   IF TYPE = '1' THEN
      INPUT ID     1-3 
            AGE    4-5 
            WEIGHT 6-8; 
   ELSE IF TYPE = '2' THEN 
      INPUT ID     1-3 
            AGE    10-11 
            WEIGHT 15-17; 
DATALINES; 
00134168           1 
00245155           1 
003      23   220  2 
00467180           1 
005      35   190  2 
;

PROC PRINT;
   TITLE 'Example 10.1';
RUN;
*--------------------------------------;

*----------- EXAMPLE 11.1 --------;
DATA LONGWAY; 
   INPUT X Y; 
DATALINES; 
1 2 
3 4 
5 6 
6 9 
10 12 
13 14 
; 

PROC PRINT;
   TITLE 'Example 11.1';
RUN;
*----------------------------------;

*----------------------- EXAMPLE 11.2 --------------------;
DATA SHORTWAY; 
   INPUT X Y @@; 
DATALINES; 
1 2 3 4 5 6 
6 9 10 12 13 14 
; 

PROC PRINT;
   TITLE 'Example 11.2';
RUN;
*----------------------------------------------------------;

*-------- EXAMPLE 12.1 -------;
DATA ERRORS; 
   INPUT X 1-2 
         Y 4-5; 
DATALINES; 
11 23 
23 NA 
NA 47 
55 66 
; 
*------------------------------;

     *---------- EXAMPLE 12.2 -----;
     DATA ERRORS; 
        INPUT X ?? 1-2 
              Y ?? 4-5; 
     DATALINES; 
     11 23 
     23 NA 
     NA 47 
     55 66 
     ; 
     *------------------------------;

     *---------------- EXAMPLE 13.1 --------------;
     DATA EXTERNAL; 
         INFILE  'C:\MYDATA\HTWT'; 
         INPUT    ID HEIGHT WEIGHT GENDER $ AGE; 
     RUN; 
     *--------------------------------------------;

     *---------------- EXAMPLE 13.2 -------------;
     FILENAME OSCAR 'C:\MYDATA\HTWT'; 

     DATA EXTERNAL; 
        INFILE  OSCAR; 
        INPUT   ID HEIGHT WEIGHT GENDER $ AGE; 
     RUN; 
     *--------------------------------------------;

     *------------------ EXAMPLE 15.1 -------------------;
     DATA TWOFILES; 
        IF NOT LASTREC1 THEN INFILE 'FILE1' END=LASTREC1; 
                        ELSE INFILE 'FILE2'; 
        INPUT ID AGE WEIGHT; 
     RUN; 
     *---------------------------------------------------;

     *----------------- EXAMPLE 15.2 --------------;
     DATA TWOFILES; 
        INPUT EXTNAME $;
        INFILE DUMMY FILEVAR=EXTNAME END=LASTREC;
        DO UNTIL (LASTREC=1);
           INPUT ID AGE WEIGHT;
           OUTPUT;
        END; 
     DATALINES;
     FILE1
     FILE2
     ; 
     *----------------------------------------------;

***Chapter 1 problems;

Problem 1 - sample data
external file "VITAL"

  A1 68 130 80
  B3 101  148 86
  C2 . . 72
  D1    72   140   88 

Problem 2 - sample data
  external file "VITALC"
 
  A1,68,130,80
  B3,101, 148,86
  C2,.,.,72
  D1,   72,  140 , 88

Problem 3 - sample data

Stevenson Ph.D. Y 2
Smith Ph.D.   N   3
Goldstein  M.D.  Y  1

Problem 4 - sample data

George Stevenson   Ph.D. Y 2
Fred Smith   Ph.D.   N   3
Alissa Goldstein  M.D.  Y  1

Problem 5 - Sample Data (in file FIRE)

001 10/21/94 03 2
002 10/23/94 01 1
003 11/01/94 11 3

Problem 7 - sample data

13AB2NY44   $123
22XXXCT88 $1,033
37123TX11$22,999

Problem 8 - Sample data

123-45-6789 100 98 96 95 92 88 95 98100 90
344-56-7234  69 79 82 65 88 78 78 92 66 77
898-23-1234  80 80 82 86 92 78 88 84 85 83

Problem 9 - sample data

120 80122 84128 90130 92
140102138 96136 92128 84
122 80122 80124 82122 78

Problem 10 - Sample Data (in file MIXED_UP)

00168155   1
002 70 200 2
00362102   1
004 74 180 2

Problem 11 - Sample Data

Taurus 20 Civic 29 Cutlass 20 Cadillac 17
Mazada 24 Corvette 17

Problem 12 - sample data
        FILE_ONE
    ---------------
CODY      100
PASS       98
BAGGETT    96
GOODNIGHT  45

        FILE_TWO
    ---------------
FRANKS     66
BEANS      68


***Chapter 2 - code and data;

Data set GRADES

ID  SCORE
 1    55
 2    65
 3    74
 4    76
 5    88
 6    92
 7    94
 8    96
 9    98

*--------------- EXAMPLE 1.1 ---------------;
DATA RECODE;
   SET GRADES;
   IF   0 LE SCORE LT 65 THEN GRADE=0;
   ELSE IF 65 LE SCORE LT 70 THEN GRADE=1;
   ELSE IF 70 LE SCORE LT 80 THEN GRADE=2;
   ELSE IF 80 LE SCORE LT 90 THEN GRADE=3;
   ELSE IF       SCORE GE 90 THEN GRADE=4;
RUN;

PROC PRINT;
   TITLE 'Example 1.1';
RUN;
*-------------------------------------------;

*--------------- EXAMPLE 1.2 ---------------;
DATA RECODE;
   SET GRADES;
   SELECT;
      WHEN (0 LE SCORE LT 65)  GRADE = 0;
      WHEN (65 LE SCORE LT 70) GRADE = 1;
      WHEN (70 LE SCORE LT 80) GRADE = 2;
      WHEN (80 LE SCORE LT 90) GRADE = 3;
      WHEN (SCORE GE 90)       GRADE = 4;
   END;
RUN;

PROC PRINT;
   TITLE 'Example 1.2';
RUN;
*-------------------------------------------;

*---------------- EXAMPLE 2 ------------------;
PROC FORMAT;
   VALUE SCOREFMT   0 - 64   = 'Fail'
                   65 - 70   = 'Low Pass'
                   70 - 80   = 'Pass'
                   80 - 90   = 'High Pass'
                   90 - HIGH = 'Honors';
RUN;

PROC FREQ DATA=GRADES;
   TITLE 'Example 2';
   TABLES SCORE;
   FORMAT SCORE SCOREFMT.;
RUN;                                                        
*---------------------------------------------;

PROC FORMAT;
   VALUE SCOREFMT   0 - <65   = 'Fail'
                 65 - <70   = 'Low Pass'
                 70 - <80   = 'Pass'
                 80 - <90   = 'High Pass'
                 90 - HIGH = 'Honors';
RUN;

*------------------- EXAMPLE 3 ---------------;
DATA NEW;
   SET GRADES;
   CATEGORY = PUT (SCORE,SCOREFMT.);
RUN;

PROC PRINT;
   TITLE 'Example 3';
RUN;                                                       
*---------------------------------------------;

***Chapter 3 - code and data;

*----------- EXAMPLE 1.1 -----------;
LIBNAME MARY 'C:\EMPLOYEE\JOBDATA';

DATA NJEMPLOY;
   SET MARY.EMPLOY;
   IF STATE EQ 'NJ';
RUN;
*-----------------------------------;

*----------- EXAMPLE 1.2 ------------;
LIBNAME MARY 'C:\EMPLOYEE\JOBDATA'; 

DATA NJEMPLOY;
   SET MARY.EMPLOY;
   WHERE STATE EQ 'NJ';
RUN;
*------------------------------------;

*------------ EXAMPLE 1.3 -------------;
LIBNAME MARY 'C:\EMPLOYEE\JOBDATA'; 

DATA NJEMPLOY;
   SET MARY.EMPLOY;
   WHERE STATE EQ 'NJ';
   KEEP ID DEPT;
RUN;
*--------------------------------------;

*--------------- EXAMPLE 1.4 --------------;
LIBNAME MARY 'C:\EMPLOYEE\JOBDATA'; 

DATA NJEMPLOY;
   SET MARY.EMPLOY (KEEP=ID DEPT STATE);
   WHERE STATE EQ 'NJ';
   DROP STATE;
RUN;
*------------------------------------------;

       Data set SURVEY1

ID  GENDER  HEIGHT  WEIGHT  YEAR 
 1     1      68      155    91 
 7     2      72      205    90 
 9     1      66      120    93 


       Data set SURVEY2

ID  GENDER  HEIGHT  WEIGHT  YEAR 
 5     2      63      102    92 
 8     1      70      250    91 

*------------- EXAMPLE 2.1 ----------;
LIBNAME GEORGE 'C:\DATA\SURVEYS';  

DATA GEORGE.SURV1_2
   SET GEORGE.SURVEY1 GEORGE.SURVEY2;
RUN;

PROC PRINT;
   TITLE 'Combined Data Sets 1 and 2';
RUN;
*------------------------------------;

       Data set SURVEY3

ID  GENDER  HEIGHT  YEAR  IQ 
 5     2      63     92   120 
 8     1      70     91   110 

*------------- EXAMPLE 2.2 ----------;
LIBNAME GEORGE 'C:\DATA\SURVEYS';  

DATA GEORGE.SURV1_3
   SET GEORGE.SURVEY1 GEORGE.SURVEY3;
RUN;

PROC PRINT;
   TITLE 'Combined Data Sets 1 and 3';
RUN;
*------------------------------------;

*------------ EXAMPLE 3 -------------;
DATA AMBIDEX;
   MERGE LEFT RIGHT;
RUN;

PROC PRINT;
   TITLE 'Simple One-to-One Merge';
RUN;
*------------------------------------;

        Data set DEMOG          Data set EMPLOYEE
                              
    ID  GENDER  STATE        ID    DEPT  SALARY 
     1     M      NY          1   PARTS  21,000 
     5     M      NY          2   SALES  45,000 
     2     F      NJ          3   PARTS  20,000 
     3     F      NJ          5   SALES  35,000 

*--------------- EXAMPLE 4.1 ----------------;
PROC SORT DATA=DEMOG;
   BY ID;
RUN;

PROC SORT DATA=EMPLOYEE;
   BY ID;
RUN;

DATA COMBINED;
   MERGE DEMOG EMPLOYEE;
   BY ID;
RUN;

PROC PRINT;
   TITLE 'DEMOG-EMPLOYEE Match-Merged Data';
RUN;
*--------------------------------------------;

   Data set DEMOG2              Data set EMPLOYEE

ID  GENDER  STATE               ID   DEPT  SALARY 
 1    M      NY                  1  PARTS  21,000 
 4    M      NY                  2  SALES  45,000
 5    M      NY                  3  PARTS  20,000
 2    F      NJ                  5  SALES  35,000 
 3    F      NJ            

*---------------- EXAMPLE 4.2 -----------------;
PROC SORT DATA=DEMOG2;
   BY ID;
RUN;

PROC SORT DATA=EMPLOYEE;
   BY ID;
RUN;

DATA COMBINED;
   MERGE DEMOG2 EMPLOYEE;
   BY ID;
RUN;

PROC PRINT;
   TITLE 'DEMOG2-EMPLOYEE Match-Merged Data';
RUN;
*----------------------------------------------;

*------------ EXAMPLE 5.1 -------------;
DATA BOTH;
   MERGE DEMOG2  EMPLOYEE (IN=EMP);
   BY ID;
   IF EMP = 1;
RUN;
*--------------------------------------;
*---------------- EXAMPLE 5.2 ---------------;
DATA BOTH;
   MERGE DEMOG2 (IN=DEM)  EMPLOYEE (IN=EMP);
   BY ID;
   IF DEM = 1 AND EMP = 1;
RUN;
*--------------------------------------------;

*--------------- EXAMPLE 6.1 ---------------; 
PROC SORT DATA=DEMOG2;
   BY ID;
RUN;

PROC SORT DATA=EMPLOYEE;
   BY ID;
RUN;

DATA ACTIVE INACTIVE (KEEP=ID GENDER STATE); 
   MERGE DEMOG2 EMPLOYEE (IN=ACT);
   BY ID;
   IF ACT = 1 THEN OUTPUT ACTIVE;
              ELSE OUTPUT INACTIVE;    
RUN;
     
PROC PRINT DATA=ACTIVE;
   TITLE 'Active Employees';
RUN;

PROC PRINT DATA=INACTIVE;
   TITLE ' Inactive Employees';
RUN;
*-------------------------------------------;

*-------------- EXAMPLE 6.2 ----------------; 
DATA COMBINED;
   MERGE ONE TWO (RENAME=(EMP_NUM=EMP_ID));
   BY EMP_ID;
RUN;
*-------------------------------------------; 

          Data set ONE                 Data set TWO

     NAME      DOB    HEIGHT       NAME        DOB    WEIGHT

     CODY    10/21/46   68         MCKLEARY   9/01/55  200
     CLARK    5/01/40   70         COTY      10/21/46  152
     CLARKE   5/10/45   72         CLARK      7/02/60  160
     ALBERT  10/01/46   69         ALBIRT    10/01/46  200
                                   CLARKE     5/01/40  210

*-------------- EXAMPLE 7 -------------------; 
DATA ONE_TEMP;
   SET ONE (RENAME=(NAME=NAME_ONE));
   S_NAME = SOUNDEX(NAME_ONE);
RUN;

DATA TWO_TEMP;
   SET TWO (RENAME=(NAME=NAME_TWO));
   S_NAME = SOUNDEX(NAME_TWO);
RUN;

PROC SORT DATA=ONE_TEMP;
   BY S_NAME DOB;
RUN;

PROC SORT DATA=TWO_TEMP;
   BY S_NAME DOB;
RUN;

PROC PRINT DATA=ONE_TEMP NOOBS;
   TITLE 'Data Set ONE_TEMP';
RUN;

PROC PRINT DATA=TWO_TEMP NOOBS;
   TITLE 'Data Set TWO_TEMP';
RUN;

DATA BOTH;
   MERGE ONE_TEMP (IN=ONE)
         TWO_TEMP (IN=TWO);
   BY S_NAME DOB;
   IF ONE = 1 AND TWO = 1;
   FORMAT DOB MMDDYY8.;
RUN;

PROC PRINT DATA=BOTH NOOBS;
   TITLE 'Data Set BOTH';
RUN;
*--------------------------------------------;

  Data set MASTER           Data set TRANS

D   DEPT    SALARY        ID  DEPT    SALARY
1   PARTS   13,000         2          22,000
2   PERSON  21,000         3  SALES   24,000
3   PARTS   15,000         5  RECORDS
4   EXEC    55,000         
5           18,000         
      

*------------ EXAMPLE 8.1 ------------;
DATA NEWMAST;
   UPDATE MASTER TRANS;
   BY ID;
RUN;
          
PROC PRINT;
   TITLE 'Updated Data Set - NEWMAST';
RUN;
*-------------------------------------;

*---------- EXAMPLE 8.2 ------------;
DATA MASTER;
   UPDATE MASTER TRANS;
   BY ID;
RUN;
*-----------------------------------;

***Problem data sets;

Problem 1 - sample data
     Data Set ONE     

   ID  SEX   DOB     SALARY  
    1   M  10/21/46  70000
    2   F  11/01/55  68000
    7   M  01/27/59  47000

     Data Set TWO

   ID  SEX   DOB     SALARY   TAXRATE   WITHHOLD
    3   F  01/01/33  78000      .36      23000 
    5   M  03/07/29  37000      .25       9000


     Data Set THREE

   ID  SEX   DOB     SALARY  HEIGHT   WEIGHT
    4   M  10/23/49  65000     68        158
    6   F  07/04/65  55000     74        202

Problem 3 - sample data
     Sample Data for MASTER

     ID     LASTNAME     FIRSTNAM     GENDER   AGE
     12     Butterfly    Roger          M       57
     39     Cline        Grove          M       44
     23     McFly        Clive          M       42
     34     Lane         Alice          F       35
     44     Hopperfly    Frank          M       21
     77     Elfly        Leslie         M       64
     13     Kline        Mary           F       29

Problem 4 - sample data
         Data set DEMOG                     Data set SCORES
   -----------------------------      ----------------------------
       ID         DOB     GENDER           SS          IQ    GPA
   101-45-2343  10/01/44    M         104-38-6686     110    3.9
   123-45-6789  09/13/77    F         788-77-7777     120    3.7
   104-38-6686  12/25/46    M         123-45-6789     106    3.8
   111-22-3333  02/02/89    F         101-45-2343     115    2.9
   555-55-5555  03/03/35    F         666-66-6666     118    3.8
   666-66-6666  12/04/42    F

Problem 5 - sample data
    Sample Data for Data Set MASTER

     PART    NUMBER    PRICE    SIZE
     111       34       8000     A
     123       87       1200     B
     124       45        800     A
     222       19       1300     C
     234       20       2000     A
     333       30       1800     B

***Chapter 4 Programs and data sets;

       Data Set PARTS                 Data Set INVOICE
     (Sorted by PART_NO)
     PART_NO   PARTNAME       INV_NO  PART_NO  COMPANY  QUANTITY
  ----------------------     ------------------------------------
       123      HAMMER           1      123     ABC        3
       232      PLIERS           1      333     ABC        2
       333      SAW              2      999     TOPHAT     2 
       432      NAILS            3      432     XYZ        20
       587      SCREWS

     *------------------ Example 1 ----------------------;
     PROC SORT DATA=INVOICE;
        BY PART_NO;
     RUN;

     DATA LOOKUP1;
        MERGE INVOICE (IN=INCLUDE) PARTS;
        BY PART_NO;
        IF INCLUDE = 1;
        IF PARTNAME = ' ' THEN PARTNAME = 'NOT ENTERED';
     RUN;

     PROC PRINT;
        TITLE 'Resulting File';
     RUN;                                            
     *---------------------------------------------------;

     *------------------- Example 2 -------------------------;
     PROC FORMAT;
        VALUE PARTDESC 
           123 = 'HAMMER'
           232 = 'PLIERS'
           333 = 'SAW'
           432 = 'NAILS'
           587 = 'SCREWS'
           OTHER = 'NOT ENTERED';
     RUN;

     DATA LOOKUP2;
        SET INVOICE;
        PARTNAME = PUT (PART_NO,PARTDESC.);
     RUN;

     PROC PRINT;                                     
        TITLE 'Resulting File';                      
     RUN;                                            
     *-------------------------------------------------------;
     *------------------- Example 3 -------------------------;
     PROC SORT DATA=INVOICE;
        BY PART_NO;
     RUN;

     DATA LOOKUP1;
        MERGE INVOICE (IN=INCLUDE) PARTS;
        BY PART_NO;
        IF INCLUDE = 1;
        IF PARTNAME=' ' THEN PARTNAME='NOT ENTERED';
        TOTAL = QUANTITY * PRICE;
        FORMAT TOTAL DOLLAR6.2;
     RUN;

     PROC PRINT;
        TITLE 'Resulting File';
     RUN;
     *-------------------------------------------------------;

     *------------------- Example 4 -------------------------;
     PROC FORMAT;
        VALUE PARTDESC
           123 = 'HAMMER'
           232 = 'PLIERS'
           333 = 'SAW'
           432 = 'NAILS'
           587 = 'SCREWS'
           OTHER = 'NOT ENTERED';
        VALUE PRICE
           123 = '25'
           232 = '8.50'
           333 = '18'
           432 = '.01'
           587 = '.05'
           OTHER = ' ';
     RUN;

     DATA LOOKUP2;
        SET INVOICE;
        PARTNAME = PUT (PART_NO,PARTDESC.);
        PRICE = INPUT (PUT(PART_NO,PRICE.),5.);
        TOTAL = QUANTITY * PRICE;
        FORMAT TOTAL DOLLAR6.2;
     RUN;

     PROC PRINT;
        TITLE 'Resulting File';
     RUN;
     *-------------------------------------------------------;

     *------------------- Example 5 -------------------------;
     PROC SORT DATA=TEMP;
        BY YEAR SEASON;
     RUN;

     PROC SORT DATA=DAILY;
        BY YEAR SEASON;
     RUN;

     DATA COMBINED;
        MERGE DAILY (IN=INDAILY) TEMP;
        BY YEAR SEASON;
        IF INDAILY;
     RUN;

     PROC PRINT;
        TITLE 'Listing of the Combined Data Set';
     RUN;
     *-------------------------------------------------------;

*Problems;

*Problem 1;

     (Data in ID order) (Data in PART_NO order)
          EMPLOY                PARTS                 SALES
     --------------------- ----------------- -------------------------
      ID  GENDER  DOB      PART_NO  PRICE    ID TRANS PART_NO QUANTITY
      01    F   10/21/46      123    15      03   1    234       5
      02    F   09/02/44      234    25      03   1    123       9
      03    M   04/23/55      237    20      03   2    237       4
      04    F   11/11/38      355    28      01   1    355       5
                              789    55      01   1    234       3
                                             01   1    123       9
                                             01   2    355       5    
                                             02   1    237      11

*Problem 2;

           CLINICAL Data                      DX Code Descriptions
 ------------------------------------------    ----------------------
   ID      DATE     BILLING     DX               DX    Description
   01    01/02/94     123        3               1        Cold  
   02    01/02/94     127        6               2        Flu
   03    01/03/94     231        4               3        Asthma
   04    01/03/94     344        3               4        Chest Pain
   05    01/04/94     765        1               5        Maternity
                                                 6        Diabetes

***Chapter 5 ? Code and data;

     *--------- EXAMPLE 1 ----------;
     DATA TRANSFRM;
        SET HOSP;
        LOGLOS = LOG (LOS);
        XPROP = ARSIN (SQRT(PROP));
     RUN;
     *------------------------------;

     *--------- EXAMPLE 2 ----------;
     DATA THIRD;
        SET OLD;
        IF MOD (_N_,3) = 1;
     RUN;
     *------------------------------;

     *----------------------- EXAMPLE 3.1 -----------------------;
     DATA NEW;
        SET OLD;  *Assume OLD has variables WEIGHT and HEIGHT;
        WEIGHT = ROUND (WEIGHT/2.2 , 1);
        HEIGHT = ROUND (2.54*HEIGHT , .1);
     RUN;
     *-----------------------------------------------------------;

     *----------------------- EXAMPLE 3.2 -----------------------;
     DATA NEW;
        SET OLD;  *Assume OLD has variables WEIGHT and HEIGHT;
        WEIGHT = INT (WEIGHT/2.2);
        HEIGHT = INT (2.54*HEIGHT);
     RUN;
     *-----------------------------------------------------------;

     *------------ EXAMPLE 4 -------------;
     DATA NEWTEST;
        SET OLDTEST;
        IF N (OF ITEM1-ITEM50) GE 40 THEN
           SCORE = MEAN (OF ITEM1-ITEM50);
     RUN;
     *------------------------------------;

     *------------ EXAMPLE 5 -------------;
     DATA TEMPER;
        INPUT DUMMY $ @@;
        IF DUMMY = 'N' THEN TEMP = 98.6;
           ELSE TEMP = INPUT (DUMMY,5.);
        DROP DUMMY;
     DATALINES;
     99.7 N 97.9 N N 102.5
     RUN;
     *------------------------------------;

     *----------- EXAMPLE 6 ------------;
     DATA ONE_CHAR;
        SET ONE (RENAME=(ID=SS));
        ID = PUT (SS,SSN11.);
        DROP SS;
     RUN;
     *----------------------------------;

     *--------- EXAMPLE 7.1 ----------;
     DATA MOVING;
        SET OLD;
        X1 = LAG (X);
        X2 = LAG2 (X);
        AVE = MEAN (OF X X1 X2);
        IF _N_ GE 3 THEN OUTPUT;
     RUN;
     *--------------------------------;

     DATA ERROR;
        INPUT X @@;
        IF X GE 5 THEN Y = LAG (X);
     DATALINES;
     1 8 3 9 2
     RUN;

     *--------- EXAMPLE 8 ----------;
     DATA NEW;
        SET OLD; *OLD CONTAINS ID;
        LENGTH STATE $ 2;
        STATE = SUBSTR (ID,4,2);
     RUN;
     *------------------------------;

     *---------------- EXAMPLE 9 ------------------;
     DATA SCORE;       
        SET OLD;
        ARRAY X[10] X1-X10;
        DO J = 1 TO 10;
           X[J] = INPUT (SUBSTR (STRING,J,1),1.);
        END;
        DROP STRING J;
     RUN;
     *---------------------------------------------;

     *------------------ EXAMPLE 10.1 -------------------;
     DATA CONVERT;
        INPUT DUMMY $ @@;
        DUMMY2 = SUBSTR (DUMMY,1,LENGTH(DUMMY)-1);  {1}
        IF INDEX (DUMMY,'I') NE 0 THEN  {2}
           CM = 2.54 * INPUT (DUMMY2,5.);
        ELSE IF INDEX (DUMMY,'C') NE 0 THEN  {3}
           CM = INPUT (DUMMY2,5.);
        ELSE CM = .;   {4}
        DROP DUMMY DUMMY2;
     DATALINES;
     23C 100I 12I 133C
     35I 45C  35  47I
     RUN;                                          
     *---------------------------------------------------;

     *---------------------- EXAMPLE 10.2 -----------------------;

     *Alternate solution to Example 10;

     DATA CONVERT;
        INPUT DUMMY $ @@;
        LENGTH LAST $ 1;
        LENGTH = LENGTH (DUMMY); {1}
        LAST = SUBSTR (DUMMY,LENGTH,1);  {2}
        FIRST = SUBSTR (DUMMY,1,LENGTH-1); {3}
        IF LAST EQ 'I' THEN CM = 2.54 * INPUT (FIRST,5.);  {4}
           ELSE IF LAST EQ 'C' THEN CM = INPUT (FIRST,5.);  {5}
           ELSE CM = .;
        KEEP CM;
     DATALINES;
     23C 100I 12I 133C
     35I 45C  35  47I
     RUN;                                          
     *-----------------------------------------------------------;

     *---------- EXAMPLE FUNCTION 11 ----------;
     DATA _NULL_;
        SET OLD;
        RETAIN CHECK 'ABCDE';
        IF VERIFY (ANSWER,CHECK) NE 0 THEN PUT
           'ERROR IN RECORD ' _N_  ANSWER=;
     RUN;
     *-----------------------------------------;

     *------------------ EXAMPLE FUNCTION 12 ------------------;
     DATA TRANS;
        ARRAY QUES[10] $  1 QUES1-QUES10;
        INPUT (QUES1-QUES10)($1.);
        DO J = 1 TO 10;
           QUES[J] = TRANSLATE (QUES[J],'ABCDE','12345');
        END;
        DROP J;
     DATALINES;
     2412552134
     1234512345
        .
        .
        .
     RUN;
     *---------------------------------------------------------;

     *---------- EXAMPLE 13 ------------;
     DATA NEWDATA;
        SET MASTER;
        LASTNAME = COMPRESS (LASTNAME);
     RUN;
     *----------------------------------;

     DATA MASTER;
        SET MASTER;
        LASTNAME = COMPRESS (LASTNAME);
     RUN;

     *----------- EXAMPLE 14----------------;
     DATA NEW;
        SET NAMES;
        NAME = TRIM (FIRST) || ' ' || LAST;
        KEEP NAME;
     RUN;
     *--------------------------------------;

*Problems;

*Problem 1;

              Data Set ORIG

     ID      SCORE     PROP       IQ
   12345       95       .8       110
   13579       72       .6        90
   24680       98       .2       140 
   11223       92       .3       106

*Problem 3;

     1 68  2 67  3 N  4 20C
     5 72  6 23C 7 75 8 N

*Problem 4;

(908)463-4490     (valid)
(201) 343-2233    (valid)
456-5034          (invalid)
(123)456-7890     (valid)
(201)SH4-1234     (invalid)
(512)2578362      (invalid)

*Problem 5;

     Data Set ONE                       Data Set TWO
    
   DATE1    HEIGHT  WEIGHT             DATE2      HR
  10/21/46    58      155             21OCT46     58
  09/05/55    66      220             11JAN44     68
  01/11/44    63      205             05SEP55     72

*Problem 6;

               Data Set STOCKS
         
             DAY     XXX     YYY
              1       43      57
              2       39      58
              3       45      56
              4       38      54
              5       40      55
              6       44      59

*Problem 7;

    Data Set SCORES

     ID      STRING
     1        12345
     2        13243
     3        53421

***Chapter 6 - Code and data;

Example 1

DATA PATIENT;
   INFILE 'HOSP';
   INPUT @1  ID      $2.
         @5  ADMIT   MMDDYY8.
         @15 DISCHRG MMDDYY8.
         @25 COST    5.;
   LOS = DISCHRG - ADMIT + 1;
   LABEL ADMIT   = 'Admission Date'
         DISCHRG = 'Discharge Date'
         COST    = 'Cost of Treatment'
         LOS     = 'Length of Stay';
   FORMAT ADMIT DISCHRG MMDDYY8. COST DOLLAR8.;
RUN;

PROC PRINT LABEL DATA=PATIENT;
   TITLE 'Hospital Report';
   ID ID;
   VAR ADMIT DISCHRG LOS COST;
RUN;                                           

Example 2

DATA MDYEXAMP;
   INPUT DAY    1-2
         MONTH 10-11
         YEAR  20-23;
   DATE = MDY (MONTH,DAY,YEAR);
   FORMAT DATE WORDDATE.;
DATALINES;
12       11        1992
11       09        1899
;

PROC PRINT DATA=MDYEXAMP;
   TITLE 'Example of MDY Function';
RUN;                               

Example 3

DATA AGE;
   SET EMPLOYEE;
   AGE1 = INT (('01JAN95'D - DOB)/365.25);
   AGE2 = ROUND (('01JAN95'D - DOB)/365.25,1);
   AGE3 = INT ((TODAY() - DOB)/365.25);
   AGE4 = ROUND ((TODAY() - DOB)/365.25,1);
RUN;

 Example 4

 PROC FORMAT;
    VALUE DAYFMT 1='SUN' 2='MON' 3='TUE' 4='WED'
                 5='THU' 6='FRI' 7='SAT';
 RUN;

 DATA PATIENT;                  
    INFILE 'HOSP';
    INPUT @1  ID      $2.
          @5  ADMIT   MMDDYY8.
          @15 DISCHRG MMDDYY8.
          @25 COST    5.;
    DAY_WEEK = WEEKDAY (ADMIT);
    DAY_MON  = DAY (ADMIT);
    LABEL ADMIT    = 'Admission Date'
          DISCHRG  = 'Discharge Date'
          COST     = 'Cost of Treatment'
          DAY_WEEK = 'Day of the Week'
          DAY_MON  = 'Day of the Month';
    FORMAT ADMIT DISCHRG MMDDYY8. COST DOLLAR8. 
           DAY_WEEK DAYFMT.;
 RUN;

 PROC CHART DATA=PATIENT;
    VBAR DAY_WEEK DAY_MON / DISCRETE;
 RUN;                                                

Example 5

DATA TEMP;
   SET FUND;
   YEAR = YEAR (DATE);
   MONTH = MONTH (DATE);
RUN;

PROC CHART DATA=TEMP;
   VBAR YEAR / SUMVAR=AMOUNT DISCRETE;
   VBAR MONTH / SUMVAR=AMOUNT DISCRETE;
RUN;

Example 6

DATA NEW_EMP;
   SET EMPLOY;
   CURRENT = TODAY();
   WORK_YRS = INTCK ('YEAR',DATEHIRE,CURRENT);
RUN;

***Problem data sets;


Problem 1 - sample data

00101059201079210211946
00211129211159209011955
00305129206099212251899
00401019301079304051952

Problem 4 - sample data
02/01/90  12,500
02/08/90  12,600
04/01/90  13,000
05/05/90  12,800
08/05/90  14,000
12/12/90  14,200
02/18/91  14,400
02/22/91  14,100
05/01/91  15,000

Problem 5 - sample data

ID  GENDER    DOB
     
18    M     03/07/62
28    F     07/12/67
38    M     03/19/59
53    M     01/25/62
72    F     09/01/67
75    F     08/07/59
77    M     05/17/55
80    F     09/07/62

***Chapter 7 - Code and data

Example 1 - Hard Way

DATA HARDWAY;         
   SET OLD;         
   IF X1   = 999 THEN X1 = .;         
   IF X2   = 999 THEN X2 = .;          
   .
   .
   .
   IF X100 = 999 THEN X100 = .;
   IF A    = 999 THEN A = .;         
   IF B    = 999 THEN B = .;          
   .
   .
   .
   IF E    = 999 then E = .;      
RUN;     

Example 1 - Easy Way 
 
DATA EASYWAY;         
   SET OLD;         
   ARRAY NARNIA[105] X1-X100 A B C D E;         
   DO I = 1 TO 105;            
      IF NARNIA[I] = 999 THEN NARNIA[I] = .;         
   END;         
   DROP I;      
RUN;     

Example 2

DATA NEW;
   SET OLD;
   ARRAY XXX[*] _NUMERIC_;
   DO I = 1 TO DIM (XXX);
      IF XXX[I] = 999 THEN XXX[I] = .;
   END;
   DROP I;
RUN;

Example 3

DATA NEW;
   SET OLD;
   ARRAY $ YYY[*] _CHARACTER_;
   DO I = 1 TO DIM (YYY);
      IF YYY[I] = 'NA' THEN YYY[I] = ' ';
   END;
   DROP I;
RUN;

Example 4

DATA NEW;
   SET OLD;
   ARRAY XX[3] X1-X3;
   DO TIME = 1 TO 3;
      X = XX[TIME];
      OUTPUT;
   END;
   DROP X1-X3;
RUN;

Example 5

DATA NEW;
   SET OLD;
   ARRAY XX[2,3] X1-X6;
   DO METHOD = 1 TO 2;
      DO TIME = 1 TO 3;
         X = XX[METHOD,TIME];
         OUTPUT;
      END;
   END;
   KEEP SUBJECT METHOD TIME SCORE;
RUN;

Example 6

PROC SORT DATA=OLD;
   BY SUBJECT TIME;
RUN;

DATA NEW;
   SET OLD;
   BY SUBJECT;
   ARRAY XX[*] X1-X3;
   RETAIN X1-X3;
   IF FIRST.SUBJECT = 1 THEN DO I = 1 TO 3;
      XX[I] = .;
      END;
   XX[TIME] = X;
   IF LAST.SUBJECT = 1 THEN OUTPUT;
   KEEP SUBJECT X1-X3; 
RUN;

***Chapter 8 - Code and data

******* INCORRECT PROGRAM *******;

DATA NOGOOD;                         
   SUBJECT = SUBJECT + 1;
   INPUT SCORE1 SCORE2;
DATALINES;
 3 4
 5 6
 7 8
;
      
PROC PRINT DATA=NOGOOD;
   TITLE1 'Incorrect Program'; 
RUN;

Example 2

DATA BETTER;
   RETAIN SUBJECT 0;
   SUBJECT = SUBJECT + 1;
   INPUT SCORE1 SCORE2;
DATALINES;
3 4 
5 6
7 8
;

PROC PRINT DATA=BETTER;
   TITLE1 'Correct Program'; 
RUN;

Example 3

DATA BEST;
   SUBJECT + 1;
   INPUT SCORE1 SCORE2;
DATALINES;
3 4
5 6
7 8
;
      
PROC PRINT DATA=BEST;
   TITLE1 'Correct Program';
RUN;

Example 4

DATA SCORING;
   ARRAY KEY[5] $  1 KEY1-KEY5;      * ANSWER KEY          ;
   ARRAY ANS[5] $  1 ANS1-ANS5;      * STUDENT RESPONSES   ;
   ARRAY SCORE[5]    SCORE1-SCORE5;  * ITEM SCORES,        ;
                                     *   1=CORRECT,        ;
                                     *   0=WRONG           ;  
   RETAIN KEY1-KEY5; 
   IF _N_ = 1 THEN DO;
      INPUT (KEY1-KEY5)($1.);
      DELETE;
   END;
   ELSE DO;  
      INPUT (ANS1-ANS5)($1.);
      DO I = 1 TO 5;
         IF KEY[I] = ANS[I] THEN SCORE[I] = 1;
         ELSE SCORE[I] = 0;
      END;
   RAW = SUM (OF SCORE1-SCORE5);
   PERCENT = (100*RAW)/5;
   END;
   KEEP RAW PERCENT;
DATALINES;
ABCDE
ABABA
ABCDA
;
      
PROC PRINT DATA=SCORING;
   TITLE1 'Scoring a Test';
RUN;

      File HTWT

SUBJ     DOB     WEIGHT   
1     10/21/50     155   
1                  158   
1                  162   
2     11/01/46     102   
2                  108   
2                  105   
3                  200
3                  202   
3                  198   

Example 5

DATA DANGER;
   RETAIN OLD_DOB;
   INFILE 'HTWT';
   INPUT @1  SUBJ    $2.
         @7  DOB     MMDDYY8.
         @17 WEIGHT  3.;
   IF DOB NE . THEN OLD_DOB = DOB;    * SET OLD_DOB TO DOB ;
   ELSE DOB = OLD_DOB;                * SO WE CAN          ;
   FORMAT DOB MMDDYY8.;               * REMEMBER IT        ;
   DROP OLD_DOB;
RUN;

PROC PRINT DATA=DANGER;
   TITLE1 'Incorrect DOB Solution';
RUN;

Example 6

DATA WORKS;
   RETAIN OLD_DOB;
   INFILE 'HTWT';
   INPUT @1   SUBJ $2.
         @7   DOB  MMDDYY8.
         @17  WEIGHT 3.;
 
*CHECK IF WE HAVE A NEW SUBJECT NUMBER. IF SO, 
 SET OLD_DOB TO THE DOB VALUE FOR THE NEW SUBJECT;  
 
   IF SUBJ NE LAG(SUBJ) THEN OLD_DOB = DOB;
   ELSE DOB = OLD_DOB;
   FORMAT DOB MMDDYY8.;
   DROP OLD_DOB;
RUN;   
      
PROC PRINT DATA=WORKS;
   TITLE1 'Correct DOB Solution';
RUN;

Example 7

DATA COMPULSE;
   RETAIN DOB;
   INFILE 'HTWT';
   INPUT @1 SUBJ  $2.  @;
   IF SUBJ NE LAG(SUBJ) THEN
      INPUT @7  DOB    MMDDYY8.
            @17 WEIGHT 3.;
   ELSE
      INPUT @17 WEIGHT 3.;
   FORMAT DOB  MMDDYY8.;

PROC PRINT DATA=COMPULSE;
   TITLE 'Another Correct DOB Solution';
RUN;

***Problem data sets;
Problem 1 - sample data
     data set DIET

 ID     DATE     WEIGHT 

  1   10/01/92    155 
  1   10/08/92    158 
  1   10/15/92    158 
  1   10/22/92    158 
  2   09/02/92    200 
  2   09/09/92    198 
  2   09/16/92    196 
  2   09/23/92    202 

Problem 2 - sample data

C 303 102 150 B 202 C 300 B 450 400 399 
420 A 289 280 278

***Chapter 9 - Code and data;

Example 1

PROC PRINT DATA=MEDICAL;
   VAR SUB_ID
       DIAGCODE
       ADMIT_DT
       DISCH_DT
       HOSPCODE
       LOS
       COST;
RUN;

Example 2

PROC PRINT DATA=MEDICAL;
   ID SUB_ID;
   VAR DIAGCODE
       ADMIT_DT
       DISCH_DT
       HOSPCODE
       LOS
       COST;
RUN;

Example 3

PROC PRINT DATA=MEDICAL LABEL;
   TITLE  'Hospital Data Base Report';
   TITLE2 '-------------------------';
   ID  SUB_ID;
   VAR DIAGCODE
       ADMIT_DT
       DISCH_DT
       HOSPCODE
       LOS
       COST;
   LABEL DIAGCODE = 'Diagnosis Code'
         ADMIT_DT = 'Admission Date'
         DISCH_DT = 'Discharge Date'
         HOSPCODE = 'Hospital Code'
         LOS      = 'Length of Stay'
         COST     = 'Cost of Treatment';
   FORMAT COST DOLLAR7.
          SUB_ID SSN11.
          ADMIT_DT DISCH_DT MMDDYY8.;
RUN;

Example 4

OPTIONS NOCENTER NODATE NONUMBER;

PROC SORT DATA=MEDICAL;
   BY DIAGCODE;
RUN;

PROC PRINT DATA=MEDICAL N LABEL;
   BY DIAGCODE;
   TITLE 'Hospital Data Base Report';
   TITLE2 '-------------------------';
   FOOTNOTE 'Prepared by RPC and Company';
   SUM LOS COST;
   SUMBY DIAGCODE;
   ID SUB_ID;
   VAR DIAGCODE
       ADMIT_DT
       DISCH_DT
       HOSPCODE
       LOS
       COST;
   LABEL DIAGCODE = 'Diagnosis Code'
         ADMIT_DT = 'Admission Date'
         DISCH_DT = 'Discharge Date'
         HOSPCODE = 'Hospital Code'
         LOS      = 'Length of Stay'
         COST     = 'Cost of Treatment';
   FORMAT COST DOLLAR7. 
          SUB_ID SSN11.
          ADMIT_DT DISCH_DT MMDDYY8.;
RUN;

Example 5

PROC PRINT DATA=MEDICAL WIDTH=FULL;
   TITLE 'WIDTH option FULL';
   VAR SUB_ID
       DIAGCODE
       ADMIT_DT
       DISCH_DT
       HOSPCODE
       LOS
       COST;
   FORMAT COST DOLLAR7. 
          SUB_ID SSN11.
          ADMIT_DT DISCH_DT MMDDYY8.;
RUN;

Example 6

PROC PRINT DATA=MEDICAL WIDTH=MINIMUM;
   TITLE 'WIDTH option MINIMUM';
   VAR SUB_ID
       DIAGCODE
       ADMIT_DT
       DISCH_DT
       HOSPCODE
       LOS
       COST;
   FORMAT COST DOLLAR7. 
          SUB_ID SSN11.
          ADMIT_DT DISCH_DT MMDDYY8.;
RUN;

***Chapter 10 - Code and data;

Example 1

PROC MEANS DATA=SALES;
   TITLE 'Sample Output from PROC MEANS';
   CLASS  REGION ITEM;
   VAR    QUANTITY;
   OUTPUT OUT=QUAN_SUM SUM=TOTAL;
 RUN;
 
 PROC PRINT DATA=QUAN_SUM;
    TITLE 'Summary Data Set';
 RUN;

Example 2

PROC MEANS DATA=ORIGDATA;
   CLASS A B;
   VAR   X Y Z;
   OUTPUT OUT  = STATS
          N    = NUM_X   NUM_Y   NUM_Z
          MEAN = MEAN_X  MEAN_Y  MEAN_Z
          SUM  = TOT_X   TOT_Y   TOT_Z;
RUN;

Example 3

PROC MEANS DATA=PRESSURE NOPRINT NWAY;

* Note: Data set PRESSURE does NOT have to be sorted;

   CLASS  SUBJ YEAR; 
   VAR    SBP DBP;
   OUTPUT OUT=MEANOUT MEAN=;
RUN;

Example 4

PROC MEANS DATA=MEANOUT MEAN MAXDEC=2;
   TITLE 'Averages Computed from Person Yearly Means';
   CLASS YEAR;
   VAR SBP DBP;
RUN;

        Data Set FUND
      
NAME          TOWNSHIP   AMOUNT
Apple         Raritan      25
Brown         Raritan      20
Collins       Readington   25
Denison       Readington    .
Early         Raritan       .
Franks        Raritan      10
George        Raritan       .
Harris        Franklin     20
Ignatz        Raritan      15
Jackson       Franklin      .
Kennedy       Franklin     15
Little        Raritan      25
Morris        Readington   35
Nash          Readington   25
Owens         Franklin      .
Percy         Franklin      .
Quincy        Readington   25
Ripple        Readington    .
Smith         Readington    .

Example 5

OPTIONS LS=72 NONUMBER NODATE;

PROC MEANS DATA=FUND NOPRINT NWAY;
   CLASS TOWNSHIP;
   VAR AMOUNT;
   OUTPUT OUT   = SUMMARY
          N     = RETURNED
          NMISS = NOT_RETN
          SUM   = TOTAL;
RUN;

DATA REPORT;
   SET SUMMARY;
   MAILED   = RETURNED + NOT_RETN;
   *Alternative: MAILED = _FREQ_ ;
   PER_RETN = TOTAL / RETURNED;
   PER_MAIL = TOTAL / MAILED;

   LABEL MAILED   = 'LETTERS MAILED'
         RETURNED = 'NUMBER OF DONATIONS'
         TOTAL    = 'TOTAL DONATION'
         PER_RETN = 'MEAN DONATION'
         PER_MAIL = 'MEAN DONATION PER LETTER MAILED';
RUN;

PROC PRINT DATA=REPORT LABEL DOUBLE;
   TITLE 'Fund Drive Summary Report';
   ID TOWNSHIP;
   VAR MAILED RETURNED TOTAL PER_RETN PER_MAIL;
   FORMAT MAILED RETURNED COMMA5.
          TOTAL DOLLAR7.
          PER_RETN PER_MAIL DOLLAR5.;
RUN;

Example 6

DATA TEST;
   INPUT HR SBP DBP;
DATALINES;
80 160 100
70 150 90
60 140 80
;

PROC MEANS NOPRINT DATA=TEST;
   VAR HR SBP DBP;
   OUTPUT OUT  = MOUT
          MEAN = MHR MSBP MDBP;
RUN;

DATA PERCENT ;
   SET TEST;
   DROP MHR MSBP MDBP _TYPE_ _FREQ_;
   IF _N_ = 1 THEN SET MOUT;
   HRPER =100*HR/MHR;
   SBPPER=100*SBP/MSBP;
   DBPPER=100*DBP/MDBP;
RUN;

PROC PRINT NOOBS DATA=PERCENT;
   TITLE 'Listing of Percent Data Set';
RUN;

PATNUM    DATE    DRUGGRP  CHOL    SBP    DBP    HR  ROUTINE
      
   01    01/05/89     D      400    160     90    88     Y
   01    02/15/89     D      350    156     88    80     Y
   01    05/18/90     D      350    140     82    76     Y
   01    09/09/90     D      300    138     78    78     N
   01    11/11/90     D      305    142     82    84     Y
   01    01/05/91     D      270    142     80    72     N
   01    02/18/91     D      260    156     92    88     N
   02    02/19/90     D      390    180    100    82     N
   02    02/22/90     D      320    178     88    86     Y
   02    02/25/90     D      325    172     82    78     Y
   02    04/24/90     D      304    166     78    99     N
   02    08/25/90     D      299    150     80    80     Y
   02    03/13/91     D      222    144     82    72     Y
   02    07/16/91     D      243    140     80    68     Y
   02    10/10/91     D      242    138     74    62     Y
   02    10/30/91     D      230    156     92    88     N
   02    12/25/91     D      200    142     82    80     Y
   03    01/01/90     P      387    190    110    90     N
   03    02/13/90     P      377    188     96    84     Y
   03    05/09/90     P      380    182     88    80     Y
   03    08/17/90     P      400    186     92    82     Y
   03    10/10/90     P      390    182     90    78     N
   03    10/11/90     P      380    178     82    72     Y
   03    11/11/90     P      370    160     82    72     Y
   03    02/02/91     P      380    156     78    70     Y
   04    05/15/91     D      380    120     78    56     Y
   04    08/20/91     D      370    122     76    58     N
   04    03/23/92     D      355    128     68    60     Y
   04    05/02/92     D      306    130     72    68     N
   04    07/02/92     D      279    126     74    62     Y
   04    07/03/92     D      277    126     74    64     Y
   04    07/05/92     D      261    130     80    72     N
   05    01/06/90     P      399    188    110    92     N
   05    03/06/90     P      377    182    100    88     N                      
   05    04/24/90     P      400    180     92    82     Y
   05    04/24/90     P      400    180     92    88     N
   05    06/24/90     P      388    176     88    80     Y
   05    08/01/90     P      378    162     82    78     Y
   05    10/10/90     P      388    156     78    78     Y
   05    12/01/90     P      359    156     72    70     Y
   06    01/01/92     D      387    128     62    60     N
   06    01/03/92     D      379    128     66    62     Y
   06    04/24/92     D      375    132     70    58     N
   06    05/01/92     D      365    130     76    66     Y
   06    05/28/92     D      321    132     78    68     N
   06    06/01/92     D      308    128     72    58     Y
   07    01/05/90     P      376    118     68    54     Y
   07    04/05/90     P      379    124     72    70     N
   07    04/07/90     P      389    120     68    62     Y
   07    06/28/90     P      388    124     78    60     Y
   07    01/04/91     P      400    128     80    66     N
   07    03/03/91     P      401    132     70    80     N

Example 7

/*------------------------------------------------------*\
| First, create a new data set NEW_CLIN from CLIN which  |
| contains a numeric variable RATIO which has values     |
| of 0 for 'N' and 1 for 'Y'.                            |
\*------------------------------------------------------*/

DATA NEW_CLIN;
   SET CLINICAL;
   RATIO = INPUT (TRANSLATE(ROUTINE,'01','NY'),1.);
/*-----------------------------------------------------*\
|                                                       |
| Two alternatives would be:                            |
|                                                       |
| IF ROUTINE = 'N' THEN RATIO = 0;                      |
|                  ELSE RATIO = 1;                      |
|                                                       |
|        or                                             |
|                                                       |
| SELECT (ROUTINE);                                     |
|    WHEN ('N') THEN RATIO = 0;                         |
|    WHEN ('Y') THEN RATIO = 1;                         |
| END;                                                  |
|                                                       |
\*-----------------------------------------------------*/
RUN;

PROC SORT DATA=NEW_CLIN;
   BY PATNUM DATE;
RUN;

* Create a data set with the last record for each Patient;
DATA LAST (RENAME=(DATE=LASTDATE
                   CHOL=LASTCHOL));
   SET NEW_CLIN  (KEEP = PATNUM DATE CHOL);
   BY PATNUM;
   IF LAST.PATNUM;
RUN;

* Output means and medians for each patient to a data set;

PROC UNIVARIATE DATA=NEW_CLIN NOPRINT;
   BY PATNUM;
   VAR CHOL SBP DBP RATIO;
   OUTPUT OUT=STATS
          MEAN=MEANCHOL MEANSBP MEANDBP RATIO
          MEDIAN=MEDCHOL;
RUN;

* Combine the LAST data set with the STATS data set;
DATA FINAL; 
   MERGE STATS LAST;
   BY PATNUM;
RUN;

* Print a final report;
PROC PRINT DATA=FINAL LABEL DOUBLE;
   TITLE 'Listing of data set FINAL in Example 7';
   ID PATNUM;
   VAR LASTDATE LASTCHOL MEANCHOL MEDCHOL
       MEANSBP MEANDBP RATIO;
   LABEL LASTDATE  = 'Date of Last Visit'
         MEANCHOL  = 'Mean Chol'
         MEANSBP   = 'Mean SBP'
         MEANDBP   = 'Mean DBP'
         MEDCHOL   = 'Median Chol'
         LASTCHOL  = 'Last Chol'
         RATIO     = 'Proportion of visits that were routine';

   FORMAT MEANCHOL MEANSBP MEANDBP MEDCHOL LASTCHOL 5.0
          RATIO 3.2;
RUN;

***Chapter 10 data;

Problem 1 - sample data

   data set GRADES

GENDER   WEIGHT  SCORE
  BOY     LIGHT     80
 GIRL    HEAVY     90
 GIRL    LIGHT     85
 BOY     HEAVY     92
 BOY     LIGHT     93
 BOY     HEAVY     88
 GIRL    LIGHT     96

Problem 4 - sample data
data set EXPER

GROUP   TIME    SCORE

  A       1       90
  A       1      100
  A       2      120
  A       2      150
  B       1       80
  B       1       80
  B       2      120
  B       2      180

***Chapter 11 - Code and data;

Example 1

 PROC FORMAT;
    VALUE GENDER 1 = 'Male'
                 2 = 'Female'
                 . = 'Missing'
             OTHER = 'Miscoded';
    VALUE $RACE  'C' = 'Caucasian'
                 'A' = 'African American'
                 'H' = 'Hispanic'
                 'N' = 'Native American'
               OTHER = 'Other'
                 ' ' = 'Missing';
   VALUE $LIKERT '1' = 'Str dis'
                 '2' = 'Disagree'
                 '3' = 'No opinion'
                 '4' = 'Agree'
                 '5' = 'Str agree'
               OTHER = ' ';
   VALUE AGEGROUP LOW-<20 = '< 20'
                  20-<40  = '20 to <40'
                  40-<60  = '40 to <60'
                  60-HIGH = '60+';
 DATA QUESTION;
   INPUT  ID      $ 1-2
          GENDER    4
          RACE    $ 6
          AGE       8-9
          SATISFY $ 11
          TIME    $ 13;
   FORMAT GENDER GENDER.
          RACE   $RACE.
          SATISFY TIME $LIKERT.;
   AGEGROUP = PUT (AGE,AGEGROUP.);
DATALINES;
01 1 C 45 4 2
02 2 A 34 5 4
03 1 C 67 3 4
04   N 18 5 5
05 9 H 47 4 2
06 1 X 55 3 3
07 2   56 2 2
08     20 1 1
RUN;

PROC PRINT DATA=QUESTION NOOBS;
   TITLE 'Data listing with formatted values';
RUN;

Example 2

PROC FORMAT;
  VALUE BADFMT  1 = 'ONE'
                2 = 'TWO'
            OTHER = 'MISCODED';
RUN;

DATA TEST;
INPUT X  Y;
DATALINES;
1 1
2 2
5 5
3 .
;

PROC FREQ DATA=TEST;
   TABLES X Y;
   FORMAT X Y BADFMT.;
RUN;

Example 4

DATA SCREEN4;
   INPUT    ID     1-3
            GENDER $ 4 ...  ;
   IF GENDER NOT IN ('M','F')
      THEN GENDER = ' ';
      .
      .
      .

Example 5

DATA SCREEN5;
   INPUT ID       1-3
         GENDER   $ 4  ... ;
   IF GENDER NOT IN ('M','F',' ')
      THEN GENDER='X';
      .
      .
      .

Example 6

PROC FORMAT;
   INVALUE $GENDER 'M', 'F' = _SAME_
                    OTHER   = ' ';
RUN;

DATA SCREEN3;
   INPUT @1 ID     3.
         @4 GENDER $GENDER1. ;
   .
   .
   .

Example 7

PROC FORMAT;
   INVALUE $GENDER 'M', 'F', ' ' = _SAME_
                   OTHER         = 'X';
RUN;

DATA SCREEN7;
   INPUT @1 ID     3.   
         @4 GENDER $GENDER1. ;
   .
   .
   .

Example 8

   PROC FORMAT;
    INVALUE SBPFMT 40 - 300 = _SAME_
                   OTHER    = .;
    INVALUE DBPFMT 10 - 150 = _SAME_
                   OTHER    = .;
 RUN;

 DATA FORMAT8;
    INPUT @1 ID   $3.
          @4 SBP  SBPFMT3.
          @7 DBP  DBPFMT3.;
 DATALINES;
 001160090
 002310220
 003020008
 004   080
 005150070
 ;

 PROC PRINT DATA=FORMAT8;
 RUN;

Example 9

PROC FORMAT;
   INVALUE SBPFMT LOW - <40  = .L
                   40 - 300  = _SAME_
                  301 - HIGH = .H;
   INVALUE DBPFMT LOW - <10  = .L
                   10 - 150  = _SAME_
                  151 - HIGH = .H;
     VALUE CHECK  .H = 'High' 
                  .L = 'Low'
                  .  = 'Missing'
               OTHER = 'Valid';
  RUN;

  DATA FORMAT9;
     INPUT @1 ID   $3.
           @4 SBP  SBPFMT3.
           @7 DBP  DBPFMT3.;
  DATALINES;
  001160090
  002310220
  003020008
  004   080
  005150070
  ;

  PROC PRINT DATA=FORMAT9 NOOBS; 
     TITLE 'Listing from Example 9';
  RUN;
   PROC FREQ DATA=FORMAT9;
     FORMAT SBP DBP CHECK.;
     TABLES SBP DBP / MISSING NOCUM;
  RUN;

  PROC MEANS DATA=FORMAT9 N MEAN MAXDEC=1;
     VAR SBP DBP;
  RUN;

Example 10

PROC FORMAT;
   INVALUE TEMPER 70-110=_SAME_
                    'N' = 98.6
                   OTHER=.;
RUN;

DATA TEST;
   INPUT TEMP : TEMPER. @@;
DATALINES;
99.7 N 97.9 N N 112.5
;

PROC PRINT DATA=TEST NOOBS;
   TITLE 'Temperature Listing';
RUN;

Example 11

PROC FORMAT;
   VALUE $ICDFMT '072' = 'Mumps'
                 '410' = 'Heart Attack'
                 '487' = 'Influenza'
                 '493' = 'Asthma'
                 '700' = 'Corns';
RUN;

Example 12

DATA CODES;
INPUT  @1 ICD9      $3.
       @5 DESCRIPT  $12.;
DATALINES;
072 MUMPS
410 HEART ATTACK
487 INFLUENZA
493 ASTHMA
700 CORNS
;

DATA CONTROL;
   RETAIN FMTNAME '$ICDFMT'
          TYPE    'C' ;
   SET CODES (RENAME=(ICD9 = START
                      DESCRIPT = LABEL));
RUN;

PROC FORMAT CNTLIN=CONTROL;
RUN;

DATA EXAMPLE;
   INPUT ICD9 $ @@;
   FORMAT ICD9 $ICDFMT.;
DATALINES;
072 493 700 410 072 700
;

PROC PRINT NOOBS DATA=EXAMPLE;
   TITLE 'Using a Control Data Set';
   VAR ICD9;
RUN;

Example 13

DATA COUNTRY;
RETAIN FMTNAME 'COUNTRY'
       TYPE 'N';
   INPUT START 1-2
         LABEL $ 3-15;
DATALINES;
01UNITED STATES
02FRANCE
03ENGLAND
04SPAIN
05GERMANY
;

PROC FORMAT CNTLIN=COUNTRY FMTLIB;
RUN;

***Chapter 12 - Code and data;

Example 1

PROC CHART DATA=SALES;
   TITLE 'Vertical Bar Chart';
   VBAR REGION;
RUN;
5
Example 2

PROC CHART DATA=SALES;
   TITLE 'Horizontal Bar Chart';
   HBAR REGION;
RUN;

Example 3

PROC CHART DATA=SALES;
   TITLE 'Horizontal Bar Chart Without Statistics';
   HBAR REGION / NOSTAT;
RUN;

Example 4

PROC CHART DATA=SALES;
   TITLE 'Vertical Bar Chart Showing Percents';
   VBAR REGION / TYPE = PERCENT;
RUN;                            

Example 5

PROC CHART DATA=SALES;
   TITLE 'Vertical Bar Chart for a Continuous Variable';
   VBAR PRICE;
RUN;

Example 6

PROC CHART DATA=SALES;
   TITLE 'Vertical Bar Chart Demonstrating MIDPOINTS Option';
   VBAR PRICE / MIDPOINTS = 8 to 20 by 4;
RUN;

Example 7

PROC CHART DATA=OSCAR;
   TITLE 'Vertical Chart Without DISCRETE Option';
   VBAR DAY;
RUN;

Example 8

PROC CHART DATA=OSCAR;
   TITLE 'Vertical Chart With DISCRETE Option';
   VBAR DAY / DISCRETE;
RUN;

Example 9 

PROC CHART DATA=SALES;
   TITLE 'Adding options SUMVAR= and TYPE=';
   VBAR REGION / SUMVAR=QUANTITY TYPE=SUM;
RUN;

Example 10

PROC CHART DATA=SALES;
   TITLE 'Vertical Bar Chart With GROUP= Option';
   VBAR ITEM / GROUP=REGION;
RUN;

Example 11

PROC CHART DATA=SALES;
   TITLE 'Vertical Bar Chart With SUBGROUP= Option';
   VBAR REGION / SUBGROUP=ITEM;
RUN;

Example 12

PROC CHART DATA=SALES;
   TITLE 'Another Vertical Bar Chart With SUBGROUP= Option';
   VBAR REGION / SUBGROUP=PRICE;
RUN;

Example 13

DATA BLOCKEG;
   SET CLINICAL (KEEP = DATE ROUTINE SBP);
   YEAR = YEAR(DATE);
RUN;

PROC CHART DATA=BLOCKEG;
   TITLE 'Example of a BLOCK CHART';
   BLOCK YEAR / GROUP=ROUTINE DISCRETE;  
   BLOCK YEAR / GROUP=ROUTINE SUMVAR=SBP TYPE=MEAN DISCRETE;
   FORMAT SBP 5.;
RUN;


***Chapter 13 - Code and data;

               Observations from the CLIN_2 Data Set

  NAME          DATE    DRUGGRP    CHOL    SBP    DBP    HR    ROUTINE

  George    01/05/89       D        400    160     90    88       Y
  Fred      02/19/90       D        390    180    100    82       N
  Ron       01/01/90       P        387    190    110    90       N
  Ray       05/15/91       D        380    120     78    56       Y
  Dave      01/06/90       P        399    188    110    92       N
  Jennifer  01/01/92       D        387    128     62    60       N
  Carol     01/05/90       P        376    118     68    54       Y
  Steven    08/15/94       P        220    160     90    77       Y
  John      08/17/94       D        170    128     62    64       N
  Mary      11/25/94       P        188    128     64    72       Y

Example 1

PROC PLOT DATA=CLIN_2;
   TITLE 'Scatter Plot of SBP by DBP';
   PLOT DBP * SBP;
 RUN;

Example 2

PROC PLOT DATA=CLIN_2;
   TITLE 'Multiple Plots on One Set of Axes';
   PLOT (DBP HR) * SBP / OVERLAY;
RUN;

Example 3

PROC PLOT DATA=CLIN_2;
   TITLE  'Multiple Plots on One Set of Axes';
   TITLE2 'with Different Plotting Symbols';
   PLOT DBP * SBP = 'D'   
        HR  * SBP = 'H' / OVERLAY;
RUN;

Example 4

PROC PLOT DATA=CLIN_2;
   TITLE 'Scatter Plot of SBP by DBP with';
   TITLE2 'Drug Group as the Plotting Symbol';
   PLOT DBP * SBP = DRUGGRP;
 RUN;

Example 5

PROC PLOT DATA=CLIN_2;
   TITLE 'Plot of SBP versus HR with';
   TITLE2 'NAME as the Labeling Variable';
   PLOT SBP * HR $ NAME = 'o';
RUN;

***Chapter 13 problems;

     Data set CHAP13

X    Y     Z   CODE   NAME
1    2     1     Y    Mary
2    3     3     N    Joe
3    5     9     N    George
4    7     6     Y    Henry
7    8     4     Y    Bill
9   11     5     N    Clifton
2   15     2     N    Philip
5   19     4     Y    John


***Chapter 14 - Code and data;

Example 1 - INEFFICIENT

DATA NEW;
   SET OLD;
RUN;

PROC MEANS N MEAN MIN MAX;
   VAR X Y Z;
RUN;

Example 1 - EFFICIENT 

PROC MEANS DATA=OLD N MEAN MIN MAX;
   VAR X Y Z;
RUN;

Example 2 - INEFFICIENT

DATA ONE;
   INPUT ID AGE HEIGHT WEIGHT;
DATALINES;
1 23 68 155
2 45 77 200
   .
   .
   .
;

PROC MEANS N MEAN STD DATA=ONE;
   VAR AGE HEIGHT WEIGHT;
RUN;

DATA EXTRA;
   SET ONE;
   LENGTH AGEGRP $ 8;
   IF        0 LE AGE LE 20 THEN AGEGRP = '0 TO 20';
   ELSE IF  21 LE AGE LE 40 THEN AGEGRP = '21 TO 40';
   ELSE IF        AGE GT 40 THEN AGEGRP = '>40';
RUN;

PROC FREQ DATA=EXTRA;
   TABLES AGEGRP;
RUN;

Example 2 - EFFICIENT
      
DATA ONE;
   INPUT ID AGE HEIGHT WEIGHT;
   LENGTH AGEGRP $ 8;
   IF        0 LE AGE LE 20 THEN AGEGRP = '0 TO 20';
   ELSE IF  21 LE AGE LE 40 THEN AGEGRP = '21 TO 40';
   ELSE IF        AGE GT 40 THEN AGEGRP = '>40';
DATALINES;
1 23 68 155
2 45 77 200
   .
   .
   .
;

PROC MEANS N MEAN STD DATA=ONE;
   VAR AGE HEIGHT WEIGHT;
RUN;

PROC FREQ;
   TABLES AGEGRP;
RUN;

Example 3 - INEFFICIENT
     
DATA SUBSET;
   INFILE 'input_file_spec';
   INPUT @1  ID        3.
         @5  GENDER   $1.
         @6  (Q1-Q10) ($1.);
   IF GENDER = 'M';
RUN;

Example 3 - EFFICIENT

DATA SUBSET;
   INFILE 'input_file_spec';
   INPUT   @5  GENDER  $1. @;
   IF GENDER = 'M';
      INPUT @1 ID   3.
            @6 (Q1-Q10) ($1.);
RUN;

Example 4 - INEFFICIENT

DATA SUBSET;
   SET OLD;
   WHERE GROUP = 'A';  /* WHERE statement in the DATA step */
RUN;                   

PROC UNIVARIATE DATA=SUBSET;
   VAR X Y Z;
RUN;

Example 4 - EFFICIENT

PROC UNIVARIATE DATA=OLD;
   WHERE GROUP = 'A';  /* WHERE statement in the PROC step */
   VAR X Y Z;          
RUN;

Example 5 INEFFICIENT

DATA SCORE;
   ARRAY KEY[5] $ 1;
   ARRAY Q[5] $ 1;
   RETAIN KEY1 'A' KEY2 'B' KEY3 'C' KEY4 'D' KEY5 'E';
   INPUT (Q1-Q5)($1.);
   DO I = 1 TO 5;
      RAW + (Q[I]=KEY[I]);
   END;
   PERCENT = 100*RAW/5;
DATALINES;
ABCDA
BBCAC
EBCAD
 ...
;                                                   

Example 5 - EFFICIENT

DATA SCORE;
   ARRAY KEY[5] $ 1;
   ARRAY Q[5] $ 1;
   RETAIN KEY1 'A' KEY2 'B' KEY3 'C' KEY4 'D' KEY5 'E';
   INPUT (Q1-Q5)($1.);
   DO I = 1 TO 5;
      RAW + (Q[I]=KEY[I]);
   END;
   PERCENT = 100*RAW/5;
   KEEP RAW PERCENT; 
   *or DROP KEY1-KEY5 Q1-Q5 I;
DATALINES;
ABCDA
BBCAC
EBCAD
 ...
;                                                   

Example 6 - INEFFICIENT

DATA NEW;
   SET OLD;
   (programming statements)
   DROP X1-X20;  /* DROP statement */
RUN;

Example 6 - EFFICIENT

DATA NEW;
   SET OLD (DROP=X1-X20);   /* DROP data set option */
   (programming statements) 
RUN;

Example 7 - INEFFICIENT 

DATA LONG;
   INPUT ID 1-3 
     @4 (Q1-Q10) (1.) 
     @15 HEIGHT   2. 
     @17 WEIGHT   3.;
DATALINES;
  . . .
;

Example 7 - EFFICIENT

DATA SHORT;
   LENGTH HEIGHT WEIGHT 4;
   INPUT ID $ 1-3
      @4 (Q1-Q10) ($1.)
      @15 HEIGHT    2.
      @17 WEIGHT    3.;
DATALINES;
  . . .
;

Example 8 - INEFFICIENT

DATA ONE;
   SET TWO;
   IF  0 LE AGE LE 10 THEN AGEGRP = 1;
   IF 10 LT AGE LE 20 THEN AGEGRP = 2;
   IF 20 LT AGE LE 30 THEN AGEGRP = 3;
   IF 30 LT AGE LE 40 THEN AGEGRP = 4;
   IF       AGE GT 40 THEN AGEGRP = 5;
RUN;

Example 8 - EFFICIENT

DATA ONE;
   SET TWO;
   IF       0 LE AGE LE 10 THEN AGEGRP = 1;
   ELSE IF 10 LT AGE LE 20 THEN AGEGRP = 2;
   ELSE IF 20 LT AGE LE 30 THEN AGEGRP = 3;
   ELSE IF 30 LT AGE LE 40 THEN AGEGRP = 4;
   ELSE IF       AGE GT 40 THEN AGEGRP = 5;
RUN;

Example 9 - INEFFICIENT

DATA ONE;
   SET TWO;
   IF       0 LE AGE LE 10 THEN AGEGRP = 1;
   ELSE IF 10 LT AGE LE 20 THEN AGEGRP = 2;
   ELSE IF 20 LT AGE LE 30 THEN AGEGRP = 3;
   ELSE IF 30 LT AGE LE 40 THEN AGEGRP = 4;
   ELSE IF       AGE GT 40 THEN AGEGRP = 5;
RUN;

Example 9 - EFFICIENT

DATA ONE;
   SET TWO;
   IF      30 LT AGE LE 40 THEN AGEGRP = 4;
   ELSE IF       AGE GT 40 THEN AGEGRP = 5;
   ELSE IF  0 LE AGE LE 10 THEN AGEGRP = 1;
   ELSE IF 10 LT AGE LE 20 THEN AGEGRP = 2;
   ELSE IF 20 LT AGE LE 30 THEN AGEGRP = 3;
RUN;

Example 10 - INEFFICIENT

DATA NEW;
   SET OLD;
   IF GROUP IN ('A','C','E','Z');
RUN;

Example 10 - EFFICIENT

DATA NEW;
   SET OLD;
   IF GROUP = 'A' OR GROUP = 'C' OR GROUP = 'E' OR GROUP = 'Z';
RUN;

Example 11 - INEFFICIENT

DATA UNNECESS;
   FILE PRINT;
   SET OLD;
   IF AGE GT 50 THEN PUT ID= AGE=;
RUN;

Example 11 - EFFICIENT

DATA _NULL_;
   FILE PRINT;
   SET OLD;
   IF AGE GT 50 THEN PUT ID= AGE=;
RUN;

Example 12 - INEFFICIENT

DATA TEMP;
   INFILE 'input_file_spec';
   INPUT . . .;
RUN;

PROC anyproc DATA=TEMP;
   etc.
RUN;

(at a future time)
DATA TEMP;
   INFILE 'input_file_spec';    * SAME AS BEFORE ;
   INPUT . . .;
RUN;

PROC anotherproc DATA=TEMP;
   etc.
RUN;

Example 12 - EFFICIENT

LIBNAME libref 'SAS_data_library';
DATA libref.PERMAN;
   INFILE 'input_file_spec';
      INPUT . . .;

PROC anyproc DATA=libref.PERMAN;

RUN;
     

(At a future time)
PROC anotherproc DATA=libref.PERMAN;
   etc.
RUN;   

Example 13 - INEFFICIENT

LIBNAME libref 'SAS_data_library';
DATA NEW;
   SET libref.OLD (RENAME=( X = NEWX  Y = NEWY));
   LABEL HT = 'Height of Subject';
   FORMAT DOB MMDDYY8.;
RUN;                                            

Example 13 - EFFICIENT

LIBNAME libref 'SAS_data_library';
PROC DATASETS LIBRARY=libref;
   MODIFY OLD;
      RENAME X = NEWX Y = NEWY;
      LABEL HT = 'Height of Subject';
      FORMAT DOB MMDDYY8.;
RUN;                                 

Example 14 - INEFFICIENT

DATA NEWNAME;
   SET OLDNAME;
RUN;

Example 14 - EFFICIENT

PROC DATASETS;
   CHANGE OLDNAME = NEWNAME;
RUN;

Example 15 - INEFFICIENT

DATA UPDATE;
   SET MASTER NEW;
RUN;

Example 15 - EFFICIENT

PROC APPEND BASE=MASTER DATA=NEW;
RUN;

Example 16 - INEFFICIENT

DATA TEST;
   INFILE 'file_specification';
   A  =  .10;
   B  = 1.57;
   PI = 3.14159;
   INPUT X @@;
   Y = A * X + B * PI * X;
   DROP A B PI;
RUN;

Example 16 - EFFICIENT

DATA TEST;
   INFILE 'file_specification';
   RETAIN A   .10 
          B  1.57 
          PI 3.14159;
   INPUT X @@;
   Y = A * X + B * PI * X;
   DROP A B PI;
RUN;                           

Example 17 - INEFFICIENT

PROC SORT DATA=TEST;
   BY YEAR;
RUN;

PROC anyprocs;
   BY YEAR;
   ...
RUN;

PROC SORT DATA=TEST;
   BY YEAR MONTH;
RUN;

PROC otherprocs;
   BY YEAR MONTH;
   .
   .
   .
RUN;

Example 17 - EFFICIENT

PROC SORT DATA=TEST;
   BY YEAR MONTH;
RUN;

PROC anyprocs;
   BY YEAR;
   ...
RUN;

PROC otherprocs;
   BY YEAR MONTH;
   .
   .
   .
RUN;

Example 18 - INEFFICIENT

PROC SORT DATA=TEST;
   BY YEAR;
RUN;

PROC MEANS NOPRINT DATA=TEST;
   BY YEAR;
   VAR COST;
   OUTPUT OUT=MEANS MEAN=;
RUN;

Example 18 - EFFICIENT

PROC MEANS NOPRINT NWAY DATA=TEST;
   CLASS YEAR;
   VAR COST;
   OUTPUT OUT=MEANS MEAN=;
RUN;

Example 19 - INEFFICIENT

PROC SORT DATA=OLD;
   BY ID DATE;
RUN;

DATA NEW;
   SET OLD(DROP=X1-X10);
   WHERE YEAR BETWEEN '01JAN90'D AND '31DEC93'D;
RUN;                                            

Example 19 - EFFICIENT

PROC SORT DATA=OLD(DROP=X1-X10) OUT=NEW;
   BY ID DATE;
   WHERE YEAR BETWEEN '01JAN90'D AND '31DEC93'D;
RUN;                                             

Example 20 - INEFFICIENT 

ROC SORT DATA=TEST;
   BY YEAR;
RUN;

Example 20 - EFFICIENT

PROC SORT DATA=TEST NOEQUALS;
   BY YEAR;
RUN;

***Chapter 14 problems;
14-1. DATA ONE;
         INPUT GROUP $ X Y Z;
      DATALINES;
      A 1 2 3
      B 2 3 4
      B 6 5 4
      A 4 5 6
      RUN;

      PROC SORT DATA=ONE;
         BY GROUP;
      RUN; 

      PROC MEANS N MEAN STD DATA=ONE;
         BY GROUP;
         VAR X Y Z;
      RUN;

      DATA TWO;
         SET ONE;
         IF 0 LE X LE 2 THEN XGROUP=1;
         IF 2 LT X LE 4 THEN XGROUP=2;
         IF 4 LT X LE 6 THEN XGROUP=3;
      RUN;

      PROC FREQ DATA=TWO;
         TABLES XGROUP;
      RUN;


14-2. Data set OLD contains variables SCORE1-SCORE100, X1-X100, ID, and 
GENDER. 

      DATA NEW;
         SET OLD;
         RAWSCORE = SUM (OF SCORE1-SCORE100);
      RUN;

      PROC SORT DATA=NEW;
         BY GENDER;
      RUN;

      PROC MEANS N MEAN STD MAXDEC=3;
         BY GENDER;
         VAR RAWSCORE;
      RUN;


14-3. The raw data file 'BIGFILE' contains one or more blanks between all 
data values. SAS data set variables ITEM1-ITEM5 are one byte in length. 

      DATA ONE;  
         INFILE 'BIGFILE';
         INPUT GENDER $ ITEM1-ITEM5 X Y Z;
         IF GENDER = 'M' THEN COMPUTE = 2 * X + Y;
         IF GENDER = 'F' THEN COMPUTE = 2 * X;
      RUN;

      PROC FREQ DATA=ONE;
         TABLES ITEM1-ITEM5;
      RUN;

      PROC PLOT DATA=ONE;
         PLOT Z * COMPUTE;
      RUN;

     Note:  We are using ITEM1-ITEM5 only frequencies only and will not be 
performing arithmetic operations on these variables. 


14-4. SAS data set ONE contains variables GROUP, GENDER, RACE, and X1-X100. 

      DATA TWO;
         SET ONE;
         IF GROUP = 1 OR GROUP = 3 OR GROUP = 5;
      RUN;

      PROC FREQ DATA=TWO;
         TABLES GENDER * RACE;
      RUN;

Problem 5 - sample program
      PROC SORT DATA=LARGE;
         BY DATE;
      RUN;

      DATA FIRST;
         SET LARGE;
         BY DATE;
         WHERE YEAR BETWEEN 1990 AND 1993;
         DROP X1-X100;
         IF FIRST.DATE;
      RUN;

      DATA LAST;
         SET LARGE;
         BY DATE;
         WHERE YEAR BETWEEN 1990 AND 1993;
         DROP X1-X100;
         IF LAST.DATE;
      RUN;

***Problem Solutions ;

     Here are our solutions to the end of chapter problems.  PLEASE make 
an attempt to solve these problems yourself before looking at our 
solutions.  You will learn much more that way. 

Chapter 1 - INPUT

Solution 1-1

     DATA VSIGNS;
       INFILE  'VITAL';
       INPUT  ID $ HR SBP DBP;
     RUN;
                     
     Did you remember to code ID as character because it contains letters 
and numbers? 


Solution 1-2

     DATA VSIGNS;
        INFILE  'VITALC' DLM=',';
        INPUT  ID $ HR SBP DBP;
     RUN; 


Solution 1-3

     DATA COLLEGE;
        INPUT NAME : $9. TITLE $ TENURE $ NUMBER;
     DATALINES;
     Stevenson Ph.D. Y 2
     Smith Ph.D.   N   3
     Goldstein  M.D.  Y  1
     RUN;


Solution 1-4

     DATA COLLEGE;
        INPUT NAME & $16. TITLE $ TENURE $ NUMBER;
     DATALINES;
     George Stevenson   Ph.D. Y 2
     Fred Smith   Ph.D.   N   3
     Alissa Goldstein  M.D.  Y  1
     RUN;


Solution 1-5

     DATA RESPOND;
        INFILE 'FIRE';
        INFORMAT DATE MMDDYY8.;
        INPUT  CALL_NO  1-3
               DATE     5-12
               TRUCKS   14-15
               ALARM    17;
     RUN;


Solution 1-6


     DATA RESPOND;
        INFILE 'FIRE';
        INPUT  @1  CALL_NO    3.
               @5  DATE       MMDDYY8.
               @14 TRUCKS     2.
               @17 ALARM      1.;
     RUN;
                      

Solution 1-7

     DATA FACTORY;
        INPUT   @1  ID       $7.
                @1  F_NUM     2.
                @6  STATE    $2. 
                @8  QUANTITY  2.
                @10 PRICE     DOLLAR7.;
     DATALINES;
     13AB2NY44   $123
     22XXXCT88 $1,033
     37123TX11$22,999
     RUN;


Solution 1-8

     DATA SCORES;
        INPUT  @1  SS   $11.
               @13 (SCORE1 - SCORE10) (3.);
     DATALINES;
     123-45-6789 100 98 96 95 92 88 95 98100 90
     344-56-7234  69 79 82 65 88 78 78 92 66 77
     898-23-1234  80 80 82 86 92 78 88 84 85 83
     RUN;

Solution 1-10

     DATA PRESSURE;
        INPUT  @1 (SBP1 - SBP4) (3. + 3)
               @4 (DBP1 - DBP4) (3. + 3);
     DATALINES;
     120 80122 84128 90130 92
     140102138 96136 92128 84
     122 80122 80124 82122 78
     RUN;


Solution 1-10

     DATA HTWT;
        INFILE 'MIXED_UP';
        INPUT @12 TEST @;
           IF TEST = 1 THEN 
              INPUT EMP_ID 1-3
                    HEIGHT 4-5
                    WEIGHT 6-8;
           ELSE IF TEST = 2 THEN 
              INPUT EMP_ID 1-3
                    HEIGHT 5-6
                    WEIGHT 8-10;
     RUN;


Solution 1-11

     DATA CARS;
        INPUT MAKE : $10. MPG @@;
     DATALINES;
     Taurus 20 Civic 29 Cutlass 20 Cadillac 17
     Mazada 24 Corvette 17
     RUN;


Solution 1-12

     DATA SCORES;
        IF EOF1 = 0 THEN INFILE 'FILE_ONE' END = EOF1;
        ELSE INFILE 'FILE_TWO';
        INPUT NAME $ 1-10 SCORE 11-13;
     RUN;


Chapter 2 - Data Recoding

Solution 2-1 

Method 1 - IF-THEN-ELSE

     DATA HTWT_2;
        SET HTWT;
        IF   0 LE HEIGHT LE 36 THEN HT_GROUP = 1;
        ELSE IF 37 LE HEIGHT LE 48 THEN HT_GROUP = 2;
        ELSE IF 49 LE HEIGHT LE 60 THEN HT_GROUP = 3;
        ELSE IF HEIGHT GT 60 THEN HT_GROUP = 4;
      
        IF    0 LE WEIGHT LE 100 THEN WT_GROUP = 1;
        ELSE IF 101 LE WEIGHT LE 200 THEN WT_GROUP = 2;
        ELSE IF WEIGHT GT 200 THEN WT_GROUP = 3;
     RUN;

     PROC FREQ;
        TABLES HT_GROUP * WT_GROUP;
     RUN;

Method 2 - SELECT

     DATA HTWT_2;
        SET HTWT;
           SELECT;
              WHEN (0 LE HEIGHT LE 36)  HT_GROUP = 1;
              WHEN (37 LE HEIGHT LE 48) HT_GROUP = 2;
              WHEN (49 LE HEIGHT LE 60) HT_GROUP = 3;
              WHEN (HEIGHT GT 60)       HT_GROUP = 4;
           END;
      
           SELECT;
              WHEN (0 LE WEIGHT LE 100)   WT_GROUP = 1;
              WHEN (101 LE WEIGHT LE 200) WT_GROUP = 2;
              WHEN (WEIGHT GT 200)        WT_GROUP = 3;
           END;
     RUN;

     PROC FREQ;
        TABLES HT_GROUP * WT_GROUP;
     RUN;

Method  3 - Using PROC FORMAT to Recode Value


     PROC FORMAT;
        VALUE HTFMT    0-36 = '1'
                      37-48 = '2'
                      49-60 = '3'
                    61-HIGH = '4';

        VALUE WTFMT   0-100 = '1'
                    101-200 = '2'
                   201-HIGH = '3';
     RUN;
          
     PROC FREQ;
        TABLES HEIGHT * WEIGHT;
        FORMAT HEIGHT HTFMT.
        WEIGHT WTFMT.;
     RUN;

Method 4 - Using a User-Defined FORMAT and a PUT Function 

     PROC FORMAT;
        VALUE HTFMT    0-36 = '1'
                      37-48 = '2'
                      49-60 = '3'
                    61-HIGH = '4';

        VALUE WTFMT   0-100 = '1'
                    101-200 = '2'
                   201-HIGH = '3';
     RUN;

     DATA HTWT_2;
        SET HTWT;
        HT_GROUP = PUT(HEIGHT,HTFMT.);
        WT_GROUP = PUT(WEIGHT,WTFMT.);
     RUN;

     PROC FREQ;
        TABLES HT_GROUP * WT_GROUP;
     RUN;

Chapter 3 - Set, Merge, Update

Solution 3-1

     DATA ALL;
        SET ONE   (DROP=SEX)
            TWO   (KEEP=ID DOB SALARY)
            THREE (KEEP=ID DOB SALARY);
     RUN;


Solution 3-2

     DATA ALL;
        SET ONE   (DROP=SEX)
            TWO   (KEEP=IDNUM DOB SALARY
                   RENAME=(IDNUM=ID))
            THREE (KEEP=ID DOB SALARY);

        WHERE=(DOB LE '01JAN60'D AND DOB IS NOT MISSING 
                                 AND SALARY GE 50000));
     FORMAT DOB MMDDYY8.;
     RUN;

     Below is an alternative solution that uses a WHERE option on the SET 
statement instead of a WHERE statement.  Although we didn't discuss a 
WHERE data set option in the chapter, we thought you might like to see an 
example of how it can be used.  You may want to refer to Chapter 14 
(Efficiency) to see more about the differences between WHERE statements 
and WHERE data set options. 

     DATA ALL;
        SET ONE   (DROP=SEX
                   WHERE=(DOB LE '01JAN60'D 
                          AND DOB IS NOT MISSING 
                          AND SALARY GE 50000))
            TWO   (KEEP=IDNUM DOB SALARY
                   WHERE=(DOB LE '01JAN60'D 
                   AND DOB IS NOT MISSING
                   AND SALARY GE 50000)
                   RENAME=(IDNUM=ID))
            THREE (KEEP=ID DOB SALARY
                   WHERE=(DOB LE '01JAN60'D 
                   AND DOB IS NOT MISSING
                   AND SALARY GE 50000));
     FORMAT DOB MMDDYY8.;
     RUN;


Solution 3-3

a)   PROC PRINT DATA=MASTER;
        WHERE LASTNAME LIKE '%fly' AND
              AGE GE 40 AND
              GENDER EQ 'M';
         TITLE 'Possible Employee Names';
         ID FIRSTNAM;
         VAR LASTNAME;
      RUN;

b)   PROC PRINT DATA=MASTER;
        WHERE LASTNAME =* 'Klein' AND
              FIRSTNAM LIKE 'G____';
         TITLE 'Possible Employee Names';
         ID FIRSTNAM;
         VAR LASTNAME;
      RUN;


Solution 3-4

     PROC SORT DATA=DEMOG;
        BY ID;
     RUN;

     PROC SORT DATA=SCORES;
        BY SS;
     RUN;

     DATA BOTH;
        MERGE DEMOG  (IN=IN_DEMOG)
              SCORES (IN=IN_SCR
                      RENAME=(SS=ID));
        BY ID;
        IF IN_DEMOG AND IN_SCR;
     RUN;

     PROC MEANS N MEAN MAXDEC=2 DATA=BOTH;
        CLASS GENDER;
        VAR IQ GPA;
     RUN;

     PROC MEANS N MEAN MAXDEC=2 DATA=BOTH;
        WHERE DOB LT '01JAN72'D and DOB IS NOT MISSING;
        CLASS GENDER;
        VAR IQ GPA;
     RUN;


Solution 3-5

     DATA NEWDATA;
        INPUT PART NUMBER PRICE;
     DATALINES;
     222 15 .
     123 . 1500
     333 20 2000
     RUN;

     PROC SORT DATA=NEWDATA;
        BY PART;  
     *As an alternative, you could have entered the part
      numbers in order;
     RUN;

     DATA MASTER;
        UPDATE MASTER NEWDATA;
        BY PART;
     RUN;


Chapter 4 - Table Lookup

Solution 4-1

     PROC SORT DATA=SALES;
        BY PART_NO;
     RUN;

     DATA NEWSALES;
        MERGE PARTS
              SALES (IN=INSALES);
        BY PART_NO;
        IF INSALES;
        TOTAL = QUANTITY * PRICE;
        KEEP ID TRANS TOTAL;
     RUN;

     PROC SORT DATA=NEWSALES;
        BY ID;
     RUN;

     PROC PRINT LABEL;
        TITLE 'Sales totals for each Salesperson and Transaction';
        LABEL ID = 'Employee ID'
              TRANS = 'Transaction Number'
              TOTAL = 'Sales per Transaction';
        ID ID;
        VAR TRANS TOTAL;
        FORMAT TOTAL DOLLAR4.;
     RUN;

     DATA ALLTHREE;
        MERGE EMPLOY (DROP=DOB) NEWSALES (IN=INNEW);
        BY ID;
        IF INNEW;
     RUN;

     PROC MEANS SUM DATA=ALLTHREE MAXDEC=0;
        TITLE 'Sales Totals for each Employee';
        CLASS ID;
        VAR TOTAL;
     RUN;

     PROC MEANS SUM DATA=ALLTHREE MAXDEC=0;
        TITLE 'Sales Summary by Gender';
        CLASS GENDER;
        VAR TOTAL;
     RUN;
     *******************************************************
     * You can replace the two PROC MEANS above with this  *
     * alternate code if you want a nicer looking report   *
     *******************************************************;
     PROC MEANS DATA=ALLTHREE NOPRINT;
        CLASS GENDER ID;
        VAR TOTAL;
        OUTPUT OUT=TOTALS SUM=;
     RUN;

     PROC PRINT LABEL DATA=TOTALS;
        TITLE 'Sales Totals for each Employee';
        WHERE _TYPE_ = 1;
        * _TYPE_ = 1 will select the sums for each
          ID.  See chapter 10 for details;
        LABEL ID = 'Employee ID'
              TOTAL = 'Sales per Sales Person';
        ID ID;
        VAR TOTAL;
        FORMAT TOTAL DOLLAR4.;
     RUN;

     PROC PRINT LABEL DATA=TOTALS;
        TITLE 'Sales Totals for Gender';
        WHERE _TYPE_ = 2;
        * _TYPE_ = 2 will select the sums for each
          GENDER.  See chapter 10 for details;
        LABEL ID = 'Employee ID'
              TOTAL = 'Total Sales';
        ID GENDER;
        VAR TOTAL;
        FORMAT TOTAL DOLLAR4.;
     RUN;


Solution 4-2

     PROC FORMAT;
        VALUE DXCODE
           1 = 'Cold'
           2 = 'Flu'
           3 = 'Asthma'
           4 = 'Chest Pain'
           5 = 'Maternity'
           6 = 'Diabetes';
     RUN;

     DATA CLINICAL;
        INFILE 'CLINICAL';
        INPUT ID  DATE : MMDDYY8. BILLING DX;
     RUN;

     DATA NEW;
        SET CLINICAL;
        DESCRIP = PUT (DX,DXCODE.);
     RUN;


Chapter 5 - SAS Functions

Solution 5-1

     DATA FUNCT1;
        SET ORIG;
        LENGTH CHAR_ID $ 4;
        LOGSCORE = LOG (SCORE);
        PROPX = ARSIN (SQRT(PROP));
        RND_IQ = ROUND (IQ,10);
        TMP = PUT (ID,5.);
        CHAR_ID = SUBSTR (TMP,1,2) || SUBSTR (TMP,4,2);
        DROP TMP;
     RUN;                                              


Solution 5-2

     DATA SUMMARY;
        SET SCORES;
        SUM_X = SUM (OF X1-X20);
        IF NMISS (OF Y1-Y20) LT 5 THEN 
           MEAN_Y = MEAN (OF Y1-Y20);
        X_MIN = MIN (OF X1-X20);
        X_MAX = MAX (OF X1-X20);
     RUN;


Solution 5-3

     DATA TEMP;
        INFILE 'TEMPER';
        INPUT HOUR DUMMY $ @@;
        IF DUMMY = 'N' THEN TEMP_F = .;
           ELSE IF INDEX(DUMMY,'C') NE 0 THEN
           TEMP_F = 9*INPUT (SUBSTR(DUMMY,1,LENGTH(DUMMY)-1),5.)/5 + 32;
           ELSE TEMP_F = INPUT (DUMMY,5.);
        DROP DUMMY;
     RUN;


Solution 5-4

     DATA VALID INVALID;
        RETAIN DIGITS '0123456789';
        INPUT  @1 STRING $15.;

        *Remove blanks;

        STRING = COMPRESS (STRING);
        *Take out the parentheses and - from the number;

        NUMBERS = COMPRESS (STRING,'()-');
        IF INDEX (STRING,'(') NE 1 OR
           INDEX (STRING,')') NE 5 OR
           INDEX (STRING,'-') NE 9 OR
           VERIFY (NUMBERS,DIGITS) NE 0 THEN OUTPUT INVALID;
        ELSE OUTPUT VALID;
        DROP NUMBERS DIGITS;
     DATALINES;
     (908)463-4490
     (201) 343-2233
     456-5034
     (123)456-7890
     (201)SH4-1234
     (512)2578362
     RUN;                                                   

Solution 5-5

a)   DATA TWO_B;
        SET TWO;
        DATE1 = PUT (INPUT(DATE2,DATE7.),MMDDYY8.);
     RUN;

     PROC SORT DATA=ONE;
         BY DATE1;
     RUN;
 
     PROC SORT DATA=TWO_B;
        BY DATE1;
     RUN;

     DATA BOTH;
        MERGE ONE TWO_B;
        BY DATE1;
     RUN;

b)   DATA ONE_B;
        SET ONE;
        DATE = INPUT (DATE1,MMDDYY8.);
     RUN;

     DATA TWO_B;
        SET TWO;
        DATE = INPUT (DATE2,DATE7.);
     RUN;

     PROC SORT DATA=ONE_B;
        BY DATE;
     RUN;
 
     PROC SORT DATA=TWO_B;
        BY DATE;
     RUN;

     DATA BOTH;
        MERGE ONE_B TWO_B;
        BY DATE;
        FORMAT DATE MMDDYY8.;
     RUN;


Solution 5-6

     DATA TIMEAVE;
        SET STOCKS;
        XXX1 = LAG (XXX);
        YYY1 = LAG (YYY);
        XXX2 = LAG2 (XXX);
        YYY2 = LAG2 (YYY);
        XXX3 = LAG3 (XXX);
        YYY3 = LAG3 (YYY);
        AVE_XXX = MEAN (OF XXX XXX1 XXX2 XXX3);
        AVE_YYY = MEAN (OF YYY YYY1 YYY2 YYY3);
        KEEP DAY AVE_XXX AVE_YYY;
     RUN;

Solution 5-7

Solution without ARRAYS.

     DATA NEW;
        SET SCORES;
        X1 = INPUT (SUBSTR(STRING,1,1),1.);
        X2 = INPUT (SUBSTR(STRING,2,1),1.);
        X3 = INPUT (SUBSTR(STRING,3,1),1.);
        X4 = INPUT (SUBSTR(STRING,4,1),1.);
        X5 = INPUT (SUBSTR(STRING,5,1),1.);
        KEEP ID X1-X5;
     RUN;

Solution using ARRAYS.

     DATA NEW;
        SET SCORES;
        ARRAY X[5] X1-X5;
        DO POINTER = 1 TO 5;
           X[POINTER] = INPUT (SUBSTR(STRING,POINTER,1),1.);
        END;
        KEEP ID X1-X5;
     RUN;


Chapter 6 - SAS Dates

Solution 6-1

     DATA DATES1;
        INPUT @1  ID     $3.
              @4  ADMIT  MMDDYY6.
              @10 DISCH  MMDDYY6.
              @16 DOB    MMDDYY8.;
        AGE   = INT((ADMIT-DOB)/365.25);
        DAY   = WEEKDAY (ADMIT);
        MONTH = MONTH (ADMIT);
        FORMAT ADMIT DISCH MMDDYY8. 
               DOB DATE9. 
               DAY DOWNAME3.;
     
     DATALINES;
     00101059201079210211946
     00211129211159209011955
     00305129206099212251899
     00401019301079304051952
     ;

     PROC PRINT DATA=DATES1;
     RUN;

     PROC MEANS DATA=DATES1 N MEAN MAXDEC=1;
        VAR AGE;
     RUN;
     
     PROC CHART DATA=DATES1;
        VBAR DAY / DISCRETE;
     RUN;
     
     PROC FREQ DATA=DATES1;
        TABLES MONTH;
     RUN;                             


Solution 6-2

     DATA AGECOMP;
        SET DATES2;
        DOB = MDY (MONTH,15,YEAR);
        AGE = ROUND ((TODAY()-DOB)/365.25,1);
        FORMAT DOB MMDDYY8.;
     RUN;


Solution 6-3

     DATA VISIT;
        SET DATES3;
        AGE = ROUND((MDY(VISIT_D,VISIT_M,VISIT_Y)-DOB)/365.25,1);
     RUN;


Solution 6-4

     DATA DATES4;
        INPUT @1  DATE     MMDDYY8. 
              @11 CRAYONS  COMMA6.;
        QUARTER = INTCK ('QTR','01JAN90'D,DATE);
     DATALINES;
     02/01/90  12,500
     02/08/90  12,600
     04/01/90  13,000
     05/05/90  12,800
     08/05/90  14,000
     12/12/90  14,200
     02/18/91  14,400
     02/22/91  14,100
     05/01/91  15,000
     ;

     PROC MEANS DATA=DATES4 N MEAN MAXDEC=0;
        CLASS QUARTER;
        VAR CRAYONS;
     RUN;


Solution 6-5

     DATA GOODATES;
        SET CLIENTS;
        DAY   = WEEKDAY (DOB);
        MONTH = MONTH (DOB);
        IF GENDER = 'M' AND DAY IN (4,5) AND MONTH IN (1,3) OR
           GENDER = 'F' AND DAY = 6 AND MONTH IN (8,9);
     RUN;
     
     PROC SORT DATA=GOODATES;
        BY GENDER;
     RUN;

     PROC PRINT DATA=GOODATES;
        TITLE "Clients Meeting the Astrologer's Criteria";
        ID ID;
        BY GENDER;
        VAR DOB DAY MONTH;
        FORMAT DOB MMDDYY8.;
     RUN;

     *** Alternate solution ***;

     DATA GOODATES;
        SET CLIENTS;
        LENGTH DAY MONTH $ 3;
        DAY   = PUT(DOB,DOWNAME3.);
        MONTH = PUT(DOB,MONNAME3.);
        IF (GENDER = 'M' AND UPCASE(DAY)   IN ('WED','THU') 
                         AND UPCASE(MONTH) IN ('JAN','MAR')) 
        OR (GENDER = 'F' AND UPCASE(DAY)    =  'Fri' 
                         AND UPCASE(MONTH) IN ('AUG','SEP'));
     RUN;
     
     PROC SORT DATA=GOODATES;
        BY GENDER;
     RUN;

     PROC PRINT DATA=GOODATES;
        TITLE "Clients Meeting the Astrologer's Criteria";
        ID ID;
        BY GENDER;
        VAR DOB DAY MONTH;
        FORMAT DOB MMDDYY8.;
     RUN;

     This alternate program makes good use of SAS System formats and 
functions.  The two system date formats DOWNAMEn.  and MONNAMEn.  
translate SAS date values into the day of the week and the month of the 
year respectively, using various formats depending on the 'n' numeric 
width specification you add to the end of the format name.  A '3' 
following the format specification will yield three character day names 
and three character month names.  The PUT function creates the character 
variables DAY and MONTH by applying the formats to the date variable DOB.  
The results are the three character day and month names.  The UPCASE 
function insures that the program will always work consistently without 
having to ever worry about case settings.  You can find out more about 
using PUT functions in Chapter 2, and about formats in Chapter 11.  


Chapter 7 - SAS Arrays

Solution 7-1

     DATA NEW;
        SET OLD;
        ARRAY HTIN[10];
        ARRAY WTLB[10];
        ARRAY HTCM[10];
        ARRAY WTKG[10];
        DO I = 1 TO 10;
           HTCM[I] = 2.54 * HTIN[I];
           WTKG[I] = WTLB[I] / 2.2;
        END;
     RUN;

     Note that we omitted the element lists from our array definitions. 
When you do this, the SAS System automatically suffixes the series of 
numbers from the index (1 to 10 from index [10] in this example) to the 
array bases (HTIN, WTLB, HTCN and WTKG) to make up the elements.  The 
array HTIN for example is automatically constructed with the elements 
HTIN1-HTIN10. 


Solution 7-2

     DATA NEW;
        SET SURVEY;
        ARRAY MINUS_1[100] X1-X100;
        ARRAY NUM99[50] Y1-Y50;
        ARRAY NODATA[5] $ A B C D E;
        DO I = 1 TO 100;
           IF MINUS_1[I] = -1 THEN MINUS_1[I] = .;
        END;
        DO I = 1 TO 50;
           IF NUM99[I] = 99 THEN NUM99[I] = .;
        END;
        DO I = 1 TO 5;
           IF NODATA[I] = 'NO DATA' THEN NODATA[I] = ' ';
        END;
        DROP I;
     RUN;

     The first two DO loops could alternatively be coded as one DO loop as 
follow:: 

     DO I = 1 TO 100;
        IF MINUS_1[I] = -1 THEN MINUS_1[I] = .;
        IF I LE 50 AND NUM99[I] = 99 THEN NUM99[I] = .;
     END;


Solution 7-3

     DATA FOURPER;
        SET ONEPER;
        ARRAY XHR[4] HR1-HR4;
        DO TREAT = 1 TO 4;
           HR = XHR[TREAT];
           OUTPUT;
        END;
        KEEP ID TREAT HR;
     RUN;


Solution 7-4

     DATA FOURPER;
        SET ONEPER;
        ARRAY XHR[4] HR1-HR4;
        DO I = 1 TO 4;
           IF      I = 1 THEN TREAT = 'A';
           ELSE IF I = 2 THEN TREAT = 'B';
           ELSE IF I = 3 THEN TREAT = 'C';
           ELSE IF I = 4 THEN TREAT = 'D';
           HR = XHR[I];
           OUTPUT;
        END;
        KEEP ID TREAT HR;
     RUN;

     Here is an interesting alternative to the previous code using some of 
the functions discussed in Chapter 5. 

     DATA FOURPER;
        SET ONEPER;
        ARRAY XHR[4] HR1-HR4;
        DO I = 1 TO 4;
           TREAT = TRANSLATE (PUT(I,1.),'ABCD','1234');
           HR = XHR[I];
           OUTPUT;
        END;
        KEEP ID TREAT HR;
     RUN;


Solution 7-5

     DATA ONEPER;
        SET THREEPER;
        BY ID;
        RETAIN SBP1-SBP3 DBP1-DBP3;
        ARRAY XSBP[3] SBP1-SBP3;
        ARRAY XDBP[3] DBP1-DBP3;
        XSBP[TIME] = SBP;
        XDBP[TIME] = DBP;
        IF LAST.ID THEN OUTPUT;
        KEEP ID SBP1-SBP3 DBP1-DBP3;
     RUN;


Chapter 8 - RETAIN

Solution 8-1

     DATA DIET2;
        SET DIET;
        BY ID;
        RETAIN WEIGHT;
        IF FIRST.ID THEN MEAN_WT = WEIGHT;
        ELSE MEAN_WT = MEAN_WT + WEIGHT;
        IF LAST.ID  THEN DO;
           MEAN_WT = MEAN_WT / 4;
           OUTPUT; 
        END;
     RUN;

     The solution using a "sum" statement would look like: 

     DATA DIET2;
        SET DIET;
        BY ID;
        IF FIRST.ID THEN MEAN_WT = WEIGHT;
        ELSE MEAN_WT + WEIGHT;
        IF LAST.ID  THEN DO;
           MEAN_WT = MEAN_WT / 4;
           OUTPUT; 
        END;
     RUN;


Solution 8-2

     DATA READING;
        INFILE 'TESTSCOR';
        RETAIN GROUP;
        INPUT DUMMY $ @@;
        IF (DUMMY='A' OR DUMMY='B' OR DUMMY='C') THEN DO;
           GROUP=DUMMY;
           DELETE;
        END;
        ELSE SCORE=INPUT (DUMMY,5.);
        DROP DUMMY;
     RUN;

     What about that DELETE statement?  Aren't we deleting each initial 
occurrence of a new group?  Yes, we are, but don't worry.  We RETAIN it as 
GROUP in each subsequent observation until we get to a new group.  

     Two alternatives to the line, IF (DUMMY='A' ... ) THEN DO; are: 

      IF DUMMY IN ('A', 'B', 'C') THEN DO;

      IF VERIFY (DUMMY,'CAB') EQ 0 THEN DO;

     These are explained in Chapter 5 - Functions.  You choose.


Chapter 9 - PROC PRINT

Solution 9-1

     PROC PRINT DATA=DONOR;
        VAR F_NAME L_NAME AMOUNT DATE;
        FORMAT AMOUNT DOLLAR8. DATE MMDDYY8.;
     RUN;


Solution 9-2

     OPTIONS NOCENTER NODATE NONUMBER;

     PROC PRINT DATA=DONOR N LABEL UNIFORM;
        TITLE 'Report on the Donor Data Base';
        TITLE2 '-----------------------------';
        ID F_NAME;
        VAR L_NAME AMOUNT DATE;

        SUM AMOUNT;

        LABEL F_NAME = 'First Name'
              L_NAME = 'Last Name'
              AMOUNT = 'Amount of Donation'
              DATE   = 'Donation Date';

        FORMAT AMOUNT DOLLAR8. DATE DATE7.;
     RUN;

     Note: The option UNIFORM will force the listing on multiple pages to 
be space identically. 

Solution 9-3

a)   PROC PRINT DATA=DONOR HEADING=VERTICAL WIDTH=MINIMUM;
        VAR F_NAME L_NAME AMOUNT DATE;
        FORMAT AMOUNT DOLLAR8. DATE MMDDYY8.;
     RUN;
   
b)   PROC PRINT DATA=DONOR HEADING=HORIZONTAL WIDTH=UNIFORM;
        VAR F_NAME L_NAME AMOUNT DATE;
        FORMAT AMOUNT DOLLAR8. DATE MMDDYY8.;
     RUN;
      

Chapter 10 - PROC MEANS

Solution 10-1

     PROC MEANS DATA=GRADES;
        CLASS GENDER;
        VAR SCORE;
        OUTPUT OUT=MEAN_GRD MEAN=;
     RUN;


Solution 10-2

     PROC MEANS DATA=GRADES NOPRINT NWAY;
        CLASS GENDER WEIGHT;
        VAR SCORE;
        OUTPUT OUT=PROB2 MEAN=AVE_SCOR;
     RUN;

     PROC PRINT DATA = PROB2; 
     RUN;


Solution 10-3

     PROC MEANS DATA=GRADES NOPRINT;
        CLASS GENDER WEIGHT;
        VAR SCORE;
        OUTPUT OUT=PROB3 MEAN=;
     RUN;

     DATA BYGENDER;
        SET PROB3;
        WHERE _TYPE_ = 2;
     RUN;


Solution 10-4

     PROC MEANS DATA=EXPER NOPRINT NWAY;
        CLASS GROUP TIME;
        VAR SCORE;
        OUTPUT OUT=MEANOUT MEAN=;
     RUN;

     PROC PLOT DATA=MEANOUT;
        PLOT SCORE * TIME = GROUP;
     RUN;


Solution 10-5

     PROC SORT DATA=CLINTEST;
        BY PATNUM DATE;
     RUN;

     DATA LAST (RENAME=(DATE=LASTDATE CHOL=LASTCHOL));
        SET CLINTEST (KEEP = PATNUM DATE CHOL);
        BY PATNUM;
        IF LAST.PATNUM;
     RUN;

     * Output means for each patient to a data set;
     PROC MEANS DATA=CLINTEST NOPRINT NWAY;
        CLASS PATNUM;
        VAR CHOL SBP DBP HR;
        OUTPUT OUT=STATS
               MEAN=MEANCHOL MEANSBP MEANDBP MEANHR;

     RUN;

     * Combine the LAST data set with the STATS data set;
     DATA FINAL;
        MERGE STATS LAST;
        BY PATNUM;
     RUN;

     * Print a final report;
     PROC PRINT DATA=FINAL LABEL DOUBLE;
        TITLE 'Listing of data set FINAL in Problem 10-5';
        ID PATNUM;
        VAR LASTDATE LASTCHOL MEANCHOL MEANSBP MEANDBP
            MEANHR;

        LABEL LASTDATE = 'Date of Last Visit'
              MEANCHOL = 'Mean Chol'
              MEANSBP  = 'Mean SBP'
              MEANDBP  = 'Mean DBP'
              MEANHR   = 'Mean HR'
              LASTCHOL = 'Last Chol';

        FORMAT MEANCHOL MEANSBP MEANDBP MEANHR LASTCHOL 5.0;
     RUN;


Chapter 11 - PROC FORMAT

Solution 11-1

     PROC FORMAT;
        VALUE $GENDER 'M'='Male'
                      'F'='Female';

        VALUE $RACE   'W'='White'
                      'A'='African American'
                      'H'='Hispanic'
                      OTHER ='Other';

        VALUE ISSUE   1='Str Disagree'
                      2='Disagree'
                      3='No opinion'
                      4='Agree'
                      5='Str Agree';

        VALUE YESNO   0='No'
                      1='Yes';
     RUN;


     The following DATA step statement will assign the above formats to 
the variables listed: 

     FORMAT GENDER        $GENDER. 
            RACE          $RACE. 
            ISSUE1-ISSUE5 ISSUE.
            QUES1-QUES10  YESNO.;



Solution 11-2

     The solution to this problems requires you to create a Control data 
set from the raw data file 'ZIP', and then to use the new data set as 
input to PROC FORMAT. 

     DATA ZIPCODES;
        RETAIN FMTNAME '$ZIPCODE'  TYPE 'C';
        INFILE 'ZIP';
        INPUT START $ 1-5
              LABEL $ 6-20;
     RUN;

     PROC FORMAT CNTLIN=ZIPCODES;
     RUN;                                           



Solution 11-3

     DATA CONTROL;
        RETAIN FMTNAME 'PARTS'  TYPE 'N';
        SET INVENTRY (RENAME=(PART_NO  = START 
                              DESCRIPT = LABEL));
     RUN;

     PROC FORMAT CNTLIN=CONTROL;
     RUN;                                            



Solution 11-4

     PROC FORMAT;
        INVALUE $GENDER 'F','M'=_SAME_
                        OTHER = ' ';
        INVALUE $RACE   'W','B','I','H' = _SAME_
                        OTHER = ' ';
     RUN;

     One final point (a reward for those of you making it this far).  You 
can add an UPCASE option to the INVALUE statement to convert all input 
data values to upper case before they are tested.  This saves you the task 
of having to use the UPCASE function in the data step to accomplish this 
very frequently needed task.  INVALUE options are placed in parentheses 
directly after the name of the INFORMAT you are creating.  The modified 
code would read: 

     PROC FORMAT;
        INVALUE $GENDER (UPCASE) 'F','M' = _SAME_
                                   OTHER = ' ';

        INVALUE $RACE   (UPCASE) 'W','A','N','H' = _SAME_
                                           OTHER = ' ';
     RUN;


Chapter 12 - PROC CHART

Solution 12-1

     PROC CHART DATA=SALES;
        VBAR ITEM;
     RUN;


Solution 12-2 

     
     PROC CHART DATA=SALES;
        HBAR ITEM / NOSTAT;
     RUN;


Solution 12-3

     PROC CHART DATA=SALES;
        VBAR ITEM / TYPE=PERCENT;
     RUN;


Solution 12-4

a)   PROC CHART DATA=SALES;
        VBAR QUANTITY;
     RUN;

b)   PROC CHART DATA=SALES;
        VBAR QUANTITY / MIDPOINTS = 0 TO 40 BY 10;
     RUN;


Solution 12-5

     PROC CHART DATA=SALES;
        VBAR PRICE / DISCRETE GROUP=ITEM;
     RUN;


Solution 12-6

     PROC CHART DATA=CLINICAL;
        VBAR DRUGGRP / SUMVAR=SBP TYPE=MEAN;
     RUN;


Solution 12-7

     PROC CHART DATA=CLINICAL;
        VBAR CHOL / GROUP=DRUGGRP SUBGROUP=ROUTINE;
     RUN;


Solution 12-8

     PROC CHART DATA=CLINICAL;
        BLOCK DRUGGRP / SUMVAR=DBP TYPE=MEAN GROUP=ROUTINE;
        FORMAT DBP 4.;
     RUN;


Chapter 13 - PROC PLOT

Solution 13-1

     PROC PLOT DATA = CHAP13;
        PLOT Y * X;
     RUN;


Solution 13-2

    PROC PLOT DATA = CHAP13;
        PLOT Y * X = 'o';
     RUN;


Solution 13-3

     PROC PLOT DATA = CHAP13;
        PLOT (Y Z) * X / OVERLAY;  
       *ALTERNATIVE CODE = PLOT Y * X   Z *  X / OVERLAY;
     RUN;


Solution 13-4

     PROC PLOT DATA = CHAP13;
        PLOT Y * X = 'Y'
             Z * X = 'Z'/ OVERLAY;
     RUN;


Solution 13-5

     PROC PLOT DATA = CHAP13;
        PLOT Y * X = CODE;
     RUN;


Solution 13-6

     PROC PLOT DATA = CHAP13;
        PLOT Y * X $ NAME = 'o';
     RUN;


Chapter 14 - Efficiency

Solution 14-1

     DATA ONE;
        INPUT @1 GROUP $1.
              @3 (X Y Z )(1. + 1);
        IF 0 LE X LE 2 THEN XGROUP=1;
        ELSE IF 2 LT X LE 4 THEN XGROUP=2;
        ELSE IF 4 LT X LE 6 THEN XGROUP=3;
     DATALINES;
     A 1 2 3
     B 2 3 4
     B 6 5 4
     A 4 5 6

     PROC MEANS N MEAN STD DATA=ONE;
        CLASS GROUP;
        VAR X Y Z;
     RUN;

     PROC FREQ DATA=ONE;
        TABLES XGROUP;
     RUN;

     Main Points:

     1. We eliminated an unnecessary DATA step (DATA TWO) by creating 
variable XGROUP in the first DATA step (DATA ONE.) 

     2. We used formatted input instead of list directed input in the DATA 
step. 

     3. We used IF-THEN-ELSE structures to create XGROUP.

     4. We eliminated the unnecessary SORT procedure and used a CLASS 
statement instead of a BY statement in the FREQ procedure. 

     An alternative would be to use PROC FORMAT to create a grouping 
format for X and then place a FORMAT statement in the PROC FREQ code to 
process X in the formatted subgroups. 


Solution 14-2

     DATA NEW;
        SET OLD (KEEP=SCORE1-SCORE100 GENDER);
        RAWSCORE = SUM (OF SCORE1-SCORE100);
        DROP SCORE1-SCORE100;
     RUN;
     PROC MEANS N MEAN STD MAXDEC=3;
        CLASS GENDER;
        VAR RAWSCORE;
     RUN;

     Main Points:

     1. We used a KEEP option on the SET statement to only bring those 
variables into the PDV that were necessary for the tasks at hand. 

     2. We used a DROP statement to drop SCORE1-SCORE100; we only needed    
these variables to calculate RAWSCORE. 

     3. We eliminated the SORT procedure and used a CLASS statement in the  
MEANS procedure instead of a BY statement. 


Solution 14-3

     DATA ONE;
        INFILE 'BIGFILE';
        LENGTH GENDER ITEM1-ITEM5 $ 1;
        INPUT GENDER ITEM1-ITEM5 X Y Z;
        COMPUTE = 2 * X + Y * (GENDER = 'M');
     RUN;

     PROC FREQ DATA=ONE;
        TABLES ITEM1-ITEM5;
     RUN;

     PROC PLOT DATA=ONE;
        PLOT Z * COMPUTE;
     RUN;

     Main Points:

     1. We used a length statement to declare ITEM1-ITEM5 (and GENDER) as 
character variables with a length of 1 byte each. 

     2. We condensed the two IF statements into one statement.  COMPUTE is 
equal to the sum of (2 * X) and (Y * another value.)  The other value is 
either 1 (when GENDER = 'M') or 0 (when GENDER NE 'M'.) 

     We could have also used an IF-THEN-ELSE construction as follows:

        IF      GENDER = 'M' THEN COMPUTE = 2 * X + Y;
        ELSE IF GENDER = 'F' THEN COMPUTE = 2 * X;


Solution 14-4

     There are two main areas of attack in this problem to make the code 
more efficient.  We could either eliminate the data step entirely by using 
a WHERE statement in the PROC FREQ code, or make the data step more 
efficient (if we knew we would be doing more analyses on the subset.) 

     * Eliminate the DATA step and use a WHERE statement to 
     * directly process a subset of the original data set;
     PROC FREQ DATA=ONE;
        WHERE GROUP IN (1,3,5);
        TABLES GENDER * RACE;
     RUN;

     * Make the DATA step more efficient by using
     * KEEP and WHERE data set options;
     DATA TWO;
        SET ONE (KEEP=GENDER RACE GROUP
                 WHERE=(GROUP IN (1,3,5)));
     RUN;
     PROC FREQ DATA=TWO;
        TABLES GENDER * RACE;
     RUN;


Solution 14-5

     Sometimes it is actually more efficient to create what may seem like 
an "unnecessary" intermediate data set.  The approach we took here was to 
do all the selection work in the SORT procedure and output a small, sorted 
temporary data set.  This was then further processed with FIRST. and LAST. 
variables to create the final two data sets we needed.  

     PROC SORT DATA=LARGE (DROP=X1-X100)
               OUT=TEMP;
        WHERE YEAR BETWEEN 1990 AND 1993;
        BY DATE ID;
     RUN;

     DATA FIRST LAST;
        SET TEMP;
        BY DATE ID;
        IF FIRST.DATE THEN OUTPUT FIRST;
        ELSE IF LAST.DATE THEN OUTPUT LAST;
        * only the first and last records 
        * for each date will be output; 
     RUN;

     This program and the inefficient program in problem 14-5 were run on 
a UNIX based minicomputer against a SAS data set containing approximately 
9000 observations.  There was a more than 50% reduction in CPU time in the 
improved program. 

/****************************************************************\
| Here are the programs to create the CLINICAL.SAS, MEDICAL.SAS, |
| and SALES.SAS data sets.  These programs can be downloaded and |
| run on your computer to create these data sets.                |
\****************************************************************/

*Program to create the SAS data set CLINICAL
LIBNAME C 'C:\SASDATA'; ***Substitute the subdirectory and/or drive of
                          your choice here;
DATA C.CLINICAL;
LENGTH DRUGGRP ROUTINE $ 1;
INFORMAT DATE MMDDYY8.;
INPUT PATNUM DATE DRUGGRP CHOL SBP DBP HR ROUTINE;
FORMAT DATE MMDDYY8.;
DATALINES;
01    01/05/89     D    400    160     90    88     Y
01    02/15/89     D    350    156     88    80     Y
01    05/18/90     D    350    140     82    76     Y
01    09/09/90     D    300    138     78    78     N
01    11/11/90     D    305    142     82    84     Y
01    01/05/91     D    270    142     80    72     N
01    02/18/91     D    260    156     92    88     N
02    02/19/90     D    390    180    100    82     N
02    02/22/90     D    320    178     88    86     Y
02    02/25/90     D    325    172     82    78     Y
02    04/24/90     D    304    166     78    99     N
02    08/25/90     D    299    150     80    80     Y
02    03/13/91     D    222    144     82    72     Y
02    07/16/91     D    243    140     80    68     Y
02    10/10/91     D    242    138     74    62     Y
02    10/30/91     D    230    156     92    88     N
02    12/25/91     D    200    142     82    80     Y
03    01/01/90     P    387    190    110    90     N
03    02/13/90     P    377    188     96    84     Y
03    05/09/90     P    380    182     88    80     Y
03    08/17/90     P    400    186     92    82     Y
03    10/10/90     P    390    182     90    78     N
03    10/11/90     P    380    178     82    72     Y
03    11/11/90     P    370    160     82    72     Y
03    02/02/91     P    380    156     78    70     Y
04    05/15/91     D    380    120     78    56     Y
04    08/20/91     D    370    122     76    58     N
04    03/23/92     D    355    128     68    60     Y
04    05/02/92     D    306    130     72    68     N
04    07/02/92     D    279    126     74    62     Y
04    07/03/92     D    277    126     74    64     Y
04    07/05/92     D    261    130     80    72     N
05    01/06/90     P    399    188    110    92     N
05    03/06/90     P    377    182    100    88     N
05    04/24/90     P    400    180     92    82     Y
05    06/24/90     P    388    176     88    80     Y
05    08/01/90     P    378    162     82    78     Y
05    10/10/90     P    388    156     78    78     Y
05    12/01/90     P    359    156     72    70     Y
06    01/01/92     D    387    128     62    60     N
06    01/03/92     D    379    128     66    62     Y
06    04/24/92     D    375    132     70    58     N
06    05/01/92     D    365    130     76    66     Y
06    05/28/92     D    321    132     78    68     N
06    06/01/92     D    308    128     72    58     Y
07    01/05/90     P    376    118     68    54     Y
07    04/05/90     P    379    124     72    70     N
07    04/07/90     P    389    120     68    62     Y
07    06/28/90     P    388    124     78    60     Y
07    01/04/91     P    400    128     80    66     N
07    03/03/91     P    401    132     70    80     N
;




*Program to create the SAS data set MEDICAL;
LIBNAME C 'C:\SASDATA'; ***Substitute the subdirectory and/or drive of
                          your choice here;
DATA C.MEDICAL;
INFORMAT ADMIT_DT DISCH_DT MMDDYY8. COST COMMA8.2;
INPUT SUB_ID DIAGCODE ADMIT_DT DISCH_DT HOSPCODE LOS COST;
FORMAT ADMIT_DT DISCH_DT MMDDYY8.;
DATALINES;
03916  291  04/13/92  04/14/92  19    1   325.00
09243  291  01/21/92  02/15/92  14   25  6000.00
71543  480  03/06/92  03/07/92  18    1   621.00
96298  480  01/06/92  01/18/92  17   12  7050.99
75986  493  01/13/92  01/27/92  18   14  5521.85
96913  493  03/02/92  03/02/92  15    0   200.00
;




*Program to create the SAS data set SALES;
LIBNAME C 'C:\SASDATA'; ***Substitute the subdirectory and/or drive of
                          your choice here;
DATA C.SALES;
INPUT PO_NUM ITEM $ REGION $ PRICE QUANTITY;
DATALINES;
1456   Hammer   NORTH  10    5
1458   Saw      NORTH  15    4
1511   Pliers   NORTH   8   35
1600   Hammer   SOUTH  10   15
1711   Hammer   EAST   10   12
1712   Hammer   EAST   10    2
1713   Saw      EAST   15   25
1715   Saw      EAST   15   24
1800   Pliers   EAST    8    7
1900   Saw      WEST   15    9
1901   Saw      WEST   15    5
;

