5       int get_random(int max) {
   0x0000000000001209 <+0>:     endbr64
   0x000000000000120d <+4>:     push   %rbp
   0x000000000000120e <+5>:     mov    %rsp,%rbp
   0x0000000000001211 <+8>:     sub    $0x30,%rsp
   0x0000000000001215 <+12>:    mov    %edi,-0x24(%rbp)
   0x0000000000001218 <+15>:    mov    %fs:0x28,%rax
   0x0000000000001221 <+24>:    mov    %rax,-0x8(%rbp)
   0x0000000000001225 <+28>:    xor    %eax,%eax

6
7               int fd;
8               unsigned int random_number;
9               ssize_t bytes_read;
10
11              fd = open("/dev/random", O_RDONLY);
   0x0000000000001227 <+30>:    mov    $0x0,%esi
   0x000000000000122c <+35>:    lea    0xdd5(%rip),%rax        # 0x2008
   0x0000000000001233 <+42>:    mov    %rax,%rdi
   0x0000000000001236 <+45>:    mov    $0x0,%eax
   0x000000000000123b <+50>:    call   0x1100 <open@plt>
   0x0000000000001240 <+55>:    mov    %eax,-0x14(%rbp)

12
13              if (fd == -1) {
   0x0000000000001243 <+58>:    cmpl   $0xffffffff,-0x14(%rbp)
   0x0000000000001247 <+62>:    jne    0x125f <get_random+86>

14                      printf("Ошибка: файл рандома нет немного\n");
   0x0000000000001249 <+64>:    lea    0xdc8(%rip),%rax        # 0x2018
   0x0000000000001250 <+71>:    mov    %rax,%rdi
   0x0000000000001253 <+74>:    call   0x10b0 <puts@plt>

15                      return -1;
   0x0000000000001258 <+79>:    mov    $0xffffffff,%eax
   0x000000000000125d <+84>:    jmp    0x12be <get_random+181>

16              }
17
18              bytes_read = read(fd, &random_number, sizeof(int));
   0x000000000000125f <+86>:    lea    -0x18(%rbp),%rcx
   0x0000000000001263 <+90>:    mov    -0x14(%rbp),%eax
   0x0000000000001266 <+93>:    mov    $0x4,%edx
   0x000000000000126b <+98>:    mov    %rcx,%rsi
   0x000000000000126e <+101>:   mov    %eax,%edi
   0x0000000000001270 <+103>:   call   0x10e0 <read@plt>
   0x0000000000001275 <+108>:   mov    %rax,-0x10(%rbp)

19
--Type <RET> for more, q to quit, c to continue without paging--
20              if (bytes_read != sizeof(int)) {
   0x0000000000001279 <+112>:   cmpq   $0x4,-0x10(%rbp)
   0x000000000000127e <+117>:   je     0x12a0 <get_random+151>

21                      printf("Ошибка: не то количествой байтов, братанчик\n");
   0x0000000000001280 <+119>:   lea    0xdd1(%rip),%rax        # 0x2058
   0x0000000000001287 <+126>:   mov    %rax,%rdi
   0x000000000000128a <+129>:   call   0x10b0 <puts@plt>

22                      close(fd);
   0x000000000000128f <+134>:   mov    -0x14(%rbp),%eax
   0x0000000000001292 <+137>:   mov    %eax,%edi
   0x0000000000001294 <+139>:   call   0x10d0 <close@plt>

23                      return -1;
   0x0000000000001299 <+144>:   mov    $0xffffffff,%eax
   0x000000000000129e <+149>:   jmp    0x12be <get_random+181>

24              }
25
26              close(fd);
   0x00000000000012a0 <+151>:   mov    -0x14(%rbp),%eax
   0x00000000000012a3 <+154>:   mov    %eax,%edi
   0x00000000000012a5 <+156>:   call   0x10d0 <close@plt>

27
28              return (random_number % max) + 1;
   0x00000000000012aa <+161>:   mov    -0x18(%rbp),%eax
   0x00000000000012ad <+164>:   mov    -0x24(%rbp),%esi
   0x00000000000012b0 <+167>:   mov    $0x0,%edx
   0x00000000000012b5 <+172>:   div    %esi
   0x00000000000012b7 <+174>:   mov    %edx,%ecx
   0x00000000000012b9 <+176>:   mov    %ecx,%eax
   0x00000000000012bb <+178>:   add    $0x1,%eax

29      }
   0x00000000000012be <+181>:   mov    -0x8(%rbp),%rdx
   0x00000000000012c2 <+185>:   sub    %fs:0x28,%rdx
   0x00000000000012cb <+194>:   je     0x12d2 <get_random+201>
   0x00000000000012cd <+196>:   call   0x10c0 <__stack_chk_fail@plt>
   0x00000000000012d2 <+201>:   leave
   0x00000000000012d3 <+202>:   ret

