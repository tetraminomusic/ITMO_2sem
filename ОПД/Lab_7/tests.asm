ORG 0x0290
; Общее выполнение всех тестов

Overall: WORD ?

; Результаты выполнения тестов поотдельности

Result1: WORD ?
Result2: WORD ?
Result3: WORD ?

; Предполагаемые результаты

Expectation1: WORD 0
Expectation2: WORD 0x7EAB
Expectation3: WORD 0x0F6D

; Аргументы тестов

ARG1_1: WORD 0
ARG1_2: WORD 0

ARG2_1: WORD 0xD01B
ARG2_2: WORD 0xAEB

ARG3_1: WORD 0xD1C0
ARG3_2: WORD 0xDEAD

ORG 0x0300

START:
    CALL $Test1
    CALL $Test2
    CALL $Test3

    LD #0x1
    AND $Result1
    AND $Result2
    AND $Result3
    ST $Overall

STOP:
    HLT


Error:
    POP
    POP
    CLA
    RET

; Первый тест

Test1:
    LD $ARG1_1
    PUSH
    LD $ARG1_2
    PUSH
    LD #0x67
    WORD 0x0F30 ;XORSP
    CMP #0x67
    BNE Error
    POP
    ST $Result1
    CMP $Expectation1
    BEQ Done1

Done1:
    POP
    POP
    LD #0x1
    ST $Result1
    CLA
    RET

; Второй тест

Test2:
    LD $ARG2_1
    PUSH
    LD $ARG2_2
    PUSH
    LD #0x78
    WORD 0x0F30 ;XORSP
    CMP #0x78
    BNE Error
    POP
    ST $Result2
    CMP $Expectation2
    BEQ Done2

Done2:
    POP
    POP
    LD #0x1
    ST $Result2
    CLA
    RET

; Третий тест

Test3:
    LD $ARG3_1
    PUSH
    LD $ARG3_2
    PUSH
    LD #0x56
    WORD 0x0F30 ;XORSP
    CMP #0x56
    BNE Error
    POP
    ST $Result3
    CMP $Expectation3
    BEQ Done3

Done3:
    POP
    POP
    LD #0x1
    ST $Result3
    CLA
    RET