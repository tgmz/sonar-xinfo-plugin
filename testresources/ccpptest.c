#pragma linkage(IRRSDL00 ,OS)
#line 26
#pragma runopts(POSIX(ON))
/*Include standard libraries */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>

int main( int argc, char *argv??(??))
{
printf("I'm here program %s. ",argv[0]);
for (int i = 1;i < argc; i++)
printf("Arg %d %s",i,argv[i]);
printf("\n");
}
