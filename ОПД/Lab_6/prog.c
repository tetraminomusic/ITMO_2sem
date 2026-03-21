#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>

int get_random(int max) {

	int fd;
	unsigned int random_number;
	ssize_t bytes_read;

	fd = open("/dev/random", O_RDONLY);

	if (fd == -1) {
		printf("Ошибка: файл рандома нет немного\n");
		return -1;
	}

	bytes_read = read(fd, &random_number, sizeof(int));

	if (bytes_read != sizeof(int)) {
		printf("Ошибка: не то количествой байтов, братанчик\n");
		close(fd);
		return -1;
	}

	close(fd);

	return (random_number % max) + 1;
}

int main() {
	printf("Бро, я жёстко загадал число от 1 до 100\n");

	int attempts = 0;
	int max_attempts = 7;
	int secret_num;
	int player_num;

	secret_num = get_random(100);

	if (secret_num == -1) {
		printf("Ошибка генерации рандомного числа\n");
		return 1;
	}

	while (attempts < max_attempts) {
		printf("Введи число:\n");

		if (scanf("%d", &player_num) != 1) {
            printf("Ошибка: введи целое число\n");
            while (getchar() != '\n');
            continue;
        }

		if (player_num < 1 || player_num > 100) {
			printf("Ты глупый или что-то?\n");
			continue;
		}

		attempts++;

		if (player_num == secret_num) {
			printf("Чел, харош, отгадал\n");
			return 0;
		}
		else if (player_num < secret_num) {
			printf("Больше, братанчик\n");
		}
		else {
			printf("Меньше, чувак\n");
		}
	}

	printf("Скилл иссю, иди бота\n");
	return 0;
}