31      int main() {
   0x00000000000012d4 <+0>:     endbr64
   0x00000000000012d8 <+4>:     push   %rbp
   0x00000000000012d9 <+5>:     mov    %rsp,%rbp
   0x00000000000012dc <+8>:     sub    $0x20,%rsp
   0x00000000000012e0 <+12>:    mov    %fs:0x28,%rax
   0x00000000000012e9 <+21>:    mov    %rax,-0x8(%rbp)
   0x00000000000012ed <+25>:    xor    %eax,%eax

32              printf("Бро, я жёстко загадал число от 1 до 100\n");
   0x00000000000012ef <+27>:    lea    0xdb2(%rip),%rax        # 0x20a8
   0x00000000000012f6 <+34>:    mov    %rax,%rdi
   0x00000000000012f9 <+37>:    call   0x10b0 <puts@plt>

33
34              int attempts = 0;
   0x00000000000012fe <+42>:    movl   $0x0,-0x14(%rbp)

35              int max_attempts = 7;
   0x0000000000001305 <+49>:    movl   $0x7,-0x10(%rbp)

36              int secret_num;
37              int player_num;
38
39              secret_num = get_random(100);
   0x000000000000130c <+56>:    mov    $0x64,%edi
   0x0000000000001311 <+61>:    call   0x1209 <get_random>
   0x0000000000001316 <+66>:    mov    %eax,-0xc(%rbp)

40
41              if (secret_num == -1) {
   0x0000000000001319 <+69>:    cmpl   $0xffffffff,-0xc(%rbp)
   0x000000000000131d <+73>:    jne    0x13f1 <main+285>

42                      printf("Ошибка генерации рандомного числа\n");
   0x0000000000001323 <+79>:    lea    0xdc6(%rip),%rax        # 0x20f0
   0x000000000000132a <+86>:    mov    %rax,%rdi
   0x000000000000132d <+89>:    call   0x10b0 <puts@plt>

43                      return 1;
   0x0000000000001332 <+94>:    mov    $0x1,%eax
   0x0000000000001337 <+99>:    jmp    0x1411 <main+317>

44              }
45
46              while (attempts < max_attempts) {
   0x00000000000013f1 <+285>:   mov    -0x14(%rbp),%eax
   0x00000000000013f4 <+288>:   cmp    -0x10(%rbp),%eax
   0x00000000000013f7 <+291>:   jl     0x133c <main+104>
47                      printf("Введи число:\n");
   0x000000000000133c <+104>:   lea    0xded(%rip),%rax        # 0x2130
   0x0000000000001343 <+111>:   mov    %rax,%rdi
   0x0000000000001346 <+114>:   call   0x10b0 <puts@plt>

48
49                      if (scanf("%d", &player_num) != 1) {
   0x000000000000134b <+119>:   lea    -0x18(%rbp),%rax
   0x000000000000134f <+123>:   mov    %rax,%rsi
   0x0000000000001352 <+126>:   lea    0xdee(%rip),%rax        # 0x2147
   0x0000000000001359 <+133>:   mov    %rax,%rdi
   0x000000000000135c <+136>:   mov    $0x0,%eax
   0x0000000000001361 <+141>:   call   0x1110 <__isoc99_scanf@plt>
   0x0000000000001366 <+146>:   cmp    $0x1,%eax
   0x0000000000001369 <+149>:   je     0x1387 <main+179>

50                  printf("Ошибка: введи целое число\n");
   0x000000000000136b <+151>:   lea    0xdde(%rip),%rax        # 0x2150
   0x0000000000001372 <+158>:   mov    %rax,%rdi
   0x0000000000001375 <+161>:   call   0x10b0 <puts@plt>

51                  while (getchar() != '\n');
   0x000000000000137a <+166>:   nop
   0x000000000000137b <+167>:   call   0x10f0 <getchar@plt>
   0x0000000000001380 <+172>:   cmp    $0xa,%eax
   0x0000000000001383 <+175>:   jne    0x137b <main+167>

52                  continue;
   0x0000000000001385 <+177>:   jmp    0x13f1 <main+285>

53              }
54
55                      if (player_num < 1 || player_num > 100) {
   0x0000000000001387 <+179>:   mov    -0x18(%rbp),%eax
   0x000000000000138a <+182>:   test   %eax,%eax
   0x000000000000138c <+184>:   jle    0x1396 <main+194>
   0x000000000000138e <+186>:   mov    -0x18(%rbp),%eax
   0x0000000000001391 <+189>:   cmp    $0x64,%eax
   0x0000000000001394 <+192>:   jle    0x13a7 <main+211>

56                              printf("Ты глупый или что-то?\n");
   0x0000000000001396 <+194>:   lea    0xde3(%rip),%rax        # 0x2180
   0x000000000000139d <+201>:   mov    %rax,%rdi
   0x00000000000013a0 <+204>:   call   0x10b0 <puts@plt>

57                              continue;
   0x00000000000013a5 <+209>:   jmp    0x13f1 <main+285>

58                      }
--Type <RET> for more, q to quit, c to continue without paging--
59
60                      attempts++;
   0x00000000000013a7 <+211>:   addl   $0x1,-0x14(%rbp)

61
62                      if (player_num == secret_num) {
   0x00000000000013ab <+215>:   mov    -0x18(%rbp),%eax
   0x00000000000013ae <+218>:   cmp    %eax,-0xc(%rbp)
   0x00000000000013b1 <+221>:   jne    0x13c9 <main+245>

63                              printf("Чел, харош, отгадал\n");
   0x00000000000013b3 <+223>:   lea    0xdee(%rip),%rax        # 0x21a8
   0x00000000000013ba <+230>:   mov    %rax,%rdi
   0x00000000000013bd <+233>:   call   0x10b0 <puts@plt>

64                              return 0;
   0x00000000000013c2 <+238>:   mov    $0x0,%eax
   0x00000000000013c7 <+243>:   jmp    0x1411 <main+317>

65                      }
66                      else if (player_num < secret_num) {
   0x00000000000013c9 <+245>:   mov    -0x18(%rbp),%eax
   0x00000000000013cc <+248>:   cmp    %eax,-0xc(%rbp)
   0x00000000000013cf <+251>:   jle    0x13e2 <main+270>

67                              printf("Больше, братанчик\n");
   0x00000000000013d1 <+253>:   lea    0xdf8(%rip),%rax        # 0x21d0
   0x00000000000013d8 <+260>:   mov    %rax,%rdi
   0x00000000000013db <+263>:   call   0x10b0 <puts@plt>
   0x00000000000013e0 <+268>:   jmp    0x13f1 <main+285>

68                      }
69                      else {
70                              printf("Меньше, чувак\n");
   0x00000000000013e2 <+270>:   lea    0xe08(%rip),%rax        # 0x21f1
   0x00000000000013e9 <+277>:   mov    %rax,%rdi
   0x00000000000013ec <+280>:   call   0x10b0 <puts@plt>

71                      }
72              }
73
74              printf("Скилл иссю, иди бота\n");
   0x00000000000013fd <+297>:   lea    0xe0c(%rip),%rax        # 0x2210
   0x0000000000001404 <+304>:   mov    %rax,%rdi
   0x0000000000001407 <+307>:   call   0x10b0 <puts@plt>

75              return 0;
   0x000000000000140c <+312>:   mov    $0x0,%eax

76      }
--Type <RET> for more, q to quit, c to continue without paging--
   0x0000000000001411 <+317>:   mov    -0x8(%rbp),%rdx
   0x0000000000001415 <+321>:   sub    %fs:0x28,%rdx
   0x000000000000141e <+330>:   je     0x1425 <main+337>
   0x0000000000001420 <+332>:   call   0x10c0 <__stack_chk_fail@plt>
   0x0000000000001425 <+337>:   leave
   0x0000000000001426 <+338>:   ret