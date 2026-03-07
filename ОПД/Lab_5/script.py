import re

def convert_to_bevm_rle(input_text):
    result = []
    
    lines = input_text.splitlines()
    
    for line in lines:
        if not line: 
            result.append("WORD 0x010A")
            continue
            
        chunks = [m.group(0) for m in re.finditer(r'(.)\1*', line)]
        
        line_words = []
        for chunk in chunks:
            char = chunk[0]
            count = len(chunk)
            char_code = ord(char)

            while count > 0:
                current_count = min(count, 255)
                hex_val = f"0x{current_count:02X}{char_code:02X}"
                line_words.append(hex_val)
                count -= current_count

        line_words.append("0x010A")
        
        result.append("WORD " + ", ".join(line_words))

    result.append("WORD 0x0000")
    return "\n".join(result)

with open("C:\\Users\\user\\Desktop\\ITMO_2sem\\ОПД\\Lab_5\\polyakov.txt", 'r', encoding='utf-8') as f:
    ascii_art = f.read()

output_asm = convert_to_bevm_rle(ascii_art)
print(output_asm)

# Сохранить в файл для вставки в ассемблер
with open("C:\\Users\\user\\Desktop\\ITMO_2sem\\ОПД\\Lab_5\\output.txt", 'w') as f:
    f.write(output_asm)