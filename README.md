# PeopleSittingChart
1-й день: можно посадить как угодно: сажаем в порядке возрастания порядкового номера, пока за столом есть место.

2-й и следующие дни:
Сортируем столы по убыванию - от самого большого до маленького.
Берем по очереди столы, например размер текущиго стола S:
выбираем S людей таким образом, чтобы появилось как можно больше новых связей.
Это можно было бы сделать полным перебором, но, например, для 100 человек и стола размера 10,
сложность нахождения всех сочетаний – 10^13.
Поэтому я предлагаю следующий алгоритм:
1) Сортируем людей по возрастанию того, сколько разных людей они знают.
2) По очереди для каждого смотрим, кого они еще не знают, берем там самого "неразнообразного",
сажаем за текущий стол.
2') А еще ведем учет, кого текущие участники стола не знают "больше всего": то есть ведем счетчики, сколько
раз тот или иной человек встречается в множествах unfamiliarWith людей, которых сажаем вместе.
При выборе следующего соседа смотрим на "лидирующего" по счетчику человека.
3) Повторить для следующего стола.

Повторить все то же самое для следующего дня.

### Пример результата
Данные:
```
int n = 20; // Количество людей
int m = 4; // Количество дней
int k = 3; // Количество столов
var tableSizes = List.of(4, 6, 10); // Размеры столов
```
Результат:
```
Полученная рассадка
День 1
	Стол 1: 1 2 3 4 
	Стол 2: 5 6 7 8 9 10 
	Стол 3: 11 12 13 14 15 16 17 18 19 20 
День 2
	Стол 1: 14 11 18 13 
	Стол 2: 12 5 8 16 20 9 
	Стол 3: 6 19 4 15 17 1 7 10 2 3 
День 3
	Стол 1: 19 15 17 20 
	Стол 2: 6 12 7 10 16 3 
	Стол 3: 4 14 5 1 8 11 18 13 2 9 
День 4
	Стол 1: 4 1 16 2 
	Стол 2: 19 15 17 12 8 18 
	Стол 3: 6 14 5 7 10 11 20 13 3 9 

Разнообразие:
Человек 1 познакомился с 17 людьми
Человек 2 познакомился с 17 людьми
Человек 3 познакомился с 17 людьми
Человек 4 познакомился с 17 людьми
Человек 5 познакомился с 16 людьми
Человек 6 познакомился с 18 людьми
Человек 7 познакомился с 18 людьми
Человек 8 познакомился с 18 людьми
Человек 9 познакомился с 16 людьми
Человек 10 познакомился с 18 людьми
Человек 11 познакомился с 19 людьми
Человек 12 познакомился с 16 людьми
Человек 13 познакомился с 19 людьми
Человек 14 познакомился с 19 людьми
Человек 15 познакомился с 17 людьми
Человек 16 познакомился с 19 людьми
Человек 17 познакомился с 17 людьми
Человек 18 познакомился с 15 людьми
Человек 19 познакомился с 17 людьми
Человек 20 познакомился с 16 людьми
```

