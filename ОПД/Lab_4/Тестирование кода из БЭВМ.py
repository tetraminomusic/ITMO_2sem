def f(x):
    if (0 < x <= 4087):
        return 4087
    return 4*x - 116

z = int("1388", 16)
y = int("0", 16)
x = int("1388", 16)

print(hex(5000))
print(hex(-f(z) + 1 + f(x+1) + 1 + f(y+1))[2:])