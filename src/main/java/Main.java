import java.util.*;

public class Main {
    public static void main(String[] args) {
        int n = 20; // Количество людей
        int m = 4; // Количество дней
        int k = 3; // Количество столов
        var tableSizes = List.of(4, 6, 10); // Размеры столов
        int sum = 0;
        for (int size : tableSizes) {
            sum += size;
        }
        if (sum != n) {
            throw new RuntimeException("Количество посадочных мест не равно количеству людей");
        }

        // РЕШЕНИЕ

        // Создаем людей
        List<Person> people = new ArrayList<>();
        for (int id = 0; id < n; id++) {
            people.add(new Person(id));
        }
        for (Person person : people) {
            person.setUnfamiliar(people);
        }

        // Создаем пустую рассадку на каждый день
        List<List<Table>> sittingRulesByDay = new ArrayList<>();
        for (int day = 0; day < m; day++) {
            List<Table> tablesForDay = new ArrayList<>();
            for (int tableSize : tableSizes) {
                tablesForDay.add(new Table(tableSize));
            }
            sittingRulesByDay.add(tablesForDay);
        }

        // 1-й день: можно посадить как угодно
        // Сажаем в порядке возрастания порядкового номера, пока за столом есть место
        int curDay = 0;
        int curTable = 0;
        for (Person person : people) {
            if (sittingRulesByDay.get(curDay).get(curTable).remainingSize == 0) {
                curTable++;
            }
            sittingRulesByDay.get(curDay).get(curTable).sitPerson(person);
        }
        // Логируем знакомства для каждого стола
        for (Table table : sittingRulesByDay.get(curDay)) {
            var satPeople = table.getPeople();
            for (Person person : satPeople) {
                person.makeFamiliar(satPeople);
            }
        }

        // 2-й и следующие дни
        /*
            Сортируем столы по убыванию - от самого большого до маленького.
            Берем по очереди столы, например размер текущего стола S:
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
         */
        curDay++;
        while (curDay < m) {
            List<Person> sortedPeople = people.stream().sorted().toList();
            Set<Person> remaining = new HashSet<>(people);
            List<Table> sortedTables = sittingRulesByDay.get(curDay).stream().sorted(Comparator.reverseOrder()).toList();
            curTable = 0;
            while (!remaining.isEmpty()) {
                int curTableSize = sortedTables.get(curTable).getSize();
                Map<Person, Integer> counter = new HashMap<>();
                people.forEach(p -> counter.put(p, 0));
                Set<Person> peopleForCurTable = new HashSet<>();

                Person firstForCurTable = sortedPeople.stream()
                    .filter(remaining::contains)
                    .findFirst()
                    .get();

                peopleForCurTable.add(firstForCurTable);
                remaining.remove(firstForCurTable);
                firstForCurTable.unfamiliarWith.forEach(
                    person -> {
                        counter.put(person, counter.get(person) + 1);
                    }
                );

                while (peopleForCurTable.size() < curTableSize) {
                    var bestCandidate = getBestCandidate(remaining, counter);
                    peopleForCurTable.add(bestCandidate);
                    remaining.remove(bestCandidate);
                    bestCandidate.unfamiliarWith.forEach(
                        person -> {
                            counter.put(person, counter.get(person) + 1);
                        }
                    );
                    for (Person person : peopleForCurTable) {
                        person.makeFamiliar(peopleForCurTable);
                    }
                }
                for (Person p : peopleForCurTable) {
                    sortedTables.get(curTable).sitPerson(p);
                }

                curTable++;
            }
            curDay++;
        }

        System.out.println("Полученная рассадка");
        for (int day = 0; day < m; day++) {
            System.out.println("День " + (day + 1));
            for (int i = 0; i < sittingRulesByDay.get(day).size(); i++) {
                System.out.print("\tСтол " + (i + 1) + ": ");
                for (var person : sittingRulesByDay.get(day).get(i).getPeople()) {
                    System.out.print(person.id + 1);
                    System.out.print(" ");
                }
                System.out.println();
            }
        }
        System.out.println();
        System.out.println("Разнообразие:");
        for (var person : people) {
            System.out.println(
                "Человек " + (person.id + 1) + " познакомился с " + (person.familiarWith.size()) + " людьми"
            );
        }
    }

    private static Person getBestCandidate(Set<Person> remaining, Map<Person, Integer> counter) {
        int maxCnt = Integer.MIN_VALUE;
        Person bestCandidate = null;
        for (var entry : counter.entrySet()) {
            Person p = entry.getKey();
            int cnt = entry.getValue();
            if (!remaining.contains(p)) {
                continue;
            }

            if (cnt >= maxCnt) {
                if (bestCandidate == null || bestCandidate.unfamiliarWith.size() < p.unfamiliarWith.size()) {
                    maxCnt = cnt;
                    bestCandidate = p;
                }
            }
        }

        if (bestCandidate != null) {
            return bestCandidate;
        }

        return remaining.stream().sorted().findFirst().orElse(null);
    }

    public static class Person implements Comparable<Person> {
        private final int id;
        private final Set<Person> familiarWith;
        private final Set<Person> unfamiliarWith;

        public Person(int id) {
            this.id = id;
            this.unfamiliarWith = new HashSet<>();
            this.familiarWith = new HashSet<>();
        }

        public void setUnfamiliar(Collection<Person> allPeople) {
            this.unfamiliarWith.addAll(allPeople);
            this.unfamiliarWith.remove(this);
        }

        public void makeFamiliar(Collection<Person> people) {
            for (Person person : people) {
                if (person.equals(this)) {
                    continue;
                }
                unfamiliarWith.remove(person);
                familiarWith.add(person);
            }
        }

        @Override
        public int compareTo(Person o) {
            if (this.familiarWith.size() - o.familiarWith.size() == 0) {
                // Без рандома в "симметричной" ситуации (например все столы одного размера)
                // будет неравноправие, так как сортировка будет опираться на id человека.
                // Добавляем рандом для борьбы с этим.
                return (int)(Math.random() * 10.0) - 5;
            }

            return this.familiarWith.size() - o.familiarWith.size();
        }
    }

    public static class Table implements Comparable<Table> {
        private final int size;

        public int getSize() {
            return size;
        }

        private int remainingSize;
        private final List<Person> people;

        public Table(int size) {
            this.size = size;
            this.remainingSize = size;
            this.people = new ArrayList<>();
        }

        public List<Person> getPeople() {
            return people;
        }

        public boolean sitPerson(Person person) {
            if (remainingSize > 0) {
                this.people.add(person);
                remainingSize--;
                return true;
            }

            return false;
        }

        @Override
        public int compareTo(Table o) {
            return this.size - o.size;
        }
    }
}