ORG 0x0
V0: WORD $default, 0x180 
V1: WORD $default, 0x180
V2: WORD $int2, 0x180
V3: WORD $int3, 0x180
V4: WORD $default, 0x180
V5: WORD $default, 0x180
V6: WORD $default, 0x180
V7: WORD $default, 0x180

ORG 0x042
X: WORD ?

min: WORD 0xFFD7
max: WORD 0x002B

temp_int2: WORD ?

default: IRET

START:
	DI
	CLA

	; Запрещаем возможность прерывания с неиспользуемых ВУ
	
	OUT 0x1
	OUT 0x3
	OUT 0xB
	OUT 0xE
	OUT 0x12
	OUT 0x16
	OUT 0x1A
	OUT 0x1E

	;Разрешаем возможность прерывания с ВУ, назначая на соответствующие векторы в начале памяти
	
	LD #0xA ; (1000|0010 = 1010)
	OUT 5
	LD #0xB ; (1000 | 0011 = 1011)
	OUT 7
	EI


; Бесконечный цикл с уменьшением X на 3

body:
	DI
	LD X
	DEC
	DEC
	DEC
	CALL check
	ST X
	NOP
	EI
	JUMP body
	
; Подпрограмма прерывания для ВУ-2

int2:
	ST temp_int2
	IN 4
	OR temp_int2
	ST X
	NOP
	IRET

; Подпрограмма прерывания для ВУ-3
	
int3:
	LD X
	ASL
	ADD X
	SUB #0x3
	OUT 6
	NOP
	IRET

; Подпрограмма для проверки X на ОДЗ

check:
check_min:
	CMP min
	BGE check_max 
check_max:
	CMP max
	BLT return
	JUMP load_max
load_max: LD max
return: RET