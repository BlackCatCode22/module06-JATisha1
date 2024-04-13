import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

class Animal {
    String species;
    String sex;
    String color;
    int weight;
    String origin;
    String uniqueID;
    LocalDate birthday;
    String name;

    public Animal(String species, String sex, String color, int weight, String origin, int age, String birthSeason) {
        this.species = species;
        this.sex = sex;
        this.color = color;
        this.weight = weight;
        this.origin = origin;
        this.birthday = genBirthDay(birthSeason, age);
    }

    private LocalDate genBirthDay(String birthSeason, int age) {
        int year = LocalDate.now().getYear() - age;
        Month month;
        switch (birthSeason.toLowerCase()) {
            case "spring":
                month = Month.MARCH;
                break;
            case "summer":
                month = Month.JUNE;
                break;
            case "autumn":
                month = Month.SEPTEMBER;
                break;
            case "winter":
                month = Month.DECEMBER;
                break;
            default:
                month = Month.JANUARY; // Default to January if no season is provided
                break;
        }
        return LocalDate.of(year, month, 1);
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format("%s; %s; birth date: %s; %s color; %s; %d pounds; from %s; arrived %s",
                uniqueID, name, birthday.format(formatter), color, sex, weight, origin, LocalDate.now().format(formatter));
    }
}

class Zoo {
    private List<Animal> animals = new ArrayList<>();
    private Map<String, Integer> speciesCount = new HashMap<>();
    private Map<String, String> animalNames = new HashMap<>();

    public void readAnimalsFromFile(String filename) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                String[] ageSex = parts[0].split(" ");
                for(int i=0;i<ageSex.length;i++){
                    System.out.println(ageSex[i]);
                }
                int age = Integer.parseInt(ageSex[0]);
                String sex = parts[1];
                String species = parts[2];
                String color = parts[3];
                int weight = Integer.parseInt(parts[4].split(" ")[0]);
                String origin = parts[5];
                String birthSeason = parts[7];

                Animal animal = new Animal(species, sex, color, weight, origin, age, birthSeason);
                animals.add(animal);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readNamesFromFile(String filename) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                animalNames.put(parts[0].trim(), parts[1].trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void assignNamesAndIDs() {
        for (Animal animal : animals) {
            speciesCount.put(animal.species, speciesCount.getOrDefault(animal.species, 0) + 1);
            String uniqueID = genUniqueID(animal.species, speciesCount.get(animal.species));
            animal.setUniqueID(uniqueID);

            String name = animalNames.getOrDefault(animal.species, "Unknown") + speciesCount.get(animal.species);
            animal.setName(name);
        }
    }

    private String genUniqueID(String species, int count) {
        return species.substring(0, 2).toUpperCase() + String.format("%02d", count);
    }

    public void organizeAndWriteToFile(String filename) {
        Map<String, List<Animal>> habitats = animals.stream()
                .collect(Collectors.groupingBy(animal -> capitalize(animal.species) + " Habitat"));

        try (PrintWriter writer = new PrintWriter(filename)) {
            for (Map.Entry<String, List<Animal>> entry : habitats.entrySet()) {
                writer.println(entry.getKey() + ":");
                for (Animal animal : entry.getValue()) {
                    writer.println("    • " + animal);
                }
                writer.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}

public class MyProg {
    public static void main(String[] args) {
        Zoo zoo = new Zoo();
        zoo.readAnimalsFromFile("arrivingAnimals.txt");
        zoo.readNamesFromFile("animalNames.txt");
        zoo.assignNamesAndIDs();
        zoo.organizeAndWriteToFile("zooPopulation.txt");
    }
}
