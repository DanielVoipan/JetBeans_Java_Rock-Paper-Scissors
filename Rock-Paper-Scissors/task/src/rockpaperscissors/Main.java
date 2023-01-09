package rockpaperscissors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    private static String name = null;
    private static String ratingFILE = "rating.txt";

    private enum standardOptions {
        PAPER, SCISSORS, ROCK;
        public boolean equalsIgnoreCase(String chose) {
            if (this.toString().equalsIgnoreCase(chose)) {
                return true;
            }
            return false;
        }
    }
    // points based of end game
    private enum points {
        draw(50), win(100);
        private int points = 0;

        points(int points) {
            this.points = points;
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name:");
        name = scanner.nextLine();
        System.out.printf("Hello, %s\n", name);
        String readOptions = scanner.nextLine();
        String[] splitOptions = readOptions.split(",");
        String[] optionsSpecified = new String[splitOptions.length];
        String[] temp = new String[optionsSpecified.length - 1];
        int counter = 0;
            for (String op : splitOptions) {
                optionsSpecified[counter] = op;
                counter++;
        }
        boolean stop = false;
        System.out.println("Okay, let's start");
        while (!stop) {
            boolean isValid = false;
            String chose = scanner.next();
            if ("!exit".equalsIgnoreCase(chose)) {
                System.out.println("Bye!");
                break;
            } if ("!rating".equalsIgnoreCase(chose)) {
                String rating = ratingReturn(name, 0);
                if (Objects.equals(rating, null)) {
                    rating = "0";
                }
                System.out.printf("Your rating: %s\n", rating);
                continue;
            }
            if (readOptions.isEmpty()) {
                for (standardOptions o : standardOptions.values()) {
                    if (o.equalsIgnoreCase(chose)) {
                        isValid = true;
                    }
                }
            } else {
                for (String o : optionsSpecified) {
                    if (o.equalsIgnoreCase(chose)) {
                        isValid = true;
                    }
                }
            }
            if (!isValid) {
                System.out.println("Invalid input");
                continue;
            }
            if (readOptions.isEmpty()) {
                whoWins(chose);
            } else {
                scanOptions(chose, temp, optionsSpecified);
                whoWinsComplex(chose, temp);
            }
        }
    }

    // scan options to see who beats who
    static void whoWinsComplex(String chose, String[] op) {
        int div = op.length / 2;
        int remained = op.length % 2;
        String[] optionsWhoBeat = new String[div];
        String[] getOptionsWhoLose = new String[div + remained];
        int lose = div + div + remained;
        int counterWB = 0;
        int counterWL = 0;
        for (int i = 0; i < op.length; i++) {
            if (i < div) {
                optionsWhoBeat[counterWB] = op[i];
                counterWB++;
            } else {
                getOptionsWhoLose[counterWL] = op[i];
                counterWL++;
            }
        }
        String computerChose = computerChose(1, op);
        int win = 0;
        if (chose.equalsIgnoreCase(computerChose)) {
            win = 1;
            addPoints(name, 0);
        } else if (Arrays.toString(optionsWhoBeat).contains(computerChose)) {
            win = 2;
        } else if (Arrays.toString(getOptionsWhoLose).contains(computerChose)) {
            win = 3;
            addPoints(name, 1);
        }
        outputWins(win, computerChose);
    }
    static void scanOptions(String option, String[] temp, String[] optionsSpecified) {
        int lastI = 0;
        boolean found = false;
        int counter = 0;
        for (int i = 0; i < optionsSpecified.length; i++) {
            if (optionsSpecified[i].equalsIgnoreCase(option)) {
                lastI = i;
                found = true;
                continue;
            }
            if (found) {
                temp[counter] = optionsSpecified[i];
                counter++;
            }
        }
        for (int i = 0; i < lastI; i++) {
            temp[counter] = optionsSpecified[i];
            counter++;
        }

    }

    // create file if not exists
    static void createFile(File ratingFile) {
        if (!ratingFile.isFile()) {
            try (FileWriter writer = new FileWriter(ratingFile)) {
                writer.write("");
            } catch (IOException e) {
                System.out.printf("An exception occured %s", e.getMessage());
            }
        }
    }

    static File ratingDelete(File tempFile, String line) {
        try (FileWriter writeTemp = new FileWriter(tempFile, true)) {
            writeTemp.write(line + "\n");
        } catch (IOException e) {
        System.out.println("Exception occured : "+ e.getMessage());
        }
        return tempFile;
    }

    // return rating of the user, if type == 1, delete the score
    // so it will be updated with the new one.
    static String ratingReturn(String name, int type) {
        File ratingFile = new File(ratingFILE);
        String time = String.valueOf(System.currentTimeMillis());
        File tempFile = new File(ratingFILE + "_" + time);
        createFile(ratingFile);
        String output = null;
        try (Scanner fileParsing = new Scanner(ratingFile)) {
            while (fileParsing.hasNext()) {
                String line = fileParsing.nextLine();
                String[] splitLine = line.split(" ");
                String readName = splitLine[0];
                String readRating = splitLine[1];
                if (readName.equalsIgnoreCase(name)) {
                    output = readRating;
                    continue;
                }
                if (type == 1) {
                    tempFile = ratingDelete(tempFile, line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.printf("File not found : %s", ratingFile.getName());
        }
        if (type == 1) {
            ratingFile.delete();
            tempFile.renameTo(ratingFile);
        }
        return output;
    }

    // add points to name
    static void addPoints(String name, int type) {
        File ratingFile = new File(ratingFILE);
        int lastRating = 0;
        String currentRating = ratingReturn(name, 1);
        if (!Objects.equals(currentRating, null)) {
            lastRating = Integer.parseInt(currentRating);
        } else {
            lastRating = 0;
        }
        if (type == 0) {
            lastRating += points.draw.points;
        } else {
            lastRating += points.win.points;
        }
        try (FileWriter write = new FileWriter(ratingFile, true)) {
            write.write(name + " " + lastRating);
        } catch (IOException e) {
            System.out.println("Exception occured : "+ e.getMessage());
        }
    }

    static String computerChose(int type, String[] op) {
        Random r = new Random();
        String computerChose = null;
        if (type == 0) {
            int randInt = r.nextInt(standardOptions.values().length);
            for (standardOptions o : standardOptions.values()) {
                if (o.ordinal() == randInt) {
                    computerChose = o.name();
                }
            }
        } else {
            int randInt = r.nextInt(op.length);
            for (int i = 0; i < op.length; i++) {
                if (i == randInt) {
                    computerChose = op[i];
                }
            }
        }
        return computerChose;
    }

    static void whoWins(String chose) {
        int win = 0;
        String computerChose = computerChose(0,null);
        if (chose.equalsIgnoreCase(computerChose)) {
            win = 1;
            addPoints(name, 0);
        } else if (standardOptions.PAPER.equalsIgnoreCase(chose) && standardOptions.SCISSORS.equalsIgnoreCase(computerChose)) {
            win = 2;
        } else if (standardOptions.SCISSORS.equalsIgnoreCase(chose) && standardOptions.ROCK.equalsIgnoreCase(computerChose)) {
            win = 2;
        } else if (standardOptions.ROCK.equalsIgnoreCase(chose) && standardOptions.PAPER.equalsIgnoreCase(computerChose)) {
            win = 2;
        } else if (standardOptions.SCISSORS.equalsIgnoreCase(chose) && standardOptions.PAPER.equalsIgnoreCase(computerChose)) {
            win = 3;
            addPoints(name, 1);
        } else if (standardOptions.ROCK.equalsIgnoreCase(chose) && standardOptions.SCISSORS.equalsIgnoreCase(computerChose)) {
            win = 3;
            addPoints(name, 1);
        } else if (standardOptions.PAPER.equalsIgnoreCase(chose) && standardOptions.ROCK.equalsIgnoreCase(computerChose)) {
            win = 3;
            addPoints(name, 1);
        }
        outputWins(win, computerChose);
    }

    static void outputWins(int win, String computerChose) {
        switch (win) {
            case 1:
                System.out.printf("There is a draw (%s)\n", computerChose);
                break;

            case 2:
                System.out.printf("Sorry, but the computer chose %s\n", computerChose);
                break;

            case 3:
                System.out.printf("Well done. The computer chose %s and failed\n", computerChose);
                break;
        }
    }
}
